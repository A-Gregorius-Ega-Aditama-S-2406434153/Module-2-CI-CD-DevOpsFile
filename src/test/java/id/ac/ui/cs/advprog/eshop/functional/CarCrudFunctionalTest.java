package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

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
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarCrudFunctionalTest {

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
    void createEditDeleteCar_flowWorksEndToEnd() {
        String baseName = "CarFlow-" + UUID.randomUUID().toString().substring(0, 8);
        String createdName = baseName + "-A";
        String updatedName = baseName + "-B";

        driver.get(baseUrl + "/car/listCar");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        driver.findElement(By.linkText("Create Car")).click();
        wait.until(ExpectedConditions.urlContains("/car/createCar"));

        driver.findElement(By.id("nameInput")).sendKeys(createdName);
        driver.findElement(By.id("colorInput")).sendKeys("Black");
        driver.findElement(By.id("quantityInput")).sendKeys("5");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/car/listCar"));
        WebElement createdRow = waitForRowByName(createdName);
        assertNotNull(createdRow);
        assertEquals("Black", createdRow.findElements(By.tagName("td")).get(1).getText());
        assertEquals("5", createdRow.findElements(By.tagName("td")).get(2).getText());

        createdRow.findElement(By.linkText("Edit")).click();
        wait.until(ExpectedConditions.urlContains("/car/editCar/"));

        WebElement nameInput = driver.findElement(By.id("carName"));
        nameInput.clear();
        nameInput.sendKeys(updatedName);

        WebElement colorInput = driver.findElement(By.id("carColor"));
        colorInput.clear();
        colorInput.sendKeys("Red");

        WebElement quantityInput = driver.findElement(By.id("carQuantity"));
        quantityInput.clear();
        quantityInput.sendKeys("8");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/car/listCar"));
        WebElement updatedRow = waitForRowByName(updatedName);
        assertNotNull(updatedRow);
        assertEquals("Red", updatedRow.findElements(By.tagName("td")).get(1).getText());
        assertEquals("8", updatedRow.findElements(By.tagName("td")).get(2).getText());

        updatedRow.findElement(By.cssSelector("form button[type='submit']")).click();
        wait.until(webDriver -> findRowByName(updatedName) == null);
        assertNull(findRowByName(updatedName));
    }

    @Test
    void createCarWithNegativeQuantity_isRejected() {
        String carName = "CarInvalid-" + UUID.randomUUID().toString().substring(0, 8);

        driver.get(baseUrl + "/car/createCar");
        wait.until(ExpectedConditions.urlContains("/car/createCar"));

        driver.findElement(By.id("nameInput")).sendKeys(carName);
        driver.findElement(By.id("colorInput")).sendKeys("Blue");
        WebElement quantityInput = driver.findElement(By.id("quantityInput"));
        quantityInput.clear();
        quantityInput.sendKeys("-3");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/car/listCar"));
        assertNull(findRowByName(carName));
    }

    private WebElement waitForRowByName(String name) {
        return wait.until(webDriver -> {
            WebElement row = findRowByName(name);
            return row == null ? null : row;
        });
    }

    private WebElement findRowByName(String name) {
        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (!cells.isEmpty() && name.equals(cells.get(0).getText())) {
                return row;
            }
        }
        return null;
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
