package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.ElementLocator.LocatorStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Universitet Detal Səhifəsi - Page Object Model
 * Universitet daxili button və məlumatlar
 */
public class UniversityDetailPage extends BasePage {
    
    // Button Locator Map - CSS və XPath strategiyaları
    private static final Map<String, ButtonLocators> BUTTON_LOCATORS = new HashMap<>();
    
    static {
        // Admission Requirements button
        BUTTON_LOCATORS.put("Admission Requirements", new ButtonLocators(
            "section a[href*='admission'], section a[href*='requirements']",
            "/html/body/section[1]/div/div[2]/div[3]/a",
            "//section//a[contains(text(), 'Admission') or contains(text(), 'Requirements')]"
        ));
        
        // Apply Now button (Modal)
        BUTTON_LOCATORS.put("Apply Now", new ButtonLocators(
            "button[class*='apply'], button:contains('Apply')",
            "/html/body/section[1]/div/div[2]/div[3]/button",
            "//button[contains(text(), 'Apply')]"
        ));
        
        // Rankings - View More
        BUTTON_LOCATORS.put("Rankings - View More", new ButtonLocators(
            "section[class*='ranking'] a, section a[href*='ranking']",
            "/html/body/section[2]/a",
            "//section[contains(@class, 'ranking')]//a[contains(text(), 'View More')]"
        ));
        
        // Programs - View More
        BUTTON_LOCATORS.put("Programs - View More", new ButtonLocators(
            "section[class*='program'] a[href*='program'], div a[href*='programs']",
            "/html/body/div[4]/section/div/div[3]/a",
            "//section[contains(@class, 'program')]//a[contains(text(), 'View More')]"
        ));
        
        // Galleries - View More
        BUTTON_LOCATORS.put("Galleries - View More", new ButtonLocators(
            "section[class*='galler'] a, section a[href*='galler']",
            "/html/body/section[5]/div[2]/section/div[2]/a",
            "//section[contains(@class, 'galler')]//a[contains(text(), 'View More')]"
        ));
        
        // Dormitories - View More
        BUTTON_LOCATORS.put("Dormitories - View More", new ButtonLocators(
            "section[class*='dormitor'] a, section a[href*='dormitor']",
            "/html/body/section[6]/a",
            "//section[contains(@class, 'dormitor')]//a[contains(text(), 'View More')]"
        ));
        
        // International Students
        BUTTON_LOCATORS.put("International Students", new ButtonLocators(
            "section a[href*='international']",
            "/html/body/section[7]/div[2]/div/a[1]",
            "//section//a[contains(text(), 'International')]"
        ));
        
        // Campuses - View More
        BUTTON_LOCATORS.put("Campuses - View More", new ButtonLocators(
            "section[class*='campus'] a:not([class*='card'])",
            "/html/body/div[5]/section/a",
            "//section[contains(@class, 'campus')]//a[contains(text(), 'View More')]"
        ));
        
        // Transportation Options
        BUTTON_LOCATORS.put("Transportation Options", new ButtonLocators(
            "section a[href*='transport']",
            "/html/body/section[8]/div[1]/a",
            "//section//a[contains(text(), 'Transport')]"
        ));
        
        // Visa Support
        BUTTON_LOCATORS.put("Visa Support", new ButtonLocators(
            "section a[href*='visa']",
            "/html/body/section[9]/div[2]/a",
            "//section//a[contains(text(), 'Visa')]"
        ));
        
        // FAQs - View More
        BUTTON_LOCATORS.put("FAQs - View More", new ButtonLocators(
            "section[class*='faq'] a, section a[href*='faq']",
            "/html/body/section[10]/a",
            "//section[contains(@class, 'faq')]//a[contains(text(), 'View More')]"
        ));
        
        // Reviews - View More
        BUTTON_LOCATORS.put("Reviews - View More", new ButtonLocators(
            "section[class*='review'] a, section a[href*='review']",
            "/html/body/section[11]/a",
            "//section[contains(@class, 'review')]//a[contains(text(), 'View More')]"
        ));
    }
    
    public UniversityDetailPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Button tap və tıkla
     */
    public boolean clickButton(String buttonName) {
        try {
            System.out.println("      🔍 Button axtarılır: " + buttonName);
            
            ButtonLocators locators = BUTTON_LOCATORS.get(buttonName);
            if (locators == null) {
                System.err.println("      ❌ Button konfiqurasiyası tapılmadı: " + buttonName);
                return false;
            }
            
            // Multiple strategiya
            LocatorStrategy cssStrategy = LocatorStrategy.byCss(
                locators.cssSelector,
                buttonName + " (CSS)"
            );
            
            LocatorStrategy xpathStrategy = LocatorStrategy.byXPath(
                locators.xpath,
                buttonName + " (XPath)"
            );
            
            LocatorStrategy relativeXPathStrategy = LocatorStrategy.byXPath(
                locators.relativeXPath,
                buttonName + " (Relative XPath)"
            );
            
            WebElement button = elementLocator.findClickableElement(
                cssStrategy, 
                xpathStrategy, 
                relativeXPathStrategy
            );
            
            if (button == null || !button.isDisplayed()) {
                System.out.println("      ⚠️  Button görünmür, skip");
                return false;
            }
            
            scrollToElement(button);
            highlightElement(button);
            clickWithJS(button);
            
            waitHelper.waitMedium();
            System.out.println("      ✅ Button tıklandı: " + buttonName);
            return true;
            
        } catch (Exception e) {
            System.err.println("      ❌ Button tıklama xətası: " + buttonName + " - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Apply Now button-u test et (modal açar)
     */
    public boolean testApplyNowButton() {
        try {
            System.out.println("      🔍 Apply Now modal testi");
            
            // Apply button tap və aç
            if (!clickButton("Apply Now")) {
                return false;
            }
            
            waitHelper.waitMedium();
            
            // Modal-ı bağla
            try {
                // Close button strategiyaları
                LocatorStrategy closeXPath = LocatorStrategy.byXPath(
                    "/html/body/div[7]/button",
                    "Modal Close Button (XPath)"
                );
                
                LocatorStrategy closeCSS = LocatorStrategy.byCss(
                    "div[class*='modal'] button[class*='close'], button.close",
                    "Modal Close Button (CSS)"
                );
                
                WebElement closeButton = elementLocator.findClickableElement(closeXPath, closeCSS);
                highlightElement(closeButton);
                clickWithJS(closeButton);
                
                System.out.println("      ✅ Modal bağlandı");
            } catch (Exception e) {
                // ESC ilə bağla
                driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                System.out.println("      ⚠️  ESC ilə bağlandı");
            }
            
            waitHelper.waitShort();
            return true;
            
        } catch (Exception e) {
            System.err.println("      ❌ Apply Now modal xətası: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Button mövcuddurmu yoxla
     */
    public boolean isButtonPresent(String buttonName) {
        ButtonLocators locators = BUTTON_LOCATORS.get(buttonName);
        if (locators == null) {
            return false;
        }
        
        LocatorStrategy cssStrategy = LocatorStrategy.byCss(
            locators.cssSelector,
            buttonName
        );
        
        return elementLocator.isElementPresent(cssStrategy);
    }
    
    /**
     * Button görünürmü yoxla
     */
    public boolean isButtonVisible(String buttonName) {
        ButtonLocators locators = BUTTON_LOCATORS.get(buttonName);
        if (locators == null) {
            return false;
        }
        
        LocatorStrategy cssStrategy = LocatorStrategy.byCss(
            locators.cssSelector,
            buttonName
        );
        
        return elementLocator.isElementVisible(cssStrategy);
    }
    
    /**
     * Səhifənin düzgün yükləndiyini yoxla
     */
    public boolean verifyPageLoaded() {
        try {
            waitHelper.waitForPageLoad();
            
            // Title yoxla
            String title = getPageTitle();
            if (title == null || title.isEmpty()) {
                System.err.println("      ❌ Səhifə başlığı boşdur");
                return false;
            }
            
            // Məzmun var mı
            LocatorStrategy h1Strategy = LocatorStrategy.byTagName("h1", "H1 element");
            boolean hasH1 = elementLocator.isElementPresent(h1Strategy);
            
            LocatorStrategy sectionStrategy = LocatorStrategy.byTagName("section", "Section element");
            boolean hasSection = elementLocator.isElementPresent(sectionStrategy);
            
            if (!hasH1 && !hasSection) {
                System.err.println("      ❌ Səhifədə məzmun yoxdur");
                return false;
            }
            
            System.out.println("      ✅ Səhifə düzgün yükləndi: " + title);
            return true;
            
        } catch (Exception e) {
            System.err.println("      ❌ Səhifə yoxlama xətası: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Button locator-lar inner class
     */
    private static class ButtonLocators {
        String cssSelector;
        String xpath;
        String relativeXPath;
        
        ButtonLocators(String cssSelector, String xpath, String relativeXPath) {
            this.cssSelector = cssSelector;
            this.xpath = xpath;
            this.relativeXPath = relativeXPath;
        }
    }
}
