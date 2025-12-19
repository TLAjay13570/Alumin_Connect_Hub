package com.automation.pages;

import com.automation.factory.DriverFactory;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * Base Page class for Page Object Model
 * All page classes should extend this class
 */
public class BasePage {
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;

    /**
     * Constructor to initialize page elements using PageFactory
     */
    public BasePage() {
        this.driver = DriverFactory.getCurrentDriver();
        PageFactory.initElements(driver, this);
    }

    /**
     * Navigates to a URL
     *
     * @param url URL to navigate to
     */
    public void navigateTo(String url) {
        logger.info("Navigating to: {}", url);
        driver.get(url);
    }

    /**
     * Gets current page title
     *
     * @return Page title
     */
    public String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("Page title: {}", title);
        return title;
    }

    /**
     * Gets current page URL
     *
     * @return Page URL
     */
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.debug("Current URL: {}", url);
        return url;
    }

    /**
     * Clicks on element with wait
     *
     * @param element WebElement to click
     */
    public void click(WebElement element) {
        logger.debug("Clicking on element");
        WebDriverWaitUtil.waitForElementClickable(element).click();
    }

    /**
     * Enters text in element with wait
     *
     * @param element WebElement to enter text
     * @param text    Text to enter
     */
    public void sendKeys(WebElement element, String text) {
        logger.debug("Entering text: {}", text);
        WebDriverWaitUtil.waitForElementVisible(element).clear();
        element.sendKeys(text);
    }

    /**
     * Gets text from element with wait
     *
     * @param element WebElement to get text from
     * @return Text from element
     */
    public String getText(WebElement element) {
        logger.debug("Getting text from element");
        return WebDriverWaitUtil.waitForElementVisible(element).getText();
    }

    /**
     * Checks if element is displayed
     *
     * @param element WebElement to check
     * @return true if element is displayed
     */
    public boolean isElementDisplayed(WebElement element) {
        try {
            return WebDriverWaitUtil.waitForElementVisible(element).isDisplayed();
        } catch (Exception e) {
            logger.debug("Element is not displayed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if element is enabled
     *
     * @param element WebElement to check
     * @return true if element is enabled
     */
    public boolean isElementEnabled(WebElement element) {
        try {
            return WebDriverWaitUtil.waitForElementVisible(element).isEnabled();
        } catch (Exception e) {
            logger.debug("Element is not enabled: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Waits for element to be visible
     *
     * @param element WebElement to wait for
     */
    public void waitForElement(WebElement element) {
        WebDriverWaitUtil.waitForElementVisible(element);
    }
}


