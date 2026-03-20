package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.JavaScriptExecutorUtil;
import com.automation.utils.ToastUtil;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Page Object for Event Management (admin). Targets {@code EventModal} field ids in the React app.
 */
public class EventManagementPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(EventManagementPage.class);

    private static final By DIALOG = By.xpath("//div[@role='dialog']");

    @FindBy(xpath = "//button[contains(.,'Create Event')]")
    private WebElement createEventButton;

    public EventManagementPage() {
        super();
    }

    private static String normalizeDateForHtml5(String raw) {
        String input = raw.trim();
        if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return input;
        }
        try {
            DateTimeFormatter us = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.US);
            return LocalDate.parse(input, us).format(DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return input;
        }
    }

    private static String normalizeTimeForHtml5(String raw) {
        String input = raw.trim();
        if (input.matches("\\d{1,2}:\\d{2}")) {
            String[] p = input.split(":");
            return String.format("%02d:%s", Integer.parseInt(p[0]), p[1]);
        }
        try {
            DateTimeFormatter in = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
            LocalTime t = LocalTime.parse(input, in);
            return t.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return input;
        }
    }

    public void navigateToEventsPage() {
        logger.info("Navigating to events management page");
        String eventsUrl = ConfigReader.getBaseUrl() + "/admin/events";
        navigateTo(eventsUrl);
        WebDriverWaitUtil.waitForPageToLoad();
    }

    public void clickCreateEventButton() {
        logger.info("Clicking Create Event button");
        WebElement btn = WebDriverWaitUtil.waitForElementClickable(createEventButton);
        click(btn);
        WebDriverWaitUtil.waitForElementVisible(DIALOG);
        WebDriverWaitUtil.waitForElementVisible(By.id("event-title"));
    }

    public void enterEventTitle(String title) {
        logger.info("Entering event title: {}", title);
        WebElement el = WebDriverWaitUtil.waitForElementVisible(By.id("event-title"));
        el.clear();
        el.sendKeys(title);
    }

    public void enterEventDescription(String description) {
        logger.info("Entering event description");
        WebElement el = WebDriverWaitUtil.waitForElementVisible(By.id("event-description"));
        el.clear();
        el.sendKeys(description);
    }

    public void enterEventDate(String date) {
        logger.info("Entering event date: {}", date);
        String iso = normalizeDateForHtml5(date);
        List<By> candidates = List.of(
                By.id("event-date"),
                By.xpath("//div[@role='dialog']//input[@type='date']"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Date')]/following::input[1]"),
                By.xpath("//div[@role='dialog']//div[contains(.,'Date')]//input[@type='text' or not(@type)]")
        );
        WebElement el = WebDriverWaitUtil.waitForAnyElementVisible(candidates);
        String inputType = el.getAttribute("type");
        if (inputType != null && "date".equalsIgnoreCase(inputType)) {
            JavaScriptExecutorUtil.setAttribute(el, "value", iso);
            JavaScriptExecutorUtil.executeScript(
                    "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));"
                            + "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));", el);
        } else {
            el.clear();
            el.sendKeys(date);
        }
    }

    public void enterEventStartTime(String time) {
        logger.info("Entering event start time: {}", time);
        List<By> candidates = List.of(
                By.id("event-start-time"),
                By.xpath("//div[@role='dialog']//input[@id='event-start-time']"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Start')]/following::input[@type='time'][1]"),
                By.xpath("//div[@role='dialog']//input[@type='time'][1]")
        );
        WebElement el = WebDriverWaitUtil.waitForAnyElementVisible(candidates);
        el.clear();
        el.sendKeys(normalizeTimeForHtml5(time));
    }

    public void enterEventEndTime(String time) {
        logger.info("Entering event end time: {}", time);
        String value = normalizeTimeForHtml5(time);
        List<WebElement> timeInputs = driver.findElements(By.xpath("//div[@role='dialog']//input[@type='time']"));
        if (timeInputs.size() >= 2) {
            WebElement el = timeInputs.get(1);
            WebDriverWaitUtil.waitForElementVisible(el);
            el.clear();
            el.sendKeys(value);
            return;
        }
        List<By> candidates = List.of(
                By.id("event-end-time"),
                By.xpath("//div[@role='dialog']//label[contains(.,'End')]/following::input[@type='time'][1]")
        );
        try {
            WebElement el = WebDriverWaitUtil.waitForAnyElementVisible(candidates);
            el.clear();
            el.sendKeys(value);
        } catch (Exception e) {
            throw new RuntimeException("End time is required by the event form but no end time field was found", e);
        }
    }

    /**
     * Backward-compatible: sets start time only (prefer explicit start/end steps in features).
     */
    public void enterEventTime(String time) {
        enterEventStartTime(time);
    }

    public void enterEventLocation(String location) {
        logger.info("Entering event location: {}", location);
        List<By> candidates = List.of(
                By.id("event-location"),
                By.xpath("//div[@role='dialog']//input[@id='event-location']"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Location')]/following::input[1]"),
                By.xpath("//div[@role='dialog']//input[contains(@placeholder,'Campus') or contains(@placeholder,'San Francisco')]")
        );
        WebElement el = WebDriverWaitUtil.waitForAnyElementVisible(candidates);
        el.clear();
        el.sendKeys(location);
    }

    public void toggleVirtualEvent() {
        logger.info("Toggling virtual event switch");
        List<By> candidates = List.of(
                By.id("virtual-event"),
                By.xpath("//div[@role='dialog']//*[@role='switch'][1]")
        );
        WebElement toggle = WebDriverWaitUtil.waitForElementClickable(
                WebDriverWaitUtil.waitForAnyElementVisible(candidates));
        click(toggle);
    }

    public void selectCategory(String categoryLabel) {
        logger.info("Selecting event type/category: {}", categoryLabel);
        List<By> triggerCandidates = List.of(
                By.id("event-type"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Event Type') or contains(.,'Type')]/following::button[1]"),
                By.xpath("//div[@role='dialog']//button[@role='combobox'][1]")
        );
        WebElement trigger = WebDriverWaitUtil.waitForElementClickable(
                WebDriverWaitUtil.waitForAnyElementVisible(triggerCandidates));
        click(trigger);
        String optionXpath = String.format("//div[@role='option' and contains(normalize-space(),\"%s\")]", categoryLabel);
        WebElement option = WebDriverWaitUtil.waitForElementClickable(By.xpath(optionXpath));
        click(option);
    }

    public void enterImageUrl(String imageUrl) {
        logger.info("Entering image URL");
        try {
            WebElement el = WebDriverWaitUtil.waitForElementVisible(By.id("event-image"));
            el.clear();
            el.sendKeys(imageUrl);
        } catch (Exception e) {
            logger.warn("Could not enter image URL: {}", e.getMessage());
        }
    }

    public void clickCreateEventButtonInModal() {
        logger.info("Clicking Create Event in modal");
        List<WebElement> buttons = driver.findElements(
                By.xpath("//div[@role='dialog']//button[contains(normalize-space(),'Create Event')]"));
        WebElement submit = null;
        for (WebElement b : buttons) {
            if (b.isDisplayed() && b.isEnabled()) {
                submit = b;
                break;
            }
        }
        if (submit == null) {
            submit = WebDriverWaitUtil.waitForElementClickable(
                    By.xpath("//div[@role='dialog']//button[contains(.,'Create Event')]"));
        }
        JavaScriptExecutorUtil.scrollToElement(submit);
        click(submit);
        int submitWaitSeconds = Math.max(ConfigReader.getExplicitWait(), ConfigReader.getPageLoadTimeout());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(submitWaitSeconds));
        try {
            wait.until(d -> {
                if (!ToastUtil.getLatestToastText().isEmpty()) {
                    return true;
                }
                if (!collectDialogErrors().isBlank()) {
                    return true;
                }
                // Radix may keep an off-screen dialog node; title field disappearing is a reliable close signal.
                List<WebElement> titles = d.findElements(By.id("event-title"));
                if (titles.isEmpty()) {
                    return true;
                }
                return titles.stream().noneMatch(WebElement::isDisplayed);
            });
        } catch (TimeoutException e) {
            throw new RuntimeException(
                    "Event form still open after submit (timeout " + submitWaitSeconds + "s). Errors: " + collectDialogErrors(),
                    e);
        }
        String errors = collectDialogErrors();
        if (!errors.isBlank()) {
            throw new RuntimeException("Event form validation failed: " + errors);
        }
    }

    private String collectDialogErrors() {
        StringBuilder sb = new StringBuilder();
        for (WebElement e : driver.findElements(By.xpath("//div[@role='dialog']//p[contains(@class,'text-destructive')]"))) {
            try {
                String t = e.getText();
                if (e.isDisplayed() && t != null && t.trim().length() > 1) {
                    sb.append(t.trim()).append("; ");
                }
            } catch (Exception ignored) {
            }
        }
        for (WebElement el : driver.findElements(By.cssSelector("[role='dialog'] input:invalid, [role='dialog'] textarea:invalid"))) {
            try {
                String vm = el.getAttribute("validationMessage");
                if (vm != null && !vm.isBlank()) {
                    sb.append(vm.trim()).append("; ");
                }
            } catch (Exception ignored) {
            }
        }
        return sb.toString().trim();
    }

    public void clickDeleteButtonForEvent(String eventTitle) {
        logger.info("Clicking delete button for event: {}", eventTitle);
        // AdminEvents cards use p-6, line-clamp on h3; Delete is a full-width outline button with Trash2 + text "Delete".
        String esc = eventTitle.replace("\"", "'");
        List<By> candidates = List.of(
                By.xpath(String.format(
                        "//h3[contains(normalize-space(),\"%s\")]/ancestor::div[contains(@class,'p-6')][1]//button[contains(normalize-space(.),'Delete')]",
                        esc)),
                By.xpath(String.format(
                        "//h3[contains(normalize-space(),\"%s\")]/ancestor::div[contains(@class,'p-4') or contains(@class,'p-6')][1]//button[contains(.,'Delete')]",
                        esc)),
                By.xpath(String.format(
                        "//h3[contains(normalize-space(),\"%s\")]/ancestor::div[contains(@class,'border')][1]//button[contains(.,'Delete')]",
                        esc)),
                By.xpath(String.format(
                        "//*[self::h3][contains(normalize-space(),\"%s\")]/following::button[contains(.,'Delete')][1]",
                        esc))
        );
        WebElement deleteButton = WebDriverWaitUtil.waitForAnyElementVisible(candidates, 25);
        WebDriverWaitUtil.waitForElementClickable(deleteButton);
        click(deleteButton);
    }

    public void confirmDeletion() {
        logger.info("Confirming deletion");
        try {
            WebDriverWaitUtil.waitForAlert().accept();
        } catch (Exception e) {
            logger.debug("No browser alert: {}", e.getMessage());
        }
    }

    public String getToastMessage() {
        return ToastUtil.getLatestToastText();
    }

    public boolean isToastMessageContaining(String expectedText) {
        String actual = getToastMessage();
        return actual.toLowerCase().contains(expectedText.toLowerCase());
    }

    public boolean isEventInList(String eventTitle) {
        logger.debug("Checking if event '{}' exists in the list", eventTitle);
        List<By> locators = List.of(
                By.xpath(String.format("//h3[contains(normalize-space(),\"%s\")]", eventTitle)),
                By.xpath(String.format("//*[@role='main']//*[contains(normalize-space(),\"%s\")]", eventTitle))
        );
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    public boolean isEventNotInList(String eventTitle) {
        return !isEventInList(eventTitle);
    }

    public boolean deleteEventIfExists(String eventTitlePrefix) {
        logger.info("Deleting event starting with '{}' if present", eventTitlePrefix);
        String xpath = String.format("//h3[starts-with(normalize-space(),\"%s\")]", eventTitlePrefix);
        List<WebElement> found = driver.findElements(By.xpath(xpath));
        if (found.isEmpty() || !found.get(0).isDisplayed()) {
            return false;
        }
        String fullTitle = found.get(0).getText().trim();
        clickDeleteButtonForEvent(fullTitle);
        confirmDeletion();
        return true;
    }
}
