import io.github.bonigarcia.wdm.WebDriverManager;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.List;

@RunWith(JUnitParamsRunner.class)
public class FinalExamYoutubeTest {
    private static WebDriver driver;
    private static String website = "https://www.youtube.com/";

    @BeforeClass
    public static void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
    }

    @Test
    @Parameters({"Q star", "Election 2024", "AGI"})
    public void youtubeTest(String search) throws InterruptedException {
        driver.manage().window().maximize();
        driver.get(website);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        WebElement searchBar = driver.findElement(By.id("search-input"));
        searchBar.click();

        Thread.sleep(5000);

        new Actions(driver).sendKeys(search).sendKeys(Keys.ENTER).perform();

        Thread.sleep(7000);
        WebElement results = driver.findElement(By.xpath("//*[@id=\"container\"]/ytd-two-column-search-results-renderer"));
        List<WebElement> thumbnails = results.findElements(By.id("thumbnail"));
        for(WebElement w: thumbnails) {
            String url = w.getAttribute("href");
            System.out.println(url);
        }
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
        driver.quit();
        driver = null;
    }
}
