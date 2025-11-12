package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import utils.ExtentReportManager;

import java.time.Duration;
import java.util.List;

public class universities {

    WebDriver driver;
    JavascriptExecutor js;
    WebDriverWait wait;

    // ExtentReports
    private static ExtentReports extent;
    private ExtentTest suiteTest;
    private ExtentTest pageTest;
    private ExtentTest universityTest;

    @BeforeSuite
    public void setupSuite() {
        extent = ExtentReportManager.createInstance();
        suiteTest = extent.createTest("🎓 StudyLeo Universitet Testləri", "Bütün universitet səhifələrinin test edilməsi");
    }

    @BeforeTest
    public void setup() {
        // Headless mode
        ChromeOptions options = new ChromeOptions();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));

        if (headless) {
            System.out.println("🚀 HEADLESS MODE aktivdir\n");
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            suiteTest.info("<span style='color: #ffffff !important;'>Browser: Chrome Headless Mode</span>");
        } else {
            System.out.println("🖥️ NORMAL MODE aktivdir\n");
            suiteTest.info("<span style='color: #ffffff !important;'>Browser: Chrome Normal Mode</span>");
        }

        driver = new ChromeDriver(options);
        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://studyleo.com/en/universities");

        if (!headless) {
            driver.manage().window().maximize();
        }

        suiteTest.pass("<span style='color: #ffffff !important;'>✅ Browser uğurla başladıldı və səhifə açıldı</span>");
    }

    @Test(priority = 1)
    public void universitiesPage1() {
        pageTest = suiteTest.createNode("📄 Səhifə 1", "Səhifə 1-də olan universitetlərin testi");
        testUniversitiesPage(1, "https://studyleo.com/en/universities", 12);
    }

    @Test(priority = 2)
    public void universitiesPage2() {
        pageTest = suiteTest.createNode("📄 Səhifə 2", "Səhifə 2-də olan universitetlərin testi");
        testUniversitiesPage(2, "https://studyleo.com/en/universities?page=2", 12);
    }

    @Test(priority = 3)
    public void universitiesPage3() {
        pageTest = suiteTest.createNode("📄 Səhifə 3", "Səhifə 3-də olan universitetlərin testi");
        testUniversitiesPage(3, "https://studyleo.com/en/universities?page=3", 12);
    }

    @Test(priority = 4)
    public void universitiesPage4() {
        pageTest = suiteTest.createNode("📄 Səhifə 4", "Səhifə 4-də olan universitetlərin testi");
        testUniversitiesPage(4, "https://studyleo.com/en/universities?page=4", 5);
    }

    private void testUniversitiesPage(int pageNumber, String url, int universityCount) {
        System.out.println("\n" + "🎯".repeat(30));
        System.out.println("SƏHIFƏ " + pageNumber + " TESTİ BAŞLADI");
        System.out.println("🎯".repeat(30) + "\n");

        pageTest.info("<span style='color: #ffffff !important;'>🔗 URL: " + url + "</span>");
        pageTest.info("<span style='color: #ffffff !important;'>📊 Gözlənilən universitet sayı: " + universityCount + "</span>");

        if (pageNumber > 1) {
            driver.get(url);
        }

        js.executeScript("window.scrollBy(0, 300)");
        waitFor(2000);

        int successCount = 0;
        int errorCount = 0;
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= universityCount; i++) {
            universityTest = pageTest.createNode("🏛️ Universitet " + i, "Universitet " + i + " səhifəsinin yoxlanması");

            try {
                String xpath = pageNumber == 1
                        ? "/html/body/div[3]/section/div/div/div[1]/div[" + i + "]/a/div[1]"
                        : "/html/body/div[3]/section/div/div/div[1]/div[" + i + "]/a";

                System.out.println("🔍 Səhifə-" + pageNumber + ", Universitet " + i + "/" + universityCount);
                universityTest.info("<span style='color: #ffffff !important;'>🔍 Test başladı: Universitet " + i + "/" + universityCount + "</span>");

                String beforeUrl = driver.getCurrentUrl();

                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                js.executeScript("arguments[0].click();", element);
                universityTest.pass("<span style='color: #ffffff !important;'>✅ Universitet elementinə tıklandı</span>");

                wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(beforeUrl)));
                waitFor(2500);

                String afterUrl = driver.getCurrentUrl();
                System.out.println("   📍 URL: " + afterUrl);
                universityTest.info("<span style='color: #ffffff !important;'>📍 Yeni URL: <a href='" + afterUrl + "' target='_blank' style='color: #3498db !important;'>" + afterUrl + "</a></span>");

                if (checkPageLoaded(universityTest)) {
                    System.out.println("   ✅ UĞURLU\n");
                    ExtentReportManager.logPass(universityTest, "Səhifə uğurla açıldı və yükləndi");
                    universityTest.pass("<span style='color: #ffffff !important;'>✅ TEST UĞURLU</span>");
                    successCount++;
                } else {
                    System.out.println("   ❌ UĞURSUZ\n");
                    universityTest.fail("<span style='color: #ffffff !important;'>❌ TEST UĞURSUZ</span>");
                    errorCount++;
                }

                driver.navigate().back();
                universityTest.info("<span style='color: #ffffff !important;'>🔙 Ana səhifəyə qayıdıldı</span>");
                wait.until(ExpectedConditions.urlToBe(beforeUrl));
                waitFor(800);
                js.executeScript("window.scrollBy(0, 50)");

            } catch (org.openqa.selenium.TimeoutException e) {
                errorCount++;
                System.out.println("   ❌ TIMEOUT XƏTASI\n");

                String errorDetails = "XPath: " + (pageNumber == 1
                        ? "/html/body/div[3]/section/div/div/div[1]/div[" + i + "]/a/div[1]"
                        : "/html/body/div[3]/section/div/div/div[1]/div[" + i + "]/a") + "\n\n" +
                        "Xəta: " + e.getMessage() + "\n\n" +
                        "Stack Trace:\n" + getStackTraceString(e);

                ExtentReportManager.logFailWithDetails(
                        universityTest,
                        "Timeout xətası: Element tapılmadı və ya tıklanamadı",
                        errorDetails
                );

            } catch (org.openqa.selenium.NoSuchElementException e) {
                errorCount++;
                System.out.println("   ❌ ELEMENT TAPILMADI\n");

                String errorDetails = "Səbəb: Universitet elementi səhifədə yoxdur\n\n" +
                        "Xəta mesajı: " + e.getMessage() + "\n\n" +
                        "Stack Trace:\n" + getStackTraceString(e);

                ExtentReportManager.logFailWithDetails(
                        universityTest,
                        "Element tapılmadı",
                        errorDetails
                );

            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                errorCount++;
                System.out.println("   ❌ ELEMENT TIKLANMADI\n");

                String errorDetails = "Səbəb: Başqa element tıklamanı bloklayır\n\n" +
                        "Xəta mesajı: " + e.getMessage() + "\n\n" +
                        "Stack Trace:\n" + getStackTraceString(e);

                ExtentReportManager.logFailWithDetails(
                        universityTest,
                        "Element tıklanamadı (Click Intercepted)",
                        errorDetails
                );

            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                errorCount++;
                System.out.println("   ❌ STALE ELEMENT\n");

                String errorDetails = "Səbəb: Element DOM-da dəyişdi\n" +
                        "Tövsiyə: Səhifə yenidən yükləndi, element yenidən tapılmalıdır\n\n" +
                        "Xəta mesajı: " + e.getMessage() + "\n\n" +
                        "Stack Trace:\n" + getStackTraceString(e);

                ExtentReportManager.logFailWithDetails(
                        universityTest,
                        "Stale Element xətası",
                        errorDetails
                );

            } catch (Exception e) {
                errorCount++;
                System.out.println("   ❌ ÜMUMI XƏTA\n");

                String errorDetails = "Xəta tipi: " + e.getClass().getSimpleName() + "\n\n" +
                        "Mesaj: " + e.getMessage() + "\n\n" +
                        "Stack Trace:\n" + getStackTraceString(e);

                ExtentReportManager.logFailWithDetails(
                        universityTest,
                        "Ümumi xəta baş verdi",
                        errorDetails
                );
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        double successRate = universityCount > 0 ? (successCount * 100.0 / universityCount) : 0;

        // Konsol nəticələri
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📊 SƏHIFƏ " + pageNumber + " NƏTİCƏSİ");
        System.out.println("=".repeat(70));
        System.out.println("   📌 Ümumi:        " + universityCount + " universitet");
        System.out.println("   ✅ Uğurlu:       " + successCount + " universitet");
        System.out.println("   ❌ Xətalı:       " + errorCount + " universitet");
        System.out.println("   📈 Uğur faizi:   " + String.format("%.1f", successRate) + "%");
        System.out.println("   ⏱️  Müddət:       " + duration + " saniyə");
        System.out.println("=".repeat(70) + "\n");

        // ExtentReport nəticələri - RƏNGLƏR AĞ
        String summary = String.format(
                "<div style='background: #34495e; padding: 15px; border-radius: 8px; margin: 10px 0;'>" +
                        "<h4 style='color: #ffffff !important; margin: 0 0 10px 0;'>📊 Səhifə %d Nəticəsi</h4>" +
                        "<table style='width: 100%%;'>" +
                        "<tr><td style='color: #ffffff !important;'>📌 Ümumi:</td><td style='color: #ffffff !important;'><strong>%d universitet</strong></td></tr>" +
                        "<tr><td style='color: #ffffff !important;'>✅ Uğurlu:</td><td><strong style='color: #2ecc71 !important;'>%d universitet</strong></td></tr>" +
                        "<tr><td style='color: #ffffff !important;'>❌ Xətalı:</td><td><strong style='color: #e74c3c !important;'>%d universitet</strong></td></tr>" +
                        "<tr><td style='color: #ffffff !important;'>📈 Uğur faizi:</td><td style='color: #ffffff !important;'><strong>%.1f%%</strong></td></tr>" +
                        "<tr><td style='color: #ffffff !important;'>⏱️ Müddət:</td><td style='color: #ffffff !important;'><strong>%d saniyə</strong></td></tr>" +
                        "<tr><td style='color: #ffffff !important;'>⚡ Orta sürət:</td><td style='color: #ffffff !important;'><strong>%.1f s/universitet</strong></td></tr>" +
                        "</table></div>",
                pageNumber, universityCount, successCount, errorCount, successRate, duration,
                universityCount > 0 ? (double)duration / universityCount : 0
        );

        pageTest.info(summary);

        if (successRate >= 90) {
            pageTest.pass("<span style='color: #ffffff !important;'>🎉 Səhifə " + pageNumber + " - Əla nəticə! Uğur faizi: " + String.format("%.1f", successRate) + "%</span>");
        } else if (successRate >= 70) {
            pageTest.warning("<span style='color: #ffffff !important;'>⚠️ Səhifə " + pageNumber + " - Yaxşı, amma təkmilləşdirilə bilər. Uğur faizi: " + String.format("%.1f", successRate) + "%</span>");
        } else {
            pageTest.fail("<span style='color: #ffffff !important;'>❌ Səhifə " + pageNumber + " - Ciddi problemlər var! Uğur faizi: " + String.format("%.1f", successRate) + "%</span>");
        }
    }

    private boolean checkPageLoaded(ExtentTest test) {
        try {
            // 1. Xəta mesajı
            List<WebElement> errorElements = driver.findElements(
                    By.xpath("//div[contains(@class, 'error') or contains(@class, 'alert')]" +
                            "//*[contains(text(), 'Something went wrong')]")
            );

            for (WebElement element : errorElements) {
                if (element.isDisplayed()) {
                    test.fail("<span style='color: #ffffff !important;'>❌ 'Something went wrong' mesajı görünür</span>");
                    return false;
                }
            }

            // 2. Başlıq
            String title = driver.getTitle();
            if (title == null || title.isEmpty() || title.equalsIgnoreCase("StudyLeo")) {
                test.fail("<span style='color: #ffffff !important;'>❌ Səhifə başlığı düzgün deyil: " + title + "</span>");
                return false;
            }
            test.pass("<span style='color: #ffffff !important;'>✅ Başlıq düzgündür: " + title + "</span>");

            // 3. URL
            String url = driver.getCurrentUrl();
            if (url.contains("/universities?page") ||
                    url.equals("https://studyleo.com/en/universities") ||
                    url.equals("https://studyleo.com/en/universities/")) {
                test.fail("<span style='color: #ffffff !important;'>❌ URL dəyişmədi, hələ list səhifəsindədir</span>");
                return false;
            }
            test.pass("<span style='color: #ffffff !important;'>✅ URL düzgündür (universitet səhifəsindədir)</span>");

            // 4. Məzmun
            boolean hasH1 = isElementVisible(By.tagName("h1"));
            boolean hasH2 = isElementVisible(By.tagName("h2"));
            boolean hasP = isElementVisible(By.tagName("p"));

            if (!hasH1 && !hasH2 && !hasP) {
                test.fail("<span style='color: #ffffff !important;'>❌ Səhifədə məzmun yoxdur</span>");
                return false;
            }
            test.pass("<span style='color: #ffffff !important;'>✅ Səhifədə məzmun mövcuddur (H1:" + hasH1 + ", H2:" + hasH2 + ", P:" + hasP + ")</span>");

            // 5. H1 mətni
            try {
                WebElement h1 = driver.findElement(By.tagName("h1"));
                String h1Text = h1.getText();
                if (h1Text != null && !h1Text.trim().isEmpty()) {
                    test.info("<span style='color: #ffffff !important;'>🏛️ Universitet: <strong>" + h1Text + "</strong></span>");
                }
            } catch (Exception e) {
                // İstisna
            }

            return true;

        } catch (Exception e) {
            test.fail("<span style='color: #ffffff !important;'>❌ Yoxlama zamanı xəta: " + e.getMessage() + "</span>");
            return false;
        }
    }

    private boolean isElementVisible(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private String getStackTraceString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            System.out.println("\n🔚 Browser bağlanır...\n");
            driver.quit();
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        System.out.println("\n🏁 Bütün testlər tamamlandı!");
        ExtentReportManager.flush();
    }

    private void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}