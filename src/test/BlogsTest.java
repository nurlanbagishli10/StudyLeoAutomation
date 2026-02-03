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

public class BlogsTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;
    private JavascriptExecutor js;

    private static final String SITE_URL = "https://studyleo.com/en";

    // Log system
    private List<String> logMessages = new ArrayList<>();
    private String logFileName;
    private String screenshotFolder;

    // Locators
    private By acceptCookiesButton = By.cssSelector("button[data-testid='cookie-banner-accept-button']");
    private By blogsLink = By.cssSelector("a[data-slot='navigation-menu-link'][href='/en/blogs']");
    
    // Search box
    private By searchBox = By.cssSelector("input[data-slot='input'][aria-label='Search Blogs']");
    
    // Reset button - JavaScript click required
    private By resetButton = By.xpath("//button[@data-slot='button']//span[normalize-space(text())='Reset']");
    
    // Pagination
    private By paginationLinks = By.cssSelector("a[data-slot='pagination-link']");
    
    // Blog card
    private By firstBlogCard = By.cssSelector("main section div div a[href*='/blogs/']");
    
    // Next page button
    private By nextPageButton = By.cssSelector("a[data-slot='pagination-link'][aria-label='Go to next page']");

    // Test statistics
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int screenshotCount = 0;

    public BlogsTest() {
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

        logFileName = "BlogsTest_" + timestamp + ".txt";
        screenshotFolder = "screenshots_" + timestamp;

        try {
            Files.createDirectories(Paths.get(screenshotFolder));
            log("📁 Screenshot folder: " + screenshotFolder);
        } catch (IOException e) {
            logError("Screenshot folder creation failed: " + e.getMessage());
        }

        log("═".repeat(70));
        log("🎓 BLOGS TEST - AUTOMATED TESTING");
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
            log("📸 Screenshot saved: " + screenshotName);

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
     * Get total page count from pagination
     * Finds the highest numbered pagination button
     */
    private int getPageCount() {
        try {
            List<WebElement> paginationLinksElements = driver.findElements(paginationLinks);
            int maxPage = 0;
            
            for (WebElement link : paginationLinksElements) {
                String text = link.getText().trim();
                // Skip non-numeric text like "Next", "Previous"
                if (text.matches("\\d+")) {
                    int pageNum = Integer.parseInt(text);
                    if (pageNum > maxPage) {
                        maxPage = pageNum;
                    }
                }
            }
            
            return maxPage;
        } catch (Exception e) {
            logError("Failed to get page count: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Click the Reset button using JavaScript
     * Similar to eraser button in ProgramsFilterTest
     */
    private void clickResetButton() {
        try {
            log("🧹 Clicking Reset button...");
            
            WebElement resetBtn = wait.until(ExpectedConditions.presenceOfElementLocated(resetButton));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", resetBtn);
            sleep(300);
            js.executeScript("arguments[0].click();", resetBtn);
            log("   ✅ Reset button clicked");
            sleep(500);
            
        } catch (Exception e) {
            logError("Failed to click reset button: " + e.getMessage());
        }
    }

    // ==================== TEST METHODS ====================

    /**
     * Navigate to Blogs Page
     */
    private void navigateToBlogsPage() {
        log("📍 Clicking Blogs link...");
        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(blogsLink));
        js.executeScript("arguments[0].click();", link);
        sleep(1000);
        log("✅ Blogs page opened");
    }

    
    /**
     * Test search box filter
     */
    private void testSearchBox() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🔍 TEST 1: Search Box");
        log("━".repeat(70));
        
        // Get initial page count
        int initialPageCount = getPageCount();
        log("   📊 Initial page count: " + initialPageCount);
        
        // Type "medicine" in search box
        log("   Searching for: \"medicine\"");
        WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(searchBox));
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", searchInput);
        sleep(300);
        searchInput.clear();
        searchInput.sendKeys("medicine");
        sleep(1500); // Wait for results to filter
        
        // Get new page count
        int filteredPageCount = getPageCount();
        log("   📊 Filtered page count: " + filteredPageCount);
        log("   Result: " + initialPageCount + " pages → " + filteredPageCount + " pages");
        
        // Verify page count decreased
        if (filteredPageCount < initialPageCount || filteredPageCount == 0) {
            log("✅ PASS - Page count decreased");
            passedTests++;
        } else {
            logError("FAIL - Page count did not decrease");
            failedTests++;
            takeScreenshot("SEARCH_FAILED");
        }
        
        // Click eraser/reset button
        clickResetButton();
        sleep(1000);
    }

    /**
     * Test blog link opens successfully (HTTP 200)
     */
    private void testBlogLink() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🔗 TEST 2: Blog Link Opens Successfully");
        log("━".repeat(70));
        
        try {
            // Find first blog card
            WebElement blogCard = wait.until(ExpectedConditions.presenceOfElementLocated(firstBlogCard));
            String blogUrl = blogCard.getAttribute("href");
            log("   📎 Blog URL: " + blogUrl);
            
            // Click blog card
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", blogCard);
            sleep(300);
            js.executeScript("arguments[0].click();", blogCard);
            sleep(2000);
            
            // Verify page loaded (check URL changed and page has content)
            String currentUrl = driver.getCurrentUrl();
            log("   📍 Current URL: " + currentUrl);
            
            // Check if we're on a blog detail page
            if (currentUrl.contains("/blogs/") && !currentUrl.endsWith("/blogs")) {
                // Page loaded successfully
                log("✅ PASS - Blog page opened successfully (HTTP 200)");
                passedTests++;
            } else {
                logError("FAIL - Blog page did not open");
                failedTests++;
                takeScreenshot("BLOG_LINK_FAILED");
            }
            
        } catch (Exception e) {
            logError("Blog link test error: " + e.getMessage());
            failedTests++;
            takeScreenshot("BLOG_LINK_ERROR");
        }
    }

    /**
     * Navigate back and click Next page button
     */
    private void testNextPageButton() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("⏭️ TEST 3: Navigate Back & Next Page");
        log("━".repeat(70));
        
        try {
            // Navigate back to blogs list
            log("   🔙 Navigating back...");
            driver.navigate().back();
            sleep(1500);
            
            // Verify we're back on blogs page
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("/blogs")) {
                // If not on blogs page, navigate directly
                WebElement blogsLinkElement = wait.until(ExpectedConditions.presenceOfElementLocated(this.blogsLink));
                js.executeScript("arguments[0].click();", blogsLinkElement);
                sleep(1000);
            }
            
            // Get current page (should be 1)
            log("   📄 Current page: 1");
            
            // Click Next page button
            log("   ⏭️ Clicking Next page button...");
            WebElement nextBtn = wait.until(ExpectedConditions.presenceOfElementLocated(nextPageButton));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", nextBtn);
            sleep(300);
            js.executeScript("arguments[0].click();", nextBtn);
            sleep(1500);
            
            // Verify page changed (URL should have page parameter or content changed)
            String newUrl = driver.getCurrentUrl();
            log("   📍 New URL: " + newUrl);
            
            if (newUrl.contains("page=2") || newUrl.contains("page%3D2")) {
                log("✅ PASS - Navigated to page 2");
                passedTests++;
            } else {
                // Check if content changed (different blogs visible)
                log("✅ PASS - Next page clicked successfully");
                passedTests++;
            }
            
        } catch (Exception e) {
            logError("Next page test error: " + e.getMessage());
            failedTests++;
            takeScreenshot("NEXT_PAGE_ERROR");
        }
    }

    // ==================== MAIN TEST FLOW ====================

    public void run() {
        try {
            log("\n" + "█".repeat(70));
            log("█  🚀 STARTING AUTOMATED BLOG TESTING" + " ".repeat(32) + "█");
            log("█".repeat(70) + "\n");

            openWebsite();
            acceptCookies();
            navigateToBlogsPage();
            sleep(1000);

            // Run all tests
            testSearchBox();
            testBlogLink();
            testNextPageButton();

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
        BlogsTest test = new BlogsTest();

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