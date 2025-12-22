package com.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Utility class for dropdown operations
 */
public class DropdownUtil {
    private static final Logger logger = LogManager.getLogger(DropdownUtil.class);

    /**
     * Selects option by visible text
     *
     * @param element    WebElement dropdown
     * @param visibleText Visible text to select
     */
    public static void selectByVisibleText(WebElement element, String visibleText) {
        logger.debug("Selecting dropdown option by visible text: {}", visibleText);
        Select select = new Select(element);
        select.selectByVisibleText(visibleText);
    }

    /**
     * Selects option by value
     *
     * @param element WebElement dropdown
     * @param value   Value to select
     */
    public static void selectByValue(WebElement element, String value) {
        logger.debug("Selecting dropdown option by value: {}", value);
        Select select = new Select(element);
        select.selectByValue(value);
    }

    /**
     * Selects option by index
     *
     * @param element WebElement dropdown
     * @param index   Index to select
     */
    public static void selectByIndex(WebElement element, int index) {
        logger.debug("Selecting dropdown option by index: {}", index);
        Select select = new Select(element);
        select.selectByIndex(index);
    }

    /**
     * Gets all options from dropdown
     *
     * @param element WebElement dropdown
     * @return List of WebElements (options)
     */
    public static List<WebElement> getAllOptions(WebElement element) {
        logger.debug("Getting all options from dropdown");
        Select select = new Select(element);
        return select.getOptions();
    }

    /**
     * Gets selected option text
     *
     * @param element WebElement dropdown
     * @return Selected option text
     */
    public static String getSelectedOptionText(WebElement element) {
        logger.debug("Getting selected option text");
        Select select = new Select(element);
        return select.getFirstSelectedOption().getText();
    }

    /**
     * Gets selected option value
     *
     * @param element WebElement dropdown
     * @return Selected option value
     */
    public static String getSelectedOptionValue(WebElement element) {
        logger.debug("Getting selected option value");
        Select select = new Select(element);
        return select.getFirstSelectedOption().getAttribute("value");
    }

    /**
     * Checks if dropdown is multiple select
     *
     * @param element WebElement dropdown
     * @return true if multiple select
     */
    public static boolean isMultiple(WebElement element) {
        logger.debug("Checking if dropdown is multiple select");
        Select select = new Select(element);
        return select.isMultiple();
    }

    /**
     * Deselects option by visible text
     *
     * @param element    WebElement dropdown
     * @param visibleText Visible text to deselect
     */
    public static void deselectByVisibleText(WebElement element, String visibleText) {
        logger.debug("Deselecting dropdown option by visible text: {}", visibleText);
        Select select = new Select(element);
        select.deselectByVisibleText(visibleText);
    }

    /**
     * Deselects option by value
     *
     * @param element WebElement dropdown
     * @param value   Value to deselect
     */
    public static void deselectByValue(WebElement element, String value) {
        logger.debug("Deselecting dropdown option by value: {}", value);
        Select select = new Select(element);
        select.deselectByValue(value);
    }

    /**
     * Deselects all options
     *
     * @param element WebElement dropdown
     */
    public static void deselectAll(WebElement element) {
        logger.debug("Deselecting all options");
        Select select = new Select(element);
        select.deselectAll();
    }
}





