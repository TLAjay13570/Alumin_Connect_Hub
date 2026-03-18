package com.automation.utils;

import com.automation.factory.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Utility class for JavaScript executor operations
 */
public class JavaScriptExecutorUtil {
    private static final Logger logger = LogManager.getLogger(JavaScriptExecutorUtil.class);
    private static WebDriver driver = DriverFactory.getCurrentDriver();
    private static JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

    /**
     * Executes JavaScript code
     *
     * @param script JavaScript code to execute
     * @return Result of execution
     */
    public static Object executeScript(String script) {
        logger.debug("Executing JavaScript: {}", script);
        return jsExecutor.executeScript(script);
    }

    /**
     * Executes JavaScript code with arguments
     *
     * @param script JavaScript code to execute
     * @param args   Arguments for the script
     * @return Result of execution
     */
    public static Object executeScript(String script, Object... args) {
        logger.debug("Executing JavaScript with arguments: {}", script);
        return jsExecutor.executeScript(script, args);
    }

    /**
     * Scrolls to element
     *
     * @param element WebElement to scroll to
     */
    public static void scrollToElement(WebElement element) {
        logger.debug("Scrolling to element");
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Scrolls to bottom of page
     */
    public static void scrollToBottom() {
        logger.debug("Scrolling to bottom of page");
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Scrolls to top of page
     */
    public static void scrollToTop() {
        logger.debug("Scrolling to top of page");
        jsExecutor.executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Scrolls by pixels
     *
     * @param xPixels Horizontal pixels
     * @param yPixels Vertical pixels
     */
    public static void scrollBy(int xPixels, int yPixels) {
        logger.debug("Scrolling by {} horizontal and {} vertical pixels", xPixels, yPixels);
        jsExecutor.executeScript("window.scrollBy(" + xPixels + "," + yPixels + ");");
    }

    /**
     * Clicks element using JavaScript
     *
     * @param element WebElement to click
     */
    public static void clickElement(WebElement element) {
        logger.debug("Clicking element using JavaScript");
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    /**
     * Highlights element with border
     *
     * @param element WebElement to highlight
     */
    public static void highlightElement(WebElement element) {
        logger.debug("Highlighting element");
        jsExecutor.executeScript("arguments[0].style.border='3px solid red'", element);
    }

    /**
     * Removes highlight from element
     *
     * @param element WebElement to remove highlight from
     */
    public static void removeHighlight(WebElement element) {
        logger.debug("Removing highlight from element");
        jsExecutor.executeScript("arguments[0].style.border=''", element);
    }

    /**
     * Gets page title using JavaScript
     *
     * @return Page title
     */
    public static String getPageTitle() {
        logger.debug("Getting page title using JavaScript");
        return (String) jsExecutor.executeScript("return document.title;");
    }

    /**
     * Gets page URL using JavaScript
     *
     * @return Page URL
     */
    public static String getPageUrl() {
        logger.debug("Getting page URL using JavaScript");
        return (String) jsExecutor.executeScript("return window.location.href;");
    }

    /**
     * Waits for page to load completely
     */
    public static void waitForPageLoad() {
        logger.debug("Waiting for page to load completely");
        jsExecutor.executeScript("return document.readyState").equals("complete");
    }

    /**
     * Sets attribute value using JavaScript
     *
     * @param element   WebElement
     * @param attribute Attribute name
     * @param value     Attribute value
     */
    public static void setAttribute(WebElement element, String attribute, String value) {
        logger.debug("Setting attribute {} to {} using JavaScript", attribute, value);
        jsExecutor.executeScript("arguments[0].setAttribute('" + attribute + "', '" + value + "');", element);
    }

    /**
     * Gets attribute value using JavaScript
     *
     * @param element   WebElement
     * @param attribute Attribute name
     * @return Attribute value
     */
    public static String getAttribute(WebElement element, String attribute) {
        logger.debug("Getting attribute {} using JavaScript", attribute);
        return (String) jsExecutor.executeScript("return arguments[0].getAttribute('" + attribute + "');", element);
    }

    /**
     * Gets inner text using JavaScript
     *
     * @param element WebElement
     * @return Inner text
     */
    public static String getInnerText(WebElement element) {
        logger.debug("Getting inner text using JavaScript");
        return (String) jsExecutor.executeScript("return arguments[0].innerText;", element);
    }
}






