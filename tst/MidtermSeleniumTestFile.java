import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.List;

public class MidtermSeleniumTestFile {

    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        ChromeOptions option = new ChromeOptions();
        option.addArguments("--remote-allow-origins=*");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(option);
    }

    @Test
    public void testNewsSite() {
        driver.get("https://news.ycombinator.com/");

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        List<WebElement> headlineList = driver.findElements(By.className("rank"));

        Assert.assertEquals("According to the instructions, the site should have 30 headlines",
                30, headlineList.size());
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
        driver.quit();
        driver = null;
    }
}
