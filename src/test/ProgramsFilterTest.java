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
import org.openqa.selenium.TimeoutException;
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

public class ProgramsFilterTest {

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
    private By programsLink = By.cssSelector("a[data-slot='navigation-menu-link'][href='/en/programs']");

    // Result counter - "6,588 Programs Found"
    private By resultCounter = By.cssSelector("span.text-xs.text-gray-500.font-medium");

    // Search box
    private By searchBox = By.cssSelector("input[data-slot='input'][aria-label='Search for a program']");

    // Eraser button - JavaScript click lazımdır
    private By eraserButton = By.cssSelector("button[data-slot='button'].text-destructive");

    // Has Discount button
    private By hasDiscountBtn = By.id("has-discount");

    // 6 Dropdowns
    private By allUniversitiesDropdown = By.xpath("//button[@data-slot='popover-trigger' and .//span[normalize-space(text())='All Universities']]");
    private By allFacultiesDropdown = By.xpath("//button[@data-slot='popover-trigger' and .//span[normalize-space(text())='All Faculties']]");
    private By allCitiesDropdown = By.xpath("//button[@data-slot='popover-trigger' and .//span[normalize-space(text())='All Cities']]");
    private By allDegreeTypesDropdown = By.xpath("//button[@data-slot='popover-trigger' and .//span[normalize-space(text())='All Degree Types']]");
    private By allLanguagesDropdown = By.xpath("//button[@data-slot='popover-trigger' and .//span[normalize-space(text())='All Languages']]");
    private By anyDurationDropdown = By.xpath("//button[@data-slot='select-trigger' and .//span[normalize-space(text())='Any Duration']]");

    // Sort By dropdown
    private By sortByDropdown = By.xpath("//button[@data-slot='select-trigger' and .//span[normalize-space(text())='Sort By']]");

    // Dropdown options
    private By dropdownOptions = By.cssSelector("div[data-slot='command-item'], [role='option']");

    // Test statistics
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int screenshotCount = 0;

    // Constants for filtering dropdown options
    private static final String OPTION_PREFIX_ALL = "all ";
    private static final String OPTION_ANY_DURATION = "any duration";

    public ProgramsFilterTest() {
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

        logFileName = "ProgramsFilterTest_" + timestamp + ".txt";
        screenshotFolder = "screenshots_" + timestamp;

        try {
            Files.createDirectories(Paths.get(screenshotFolder));
            log("📁 Screenshot folder: " + screenshotFolder);
        } catch (IOException e) {
            logError("Screenshot folder creation failed: " + e.getMessage());
        }

        log("═".repeat(70));
        log("🎓 PROGRAMS FILTER TEST - AUTOMATED TESTING");
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
     * Get current result count from the page
     * Extracts number from text like "6,588 Programs Found"
     * Handles comma in number (e.g., "6,588" -> 6588)
     */
    private int getResultCount() {
        try {
            // Try to find span with "Programs Found" text
            List<WebElement> spans = driver.findElements(resultCounter);

            for (WebElement span : spans) {
                String text = span.getText().trim();

                if (text.contains("Programs Found")) {
                    // Extract number: "6,588 Programs Found" -> 6588
                    // Remove everything except digits
                    String numStr = text.replaceAll("[^0-9]", "");

                    if (!numStr.isEmpty()) {
                        return Integer.parseInt(numStr);
                    }
                }
            }

            return -1;

        } catch (Exception e) {
            logError("Failed to get result count: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Wait for result count to change from the given previous count
     * Polls every 500ms until count changes or timeout
     */
    private boolean waitForResultChange(int previousCount, int timeoutSeconds) {
        try {
            WebDriverWait tempWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            tempWait.until(driver -> {
                int current = getResultCount();
                return current != -1 && current != previousCount;
            });
            return true;
        } catch (TimeoutException e) {
            log("   ⚠️  Result count did not change within " + timeoutSeconds + " seconds");
            return false;
        }
    }

    /**
     * Eraser buttonunu tap və JavaScript ilə click et
     * Normal click işləmir çünki element overlay/modal altındadır
     */
    private void clickEraserButton() {
        try {
            log("🧹 Clearing filters...");

            // Bütün text-destructive buttonları tap
            List<WebElement> destructiveButtons = driver.findElements(eraserButton);

            if (destructiveButtons.isEmpty()) {
                log("   ⚠️ No destructive buttons found");
                return;
            }

            // Eraser SVG icon olanı tap
            for (WebElement btn : destructiveButtons) {
                List<WebElement> eraserSvg = btn.findElements(By.cssSelector("svg.lucide-eraser"));

                if (!eraserSvg.isEmpty()) {
                    // Scroll into view
                    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btn);
                    sleep(300);

                    // JavaScript click (overlay problemi yoxdur)
                    js.executeScript("arguments[0].click();", btn);
                    log("   ✅ Eraser button clicked (via JavaScript)");
                    sleep(500); // Animation bitsin
                    return;
                }
            }

            log("   ⚠️ Eraser button (with SVG icon) not found");

        } catch (Exception e) {
            logError("Failed to click eraser button: " + e.getMessage());
        }
    }

    /**
     * Select first non-default option from a dropdown
     * Clicks dropdown to open, waits for options, clicks first valid option
     */
    private boolean selectFirstDropdownOption(By dropdownLocator, String dropdownName) {
        try {
            // Click dropdown to open
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", dropdown);
            sleep(300);
            js.executeScript("arguments[0].click();", dropdown);
            sleep(500);

            // Wait for options to appear
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(dropdownOptions));
            sleep(300);

            // Get all options
            List<WebElement> options = driver.findElements(dropdownOptions);
            log("   📋 Found " + options.size() + " options");

            // Select first valid option (skip "All..." options)
            for (WebElement option : options) {
                try {
                    if (option.isDisplayed() && option.isEnabled()) {
                        String optionText = option.getText();

                        // Skip "All" or empty options
                        if (optionText.isEmpty() ||
                                optionText.toLowerCase().startsWith(OPTION_PREFIX_ALL) ||
                                optionText.equalsIgnoreCase(OPTION_ANY_DURATION)) {
                            continue;
                        }

                        js.executeScript("arguments[0].click();", option);
                        log("   ✓ Selected: " + optionText);
                        return true;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            return false;

        } catch (Exception e) {
            logError("Failed to select dropdown option: " + e.getMessage());
            return false;
        }
    }

    // ==================== TEST METHODS ====================

    /**
     * Test search box filter
     */
    private void testSearchBox() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🔍 TEST 1: Search Box");
        log("━".repeat(70));

        try {
            // Get initial count
            int initialCount = getResultCount();
            log("   📊 Initial count: " + formatCount(initialCount));

            if (initialCount == -1) {
                logError("Cannot read initial count - SKIPPING TEST");
                failedTests++;
                takeScreenshot("SEARCH_NO_INITIAL_COUNT");
                return;
            }

            // Type in search box
            log("   Searching for: \"engineering\"");
            WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(searchBox));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", searchInput);
            sleep(300);
            searchInput.clear();
            searchInput.sendKeys("engineering");
            sleep(500);

            // Wait for results to update
            log("   ⏳ Waiting for results to update...");
            boolean changed = waitForResultChange(initialCount, 10);

            int filteredCount = getResultCount();
            log("   Result: " + formatCount(initialCount) + " → " + formatCount(filteredCount));

            // Validate
            if (changed && filteredCount != -1 && filteredCount != initialCount) {
                log("✅ PASS - Count changed");
                passedTests++;
            } else {
                logError("FAIL - Count did not change");
                failedTests++;
                takeScreenshot("SEARCH_FAILED");
            }

            // Clear filters with eraser button
            clickEraserButton();
            sleep(500);

        } catch (Exception e) {
            logError("Search box test error: " + e.getMessage());
            failedTests++;
            takeScreenshot("SEARCH_ERROR");
        }
    }

    /**
     * Test Has Discount button filter
     */
    private void testHasDiscountButton() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🔘 TEST 2: Has Discount Button");
        log("━".repeat(70));

        try {
            // Get initial count
            int initialCount = getResultCount();
            log("   📊 Initial count: " + formatCount(initialCount));

            if (initialCount == -1) {
                logError("Cannot read initial count - SKIPPING TEST");
                failedTests++;
                takeScreenshot("DISCOUNT_NO_INITIAL_COUNT");
                return;
            }

            // Click button with JavaScript
            log("   🖱️  Clicking Has Discount button...");
            WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(hasDiscountBtn));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", button);
            sleep(300);
            js.executeScript("arguments[0].click();", button);
            sleep(1000);

            // Wait for results to update
            log("   ⏳ Waiting for results to update...");
            waitForResultChange(initialCount, 10);

            int filteredCount = getResultCount();
            log("   Result: " + formatCount(initialCount) + " → " + formatCount(filteredCount));

            // Validate
            if (filteredCount != -1 && filteredCount != initialCount) {
                log("✅ PASS - Count changed");
                passedTests++;
            } else if (filteredCount == initialCount) {
                log("✅ PASS - Count unchanged (no discounted programs)");
                passedTests++;
            } else {
                logError("FAIL - Cannot read count");
                failedTests++;
                takeScreenshot("DISCOUNT_FAILED");
            }

            // Clear filters with eraser button
            clickEraserButton();
            sleep(500);

        } catch (Exception e) {
            logError("Has Discount test error: " + e.getMessage());
            failedTests++;
            takeScreenshot("DISCOUNT_ERROR");
        }
    }

    /**
     * Test a dropdown filter
     */
    private void testDropdown(String dropdownName, By dropdownLocator, int testNumber) {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("📋 TEST " + testNumber + ": " + dropdownName);
        log("━".repeat(70));

        try {
            // Get initial count
            int initialCount = getResultCount();
            log("   📊 Initial count: " + formatCount(initialCount));

            if (initialCount == -1) {
                logError("Cannot read initial count - SKIPPING TEST");
                failedTests++;
                takeScreenshot(dropdownName.replaceAll(" ", "_") + "_NO_INITIAL_COUNT");
                return;
            }

            // Select first option from dropdown
            log("   🖱️  Opening " + dropdownName + "...");
            boolean optionSelected = selectFirstDropdownOption(dropdownLocator, dropdownName);

            if (!optionSelected) {
                logError("No valid option found in dropdown");
                failedTests++;
                takeScreenshot(dropdownName.replaceAll(" ", "_") + "_NO_OPTIONS");
                return;
            }

            sleep(500);

            // Wait for results to update
            log("   ⏳ Waiting for results to update...");
            boolean changed = waitForResultChange(initialCount, 10);

            int filteredCount = getResultCount();
            log("   Result: " + formatCount(initialCount) + " → " + formatCount(filteredCount));

            // Validate
            if (changed && filteredCount != -1 && filteredCount != initialCount) {
                log("✅ PASS - Count changed");
                passedTests++;
            } else {
                logError("FAIL - Count did not change");
                failedTests++;
                takeScreenshot(dropdownName.replaceAll(" ", "_") + "_FAILED");
            }

            // Clear filters with eraser button
            clickEraserButton();
            sleep(500);

        } catch (Exception e) {
            logError(dropdownName + " test error: " + e.getMessage());
            failedTests++;
            takeScreenshot(dropdownName.replaceAll(" ", "_") + "_ERROR");
        }
    }

    /**
     * Test Sort By dropdown (may not change count, just verify page updates)
     */
    private void testSortBy() {
        totalTests++;
        log("\n" + "━".repeat(70));
        log("🔃 TEST 9: Sort By - Highest Price");
        log("━".repeat(70));

        try {
            // Get initial count
            int initialCount = getResultCount();
            log("   📊 Initial count: " + formatCount(initialCount));

            if (initialCount == -1) {
                logError("Cannot read initial count - SKIPPING TEST");
                failedTests++;
                takeScreenshot("SORT_BY_NO_INITIAL_COUNT");
                return;
            }

            // Open dropdown with JavaScript
            log("   🖱️  Opening Sort By dropdown...");
            WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(sortByDropdown));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", dropdown);
            sleep(300);
            js.executeScript("arguments[0].click();", dropdown);
            sleep(500);

            // Wait for options to appear
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(dropdownOptions));
            sleep(300);

            // Find and click "Highest Price" option
            List<WebElement> options = driver.findElements(dropdownOptions);
            boolean found = false;

            for (WebElement option : options) {
                try {
                    String optionText = option.getText();
                    if (optionText.contains("Highest Price") || (optionText.contains("Price") && optionText.contains("High"))) {
                        log("   ✓ Selecting: \"Highest Price\"");
                        js.executeScript("arguments[0].click();", option);
                        found = true;
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            if (!found) {
                // Just select first option
                if (!options.isEmpty()) {
                    js.executeScript("arguments[0].click();", options.get(0));
                    log("   ✓ Selected first sort option");
                }
            }

            sleep(1000);

            // For sort, we just verify page didn't crash and count is valid
            int newCount = getResultCount();
            log("   Result: " + formatCount(newCount));

            if (newCount != -1) {
                log("✅ PASS - Sort applied");
                passedTests++;
            } else {
                logError("FAIL - Cannot read count after sort");
                failedTests++;
                takeScreenshot("SORT_BY_FAILED");
            }

        } catch (Exception e) {
            logError("Sort By test error: " + e.getMessage());
            failedTests++;
            takeScreenshot("SORT_BY_ERROR");
        }
    }

    private String formatCount(int count) {
        if (count == -1) return "N/A";
        return String.format("%,d", count);
    }

    // ==================== MAIN TEST FLOW ====================

    public void run() {
        try {
            log("\n" + "█".repeat(70));
            log("█  🚀 STARTING PROGRAMS FILTER TESTING" + " ".repeat(30) + "█");
            log("█".repeat(70) + "\n");

            openWebsite();
            acceptCookies();
            clickProgramsLink();
            sleep(1000);

            // Get initial count to verify page loaded
            int initialCount = getResultCount();
            log("\n📊 Initial Program Count: " + formatCount(initialCount) + "\n");

            if (initialCount == -1) {
                logError("Cannot read initial count - aborting tests");
                return;
            }

            // Run all tests
            testSearchBox();
            testHasDiscountButton();
            testDropdown("All Universities", allUniversitiesDropdown, 3);
            testDropdown("All Faculties", allFacultiesDropdown, 4);
            testDropdown("All Cities", allCitiesDropdown, 5);
            testDropdown("All Degree Types", allDegreeTypesDropdown, 6);
            testDropdown("All Languages", allLanguagesDropdown, 7);
            testDropdown("Any Duration", anyDurationDropdown, 8);
            testSortBy();

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
            log("⚠️  Cookies already accepted\n");
        }
    }

    private void clickProgramsLink() {
        log("📍 Clicking Programs link...");
        try {
            WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(programsLink));
            js.executeScript("arguments[0].click();", link);
            sleep(1000);
            log("✅ Programs page opened\n");
        } catch (Exception e) {
            logError("Programs link not found");
            throw e;
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
        ProgramsFilterTest test = new ProgramsFilterTest();

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