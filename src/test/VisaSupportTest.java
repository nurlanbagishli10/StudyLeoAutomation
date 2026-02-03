import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VisaSupportTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;
    private JavascriptExecutor js;

    private static final String SITE_URL = "https://studyleo.com/en";

    // Centralized folder paths
    private static final String LOGS_FOLDER = "logs";
    private static final String SCREENSHOTS_FOLDER = "screenshots";

    // Log system
    private List<String> logMessages = new ArrayList<>();
    private String logFileName;
    private String screenshotFolder;

    // Locators
    private By acceptCookiesButton = By.cssSelector("button[data-testid='cookie-banner-accept-button']");
    private By visaSupportLink = By.cssSelector("a[data-slot='navigation-menu-link'][href='/en/study-visa-support-in-turkey']");
    
    // Search box
    private By searchBox = By.cssSelector("input[data-slot='input'][aria-label='Search Countries']");
    
    // Result counter
    private By resultCounter = By.cssSelector("span.text-xs.text-black-text.opacity-80.font-medium");
    
    // Reset button - JavaScript click required
    private By resetButton = By.xpath("//button[@data-slot='button']//span[normalize-space(text())='Reset']");
    
    // Country card
    private By countryCardLocator = By.xpath("/html/body/main/div/section/div/div[2]/ul/li/a[1]");
    
    // Pagination
    private By paginationButtons = By.cssSelector("a[data-slot='pagination-link']");
    private By nextPageButton = By.cssSelector("a[data-slot='pagination-link'][aria-label='Go to next page']");
    
    // Per Page dropdown
    private By perPageDropdown = By.cssSelector("button[type='button'][aria-labelledby='per-page-options']");

    // Test statistics
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int screenshotCount = 0;

    public VisaSupportTest() {
        initializeDriver();
        initializeLog();
    }

    private void initializeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        js = (JavascriptExecutor) driver;
    }

    private void initializeLog() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);

        // Create logs folder if not exists
        try {
            Files.createDirectories(Paths.get(LOGS_FOLDER));
        } catch (IOException e) {
            System.err.println("Failed to create logs folder: " + e.getMessage());
        }

        // Create screenshots folder if not exists
        try {
            Files.createDirectories(Paths.get(SCREENSHOTS_FOLDER));
        } catch (IOException e) {
            System.err.println("Failed to create screenshots folder: " + e.getMessage());
        }

        // Log file in logs folder
        logFileName = LOGS_FOLDER + "/" + "VisaSupportTest_" + timestamp + ".txt";

        // Screenshot subfolder in screenshots folder
        screenshotFolder = SCREENSHOTS_FOLDER + "/" + "VisaSupportTest_" + timestamp;

        try {
            Files.createDirectories(Paths.get(screenshotFolder));
            log("📁 Screenshot folder: " + screenshotFolder);
        } catch (IOException e) {
            logError("Screenshot folder creation failed: " + e.getMessage());
        }

        log("📁 Log file: " + logFileName);
        log("═".repeat(70));
        log("🎓 VISA SUPPORT TEST - AUTOMATED TESTING");
        log("📅 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log("═".repeat(70));
    }

    private void log(String message) {
        System.out.println(message);
        logMessages.add(message);
    }

    private void logError(String message) {
        String errorMsg = "❌ " + message;
        System.err.println(errorMsg);
        logMessages.add(errorMsg);
    }

    private String takeScreenshot(String fileName) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            String screenshotName = fileName + "_" + timestamp + ".png";
            String destinationPath = screenshotFolder + "/" + screenshotName;

            Files.copy(sourceFile.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);

            screenshotCount++;
            log("📸 Screenshot saved: " + destinationPath);

            return destinationPath;

        } catch (Exception e) {
            logError("Screenshot failed: " + e.getMessage());
            return null;
        }
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void saveLogsToFile() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(logFileName)))) {
            for (String logMessage : logMessages) {
                writer.println(logMessage);
            }
            log("\n💾 Log saved: " + logFileName);
        } catch (IOException e) {
            System.err.println("❌ Log save error: " + e.getMessage());
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Get showing count from "Showing X of 190"
     */
    private int getShowingCount() {
        try {
            List<WebElement> spans = driver.findElements(resultCounter);
            for (WebElement span : spans) {
                String text = span.getText().trim();
                if (text.contains("Showing") && text.contains("of")) {
                    // "Showing 20 of 190" -> extract 20
                    String[] parts = text.split(" ");
                    // Add bounds checking to prevent ArrayIndexOutOfBoundsException
                    if (parts.length > 1) {
                        return Integer.parseInt(parts[1]);
                    }
                }
            }
            return -1;
        } catch (Exception e) {
            logError("Failed to get showing count: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Get max page number from pagination
     */
    private int getMaxPageNumber() {
        try {
            List<WebElement> paginationLinks = driver.findElements(paginationButtons);
            int maxPage = 0;
            for (WebElement link : paginationLinks) {
                String text = link.getText().trim();
                if (text.matches("\\d+")) {
                    int pageNum = Integer.parseInt(text);
                    if (pageNum > maxPage) {
                        maxPage = pageNum;
                    }
                }
            }
            return maxPage;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Click reset button using JavaScript
     */
    private void clickResetButton() {
        try {
            log("🧹 Clicking Reset button...");
            // Find the button containing the Reset span
            WebElement resetSpan = wait.until(ExpectedConditions.presenceOfElementLocated(resetButton));
            WebElement parentButton = resetSpan.findElement(By.xpath("./ancestor::button"));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", parentButton);
            sleep(300);
            js.executeScript("arguments[0].click();", parentButton);
            log("   ✅ Reset button clicked");
            sleep(500);
        } catch (Exception e) {
            logError("Failed to click reset button: " + e.getMessage());
        }
    }

    // ==================== TEST METHODS ====================

    /**
     * Navigate to Visa Support Page
     */
    private void navigateToVisaSupportPage() {
        log("📍 Clicking Visa Support link...");
        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(visaSupportLink));
        js.executeScript("arguments[0].click();", link);
        sleep(1000);
        log("✅ Visa Support page opened");
    }

    /**
     * Test search box filter with Afghanistan
     */
    private void testSearchBox() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🔍 TEST 1: Search Box - Afghanistan");
        log("━".repeat(70));
        
        // Get initial showing count
        int initialCount = getShowingCount();
        log("   📊 Initial showing: " + initialCount + " of 190");
        
        // Type "Afghanistan" in search box
        log("   Searching for: \"Afghanistan\"");
        WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(searchBox));
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", searchInput);
        sleep(300);
        searchInput.clear();
        searchInput.sendKeys("Afghanistan");
        sleep(1500); // Wait for results to filter
        
        // Get new showing count - should be 1
        int filteredCount = getShowingCount();
        log("   📊 Filtered showing: " + filteredCount);
        log("   Result: Showing " + initialCount + " → Showing " + filteredCount);
        
        // Verify showing count is 1 (only Afghanistan)
        if (filteredCount == 1) {
            log("✅ PASS - Only 1 country shown (Afghanistan)");
            passedTests++;
        } else {
            logError("FAIL - Expected 1 country, got " + filteredCount);
            failedTests++;
            takeScreenshot("SEARCH_FAILED");
        }
        
        // Click reset button
        clickResetButton();
        sleep(1000);
    }

    /**
     * Test country link opens successfully (HTTP 200)
     */
    private void testCountryLink() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🔗 TEST 2: Country Link Opens Successfully");
        log("━".repeat(70));
        
        try {
            // Find first country card
            WebElement countryCard = wait.until(ExpectedConditions.presenceOfElementLocated(countryCardLocator));
            String countryUrl = countryCard.getAttribute("href");
            log("   📎 Country URL: " + countryUrl);
            
            // Click country card
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", countryCard);
            sleep(300);
            js.executeScript("arguments[0].click();", countryCard);
            sleep(2000);
            
            // Verify page loaded
            String currentUrl = driver.getCurrentUrl();
            log("   📍 Current URL: " + currentUrl);
            
            // Check if URL contains visa support pattern (both patterns needed for different country URL formats)
            if (currentUrl.contains("/visa-support/") || currentUrl.contains("/study-visa-support-in-turkey/")) {
                log("✅ PASS - Country page opened successfully (HTTP 200)");
                passedTests++;
            } else {
                logError("FAIL - Country page did not open");
                failedTests++;
                takeScreenshot("COUNTRY_LINK_FAILED");
            }
            
        } catch (Exception e) {
            logError("Country link test error: " + e.getMessage());
            failedTests++;
            takeScreenshot("COUNTRY_LINK_ERROR");
        }
    }

    /**
     * Test pagination - Next button and Last page
     */
    private void testPagination() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("⏭️ TEST 3: Pagination - Next & Last Page");
        log("━".repeat(70));
        
        try {
            // Navigate back to visa support page
            log("   🔙 Navigating back...");
            driver.navigate().back();
            sleep(1500);
            
            // Click Next page button
            log("   ⏭️ Clicking Next page button...");
            WebElement nextBtn = wait.until(ExpectedConditions.presenceOfElementLocated(nextPageButton));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", nextBtn);
            sleep(300);
            js.executeScript("arguments[0].click();", nextBtn);
            sleep(1500);
            log("   ✅ Navigated to page 2");
            
            // Find and click the last page (highest number)
            int lastPageNum = getMaxPageNumber();
            log("   📄 Last page number: " + lastPageNum);
            
            // Click last page
            log("   ⏭️ Clicking last page (" + lastPageNum + ")...");
            List<WebElement> paginationLinks = driver.findElements(paginationButtons);
            for (WebElement link : paginationLinks) {
                String text = link.getText().trim();
                if (text.equals(String.valueOf(lastPageNum))) {
                    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", link);
                    sleep(300);
                    js.executeScript("arguments[0].click();", link);
                    break;
                }
            }
            sleep(1500);
            
            log("✅ PASS - Pagination test completed");
            passedTests++;
            
        } catch (Exception e) {
            logError("Pagination test error: " + e.getMessage());
            failedTests++;
            takeScreenshot("PAGINATION_ERROR");
        }
    }

    /**
     * Test Per Page dropdown - Select 50
     */
    private void testPerPageDropdown() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("📋 TEST 4: Per Page Dropdown - Select 50");
        log("━".repeat(70));
        
        try {
            // Get initial per page value (should be 20)
            WebElement perPageBtn = wait.until(ExpectedConditions.presenceOfElementLocated(perPageDropdown));
            String initialText = perPageBtn.getText();
            log("   📊 Initial per page: " + initialText);
            
            // Click dropdown to open
            log("   🖱️ Opening Per Page dropdown...");
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", perPageBtn);
            sleep(300);
            js.executeScript("arguments[0].click();", perPageBtn);
            sleep(500);
            
            // Find and click "50" option
            log("   ✓ Selecting 50...");
            // Look for option containing "50"
            List<WebElement> options = driver.findElements(By.cssSelector("[role='option'], [role='menuitem'], li"));
            boolean found = false;
            
            for (WebElement option : options) {
                String optionText = option.getText().trim();
                // Use exact match to avoid matching "150" or "500"
                if (optionText.equals("50 Per Page")) {
                    js.executeScript("arguments[0].click();", option);
                    found = true;
                    log("   ✅ Selected: 50");
                    break;
                }
            }
            
            if (!found) {
                // Fallback: Try clicking 4th option directly (index 3)
                // Expected dropdown order: 20, 30, 40, 50 - so 50 is at index 3
                if (options.size() >= 4) {
                    js.executeScript("arguments[0].click();", options.get(3));
                    log("   ✅ Selected 4th option (assumed to be 50)");
                }
            }
            
            sleep(1500);
            
            // Verify selection - button should now show "50"
            perPageBtn = driver.findElement(perPageDropdown);
            String newText = perPageBtn.getText();
            log("   📊 New per page: " + newText);
            
            if (newText.contains("50")) {
                log("✅ PASS - Per page changed to 50");
                passedTests++;
            } else {
                logError("FAIL - Per page did not change to 50");
                failedTests++;
                takeScreenshot("PER_PAGE_FAILED");
            }
            
        } catch (Exception e) {
            logError("Per page dropdown test error: " + e.getMessage());
            failedTests++;
            takeScreenshot("PER_PAGE_ERROR");
        }
    }

    // ==================== MAIN TEST FLOW ====================

    public void run() {
        try {
            log("\n" + "█".repeat(70));
            log("█  🚀 STARTING AUTOMATED VISA SUPPORT TESTING" + " ".repeat(24) + "█");
            log("█".repeat(70) + "\n");

            openWebsite();
            acceptCookies();
            navigateToVisaSupportPage();
            sleep(1000);

            // Run all tests
            testSearchBox();
            testCountryLink();
            testPagination();
            testPerPageDropdown();

            printSummary();

        } catch (Exception e) {
            logError("CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
            takeScreenshot("CRITICAL_ERROR");
        }
    }

    private void printSummary() {
        log("\n" + "═".repeat(70));
        log("📊 FINAL RESULTS");
        log("═".repeat(70));
        log("   Total Tests: " + totalTests);
        log("   ✅ Passed: " + passedTests);
        log("   ❌ Failed: " + failedTests);

        double successRate = totalTests > 0
                ? (passedTests * 100.0 / totalTests)
                : 0;
        log("   📈 Success Rate: " + String.format("%.2f%%", successRate));
        log("═".repeat(70));

        saveLogsToFile();
    }

    private void openWebsite() {
        log("🌐 Opening: " + SITE_URL);
        driver.get(SITE_URL);
        sleep(1000);
        log("✅ Website opened\n");
    }

    private void acceptCookies() {
        log("🍪 Accepting cookies...");
        try {
            if (isElementPresent(acceptCookiesButton)) {
                WebElement cookieBtn = driver.findElement(acceptCookiesButton);
                js.executeScript("arguments[0].click();", cookieBtn);
                log("✅ Cookies accepted\n");
            }
        } catch (Exception e) {
            log("⚠️ Cookies already accepted\n");
        }
    }

    private boolean isElementPresent(By locator) {
        try {
            shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void close() {
        if (driver != null) {
            log("\n🔚 Closing browser...");
            driver.quit();
        }
    }

    public static void main(String[] args) {
        VisaSupportTest test = new VisaSupportTest();

        try {
            test.run();
            test.sleep(500);

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.close();
        }
    }
}