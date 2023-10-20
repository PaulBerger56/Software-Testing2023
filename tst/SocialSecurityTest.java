import io.github.bonigarcia.wdm.WebDriverManager;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.HashMap;

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

    @Ignore
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

    /*
    Wrote a test that passes in income values with a static birthdate.  This checks how much money is returned
    per month at age 67.  At first, we used some normal values, but added $200,000. and were surprised to see that
    the large amount of money didn't hit a cap.

    On the next test, we added much larger values.  The maximum benefit seemed to peak between $300,000 and $500,000

    Using these parameters, @Parameters({ "300000","350000","400000" ,"450000" ,"500000"}) The benefit peaked between
    $300,000. and $350,

    Using these parameters, @Parameters({"300000", "310000", "320000", "320000", "340000", "350000"}) we see that the
    benefit peaked between 300000 and 310000
    **/
    @Test
    @Parameters({"300000", "310000", "320000", "320000", "340000", "350000"})
    public void testMaxValue(String earningsValue){
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
        year.sendKeys("1970");
        earnings.sendKeys(earningsValue);
        submitButton.click();

        WebElement total = driver.findElement(By.cssSelector("#est_fra"));
        String pulledFinalValue = total.getText();
        pulledFinalValue = pulledFinalValue.replace("$", "").replace(",", "");
        System.out.println(earningsValue + ": " + pulledFinalValue);
    }

    /*
    This is my second version of the test where I pass in a range of salaries.  I print out when there is a rise in the
    benefits and the two salaries it happens between to keep track of the new ranges.

    I change the number that the for loop increases by depending on the data set.  If the numbers are very large, I may
    increase every salary by 10,000, but as we narrow things down, I increase it with smaller increments.

    Eventually when we narrow it down, we start increasing by 1 to find the exact salary that produces the
    largest benefit

    After narrowing down the salaries, I found that the salary with the maximum benefit is $306,474.00 with a benefit
    of $3879.00

    A possible improvement when using large data sets would still be increasing by 1, but breaking the tasks into more
    manageable chunks with multi-threading
     */
    @Test
    @Parameters({"306460, 306490"})
    public void testMaxBenefitWithLoop(int min, int max){
        int salaryCap = min;
        HashMap<Integer, Double> salaryAndBenefit = new HashMap<>();

        for(int i = min; i <= max; i++){
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
            year.sendKeys("1970");
            earnings.sendKeys(String.valueOf(i));
            submitButton.click();

            WebElement total = driver.findElement(By.cssSelector("#est_fra"));
            String pulledFinalValue = total.getText();
            pulledFinalValue = pulledFinalValue.replace("$", "").replace(",", "");
            salaryAndBenefit.put(i, Double.valueOf(pulledFinalValue));
            if(salaryAndBenefit.get(i) > salaryAndBenefit.get(salaryCap)){
                System.out.println("There was a jump in benefits from $" + salaryCap + " and $" + i);
                salaryCap = i;
                System.out.println("The new salary with max benefit is $" + i + ": " + salaryAndBenefit.get(i));
            }
        }
    }

    /**
     This Test checks the difference in monthly benefits depending on when they started.  In this case we tracked years
     worked, assuming the later someone started, the fewer years they have worked.

     The test takes a starting and ending year as parameters and uses a static retirement year.  A for loop runs through
     the years and prints the amount of benefit per month based on years worked.

     The Data collected shows that the earlier someone starts, and the more years they work, the more monthly benefits
     they get.
    **/
    @Test
    @Parameters({"1945, 1995"})
    public void testBenefitDifferenceBetweenAges(int startingYear, int finalYear) {

        for(int i = startingYear; i <= finalYear; i+= 10){
            driver.get(CALC_SITE);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

            WebElement month = driver.findElement(By.id("month"));
            WebElement day = driver.findElement(By.id("day"));
            WebElement year = driver.findElement(By.id("year"));
            WebElement earnings = driver.findElement(By.id(("earnings")));
            WebElement submitButton = driver.findElement(By.cssSelector("body > table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(2) > form > table > tbody > tr:nth-child(5) > td > input[type=submit]"));
            WebElement retirementYear = driver.findElement(By.id("retireyear"));
            WebElement retirementMonth = driver.findElement(By.id("retiremonth"));

            month.clear();
            day.clear();
            year.clear();
            earnings.clear();

            month.sendKeys("1");
            day.sendKeys("1");
            year.sendKeys(String.valueOf(i));
            earnings.sendKeys("50000");
            retirementYear.sendKeys("2025");
            retirementMonth.sendKeys("1");
            submitButton.click();

            WebElement benefit = driver.findElement(By.id("ret_amount"));
            String amountString = benefit.getText();
            amountString = amountString.replace(",", "");
            double retirementAmount = Double.parseDouble(amountString);

            System.out.println("Years worked: " + (2025 - i) + " | Monthly benefit: $" + retirementAmount);
        }
    }

    @AfterClass
    public static void tearDown() {
        // Closes all windows
        driver.close();
        driver.quit();
        driver = null;
    }
}
