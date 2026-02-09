package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateProductFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    private static final String BRAVE_BINARY =
            "C:\\\\Program Files\\\\BraveSoftware\\\\Brave-Browser\\\\Application\\\\brave.exe";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setUp() {
        String envVersion = System.getenv("BRAVE_VERSION");
        String version = envVersion != null ? envVersion : detectBraveVersion();
        String majorVersion = extractMajorVersion(version);
        WebDriverManager manager = WebDriverManager.chromedriver();
        if (majorVersion != null && !majorVersion.isBlank()) {
            manager.browserVersion(majorVersion);
        }
        manager.setup();

        ChromeOptions options = new ChromeOptions();
        if (Files.isRegularFile(Path.of(BRAVE_BINARY))) {
            options.setBinary(BRAVE_BINARY);
        }
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    void setupTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    @Test
    void createProduct_showsInProductList() {
        String productName = "CreateTest " + UUID.randomUUID().toString().substring(0, 8);
        String productQuantity = "15";

        driver.get(baseUrl + "/product/list");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        driver.findElement(By.linkText("Create Product")).click();
        wait.until(ExpectedConditions.urlContains("/product/create"));

        driver.findElement(By.id("nameInput")).sendKeys(productName);
        driver.findElement(By.id("quantityInput")).sendKeys(productQuantity);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/product/list"));
        WebElement createdRow = waitForRowByName(wait, productName);
        assertNotNull(createdRow);
        assertEquals(productQuantity, createdRow.findElements(By.tagName("td")).get(1).getText());
    }

    private WebElement waitForRowByName(WebDriverWait wait, String name) {
        return wait.until(webDriver -> {
            List<WebElement> rows = webDriver.findElements(By.cssSelector("table tbody tr"));
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (!cells.isEmpty() && name.equals(cells.get(0).getText())) {
                    return row;
                }
            }
            return null;
        });
    }

    private static String detectBraveVersion() {
        if (!Files.isRegularFile(Path.of(BRAVE_BINARY))) {
            return null;
        }
        try {
            String command = "(Get-Item '" + BRAVE_BINARY.replace("'", "''")
                    + "').VersionInfo.FileVersion";
            Process process = new ProcessBuilder("powershell", "-NoProfile", "-Command", command)
                    .redirectErrorStream(true)
                    .start();
            byte[] output = process.getInputStream().readAllBytes();
            String text = new String(output, StandardCharsets.UTF_8);
            Matcher matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)").matcher(text);
            String last = null;
            while (matcher.find()) {
                last = matcher.group(1);
            }
            return last;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String extractMajorVersion(String version) {
        if (version == null || version.isBlank()) {
            return null;
        }
        int dotIndex = version.indexOf('.');
        return dotIndex > 0 ? version.substring(0, dotIndex) : version;
    }

}
