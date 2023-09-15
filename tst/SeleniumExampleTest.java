import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


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
        System.setProperty("webdriver.chrome.whitelistedIps", "");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(option);
    }

    @Test
    public void testGGC() {
        driver.get("https://www.ggc.edu");
    }

    @Test
    public void testReddit() {
        // opens a new tab and opens reddit
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("http://www.reddit.com");
    }
}

