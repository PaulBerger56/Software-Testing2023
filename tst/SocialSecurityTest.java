import io.github.bonigarcia.wdm.WebDriverManager;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

@RunWith(JUnitParamsRunner.class)
public class SocialSecurityTest {

    private static WebDriver driver;
    private static final String CALC_SITE = "https://www.ssa.gov/OACT/quickcalc/";

    @BeforeClass
    public static void setUp() {
        ChromeOptions option = new ChromeOptions();
        option.addArguments("--remote-allow-origins=*");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(option);
    }

    @Ignore
    @Test
    public void testOpeningSite() {
        driver.get(CALC_SITE);
    }

    @Test
    @Parameters({"1990, 55000, 2141.00", "1980, 65000, 2322.00", "1970, 75000, 2391.00"})
    public void testDifferentAges(String birthYear, String earningsValue, String finalAmount){
        driver.get(CALC_SITE);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        WebElement month = driver.findElement(By.id("month"));
        WebElement day = driver.findElement(By.id("day"));
        WebElement year = driver.findElement(By.id("year"));
        WebElement earnings = driver.findElement(By.id(("earnings")));
        WebElement submitButton = driver.findElement(By.cssSelector("body > table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(2) > form > table > tbody > tr:nth-child(5) > td > input[type=submit]"));

        month.clear();
        day.clear();
        year.clear();
        earnings.clear();

        month.sendKeys("1");
        day.sendKeys("1");
        year.sendKeys(birthYear);
        earnings.sendKeys(earningsValue);
        submitButton.click();

        WebElement total = driver.findElement(By.cssSelector("#est_fra"));
        String pulledFinalValue = total.getText();
        pulledFinalValue = pulledFinalValue.replace("$", "").replace(",", "");
        System.out.println(pulledFinalValue);
        Assert.assertEquals(finalAmount,pulledFinalValue);
    }

    @AfterClass
    public static void tearDown() {
        // Closes all windows
        driver.close();
        driver.quit();
        driver = null;
    }
}
