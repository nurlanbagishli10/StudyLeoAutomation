package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import config.TestConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;
import pages.HomePage;
import pages.UniversityListPage;
import pages.UniversityDetailPage;
import utils.ExtentReportManager;
import utils.WaitHelper;

/**
 * ✅ TƏKMİLLƏŞDİRİLMİŞ Universitet Testləri
 * 
 * Yeniliklər:
 * - Page Object Model pattern istifadəsi
 * - Multiple locator strategiyaları (CSS, XPath, Relative XPath)
 * - Daha robust element tapma mexanizmi
 * - Better error handling və reporting
 * - Code maintainability və reusability
 */
public class ImprovedUniversityTest {

    WebDriver driver;
    WaitHelper waitHelper;
    
    // Page Objects
    HomePage homePage;
    UniversityListPage universityListPage;
    UniversityDetailPage universityDetailPage;
    
    // ExtentReports
    private static ExtentReports extent;
    private ExtentTest suiteTest;
    private ExtentTest pageTest;
    private ExtentTest universityTest;

    @BeforeSuite
    public void setupSuite() {
        extent = ExtentReportManager.createInstance();
        suiteTest = extent.createTest("✅ Təkmilləşdirilmiş Universitet Testləri",
                "Page Object Model və multiple locator strategiyaları ilə");
    }

    @BeforeTest
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        boolean headless = TestConfig.isHeadless();

        options.addArguments("--disable-extensions");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");

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
        waitHelper = new WaitHelper(driver);
        
        // Page Object-ləri başlat
        homePage = new HomePage(driver);
        universityListPage = new UniversityListPage(driver);
        universityDetailPage = new UniversityDetailPage(driver);

        if (!headless) {
            driver.manage().window().maximize();
        }

        suiteTest.pass("<span style='color: #ffffff !important;'>✅ Browser və Page Objects uğurla başladıldı</span>");
    }

    @Test(priority = 1, description = "Ana səhifə menu buttonlarını test et")
    public void testHomePageMenuButtons() {
        pageTest = suiteTest.createNode("🏠 Ana Səhifə Menu Testləri",
                "POM və multiple locator strategiyaları ilə");
        
        System.out.println("\n" + "🎯".repeat(30));
        System.out.println("ANA SƏHIFƏ MENU TESTİ BAŞLADI (POM)");
        System.out.println("🎯".repeat(30) + "\n");

        // Ana səhifəyə get
        homePage.open();
        pageTest.info("<span style='color: #ffffff !important;'>🌐 Ana səhifə açıldı</span>");

        // Səhifə yüklənməsini yoxla
        if (!homePage.verifyPageLoaded()) {
            pageTest.fail("<span style='color: #ffffff !important;'>❌ Ana səhifə düzgün yüklənmədi</span>");
            return;
        }

        // Menu buttonlarını test et
        String[] menuButtons = {"Universities", "Programs", "Blogs", "Visa Support", "About", "Contact"};
        int successCount = 0;
        int errorCount = 0;

        for (int i = 0; i < menuButtons.length; i++) {
            String buttonName = menuButtons[i];
            ExtentTest buttonTest = pageTest.createNode("🔘 " + buttonName + " Menu Button",
                    "Multiple locator strategiyası ilə");

            try {
                System.out.println("🔍 Menu Button " + (i + 1) + "/" + menuButtons.length + ": " + buttonName);
                buttonTest.info("<span style='color: #ffffff !important;'>🔍 Test başladı</span>");

                String beforeUrl = homePage.getCurrentUrl();

                // POM ilə button-a tıkla
                if (homePage.clickMenuButtonByName(buttonName)) {
                    waitHelper.waitForUrlChange(beforeUrl);
                    String afterUrl = homePage.getCurrentUrl();

                    buttonTest.info("<span style='color: #ffffff !important;'>📍 Yeni URL: <a href='" + 
                                  afterUrl + "' target='_blank' style='color: #3498db !important;'>" + 
                                  afterUrl + "</a></span>");

                    System.out.println("   ✅ Uğurlu: " + afterUrl);
                    buttonTest.pass("<span style='color: #ffffff !important;'>✅ TEST UĞURLU</span>");
                    successCount++;
                } else {
                    System.out.println("   ❌ Uğursuz");
                    buttonTest.fail("<span style='color: #ffffff !important;'>❌ Button tıklanamadı</span>");
                    errorCount++;
                }

            } catch (Exception e) {
                errorCount++;
                System.out.println("   ❌ Xəta: " + e.getMessage());
                ExtentReportManager.logFailWithDetails(buttonTest, "Xəta baş verdi", e.getMessage());
            }
        }

        // Nəticə
        double successRate = menuButtons.length > 0 ? (successCount * 100.0 / menuButtons.length) : 0;
        System.out.println("\n📊 Ana Səhifə Nəticəsi: " + successCount + "/" + menuButtons.length + 
                         " (" + String.format("%.1f", successRate) + "%)");

        String summary = String.format(
            "<div style='background: #34495e; padding: 15px; border-radius: 8px; margin: 10px 0;'>" +
            "<h4 style='color: #ffffff !important;'>📊 Test Nəticəsi</h4>" +
            "<table style='width: 100%%;'>" +
            "<tr><td style='color: #ffffff !important;'>✅ Uğurlu:</td><td style='color: #2ecc71 !important;'><strong>%d/%d</strong></td></tr>" +
            "<tr><td style='color: #ffffff !important;'>❌ Xətalı:</td><td style='color: #e74c3c !important;'><strong>%d</strong></td></tr>" +
            "<tr><td style='color: #ffffff !important;'>📈 Uğur faizi:</td><td style='color: #ffffff !important;'><strong>%.1f%%</strong></td></tr>" +
            "</table></div>",
            successCount, menuButtons.length, errorCount, successRate
        );

        pageTest.info(summary);
    }

    @Test(priority = 2, description = "Universitet səhifələrini və button-ları test et (POM)")
    public void testUniversityListAndDetailPages() {
        pageTest = suiteTest.createNode("🎓 Universitet Səhifələri Testləri (POM)",
                "Page Object Model və robust locator-larla");
        
        System.out.println("\n" + "🎯".repeat(30));
        System.out.println("UNİVERSİTET SƏHİFƏLƏRİ TESTİ (POM)");
        System.out.println("🎯".repeat(30) + "\n");

        // Test data - səhifə, URL, universitet sayı
        Object[][] testData = {
            {1, "https://studyleo.com/en/universities", 3},  // İlk 3 universitet
            {2, "https://studyleo.com/en/universities?page=2", 3}  // İkinci səhifədən 3 universitet
        };

        int totalSuccess = 0;
        int totalError = 0;

        for (Object[] data : testData) {
            int pageNumber = (int) data[0];
            String url = (String) data[1];
            int universityCount = (int) data[2];

            ExtentTest pageNode = pageTest.createNode("📄 Səhifə " + pageNumber,
                    "Səhifə " + pageNumber + "-dəki universitetlərin testi");

            System.out.println("\n📄 SƏHIFƏ " + pageNumber + " TESTİ");

            // Universitet list səhifəsinə get
            universityListPage.openPage(pageNumber);
            pageNode.info("<span style='color: #ffffff !important;'>🔗 URL: " + url + "</span>");

            // Səhifə yüklənməsini yoxla
            if (!universityListPage.verifyPageLoaded()) {
                pageNode.fail("<span style='color: #ffffff !important;'>❌ Səhifə yüklənmədi</span>");
                totalError += universityCount;
                continue;
            }

            // Universitetləri test et
            for (int i = 1; i <= universityCount; i++) {
                String universityName = universityListPage.getUniversityName(pageNumber, i);
                ExtentTest uniTest = pageNode.createNode("🏛️ " + universityName,
                        "Universitet detail və button testləri");

                try {
                    System.out.println("\n   🏛️  " + universityName);

                    // Universitet səhifəsinə get
                    if (universityListPage.clickUniversityCard(pageNumber, i)) {
                        waitHelper.waitForPageLoad();
                        waitHelper.waitLong();

                        String detailUrl = universityDetailPage.getCurrentUrl();
                        uniTest.info("<span style='color: #ffffff !important;'>🔗 URL: <a href='" + 
                                   detailUrl + "' target='_blank' style='color: #3498db !important;'>" + 
                                   detailUrl + "</a></span>");

                        // Səhifə yüklənməsini yoxla
                        if (universityDetailPage.verifyPageLoaded()) {
                            System.out.println("      ✅ Universitet səhifəsi açıldı");
                            uniTest.pass("<span style='color: #ffffff !important;'>✅ Səhifə yükləndi</span>");
                            totalSuccess++;
                        } else {
                            System.out.println("      ❌ Səhifə düzgün yüklənmədi");
                            uniTest.fail("<span style='color: #ffffff !important;'>❌ Səhifə yüklənmədi</span>");
                            totalError++;
                        }

                        // Geri qayıt
                        universityListPage.openPage(pageNumber);
                        waitHelper.waitMedium();

                    } else {
                        System.out.println("      ❌ Universitet kartı tıklanamadı");
                        uniTest.fail("<span style='color: #ffffff !important;'>❌ Tıklama xətası</span>");
                        totalError++;
                    }

                } catch (Exception e) {
                    totalError++;
                    System.err.println("      ❌ Xəta: " + e.getMessage());
                    ExtentReportManager.logFailWithDetails(uniTest, "Test xətası", e.getMessage());
                }
            }
        }

        // Ümumi nəticə
        int total = totalSuccess + totalError;
        double successRate = total > 0 ? (totalSuccess * 100.0 / total) : 0;
        System.out.println("\n📊 Ümumi Nəticə: " + totalSuccess + "/" + total + 
                         " (" + String.format("%.1f", successRate) + "%)");

        String summary = String.format(
            "<div style='background: #34495e; padding: 15px; border-radius: 8px; margin: 10px 0;'>" +
            "<h4 style='color: #ffffff !important;'>📊 Ümumi Nəticə</h4>" +
            "<table style='width: 100%%;'>" +
            "<tr><td style='color: #ffffff !important;'>✅ Uğurlu:</td><td style='color: #2ecc71 !important;'><strong>%d</strong></td></tr>" +
            "<tr><td style='color: #ffffff !important;'>❌ Xətalı:</td><td style='color: #e74c3c !important;'><strong>%d</strong></td></tr>" +
            "<tr><td style='color: #ffffff !important;'>📈 Uğur faizi:</td><td style='color: #ffffff !important;'><strong>%.1f%%</strong></td></tr>" +
            "</table></div>",
            totalSuccess, totalError, successRate
        );

        pageTest.info(summary);
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
        System.out.println("   ✅ Page Object Model istifadə edildi");
        System.out.println("   ✅ Multiple locator strategiyaları tətbiq edildi");
        System.out.println("   ✅ Robust element tapma mexanizmi");
        ExtentReportManager.flush();
    }
}
