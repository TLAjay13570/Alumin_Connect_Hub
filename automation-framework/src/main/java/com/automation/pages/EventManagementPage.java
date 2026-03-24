package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.JavaScriptExecutorUtil;
import com.automation.utils.ToastUtil;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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

    /**
     * Maps UI labels from features to Radix {@code SelectItem} {@code value} (see {@code EventModal.tsx} EVENT_TYPES).
     */
    private static String categoryLabelToRadixValue(String categoryLabel) {
        if (categoryLabel == null) {
            return "";
        }
        String k = categoryLabel.trim().toLowerCase(Locale.US);
        switch (k) {
            case "networking":
                return "networking";
            case "social":
                return "social";
            case "professional development":
            case "professional":
                return "professional";
            case "technology":
                return "technology";
            case "career":
                return "career";
            case "sports":
                return "sports";
            case "cultural":
                return "cultural";
            case "academic":
                return "academic";
            case "reunion":
                return "reunion";
            case "workshop":
                return "workshop";
            case "webinar":
                return "webinar";
            case "other":
                return "other";
            default:
                return k.replace(" ", "-");
        }
    }

    public void selectCategory(String categoryLabel) {
        logger.info("Selecting event type/category: {}", categoryLabel);
        String wanted = categoryLabel == null ? "" : categoryLabel.trim();
        String radixValue = categoryLabelToRadixValue(categoryLabel);
        List<By> triggerCandidates = List.of(
                By.cssSelector("#event-type"),
                By.xpath("//div[@role='dialog']//*[@id='event-type']"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Event Type') or contains(.,'Type')]/following::button[1]"),
                By.xpath("//div[@role='dialog']//button[@role='combobox'][1]")
        );
        WebElement trigger = WebDriverWaitUtil.waitForElementClickable(
                WebDriverWaitUtil.waitForAnyElementVisible(triggerCandidates));
        JavaScriptExecutorUtil.scrollToElement(trigger);
        click(trigger);
        WebDriverWaitUtil.staticWait(1);

        // Wait for a visible portaled listbox (Radix); avoid matching options from other widgets.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        wait.until(d -> Boolean.TRUE.equals(JavaScriptExecutorUtil.executeScript(
                "var lbs = document.querySelectorAll('[role=\"listbox\"]');"
                        + "for (var j = 0; j < lbs.length; j++) {"
                        + "  var br = lbs[j].getBoundingClientRect();"
                        + "  if (br.width > 1 && br.height > 1) return true;"
                        + "}"
                        + "var opts = document.querySelectorAll('[role=\"option\"]');"
                        + "for (var i = 0; i < opts.length; i++) {"
                        + "  var b = opts[i].getBoundingClientRect();"
                        + "  if (b.width > 1 && b.height > 1) return true;"
                        + "}"
                        + "return false;")));

        Boolean clicked = (Boolean) JavaScriptExecutorUtil.executeScript(
                "var val = (arguments[0] || '').toLowerCase();"
                        + "var want = (arguments[1] || '').trim().toLowerCase();"
                        + "function pick(el) {"
                        + "  if (!el) return false;"
                        + "  el.scrollIntoView({block:'nearest', inline:'nearest'});"
                        + "  el.click();"
                        + "  return true;"
                        + "}"
                        + "function matches(n) {"
                        + "  var dv = (n.getAttribute('data-value') || '').toLowerCase();"
                        + "  if (dv && dv === val) return true;"
                        + "  var raw = (n.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();"
                        + "  return raw === want || (want.length > 1 && raw.indexOf(want) >= 0);"
                        + "}"
                        + "var lbs = document.querySelectorAll('[role=\"listbox\"]');"
                        + "for (var j = 0; j < lbs.length; j++) {"
                        + "  var lb = lbs[j];"
                        + "  var br = lb.getBoundingClientRect();"
                        + "  if (br.width < 2 || br.height < 2) continue;"
                        + "  var opts = lb.querySelectorAll('[role=\"option\"]');"
                        + "  for (var i = 0; i < opts.length; i++) {"
                        + "    var n = opts[i];"
                        + "    n.scrollIntoView({block:'nearest', inline:'nearest'});"
                        + "    if (matches(n)) return pick(n);"
                        + "  }"
                        + "}"
                        + "var all = document.querySelectorAll('[role=\"option\"]');"
                        + "for (var k = 0; k < all.length; k++) {"
                        + "  var n = all[k];"
                        + "  var b = n.getBoundingClientRect();"
                        + "  if (b.width < 2 || b.height < 2) continue;"
                        + "  n.scrollIntoView({block:'nearest', inline:'nearest'});"
                        + "  if (matches(n)) return pick(n);"
                        + "}"
                        + "return false;",
                radixValue,
                wanted);
        if (!Boolean.TRUE.equals(clicked)) {
            throw new TimeoutException(
                    "Could not select event category \"" + categoryLabel + "\" (value=" + radixValue + ").");
        }
    }

    /**
     * Event modal is scrollable; fields at the bottom (image URL, registration deadline) are not visible until scrolled.
     */
    private void scrollEventModalToBottom() {
        WebDriverWaitUtil.waitForElementVisible(DIALOG);
        for (int pass = 0; pass < 4; pass++) {
            JavaScriptExecutorUtil.executeScript(
                    "var root = document.querySelector('[role=\"dialog\"]');"
                            + "if (!root) return;"
                            + "var inner = root.querySelector('.overflow-y-auto') || root.querySelector('[class*=\"overflow-y\"]');"
                            + "var el = inner || root;"
                            + "el.scrollTop = el.scrollHeight;");
            WebDriverWaitUtil.staticWait(1);
        }
    }

    private WebElement findFirstInDialog(List<By> locators) {
        for (By locator : locators) {
            List<WebElement> found = driver.findElements(locator);
            for (WebElement element : found) {
                if (element != null) {
                    return element;
                }
            }
        }
        return null;
    }

    public void enterImageUrl(String imageUrl) {
        logger.info("Entering image URL");
        scrollEventModalToBottom();
        List<By> locators = List.of(
                By.id("event-image"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Image')]"
                        + "[contains(.,'URL') or contains(.,'Url')]/following::input[1]"),
                By.xpath("//div[@role='dialog']//input[@type='url' and not(@id='meeting-link')]"));
        WebElement el = null;
        try {
            el = WebDriverWaitUtil.waitForAnyElementVisible(locators, 8);
        } catch (TimeoutException e) {
            logger.debug("Image URL field not visible within 8s; trying DOM presence / JS lookup");
        }
        if (el == null) {
            el = findFirstInDialog(locators);
        }
        if (el == null) {
            Object o = JavaScriptExecutorUtil.executeScript(
                    "return document.querySelector('#event-image') "
                            + "|| document.querySelector('[role=\"dialog\"] input[placeholder*=\"image\"]') "
                            + "|| document.querySelector('[role=\"dialog\"] input[placeholder*=\".jpg\"]');");
            if (o instanceof WebElement) {
                el = (WebElement) o;
            }
        }
        if (el == null) {
            throw new TimeoutException(
                    "Event image URL input not found in modal (missing on deploy or different DOM).");
        }
        JavaScriptExecutorUtil.scrollToElement(el);
        WebDriverWaitUtil.staticWait(1);
        try {
            el.clear();
            el.sendKeys(imageUrl);
        } catch (Exception ex) {
            logger.debug("sendKeys failed on image URL; using React input setter: {}", ex.getMessage());
            JavaScriptExecutorUtil.setReactInputValue(el, imageUrl);
        }
    }

    private WebElement findPrimaryModalSubmitButton() {
        List<WebElement> buttons = driver.findElements(
                By.xpath("//div[@role='dialog']//button[contains(normalize-space(),'Create Event') "
                        + "or contains(normalize-space(),'Update Event')]"));
        for (WebElement b : buttons) {
            try {
                if (b.isDisplayed() && b.isEnabled()) {
                    return b;
                }
            } catch (Exception ignored) {
            }
        }
        return WebDriverWaitUtil.waitForElementClickable(
                By.xpath("//div[@role='dialog']//button[contains(.,'Create Event') or contains(.,'Update Event')]"));
    }

    public void clickCreateEventButtonInModal() {
        logger.info("Clicking Create Event in modal");
        WebElement submit = findPrimaryModalSubmitButton();
        JavaScriptExecutorUtil.scrollToElement(submit);
        click(submit);
        waitForSuccessfulModalSubmit();
    }

    public void clickUpdateEventButtonInModal() {
        logger.info("Clicking Update Event in modal");
        WebElement submit = findPrimaryModalSubmitButton();
        JavaScriptExecutorUtil.scrollToElement(submit);
        click(submit);
        waitForSuccessfulModalSubmit();
    }

    private void waitForSuccessfulModalSubmit() {
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
                try {
                    List<WebElement> titles = d.findElements(By.id("event-title"));
                    if (titles.isEmpty()) {
                        return true;
                    }
                    for (WebElement t : titles) {
                        try {
                            if (t.isDisplayed()) {
                                return false;
                            }
                        } catch (StaleElementReferenceException stale) {
                            return false;
                        }
                    }
                    return true;
                } catch (StaleElementReferenceException stale) {
                    return false;
                }
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

    /**
     * Clicks Create/Update in the modal and waits until validation messages appear (negative tests).
     */
    public void clickModalSubmitExpectingValidation() {
        logger.info("Submitting event form expecting validation errors");
        WebElement submit = findPrimaryModalSubmitButton();
        JavaScriptExecutorUtil.scrollToElement(submit);
        click(submit);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(d -> !collectDialogErrors().isBlank());
    }

    public String getModalValidationErrorsText() {
        return collectDialogErrors();
    }

    public void clearEventTitleField() {
        WebElement el = WebDriverWaitUtil.waitForElementVisible(By.id("event-title"));
        el.clear();
    }

    public void clearEventDescriptionField() {
        WebElement el = WebDriverWaitUtil.waitForElementVisible(By.id("event-description"));
        el.clear();
    }

    public void enterMeetingLink(String url) {
        logger.info("Entering meeting link");
        WebElement el = WebDriverWaitUtil.waitForElementVisible(By.id("meeting-link"));
        el.clear();
        el.sendKeys(url);
    }

    public void togglePublicEventOff() {
        logger.info("Turning public event off");
        WebElement dialog = WebDriverWaitUtil.waitForElementVisible(DIALOG);
        JavaScriptExecutorUtil.executeScript(
                "var root = arguments[0];"
                        + "var t = root.querySelector('#public-event');"
                        + "if (t) { t.scrollIntoView({block: 'center', inline: 'nearest'}); return; }"
                        + "var switches = root.querySelectorAll('[role=\"switch\"]');"
                        + "if (switches.length) { switches[switches.length - 1].scrollIntoView({block: 'center', inline: 'nearest'}); }",
                dialog);
        WebDriverWaitUtil.staticWait(1);

        List<By> candidates = List.of(
                By.cssSelector("[role='dialog'] #public-event"),
                By.cssSelector("#public-event"),
                By.xpath("//div[@role='dialog']//div[contains(@class,'bg-muted') and contains(@class,'rounded-lg')]//*[@role='switch']"),
                By.xpath("//div[@role='dialog']//*[@id='public-event']"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Public Event')]/ancestor::div[contains(@class,'justify-between')][1]//*[@role='switch']"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Public Event')]/ancestor::div[contains(@class,'rounded-lg')][1]//*[@role='switch']"));

        WebElement toggle;
        try {
            toggle = WebDriverWaitUtil.waitForElementClickable(
                    WebDriverWaitUtil.waitForAnyElementVisible(candidates, ConfigReader.getExplicitWait() + 10));
        } catch (TimeoutException e) {
            Object found = JavaScriptExecutorUtil.executeScript(
                    "return document.querySelector('[role=\"dialog\"] #public-event');");
            if (!(found instanceof WebElement)) {
                throw new TimeoutException(
                        "Public event switch not found in modal (#public-event). Is the deployed EventModal the same as source?",
                        e);
            }
            toggle = (WebElement) found;
            JavaScriptExecutorUtil.scrollToElement(toggle);
            WebDriverWaitUtil.waitForElementClickable(toggle);
        }

        JavaScriptExecutorUtil.scrollToElement(toggle);
        String state = toggle.getAttribute("data-state");
        String aria = toggle.getAttribute("aria-checked");
        if ("checked".equals(state) || "true".equals(aria)) {
            click(toggle);
        }
    }

    public void enterMaxAttendees(String value) {
        WebElement el = WebDriverWaitUtil.waitForElementVisible(By.id("max-attendees"));
        el.clear();
        el.sendKeys(value);
    }

    public void enterRegistrationDeadline(String date) {
        String iso = normalizeDateForHtml5(date);
        WebDriverWaitUtil.waitForElementVisible(DIALOG);
        scrollEventModalToBottom();
        List<By> deadlineLocators = List.of(
                By.xpath("//div[@role='dialog']//input[@type='date'][2]"),
                By.id("registration-deadline"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Registration')][contains(.,'eadline')]"
                        + "/following::input[@type='date'][1]"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Registration Deadline')]/following::input[1]"));
        WebElement el = null;
        try {
            el = WebDriverWaitUtil.waitForAnyElementVisible(deadlineLocators, 10);
        } catch (TimeoutException e) {
            logger.debug("Registration deadline not visible; trying DOM presence / JS lookup");
        }
        if (el == null) {
            el = findFirstInDialog(deadlineLocators);
        }
        if (el == null) {
            Object o = JavaScriptExecutorUtil.executeScript(
                    "var d = document.querySelector('[role=\"dialog\"]');"
                            + "if (!d) return null;"
                            + "var reg = d.querySelector('#registration-deadline');"
                            + "if (reg) return reg;"
                            + "var dates = d.querySelectorAll('input[type=\"date\"]');"
                            + "return dates.length >= 2 ? dates[1] : null;");
            if (o instanceof WebElement) {
                el = (WebElement) o;
            }
        }
        if (el == null) {
            throw new TimeoutException(
                    "Registration deadline input not found in modal (missing on deploy or different DOM).");
        }
        JavaScriptExecutorUtil.executeScript(
                "arguments[0].scrollIntoView({block:'center',inline:'nearest'});", el);
        WebDriverWaitUtil.staticWait(1);
        String inputType = el.getAttribute("type");
        if (inputType != null && "date".equalsIgnoreCase(inputType)) {
            try {
                JavaScriptExecutorUtil.setAttribute(el, "value", iso);
                JavaScriptExecutorUtil.executeScript(
                        "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));"
                                + "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));", el);
            } catch (Exception ex) {
                logger.debug("Native date set failed; using React input setter: {}", ex.getMessage());
                JavaScriptExecutorUtil.setReactInputValue(el, iso);
            }
        } else {
            try {
                el.clear();
                el.sendKeys(date);
            } catch (Exception ex) {
                JavaScriptExecutorUtil.setReactInputValue(el, iso);
            }
        }
    }

    public void clickCancelInEventModal() {
        logger.info("Clicking Cancel in event modal");
        WebElement btn = WebDriverWaitUtil.waitForElementClickable(
                By.xpath("//div[@role='dialog']//button[contains(normalize-space(),'Cancel')]"));
        click(btn);
        WebDriverWaitUtil.staticWait(1);
    }

    public boolean isEventModalOpen() {
        try {
            List<WebElement> dialogs = driver.findElements(DIALOG);
            for (WebElement d : dialogs) {
                if (!d.isDisplayed()) {
                    continue;
                }
                for (WebElement t : d.findElements(By.id("event-title"))) {
                    if (t.isDisplayed()) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Radix/shadcn leaves a full-screen {@code fixed inset-0 z-50} overlay open if a dialog did not unmount cleanly.
     * That layer intercepts clicks on the header refresh button.
     */
    private void dismissBlockingRadixOverlays() {
        By backdrop = By.cssSelector("div.fixed.inset-0.z-50[data-state='open']");
        for (int i = 0; i < 12; i++) {
            boolean blocking = false;
            for (WebElement el : driver.findElements(backdrop)) {
                try {
                    if (el.isDisplayed()) {
                        blocking = true;
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
            if (!blocking) {
                return;
            }
            logger.debug("Dismissing blocking overlay (attempt {})", i + 1);
            try {
                new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            } catch (Exception e) {
                logger.debug("Escape to dismiss overlay: {}", e.getMessage());
            }
            WebDriverWaitUtil.staticWait(1);
        }
    }

    public void clickRefreshEventsList() {
        logger.info("Clicking refresh on event management page");
        dismissBlockingRadixOverlays();
        WebElement refresh = WebDriverWaitUtil.waitForElementClickable(
                By.xpath("//h2[contains(normalize-space(),'Event Management')]/ancestor::div[contains(@class,'p-6')][1]"
                        + "//div[contains(@class,'flex') and contains(@class,'gap-2')]//button[1]"));
        try {
            click(refresh);
        } catch (ElementClickInterceptedException e) {
            logger.warn("Refresh click intercepted; using JS click. Cause: {}", e.getMessage());
            JavaScriptExecutorUtil.executeScript(
                    "arguments[0].scrollIntoView({block:'center',inline:'nearest'}); arguments[0].click();", refresh);
        }
        WebDriverWaitUtil.waitForPageToLoad();
    }

    public void enterEventsSearchQuery(String query) {
        logger.info("Searching events for: {}", query);
        List<By> candidates = List.of(
                By.xpath("//input[@placeholder='Search events...']"),
                By.xpath("//input[contains(@placeholder,'Search') and contains(@placeholder,'event')]"),
                By.xpath("//h2[contains(normalize-space(),'Event Management')]/ancestor::div[contains(@class,'p-6')][1]"
                        + "//input[@type='text' or not(@type)]")
        );
        WebElement input = WebDriverWaitUtil.waitForAnyElementVisible(candidates);
        input.clear();
        input.sendKeys(query);
        WebDriverWaitUtil.staticWait(1);
    }

    public void clickEventsStatusFilter(String label) {
        logger.info("Clicking status filter: {}", label);
        WebElement chip = WebDriverWaitUtil.waitForElementClickable(
                By.xpath(String.format(
                        "//button[contains(@class,'rounded-lg')][.//p[normalize-space()='%s']]", label)));
        click(chip);
        WebDriverWaitUtil.staticWait(1);
    }

    public void clickEditForEvent(String titleSubstring) {
        logger.info("Clicking Edit for event containing: {}", titleSubstring);
        String esc = titleSubstring.replace("\"", "'");
        WebElement card = findEventCardForTitle(esc);
        JavaScriptExecutorUtil.scrollToElement(card);
        WebDriverWaitUtil.staticWait(1);

        List<WebElement> buttons = card.findElements(By.tagName("button"));
        for (WebElement b : buttons) {
            try {
                if (!b.isDisplayed() || !b.isEnabled()) {
                    continue;
                }
                String txt = b.getText() != null ? b.getText().trim().toLowerCase() : "";
                String aria = b.getAttribute("aria-label");
                String ariaLc = aria != null ? aria.toLowerCase() : "";
                if (txt.contains("edit") || ariaLc.contains("edit")) {
                    JavaScriptExecutorUtil.executeScript(
                            "arguments[0].scrollIntoView({block:'center',inline:'nearest'}); arguments[0].click();",
                            b);
                    WebDriverWaitUtil.waitForElementVisible(By.id("event-title"));
                    return;
                }
            } catch (Exception ignored) {
            }
        }

        // Icon-only Edit (no text / aria): AdminEvents uses Edit then Delete in a flex row — first non-destructive button.
        List<WebElement> rowButtons = card.findElements(
                By.xpath(".//div[contains(@class,'flex') and contains(@class,'gap-2')]//button"));
        for (WebElement b : rowButtons) {
            try {
                if (!b.isDisplayed() || !b.isEnabled()) {
                    continue;
                }
                String cls = b.getAttribute("class");
                if (cls != null && cls.contains("text-destructive")) {
                    continue;
                }
                logger.info("Using first non-destructive action button as Edit for: {}", titleSubstring);
                JavaScriptExecutorUtil.executeScript(
                        "arguments[0].scrollIntoView({block:'center',inline:'nearest'}); arguments[0].click();",
                        b);
                WebDriverWaitUtil.waitForElementVisible(By.id("event-title"));
                return;
            } catch (Exception ignored) {
            }
        }

        throw new TimeoutException("Could not find Edit control in event card for title containing: " + titleSubstring);
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

    /**
     * Finds the event card root for a title. Prefers shadcn Card (rounded-lg + shadow) so we do not match inner
     * elements that also use rounded-lg (e.g. images). Deployed builds may differ from local AdminEvents.tsx
     * (e.g. icon-only delete, extra "Attendees" button), so delete resolution is done in-card, not only by "Delete" text.
     */
    private WebElement findEventCardForTitle(String escapedTitle) {
        List<By> cardRoots = List.of(
                By.xpath(String.format(
                        "//h3[contains(normalize-space(),\"%s\")]/ancestor::div[contains(@class,'rounded-lg') "
                                + "and contains(@class,'shadow')][1]",
                        escapedTitle)),
                By.xpath(String.format(
                        "//h3[contains(normalize-space(),\"%s\")]/ancestor::div[contains(@class,'rounded-lg') "
                                + "and contains(@class,'border')][1]",
                        escapedTitle)),
                By.xpath(String.format(
                        "//h3[contains(normalize-space(),\"%s\")]/ancestor::div[contains(@class,'p-6')][1]",
                        escapedTitle))
        );
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return wait.until(d -> {
            for (By locator : cardRoots) {
                List<WebElement> found = d.findElements(locator);
                for (WebElement el : found) {
                    try {
                        if (el.isDisplayed()) {
                            return el;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            return null;
        });
    }

    private WebElement resolveDeleteButtonInsideCard(WebElement card, String eventTitle) {
        JavaScriptExecutorUtil.scrollToElement(card);
        WebDriverWaitUtil.staticWait(1);

        List<WebElement> buttons = card.findElements(By.tagName("button"));
        if (buttons.isEmpty()) {
            buttons = card.findElements(By.cssSelector("[role='button']"));
        }

        // 1) Explicit label / text (local dev UI: "Delete")
        for (WebElement b : buttons) {
            try {
                if (!b.isDisplayed() || !b.isEnabled()) {
                    continue;
                }
                String txt = b.getText() != null ? b.getText().trim().toLowerCase() : "";
                if (txt.contains("delete")) {
                    return b;
                }
                String aria = b.getAttribute("aria-label");
                if (aria != null && aria.toLowerCase().contains("delete")) {
                    return b;
                }
                String cls = b.getAttribute("class");
                if (cls != null && cls.contains("text-destructive") && !txt.contains("attendee")) {
                    return b;
                }
            } catch (Exception ignored) {
            }
        }

        // 2) Deployed UI: trash-only delete (no "Delete" string) with Attendees + Edit + Delete — take last
        //    sensible control: not Edit, not Attendees, still enabled.
        for (int i = buttons.size() - 1; i >= 0; i--) {
            WebElement b = buttons.get(i);
            try {
                if (!b.isDisplayed() || !b.isEnabled()) {
                    continue;
                }
                String txt = b.getText() != null ? b.getText().trim().toLowerCase() : "";
                if (txt.contains("edit") || txt.contains("attendee")) {
                    continue;
                }
                logger.info("Using fallback delete control (icon-only or unlabeled) for event: {}", eventTitle);
                return b;
            } catch (Exception ignored) {
            }
        }

        // 3) Last visible enabled button on card (common DOM order: …, Edit, Delete)
        for (int i = buttons.size() - 1; i >= 0; i--) {
            WebElement b = buttons.get(i);
            try {
                if (b.isDisplayed() && b.isEnabled()) {
                    return b;
                }
            } catch (Exception ignored) {
            }
        }

        throw new RuntimeException(
                "Could not resolve delete control inside event card for title: " + eventTitle
                        + " (button count=" + buttons.size() + "). UI may have changed.");
    }

    public void clickDeleteButtonForEvent(String eventTitle) {
        logger.info("Clicking delete button for event: {}", eventTitle);
        String esc = eventTitle.replace("\"", "'");

        List<WebElement> titleHits = driver.findElements(
                By.xpath(String.format("//h3[contains(normalize-space(),\"%s\")]", esc)));
        for (WebElement h3 : titleHits) {
            try {
                if (h3.isDisplayed()) {
                    JavaScriptExecutorUtil.executeScript(
                            "arguments[0].scrollIntoView({block:'center',inline:'nearest'});", h3);
                    break;
                }
            } catch (Exception ignored) {
            }
        }
        WebDriverWaitUtil.staticWait(1);

        WebElement card = findEventCardForTitle(esc);
        WebElement deleteButton = resolveDeleteButtonInsideCard(card, eventTitle);
        JavaScriptExecutorUtil.scrollToElement(deleteButton);
        WebDriverWaitUtil.staticWait(1);
        // Admin /events uses a sticky top bar (AdminEventsPage). Native click often hits that bar
        // (ElementClickInterceptedException at ~y=18) even when the delete button is found — use JS click.
        JavaScriptExecutorUtil.executeScript(
                "arguments[0].scrollIntoView({block:'center',inline:'nearest'}); arguments[0].click();",
                deleteButton);
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

    /**
     * True if an event card title (h3 in the admin grid) shows this text. Avoids {@code role=main} text scans that
     * match toasts, confirm strings, or other incidental copies of the title.
     */
    public boolean isEventInList(String eventTitle) {
        logger.debug("Checking if event '{}' exists in the list", eventTitle);
        String esc = eventTitle.replace("\"", "'");
        // Prefer list titles outside the modal; prod builds may omit Tailwind tokens like lg:grid-cols-2 from class strings.
        List<By> locators = List.of(
                By.xpath(String.format(
                        "//h3[contains(normalize-space(),\"%s\")][not(ancestor::div[@role='dialog'])]",
                        esc)),
                By.xpath(String.format(
                        "//div[contains(@class,'lg:grid-cols-2') and contains(@class,'grid')]//h3[contains(normalize-space(),\"%s\")]",
                        esc)),
                By.xpath(String.format(
                        "//div[contains(@class,'grid-cols-1') and contains(@class,'gap-4')]//h3[contains(normalize-space(),\"%s\")]",
                        esc)),
                By.xpath(String.format("//h3[contains(normalize-space(),\"%s\")]", esc))
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

    /**
     * After create, the grid may render after modal close; poll then refresh once if needed.
     */
    public boolean waitUntilEventInList(String eventTitle) {
        int primary = Math.max(20, ConfigReader.getExplicitWait() + 10);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(primary));
        try {
            wait.until(d -> isEventInList(eventTitle));
            return true;
        } catch (TimeoutException e) {
            logger.info("Event not visible after {}s; refreshing list", primary);
            try {
                clickRefreshEventsList();
                WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(15));
                wait2.until(d -> isEventInList(eventTitle));
                return true;
            } catch (TimeoutException e2) {
                return false;
            }
        }
    }

    /**
     * After delete, the list updates asynchronously; poll then optionally refresh once.
     */
    public boolean waitUntilEventNotInList(String eventTitle) {
        int primary = Math.max(20, ConfigReader.getExplicitWait() + 10);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(primary));
        try {
            wait.until(d -> !isEventInList(eventTitle));
            return true;
        } catch (TimeoutException e) {
            logger.info("Event still visible after {}s; refreshing list", primary);
            try {
                clickRefreshEventsList();
                WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(15));
                wait2.until(d -> !isEventInList(eventTitle));
                return true;
            } catch (TimeoutException e2) {
                return false;
            }
        }
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
