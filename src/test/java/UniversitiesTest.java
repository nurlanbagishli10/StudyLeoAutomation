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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniversitiesTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    private static final String SITE_URL = "https://studyleo.com/en";

    // Log sistemi
    private List<String> logMessages = new ArrayList<>();
    private String logFileName;
    private String screenshotFolder;

    // Locators
    private By acceptCookiesButton = By.cssSelector("button[data-testid='cookie-banner-accept-button']");
    private By universitiesLink = By.xpath("/html/body/div[2]/header/div/nav/div/ul/li[1]/a");
    private By universityCards = By.cssSelector("a[href*='/universities/'][aria-label*='View details']");
    private By paginationButtons = By.cssSelector("a[data-slot='pagination-link'], button[data-slot='pagination-link']");

    // Statistika
    private int totalPages = 0;
    private int universitiesPerPage = 0;
    private int totalTestedUniversities = 0;
    private int totalSuccessful = 0;
    private int totalFailed = 0;
    private int screenshotCount = 0;

    // Debug mode
    private boolean debugMode = true; // true olarsa detallı log

    public UniversitiesTest() {
        initializeDriver();
        initializeLog();
    }

    // ⚡ OPTİMALLAŞDIRILMIŞ DRIVER INIT
    private void initializeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver = new ChromeDriver(options);

        // ⚡ Page load timeout
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

        // ⚡ Implicit wait QISA
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        js = (JavascriptExecutor) driver;
    }

    private void initializeLog() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);

        logFileName = "UniversitiesTest_" + timestamp + ".txt";
        screenshotFolder = "screenshots_" + timestamp;

        try {
            Files.createDirectories(Paths.get(screenshotFolder));
            log("📁 Screenshot folder:  " + screenshotFolder);
        } catch (IOException e) {
            logError("Screenshot folder creation failed: " + e.getMessage());
        }

        log("═".repeat(70));
        log("🎓 UNIVERSITIES TEST - AUTOMATED TESTING");
        log("📅 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm: ss")));
        log("═".repeat(70));
    }

    private void log(String message) {
        System.out.println(message);
        logMessages.add(message);
    }

    private void logDebug(String message) {
        if (debugMode) {
            log("🔍 " + message);
        }
    }

    private void logError(String message) {
        String errorMsg = "❌ " + message;
        System.err.println(errorMsg);
        logMessages.add(errorMsg);
    }

    /**
     * 📸 Screenshot çək və saxla
     */
    /**
     * 📸 Screenshot çək və saxla - GÜCLÜ VERSİYA
     */
    private String takeScreenshot(String fileName) {
        try {
            System.out.println("📸 Screenshot çəkilir: " + fileName); // DEBUG

            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            String screenshotName = fileName + "_" + timestamp + ".png";
            String destinationPath = screenshotFolder + "/" + screenshotName;

            Files.copy(sourceFile.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);

            screenshotCount++;
            log("📸 Screenshot saved: " + screenshotName);
            System.out.println("✅ Screenshot saxlanıldı: " + destinationPath); // DEBUG

            return destinationPath;

        } catch (Exception e) {
            System.err.println("❌ Screenshot XƏTASI:  " + e.getMessage()); // DEBUG
            e.printStackTrace();
            logError("Screenshot failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * ⏱️ Thread.sleep wrapper
     */
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
            log("\n💾 Log saved:  " + logFileName);
        } catch (IOException e) {
            System.err.println("❌ Log save error: " + e.getMessage());
        }
    }

    public void run() {
        try {
            log("\n" + "█".repeat(70));
            log("█  🚀 STARTING AUTOMATED UNIVERSITY TESTING" + " ".repeat(26) + "█");
            log("█".repeat(70) + "\n");

            openWebsite();
            acceptCookies();
            clickUniversitiesLink();
            getPageInfo();

            log("\n" + "═".repeat(70));
            log("📊 DISCOVERED:");
            log("   📄 Total Pages: " + totalPages);
            log("   🎓 Universities per Page: " + universitiesPerPage);
            log("═".repeat(70) + "\n");

            for (int page = 1; page <= totalPages; page++) {
                log("\n" + "═".repeat(70));
                log("📄 PAGE " + page + "/" + totalPages);
                log("═".repeat(70));

                testUniversitiesOnPage(page);

                // ✅ Navigation check
                if (page < totalPages) {
                    boolean navigated = navigateToNextPage(page + 1);

                    if (! navigated) {
                        logError("Could not navigate to page " + (page + 1) + ".Stopping.");
                        break;
                    }
                }
            }

            printSummary();

        } catch (Exception e) {
            logError("CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
            takeScreenshot("CRITICAL_ERROR");
        }
    }

    private List<WebElement> getUniversityCardElements() {
        List<By> selectors = List.of(
                By.cssSelector("a[href*='/universities/'][aria-label*='View details']"),
                By.cssSelector("div[class*='grid'] > div > a[href*='/universities/']"),
                By.cssSelector("a[href*='/universities/']")
        );

        for (By selector : selectors) {
            try {
                List<WebElement> elements = driver.findElements(selector);
                List<WebElement> validElements = new ArrayList<>();

                for (WebElement el : elements) {
                    try {
                        String href = el.getAttribute("href");
                        if (href != null &&
                                href.contains("/universities/") &&
                                ! href.endsWith("/universities") &&
                                !href.contains("#")) {
                            validElements.add(el);
                        }
                    } catch (Exception e) {
                        // Skip
                    }
                }

                if (!validElements.isEmpty()) {
                    logDebug("Selector found " + validElements.size() + " elements");
                    return validElements;
                }
            } catch (Exception e) {
                logDebug("Selector failed: " + selector);
            }
        }

        return new ArrayList<>();
    }

    private void getPageInfo() {
        try {
            sleep(1000);
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            sleep(600);
            js.executeScript("window.scrollTo(0, 0);");
            sleep(600);

            List<WebElement> cards = getUniversityCardElements();
            Set<String> uniqueUrls = new HashSet<>();

            for (WebElement card : cards) {
                try {
                    String href = card.getAttribute("href");
                    if (href != null && href.contains("/universities/")) {
                        uniqueUrls.add(href);
                    }
                } catch (Exception e) {
                    // Skip
                }
            }

            universitiesPerPage = uniqueUrls.size();
            totalPages = getTotalPagesFromPagination();

            if (universitiesPerPage == 0) universitiesPerPage = 12;
            if (totalPages == 0) totalPages = 7;

        } catch (Exception e) {
            logError("Page info error: " + e.getMessage());
            universitiesPerPage = 12;
            totalPages = 7;
        }
    }

    private int getTotalPagesFromPagination() {
        try {
            List<WebElement> paginationElements = driver.findElements(paginationButtons);

            int maxPage = 1;
            for (WebElement element : paginationElements) {
                try {
                    String text = element.getText().trim();
                    if (text.matches("\\d+")) {
                        int pageNum = Integer.parseInt(text);
                        if (pageNum > maxPage) {
                            maxPage = pageNum;
                        }
                    }
                } catch (Exception e) {
                    // Skip
                }
            }

            return maxPage;

        } catch (Exception e) {
            return 0;
        }
    }

    // ⚡ OPTİMALLAŞDIRILMIŞ TEST METODU
    private void testUniversitiesOnPage(int pageNumber) {
        int successCount = 0;
        int errorCount = 0;

        try {
            sleep(500);
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            sleep(500);
            js.executeScript("window.scrollTo(0, 0);");
            sleep(400);

            List<String> universityUrls = new ArrayList<>();
            List<String> universityTitles = new ArrayList<>();
            List<WebElement> allCards = getUniversityCardElements();

            for (WebElement card : allCards) {
                try {
                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", card);
                    sleep(50);

                    String href = card.getAttribute("href");

                    if (href == null || href.isEmpty() ||
                            ! href.contains("/universities/") ||
                            href.endsWith("/universities") ||
                            href.contains("#")) {
                        continue;
                    }

                    if (universityUrls.contains(href)) {
                        continue;
                    }

                    universityUrls.add(href);
                    universityTitles.add(getUniversityTitle(card));

                } catch (Exception e) {
                    logDebug("Element read error: " + e.getMessage());
                }
            }

            int count = universityUrls.size();
            log("\n🔍 Found " + count + " universities on this page\n");

            String listPageUrl = driver.getCurrentUrl();

            for (int i = 0; i < count; i++) {
                totalTestedUniversities++;

                try {
                    String universityUrl = universityUrls.get(i);
                    String universityTitle = universityTitles.get(i);

                    log("━".repeat(70));
                    log("🎓 " + (i + 1) + "/" + count + ": " + universityTitle);
                    logDebug("URL: " + universityUrl);

                    // Universitet səhifəsini aç
                    driver.get(universityUrl);
                    sleep(600);

                    // Verification
                    boolean pageOpened = verifyUniversityPage();

                    String safeName = universityTitle.replaceAll("[^a-zA-Z0-9]", "_");

                    if (pageOpened) {
                        // ✅ SUCCESS - Screenshot ALMIRIQ
                        log("✅ Success");
                        successCount++;
                        totalSuccessful++;

                    } else {
                        // ❌ FAILED - Screenshot AL
                        logError("Failed - Verification unsuccessful");

                        // Screenshot AL (universitet səhifəsi açıq ikən)
                        takeScreenshot("FAILED_Page" + pageNumber + "_" + (i + 1) + "_" + safeName);

                        errorCount++;
                        totalFailed++;
                    }

                    // Navigate back to list
                    driver.get(listPageUrl);
                    sleep(500);

                } catch (Exception e) {
                    // ❌ EXCEPTION - Screenshot AL
                    logError("Test error: " + e.getMessage());

                    String safeName = (i < universityTitles.size())
                            ? universityTitles.get(i).replaceAll("[^a-zA-Z0-9]", "_")
                            : "Unknown";

                    takeScreenshot("EXCEPTION_Page" + pageNumber + "_" + (i + 1) + "_" + safeName);

                    errorCount++;
                    totalFailed++;

                    try {
                        driver.get(listPageUrl);
                        sleep(500);
                    } catch (Exception ex) {
                        logError("Return error!");
                    }
                }
            }

        } catch (Exception e) {
            logError("Page test error: " + e.getMessage());
            takeScreenshot("PAGE_ERROR_Page" + pageNumber);
        }

        log("\n" + "─".repeat(70));
        log("📊 PAGE " + pageNumber + " RESULT:");
        log("   ✅ Success: " + successCount);
        log("   ❌ Failed: " + errorCount);
        log("─".repeat(70));

        // DEBUG (yalnız ilk səhifədə)
        if (pageNumber == 1) {
            debugPagination();
        }
    }

    // ✅ GÜCLƏNDİRİLMİŞ NAVIGATION
    private boolean navigateToNextPage(int targetPage) {
        log("\n⏩ Navigating to page " + targetPage + "...");

        try {
            String currentUrl = driver.getCurrentUrl();
            String baseUrl = currentUrl.split("\\?")[0];
            String newUrl = baseUrl + "?page=" + targetPage;

            log("   🔗 URL: " + newUrl);

            // Yol 1: Direct URL navigation
            try {
                driver.get(newUrl);
                sleep(1000);

                String afterUrl = driver.getCurrentUrl();
                if (afterUrl.contains("page=" + targetPage)) {
                    log("✅ Navigated successfully (URL)");

                    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                    sleep(500);
                    js.executeScript("window.scrollTo(0, 0);");
                    sleep(500);

                    return true;
                }
            } catch (Exception e) {
                logDebug("   ⚠ Direct URL failed: " + e.getMessage());
            }

            // Yol 2: Pagination button click
            log("   🔄 Trying pagination button...");
            try {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                sleep(800);

                List<WebElement> paginationElements = driver.findElements(
                        By.cssSelector("a[data-slot='pagination-link'], button[data-slot='pagination-link']")
                );

                for (WebElement element : paginationElements) {
                    try {
                        String text = element.getText().trim();
                        if (text.equals(String.valueOf(targetPage))) {
                            log("   ✓ Found pagination button:  " + targetPage);

                            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
                            sleep(300);

                            try {
                                element.click();
                            } catch (Exception e) {
                                js.executeScript("arguments[0].click();", element);
                            }

                            sleep(1500);

                            String afterUrl = driver.getCurrentUrl();
                            if (afterUrl.contains("page=" + targetPage)) {
                                log("✅ Navigated successfully (button click)");

                                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                                sleep(500);
                                js.executeScript("window.scrollTo(0, 0);");
                                sleep(500);

                                return true;
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            } catch (Exception e) {
                logDebug("   ⚠ Pagination button failed: " + e.getMessage());
            }

            // Yol 3: Alternative selectors
            log("   🔄 Trying alternative selectors...");
            List<By> alternativeSelectors = List.of(
                    By.xpath("//a[text()='" + targetPage + "']"),
                    By.xpath("//button[text()='" + targetPage + "']"),
                    By.cssSelector("a[href*='page=" + targetPage + "']"),
                    By.cssSelector("button[aria-label='Page " + targetPage + "']")
            );

            for (By selector : alternativeSelectors) {
                try {
                    WebElement pageButton = wait.until(
                            ExpectedConditions.elementToBeClickable(selector)
                    );

                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", pageButton);
                    sleep(300);

                    try {
                        pageButton.click();
                    } catch (Exception e) {
                        js.executeScript("arguments[0].click();", pageButton);
                    }

                    sleep(1500);

                    String afterUrl = driver.getCurrentUrl();
                    if (afterUrl.contains("page=" + targetPage)) {
                        log("✅ Navigated successfully (alternative selector)");

                        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                        sleep(500);
                        js.executeScript("window.scrollTo(0, 0);");
                        sleep(500);

                        return true;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            // Yol 4: JavaScript redirect
            log("   🔄 Forcing URL with JavaScript...");
            try {
                js.executeScript("window.location.href = '" + newUrl + "';");
                sleep(2000);

                String afterUrl = driver.getCurrentUrl();
                if (afterUrl.contains("page=" + targetPage)) {
                    log("✅ Navigated successfully (JS redirect)");

                    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                    sleep(500);
                    js.executeScript("window.scrollTo(0, 0);");
                    sleep(500);

                    return true;
                }
            } catch (Exception e) {
                logDebug("   ⚠ JS redirect failed: " + e.getMessage());
            }

            logError("All navigation methods failed!");
            takeScreenshot("NAVIGATION_FAILED_Page" + targetPage);
            return false;

        } catch (Exception e) {
            logError("Navigation error: " + e.getMessage());
            takeScreenshot("NAVIGATION_ERROR_Page" + targetPage);
            return false;
        }
    }

    /**
     * 🔍 DEBUG:  Pagination elementlərini göstər
     */
    private void debugPagination() {
        log("\n🔍 DEBUG:  Pagination elements:");

        try {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            sleep(1000);

            List<WebElement> allPagination = driver.findElements(
                    By.cssSelector("a, button")
            );

            log("   Found " + allPagination.size() + " a/button elements");

            int count = 0;
            for (WebElement el : allPagination) {
                try {
                    String text = el.getText().trim();
                    String href = el.getAttribute("href");
                    String ariaLabel = el.getAttribute("aria-label");
                    String dataSlot = el.getAttribute("data-slot");

                    if ((text.matches("\\d+") ||
                            (href != null && href.contains("page=")) ||
                            (dataSlot != null && dataSlot.contains("pagination")))) {

                        count++;
                        log("   " + count + ". Text: '" + text + "' | Href: " + href + " | Aria:  " + ariaLabel + " | Data-slot: " + dataSlot);

                        if (count >= 10) break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            if (count == 0) {
                log("   ❌ No pagination elements found!");
            }

        } catch (Exception e) {
            logError("Debug pagination failed: " + e.getMessage());
        }
    }

    // ⚡ SMART VERIFICATION - University Title ilə
    /**
     * ⚡ VERIFICATION - Screenshot üçün wait əlavəsi
     */
    private boolean verifyUniversityPage() {
        try {
            sleep(800); // 500→800 (screenshot üçün səhifə tam render olsun)

            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("/universities/") || currentUrl.endsWith("/universities")) {
                logError("Invalid URL: " + currentUrl);

                // ✅ URL xətası halında screenshot
                takeScreenshot("ERROR_INVALID_URL");

                return false;
            }

            try {
                WebElement body = driver.findElement(By.tagName("body"));
                String bodyText = body.getText();

                if (bodyText != null && bodyText.length() > 100) {
                    logDebug("   ✓ Content OK (" + bodyText.length() + " chars)");
                    return true;
                } else {
                    logError("Content too short: " + (bodyText != null ? bodyText.length() : "null") + " chars");

                    // ✅ Content qısa halında screenshot (burda problem var)
                    System.out.println("🔴 CONTENT PROBLEM - Screenshot çəkilir...");
                    takeScreenshot("ERROR_CONTENT_TOO_SHORT");

                    return false;
                }
            } catch (Exception e) {
                logError("Body check failed: " + e.getMessage());

                // ✅ Body tapılmadı halında screenshot
                takeScreenshot("ERROR_BODY_NOT_FOUND");

                return false;
            }

        } catch (Exception e) {
            logError("Verification error: " + e.getMessage());

            // ✅ Ümumi xəta halında screenshot
            takeScreenshot("ERROR_VERIFICATION_EXCEPTION");

            return false;
        }
    }

    private String getUniversityTitle(WebElement card) {
        try {
            String ariaLabel = card.getAttribute("aria-label");
            if (ariaLabel != null && ! ariaLabel.isEmpty()) {
                return ariaLabel.replace("View details for ", "");
            }

            try {
                WebElement titleSpan = card.findElement(By.cssSelector("article span:first-child"));
                String title = titleSpan.getText();
                if (title != null && !title.isEmpty()) return title;
            } catch (Exception e) {}

            return card.getText().split("\n")[0];

        } catch (Exception e) {
            return "Unknown University";
        }
    }

    private void printSummary() {
        log("\n" + "█".repeat(70));
        log("█  📊 FINAL RESULTS" + " ".repeat(51) + "█");
        log("█".repeat(70));
        log("");
        log("   🎓 Total Universities Tested: " + totalTestedUniversities);
        log("   ✅ Successful:  " + totalSuccessful);
        log("   ❌ Failed: " + totalFailed);
        log("   📸 Screenshots:  " + screenshotCount);
        log("");

        double successRate = totalTestedUniversities > 0
                ? (totalSuccessful * 100.0 / totalTestedUniversities)
                : 0;
        log("   📈 Success Rate: " + String.format("%.2f%%", successRate));

        log("");
        log("█".repeat(70));
        log("█  ✅ TEST COMPLETED" + " ".repeat(50) + "█");
        log("█".repeat(70) + "\n");

        saveLogsToFile();
    }

    private void openWebsite() {
        log("🌐 Opening:  " + SITE_URL);
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
        if (driver != null) {
            log("\n🔚 Closing browser...");
            driver.quit();
        }
    }

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