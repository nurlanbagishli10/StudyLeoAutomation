import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProgramsFilterTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;
    private JavascriptExecutor js;
    private String screenshotFolder;

    private static final String SITE_URL = "https://studyleo.com/en";

    // ==================== LOCATORS ====================

    // Cookie
    private final By cookieAccept = By.cssSelector("button[data-testid='cookie-banner-accept-button']");

    // Navigation
    private final By programsLink = By.linkText("Programs");

    // Counter - təkmilləşdirilmiş
    private final By programsCounter = By.xpath("//span[contains(text(), 'Programs Found')]");

    // Clear button - text ilə
    private final By clearButton = By.xpath("//button[contains(text(), 'Clear') or contains(@aria-label, 'Clear')]");

    // Filter buttons - daha robust
    private final By universityBtn = By.xpath("//button[contains(@aria-label, 'Universities') or contains(., 'Universities')]");
    private final By facultiesBtn = By.xpath("//button[contains(@aria-label, 'Faculties') or contains(., 'Faculties')]");
    private final By citiesBtn = By.xpath("//button[contains(@aria-label, 'Cities') or contains(., 'Cities')]");
    private final By degreeBtn = By.xpath("//button[contains(@aria-label, 'Degree') or contains(., 'Degree')]");
    private final By languageBtn = By.xpath("//button[contains(@aria-label, 'Language') or contains(., 'Language')]");

    // Command items - daha geniş
    private final By commandItems = By.cssSelector("div[data-slot='command-item'], [role='option']");

    // Test stats
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;

    public ProgramsFilterTest() {
        initDriver();
        createScreenshotFolder();
    }

    private void initDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        js = (JavascriptExecutor) driver;

        // ✅ Implicit wait SİLİNDİ - yalnız explicit wait
    }

    private void createScreenshotFolder() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        screenshotFolder = "screenshots_" + timestamp;
        new File(screenshotFolder).mkdirs();
        System.out.println("📁 Screenshot folder:  " + screenshotFolder);
    }

    // ==================== MAIN TEST ====================

    public void run() {
        try {
            printHeader();

            // Navigate
            log("🌐 Opening:  " + SITE_URL);
            driver.get(SITE_URL);
            waitForPageLoad();
            log("✅ Website loaded\n");

            // Cookie
            if (isPresent(cookieAccept, 3)) {
                click(cookieAccept);
                log("✅ Cookies accepted\n");
                sleep(500);
            }

            // Go to programs
            log("🔗 Navigating to Programs...");
            click(programsLink);
            waitForPageLoad();

            // Wait for page to load
            waitForCounterToLoad();
            log("✅ Programs page loaded\n");

            // Run tests
            testFilter("University", universityBtn);
            testFilter("Faculties", facultiesBtn);
            testFilter("Cities", citiesBtn);
            testFilter("Degree Types", degreeBtn);
            testFilter("Language", languageBtn);

            printSummary();

        } catch (Exception e) {
            System.err.println("❌ CRITICAL ERROR: " + e.getMessage());
            takeScreenshot("CRITICAL_ERROR");
            e.printStackTrace();
        }
    }

    // ==================== TEST FILTER - TƏKMİLLƏŞDİRİLMİŞ ====================

    private void testFilter(String filterName, By filterButton) {
        totalTests++;
        printTestHeader(filterName);

        try {
            // 1.Get initial count
            int initialCount = getCount();
            log("   📊 Initial:  " + formatCount(initialCount));

            if (initialCount == -1) {
                log("   ❌ Cannot read initial count - SKIPPING TEST");
                takeScreenshot("NO_INITIAL_COUNT_" + filterName);
                failedTests++;
                return;
            }

            // 2.Open filter
            log("   🔍 Opening " + filterName + " filter...");
            click(filterButton);

            // ✅ YENİ:  Dropdown açılmasını yoxla
            if (! waitForDropdownToOpen()) {
                log("   ❌ Dropdown did not open!");
                takeScreenshot("DROPDOWN_NOT_OPENED_" + filterName);
                failedTests++;
                return;
            }
            log("   ✓ Dropdown opened");

            // 3.Select first visible option
            log("   🔍 Selecting option...");
            WebElement selectedOption = selectFirstVisibleOption();

            if (selectedOption == null) {
                log("   ❌ No options found!");
                takeScreenshot("NO_OPTIONS_" + filterName);
                failedTests++;
                closeDropdownIfOpen(filterButton);
                return;
            }

            // 4.Wait for filter to apply
            log("   ⏳ Waiting for filter to apply...");
            waitForCounterUpdate(initialCount);

            int filteredCount = getCount();
            log("   📊 After filter: " + formatCount(filteredCount));

            // 5.Validate filter
            boolean filterWorks = validateFilter(initialCount, filteredCount);

            if (filterWorks) {
                log("   ✅ Filter works correctly!");
            } else {
                log("   ⚠️ Filter validation failed");
                takeScreenshot("FILTER_FAILED_" + filterName);
            }

            // 6.Clear filters
            log("   🗑️ Clearing filters...");

            if (! isPresent(clearButton, 2)) {
                log("   ⚠️ Clear button not visible!");
                takeScreenshot("CLEAR_BTN_MISSING_" + filterName);
                failedTests++;
                return;
            }

            click(clearButton);

            // ✅ YENİ:  Clear-dan sonra counter update-ini gözlə
            waitForCounterUpdate(filteredCount);

            int clearedCount = getCount();
            log("   📊 After clear: " + formatCount(clearedCount));

            // 7.Validate clear
            boolean clearWorks = validateClear(initialCount, clearedCount, filteredCount);

            if (clearWorks) {
                log("   ✅ Clear works correctly!");
            } else {
                log("   ⚠️ Clear validation failed");
                takeScreenshot("CLEAR_FAILED_" + filterName);
            }

            // 8.Final result
            if (filterWorks && clearWorks) {
                log("✅ " + filterName + " TEST PASSED");
                passedTests++;
            } else {
                log("❌ " + filterName + " TEST FAILED");
                failedTests++;
            }

        } catch (Exception e) {
            System.err.println("❌ " + filterName + " error: " + e.getMessage());
            e.printStackTrace();
            takeScreenshot("ERROR_" + filterName);
            failedTests++;
        }

        sleep(500);
    }

    // ==================== HELPER METHODS - TƏKMİLLƏŞDİRİLMİŞ ====================

    private void waitForPageLoad() {
        try {
            wait.until(driver -> {
                String readyState = js.executeScript("return document.readyState").toString();
                return readyState.equals("complete");
            });
            sleep(500);
        } catch (Exception e) {
            log("   ⚠️ Page load warning: " + e.getMessage());
        }
    }

    private void waitForCounterToLoad() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(programsCounter));

            // Counter mətninin yüklənməsini gözlə
            wait.until(driver -> {
                try {
                    WebElement counter = driver.findElement(programsCounter);
                    String text = counter.getText().trim();
                    return ! text.isEmpty() && text.contains("Programs Found");
                } catch (Exception e) {
                    return false;
                }
            });

            log("   ✓ Counter loaded");
        } catch (Exception e) {
            log("   ⚠️ Counter load warning: " + e.getMessage());
        }
    }

    // ✅ YENİ METOD: Dropdown açılmasını yoxla
    private boolean waitForDropdownToOpen() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(commandItems));
            sleep(300); // Animasiya üçün
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ YENİ METOD: Counter update-ini gözlə
    private void waitForCounterUpdate(int previousCount) {
        try {
            shortWait.until(driver -> {
                int current = getCount();
                return current != -1 && current != previousCount;
            });
        } catch (TimeoutException e) {
            log("   ⚠️ Counter did not update in time");
        }
    }

    // ✅ TƏKMİLLƏŞDİRİLMİŞ:  getCount metodu
    private int getCount() {
        try {
            WebElement counter = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(programsCounter)
            );

            // Text-in mövcud olmasını gözlə
            wait.until(driver -> {
                String text = counter.getText().trim();
                return ! text.isEmpty() && text.contains("Programs Found");
            });

            String text = counter.getText().trim();

            if (text.isEmpty()) {
                return -1;
            }

            // Extract number:  "6,488 Programs Found" -> 6488
            String numStr = text.replaceAll("[^0-9]", "");

            return numStr.isEmpty() ? -1 : Integer.parseInt(numStr);

        } catch (StaleElementReferenceException e) {
            // Bir dəfə retry
            try {
                WebElement counter = driver.findElement(programsCounter);
                String text = counter.getText().trim();
                String numStr = text.replaceAll("[^0-9]", "");
                return numStr.isEmpty() ? -1 : Integer.parseInt(numStr);
            } catch (Exception ex) {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    // ✅ TƏKMİLLƏŞDİRİLMİŞ: selectFirstVisibleOption
    private WebElement selectFirstVisibleOption() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(commandItems));
            sleep(300);

            List<WebElement> options = driver.findElements(commandItems);
            log("   📋 Found " + options.size() + " total options");

            for (WebElement opt : options) {
                try {
                    // Scroll to view
                    scrollToElement(opt);
                    sleep(200);

                    // Yoxla
                    if (opt.isDisplayed() && opt.isEnabled()) {
                        String optionText = getOptionText(opt);

                        // Click
                        try {
                            opt.click();
                        } catch (ElementClickInterceptedException e) {
                            js.executeScript("arguments[0].click();", opt);
                        }

                        log("   ✓ Selected:  " + optionText);
                        return opt;
                    }
                } catch (StaleElementReferenceException e) {
                    continue;
                }
            }

            return null;

        } catch (Exception e) {
            log("   ❌ Option selection failed: " + e.getMessage());
            return null;
        }
    }

    // ✅ YENİ METOD: Option text oxuma
    private String getOptionText(WebElement element) {
        try {
            String text = element.getText();
            if (! text.isEmpty()) return text;

            text = element.getAttribute("aria-label");
            if (text != null && !text.isEmpty()) return text;

            text = element.getAttribute("data-value");
            if (text != null && !text.isEmpty()) return text;

            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    // ✅ YENİ METOD:  Filter validation
    private boolean validateFilter(int initial, int filtered) {
        if (filtered == -1) return false;
        if (filtered == initial) return false;
        if (filtered <= 0) return false;
        if (filtered > initial) return false; // Filter azaltmalıdır
        return true;
    }

    // ✅ YENİ METOD: Clear validation
    private boolean validateClear(int initial, int cleared, int filtered) {
        if (cleared == -1) return false;
        if (cleared <= 0) return false;
        // Clear edəndən sonra count artmalı və ya initial-a qayıtmalıdır
        return cleared >= filtered;
    }

    // ✅ YENİ METOD:  Dropdown bağlama
    private void closeDropdownIfOpen(By filterButton) {
        try {
            if (isPresent(commandItems, 1)) {
                click(filterButton);
                sleep(300);
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    // ✅ TƏKMİLLƏŞDİRİLMİŞ: Click metodu
    private void click(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            scrollToElement(element);
            sleep(200);
            element.click();
        } catch (ElementClickInterceptedException e) {
            WebElement element = driver.findElement(locator);
            js.executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            WebElement element = driver.findElement(locator);
            js.executeScript("arguments[0].click();", element);
        }
    }

    // ✅ TƏKMİLLƏŞDİRİLMİŞ:  Scroll metodu
    private void scrollToElement(WebElement element) {
        try {
            if (! element.isDisplayed()) {
                js.executeScript(
                        "arguments[0].scrollIntoView({block: 'center', behavior: 'instant'});",
                        element
                );
                sleep(200);
            }
        } catch (Exception e) {
            try {
                js.executeScript("arguments[0].scrollIntoView(true);", element);
                sleep(200);
            } catch (Exception ex) {
                // Ignore
            }
        }
    }

    private boolean isPresent(By locator, int seconds) {
        try {
            WebDriverWait tempWait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
            tempWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String formatCount(int count) {
        if (count == -1) return "N/A";
        return String.format("%,d", count);
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void takeScreenshot(String name) {
        try {
            String timestamp = new SimpleDateFormat("HHmmss").format(new Date());
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(screenshotFolder + "/" + name + "_" + timestamp + ".png");
            FileUtils.copyFile(screenshot, dest);
            System.out.println("📸 " + dest.getName());
        } catch (Exception e) {
            System.err.println("Screenshot failed: " + e.getMessage());
        }
    }

    // ==================== PRINT METHODS ====================

    private void printTestHeader(String filterName) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🧪 TEST: " + filterName.toUpperCase());
        System.out.println("=".repeat(70));
    }

    private void printHeader() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🧪 PROGRAMS FILTER TEST");
        System.out.println("📅 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("=".repeat(70));
        System.out.println();

        System.out.println("█".repeat(70));
        System.out.println("█  🧪 PROGRAMS FILTER TEST - 5 SCENARIOS                          █");
        System.out.println("█".repeat(70));
        System.out.println();

        System.out.println("📋 TEST SCENARIOS:");
        System.out.println("  1️⃣ University Filter");
        System.out.println("  2️⃣ Faculties Filter");
        System.out.println("  3️⃣ Cities Filter");
        System.out.println("  4️⃣ Degree Types Filter");
        System.out.println("  5️⃣ Language Filter");
        System.out.println();
        System.out.println("─".repeat(70));
        System.out.println();
    }

    private void printSummary() {
        System.out.println("\n\n" + "█".repeat(70));
        System.out.println("█  📊 SUMMARY                                                     █");
        System.out.println("█".repeat(70));
        System.out.println();
        System.out.println("   🧪 Total:  " + totalTests);
        System.out.println("   ✅ Passed: " + passedTests);
        System.out.println("   ❌ Failed: " + failedTests);
        System.out.println();

        double rate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0;
        System.out.println("   📈 Success Rate: " + String.format("%.2f", rate) + "%");
        System.out.println();
        System.out.println("█".repeat(70));
        System.out.println();
    }

    public void close() {
        if (driver != null) {
            log("🔚 Closing browser...");
            driver.quit();
        }
    }

    // ==================== MAIN ====================

    public static void main(String[] args) {
        ProgramsFilterTest test = new ProgramsFilterTest();

        try {
            test.run();
            test.sleep(2000);
        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.close();
        }
    }
}