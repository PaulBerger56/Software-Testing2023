import Midterm.Lawyer;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class MidtermTests {

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
    public void vacationDaysTest() {
        Lawyer lawyer = new Lawyer("Paul");
        Assert.assertEquals("Midterm.Lawyer's getVacationDays overrides Midterm.Employee's so it should be 1",
                1, lawyer.getVacationDays());
    }

    @Test
    public void fileDownloadTest() throws IOException {

        String fileUrl = "https://gist.githubusercontent.com/tacksoo/d1fcb51f8921cdc90d1ffadb0b63b768/raw/6c9a8b9ffadd87b4bd0217b91cdd90bb9e227ef2/schedule.csv";

        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try (InputStream inputStream = connection.getInputStream()) {
            String fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            String[] lines = fileContent.split("\n");
            int lineCount = lines.length;

            Assert.assertEquals("According to the instructions, there are 29 lines.", 29, lineCount);
        }
    }

    @Test
    public void stonkTest() throws IOException {

        FileUtils.copyURLToFile(new URL("https://gist.githubusercontent.com/tacksoo/b9edbfc8c03e1ca89d459bf1af39842d/raw/75abf553a0297d9202b1a568f185f735055d6f81/stonks.csv"), new File("Download.csv"));
        FileReader fr = new FileReader("Download.csv");
        CSVParser parser = new CSVParser(fr,
                CSVFormat.DEFAULT.builder().setIgnoreSurroundingSpaces(true).setHeader().build());

        int recordLine = 0;
        CSVRecord lowestPercentage = null;

        for(CSVRecord r: parser) {
            if(lowestPercentage == null) {
                lowestPercentage = r;
            }

            String lowestPercentageFormatted = lowestPercentage.get("Prediction");
            lowestPercentageFormatted = lowestPercentageFormatted.replace("%", "");
            double lowestPercentageDouble = Double.parseDouble(lowestPercentageFormatted);

            String currentPercentageFormatted = r.get("Prediction");
            currentPercentageFormatted = currentPercentageFormatted.replace("%", "");
            double currentPercentageDouble = Double.parseDouble(currentPercentageFormatted);

            if(currentPercentageDouble < lowestPercentageDouble) {
                lowestPercentage = r;
            }
        }


        if(lowestPercentage != null) {
            File outputFile = new File("stonk.csv");
            String lowestPercentageCSV = String.join(", ", lowestPercentage);
            FileUtils.writeStringToFile(outputFile, lowestPercentageCSV, StandardCharsets.UTF_8);
        }
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
