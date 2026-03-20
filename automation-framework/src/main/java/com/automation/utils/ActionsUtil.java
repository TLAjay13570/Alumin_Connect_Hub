package com.automation.utils;

import com.automation.factory.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Utility class for Selenium Actions operations
 */
public class ActionsUtil {
    private static final Logger logger = LogManager.getLogger(ActionsUtil.class);

    /**
     * Creates a fresh {@link Actions} instance for the current thread driver.
     * This avoids issues where a static driver reference becomes stale in parallel runs.
     */
    private static Actions getActions() {
        WebDriver driver = DriverFactory.getCurrentDriver();
        return new Actions(driver);
    }

    /**
     * Performs mouse hover on element
     *
     * @param element WebElement to hover
     */
    public static void mouseHover(WebElement element) {
        logger.debug("Performing mouse hover on element");
        getActions().moveToElement(element).perform();
    }

    /**
     * Performs right click on element
     *
     * @param element WebElement to right click
     */
    public static void rightClick(WebElement element) {
        logger.debug("Performing right click on element");
        getActions().contextClick(element).perform();
    }

    /**
     * Performs double click on element
     *
     * @param element WebElement to double click
     */
    public static void doubleClick(WebElement element) {
        logger.debug("Performing double click on element");
        getActions().doubleClick(element).perform();
    }

    /**
     * Performs drag and drop operation
     *
     * @param source WebElement to drag
     * @param target WebElement to drop
     */
    public static void dragAndDrop(WebElement source, WebElement target) {
        logger.debug("Performing drag and drop operation");
        getActions().dragAndDrop(source, target).perform();
    }

    /**
     * Performs click and hold on element
     *
     * @param element WebElement to click and hold
     */
    public static void clickAndHold(WebElement element) {
        logger.debug("Performing click and hold on element");
        getActions().clickAndHold(element).perform();
    }

    /**
     * Releases the mouse button
     */
    public static void release() {
        logger.debug("Releasing mouse button");
        getActions().release().perform();
    }

    /**
     * Moves to element and clicks
     *
     * @param element WebElement to move to and click
     */
    public static void moveToElementAndClick(WebElement element) {
        logger.debug("Moving to element and clicking");
        getActions().moveToElement(element).click().perform();
    }

    /**
     * Sends keys to element
     *
     * @param element WebElement to send keys to
     * @param keys    Keys to send
     */
    public static void sendKeys(WebElement element, CharSequence... keys) {
        logger.debug("Sending keys to element");
        getActions().sendKeys(element, keys).perform();
    }
}






