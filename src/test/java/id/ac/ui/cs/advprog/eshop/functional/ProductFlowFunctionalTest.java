package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductFlowFunctionalTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setUp() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");
        driver = new FirefoxDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testCreateEditDeleteProductFlow() {
        String baseUrl = "http://localhost:" + port;
        String productName = "Selenium Product " + UUID.randomUUID().toString().substring(0, 8);
        String updatedName = productName + " Updated";

        driver.get(baseUrl + "/product/list");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        driver.findElement(By.linkText("Create Product")).click();
        wait.until(ExpectedConditions.urlContains("/product/create"));

        WebElement nameInput = driver.findElement(By.id("nameInput"));
        WebElement quantityInput = driver.findElement(By.id("quantityInput"));
        nameInput.sendKeys(productName);
        quantityInput.sendKeys("10");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/product/list"));
        WebElement createdRow = waitForRowByName(productName);
        assertEquals("10", createdRow.findElements(By.tagName("td")).get(1).getText());

        createdRow.findElement(By.linkText("Edit")).click();
        wait.until(ExpectedConditions.urlContains("/product/edit"));

        WebElement editNameInput = driver.findElement(By.id("nameInput"));
        WebElement editQuantityInput = driver.findElement(By.id("quantityInput"));
        editNameInput.clear();
        editNameInput.sendKeys(updatedName);
        editQuantityInput.clear();
        editQuantityInput.sendKeys("25");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/product/list"));
        WebElement updatedRow = waitForRowByName(updatedName);
        assertEquals("25", updatedRow.findElements(By.tagName("td")).get(1).getText());

        updatedRow.findElement(By.cssSelector("form button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/product/list"));
        waitForRowMissing(updatedName);
    }

    private WebElement waitForRowByName(String name) {
        return wait.until(driver -> {
            List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (!cells.isEmpty() && name.equals(cells.get(0).getText())) {
                    return row;
                }
            }
            return null;
        });
    }

    private void waitForRowMissing(String name) {
        boolean removed = wait.until(driver -> {
            List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (!cells.isEmpty() && name.equals(cells.get(0).getText())) {
                    return false;
                }
            }
            return true;
        });
        assertTrue(removed);
    }
}
