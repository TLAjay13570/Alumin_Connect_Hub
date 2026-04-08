package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.WebDriverWaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the Alumni Support Ticket module ({@code /support}).
 *
 * <p>Source reference: {@code Support.tsx}, {@code SupportTicketModal.tsx}
 *
 * <p>Key UI facts:
 * <ul>
 *   <li>Page heading: {@code <h1>Support Center</h1>}</li>
 *   <li>"New Ticket" button is in the sticky header; text is inside
 *       {@code <span class="hidden sm:inline">New Ticket</span>}</li>
 *   <li>Empty-state shows "Create Your First Ticket" button instead</li>
 *   <li>Modal title: "Submit Support Request" (Radix UI Dialog)</li>
 *   <li>Form field IDs: {@code subject}, {@code category} (SelectTrigger), {@code priority}
 *       (SelectTrigger), {@code description}</li>
 *   <li>Category options display text: "General Inquiry", "Technical Issue",
 *       "Academic Records", "Events & Programs", "Mentorship", "Other"</li>
 *   <li>Priority options: "Low", "Medium", "High"</li>
 *   <li>Submit button text: "Submit Ticket"</li>
 *   <li>Success toast title: "Ticket Submitted"</li>
 *   <li>Validation toast: "Error" / "Please fill in all required fields."</li>
 *   <li>Each ticket card has a ChevronDown/Up toggle button — click to expand/collapse</li>
 *   <li>Conversation section heading: "Conversation"</li>
 * </ul>
 */
public class SupportPage extends BasePage {

    // ── Page shell ────────────────────────────────────────────────────────────
    // <h1 class="...">Support Center</h1>
    private static final By PAGE_HEADING = By.xpath(
            "//h1[contains(normalize-space(.),'Support Center')]");

    // ── Create-ticket trigger ─────────────────────────────────────────────────
    // Header button: <Button><span class="hidden sm:inline">New Ticket</span></Button>
    // Anchored to the sticky header containing the page h1 to avoid matching Submit/other buttons.
    private static final By BTN_NEW_TICKET = By.xpath(
            "//h1[contains(normalize-space(.),'Support Center')]"
                    + "/ancestor::div[contains(@class,'sticky')][1]"
                    + "//button[.//span[normalize-space(.)='New Ticket']]");

    // Empty-state card: "Create Your First Ticket" (shown when user has no tickets)
    private static final By BTN_CREATE_FIRST_TICKET = By.xpath(
            "//button[contains(normalize-space(.),'Create Your First Ticket')]");

    // ── Dialog / Modal ────────────────────────────────────────────────────────
    // Radix Dialog renders: <div role="dialog" aria-modal="true" data-state="open">
    private static final By DIALOG = By.xpath("//div[@role='dialog']");

    // ── Form fields (all have explicit id attributes in SupportTicketModal.tsx) ──
    // <Input id="subject" placeholder="Brief description of your issue" />
    private static final By INPUT_SUBJECT = By.id("subject");
    // <SelectTrigger id="category"> (Radix Select renders as button[@role='combobox'])
    private static final By TRIGGER_CATEGORY = By.xpath(
            "//button[@role='combobox'][@id='category']");
    // <SelectTrigger id="priority">
    private static final By TRIGGER_PRIORITY = By.xpath(
            "//button[@role='combobox'][@id='priority']");
    // <Textarea id="description" placeholder="Please provide detailed information..." />
    private static final By INPUT_DESCRIPTION = By.id("description");
    // <Button type="submit">Submit Ticket</Button>
    private static final By BTN_SUBMIT_TICKET = By.xpath(
            "//div[@role='dialog']//button[contains(normalize-space(.),'Submit Ticket')]"
                    + " | //div[@role='dialog']//button[@type='submit']");

    // ── Conversation section ──────────────────────────────────────────────────
    // <h4 class="font-semibold ...">Conversation</h4>  (shown after ticket is expanded)
    private static final By CONVERSATION_HEADING = By.xpath(
            "//h4[contains(normalize-space(.),'Conversation')]");

    // ── Validation (inline + toast) ───────────────────────────────────────────
    // App uses toast notifications for form validation (title "Error")
    // Also covers role="alert" inline errors as fallback.
    private static final By ERROR_SIGNALS = By.xpath(
            "//*[@role='alert'][string-length(normalize-space(.)) > 0]"
                    + " | //*[@role='status'][string-length(normalize-space(.)) > 0]"
                    + " | //ol[contains(@class,'fixed')]//li[@data-state='open']"
                    + "     [string-length(normalize-space(.)) > 0]");

    // ═════════════════════════════════════════════════════════════════════════
    // URL helpers
    // ═════════════════════════════════════════════════════════════════════════

    private static String buildSupportUrl() {
        String custom = ConfigReader.getProperty("alumni.support.url");
        if (custom != null && !custom.isBlank()) {
            return custom;
        }
        String base = ConfigReader.getBaseUrl();
        return (base.endsWith("/") ? base : base + "/") + "support";
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation
    // ═════════════════════════════════════════════════════════════════════════

    /** Navigates to the alumni support page and waits for the page heading. */
    public void navigateToSupportPage() {
        navigateTo(buildSupportUrl());
        waitForPageShell();
    }

    public void waitForPageShell() {
        new WebDriverWait(driver, Duration.ofSeconds(Math.max(20, ConfigReader.getExplicitWait())))
                .until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
    }

    public boolean isOnSupportPage() {
        return driver.findElements(PAGE_HEADING).stream().anyMatch(WebElement::isDisplayed);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Create Ticket
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Clicks the "New Ticket" button in the sticky header (or the empty-state
     * "Create Your First Ticket" button) and waits for the modal form to appear.
     *
     * <p>Uses {@code By.id("subject")} as the definitive wait condition — more
     * reliable than waiting for the dialog container because Radix UI portals
     * can briefly be in the DOM before becoming fully interactive.
     */
    public void clickCreateTicket() {
        // Locate the button — prefer the header variant first
        WebElement btn;
        try {
            btn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(BTN_NEW_TICKET));
        } catch (TimeoutException e) {
            logger.debug("Header 'New Ticket' button not found — trying 'Create Your First Ticket'");
            btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.elementToBeClickable(BTN_CREATE_FIRST_TICKET));
        }

        // Scroll into view (sticky header is always at top — scroll is usually a no-op)
        final JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn);

        // Click — try native click first (triggers React synthetic events); fall back to JS click
        try {
            btn.click();
        } catch (Exception e) {
            logger.debug("Native click failed ({}), falling back to JS click", e.getMessage());
            js.executeScript("arguments[0].click();", btn);
        }

        // Wait specifically for the subject input — confirms the dialog opened AND is ready
        try {
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.visibilityOfElementLocated(INPUT_SUBJECT));
        } catch (TimeoutException first) {
            // Retry once — re-fetch the button and click again in case of stale element
            logger.warn("Dialog subject field not visible after first click — retrying once");
            try {
                WebElement retryBtn = driver.findElement(BTN_NEW_TICKET);
                js.executeScript("arguments[0].click();", retryBtn);
            } catch (Exception ignored) {
                try {
                    WebElement retryBtn = driver.findElement(BTN_CREATE_FIRST_TICKET);
                    js.executeScript("arguments[0].click();", retryBtn);
                } catch (Exception ignored2) {
                    // Let the next wait surface the real error
                }
            }
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.visibilityOfElementLocated(INPUT_SUBJECT));
        }
    }

    /** Clears and types the ticket subject. */
    public void enterSubject(String subject) {
        WebElement el = waitForFieldVisible(INPUT_SUBJECT);
        el.clear();
        el.sendKeys(subject);
    }

    /**
     * Selects a Category from the Radix Select.
     *
     * <p>Trigger: {@code <SelectTrigger id="category">} — matched by id, not label proximity
     * (two comboboxes share the same grid row, so label-proximity XPath is ambiguous).
     *
     * <p>Options display text: "General Inquiry", "Technical Issue", "Academic Records",
     * "Events &amp; Programs", "Mentorship", "Other".
     * Pass any unique substring (e.g. "Technical" matches "Technical Issue").
     */
    public void selectCategory(String displayText) {
        clickRadixSelect(TRIGGER_CATEGORY, displayText);
    }

    /**
     * Selects a Priority from the Radix Select.
     *
     * <p>Trigger: {@code <SelectTrigger id="priority">}
     * Options: "Low", "Medium", "High".
     */
    public void selectPriority(String displayText) {
        clickRadixSelect(TRIGGER_PRIORITY, displayText);
    }

    /** Clears and types the ticket description. */
    public void enterDescription(String description) {
        WebElement el = waitForFieldVisible(INPUT_DESCRIPTION);
        el.clear();
        el.sendKeys(description);
    }

    /** Submits the form and waits for the dialog to close (positive-path). */
    public void submitTicketForm() {
        clickSubmitTicket();
        waitForDialogClosed();
    }

    /**
     * Clicks Submit without waiting for the dialog to close (validation / negative-path).
     * Adds a 1-second static wait so the toast notification has time to appear.
     */
    public void attemptSubmitTicketForm() {
        clickSubmitTicket();
        WebDriverWaitUtil.staticWait(1);
    }

    /** Returns {@code true} if the create-ticket dialog is still visible. */
    public boolean isCreateFormOpen() {
        return driver.findElements(DIALOG).stream()
                .anyMatch(el -> { try { return el.isDisplayed(); } catch (Exception e) { return false; } })
                || driver.findElements(INPUT_SUBJECT).stream()
                .anyMatch(el -> { try { return el.isDisplayed(); } catch (Exception e) { return false; } });
    }

    /** Waits up to 20 s for the Radix Dialog to become invisible. */
    public void waitForDialogClosed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.invisibilityOfElementLocated(DIALOG));
        } catch (TimeoutException ignored) {
            // Some forms navigate away instead of closing a dialog — not a failure here
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Ticket List
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Polls until a ticket card containing the given subject appears in the list.
     */
    public boolean isTicketVisibleInList(String subject) {
        By locator = By.xpath(
                "//*[contains(normalize-space(.)," + xpathLiteral(subject) + ")]"
                        + "[not(ancestor::*[@role='dialog'])]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(Math.max(20, ConfigReader.getExplicitWait())))
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
            return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Returns the status badge text from the ticket card containing the given subject.
     *
     * <p>Status badge: {@code <span class="capitalize">{status.replace('-', ' ')}</span>}
     * Examples: "open" → "open", "in-progress" → "in progress" (CSS capitalize = "In Progress").
     */
    public String getTicketStatus(String subject) {
        // Status badge is inside the same card as the subject
        By statusLocator = By.xpath(
                "//*[contains(normalize-space(.)," + xpathLiteral(subject) + ")]"
                        + "[not(ancestor::*[@role='dialog'])]"
                        + "/ancestor::*[contains(@class,'overflow-hidden') or contains(@class,'rounded-lg')][1]"
                        + "//*[contains(@class,'capitalize')]"
                        + "[contains(normalize-space(.),'open') or contains(normalize-space(.),'progress')"
                        + " or contains(normalize-space(.),'resolved') or contains(normalize-space(.),'closed')]");
        try {
            WebElement badge = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(statusLocator));
            return badge.getText().trim();
        } catch (TimeoutException e) {
            logger.warn("Status badge not found for ticket: {}", subject);
            return "";
        }
    }

    /**
     * Expands the ticket card for the given subject by clicking the ChevronDown/Up toggle button.
     *
     * <p>DOM structure:
     * <pre>
     * &lt;div class="flex ... justify-between"&gt;
     *   &lt;div&gt; ...subject, badges... &lt;/div&gt;
     *   &lt;button&gt; &lt;svg/&gt; &lt;/button&gt;   ← toggle button (no text, icon only)
     * &lt;/div&gt;
     * </pre>
     */
    public void openTicketBySubject(String subject) {
        // The toggle button shares a justify-between flex container with the subject element
        By toggleBtn = By.xpath(
                "//*[contains(normalize-space(.)," + xpathLiteral(subject) + ")]"
                        + "[not(ancestor::*[@role='dialog'])]"
                        + "/ancestor::*[contains(@class,'justify-between')][1]"
                        + "/button");
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(toggleBtn));
        jsScrollIntoView(btn);
        jsClick(btn);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Ticket Detail / Conversation
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns {@code true} when the "Conversation" heading is visible
     * (ticket detail is expanded).
     */
    public boolean isConversationThreadVisible() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.visibilityOfElementLocated(CONVERSATION_HEADING));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Returns {@code true} when the given text appears anywhere on the page
     * (used to verify an admin reply is visible in the conversation).
     */
    public boolean isReplyVisibleInConversation(String replyText) {
        By locator = By.xpath(
                "//*[contains(normalize-space(.)," + xpathLiteral(replyText) + ")]");
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(Math.max(20, ConfigReader.getExplicitWait())))
                    .until(d -> {
                        List<WebElement> els = d.findElements(locator);
                        return els.stream().anyMatch(WebElement::isDisplayed) ? Boolean.TRUE : null;
                    });
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Validation
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns {@code true} if any validation error signal is visible.
     *
     * <p>The SupportTicketModal shows validation via a toast notification
     * (title "Error", description "Please fill in all required fields.") rather than
     * inline HTML errors.  This method covers both toast ({@code role="status"})
     * and classic inline {@code role="alert"} patterns.
     */
    public boolean isValidationErrorDisplayed() {
        return driver.findElements(ERROR_SIGNALS).stream()
                .anyMatch(el -> {
                    try {
                        if (!el.isDisplayed()) return false;
                        String text = el.getText().toLowerCase();
                        // Toast with "Error" title OR any element with error-related text
                        return text.contains("error") || text.contains("required")
                                || text.contains("fill") || text.contains("please enter");
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Clicks a Radix UI combobox trigger then selects an option by partial text match.
     *
     * <p><b>Why Actions instead of {@code element.click()} or JS click:</b><br>
     * Radix UI {@code SelectTrigger} opens the dropdown on {@code onPointerDown}, not {@code onClick}.
     * JavaScript's {@code element.click()} only dispatches a {@code click} event (no pointer events).
     * Selenium's {@code element.click()} via ChromeDriver fires the full pointer-event chain,
     * but can fail with {@code ElementClickInterceptedException} inside dialogs.
     * {@link org.openqa.selenium.interactions.Actions#click()} fires {@code mousemove → pointerdown →
     * mousedown → pointerup → mouseup → click} — the complete sequence Radix needs.
     *
     * @param triggerLocator {@code By} for the {@code button[@role='combobox']}
     * @param displayText    visible option text or a unique substring (partial match)
     */
    private void clickRadixSelect(By triggerLocator, String displayText) {
        WebElement trigger = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(triggerLocator));
        jsScrollIntoView(trigger);

        // Open the select — must fire pointer events (pointerdown) for Radix to respond
        actionsClick(trigger);

        // SelectContent renders in a portal — wait for role="option" elements to appear
        By optionLocator = By.xpath(
                "//*[@role='option'][contains(normalize-space(.)," + xpathLiteral(displayText) + ")]");
        WebElement option = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(optionLocator));
        actionsClick(option);
    }

    /**
     * Fires a full pointer-event sequence (pointerdown → pointerup → click) using
     * Selenium {@link org.openqa.selenium.interactions.Actions}.
     * Falls back to native {@code element.click()} if Actions throws.
     */
    private void actionsClick(WebElement el) {
        try {
            new org.openqa.selenium.interactions.Actions(driver)
                    .moveToElement(el)
                    .click()
                    .perform();
        } catch (Exception e) {
            logger.debug("Actions click failed ({}), falling back to native click", e.getMessage());
            try {
                el.click();
            } catch (Exception e2) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
        }
    }

    /** Clicks the "Submit Ticket" button inside the open dialog. */
    private void clickSubmitTicket() {
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(BTN_SUBMIT_TICKET));
        jsScrollIntoView(btn);
        jsClick(btn);
    }

    /**
     * Waits for a form field to be present in the DOM, scrolls it into view,
     * then waits for visibility.  Mirrors the pattern in FundraiserManagementPage
     * for {@code max-h-[90vh] overflow-y-auto} dialogs.
     */
    private WebElement waitForFieldVisible(By locator) {
        WebElement el = new WebDriverWait(driver, Duration.ofSeconds(Math.max(15, ConfigReader.getExplicitWait())))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', behavior:'instant'});", el);
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOf(el));
        return el;
    }

    private void jsScrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", el);
    }

    private void jsClick(WebElement el) {
        try {
            el.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    private static String xpathLiteral(String s) {
        if (s == null) return "''";
        if (!s.contains("'")) return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        String[] parts = s.split("'", -1);
        StringBuilder sb = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(", \"'\", ");
            sb.append("'").append(parts[i]).append("'");
        }
        sb.append(")");
        return sb.toString();
    }
}
