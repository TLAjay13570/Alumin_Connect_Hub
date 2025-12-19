package com.automation.utils;

import com.automation.factory.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Utility class for WebDriver wait operations
 */
public class WebDriverWaitUtil {
    private static final Logger logger = LogManager.getLogger(WebDriverWaitUtil.class);
    private static WebDriver driver = DriverFactory.getCurrentDriver();
    private static WebDriverWait wait;

    static {
        int explicitWait = ConfigReader.getExplicitWait();
        wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
    }

    /**
     * Waits for element to be visible
     *
     * @param element WebElement to wait for
     * @return WebElement when visible
     */
    public static WebElement waitForElementVisible(WebElement element) {
        logger.debug("Waiting for element to be visible");
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits for element to be visible by locator
     *
     * @param locator By locator
     * @return WebElement when visible
     */
    public static WebElement waitForElementVisible(By locator) {
        logger.debug("Waiting for element to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits for element to be clickable
     *
     * @param element WebElement to wait for
     * @return WebElement when clickable
     */
    public static WebElement waitForElementClickable(WebElement element) {
        logger.debug("Waiting for element to be clickable");
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits for element to be clickable by locator
     *
     * @param locator By locator
     * @return WebElement when clickable
     */
    public static WebElement waitForElementClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits for element to be present in DOM
     *
     * @param locator By locator
     * @return WebElement when present
     */
    public static WebElement waitForElementPresent(By locator) {
        logger.debug("Waiting for element to be present: {}", locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits for all elements to be visible
     *
     * @param locator By locator
     * @return List of WebElements when all visible
     */
    public static List<WebElement> waitForAllElementsVisible(By locator) {
        logger.debug("Waiting for all elements to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /**
     * Waits for element to be invisible
     *
     * @param element WebElement to wait for
     * @return true when element is invisible
     */
    public static boolean waitForElementInvisible(WebElement element) {
        logger.debug("Waiting for element to be invisible");
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    /**
     * Waits for element to be invisible by locator
     *
     * @param locator By locator
     * @return true when element is invisible
     */
    public static boolean waitForElementInvisible(By locator) {
        logger.debug("Waiting for element to be invisible: {}", locator);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits for text to be present in element
     *
     * @param element WebElement
     * @param text    Text to wait for
     * @return true when text is present
     */
    public static boolean waitForTextToBePresentInElement(WebElement element, String text) {
        logger.debug("Waiting for text '{}' to be present in element", text);
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    /**
     * Waits for URL to contain text
     *
     * @param text Text to check in URL
     * @return true when URL contains text
     */
    public static boolean waitForUrlToContain(String text) {
        logger.debug("Waiting for URL to contain: {}", text);
        return wait.until(ExpectedConditions.urlContains(text));
    }

    /**
     * Waits for title to contain text
     *
     * @param text Text to check in title
     * @return true when title contains text
     */
    public static boolean waitForTitleToContain(String text) {
        logger.debug("Waiting for title to contain: {}", text);
        return wait.until(ExpectedConditions.titleContains(text));
    }

    /**
     * Waits for alert to be present
     *
     * @return Alert when present
     */
    public static org.openqa.selenium.Alert waitForAlert() {
        logger.debug("Waiting for alert to be present");
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Waits for frame to be available and switch to it
     *
     * @param locator By locator of frame
     * @return WebDriver after switching to frame
     */
    public static WebDriver waitForFrameAndSwitch(By locator) {
        logger.debug("Waiting for frame and switching: {}", locator);
        return wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    /**
     * Static wait (use sparingly)
     *
     * @param seconds Seconds to wait
     */
    public static void staticWait(int seconds) {
        try {
            logger.debug("Static wait for {} seconds", seconds);
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            logger.error("Interrupted during static wait: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}


