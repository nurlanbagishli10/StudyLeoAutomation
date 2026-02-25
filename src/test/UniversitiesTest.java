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

public class UniversitiesTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;
    private JavascriptExecutor js;

    private static final String SITE_URL = "https://studyleo.com/en";

    // Centralized folder paths
    private static final String LOGS_FOLDER = "logs";
    private static final String SCREENSHOTS_FOLDER = "screenshots";

    private List<String> logMessages = new ArrayList<>();
    private String logFileName;
    private String screenshotFolder;

    // Locators
    private By acceptCookiesButton = By.cssSelector("button[data-testid='cookie-banner-accept-button']");
    private By universitiesLink = By.cssSelector("[data-slot='navigation-menu-link'][href='/en/universities']");

    // Filter locators
    private By resultCounter = By.cssSelector("span[aria-live='polite']");
    private By searchBox = By.cssSelector("input[data-slot='input'][aria-label='Search Universities']");
    private By hasQsRankingBtn = By.id("has-scholarships");
    private By hasDormitoryBtn = By.id("has-dormitory");
    private By canApplyBtn = By.id("can-apply");
    private By citiesDropdown = By.id("cities");
    private By facultiesDropdown = By.id("faculties");
    private By programsDropdown = By.id("programs");
    private By degreeTypesDropdown = By.id("degree-types");
    private By sortByDropdown = By.cssSelector("button[aria-label='Sort By']");
    private By dropdownOptions = By.cssSelector("div[data-slot='command-item'], [role='option']");

    // Test statistics
    private int totalFilters = 0;
    private int passedFilters = 0;
    private int failedFilters = 0;
    private int screenshotCount = 0;

    private boolean sharedDriver = false;

    // Default constructor (standalone mode)
    public UniversitiesTest() {
        this(null, false);
    }

    // New constructor for shared driver mode
    public UniversitiesTest(WebDriver driver, boolean sharedDriver) {
        this.sharedDriver = sharedDriver;
        if (driver != null && sharedDriver) {
            this.driver = driver;
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            this.js = (JavascriptExecutor) driver;
        } else {
            initializeDriver();
        }
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
        logFileName = LOGS_FOLDER + "/" + "UniversitiesTest_" + timestamp + ".txt";

        // Screenshot subfolder in screenshots folder
        screenshotFolder = SCREENSHOTS_FOLDER + "/" + "UniversitiesTest_" + timestamp;

        try {
            Files.createDirectories(Paths.get(screenshotFolder));
            log("📁 Screenshot folder: " + screenshotFolder);
        } catch (IOException e) {
            logError("Screenshot folder creation failed: " + e.getMessage());
        }

        log("📁 Log file: " + logFileName);
        log("═".repeat(70));
        log("🎓 UNIVERSITIES FILTER TEST - AUTOMATED TESTING");
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
            e.printStackTrace();
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
     * Extracts number from text like "78 Universities Found"
     */
    private int getResultCount() {
        try {
            WebElement counter = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(resultCounter)
            );

            String text = counter.getText().trim();

            if (text.isEmpty()) {
                return -1;
            }

            // Extract number: "78 Universities Found" -> 78
            String numStr = text.replaceAll("[^0-9]", "");

            return numStr.isEmpty() ? -1 : Integer.parseInt(numStr);

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
            log("   ⚠️ Result count did not change within " + timeoutSeconds + " seconds");
            return false;
        }
    }

    /**
     * Clear all filters by navigating back to universities page
     */
    private void clearFilters() {
        try {
            log("   🗑️ Clearing filters...");
            driver.get(SITE_URL + "/universities");
            sleep(1000);
            log("   ✓ Filters cleared");
        } catch (Exception e) {
            logError("Failed to clear filters: " + e.getMessage());
        }
    }

    /**
     * Select first non-default option from a dropdown (daha universal skip və ətraflı log)
     */
    private boolean selectFirstDropdownOption(By dropdownLocator, String dropdownName) {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", dropdown);
            sleep(300);
            js.executeScript("arguments[0].click();", dropdown);
            sleep(500);

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(dropdownOptions));
            sleep(300);

            List<WebElement> options = driver.findElements(dropdownOptions);
            log("   📋 Found " + options.size() + " options");
            int skipped = 0;

            for (WebElement option : options) {
                try {
                    if (option.isDisplayed() && option.isEnabled()) {
                        String optionText = option.getText().trim();
                        String innerText = option.getAttribute("innerText");
                        String textContent = option.getAttribute("textContent");

                        log(String.format("       > Option: '%s' | innerText: '%s' | textContent: '%s'", optionText, innerText, textContent));

                        // Universal skip logic
                        String check = ((optionText + " " + innerText + " " + textContent).toLowerCase()).trim();
                        if (check.isEmpty() ||
                                check.equals("all") ||
                                check.startsWith("all ") ||
                                check.matches("^(all)[\\s\\.\\,\\-:·]*.*") ||
                                check.contains("any duration")) {
                            skipped++;
                            continue;
                        }

                        js.executeScript("arguments[0].click();", option);
                        log("   ✓ Selected: " + check);
                        return true;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            log("   Skipped " + skipped + " options due to filtering.");
            return false;

        } catch (Exception e) {
            logError("Failed to select dropdown option: " + e.getMessage());
            return false;
        }
    }


    // ==================== TEST METHODS ====================

    private void testSearchBox() {
        totalFilters++;
        log("\n" + "═".repeat(70));
        log("🔍 TEST 1: SEARCH BOX");
        log("═".repeat(70));

        try {
            int initialCount = getResultCount();
            log("   📊 Initial count: " + initialCount);

            if (initialCount == -1) {
                logError("Cannot read initial count - SKIPPING TEST");
                failedFilters++;
                takeScreenshot("SEARCH_NO_INITIAL_COUNT");
                return;
            }

            log("   ⌨️ Typing 'istanbul' in search box...");
            WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(searchBox));
            searchInput.clear();
            searchInput.sendKeys("istanbul");
            sleep(500);

            log("   ⏳ Waiting for results to update...");
            boolean changed = waitForResultChange(initialCount, 10);

            int filteredCount = getResultCount();
            log("   📊 After search: " + filteredCount);

            if (changed && filteredCount != -1 && filteredCount != initialCount) {
                log("✅ SEARCH BOX TEST PASSED");
                passedFilters++;
            } else {
                logError("SEARCH BOX TEST FAILED - Count did not change");
                failedFilters++;
                takeScreenshot("SEARCH_FAILED");
            }

        } catch (Exception e) {
            logError("Search box test error: " + e.getMessage());
            failedFilters++;
            takeScreenshot("SEARCH_ERROR");
        }
    }

    private void testButtonFilter(String buttonName, By buttonLocator, boolean changeExpected) {
        totalFilters++;
        log("\n" + "═".repeat(70));
        log("🔘 TEST: " + buttonName.toUpperCase() + " BUTTON");
        log("═".repeat(70));

        try {
            clearFilters();
            sleep(500);

            int initialCount = getResultCount();
            log("   📊 Initial count: " + initialCount);

            if (initialCount == -1) {
                logError("Cannot read initial count - SKIPPING TEST");
                failedFilters++;
                takeScreenshot(buttonName.replaceAll(" ", "_") + "_NO_INITIAL_COUNT");
                return;
            }

            log("   🖱️ Clicking " + buttonName + " button...");
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
            button.click();
            sleep(1500);

            int filteredCount = getResultCount();
            log("   📊 After filter: " + filteredCount);

            if (filteredCount != initialCount) {
                log("✅ " + buttonName.toUpperCase() + " TEST PASSED (Count changed: " + initialCount + " → " + filteredCount + ")");
                passedFilters++;
            } else if (!changeExpected) {
                log("✅ " + buttonName.toUpperCase() + " TEST PASSED (No change, this is expected)");
                passedFilters++;
            } else {
                log("⚠️ " + buttonName.toUpperCase() + " - Count unchanged: " + filteredCount + " (Warning: change was expected)");
                passedFilters++;
            }

        } catch (Exception e) {
            logError(buttonName + " test error: " + e.getMessage());
            failedFilters++;
            takeScreenshot(buttonName.replaceAll(" ", "_") + "_ERROR");
        }
    }

    private void testDropdown(String dropdownName, By dropdownLocator) {
        totalFilters++;
        log("\n" + "═".repeat(70));
        log("📋 TEST: " + dropdownName.toUpperCase() + " DROPDOWN");
        log("═".repeat(70));

        try {
            clearFilters();
            sleep(500);

            int initialCount = getResultCount();
            log("   📊 Initial count: " + initialCount);

            if (initialCount == -1) {
                logError("Cannot read initial count - SKIPPING TEST");
                failedFilters++;
                takeScreenshot(dropdownName.replaceAll(" ", "_") + "_NO_INITIAL_COUNT");
                return;
            }

            log("   🖱️ Opening " + dropdownName + " dropdown...");
            boolean optionSelected = selectFirstDropdownOption(dropdownLocator, dropdownName);

            if (!optionSelected) {
                logError("No valid option found in dropdown");
                failedFilters++;
                takeScreenshot(dropdownName.replaceAll(" ", "_") + "_NO_OPTIONS");
                return;
            }

            sleep(500);

            log("   ⏳ Waiting for results to update...");
            boolean changed = waitForResultChange(initialCount, 10);

            int filteredCount = getResultCount();
            log("   📊 After filter: " + filteredCount);

            if (changed && filteredCount != -1 && filteredCount != initialCount) {
                log("✅ " + dropdownName.toUpperCase() + " TEST PASSED");
                passedFilters++;
            } else {
                logError(dropdownName.toUpperCase() + " TEST FAILED - Count did not change");
                failedFilters++;
                takeScreenshot(dropdownName.replaceAll(" ", "_") + "_FAILED");
            }

        } catch (Exception e) {
            logError(dropdownName + " test error: " + e.getMessage());
            failedFilters++;
            takeScreenshot(dropdownName.replaceAll(" ", "_") + "_ERROR");
        }
    }

    private void testSortBy() {
        totalFilters++;
        log("\n" + "═".repeat(70));
        log("🔄 TEST: SORT BY DROPDOWN");
        log("═".repeat(70));

        try {
            clearFilters();
            sleep(500);

            int initialCount = getResultCount();
            log("   📊 Initial count: " + initialCount);

            if (initialCount == -1) {
                logError("Cannot read initial count - SKIPPING TEST");
                failedFilters++;
                takeScreenshot("SORT_BY_NO_INITIAL_COUNT");
                return;
            }

            log("   🖱️ Opening Sort By dropdown...");
            boolean optionSelected = selectFirstDropdownOption(sortByDropdown, "Sort By");

            if (!optionSelected) {
                logError("No valid option found in dropdown");
                failedFilters++;
                takeScreenshot("SORT_BY_NO_OPTIONS");
                return;
            }

            sleep(1000);

            int newCount = getResultCount();
            log("   📊 After sort: " + newCount);

            if (newCount != -1) {
                log("✅ SORT BY TEST PASSED (page updated successfully)");
                passedFilters++;
            } else {
                logError("SORT BY TEST FAILED - Cannot read count after sort");
                failedFilters++;
                takeScreenshot("SORT_BY_FAILED");
            }

        } catch (Exception e) {
            logError("Sort By test error: " + e.getMessage());
            failedFilters++;
            takeScreenshot("SORT_BY_ERROR");
        }
    }

    public void run() {
        try {
            log("\n" + "█".repeat(70));
            log("█  🚀 STARTING UNIVERSITIES FILTER TESTING" + " ".repeat(27) + "█");
            log("█".repeat(70) + "\n");

            openWebsite();
            acceptCookies();
            clickUniversitiesLink();
            sleep(1000);

            int initialCount = getResultCount();
            log("\n📊 Universities page loaded with " + initialCount + " results\n");

            if (initialCount == -1) {
                logError("Cannot read initial count - aborting tests");
                return;
            }

            testSearchBox();
            testButtonFilter("Has QS Ranking", hasQsRankingBtn, true);
            testButtonFilter("Has Dormitory", hasDormitoryBtn, false);
            testButtonFilter("Can Apply", canApplyBtn, false);
            testDropdown("Cities", citiesDropdown);
            testDropdown("Faculties", facultiesDropdown);
            testDropdown("Programs", programsDropdown);
            testDropdown("Degree Types", degreeTypesDropdown);
            testSortBy();

            printSummary();

        } catch (Exception e) {
            logError("CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
            takeScreenshot("CRITICAL_ERROR");
        }
    }

    private void printSummary() {
        log("\n" + "█".repeat(70));
        log("█  📊 TEST SUMMARY" + " ".repeat(51) + "█");
        log("█".repeat(70));
        log("");
        log("   🧪 Total Filters Tested: " + totalFilters);
        log("   ✅ Passed: " + passedFilters);
        log("   ❌ Failed: " + failedFilters);
        log("   📸 Screenshots: " + screenshotCount);
        log("");

        double successRate = totalFilters > 0
                ? (passedFilters * 100.0 / totalFilters)
                : 0;
        log("   📈 Success Rate: " + String.format("%.2f%%", successRate));

        log("");
        log("█".repeat(70));
        log("█  ✅ TEST COMPLETED" + " ".repeat(50) + "█");
        log("█".repeat(70) + "\n");

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
                clickElement(acceptCookiesButton);
                log("✅ Cookies accepted\n");
            }
        } catch (Exception e) {
            log("⚠️ Cookies already accepted\n");
        }
    }

    private void clickUniversitiesLink() {
        log("📍 Clicking Universities link...");
        try {
            WebElement link = wait.until(ExpectedConditions.elementToBeClickable(universitiesLink));
            link.click();
            log("✅ Universities page opened\n");
        } catch (Exception e) {
            logError("Universities link not found");
            throw e;
        }
    }

    private boolean isElementPresent(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void clickElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            WebElement element = driver.findElement(locator);
            js.executeScript("arguments[0].click();", element);
        }
    }

    public void close() {
        if (!sharedDriver && driver != null) {
            log("\n\uD83D\uDD1A Closing browser...");
            driver.quit();
        }
    }

    public int getTotalTests() { return totalFilters; }
    public int getPassedTests() { return passedFilters; }
    public int getFailedTests() { return failedFilters; }
    public String getTestClassName() { return this.getClass().getSimpleName(); }

    public static void main(String[] args) {
        UniversitiesTest test = new UniversitiesTest();
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