import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@RunWith(JUnitParamsRunner.class)
public class ParameterTest {

    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        driver = new FirefoxDriver();
    }

    @Test
    @Parameters({"Georgia Gwinnett College","Georgia State University","Kennesaw State University"})
    public void testGoogle(String searchString) {
        driver.get("https://www.google.com");
        WebElement textBox = driver.findElement(By.name("q"));
        textBox.sendKeys(searchString);
        textBox.submit();

        List<WebElement> spans = driver.findElements(By.tagName("span"));
        for(WebElement span : spans) {
            String spanText = span.getText();
            if(spanText.contains(searchString)) {
                System.out.println(spanText);
            }
        }
    }

    @AfterClass
    public static void cleanUp() {
        driver.close();
    }
}
