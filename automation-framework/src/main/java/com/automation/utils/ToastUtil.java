package com.automation.utils;

import com.automation.factory.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads toast notifications: Radix/shadcn ({@code useToast}) and Sonner ({@code sonner}).
 * The app may mount both shadcn and Sonner toasters — cover both DOM shapes.
 */
public final class ToastUtil {
    private static final Logger logger = LogManager.getLogger(ToastUtil.class);

    private ToastUtil() {
    }

    private static List<By> toastRootLocators() {
        List<By> locators = new ArrayList<>();
        // Sonner (section.toaster + data-sonner-toast on items)
        locators.add(By.cssSelector("[data-sonner-toast]"));
        locators.add(By.cssSelector("section.toaster li[data-sonner-toast]"));
        locators.add(By.xpath("//section[contains(@class,'toaster')]//li[contains(@class,'toast')]"));
        // Radix Toast root (shadcn)
        locators.add(By.cssSelector("[data-state='open'][class*='group']"));
        locators.add(By.xpath("//li[@data-state='open']"));
        locators.add(By.xpath("//*[@role='status' and (contains(@class,'border') or contains(@class,'toast'))]"));
        locators.add(By.xpath("//ol[contains(@class,'fixed')]//li[@data-state='open']"));
        return locators;
    }

    private static String visibleText(WebElement el) {
        try {
            String t = el.getText();
            return t != null ? t.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Best-effort visible toast text from the viewport (longest non-empty match wins).
     */
    public static String getLatestToastText() {
        WebDriver driver = DriverFactory.getCurrentDriver();
        String best = "";
        Set<String> seen = new LinkedHashSet<>();
        for (By locator : toastRootLocators()) {
            try {
                List<WebElement> els = driver.findElements(locator);
                for (WebElement el : els) {
                    try {
                        boolean sonner = isSonnerToast(el);
                        if (!sonner && !el.isDisplayed()) {
                            continue;
                        }
                        String t = visibleText(el);
                        if (t.isEmpty()) {
                            continue;
                        }
                        if (seen.add(t) && t.length() > best.length()) {
                            best = t;
                        }
                    } catch (Exception e) {
                        logger.debug("Toast element skip: {}", e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.debug("Toast locator skip {}: {}", locator, e.getMessage());
            }
        }
        String js = scrapeToastTextViaScript(driver);
        if (js.length() > best.length()) {
            best = js;
        }
        return best;
    }

    /**
     * Sonner / Radix nodes are sometimes not {@code isDisplayed()} to Selenium; scrape innerText from the DOM.
     */
    private static String scrapeToastTextViaScript(WebDriver driver) {
        try {
            Object o = ((JavascriptExecutor) driver).executeScript(
                    "var p=[];"
                            + "document.querySelectorAll('[data-sonner-toast]').forEach(function(e){"
                            + "  var t=(e.innerText||'').trim(); if(t) p.push(t);"
                            + "});"
                            + "document.querySelectorAll('section.toaster li, ol[class*=\"fixed\"] li').forEach(function(e){"
                            + "  var t=(e.innerText||'').trim(); if(t&&t.length<500) p.push(t);"
                            + "});"
                            + "document.querySelectorAll('[role=\"status\"]').forEach(function(e){"
                            + "  var t=(e.innerText||'').trim(); if(t&&t.length<500) p.push(t);"
                            + "});"
                            + "return p.join(' | ');");
            return o == null ? "" : String.valueOf(o).trim();
        } catch (Exception e) {
            logger.debug("JS toast scrape: {}", e.getMessage());
            return "";
        }
    }

    private static boolean isSonnerToast(WebElement el) {
        try {
            String tag = el.getTagName();
            if ("li".equalsIgnoreCase(tag) && el.getAttribute("data-sonner-toast") != null) {
                return true;
            }
            return el.getAttribute("data-sonner-toast") != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Waits until any visible toast contains one of the phrases (case-insensitive).
     */
    public static String waitForToastContainingEither(String text1, String text2) {
        String lower1 = text1.toLowerCase();
        String lower2 = text2.toLowerCase();
        WebDriver driver = DriverFactory.getCurrentDriver();
        int seconds = Math.max(45, ConfigReader.getExplicitWait() * 2);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(d -> {
            String msg = getLatestToastText();
            if (msg.isEmpty()) {
                return null;
            }
            String low = msg.toLowerCase();
            if (low.contains(lower1) || low.contains(lower2)) {
                return msg;
            }
            return null;
        });
    }

    /**
     * Waits until a visible toast contains the phrase (case-insensitive).
     */
    public static String waitForToastContaining(String phrase) {
        String needle = phrase.toLowerCase();
        WebDriver driver = DriverFactory.getCurrentDriver();
        int seconds = Math.max(45, ConfigReader.getExplicitWait() * 2);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(d -> {
            String msg = getLatestToastText();
            if (msg.isEmpty()) {
                return null;
            }
            if (msg.toLowerCase().contains(needle)) {
                return msg;
            }
            return null;
        });
    }
}
