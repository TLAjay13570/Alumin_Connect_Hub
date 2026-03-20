package com.automation.utils;

import com.automation.factory.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads Radix/shadcn toast notifications from any page (not tied to a single POM).
 */
public final class ToastUtil {
    private static final Logger logger = LogManager.getLogger(ToastUtil.class);

    private ToastUtil() {
    }

    private static List<By> toastRootLocators() {
        List<By> locators = new ArrayList<>();
        locators.add(By.cssSelector("[data-state='open'][class*='group']"));
        locators.add(By.xpath("//li[@data-state='open']"));
        locators.add(By.xpath("//*[@role='status' and (contains(@class,'border') or contains(@class,'toast'))]"));
        locators.add(By.xpath("//ol[contains(@class,'fixed')]//li[@data-state='open']"));
        return locators;
    }

    /**
     * Best-effort visible toast text from the viewport.
     */
    public static String getLatestToastText() {
        WebDriver driver = DriverFactory.getCurrentDriver();
        String best = "";
        for (By locator : toastRootLocators()) {
            try {
                List<WebElement> els = driver.findElements(locator);
                for (WebElement el : els) {
                    if (el.isDisplayed()) {
                        String t = el.getText();
                        if (t != null && t.trim().length() > best.trim().length()) {
                            best = t.trim();
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("Toast locator skip {}: {}", locator, e.getMessage());
            }
        }
        return best;
    }

    /**
     * Waits until any visible toast contains one of the phrases (case-insensitive).
     *
     * @return matching toast text
     */
    public static String waitForToastContainingEither(String text1, String text2) {
        String lower1 = text1.toLowerCase();
        String lower2 = text2.toLowerCase();
        WebDriver driver = DriverFactory.getCurrentDriver();
        int seconds = Math.max(45, ConfigReader.getExplicitWait() * 2);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(d -> {
            String msg = getLatestToastText();
            if (msg == null || msg.isEmpty()) {
                return null;
            }
            String low = msg.toLowerCase();
            if (low.contains(lower1) || low.contains(lower2)) {
                return msg;
            }
            return null;
        });
    }
}
