import io.github.bonigarcia. wdm.WebDriverManager;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium. chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org. openqa.selenium.support.ui.WebDriverWait;

public class VisaSupportTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    private static final String HOME_URL = "https://studyleo.com/en";
    private static final String VISA_SUPPORT_BASE = "https://studyleo.com/en/study-visa-support-in-turkey";
    private static final int TOTAL_PAGES = 10;
    private static final int COUNTRIES_PER_PAGE = 20;
    private static final int LAST_PAGE_COUNTRIES = 13;
    private static final int TOTAL_COUNTRIES = 193;

    // ⚙️ LOCATORS
    private final By visaSupportLink = By.cssSelector("a[href*='study-visa-support-in-turkey']");
    private final By countryCards = By.cssSelector("a[href*='/study-visa-support-in-turkey/']");
    private final By documentsTable = By.cssSelector("table[data-slot='table']");
    private final By documentRows = By.cssSelector("tbody[data-slot='table-body'] tr");
    private final By cookieAcceptButton = By.cssSelector("button[data-testid='cookie-banner-accept-button']");

    // ✅ Required Documents List
    private static final List<String> REQUIRED_DOCUMENTS = Arrays.asList(
            "University Acceptance Letter",
            "Visa Form",
            "Valid Passport",
            "Tuition Payment Receipt",
            "Bank Statement or Sponsor Letter",
            "2 Biometric Photos",
            "Travel Insurance",
            "Accommodation Proof",
            "Visa Fee Receipt",
            "Health Report (If Required)",
            "Flight Booking or Hotel Booking"
    );

    private int totalCountries = 0;
    private int successfulTests = 0;
    private int failedTests = 0;
    private List<String> missingDocuments = new ArrayList<>();
    private List<String> pageErrors = new ArrayList<>();

    public static void main(String[] args) {
        VisaSupportTest test = new VisaSupportTest();
        test.run();
    }

    public void run() {
        try {
            initializeDriver();
            printHeader();

            navigateToVisaSupport();
            testAllPages();

            printSummary();

        } catch (Exception e) {
            System.err.println("❌ Test xətası:  " + e.getMessage());
            e.printStackTrace();
        } finally {
            quit();
        }
    }

    private void initializeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        js = (JavascriptExecutor) driver;
    }

    private void navigateToVisaSupport() {
        System.out.println("🌐 Home page açılır:  " + HOME_URL);
        driver.get(HOME_URL);
        waitForPageReady();
        sleep(1000);
        System.out.println("✅ Home page açıldı\n");

        acceptCookies();

        System.out.println("🔍 Visa Support linkini tapır.. .");
        WebElement visaLink = wait.until(ExpectedConditions.elementToBeClickable(visaSupportLink));
        scrollToElement(visaLink);
        System.out.println("🖱️ Visa Support linkə click edilir...");
        clickElement(visaLink);

        waitForPageReady();
        sleep(1500);
        System.out. println("✅ Visa Support səhifəsi açıldı:  " + driver.getCurrentUrl());
        System.out.println("═".repeat(70) + "\n");
    }

    private void testAllPages() {
        for (int page = 1; page <= TOTAL_PAGES; page++) {
            System.out. println("\n" + "█".repeat(70));
            System.out.println("█  📄 SƏHİFƏ " + page + "/" + TOTAL_PAGES + "                                                     █");
            System.out.println("█". repeat(70) + "\n");

            int expectedCountries = (page == TOTAL_PAGES) ? LAST_PAGE_COUNTRIES : COUNTRIES_PER_PAGE;
            testCurrentPage(page, expectedCountries);

            if (page < TOTAL_PAGES) {
                clickNextButton();
            }
        }
    }

    private void testCurrentPage(int pageNumber, int expectedCount) {
        try {
            waitForPageReady();
            sleep(1000);

            // ✅ Səhifə nömrəsini yoxla və lazım olsa düzəlt
            int currentPage = getCurrentPageNumber();
            if (currentPage != pageNumber) {
                System.out. println("⚠️ Səhifə nömrəsi uyğun gəlmir!  Cari:  " + currentPage + ", Gözlənilən: " + pageNumber);
                System.out.println("🔄 Düzgün səhifəyə keçid edilir.. .");
                String correctUrl = VISA_SUPPORT_BASE + (pageNumber > 1 ? "?page=" + pageNumber : "");
                driver.get(correctUrl);
                waitForPageReady();
                sleep(1500);
            }

            js. executeScript("window.scrollTo(0, 500);");
            sleep(500);

            List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(countryCards));
            int actualCount = cards.size();

            System.out.println("📊 Gözlənilən ölkə sayı: " + expectedCount);
            System.out.println("📊 Faktiki ölkə sayı: " + actualCount);

            if (actualCount != expectedCount) {
                System.out.println("⚠️ XƏBƏRDARLIQ:  Ölkə sayı uyğun gəlmir!");
            }

            System.out.println("═".repeat(70) + "\n");

            for (int i = 0; i < actualCount; i++) {
                testCountryCard(pageNumber, i, actualCount);
            }

            System.out.println("🔙 Səhifə test tamamlandı\n");

        } catch (Exception e) {
            System.err.println("❌ Səhifə " + pageNumber + " test xətası:  " + e.getMessage());
        }
    }

    private void testCountryCard(int pageNumber, int index, int totalOnPage) {
        String countryName = "Unknown";
        int maxRetries = 2;

        for (int retry = 0; retry <= maxRetries; retry++) {
            try {
                // Düzgün səhifədə olduğunu yoxla
                String currentUrl = driver.getCurrentUrl();
                String expectedUrl = VISA_SUPPORT_BASE + (pageNumber > 1 ? "?page=" + pageNumber : "");

                if (! currentUrl.startsWith(VISA_SUPPORT_BASE) ||
                        !currentUrl.contains("/study-visa-support-in-turkey")) {
                    System.out.println("🔙 Visa Support səhifəsinə qayıdır.. .");
                    driver.get(expectedUrl);
                    waitForPageReady();
                    sleep(1500);
                }

                js. executeScript("window.scrollTo(0, 500);");
                sleep(800);

                List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(countryCards));

                if (index >= cards.size()) {
                    System. out.println("⚠️ Ölkə #" + (index + 1) + " tapılmadı");
                    return;
                }

                WebElement card = cards.get(index);
                countryName = getCountryName(card);
                totalCountries++;

                System.out.println("━". repeat(70));
                System.out.println("🌍 Səhifə " + pageNumber + " | Ölkə " + (index + 1) + "/" + totalOnPage + ": " + countryName);
                System.out.println("   Ümumi progress: " + totalCountries + "/" + TOTAL_COUNTRIES);
                System.out.println("━".repeat(70));

                scrollToElement(card);

                if (retry > 0) {
                    System. out.println("🔄 Retry " + retry + "/" + maxRetries);
                }

                System.out.println("🖱️ \"" + countryName + "\" ölkəsinə click edilir.. .");

                String beforeClickUrl = driver.getCurrentUrl();
                clickElement(card);

                // URL dəyişməsini AKTIV gözlə
                try {
                    wait.until(driver1 -> ! driver. getCurrentUrl().equals(beforeClickUrl));
                    System.out.println("   ⏳ URL dəyişdi, səhifə yüklənir...");
                } catch (Exception e) {
                    System.out.println("   ⚠️ URL dəyişmədi " + (retry < maxRetries ? "- retry ediləcək" : ""));
                    if (retry < maxRetries) {
                        sleep(1000);
                        continue;
                    }
                }

                waitForPageReady();
                sleep(1200);

                String afterClickUrl = driver.getCurrentUrl();
                System.out.println("✅ Səhifə açıldı: " + afterClickUrl);

                // URL dəyişməyibsə = click uğursuz
                if (beforeClickUrl.equals(afterClickUrl)) {
                    if (retry < maxRetries) {
                        System.out.println("⚠️ Click uğursuz, retry edilir...");
                        sleep(1000);
                        continue;
                    }

                    String errorMsg = countryName + ": Click uğursuz oldu, səhifə açılmadı";
                    System.out.println("❌ " + errorMsg);
                    pageErrors.add(errorMsg);
                    failedTests++;
                    System.out.println();

                    // Visa Support-a qayıt
                    driver.get(expectedUrl);
                    waitForPageReady();
                    sleep(800);

                    return;
                }

                // URL yanlış səhifədədirsə
                if (! afterClickUrl.contains("/study-visa-support-in-turkey/")) {
                    String errorMsg = countryName + ": Yanlış səhifə açıldı - " + afterClickUrl;
                    System.out.println("❌ " + errorMsg);
                    pageErrors.add(errorMsg);
                    failedTests++;
                    System.out.println();

                    // Visa Support-a qayıt
                    driver. get(expectedUrl);
                    waitForPageReady();
                    sleep(800);

                    return;
                }

                // ✅ ERROR PAGE DETECTION
                if (isErrorPage()) {
                    String errorMsg = countryName + ":  Səhifə açılmadı (Something went wrong / Error page)";
                    System.out.println("❌ " + errorMsg);
                    pageErrors.add(errorMsg);
                    failedTests++;
                    System.out.println();

                    // ✅ BURA ƏLAVƏ EDİLDİ
                    System.out.println("🔙 Visa Support səhifəsinə qayıdır (error sonrası)...");
                    driver.get(expectedUrl);
                    waitForPageReady();
                    sleep(800);

                    return;
                }

                // Documents table-ı yoxla
                if (checkDocumentsTable(countryName)) {
                    System. out.println("✅ Bütün tələb olunan sənədlər mövcuddur (" + REQUIRED_DOCUMENTS. size() + " ədəd)");
                    successfulTests++;
                } else {
                    System.out.println("❌ Bəzi sənədlər əksikdir!");
                    failedTests++;
                }

                System.out.println();

                // Visa Support səhifəsinə qayıt
                System.out.println("🔙 Visa Support səhifəsinə qayıdır.. .");
                driver.get(expectedUrl);
                waitForPageReady();
                sleep(800);

                return;

            } catch (Exception e) {
                if (retry < maxRetries) {
                    System.out.println("⚠️ Exception, retry " + (retry + 1) + "/" + maxRetries + ":  " + e.getMessage());

                    // ✅ Exception olsa da Visa Support-a qayıt
                    try {
                        String expectedUrl = VISA_SUPPORT_BASE + (pageNumber > 1 ? "?page=" + pageNumber : "");
                        driver.get(expectedUrl);
                        waitForPageReady();
                        sleep(1500);
                    } catch (Exception ex) {
                        System.err.println("⚠️ Visa Support-a qayıtma xətası: " + ex.getMessage());
                    }

                    continue;
                }

                System.err.println("❌ \"" + countryName + "\" test xətası: " + e.getMessage());
                failedTests++;
                totalCountries++;

                // ✅ Final exception - yenə qayıt
                try {
                    String expectedUrl = VISA_SUPPORT_BASE + (pageNumber > 1 ? "?page=" + pageNumber : "");
                    driver.get(expectedUrl);
                    waitForPageReady();
                    sleep(800);
                } catch (Exception ex) {
                    System.err. println("⚠️ Final Visa Support qayıtma xətası:  " + ex.getMessage());
                }

                return;
            }
        }
    }

    // ✅ ERROR PAGE DETECTION
    private boolean isErrorPage() {
        try {
            // 1. Table varsa = normal səhifədir
            try {
                driver.findElement(documentsTable);
                return false;
            } catch (Exception e) {
                // Table yoxdur, davam et
            }

            // 2. Specific error elements
            List<By> errorSelectors = Arrays.asList(
                    By.xpath("//*[contains(text(), 'Error')]"),
                    By.xpath("//*[contains(text(), '404')]"),
                    By.xpath("//*[contains(text(), '500')]"),
                    By. cssSelector("[class*='error-page']"),
                    By.cssSelector("[class*='error-message']")
            );

            for (By selector : errorSelectors) {
                try {
                    WebElement errorElement = driver.findElement(selector);
                    if (errorElement.isDisplayed()) {
                        String errorText = errorElement.getText().toLowerCase();
                        System.out.println("⚠️ Error element detected: " + errorText);
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }

            // 3. Page title
            String title = driver.getTitle().toLowerCase();
            if (title.contains("error") || title.contains("not found") || title.contains("404")) {
                System.out. println("⚠️ Error in title: " + title);
                return true;
            }

            // 4. Body text - error keywords
            WebElement body = driver.findElement(By.tagName("body"));
            String pageText = body.getText().toLowerCase();

            List<String> errorKeywords = Arrays.asList(
                    "something went wrong",
                    "error occurred",
                    "page not found",
                    "internal server error",
                    "cannot find",
                    "oops"
            );

            for (String keyword : errorKeywords) {
                if (pageText.contains(keyword)) {
                    System. out.println("⚠️ Error keyword detected: '" + keyword + "'");
                    return true;
                }
            }

            // 5. Content çox az
            if (pageText.length() < 100) {
                System.out. println("⚠️ Content ÇOX azdır: " + pageText.length() + " simvol");
                return true;
            }

            return false;

        } catch (Exception e) {
            System.err.println("⚠️ Error page detection failed: " + e.getMessage());
            return false;
        }
    }

    private boolean checkDocumentsTable(String countryName) {
        try {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight / 2);");
            sleep(800);

            WebElement table;
            try {
                table = new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(ExpectedConditions.presenceOfElementLocated(documentsTable));
                System.out.println("📋 Documents table tapıldı");
            } catch (Exception e) {
                System. out.println("⚠️ Documents table tapılmadı");

                // "No visa required" yoxla
                try {
                    WebElement body = driver.findElement(By.tagName("body"));
                    String pageText = body.getText().toLowerCase();

                    if (pageText.contains("no visa required") ||
                            pageText.contains("visa-free") ||
                            pageText.contains("not required") ||
                            pageText.contains("visa is not required")) {
                        System.out.println("ℹ️ Bu ölkə üçün visa lazım deyil");
                        return true;
                    }
                } catch (Exception ignored) {
                }

                missingDocuments.add(countryName + ": Documents table tapılmadı");
                return false;
            }

            List<WebElement> rows = driver.findElements(documentRows);
            System.out.println("📄 Tapılan sənəd sayı: " + rows.size());

            if (rows.size() != REQUIRED_DOCUMENTS.size()) {
                System.out.println("⚠️ XƏBƏRDARLIQ:  Gözlənilən " + REQUIRED_DOCUMENTS.size() + " sənəd, faktiki " + rows.size());
            }

            // Faktiki sənədləri çıxar
            List<String> foundDocuments = new ArrayList<>();
            for (WebElement row : rows) {
                try {
                    WebElement docCell = row.findElement(By.cssSelector("td[data-slot='table-cell']"));
                    String docName = docCell.getText()
                            .trim()
                            .replaceAll("\\s+", " ");

                    if (! docName.isEmpty()) {
                        foundDocuments.add(docName);
                        System.out.println("   📄 " + docName);
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Sənəd adı oxuna bilmədi");
                }
            }

            // Əksik sənədləri yoxla - CASE INSENSITIVE
            List<String> missing = new ArrayList<>();
            for (String requiredDoc : REQUIRED_DOCUMENTS) {
                boolean found = false;

                for (String foundDoc : foundDocuments) {
                    if (foundDoc.equalsIgnoreCase(requiredDoc)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    missing.add(requiredDoc);
                }
            }

            if (!missing.isEmpty()) {
                String missingInfo = countryName + ": " + String.join(", ", missing);
                missingDocuments.add(missingInfo);
                System.out. println("❌ Əksik sənədlər:");
                for (String doc : missing) {
                    System.out.println("   - " + doc);
                }
                return false;
            }

            return true;

        } catch (Exception e) {
            System.err.println("❌ Documents table yoxlama xətası: " + e.getMessage());
            missingDocuments.add(countryName + ": Exception - " + e.getMessage());
            return false;
        }
    }

    // ✅ YENİLƏNMİŞ NEXT BUTTON - Direct URL navigation
    private void clickNextButton() {
        try {
            System.out.println("\n🔄 Növbəti səhifəyə keçid...");

            int currentPage = getCurrentPageNumber();
            int nextPageNum = currentPage + 1;

            System.out.println("   📍 Cari səhifə: " + currentPage + ", Növbəti: " + nextPageNum);

            // ✅ Direct URL navigation
            String nextUrl = VISA_SUPPORT_BASE + "?page=" + nextPageNum;
            System.out.println("   🔗 URL: " + nextUrl);

            driver.get(nextUrl);
            waitForPageReady();
            sleep(1500);

            // Scroll to top
            js.executeScript("window.scrollTo(0, 0);");
            sleep(500);

            // Verify
            String afterUrl = driver.getCurrentUrl();
            int afterPage = getCurrentPageNumber();

            if (afterUrl.contains("?page=" + nextPageNum) || afterPage == nextPageNum) {
                System. out.println("✅ Növbəti səhifə açıldı\n");
            } else {
                System.err.println("❌ Səhifə keçidi uğursuz!  URL: " + afterUrl + "\n");
            }

        } catch (Exception e) {
            System.err.println("❌ Next button click xətası: " + e.getMessage());
        }
    }

    // ✅ Current page number
    private int getCurrentPageNumber() {
        try {
            // 1. URL-dən oxu
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl. contains("?page=")) {
                String[] parts = currentUrl.split("\\?page=");
                try {
                    return Integer.parseInt(parts[1]. split("&")[0]);
                } catch (Exception e) {
                }
            }

            // 2. Active pagination button
            try {
                WebElement activePage = driver.findElement(
                        By.cssSelector("a[data-slot='pagination-link'][data-active='true']")
                );
                String pageText = activePage.getText().trim();
                if (pageText. matches("\\d+")) {
                    return Integer.parseInt(pageText);
                }
            } catch (Exception e) {
            }

            // 3. Default
            return 1;

        } catch (Exception e) {
            return 1;
        }
    }

    private String getCountryName(WebElement card) {
        try {
            try {
                WebElement span = card.findElement(By. cssSelector("span. text-md"));
                String name = span.getText().trim();
                if (! name.isEmpty()) {
                    return name;
                }
            } catch (Exception ignored) {
            }

            try {
                WebElement img = card.findElement(By. tagName("img"));
                String alt = img.getAttribute("alt");
                if (alt != null && !alt.isEmpty()) {
                    return alt. replace(" flag", "").trim();
                }
            } catch (Exception ignored) {
            }

            try {
                String href = card.getAttribute("href");
                if (href != null && href.contains("/study-visa-support-in-turkey/")) {
                    String[] parts = href.split("/");
                    String countrySlug = parts[parts.length - 1];
                    return countrySlug.substring(0, 1).toUpperCase() + countrySlug.substring(1).replace("-", " ");
                }
            } catch (Exception ignored) {
            }

            return "Unknown Country";
        } catch (Exception e) {
            return "Unknown Country";
        }
    }

    // ==================== HELPER METHODS ====================

    private void acceptCookies() {
        try {
            System.out.println("🍪 Cookie qəbul edilir.. .");
            WebElement cookieBtn = wait.until(ExpectedConditions. elementToBeClickable(cookieAcceptButton));
            clickElement(cookieBtn);
            sleep(500);
            System.out.println("✅ Cookie qəbul edildi\n");
        } catch (Exception e) {
            System.out.println("ℹ️ Cookie popup tapılmadı\n");
        }
    }

    private void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", element);
        sleep(400);
    }

    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            js.executeScript("arguments[0]. click();", element);
        }
    }

    private void waitForPageReady() {
        wait.until(driver1 ->
                js.executeScript("return document.readyState").toString().equals("complete"));
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    // ==================== PRINT METHODS ====================

    private void printHeader() {
        System.out.println("\n" + "█".repeat(70));
        System.out.println("█" + " ". repeat(68) + "█");
        System.out.println("█  🛂 VISA SUPPORT TEST - 193 COUNTRIES                           █");
        System.out. println("█" + " ".repeat(68) + "█");
        System.out.println("█". repeat(70) + "\n");
    }

    private void printSummary() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("📊 YEKUN NƏTİCƏ");
        System.out.println("═".repeat(70));
        System.out.println("   🌍 Toplam test edilən ölkə:  " + totalCountries);
        System.out.println("   ✅ Uğurlu (bütün sənədlər mövcud): " + successfulTests);
        System.out.println("   ❌ Uğursuz (əksik sənədlər/xəta): " + failedTests);

        double successRate = totalCountries > 0 ? (successfulTests * 100.0 / totalCountries) : 0;
        System.out.println("   📈 Uğur nisbəti: " + String.format("%.2f", successRate) + "%");

        if (! pageErrors.isEmpty()) {
            System.out.println("\n" + "─".repeat(70));
            System.out.println("🔴 SƏHİFƏ AÇILMAYAN ÖLKƏLƏR:");
            System.out.println("─".repeat(70));
            for (String error : pageErrors) {
                System.out. println("   " + error);
            }
        }

        if (!missingDocuments.isEmpty()) {
            System.out.println("\n" + "─".repeat(70));
            System.out.println("⚠️ ƏKSİK SƏNƏDLƏR OLAN ÖLKƏLƏR:");
            System.out.println("─".repeat(70));
            for (String missing : missingDocuments) {
                System.out. println("   " + missing);
            }
        }

        System.out.println("\n" + "█".repeat(70));
        System.out.println("█  ✅ TEST TAMAMLANDI!                                                  █");
        System.out. println("█".repeat(70) + "\n");
    }

    private void quit() {
        if (driver != null) {
            System.out.println("🔚 Browser bağlanır.. .");
            driver.quit();
        }
    }
}