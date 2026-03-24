package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Alumni-facing routes: /events, /notifications (not admin /admin/events).
 */
public class AlumniPortalPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(AlumniPortalPage.class);

    public void navigateToAlumniEventsPage() {
        navigateTo(ConfigReader.getBaseUrl() + "/events");
        WebDriverWaitUtil.waitForPageToLoad();
        selectAlumniEventsUpcomingTabIfPresent();
    }

    /**
     * Clicks the "Upcoming" events tab (e.g. "Upcoming (42)") so future-dated E2E events appear.
     * Supports shadcn/Radix {@code role="tab"}; no-op if the tab is missing (older layouts default to all).
     */
    public void selectAlumniEventsUpcomingTabIfPresent() {
        try {
            By byRole = By.xpath("//main//button[@role='tab' and contains(normalize-space(),'Upcoming')]");
            List<WebElement> tabs = driver.findElements(byRole);
            if (tabs.isEmpty()) {
                byRole = By.xpath("//main//button[contains(normalize-space(),'Upcoming')]");
                tabs = driver.findElements(byRole);
            }
            for (WebElement t : tabs) {
                if (!t.isDisplayed()) {
                    continue;
                }
                String state = t.getAttribute("data-state");
                if (state != null && state.equalsIgnoreCase("active")) {
                    return;
                }
                WebDriverWaitUtil.waitForElementClickable(t);
                click(t);
                WebDriverWaitUtil.staticWait(1);
                return;
            }
        } catch (Exception e) {
            logger.debug("Could not select Upcoming tab: {}", e.getMessage());
        }
    }

    public void navigateToAlumniDashboard() {
        navigateTo(ConfigReader.getBaseUrl() + "/dashboard");
    }

    /**
     * Opens the dashboard sticky-header notification bell (not sidebar controls).
     * Popover heading can vary by build; we only wait briefly for the panel to mount.
     */
    public void openDashboardNotificationBell() {
        By bellTrigger = By.xpath(
                "//main//button[contains(@class,'rounded-full')][contains(@class,'relative')][contains(@class,'hover:bg-accent')]");
        WebElement btn = WebDriverWaitUtil.waitForElementClickable(bellTrigger);
        click(btn);
        WebDriverWaitUtil.staticWait(2);
    }

    public void navigateToAlumniNotificationsPage() {
        navigateTo(ConfigReader.getBaseUrl() + "/notifications");
    }

    /** Scroll full page so long notification lists (or lazy content) enter the DOM. */
    public void scrollDocumentToBottom() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            WebDriverWaitUtil.staticWait(1);
            js.executeScript("window.scrollTo(0, 0);");
        } catch (Exception e) {
            logger.debug("scrollDocumentToBottom: {}", e.getMessage());
        }
    }

    public void enterAlumniEventsSearch(String query) {
        By search = By.xpath("//input[contains(@placeholder,'Search events')]");
        WebElement input = WebDriverWaitUtil.waitForElementVisible(search);
        input.clear();
        input.sendKeys(query);
    }

    /**
     * Clicks the alumni events list refresh control if present (title/aria-label vary by deploy).
     */
    public void clickRefreshAlumniEvents() {
        List<By> candidates = List.of(
                By.cssSelector("button[title='Refresh events']"),
                By.xpath("//button[contains(@title,'Refresh')]"),
                By.xpath("//button[contains(@aria-label,'Refresh') or contains(@aria-label,'refresh')]"),
                By.xpath("//main//button[.//*[contains(@class,'refresh-cw') or contains(@class,'lucide-refresh')]]"));
        for (By locator : candidates) {
            try {
                List<WebElement> found = driver.findElements(locator);
                for (WebElement e : found) {
                    if (!e.isDisplayed() || !e.isEnabled()) {
                        continue;
                    }
                    WebDriverWaitUtil.waitForElementClickable(e);
                    click(e);
                    WebDriverWaitUtil.staticWait(2);
                    return;
                }
            } catch (Exception ex) {
                logger.debug("Refresh locator {} skipped: {}", locator, ex.getMessage());
            }
        }
        logger.warn("No refresh control matched on alumni events page; continuing without refresh");
        WebDriverWaitUtil.staticWait(1);
    }

    /**
     * Refreshes the list, then finds the card by full stored title, feature prefix, or shorter prefix (line-clamped UI).
     */
    public void clickRegisterAfterRefresh(String storedFullTitle, String titleFragmentFromFeature) {
        selectAlumniEventsUpcomingTabIfPresent();
        clickRefreshAlumniEvents();
        scrollDocumentToBottom();
        WebElement card = waitForEventCardMatchingProbes(
                Math.max(ConfigReader.getExplicitWait(), 30),
                titleProbes(storedFullTitle, titleFragmentFromFeature));
        WebElement btn = card.findElement(By.xpath(".//button[normalize-space()='Register' and not(@disabled)]"));
        WebDriverWaitUtil.waitForElementClickable(btn);
        click(btn);
    }

    public boolean waitUntilRegisteredStateForEvent(String titleFragment, int timeoutSeconds) {
        return waitUntilRegisteredForProbes(timeoutSeconds, List.of(titleFragment));
    }

    public boolean waitUntilRegisteredForProbes(int timeoutSeconds, String storedFullTitle, String titleFragmentFromFeature) {
        return waitUntilRegisteredForProbes(timeoutSeconds, titleProbes(storedFullTitle, titleFragmentFromFeature));
    }

    private boolean waitUntilRegisteredForProbes(int timeoutSeconds, List<String> probes) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(d -> {
                for (String p : probes) {
                    if (p == null || p.isBlank()) {
                        continue;
                    }
                    List<WebElement> els = d.findElements(registeredButtonUnderTitleXpath(p));
                    if (els.stream().anyMatch(WebElement::isDisplayed)) {
                        return true;
                    }
                }
                return false;
            });
        } catch (Exception e) {
            logger.warn("Registered state not found for probes: {}", probes);
            return false;
        }
    }

    private static List<String> titleProbes(String storedFullTitle, String titleFragmentFromFeature) {
        Set<String> ordered = new LinkedHashSet<>();
        if (storedFullTitle != null && !storedFullTitle.isBlank()) {
            ordered.add(storedFullTitle);
            String noId = storedFullTitle.replaceAll(" \\d{4,}$", "").trim();
            if (!noId.equals(storedFullTitle)) {
                ordered.add(noId);
            }
        }
        if (titleFragmentFromFeature != null && !titleFragmentFromFeature.isBlank()) {
            ordered.add(titleFragmentFromFeature.trim());
        }
        ordered.add("Alumni E2E Notify Register");
        return new ArrayList<>(ordered);
    }

    private WebElement waitForEventCardMatchingProbes(int timeoutSeconds, List<String> probes) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(d -> {
            for (String p : probes) {
                if (p == null || p.isBlank()) {
                    continue;
                }
                List<WebElement> titles = d.findElements(By.xpath(titleHeadingXpath(p)));
                for (WebElement titleEl : titles) {
                    try {
                        if (!titleEl.isDisplayed()) {
                            continue;
                        }
                        WebElement card = resolveEventCardContainer(titleEl);
                        if (card != null
                                && !card.findElements(By.xpath(".//button[normalize-space()='Register' and not(@disabled)]"))
                                        .isEmpty()) {
                            return card;
                        }
                    } catch (Exception ignored) {
                        // try next
                    }
                }
            }
            return null;
        });
    }

    private static By registeredButtonUnderTitleXpath(String fragment) {
        return By.xpath(
                titleHeadingXpath(fragment)
                        + "/ancestor::*[.//button[normalize-space()='Registered']][1]"
                        + "//button[normalize-space()='Registered']");
    }

    /**
     * Event title on alumni Events may be h2–h4 depending on layout; avoids brittle quoting in XPath.
     */
    private static String titleHeadingXpath(String fragment) {
        String f = fragment == null ? "" : fragment.replace("\"", "");
        if (f.contains("'")) {
            return "//*[self::h2 or self::h3 or self::h4][contains(normalize-space(.), \"" + f + "\")]";
        }
        return "//*[self::h2 or self::h3 or self::h4][contains(normalize-space(.), '" + f + "')]";
    }

    /**
     * Walks up from a title node to a container that includes Register / Registered (deploy card markup varies).
     */
    private WebElement resolveEventCardContainer(WebElement titleEl) {
        String[] paths = {
                "./ancestor::div[contains(@class,'overflow-hidden')][1]",
                "./ancestor::div[contains(@class,'rounded-lg')][1]",
                "./ancestor::div[contains(@class,'rounded-xl')][1]",
                "./ancestor::article[1]",
                "./ancestor::*[.//button[normalize-space()='Register' or normalize-space()='Registered']][1]"
        };
        for (String path : paths) {
            try {
                WebElement c = titleEl.findElement(By.xpath(path));
                if (!c.findElements(
                                By.xpath(".//button[normalize-space()='Register' or normalize-space()='Registered']"))
                        .isEmpty()) {
                    return c;
                }
            } catch (Exception ignored) {
                // try next ancestor pattern
            }
        }
        return null;
    }

    /**
     * True if the visible page source contains the text (notifications list or full page).
     */
    public boolean pageBodyContains(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return driver.getPageSource().contains(text);
    }

    /**
     * True if the page shows an event notification for the automation-created title.
     * Matches full title in HTML, or unique numeric suffix plus "new event" copy (deploy may format text differently).
     */
    public boolean pageShowsCreatedEventNotification(String fullEventTitle) {
        if (fullEventTitle == null || fullEventTitle.isBlank()) {
            return false;
        }
        String src = driver.getPageSource();
        if (src.contains(fullEventTitle)) {
            return true;
        }
        String suffix = trailingNumericSuffix(fullEventTitle);
        if (suffix != null && src.contains(suffix)) {
            String lower = src.toLowerCase();
            return lower.contains("new event") || lower.contains("created a new event");
        }
        return false;
    }

    private static String trailingNumericSuffix(String title) {
        int sp = title.lastIndexOf(' ');
        if (sp < 0 || sp >= title.length() - 1) {
            return null;
        }
        String tail = title.substring(sp + 1).trim();
        return tail.matches("\\d{4,}") ? tail : null;
    }

    public void refreshPage() {
        driver.navigate().refresh();
        WebDriverWaitUtil.waitForPageToLoad();
    }
}
