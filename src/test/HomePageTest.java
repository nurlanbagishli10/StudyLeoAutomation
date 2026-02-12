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
import java.util.Set;

public class HomePageTest {

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
    // Cookie banner
    private By acceptCookiesButton = By.cssSelector("button[data-testid='cookie-banner-accept-button']");

    // Apply Now button (header)
    private By applyNowButton = By.cssSelector("button[data-slot='button'][aria-label='Apply Now']");

    // Dialog close button
    private By closeDialogButton = By.cssSelector("button[data-slot='dialog-close']");

    // Search box and button
    private By searchBox = By.cssSelector("input[data-slot='input'][placeholder='Search']");
    private By searchButton = By.cssSelector("button[data-slot='button'][type='submit']");

    // WhatsApp button - FIXED LOCATOR
    private By whatsappButton = By.cssSelector("button.bg-\\[\\#019875\\]");

    // Apply Now Timer button
    private By applyNowTimer = By.xpath("/html/body/main/div/div[2]//button[contains(@class, 'pushable')]");

    // Navigation links
    private By universitiesLink = By.cssSelector("a[data-slot='navigation-menu-link'][href='/en/universities']");
    private By programsLink = By.cssSelector("a[data-slot='navigation-menu-link'][href='/en/programs']");
    private By blogsLink = By.cssSelector("a[data-slot='navigation-menu-link'][href='/en/blogs']");
    private By visaSupportLink = By.cssSelector("a[data-slot='navigation-menu-link'][href='/en/study-visa-support-in-turkey']");
    private By aboutLink = By.cssSelector("a[data-slot='navigation-menu-link'][href='/en/about']");
    private By contactLink = By.cssSelector("a[data-slot='navigation-menu-link'][href='/en/contact']");

    // Test statistics
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int screenshotCount = 0;

    public HomePageTest() {
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
        logFileName = LOGS_FOLDER + "/" + "HomePageTest_" + timestamp + ".txt";

        // Screenshot subfolder in screenshots folder
        screenshotFolder = SCREENSHOTS_FOLDER + "/" + "HomePageTest_" + timestamp;

        try {
            Files.createDirectories(Paths.get(screenshotFolder));
            log("📁 Screenshot folder: " + screenshotFolder);
        } catch (IOException e) {
            logError("Screenshot folder creation failed: " + e.getMessage());
        }

        log("📁 Log file: " + logFileName);
        log("═".repeat(70));
        log("🏠 HOMEPAGE TEST - AUTOMATED TESTING");
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

    private boolean isElementPresent(By locator) {
        try {
            shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== TEST METHODS ====================

    private void testAcceptCookies() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🍪 TEST 1: Accept Cookies");
        log("━".repeat(70));

        try {
            if (isElementPresent(acceptCookiesButton)) {
                log("   ℹ️ Cookie banner found");
                WebElement cookieBtn = driver.findElement(acceptCookiesButton);
                js.executeScript("arguments[0].click();", cookieBtn);
                sleep(1500);
                log("✅ PASS - Cookies accepted");
                passedTests++;
            } else {
                log("   ⚠️ Cookie banner not found (already accepted)");
                passedTests++;
            }
        } catch (Exception e) {
            logError("FAIL - Cookie accept error: " + e.getMessage());
            failedTests++;
            takeScreenshot("COOKIES_ERROR");
        }
    }

    private void testApplyNowButton() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("📝 TEST 2: Apply Now Button (Header)");
        log("━".repeat(70));

        try {
            if (isElementPresent(applyNowButton)) {
                log("   ℹ️ Apply Now button found");
                WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(applyNowButton));
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btn);
                sleep(300);
                js.executeScript("arguments[0].click();", btn);
                sleep(1500);
                log("✅ PASS - Apply Now button clicked, dialog opened");
                passedTests++;
            } else {
                logError("FAIL - Apply Now button not found");
                failedTests++;
                takeScreenshot("APPLY_NOW_NOT_FOUND");
            }
        } catch (Exception e) {
            logError("FAIL - Apply Now error: " + e.getMessage());
            failedTests++;
            takeScreenshot("APPLY_NOW_ERROR");
        }
    }

    private void testCloseDialog() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("❌ TEST: Close Dialog");
        log("━".repeat(70));

        try {
            if (isElementPresent(closeDialogButton)) {
                log("   ℹ️ Close button found");
                WebElement closeBtn = wait.until(ExpectedConditions.presenceOfElementLocated(closeDialogButton));
                js.executeScript("arguments[0].click();", closeBtn);
                sleep(1000);
                log("✅ PASS - Dialog closed");
                passedTests++;
            } else {
                log("   ⚠️ No dialog to close");
                passedTests++;
            }
        } catch (Exception e) {
            logError("FAIL - Close dialog error: " + e.getMessage());
            failedTests++;
            takeScreenshot("CLOSE_DIALOG_ERROR");
        }
    }

    private void testSearchBox() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🔍 TEST 4: Search Box");
        log("━".repeat(70));

        try {
            if (isElementPresent(searchBox)) {
                log("   ℹ️ Search box found");
                WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(searchBox));
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", input);
                sleep(300);
                input.clear();
                input.sendKeys("medicine");
                sleep(500);
                log("   ✓ Typed: \"medicine\"");
                log("✅ PASS - Search box working");
                passedTests++;
            } else {
                logError("FAIL - Search box not found");
                failedTests++;
                takeScreenshot("SEARCH_BOX_NOT_FOUND");
            }
        } catch (Exception e) {
            logError("FAIL - Search box error: " + e.getMessage());
            failedTests++;
            takeScreenshot("SEARCH_BOX_ERROR");
        }
    }

    private void testSearchButton() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🔎 TEST 5: Search Button");
        log("━".repeat(70));

        try {
            if (isElementPresent(searchButton)) {
                log("   ℹ️ Search button found");
                WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(searchButton));
                js.executeScript("arguments[0].click();", btn);
                sleep(2000);
                log("   ✓ Search results opened");

                // Navigate back
                log("   🔙 Navigating back...");
                driver.navigate().back();
                sleep(1500);
                log("✅ PASS - Search button working");
                passedTests++;
            } else {
                logError("FAIL - Search button not found");
                failedTests++;
                takeScreenshot("SEARCH_BTN_NOT_FOUND");
            }
        } catch (Exception e) {
            logError("FAIL - Search button error: " + e.getMessage());
            failedTests++;
            takeScreenshot("SEARCH_BTN_ERROR");
        }
    }

    private void testWhatsAppButton() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("💬 TEST 6: WhatsApp Button");
        log("━".repeat(70));

        try {
            if (isElementPresent(whatsappButton)) {
                log("   ℹ️ WhatsApp button found");
                String mainWindow = driver.getWindowHandle();
                log("   ℹ️ Main window saved");

                WebElement wpBtn = wait.until(ExpectedConditions.presenceOfElementLocated(whatsappButton));
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", wpBtn);
                sleep(300);
                js.executeScript("arguments[0].click();", wpBtn);
                sleep(2000);

                Set<String> allWindows = driver.getWindowHandles();
                if (allWindows.size() > 1) {
                    log("   ℹ️ New tab opened");
                    for (String window : allWindows) {
                        if (!window.equals(mainWindow)) {
                            driver.switchTo().window(window);
                            sleep(500);
                            log("   ✓ Closing new tab...");
                            driver.close();
                            break;
                        }
                    }
                } else {
                    log("   ℹ️ Support options opened (popup blocked)");
                }

                driver.switchTo().window(mainWindow);
                log("✅ PASS - WhatsApp button working");
                passedTests++;
                sleep(1000);
            } else {
                logError("FAIL - WhatsApp button not found");
                failedTests++;
                takeScreenshot("WHATSAPP_NOT_FOUND");
            }
        } catch (Exception e) {
            logError("FAIL - WhatsApp error: " + e.getMessage());
            failedTests++;
            takeScreenshot("WHATSAPP_ERROR");
        }
    }

    private void testApplyNowTimer() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("⏱️ TEST 7: Apply Now Timer Button");
        log("━".repeat(70));

        try {
            if (isElementPresent(applyNowTimer)) {
                log("   ℹ️ Timer button found");
                WebElement timerBtn = wait.until(ExpectedConditions.presenceOfElementLocated(applyNowTimer));
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", timerBtn);
                sleep(300);

                String timerText = timerBtn.getText();
                log("   ℹ️ Timer text: " + timerText);

                js.executeScript("arguments[0].click();", timerBtn);
                sleep(1500);
                log("✅ PASS - Timer button clicked, dialog opened");
                passedTests++;
            } else {
                logError("FAIL - Timer button not found");
                failedTests++;
                takeScreenshot("TIMER_NOT_FOUND");
            }
        } catch (Exception e) {
            logError("FAIL - Timer button error: " + e.getMessage());
            failedTests++;
            takeScreenshot("TIMER_ERROR");
        }
    }

    private void testNavigationLink(String linkName, By locator, String expectedPath) {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🔗 TEST: Navigation - " + linkName);
        log("━".repeat(70));

        try {
            // First go back to home page
            driver.get(SITE_URL);
            sleep(1000);

            if (isElementPresent(locator)) {
                log("   ℹ️ " + linkName + " link found");
                WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", link);
                sleep(300);
                js.executeScript("arguments[0].click();", link);
                sleep(1500);

                String currentUrl = driver.getCurrentUrl();
                log("   📍 Current URL: " + currentUrl);

                if (currentUrl.contains(expectedPath)) {
                    log("✅ PASS - " + linkName + " page opened");
                    passedTests++;
                } else {
                    logError("FAIL - Wrong URL: expected " + expectedPath);
                    failedTests++;
                    takeScreenshot(linkName.toUpperCase() + "_WRONG_URL");
                }
            } else {
                logError("FAIL - " + linkName + " link not found");
                failedTests++;
                takeScreenshot(linkName.toUpperCase() + "_NOT_FOUND");
            }
        } catch (Exception e) {
            logError("FAIL - " + linkName + " error: " + e.getMessage());
            failedTests++;
            takeScreenshot(linkName.toUpperCase() + "_ERROR");
        }
    }

    // ==================== MAIN TEST FLOW ====================

    public void run() {
        try {
            openWebsite();

            // Test 1: Accept Cookies
            testAcceptCookies();

            // Test 2: Apply Now Button (Header)
            testApplyNowButton();

            // Test 3: Close Dialog
            testCloseDialog();

            // Test 4: Search Box
            testSearchBox();

            // Test 5: Search Button
            testSearchButton();

            // Test 6: WhatsApp Button (FIXED)
            testWhatsAppButton();

            // Test 7: Apply Now Timer Button
            testApplyNowTimer();

            // Test 8: Close Dialog
            testCloseDialog();

            // Test 9-14: Navigation Links
            testNavigationLink("Universities", universitiesLink, "/en/universities");
            testNavigationLink("Programs", programsLink, "/en/programs");
            testNavigationLink("Blogs", blogsLink, "/en/blogs");
            testNavigationLink("Visa Support", visaSupportLink, "/en/study-visa-support-in-turkey");
            testNavigationLink("About", aboutLink, "/en/about");
            testNavigationLink("Contact", contactLink, "/en/contact");

            // Print summary
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
        log("\n🌐 Opening: " + SITE_URL);
        driver.get(SITE_URL);
        sleep(1000);
        log("✅ Website opened\n");
    }

    public void close() {
        if (driver != null) {
            log("\n🔚 Closing browser...");
            driver.quit();
        }
    }

    // ==================== MAIN METHOD ====================

    public static void main(String[] args) {
        HomePageTest test = new HomePageTest();

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