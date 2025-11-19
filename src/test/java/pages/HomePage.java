package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.ElementLocator.LocatorStrategy;

/**
 * StudyLeo Ana Səhifə (Home Page) - Page Object Model
 * URL: https://studyleo.com/
 */
public class HomePage extends BasePage {
    
    // URL
    private static final String HOME_URL = "https://studyleo.com/";
    
    // Menu Button Locator-ları - CSS Selector və XPath fallback
    private static final String MENU_NAV_CSS = "header nav.navigation ul li";
    
    public HomePage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Ana səhifəyə get
     */
    public void open() {
        navigateTo(HOME_URL);
    }
    
    /**
     * Menu buttonunu index-ə görə tap və tıkla
     * @param index Button index-i (1-dən başlayır)
     * @return true əgər uğurlu olsa
     */
    public boolean clickMenuButton(int index) {
        try {
            // CSS Selector ilə əvvəl cəhd et
            LocatorStrategy cssStrategy = LocatorStrategy.byCss(
                MENU_NAV_CSS + ":nth-child(" + index + ") a",
                "Menu Button " + index + " (CSS)"
            );
            
            // Fallback XPath
            LocatorStrategy xpathStrategy = LocatorStrategy.byXPath(
                "/html/body/header/div/nav/div/ul/li[" + index + "]/a",
                "Menu Button " + index + " (XPath)"
            );
            
            WebElement button = elementLocator.findClickableElement(cssStrategy, xpathStrategy);
            scrollToElement(button);
            clickWithJS(button);
            waitHelper.waitMedium();
            
            return true;
        } catch (Exception e) {
            System.err.println("   ❌ Menu button " + index + " tıklanamadı: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Menu buttonunu ada görə tap və tıkla
     * @param buttonName Button adı (məs: "Universities", "Programs")
     * @return true əgər uğurlu olsa
     */
    public boolean clickMenuButtonByName(String buttonName) {
        try {
            // Link text ilə
            LocatorStrategy linkStrategy = LocatorStrategy.byLinkText(
                buttonName,
                "Menu Button: " + buttonName
            );
            
            // Partial link text fallback
            LocatorStrategy partialLinkStrategy = LocatorStrategy.byPartialLinkText(
                buttonName,
                "Menu Button (Partial): " + buttonName
            );
            
            // CSS selector fallback
            LocatorStrategy cssStrategy = LocatorStrategy.byCss(
                "header nav a[href*='" + buttonName.toLowerCase() + "']",
                "Menu Button CSS: " + buttonName
            );
            
            WebElement button = elementLocator.findClickableElement(
                linkStrategy, partialLinkStrategy, cssStrategy
            );
            
            scrollToElement(button);
            clickWithJS(button);
            waitHelper.waitMedium();
            
            return true;
        } catch (Exception e) {
            System.err.println("   ❌ Menu button '" + buttonName + "' tıklanamadı: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ana səhifədəyik?
     */
    public boolean isHomePage() {
        String currentUrl = getCurrentUrl();
        return currentUrl.equals(HOME_URL) || currentUrl.equals("https://studyleo.com");
    }
    
    /**
     * Səhifənin düzgün yükləndiyini yoxla
     */
    public boolean verifyPageLoaded() {
        try {
            // Səhifə yüklənmə
            waitHelper.waitForPageLoad();
            
            // Header mövcuddur
            LocatorStrategy headerStrategy = LocatorStrategy.byCss(
                "header",
                "Header element"
            );
            
            elementLocator.findElement(headerStrategy);
            
            // Navigation menu mövcuddur
            LocatorStrategy navStrategy = LocatorStrategy.byCss(
                "header nav",
                "Navigation menu"
            );
            
            elementLocator.findElement(navStrategy);
            
            return true;
        } catch (Exception e) {
            System.err.println("   ❌ Ana səhifə düzgün yüklənmədi: " + e.getMessage());
            return false;
        }
    }
}
