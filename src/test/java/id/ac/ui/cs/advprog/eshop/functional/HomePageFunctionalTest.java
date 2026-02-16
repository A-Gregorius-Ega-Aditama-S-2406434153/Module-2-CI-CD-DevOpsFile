package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HomePageFunctionalTest {

    /**
     * The port number assigned to the running application during test execution.
     * Set automatically during each test run by Spring Framework's test context.
     */
    @LocalServerPort
    private int serverPort;

    /**
     * The base URL for testing. Default to {@code http://localhost}.
     */
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
    void pageTitle_isCorrect() {
        // Exercise
        driver.get(baseUrl);
        wait.until(ExpectedConditions.titleIs("ADV Shop"));
        String pageTitle = driver.getTitle();

        // Verify
        assertEquals("ADV Shop", pageTitle);
    }

    @Test
    void welcomeMessage_homePage_isCorrect() {
        // Exercise
        driver.get(baseUrl);
        String welcomeMessage = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3")))
                .getText();

        // Verify
        assertEquals("Welcome", welcomeMessage);
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
