import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FinalExamHealthcareTest {

    private static WebDriver driver;
    private static String website = "https://www.healthcare.gov/lower-costs/qualifying-for-lower-costs/";

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
    public void healthcareSiteTest() throws InterruptedException {
        driver.get(website);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        WebElement popupExitButton = driver.findElement(By.className("prefix-overlay-close"));
        popupExitButton.click();

        WebElement stateDropDown = driver.findElement(By.id("dropdown__button--31"));
        stateDropDown.click();

        WebElement georgia = driver.findElement(By.id("dropdown__button--31__item--12"));
        georgia.click();

        WebElement numberOfPeopleDropdown = driver.findElement(By.id("dropdown__button--33"));
        numberOfPeopleDropdown.click();

        WebElement five = driver.findElement(By.id("dropdown__button--33__item--5"));
        five.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement incomeDropdown = driver.findElement(By.id("dropdown__button--35"));
        wait.until(ExpectedConditions.elementToBeClickable(incomeDropdown));
        incomeDropdown.click();

        new Actions(driver).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).perform();

        WebElement getNextStepsButton = driver.findElement(By.xpath("//*[@id=\"main\"]/div[2]/div[3]/button"));
        getNextStepsButton.click();

        WebElement textHolder = driver.findElement(By.xpath("//*[@id=\"main\"]/div[2]/div[3]/ul[1]/li[1]"));
        String text = textHolder.getText();

        Assert.assertTrue(text.contains("You may qualify for a Marketplace plan with lower monthly premiums"));
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
        driver.quit();
        driver = null;
    }
}
