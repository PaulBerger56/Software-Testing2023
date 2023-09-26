import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;

public class TuitionCalculatorTest {

    private final String financialAidLink = "https://www.ggc.edu/admissions/tuition-and-financial-aid-calculators";
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
    @Ignore
    public void testWebsiteAccess() {
        driver.get(financialAidLink);
    }

    @Test
    public void testInState() {
        driver.get(financialAidLink);

        WebElement yesRadioButton = driver.findElement(By.cssSelector("#myform > div:nth-child(1) > div > div > div:nth-child(1) > fieldset > div > label:nth-child(1)"));
        yesRadioButton.click();

        WebElement hours = driver.findElement(By.cssSelector("#creditHOURS > option:nth-child(1)"));
        hours.click();

        WebElement livingOnCampus = driver.findElement(By.cssSelector("#onoroff0"));
        livingOnCampus.click();

        WebElement estimateButton = driver.findElement(By.cssSelector("#myform > div:nth-child(2) > div > div:nth-child(3) > input"));
        estimateButton.click();

        WebElement totalCost = driver.findElement(By.name("totalcost"));
        String tuitionAmountString = totalCost.getAttribute("value");
        tuitionAmountString = tuitionAmountString.replace("$","");
        double tuitionAmount = Double.parseDouble(tuitionAmountString);

        Assert.assertEquals(5262.00, tuitionAmount * 2, 0);
    }

    @Test
    public void testOutOfState() {
        driver.get(financialAidLink);

        WebElement noRadioButton = driver.findElement(By.cssSelector("#inorout0"));
        noRadioButton.click();

        WebElement hours = driver.findElement(By.cssSelector("#creditHOURS > option:nth-child(1)"));
        hours.click();

        WebElement livingOnCampus = driver.findElement(By.cssSelector("#onoroff0"));
        livingOnCampus.click();

        WebElement estimateButton = driver.findElement(By.cssSelector("#myform > div:nth-child(2) > div > div:nth-child(3) > input"));
        estimateButton.click();

        WebElement totalCost = driver.findElement(By.name("totalcost"));
        String tuitionAmountString = totalCost.getAttribute("value");
        tuitionAmountString = tuitionAmountString.replace("$","");
        double tuitionAmount = Double.parseDouble(tuitionAmountString);

        Assert.assertEquals(16244.00, tuitionAmount * 2, 0);
    }

    @Test
    public void testMyTuition() {
        driver.get("https://ggc.gabest.usg.edu/pls/B400/twbkwbis.P_ValLogin");

        WebElement userID = driver.findElement(By.name("sid"));
        userID.clear();
        userID.sendKeys(Credentials.ID);

        WebElement pin = driver.findElement(By.name("PIN"));
        pin.clear();
        pin.sendKeys(Credentials.PIN);

        WebElement login = driver.findElement(By.cssSelector("body > div.pagebodydiv > form > p > input[type=submit]"));
        login.click();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        WebElement studentAccountCenter = driver.findElement(By.cssSelector("body > div.pagebodydiv > table.menuplaintable > tbody > tr:nth-child(7) > td:nth-child(2) > a"));
        studentAccountCenter.click();

        WebElement accountSummaryForTerm = driver.findElement(By.cssSelector("body > div.pagebodydiv > div > table > tbody > tr > td > span > table > tbody > tr:nth-child(3) > td > font > table.menuplaintable > tbody > tr:nth-child(1) > td:nth-child(2) > a > font > b"));
        accountSummaryForTerm.click();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        WebElement termSelector = driver.findElement(By.cssSelector("#term_id > option:nth-child(1)"));
        termSelector.click();

        WebElement submitButton = driver.findElement(By.cssSelector("body > div.pagebodydiv > form > input[type=submit]"));
        submitButton.click();

        WebElement tuitionBox = driver.findElement(By.cssSelector("body > div.pagebodydiv > form > table:nth-child(5) > tbody > tr:nth-child(14) > td:nth-child(2) > p"));
        String tuitionAmountString = tuitionBox.getText();
        tuitionAmountString = tuitionAmountString.replace("$", "");
        tuitionAmountString = tuitionAmountString.replace(",", "");
        double tuitionAmount = Double.parseDouble(tuitionAmountString);

        System.out.println("My tuition amount for the year is $" + tuitionAmount*2);
        System.out.println("The website amount for the year is $5262.00");

        // This should return false because my tuition does not match the amount listed on the website.
        //  I am not full 15 hours
        //Assert.assertFalse((tuitionAmount * 2) == 5262.00);

        // This test works using just the tuition amounts
        Assert.assertEquals(3750.04,tuitionAmount * 2,0);

        // Can use this test as a purposeful fail to double-check the amounts do not match
//        Assert.assertEquals("Should return false because my tuition does not match" +
//                "the website's amount",5262.00, tuitionAmount*2, 0);
    }
}
