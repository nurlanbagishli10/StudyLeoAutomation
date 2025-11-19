package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Element tapma strategiyaları üçün utility class
 * Multiple locator strategiyası və fallback dəstəyi ilə
 */
public class ElementLocator {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private static final int DEFAULT_TIMEOUT = 10;
    private static final int MAX_RETRIES = 3;

    public ElementLocator(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        this.js = (JavascriptExecutor) driver;
    }

    /**
     * Element tapma - Multiple strategiya ilə
     * Əvvəlcə CSS, sonra XPath, sonra digər metodları sına
     */
    public WebElement findElement(LocatorStrategy... strategies) {
        List<Exception> exceptions = new ArrayList<>();
        
        for (LocatorStrategy strategy : strategies) {
            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                try {
                    By locator = strategy.getLocator();
                    WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                    
                    // Elementə scroll et
                    scrollToElement(element);
                    
                    // Görünməsini gözlə
                    wait.until(ExpectedConditions.visibilityOf(element));
                    
                    System.out.println("   ✅ Element tapıldı: " + strategy.getDescription() + 
                                     " (Strategy: " + strategy.getType() + ", Attempt: " + attempt + ")");
                    return element;
                    
                } catch (Exception e) {
                    exceptions.add(e);
                    if (attempt < MAX_RETRIES) {
                        System.out.println("   ⚠️  Cəhd " + attempt + " uğursuz (Strategy: " + 
                                         strategy.getType() + "), yenidən cəhd edilir...");
                        waitShort();
                    }
                }
            }
        }
        
        // Heç bir strategiya işləmədi
        System.err.println("   ❌ Element tapılmadı! Bütün strategiyalar sınandı:");
        for (int i = 0; i < strategies.length; i++) {
            System.err.println("      " + (i+1) + ". " + strategies[i].getType() + 
                             ": " + strategies[i].getDescription());
        }
        
        throw new NoSuchElementException("Element tapılmadı. " + exceptions.size() + 
                                       " strategiya sınandı.");
    }

    /**
     * Elementlərin listini tap
     */
    public List<WebElement> findElements(LocatorStrategy strategy) {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(strategy.getLocator()));
        } catch (Exception e) {
            System.err.println("   ❌ Elementlər tapılmadı: " + strategy.getDescription());
            return new ArrayList<>();
        }
    }

    /**
     * Element görünür və klik edilə bilərmi yoxla
     */
    public WebElement findClickableElement(LocatorStrategy... strategies) {
        List<Exception> exceptions = new ArrayList<>();
        
        for (LocatorStrategy strategy : strategies) {
            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                try {
                    By locator = strategy.getLocator();
                    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                    
                    // Elementə scroll et
                    scrollToElement(element);
                    waitShort();
                    
                    System.out.println("   ✅ Klik edilə bilən element tapıldı: " + 
                                     strategy.getDescription());
                    return element;
                    
                } catch (Exception e) {
                    exceptions.add(e);
                    if (attempt < MAX_RETRIES) {
                        System.out.println("   ⚠️  Klik cəhdi " + attempt + " uğursuz, yenidən...");
                        waitShort();
                    }
                }
            }
        }
        
        throw new NoSuchElementException("Klik edilə bilən element tapılmadı");
    }

    /**
     * Elementə scroll et
     */
    private void scrollToElement(WebElement element) {
        try {
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", 
                           element);
            waitShort();
        } catch (Exception e) {
            // Ignore scroll errors
        }
    }

    /**
     * Element mövcuddurmu yoxla (exception atmadan)
     */
    public boolean isElementPresent(LocatorStrategy strategy) {
        try {
            driver.findElement(strategy.getLocator());
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Element görünürmü yoxla
     */
    public boolean isElementVisible(LocatorStrategy strategy) {
        try {
            WebElement element = driver.findElement(strategy.getLocator());
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void waitShort() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Locator strategiyası inner class
     */
    public static class LocatorStrategy {
        private String type;
        private By locator;
        private String description;

        private LocatorStrategy(String type, By locator, String description) {
            this.type = type;
            this.locator = locator;
            this.description = description;
        }

        public static LocatorStrategy byCss(String cssSelector, String description) {
            return new LocatorStrategy("CSS", By.cssSelector(cssSelector), description);
        }

        public static LocatorStrategy byXPath(String xpath, String description) {
            return new LocatorStrategy("XPath", By.xpath(xpath), description);
        }

        public static LocatorStrategy byId(String id, String description) {
            return new LocatorStrategy("ID", By.id(id), description);
        }

        public static LocatorStrategy byClassName(String className, String description) {
            return new LocatorStrategy("ClassName", By.className(className), description);
        }

        public static LocatorStrategy byLinkText(String linkText, String description) {
            return new LocatorStrategy("LinkText", By.linkText(linkText), description);
        }

        public static LocatorStrategy byPartialLinkText(String partialLinkText, String description) {
            return new LocatorStrategy("PartialLinkText", By.partialLinkText(partialLinkText), 
                                     description);
        }

        public static LocatorStrategy byTagName(String tagName, String description) {
            return new LocatorStrategy("TagName", By.tagName(tagName), description);
        }

        public String getType() { return type; }
        public By getLocator() { return locator; }
        public String getDescription() { return description; }
    }
}
