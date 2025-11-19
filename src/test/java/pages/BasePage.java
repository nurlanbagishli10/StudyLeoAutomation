package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ElementLocator;
import utils.WaitHelper;

import java.time.Duration;

/**
 * Bütün Page class-ları üçün base class
 * Ümumi metodlar və funksionallıq
 */
public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    protected WaitHelper waitHelper;
    protected ElementLocator elementLocator;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js = (JavascriptExecutor) driver;
        this.waitHelper = new WaitHelper(driver);
        this.elementLocator = new ElementLocator(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Elementə JavaScript ilə tıkla
     */
    protected void clickWithJS(WebElement element) {
        js.executeScript("arguments[0].click();", element);
    }

    /**
     * Elementə scroll et
     */
    protected void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", 
                       element);
        waitHelper.waitShort();
    }

    /**
     * Elementi highlight et (debug üçün)
     */
    protected void highlightElement(WebElement element) {
        try {
            String originalStyle = element.getAttribute("style");
            js.executeScript(
                "arguments[0].setAttribute('style', 'border: 3px solid yellow; box-shadow: 0 0 10px yellow;');",
                element
            );
            waitHelper.waitShort();
            js.executeScript(
                "arguments[0].setAttribute('style', '" + (originalStyle != null ? originalStyle : "") + "');",
                element
            );
        } catch (Exception e) {
            // Ignore highlight errors
        }
    }

    /**
     * Səhifə yükləndimi yoxla
     */
    protected boolean isPageLoaded() {
        return js.executeScript("return document.readyState").equals("complete");
    }

    /**
     * Cari URL-i al
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Səhifə başlığını al
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Geri get
     */
    public void navigateBack() {
        driver.navigate().back();
    }

    /**
     * URL-ə get
     */
    public void navigateTo(String url) {
        driver.get(url);
        waitHelper.waitForPageLoad();
    }
}
