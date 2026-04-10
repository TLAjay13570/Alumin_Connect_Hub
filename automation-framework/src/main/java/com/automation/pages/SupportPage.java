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

    // ── Validation (toast) ──────────────────────────────────────────────────────
    // SupportTicketModal shows validation via toast: title "Error", variant "destructive".
    // Radix Toast renders as <li role="status" data-state="open"> inside <ol class="fixed ...">
    // The destructive variant adds class "destructive" on the <li>.
    private static final By ERROR_TOAST = By.xpath(
            "//li[@data-state='open'][contains(@class,'destructive')]"
                    + " | //li[@data-state='open'][.//text()[contains(.,'Error')]]"
                    + " | //*[@role='status'][string-length(normalize-space(.)) > 0]"
                    + " | //*[@role='alert'][string-length(normalize-space(.)) > 0]");

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
        selectFromRadixDropdown(TRIGGER_CATEGORY, displayText);
    }

    /**
     * Selects a Priority from the Radix Select.
     *
     * <p>Trigger: {@code <SelectTrigger id="priority">}
     * Options: "Low", "Medium", "High".
     */
    public void selectPriority(String displayText) {
        selectFromRadixDropdown(TRIGGER_PRIORITY, displayText);
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
     *
     * <p>The form has HTML5 {@code required} attributes on subject and description.
     * If either is empty, the browser's built-in validation prevents {@code onSubmit}
     * from firing — no React toast appears.  We click Submit (to trigger the native
     * validation tooltip) and then also check for the HTML5 validity state.
     */
    public void attemptSubmitTicketForm() {
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(BTN_SUBMIT_TICKET));
        jsScrollIntoView(btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
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
                        + "//button[contains(@class,'shrink')]");
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(toggleBtn));
        jsScrollIntoView(btn);
        // Native click to trigger React onClick handler
        clickWithRetry(btn, 3);
        // Wait for expansion animation
        WebDriverWaitUtil.staticWait(1);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Ticket Detail / Conversation
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns {@code true} when the "Conversation" heading is visible
     * (ticket detail is expanded).
     */
    public boolean isConversationThreadVisible() {
        // "Conversation" heading only appears when ticket has responses.
        // For a new ticket, check for "Description" or "Add a Response" as proof of expansion.
        By expandedContent = By.xpath(
                "//h4[contains(normalize-space(.),'Conversation')]"
                        + " | //*[contains(normalize-space(.),'Description')]"
                        + "     [ancestor::*[contains(@class,'border-t')]]"
                        + " | //textarea[contains(@placeholder,'message')]"
                        + " | //*[contains(normalize-space(.),'Add a Response')]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.visibilityOfElementLocated(expandedContent));
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
    /**
     * Returns {@code true} if validation errors are present.
     *
     * <p>Checks three layers:
     * <ol>
     *   <li>HTML5 native validation: subject or description has {@code :invalid} pseudo-class
     *       (browser blocks submit when {@code required} fields are empty)</li>
     *   <li>React toast: destructive toast with "Error" title</li>
     *   <li>Inline role="alert" elements</li>
     * </ol>
     */
    public boolean isValidationErrorDisplayed() {
        // 1. Check HTML5 native validation — required fields that are empty will be :invalid
        try {
            Boolean html5Invalid = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "var subj = document.getElementById('subject');"
                            + "var desc = document.getElementById('description');"
                            + "return (subj && !subj.validity.valid) || (desc && !desc.validity.valid);");
            if (Boolean.TRUE.equals(html5Invalid)) {
                return true;
            }
        } catch (Exception e) {
            logger.debug("HTML5 validity check failed: {}", e.getMessage());
        }

        // 2. Check for toast notifications
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(3)).until(d -> {
                List<WebElement> toasts = d.findElements(ERROR_TOAST);
                return toasts.stream().anyMatch(el -> {
                    try {
                        if (!el.isDisplayed()) return false;
                        String text = el.getText().toLowerCase();
                        return text.contains("error") || text.contains("required")
                                || text.contains("fill") || text.contains("please enter");
                    } catch (Exception ex) {
                        return false;
                    }
                }) ? Boolean.TRUE : null;
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Selects an option from a Radix UI Select dropdown.
     *
     * <p>Radix SelectTrigger opens on {@code onPointerDown} and checks
     * {@code event.pointerType !== ""} — requires {@code pointerType:'mouse'}.
     */
    public void selectFromRadixDropdown(By triggerLocator, String visibleText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));

        try {
            // Step 1: Wait & scroll to trigger
            WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(triggerLocator));
            jsScrollIntoView(trigger);

            // Step 2: Open dropdown using JS pointer events (Radix fix)
            openRadixDropdown(trigger);

            // Step 3: Wait for dropdown (portal rendering)
            By listboxLocator = By.xpath("(//*[@role='listbox'])[last()]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(listboxLocator));

            // Step 4: Locate option (partial match — e.g. "Technical" matches "Technical Issue")
            By optionLocator = By.xpath(
                    "(//*[@role='option' and contains(normalize-space(.)," + xpathLiteral(visibleText) + ")])[last()]");
            WebElement option = wait.until(ExpectedConditions.presenceOfElementLocated(optionLocator));

            // Step 5: Scroll option into view (important for virtual lists)
            jsScrollIntoView(option);

            // Step 6: Click with retry + fallback
            clickWithRetry(option, 3);

            // Step 7: Wait for dropdown to close
            wait.until(ExpectedConditions.invisibilityOfElementLocated(listboxLocator));

        } catch (Exception e) {
            logger.error("Failed to select option: {}", visibleText);
            throw new RuntimeException("Radix dropdown selection failed for: " + visibleText, e);
        }
    }

    private void openRadixDropdown(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];"
                        + "el.dispatchEvent(new PointerEvent('pointerdown', "
                        + "  {bubbles:true, cancelable:true, pointerType:'mouse'}));"
                        + "el.dispatchEvent(new PointerEvent('pointerup', "
                        + "  {bubbles:true, cancelable:true, pointerType:'mouse'}));",
                element);
    }

    private void clickWithRetry(WebElement element, int maxAttempts) {
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                element.click();
                return;
            } catch (Exception e) {
                attempts++;
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                if (attempts == maxAttempts) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                }
            }
        }
    }

    /** Clicks the "Submit Ticket" button inside the open dialog. */
    private void clickSubmitTicket() {
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(BTN_SUBMIT_TICKET));
        jsScrollIntoView(btn);
        // Native WebDriver click fires a trusted event → triggers form submission + React handler
        try {
            btn.click();
        } catch (Exception e) {
            // Fallback: HTMLElement.click() also fires a trusted click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
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
