import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
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
    private WebDriverWait longWait;
    private WebDriverWait shortWait;
    private JavascriptExecutor js;

    private static final String SITE_URL = "https://studyleo.com/en";

    private List<String> logMessages = new ArrayList<>();
    private String logFileName;
    private String screenshotFolder;

    private By acceptCookiesButton = By.cssSelector("button[data-testid='cookie-banner-accept-button']");
    private By blogsButton = By.xpath("/html/body/div[2]/header/div/nav/div/ul/li[3]/a");
    private By blogsContainer = By.cssSelector("main");
    private By blogItems = By.cssSelector("a[href*='/blogs/']");
    private By blogMainContent = By.cssSelector("main h1, main h2, article h1, article h2, main article, article p");
    private By nextPageButton = By.cssSelector("a[aria-label='Go to next page']");

    private int totalPages = 0;
    private int totalTestedBlogs = 0;
    private int totalSuccessful = 0;
    private int totalFailed = 0;
    private int screenshotCount = 0;

    private boolean debugMode = false;

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
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;
    }

    private void initializeLog() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);

        logFileName = "BlogsTest_" + timestamp + ".txt";
        screenshotFolder = "screenshots_blogs_" + timestamp;

        try {
            Files.createDirectories(Paths.get(screenshotFolder));
            log("📁 Screenshot folder:  " + screenshotFolder);
        } catch (IOException e) {
            logError("Screenshot folder creation failed: " + e.getMessage());
        }

        log("═".repeat(70));
        log("📰 BLOGS TEST - AUTOMATED TESTING");
        log("📅 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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

    private String takeScreenshot(String fileName) {
        try {
            System.out.println("📸 Screenshot çəkilir:  " + fileName);

            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            String screenshotName = fileName + "_" + timestamp + ".png";
            String destinationPath = screenshotFolder + "/" + screenshotName;

            Files.copy(sourceFile.toPath(), Paths.get(destinationPath), StandardCopyOption. REPLACE_EXISTING);

            screenshotCount++;
            log("📸 Screenshot saved: " + screenshotName);
            System.out.println("✅ Screenshot saxlanıldı: " + destinationPath);

            return destinationPath;

        } catch (Exception e) {
            System.err.println("❌ Screenshot XƏTASI:  " + e.getMessage());
            e.printStackTrace();
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
            log("\n��� Log saved:  " + logFileName);
        } catch (IOException e) {
            System.err.println("❌ Log save error: " + e.getMessage());
        }
    }

    public void run() {
        try {
            log("\n" + "█".repeat(70));
            log("█  🚀 STARTING AUTOMATED BLOG TESTING" + " ". repeat(32) + "█");
            log("█". repeat(70) + "\n");

            openWebsite();
            acceptCookies();
            clickBlogsLink();
            iterateAllBlogs();

            printSummary();

        } catch (Exception e) {
            logError("CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
            takeScreenshot("CRITICAL_ERROR");
        }
    }

    private void iterateAllBlogs() {
        int pageNumber = 1;

        while (true) {
            log("\n" + "═".repeat(70));
            log("📄 PAGE " + pageNumber);
            log("═".repeat(70));

            waitForPageReady();
            sleep(1500);

            js.executeScript("window.scrollTo(0, document.body. scrollHeight);");
            sleep(800);
            js.executeScript("window.scrollTo(0, 0);");
            sleep(800);

            try {
                longWait.until(ExpectedConditions.presenceOfElementLocated(blogsContainer));
            } catch (Exception e) {
                logError("Blogs container not found on page " + pageNumber);
                takeScreenshot("ERROR_NO_BLOGS_CONTAINER_Page" + pageNumber);
                break;
            }

            List<String> blogUrls = new ArrayList<>();
            List<String> blogTitles = new ArrayList<>();

            List<WebElement> blogElements = getBlogElements();

            if (blogElements.isEmpty()) {
                log("⚠️ Bu səhifədə blog tapılmadı.  Test dayandırılır.");
                takeScreenshot("WARNING_NO_BLOGS_Page" + pageNumber);
                break;
            }

            for (WebElement blog : blogElements) {
                try {
                    String href = blog.getAttribute("href");
                    if (href != null && ! href.isEmpty() && href.contains("/blogs/")) {
                        if (! href.endsWith("/blogs") && !blogUrls.contains(href)) {
                            blogUrls.add(href);
                            blogTitles.add(getBlogTitle(blog));
                        }
                    }
                } catch (Exception e) {
                    logDebug("Blog element oxuma xətası: " + e.getMessage());
                }
            }

            int blogsOnPage = blogUrls.size();
            log("\n🔍 Bu səhifədə " + blogsOnPage + " blog tapıldı\n");

            if (blogsOnPage == 0) {
                log("⚠️ Heç bir blog tapılmadı.  Test dayandırılır.");
                break;
            }

            testBlogsOnPage(pageNumber, blogUrls, blogTitles);

            totalPages++;

            if (blogsOnPage < 15) {
                log("\n✅ Bu səhifədə 15-dən az blog var (" + blogsOnPage + "), test tamamlandı.");
                break;
            }

            if (hasNextPage()) {
                log("\n⏩ Navigating to page " + (pageNumber + 1) + "...");
                boolean navigated = clickNextPage();

                if (! navigated) {
                    logError("Could not navigate to page " + (pageNumber + 1) + ".  Stopping.");
                    break;
                }

                pageNumber++;
                waitForPageReady();
                sleep(1500);
            } else {
                log("\n✅ Növbəti səhifə yoxdur. Test tamamlandı.");
                break;
            }
        }
    }

    private void testBlogsOnPage(int pageNumber, List<String> blogUrls, List<String> blogTitles) {
        int successCount = 0;
        int errorCount = 0;
        int count = blogUrls.size();

        String listPageUrl = driver.getCurrentUrl();

        for (int i = 0; i < count; i++) {
            totalTestedBlogs++;

            try {
                String blogUrl = blogUrls.get(i);
                String blogTitle = blogTitles.get(i);

                log("━". repeat(70));
                log("📰 " + (i + 1) + "/" + count + ": " + blogTitle);
                logDebug("URL: " + blogUrl);

                driver.get(blogUrl);
                sleep(1500);

                boolean pageOpened = verifyBlogPage();

                String safeName = blogTitle.replaceAll("[^a-zA-Z0-9]", "_");

                if (pageOpened) {
                    log("✅ Success");
                    successCount++;
                    totalSuccessful++;
                } else {
                    logError("Failed - Verification unsuccessful");
                    takeScreenshot("FAILED_Page" + pageNumber + "_" + (i + 1) + "_" + safeName);
                    errorCount++;
                    totalFailed++;
                }

                driver.get(listPageUrl);
                sleep(1000);
                waitForPageReady();

            } catch (Exception e) {
                logError("Test error:  " + e.getMessage());

                String safeName = (i < blogTitles.size())
                        ? blogTitles.get(i).replaceAll("[^a-zA-Z0-9]", "_")
                        : "Unknown";

                takeScreenshot("EXCEPTION_Page" + pageNumber + "_" + (i + 1) + "_" + safeName);

                errorCount++;
                totalFailed++;

                try {
                    driver.get(listPageUrl);
                    sleep(1000);
                    waitForPageReady();
                } catch (Exception ex) {
                    logError("Return error!");
                }
            }
        }

        log("\n" + "─".repeat(70));
        log("📊 PAGE " + pageNumber + " RESULT:");
        log("   ✅ Success: " + successCount);
        log("   ❌ Failed: " + errorCount);
        log("─".repeat(70));
    }

    private boolean verifyBlogPage() {
        try {
            // ✅ 1. Səhifənin tam yüklənməsini gözlə
            log("   ⏳ Waiting for page to load completely...");
            waitForPageReady();
            sleep(2000); // 1500→2000

            // ✅ 2. URL yoxla
            String currentUrl = driver.getCurrentUrl();
            logDebug("Current URL: " + currentUrl);

            if (! currentUrl.contains("/blogs/") || currentUrl.endsWith("/blogs")) {
                logError("Invalid URL:  " + currentUrl);
                return false;
            }

            // ✅ 3. Content elementlərinin yüklənməsini gözlə (retry ilə)
            int maxRetries = 3;
            int attempt = 0;
            List<WebElement> contentElements = new ArrayList<>();

            while (attempt < maxRetries && contentElements.isEmpty()) {
                attempt++;

                try {
                    logDebug("Waiting for content elements (attempt " + attempt + "/" + maxRetries + ")...");

                    // Explicit wait - content elementləri görünənə qədər gözlə
                    contentElements = wait. until(ExpectedConditions.presenceOfAllElementsLocatedBy(blogMainContent));

                    if (! contentElements.isEmpty()) {
                        logDebug("✓ Content elements found:  " + contentElements.size());
                        break;
                    }

                } catch (Exception e) {
                    if (attempt < maxRetries) {
                        logDebug("Content not ready, retrying... (" + attempt + "/" + maxRetries + ")");
                        sleep(1500);
                    } else {
                        logError("Blog content elements not found after " + maxRetries + " attempts");
                        return false;
                    }
                }
            }

            if (contentElements. isEmpty()) {
                logError("Blog content elements not found");
                return false;
            }

            // ✅ 4. Body text yoxla (retry ilə)
            attempt = 0;
            String bodyText = null;

            while (attempt < maxRetries) {
                attempt++;

                try {
                    WebElement body = driver.findElement(By. tagName("body"));
                    bodyText = body.getText();

                    if (bodyText != null && bodyText. length() > 100) {
                        logDebug("   ✓ Content OK (" + bodyText.length() + " chars)");
                        return true;
                    }

                    // Content qısa olarsa, bir az gözlə və yenidən yoxla
                    if (attempt < maxRetries) {
                        logDebug("Content short (" + (bodyText != null ? bodyText. length() : "0") + " chars), waiting and retrying...");
                        sleep(1500);
                    } else {
                        logError("Content too short:  " + (bodyText != null ? bodyText. length() : "null") + " chars after " + maxRetries + " attempts");
                        return false;
                    }

                } catch (Exception e) {
                    if (attempt < maxRetries) {
                        logDebug("Body check failed, retrying... (" + attempt + "/" + maxRetries + ")");
                        sleep(1500);
                    } else {
                        logError("Content check failed after " + maxRetries + " attempts:  " + e.getMessage());
                        return false;
                    }
                }
            }

            logError("Content verification failed");
            return false;

        } catch (Exception e) {
            logError("Verification error: " + e.getMessage());
            return false;
        }
    }

    private List<WebElement> getBlogElements() {
        List<By> selectors = List.of(
                By.cssSelector("a[href*='/blogs/']"),
                By.cssSelector("main a[href*='/blogs/']")
        );

        for (By selector : selectors) {
            try {
                List<WebElement> elements = driver.findElements(selector);
                List<WebElement> validElements = new ArrayList<>();

                for (WebElement el : elements) {
                    try {
                        String href = el.getAttribute("href");
                        if (href != null &&
                                href.contains("/blogs/") &&
                                !href.endsWith("/blogs") &&
                                !href.contains("#")) {
                            validElements.add(el);
                        }
                    } catch (Exception e) {
                        // Skip
                    }
                }

                if (!validElements.isEmpty()) {
                    logDebug("Selector found " + validElements.size() + " blog elements");
                    return validElements;
                }
            } catch (Exception e) {
                logDebug("Selector failed: " + selector);
            }
        }

        return new ArrayList<>();
    }

    private String getBlogTitle(WebElement blog) {
        try {
            String text = blog.getText();
            if (text != null && ! text.isEmpty()) {
                String[] lines = text.split("\n");
                if (lines.length > 0) {
                    return lines[0];
                }
            }

            String ariaLabel = blog.getAttribute("aria-label");
            if (ariaLabel != null && !ariaLabel.isEmpty()) {
                return ariaLabel;
            }

            String href = blog.getAttribute("href");
            if (href != null) {
                String[] parts = href.split("/");
                return parts[parts.length - 1];
            }

            return "Unknown Blog";

        } catch (Exception e) {
            return "Unknown Blog";
        }
    }


    private boolean hasNextPage() {
        try {
            log("\n🔍 Checking for next page button...");

            js.executeScript("window.scrollTo(0, document.body. scrollHeight);");
            sleep(1500);

            // ✅ Strategiya 1: Page number button (2, 3, 4...)
            try {
                List<WebElement> pageButtons = driver.findElements(
                        By.cssSelector("a[data-slot='pagination-link']:not([aria-current])")
                );

                for (WebElement btn : pageButtons) {
                    try {
                        String innerHtml = btn.getAttribute("innerHTML");

                        // İnnerHTML-də rəqəm varsa (2, 3, 4...)
                        if (innerHtml != null && innerHtml.trim().matches("\\d+")) {
                            // ✅ isDisplayed() yoxlamasını SILIRIQ, yalnız isEnabled() yoxlayırıq
                            if (btn. isEnabled()) {
                                logDebug("Next page button found:  " + innerHtml. trim());
                                log("✅ Next page option found (page " + innerHtml.trim() + " button)");
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        // Skip stale elements
                    }
                }
            } catch (Exception e) {
                logDebug("Page number button not found: " + e.getMessage());
            }

            // ✅ Strategiya 2: "Next" button
            try {
                WebElement nextButton = driver.findElement(
                        By.cssSelector("a[aria-label='Go to next page']")
                );

                if (nextButton.isEnabled()) {
                    log("✅ Next page option found ('Next' button)");
                    return true;
                }
            } catch (Exception e) {
                logDebug("'Next' button not found or not clickable: " + e.getMessage());
            }

            log("❌ No clickable next page option found");

            try {
                Files.createDirectories(Paths.get(screenshotFolder));
            } catch (IOException ex) {
                logDebug("Screenshot folder check:  " + ex.getMessage());
            }

            takeScreenshot("NO_NEXT_PAGE_BUTTON");
            return false;

        } catch (Exception e) {
            logError("hasNextPage error: " + e.getMessage());
            return false;
        }
    }

    private boolean clickNextPage() {
        try {
            String currentUrl = driver.getCurrentUrl();
            log("📍 Current URL: " + currentUrl);

            js.executeScript("window.scrollTo(0, document.body. scrollHeight);");
            sleep(1500);

            // ❌ BU SƏTRİ SİLDİM (debug screenshot)
            // takeScreenshot("BEFORE_NEXT_PAGE_CLICK");

            try {
                WebElement page2Button = wait.until(ExpectedConditions. elementToBeClickable(
                        By.cssSelector("a[data-slot='pagination-link'][href='#']:not([aria-current])")
                ));

                String buttonText = page2Button.getText().trim();
                logDebug("Clicking pagination button: " + buttonText);

                scrollToElement(page2Button);
                sleep(500);

                try {
                    page2Button.click();
                    log("   ✓ Clicked pagination button (standard click)");
                } catch (Exception e) {
                    js. executeScript("arguments[0].click();", page2Button);
                    log("   ✓ Clicked pagination button (JavaScript click)");
                }

                sleep(2500);
                waitForPageReady();

                String newUrl = driver.getCurrentUrl();
                log("📍 New URL: " + newUrl);

                if (! newUrl.equals(currentUrl) || isPageContentChanged()) {
                    log("✅ Navigated successfully to next page");
                    return true;
                } else {
                    logError("Page did not change after clicking");
                    takeScreenshot("NEXT_PAGE_CLICK_FAILED");
                    return false;
                }

            } catch (Exception e1) {
                logDebug("Strategy 1 (page number button) failed: " + e1.getMessage());

                try {
                    WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("a[aria-label='Go to next page']")
                    ));

                    logDebug("Trying 'Next' button");
                    scrollToElement(nextButton);
                    sleep(500);

                    try {
                        nextButton.click();
                        log("   ✓ Clicked 'Next' button (standard click)");
                    } catch (Exception e) {
                        js.executeScript("arguments[0].click();", nextButton);
                        log("   ✓ Clicked 'Next' button (JavaScript click)");
                    }

                    sleep(2500);
                    waitForPageReady();

                    String newUrl = driver.getCurrentUrl();
                    log("📍 New URL:  " + newUrl);

                    if (!newUrl.equals(currentUrl) || isPageContentChanged()) {
                        log("✅ Navigated successfully to next page");
                        return true;
                    } else {
                        logError("Page did not change after clicking 'Next'");
                        takeScreenshot("NEXT_BUTTON_CLICK_FAILED");
                        return false;
                    }

                } catch (Exception e2) {
                    logError("Strategy 2 (Next button) also failed: " + e2.getMessage());
                    takeScreenshot("ALL_NEXT_PAGE_STRATEGIES_FAILED");
                    return false;
                }
            }

        } catch (Exception e) {
            logError("clickNextPage error: " + e.getMessage());
            takeScreenshot("NEXT_PAGE_ERROR");
            return false;
        }
    }

    // ✅ YENİ METOD: Content dəyişdiyini yoxla (URL dəyişməsə belə)
    private boolean isPageContentChanged() {
        try {
            // Aktiv səhifə nömrəsini yoxla
            List<WebElement> activePages = driver.findElements(
                    By.cssSelector("a[data-slot='pagination-link'][aria-current='page']")
            );

            if (! activePages.isEmpty()) {
                String activePageNum = activePages.get(0).getText().trim();
                logDebug("Active page number: " + activePageNum);

                // Əgər "2" və ya daha böyükdürsə, content dəyişib
                if (activePageNum.matches("\\d+") && Integer.parseInt(activePageNum) > 1) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            logDebug("isPageContentChanged error:  " + e.getMessage());
            return false;
        }
    }

    private void waitForUrlChange(String previousUrl, String mustContain) {
        try {
            longWait.until((ExpectedCondition<Boolean>) drv -> {
                String url = drv.getCurrentUrl();
                return !url.equals(previousUrl) && (mustContain == null || url.contains(mustContain));
            });
        } catch (Exception e) {
            logDebug("URL change timeout: " + e.getMessage());
        }
    }

    private void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        sleep(500);
    }

    private void waitForPageReady() {
        try {
            longWait. until((ExpectedCondition<Boolean>) drv ->
                    js.executeScript("return document.readyState").toString().equals("complete"));
            sleep(500);
        } catch (Exception ignored) {}
    }

    private void printSummary() {
        log("\n" + "█".repeat(70));
        log("█  📊 FINAL RESULTS" + " ".repeat(51) + "█");
        log("█". repeat(70));
        log("");
        log("   📰 Total Blogs Tested: " + totalTestedBlogs);
        log("   ✅ Successful:  " + totalSuccessful);
        log("   ❌ Failed: " + totalFailed);
        log("   📄 Total Pages: " + totalPages);
        log("   📸 Screenshots:  " + screenshotCount);
        log("");

        double successRate = totalTestedBlogs > 0
                ? (totalSuccessful * 100.0 / totalTestedBlogs)
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
        sleep(2000);
        waitForPageReady();
        log("✅ Website opened\n");
    }

    private void acceptCookies() {
        log("🍪 Accepting cookies...");
        try {
            WebElement btn = shortWait.until(ExpectedConditions.elementToBeClickable(acceptCookiesButton));
            btn.click();
            sleep(800);
            log("✅ Cookies accepted\n");
        } catch (Exception e) {
            log("⚠️ Cookies already accepted\n");
        }
    }

    private void clickBlogsLink() {
        log("📍 Clicking Blogs link...");

        try {
            String currentUrl = driver.getCurrentUrl();
            logDebug("Current URL before click: " + currentUrl);

            if (currentUrl.contains("/blogs")) {
                log("✅ Already on blogs page!\n");

                try {
                    longWait.until(ExpectedConditions.presenceOfElementLocated(blogsContainer));
                    log("✅ Blogs container found!\n");
                    return;
                } catch (Exception e) {
                    logError("Blogs container not found even though URL contains /blogs");
                    takeScreenshot("ERROR_BLOGS_CONTAINER_NOT_FOUND");
                    throw e;
                }
            }

            WebElement link = longWait.until(ExpectedConditions.presenceOfElementLocated(blogsButton));
            logDebug("Blogs button found");

            scrollToElement(link);
            sleep(800);

            link = longWait.until(ExpectedConditions.elementToBeClickable(blogsButton));
            logDebug("Blogs button is clickable");

            try {
                link.click();
                log("   ✓ Clicked with standard click");
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", link);
                log("   ✓ Clicked with JavaScript");
            }

            sleep(3000);

            log("   ⏳ Waiting for URL change...");
            waitForUrlChange(currentUrl, "/blogs");
            waitForPageReady();
            sleep(1000);

            log("   ⏳ Waiting for blogs container...");
            longWait.until(ExpectedConditions.presenceOfElementLocated(blogsContainer));

            String newUrl = driver.getCurrentUrl();
            logDebug("Current URL after click: " + newUrl);

            if (newUrl.contains("/blogs")) {
                log("✅ Blogs page opened successfully!\n");
            } else {
                logError("Failed to navigate to blogs page.  Current URL: " + newUrl);
                takeScreenshot("ERROR_BLOGS_PAGE_NOT_OPENED");
                throw new Exception("Blogs page not opened");
            }

        } catch (Exception e) {
            logError("Blogs link click failed: " + e.getMessage());
            takeScreenshot("ERROR_BLOGS_LINK_CLICK");
            throw new RuntimeException(e);
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