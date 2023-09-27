import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.sql.SQLException;
import java.time.Duration;


public class SeleniumExampleTest {

    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        // had to add the apache.logging.log4j.slf4j.impl library and the options below to make this work
        // found out about the library on https://www.youtube.com/watch?v=FtdvhPEVhuE
        // found out about the options on https://stackoverflow.com/questions/75678572/java-io-ioexception-invalid-status-code-403-text-forbidden

        ChromeOptions option = new ChromeOptions();
        option.addArguments("--remote-allow-origins=*");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
//        System.setProperty("webdriver.chrome.whitelistedIps", "");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(option);
    }

    @Ignore
    @Test
    public void testGGC() {
        driver.get("https://www.ggc.edu");
    }

    @Ignore
    @Test
    public void testReddit() {
        // opens a new tab and opens reddit
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("http://www.reddit.com");
    }

    @Test
    public void googleSearchExample() {
        driver.get("http:www.google.com");
        WebElement element = driver.findElement(By.name("q"));

        element.clear();
        element.sendKeys("GGC");

        element.submit();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        Assert.assertTrue(driver.getTitle().contains("GGC"));
    }


    @Test
    public void testCraigslistBestOf() {
        driver.get("https://www.craigslist.org/about/best/all");
        WebElement element = driver.findElement(By.cssSelector("#page-top > section > div > table > tbody > tr:nth-child(2)"));
        System.out.println(element.getText());
    }

    @Test
    public void testForm() {
        driver.get("https://ihgrewardsdineandearn.rewardsnetwork.com/join");

        WebElement firstName = driver.findElement(By.name("firstName"));
        firstName.clear();
        firstName.sendKeys("Paul");


        WebElement lastName = driver.findElement(By.name("lastName"));
        lastName.clear();
        lastName.sendKeys("Berger");


        WebElement zip = driver.findElement(By.name("zipCode"));
        zip.clear();
        zip.sendKeys("30024");


        WebElement rewardNumber = driver.findElement(By.name("partnerProgramNumber"));
        rewardNumber.clear();
        rewardNumber.sendKeys("9999999999999999");


        WebElement email = driver.findElement(By.name("email"));
        email.clear();
        email.sendKeys("pberger1@ggc.edu");


        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys("fakepassworD1");


        WebElement emailOptIn = driver.findElement(By.name("emailOptIn"));
        emailOptIn.click();


        WebElement submitButton = driver.findElement(By.cssSelector("#main > div.page__content > div > div.Step1Form__Wrapper-sc-giuvmq-0.iVjcDa > form > div.enrollment_1__actions > button"));
        submitButton.click();
    }

    @AfterClass
    public static void tearDown() {
        // Closes all windows
        driver.close();
        driver.quit();
        driver = null;
    }
}

