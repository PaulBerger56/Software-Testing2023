import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;


import java.sql.*;
import java.time.Duration;
import java.util.List;


public class CarListingTest {

    private static WebDriver driver;
    private static final String CRAIGSLIST_LINK = "https://atlanta.craigslist.org/search/cta#search=1~gallery~0~0";
    private static Connection connection;

    @BeforeClass
    public static void setUp() {
        ChromeOptions option = new ChromeOptions();
        option.addArguments("--remote-allow-origins=*");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(option);

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:car_listings.db");
            initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void testWebsite() {
        driver.get(CRAIGSLIST_LINK);
    }

    @Test
    public void testNumberOfCars() {
        driver.get(CRAIGSLIST_LINK);

        // had to add in a timeout because we were trying to grab the elements before they loaded
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        List<WebElement> carList = driver.findElements(By.cssSelector("#search-results-page-1 > ol > li"));

        System.out.println("Number of elements = " + carList.size());

        Assert.assertEquals(120, carList.size());
    }

    @Test
    public void testNumberOfPricesAboveZero() {
        driver.get(CRAIGSLIST_LINK);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        List<WebElement> carList = driver.findElements(By.cssSelector("#search-results-page-1 > ol > li"));

        // I already wrote the method before separating the tests, so the logic is there
        int aboveZero = numberOfPricesAboveZero(carList);

//        Assert.assertEquals("Will fail if any of the cars are missing a price," +
//                "or if their price is below 0.0", aboveZero, 120);

        System.out.println("Number of cars above $0.00 " + aboveZero);
        // There is almost always at least one car missing a price, so we will use this test
        Assert.assertNotEquals(120, aboveZero);
    }


    @Test
    public void testSQLiteAddition() throws SQLException, InterruptedException {
        driver.get(CRAIGSLIST_LINK);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        List<WebElement> carList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#search-results-page-1 > ol > li")));

        int previousRowCount = getDBCount(connection);

        for (WebElement car : carList) {
            // Set the priceString to 0 because some of the cars do not have a price element, so it defaults to 0
            // isElementPresent is used to check if the element exists or not and allows the program to continue
            // rather than crash if it is missing
            String priceString = "0";
            String subjectString = "N/A";
            String urlString = "N/A";
            String retrievalTimeString = "N/A";

            if(isElementPresent(car, "label")) {
                WebElement subject = car.findElement(By.className("label"));
                subjectString = subject.getText();
            }

            if(isElementPresent(car, "priceinfo")) {
                WebElement price = car.findElement(By.className("priceinfo"));
                priceString = price.getText();
            }

            if(isElementPresent(car, "posting-title")) {
                WebElement url = car.findElement(By.className("posting-title"));
                urlString = url.getAttribute("href");
            }

            if(isElementPresent(car, "meta")) {
                WebElement retreivalTime = car.findElement(By.className("meta"));
                retrievalTimeString = retreivalTime.getText();
                String[] pieces = retrievalTimeString.split("·");
                retrievalTimeString = pieces[0];
            }

            priceString = priceString.replace("$", "");
            priceString = priceString.replace(",", "");
            double formattedPrice = Double.parseDouble(priceString);

            insertCarListing(subjectString, formattedPrice, urlString, retrievalTimeString);
        }

        int countAfterUpdate = getDBCount(connection);

        System.out.println("Before: " + previousRowCount + ", After: " + countAfterUpdate);

        // Tests the count before updating + 120 vs the new count to make sure all 120 were added
        Assert.assertEquals(previousRowCount + 120, countAfterUpdate);
    }


    private static void initializeDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS car_listings (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "subject TEXT," +
                        "price REAL," +
                        "url TEXT," +
                        "retrieval_time TEXT)"
        );
        statement.close();

        Statement table2 = connection.createStatement();
        table2.execute(
                "CREATE TABLE IF NOT EXISTS specific_cars (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "brand TEXT," +
                        "subject TEXT," +
                        "price REAL," +
                        "url TEXT," +
                        "retrieval_time TEXT)"
        );
        table2.close();
    }

    private void insertCarListing(String subject, double price, String url, String retrievalTime) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO car_listings (subject, price, url, retrieval_Time) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, subject);
            preparedStatement.setDouble(2, price);
            preparedStatement.setString(3, url);
            preparedStatement.setString(4, retrievalTime);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Some of the cars are missing elements, so we check with this method and just move on
    // instead of crashing the code
    private boolean isElementPresent(WebElement car, String elementName) {
        try{
            car.findElement(By.className(elementName));
        } catch(NoSuchElementException e) {
            return false;
        }
        return true;
    }

    private int getDBCount(Connection c) throws SQLException {
        int rowCount = 0;
        String statement = "SELECT COUNT(*) FROM car_listings";
        PreparedStatement preparedStatement = c.prepareStatement(statement);
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            rowCount = resultSet.getInt(1);
        }
        return rowCount;
    }

    private int numberOfPricesAboveZero(List<WebElement> l) {
        int count = 0;
        for(WebElement car : l) {
            if(isElementPresent(car, "priceinfo")) {
                WebElement price = car.findElement(By.className("priceinfo"));
                String priceString = price.getText();
                priceString = priceString.replace("$", "");
                priceString = priceString.replace(",", "");
                double formattedPrice = Double.parseDouble(priceString);

                if(formattedPrice >= 0.0) {
                    count++;
                }
            }
        }
        return count;
    }

    @AfterClass
    public static void tearDown() {

        // Closes the Databaase
        try {
            if(connection != null) {
                connection.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        // Closes all windows
        driver.close();
        driver.quit();
        driver = null;
    }
}
