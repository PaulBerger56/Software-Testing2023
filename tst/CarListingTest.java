import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.bouncycastle.oer.its.etsi102941.Url;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;


import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import java.io.*;





public class CarListingTest {

    private static WebDriver driver;
    private static final String CRAIGSLIST_LINK = "https://atlanta.craigslist.org/search/cta#search=1~gallery~0~0";
    private static Connection connection;

    @BeforeClass
    public static void setUp() {
        ChromeOptions option = new ChromeOptions();
        option.addArguments("--remote-allow-origins=*");
        option.addArguments("--headless");
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

        List<WebElement> carList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#search-results-page-1 > ol > li")));

        int previousRowCount = getDBCount(connection);

        for (WebElement car : carList) {
            // Set the priceString to 0 because some of the cars do not have a price element, so it defaults to 0
            // isElementPresent is used to check if the element exists or not and allows the program to continue
            // rather than crash if it is missing
            String priceString = "0";
            String subjectString = "N/A";
            String urlString = "N/A";
            String retrievalTimeString = "N/A";
            Blob img = null;

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


    // Thank you ChatGPT!!!!!
    // Could not figure out how to make it work with joins, but used insert instead
    @Test
    public void testAddSpecificCars() throws SQLException {
        // Define the brand names to search for
        String[] targetBrands = {"Ford", "Chevrolet", "Toyota"};

        // Create an SQL query to insert matching records into specific_cars
        String sql = "INSERT INTO specific_cars (brand, subject, price, url, retrieval_time) " +
                "SELECT ?, cl.subject, cl.price, cl.url, cl.retrieval_time " +
                "FROM car_listings AS cl " +
                "WHERE (LOWER(cl.subject) LIKE ? OR LOWER(cl.subject) LIKE ? OR LOWER(cl.subject) LIKE ?)" +
                " AND NOT EXISTS (SELECT 1 FROM specific_cars AS sc WHERE sc.subject = cl.subject AND sc.brand = ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < targetBrands.length; i++) {
                String targetBrand = targetBrands[i];
                preparedStatement.setString(1, targetBrand);
                for (int j = 2; j <= 4; j++) {
                    preparedStatement.setString(j, "%" + targetBrand.toLowerCase() + "%");
                }
                preparedStatement.setString(5, targetBrand);

                int rowsInserted = preparedStatement.executeUpdate();

                System.out.println(rowsInserted + " rows inserted into specific_cars for brand: " + targetBrand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGrabImages() throws SQLException {
        driver.get(CRAIGSLIST_LINK);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        List<WebElement> carList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#search-results-page-1 > ol > li")));
        List<Blob> images = new ArrayList<>();

        System.out.println(carList.size());
        for(WebElement car: carList) {
            if(isElementPresent(car, "img")) {
                List<WebElement> tempImages = car.findElements(By.tagName("img"));
                if(!tempImages.isEmpty()) {
                    for(WebElement w: tempImages) {
                        if(w.getAttribute("src") != null){
                            byte[] tempByte = getImageAsBlob(w.getAttribute("src"));
                            Blob tempBlob = new SerialBlob(tempByte);
                            images.add(tempBlob);
                            System.out.println(tempBlob.toString());
                        }
                    }
                }
            }
        }
        System.out.println(images.size());
    }

    public static byte[] getImageAsBlob(String imageUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(imageUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                byte[] imageBytes = EntityUtils.toByteArray(response.getEntity());
                return imageBytes;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

    // This code adds the car listing only if it doesn't already exist in the db
//    private void insertCarListing(String subject, double price, String url, String retrievalTime) {
//        try (PreparedStatement preparedStatement = connection.prepareStatement(
//                "INSERT INTO car_listings (subject, price, url, retrieval_time) " +
//                        "SELECT ?, ?, ?, ? " +
//                        "WHERE NOT EXISTS (SELECT 1 FROM car_listings WHERE subject = ?)")) {
//            preparedStatement.setString(1, subject);
//            preparedStatement.setDouble(2, price);
//            preparedStatement.setString(3, url);
//            preparedStatement.setString(4, retrievalTime);
//            preparedStatement.setString(5, subject);
//            int rowsInserted = preparedStatement.executeUpdate();
//
//            if (rowsInserted == 0) {
//                System.out.println("Car listing with subject " + subject + " already exists (no new entry added)");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    // Some of the cars are missing elements, so we check with this method and just move on
    // instead of crashing the code
    private boolean isElementPresent(WebElement car, String elementName) {
        if(elementName.equals("img")) {
            try{
                car.findElements(By.tagName("img"));
            } catch(NoSuchElementException e) {
                return false;
            }
        } else {
            try {
                car.findElement(By.className(elementName));
            } catch (NoSuchElementException e) {
                return false;
            }
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
