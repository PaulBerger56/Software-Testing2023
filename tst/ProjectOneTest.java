import Project.DateHolder;
import Project.Flight;
import Project.Week;
import java.sql.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;

import java.security.Key;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JUnitParamsRunner.class)
public class ProjectOneTest {

    private static WebDriver driver;
    private static DateHolder startingDateHolder;
    private static final String EXPEDIA = "https://www.expedia.com/?afmcid=nav.exp.mis.expedia";
    private static ArrayList<Flight> flightList;
    private static DateHolder flightsWithErrors;
    private static Connection connection;

    @BeforeClass
    public static void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // This user data option causes issues on other systems
//        options.addArguments("user-data-dir=C:/Users/paulb/AppData/Local/Google/Chrome/User Data");
        options.addArguments("--disable-blink-features=AutomationControlled");
//        options.addArguments("--headless");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        flightList = new ArrayList<>();
        startingDateHolder = new DateHolder();
        startingDateHolder.fillTravelWeeks();
        flightsWithErrors = new DateHolder();

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:flights.db");
            initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // THIS IS THE MAIN TEST!!!!!!!!!
    @Test
    @Parameters({"Cancun", "Las Vegas", "Denver", "Rome", "Milan", "Paris", "Madrid", "Amsterdam", "Singapore"})
    public void mainProjectTest(String city) throws InterruptedException {
        mainPageActions(city);

        for (Flight f : flightList) {
            System.out.println(f.toString());
        }

        while(!flightsWithErrors.getTRAVEL_WEEKS().isEmpty()) {
            runDatesThatHadErrors(city);
        }

        addFlightListToDB();
    }

    public void mainPageActions(String city) {
        ArrayList<Week> dates = startingDateHolder.getTRAVEL_WEEKS();

        for(int i = 0; i < dates.size(); i++) {
            System.out.println("Current number of flights on the flightlist: " + flightList.size());
            System.out.println("Current number of flights on the error list: " + flightsWithErrors.getTRAVEL_WEEKS().size());
            System.out.println("Scanning for flights from Atlanta to " + city + " from " + dates.get(i).getFIRST_MONTH() + " " + dates.get(i).getFIRST_DAY() + " to " +
                    dates.get(i).getSECOND_MONTH() + " " + dates.get(i).getSECOND_DAY());
            System.out.println();
            try {
                driver.get(EXPEDIA);
                // maximize the window because the page changes it's layout, divs, and names when it gets resized
                driver.manage().window().maximize();
                WebElement flightButton = driver.findElement(By.cssSelector("#multi-product-search-form-1 > div > div > div.uitk-tabs-container > ul > li:nth-child(2) > a"));
                flightButton.click();

                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

                WebElement leavingFromButton = driver.findElement(By.className("uitk-fake-input"));
                leavingFromButton.click();

                WebElement leavingFromField = driver.findElement(By.id("origin_select"));
                leavingFromField.sendKeys("Atlanta", Keys.ENTER);

                WebElement goingToButton = driver.findElement(By.xpath("//*[@id=\"FlightSearchForm_ROUND_TRIP\"]/div/div[1]/div/div[2]/div/div/div[2]/div[1]/button"));
                goingToButton.click();

                WebElement goingToField = driver.findElement(By.xpath("//*[@id=\"destination_select\"]"));
                goingToField.sendKeys(city, Keys.ENTER);

                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
                Week currentWeek = dates.get(i);
                selectDate(currentWeek.getFIRST_MONTH(), currentWeek.getSECOND_MONTH(), currentWeek.getFIRST_DAY(), currentWeek.getSECOND_DAY());

                WebElement searchButton = driver.findElement(By.id("search_button"));
                searchButton.click();

                // This is needed to allow the flights to load on the page
                Thread.sleep(6000);
                grabFlights(currentWeek.getFIRST_MONTH(), currentWeek.getSECOND_MONTH(), currentWeek.getFIRST_DAY(), currentWeek.getSECOND_DAY(), city);

            }catch (Exception | AssertionError e){
                Week currentWeek = dates.get(i);
                flightsWithErrors.addWeek(currentWeek.getFIRST_MONTH(), currentWeek.getSECOND_MONTH(), currentWeek.getFIRST_DAY(), currentWeek.getSECOND_DAY());
                e.printStackTrace();
            }
        }
    }

    public void runDatesThatHadErrors(String city){
     ArrayList<Week> dates = flightsWithErrors.getTRAVEL_WEEKS();
     ArrayList<Week> weeksToRemove = new ArrayList<>();

     for(Week w: dates) {
         try {
             driver.get(EXPEDIA);
             WebElement flightButton = driver.findElement(By.cssSelector("#multi-product-search-form-1 > div > div > div.uitk-tabs-container > ul > li:nth-child(2) > a"));
             flightButton.click();

             driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

             WebElement leavingFromButton = driver.findElement(By.className("uitk-fake-input"));
             leavingFromButton.click();

             WebElement leavingFromField = driver.findElement(By.id("origin_select"));
             leavingFromField.sendKeys("Atlanta", Keys.ENTER);

             WebElement goingToButton = driver.findElement(By.xpath("//*[@id=\"FlightSearchForm_ROUND_TRIP\"]/div/div[1]/div/div[2]/div/div/div[2]/div[1]/button"));
             goingToButton.click();

             WebElement goingToField = driver.findElement(By.xpath("//*[@id=\"destination_select\"]"));
             goingToField.sendKeys(city, Keys.ENTER);

             driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
             selectDate(w.getFIRST_MONTH(), w.getSECOND_MONTH(), w.getFIRST_DAY(), w.getSECOND_DAY());

             WebElement searchButton = driver.findElement(By.id("search_button"));
             searchButton.click();

             // This is needed to allow the flights to load on the page
             Thread.sleep(6000);
             grabFlights(w.getFIRST_MONTH(), w.getSECOND_MONTH(), w.getFIRST_DAY(), w.getSECOND_DAY(), city);

             // grabs the current week object and adds it to an arraylist so that we can remove succesfull dates
             // from the error list
             weeksToRemove.add(w);
         }catch (Exception | AssertionError e){
             flightsWithErrors.addWeek(w.getFIRST_MONTH(), w.getSECOND_MONTH(), w.getFIRST_DAY(), w.getSECOND_DAY());
             e.printStackTrace();
         }
     }

     // removes successful dates from the error list
     for(Week w: weeksToRemove) {
         flightsWithErrors.getTRAVEL_WEEKS().remove(w);
     }
    }

    public void selectDate(String firstMonth, String secondMonth, int firstDate, int secondDate) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement datePickerClicker = driver.findElement(By.xpath("//*[@id=\"FlightSearchForm_ROUND_TRIP\"]/div/div[2]/div/div/div/div/button"));
        wait.until(ExpectedConditions.elementToBeClickable(datePickerClicker));
        datePickerClicker.click();

        WebElement firstMonthAndYear = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[1]/div[2]/div[1]/div[5]/div[1]/div/div/div/div[2]/form/div[1]/div/div[2]/div/section/div[2]/div/div/div[3]/div/div[1]/span"));

        WebElement nextMonthButton = driver.findElement(By.xpath("//*[@id=\"FlightSearchForm_ROUND_TRIP\"]/div/div[2]/div/section/div[2]/div/div/div[2]/button"));
        wait.until(ExpectedConditions.elementToBeClickable(nextMonthButton));

        // The picker only shows two months at a time.  This is used to make sure the months
        // we are looking for are on screen
        while (!firstMonthAndYear.getText().equals(firstMonth + " 2024")) {
            nextMonthButton.click();
            firstMonthAndYear = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[1]/div[2]/div[1]/div[5]/div[1]/div/div/div/div[2]/form/div[1]/div/div[2]/div/section/div[2]/div/div/div[3]/div/div[1]/span"));
        }

        // Grabs the outer class that contains both months in the calendar and puts each them in a list
        WebElement bothVisibleMonths = driver.findElement(By.xpath("//*[@id=\"FlightSearchForm_ROUND_TRIP\"]/div/div[2]/div/section/div[2]/div/div/div[3]/div"));
        List<WebElement> months = bothVisibleMonths.findElements(By.className("uitk-month-table"));

        // Grabs the day buttons from each month and adds them to their own ArrayList
        List<List<WebElement>> dayLists = new ArrayList<>();
        for(WebElement m: months){
            List<WebElement> dayButtons = m.findElements(By.className("uitk-day-button"));
            dayLists.add(dayButtons);
        }

        List<WebElement> firstMonthList = dayLists.get(0);
        List<WebElement> secondMonthList = dayLists.get(1);


        //The button does not have day information in it, so we have to save it with the separate label div which
        // has the day info that we can read and check
        List<WebElement[]> firstMonthButtonsAndLabels = new ArrayList<>();
        for(WebElement e: firstMonthList){
            WebElement dayLabel = e.findElement(By.className("uitk-day-aria-label"));
            WebElement[] buttonAndLabel = new WebElement[2];
            buttonAndLabel[0] = e;
            buttonAndLabel[1] = dayLabel;
            firstMonthButtonsAndLabels.add(buttonAndLabel);
        }

        // Saves the contents of the second month shown in the div
        List<WebElement[]> secondMonthButtonsAndLabels = new ArrayList<>();
        for(WebElement e: secondMonthList){
            WebElement dayLabel = e.findElement(By.className("uitk-day-aria-label"));
            WebElement[] buttonAndLabel = new WebElement[2];
            buttonAndLabel[0] = e;
            buttonAndLabel[1] = dayLabel;
            secondMonthButtonsAndLabels.add(buttonAndLabel);
        }

        boolean secondMonthMatch = false;
        // Click the date buttons
        // Checks if the label of the date button matches the month/date
        for(WebElement[] e: firstMonthButtonsAndLabels){
            String[] brokenLabel = e[1].getAttribute("aria-label").split(",");
            String monthAndDay = brokenLabel[1].replace(" ", "");
            String firstDateString = firstMonth + firstDate;
            String secondDateString = secondMonth + secondDate;
            if(monthAndDay.equals(firstDateString)){
                e[0].click();
            }

            if(monthAndDay.equals(secondDateString)){
                e[0].click();
                secondMonthMatch = true;
            }
        }

        // If there was no match for the second date, we need to move to the next month's days and press the
        // corresponding date button
        if(!secondMonthMatch) {
            for(WebElement[] e: secondMonthButtonsAndLabels){
                String[] brokenLabel = e[1].getAttribute("aria-label").split(",");
                String monthAndDay = brokenLabel[1].replace(" ", "");
                String secondDateString = secondMonth + secondDate;
                if(monthAndDay.equals(secondDateString)){
                    e[0].click();
                    secondMonthMatch = true;
                }
            }
        }

        WebElement doneButton = driver.findElement(By.xpath("//*[@id=\"FlightSearchForm_ROUND_TRIP\"]/div/div[2]/div/section/footer/div/button"));
        doneButton.click();
    }

    // use this to test new webelements without having to run the whole program every time
    @Test
    public void specificThingTest() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(EXPEDIA);
        Thread.sleep(7000);

        grabFlights("May", "May", 7, 14, "Singapore");

        for(Flight f: flightList){
            System.out.println(f.toString());
        }

    }

    public void grabFlights(String firstMonth, String secondMonth, int firstDay, int secondDay, String destination) throws InterruptedException {
        boolean isNonStop = false;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"app-layer-base\"]/div[2]/div[3]/div/div/div[1]/fieldset/form/div")));
        //clicks the nonstop box if it is there and sets nonstop to true.
        WebElement filterBy = driver.findElement(By.xpath("//*[@id=\"app-layer-base\"]/div[2]/div[3]/div/div/div[1]/fieldset/form/div"));
        List<WebElement> stopTypes = filterBy.findElements(By.name("NUM_OF_STOPS"));

        for(WebElement w: stopTypes){
            String stop = w.getAttribute("id");
            if(stop.contains("NUM_OF_STOPS-0-")){
                w.click();
                isNonStop = true;
                break;
            }
        }

        // allows the flights to reload after clicking nonstop
        Thread.sleep(6000);

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("li")));
        WebElement flightContainer = driver.findElement(By.xpath("//*[@id=\"app-layer-base\"]/div[2]/div[3]/div/div/div[2]/main/ul"));

        List<WebElement> flights = flightContainer.findElements(By.tagName("li"));

        for(WebElement f: flights){
             if(isElementPresentByClassName(f, "uitk-price-a11y") && isElementPresentByClassName(f, "truncate-lines-2")){
                 WebElement priceContainer = f.findElement(By.className("uitk-price-a11y"));
                 String price = priceContainer.getText();

                WebElement airlineContainer = f.findElement(By.className("truncate-lines-2"));
                String airline = airlineContainer.getText();

                String leavingDate = firstMonth + " " + firstDay;
                String returnDate = secondMonth + " " + secondDay;
                flightList.add(new Flight(airline,"Atlanta", destination, leavingDate,  returnDate, isNonStop, price));
             }
        }
    }

    private boolean isElementPresentByClassName(WebElement element, String elementName) {
            try {
                element.findElement(By.className(elementName));
            } catch (NoSuchElementException e) {
                return false;
            }
        return true;
    }

    private boolean isElementPresentByID(WebElement element, String elementID) {
        try {
            element.findElement(By.name(elementID));
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    private static void initializeDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS flights (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "airline TEXT," +
                        "departure_city TEXT," +
                        "destination_city TEXT," +
                        "departure_date TEXT," +
                        "return_date TEXT," +
                        "price INTEGER," +
                        "nonstop TEXT," +
                        "retrieval_time TEXT)"
        );
        statement.close();
    }

    public void addFlightListToDB() {
        for(Flight f: flightList) {
            String priceString = f.getPRICE().replace("$", "").replace(",","");
            int price = Integer.parseInt(priceString);

            try(PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO flights (airline, departure_city, destination_city, departure_date," +
                            "return_date, price, nonstop, retrieval_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            )) {
                preparedStatement.setString(1, f.getAIRLINE());
                preparedStatement.setString(2, f.getDEPARTURE_CITY());
                preparedStatement.setString(3, f.getARRIVAL_CITY());
                preparedStatement.setString(4, f.getLEAVING_DATE());
                preparedStatement.setString(5, f.getRETURN_DATE());
                preparedStatement.setInt(6, price);
                preparedStatement.setString(7, String.valueOf(f.isNONSTOP()));
                preparedStatement.setString(8, getCurrentTime());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        flightList.clear();
    }

    // Gets the current date and time to add into the database
    public String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    @AfterClass
    public static void tearDown() {
        // Closes all windows
        driver.close();
        driver.quit();
        driver = null;
    }
}
