import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarListingTest {

    private static WebDriver driver;

    private final String CRAIGSLIST_LINK = "https://atlanta.craigslist.org/search/cta#search=1~gallery~0~0";

    @BeforeClass
    public static void setUp() {
        ChromeOptions option = new ChromeOptions();
        option.addArguments("--remote-allow-origins=*");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(option);
    }

    @Test
    public void testWebsite() {
        driver.get(CRAIGSLIST_LINK);
    }

    @Test
    public void testNumberOfCars() {
        driver.get(CRAIGSLIST_LINK);

        // had to add in a timeout because we were trying to grab the elements before they loaded
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        List<WebElement> carList = driver.findElements(By.cssSelector("#search-results-page-1 > ol > li"));

        driver.close();

        Assert.assertEquals(120, carList.size());
    }
}
