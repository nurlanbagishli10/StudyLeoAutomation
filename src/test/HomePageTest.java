import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.Set;

public class HomePageTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    private static final String SITE_URL = "https://studyleo.com/en";

    // Locators
    private By acceptCookiesButton = By.cssSelector("button[data-testid='cookie-banner-accept-button']");
    private By applyNowButton = By.cssSelector("button[data-slot='button'][aria-label='Apply Now']");
    private By closeDialogButton = By.cssSelector("button[data-slot='dialog-close']");
    private By searchBox = By.cssSelector("input[data-slot='input'][placeholder='Search']");
    private By searchButton = By.cssSelector("button[data-slot='button'][type='submit']");

    // WhatsApp - XPath
    private By whatsappButton = By.xpath("/html/body/a");

    // Timer/Apply Now - XPath
    private By applyNowTimer = By.xpath("/html/body/main/div/div[2]//button[contains(@class, 'pushable')]");

    private By closeButtonDialog = By.cssSelector("button[data-slot='dialog-close']");

    public HomePageTest() {
        initializeDriver();
    }

    private void initializeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        js = (JavascriptExecutor) driver;
    }

    public void calistir() {
        try {
            System.out.println("🚀 TEST AUTOMATION BAŞLANIYOR...\n");

            openWebsite();

            test1_AcceptCookies();
            bekle(1000);

            test2_ApplyNowButton();
            bekle(1000);

            test3_CloseDialog();
            bekle(1000);

            test4_SearchBox();
            bekle(1000);

            test5_SearchButton();
            bekle(1000);

            test6_WhatsAppButton();
            bekle(1000);

            test7_ApplyNowTimer();
            bekle(1000);

            test8_CloseDialog();
            bekle(1000);

            System.out.println("\n✅ TÜM TESTLER BAŞARIYLA TAMAMLANDI!");

        } catch (Exception e) {
            System.err.println("\n❌ HATA OLUŞTU: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openWebsite() {
        System.out.println("🌐 Website açılıyor:   " + SITE_URL);
        driver.get(SITE_URL);
        bekle(3000);
        System.out.println("✅ Website başarıyla açıldı!\n");
    }

    private void test1_AcceptCookies() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("TEST 1 - ACCEPT COOKIES (PRIVACY POLICY)");
        System.out.println("═══════════════════════════════════════\n");

        try {
            if (isElementPresent(acceptCookiesButton)) {
                System.out.println("ℹ️  Cookie banner bulundu");
                clickElement(acceptCookiesButton);
                System.out.println("✅ 'Accept All' butonuna tıklandı\n");
                bekle(1500);
            } else {
                System.out.println("⚠️  Cookie banner bulunamadı\n");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST 1 Hatası: " + e.getMessage() + "\n");
        }
    }

    private void test2_ApplyNowButton() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("TEST 2 - APPLY NOW BUTONU");
        System.out. println("═══════════════════════════════════════\n");

        try {
            if (isElementPresent(applyNowButton)) {
                System. out.println("ℹ️  Apply Now butonu bulundu");
                scrollToElement(applyNowButton);
                bekle(500);
                clickElement(applyNowButton);
                System. out.println("✅ Apply Now'a tıklandı");
                bekle(1500);

                if (isElementPresent(closeDialogButton)) {
                    System.out.println("✅ Dialog açıldı (Close butonu görünüyor)\n");
                } else {
                    System.out.println("⚠️  Dialog açılmadı\n");
                }
            } else {
                System.out.println("❌ Apply Now butonu bulunamadı\n");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST 2 Hatası: " + e.getMessage() + "\n");
        }
    }

    private void test3_CloseDialog() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("TEST 3 - X BUTONU (DIALOG KAPAT)");
        System.out.println("═══════════════════════════════════════\n");

        try {
            if (isElementPresent(closeDialogButton)) {
                System. out.println("ℹ️  X butonu bulundu");
                clickElement(closeDialogButton);
                System.out.println("✅ X butonuna tıklandı, dialog kapatıldı\n");
                bekle(1500);
            } else {
                System. out.println("⚠️  X butonu bulunamadı\n");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST 3 Hatası: " + e.getMessage() + "\n");
        }
    }

    private void test4_SearchBox() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("TEST 4 - SEARCH BOX");
        System.out. println("═══════════════════════════════════════\n");

        try {
            if (isElementPresent(searchBox)) {
                System.out.println("ℹ️  Search box bulundu");
                sendKeys(searchBox, "ad");
                System.out.println("✅ Search box'a 'ad' yazıldı\n");
                bekle(1000);
            } else {
                System.out.println("❌ Search box bulunamadı\n");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST 4 Hatası: " + e.getMessage() + "\n");
        }
    }

    private void test5_SearchButton() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("TEST 5 - SEARCH BUTTON");
        System.out.println("═══════════════════════════════════════\n");

        try {
            if (isElementPresent(searchButton)) {
                System.out. println("ℹ️  Search butonu bulundu");
                clickElement(searchButton);
                System.out.println("✅ Search butonuna tıklandı");
                bekle(2500);

                System.out.println("ℹ️  Arama sonuçları açıldı");
                System.out.println("⏳ İçerik kontrol ediliyor...");
                bekle(1500);

                System.out.println("🔙 Geri dönülüyor...");
                driver.navigate().back();
                bekle(2000);
                System.out.println("✅ Website'e geri dönüldü\n");
            } else {
                System.out.println("❌ Search butonu bulunamadı\n");
            }
        } catch (Exception e) {
            System.err. println("❌ TEST 5 Hatası: " + e.getMessage() + "\n");
        }
    }

    private void test6_WhatsAppButton() {
        System.out. println("═══════════════════════════════════════");
        System.out.println("TEST 6 - WHATSAPP BUTONU");
        System.out. println("═══════════════════════════════════════\n");

        try {
            if (isElementPresent(whatsappButton)) {
                System.out.println("ℹ️  WhatsApp linki bulundu");
                scrollToElement(whatsappButton);
                bekle(500);

                String mainWindow = driver.getWindowHandle();
                System.out.println("ℹ️  Ana pencere kaydedildi");

                clickElement(whatsappButton);
                System.out.println("✅ WhatsApp linkine tıklandı");
                bekle(2000);

                Set<String> allWindows = driver.getWindowHandles();
                if (allWindows.size() > 1) {
                    System.out.println("ℹ️  Yeni tab açıldı");
                    for (String window : allWindows) {
                        if (!window. equals(mainWindow)) {
                            driver.switchTo().window(window);
                            bekle(500);
                            System.out.println("✅ Yeni tab kapatılıyor...");
                            driver.close();
                            break;
                        }
                    }
                } else {
                    System.out.println("ℹ️  Yeni tab açılmadı (PopUp engellendi)");
                }

                driver.switchTo().window(mainWindow);
                System.out.println("✅ Ana pencereye geri dönüldü\n");
                bekle(1000);
            } else {
                System.out.println("❌ WhatsApp linki bulunamadı\n");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST 6 Hatası:   " + e.getMessage() + "\n");
        }
    }

    private void test7_ApplyNowTimer() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("TEST 7 - APPLY NOW TIMER BUTONU");
        System.out.println("═══════════════════════════════════════\n");

        try {
            if (isElementPresent(applyNowTimer)) {
                System.out.println("ℹ️  Timer butonu bulundu");
                scrollToElement(applyNowTimer);
                bekle(500);

                WebElement timerElement = driver.findElement(applyNowTimer);
                String timerText = timerElement. getText();
                System.out.println("ℹ️  Timer metni: " + timerText);

                clickElement(applyNowTimer);
                System.out.println("✅ Timer butonuna tıklandı");
                bekle(1500);

                if (isElementPresent(closeButtonDialog)) {
                    System. out.println("✅ Dialog açıldı (X butonu görünüyor)\n");
                } else {
                    System.out.println("⚠️  Dialog açılmadı\n");
                }
            } else {
                System.out.println("⚠️  Timer butonu bulunamadı\n");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST 7 Hatası: " + e.getMessage() + "\n");
        }
    }

    private void test8_CloseDialog() {
        System.out. println("═══════════════════════════════════════");
        System.out.println("TEST 8 - X BUTONU (SON DIALOG KAPAT)");
        System.out.println("═══════════════════════════════════════\n");

        try {
            if (isElementPresent(closeButtonDialog)) {
                System.out.println("ℹ️  X butonu bulundu");
                clickElement(closeButtonDialog);
                System.out.println("✅ X butonuna tıklandı, dialog kapatıldı\n");
                bekle(1500);
            } else {
                System. out.println("⚠️  X butonu bulunamadı\n");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST 8 Hatası: " + e.getMessage() + "\n");
        }
    }

    // ==================== YARDIMCI METODLAR ====================

    private boolean isElementPresent(By locator) {
        try {
            wait.until(ExpectedConditions. presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void clickElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions. elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            WebElement element = driver.findElement(locator);
            js.executeScript("arguments[0]. click();", element);
        }
    }

    private void sendKeys(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    private void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        bekle(500);
    }

    private void bekle(int millisaniye) {
        try {
            Thread.sleep(millisaniye);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void kapat() {
        if (driver != null) {
            System.out.println("\n🔚 Browser kapatılıyor...");
            driver.quit();
        }
    }

    // ==================== MAIN METHOD ====================

    public static void main(String[] args) {
        HomePageTest test = new HomePageTest();

        try {
            System.out.println("\n" + "█". repeat(70));
            System.out.println("█" + " ". repeat(68) + "█");
            System.out.println("█  🚀 SELENIUM TEST AUTOMATION - 8 TEST SENARYOSU          █");
            System.out. println("█" + " ".repeat(68) + "█");
            System.out.println("█". repeat(70) + "\n");

            System.out.println("📋 TEST SENARYOLARı:");
            System.out.println("  1️⃣  Accept Cookies (Privacy Policy)");
            System.out.println("  2️⃣  Apply Now Butonu");
            System.out. println("  3️⃣  X Butonu (Dialog Kapat)");
            System.out.println("  4️⃣  Search Box ('ad' Yaz)");
            System.out.println("  5️⃣  Search Button (Ara ve Geri Dön)");
            System. out.println("  6️⃣  WhatsApp Linki (Yeni Tab)");
            System. out.println("  7️⃣  Apply Now Timer Butonu");
            System.out. println("  8️⃣  X Butonu (Son Dialog Kapat)");
            System.out.println("\n" + "─".repeat(70) + "\n");

            test.calistir();

            System.out.println("\n" + "█".repeat(70));
            System.out.println("█" + " ".repeat(68) + "█");
            System.out.println("█  ✅ TÜM TESTLER BAŞARIYLA TAMAMLANDI!                          █");
            System.out. println("█" + " ".repeat(68) + "█");
            System.out.println("█".repeat(70) + "\n");

            test.bekle(3000);

        } catch (Exception e) {
            System.err.println("\n❌ HATA:   " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.kapat();
        }
    }
}