package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.ElementLocator.LocatorStrategy;

import java.util.List;

/**
 * Universitet List Səhifəsi - Page Object Model
 * URL: https://studyleo.com/en/universities
 */
public class UniversityListPage extends BasePage {
    
    // URL-lər
    private static final String BASE_URL = "https://studyleo.com/en/universities";
    
    // CSS Selectors - Daha robust və maintainable
    private static final String UNIVERSITY_CARDS_CSS = "div.university-card, section div[class*='university']";
    private static final String UNIVERSITY_LINK_CSS = "div.university-card a, section div[class*='university'] a";
    
    public UniversityListPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Universitet list səhifəsinə get
     */
    public void open() {
        navigateTo(BASE_URL);
    }
    
    /**
     * Səhifə nömrəsinə get
     */
    public void openPage(int pageNumber) {
        String url = pageNumber == 1 ? BASE_URL : BASE_URL + "?page=" + pageNumber;
        navigateTo(url);
    }
    
    /**
     * Universitet kartını tap və tıkla (index ilə)
     * Multiple locator strategiyası ilə
     */
    public boolean clickUniversityCard(int pageNumber, int index) {
        try {
            System.out.println("   🔍 Universitet " + index + " axtarılır (Səhifə " + pageNumber + ")");
            
            // Strategy 1: CSS Selector (daha robust)
            LocatorStrategy cssStrategy = LocatorStrategy.byCss(
                UNIVERSITY_LINK_CSS + ":nth-of-type(" + index + ")",
                "University Card " + index + " (CSS)"
            );
            
            // Strategy 2: XPath - Səhifə 1 üçün fərqli struktur
            String xpath = pageNumber == 1
                ? "/html/body/div[3]/section/div/div/div[1]/div[" + index + "]/a/div[1]"
                : "/html/body/div[3]/section/div/div/div[1]/div[" + index + "]/a";
            
            LocatorStrategy xpathStrategy = LocatorStrategy.byXPath(
                xpath,
                "University Card " + index + " (XPath)"
            );
            
            // Strategy 3: Relative XPath (daha flexible)
            LocatorStrategy relativeXPathStrategy = LocatorStrategy.byXPath(
                "//section//div[contains(@class, 'university') or contains(@class, 'card')][" + index + "]//a",
                "University Card " + index + " (Relative XPath)"
            );
            
            // Element tapma - bütün strategiyaları sına
            WebElement card = elementLocator.findClickableElement(
                cssStrategy, 
                xpathStrategy, 
                relativeXPathStrategy
            );
            
            scrollToElement(card);
            highlightElement(card);
            clickWithJS(card);
            
            waitHelper.waitForPageLoad();
            waitHelper.waitLong();
            
            System.out.println("   ✅ Universitet kartına tıklandı");
            return true;
            
        } catch (Exception e) {
            System.err.println("   ❌ Universitet kartı " + index + " tıklanamadı: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Universitet adını al
     */
    public String getUniversityName(int pageNumber, int index) {
        try {
            // Strategy 1: CSS
            LocatorStrategy cssStrategy = LocatorStrategy.byCss(
                UNIVERSITY_LINK_CSS + ":nth-of-type(" + index + ") h3",
                "University Name (CSS)"
            );
            
            // Strategy 2: XPath
            String xpath = pageNumber == 1
                ? "/html/body/div[3]/section/div/div/div[1]/div[" + index + "]/a/div[2]/h3"
                : "/html/body/div[3]/section/div/div/div[1]/div[" + index + "]/a//h3";
            
            LocatorStrategy xpathStrategy = LocatorStrategy.byXPath(
                xpath,
                "University Name (XPath)"
            );
            
            WebElement nameElement = elementLocator.findElement(cssStrategy, xpathStrategy);
            return nameElement.getText();
            
        } catch (Exception e) {
            System.err.println("   ⚠️  Universitet adı alınmadı: " + e.getMessage());
            return "Universitet " + index;
        }
    }
    
    /**
     * Səhifədəki universitet sayını al
     */
    public int getUniversityCount() {
        try {
            // CSS ilə
            LocatorStrategy cssStrategy = LocatorStrategy.byCss(
                UNIVERSITY_LINK_CSS,
                "University Links"
            );
            
            List<WebElement> cards = elementLocator.findElements(cssStrategy);
            return cards.size();
            
        } catch (Exception e) {
            System.err.println("   ⚠️  Universitet sayı hesablanmadı: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Səhifənin düzgün yükləndiyini yoxla
     */
    public boolean verifyPageLoaded() {
        try {
            waitHelper.waitForPageLoad();
            
            // Universitet list container mövcuddur
            LocatorStrategy containerStrategy = LocatorStrategy.byCss(
                "section, div.container",
                "University List Container"
            );
            
            elementLocator.findElement(containerStrategy);
            
            // Ən azı bir universitet kartı var
            int count = getUniversityCount();
            if (count > 0) {
                System.out.println("   ✅ Səhifədə " + count + " universitet tapıldı");
                return true;
            } else {
                System.err.println("   ❌ Heç bir universitet kartı tapılmadı");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("   ❌ Səhifə yüklənmə xətası: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Səhifədə scroll et
     */
    public void scrollDown(int pixels) {
        js.executeScript("window.scrollBy(0, " + pixels + ");");
        waitHelper.waitShort();
    }
}
