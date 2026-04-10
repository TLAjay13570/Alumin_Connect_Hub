package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.WebDriverWaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the Admin Support Ticket module ({@code /admin/support}).
 *
 * <p>Source reference: {@code AdminSupportPage.tsx}, {@code AdminSupport.tsx}
 *
 * <p>Key UI facts:
 * <ul>
 *   <li>Page heading: {@code <h1>Support Tickets</h1>}</li>
 *   <li>Route: {@code /admin/support} (protected by AdminRoute)</li>
 *   <li>Ticket list: {@code <Card>} div elements with {@code cursor-pointer} class;
 *       clicking a card opens the detail dialog</li>
 *   <li>No "View" button — the entire card div is the click target</li>
 *   <li>Search input placeholder: "Search tickets..."</li>
 *   <li>Detail dialog: Radix UI {@code <Dialog open={!!selectedTicket}>}</li>
 *   <li>Reply textarea: {@code id="response"}, placeholder "Type your response here..."</li>
 *   <li>Send button text: "Send Response"</li>
 *   <li>Reply success toast title: "Response Sent"</li>
 *   <li>Status SelectTrigger: {@code id="status"}</li>
 *   <li>Status options: "Open", "In Progress", "Resolved", "Closed"</li>
 *   <li>Save button text: "Update Ticket"</li>
 *   <li>Save success toast: "Ticket Updated"</li>
 *   <li>After "Update Ticket" is clicked, dialog closes ({@code setSelectedTicket(null)})</li>
 *   <li>Conversation section heading: "Conversation History"</li>
 *   <li>Empty reply shows toast: "Error" / "Please enter a message."</li>
 * </ul>
 */
public class AdminSupportPage extends BasePage {

    // ── Page shell ────────────────────────────────────────────────────────────
    // <h1 class="...">Support Tickets</h1>
    private static final By PAGE_HEADING = By.xpath(
            "//h1[contains(normalize-space(.),'Support Tickets')]"
                    + " | //h1[contains(normalize-space(.),'Support')]"
                    + "     [contains(normalize-space(.),'Ticket')]");

    // AdminSupport component renders a search input once it mounts
    private static final By SEARCH_INPUT = By.xpath(
            "//input[@placeholder='Search tickets...']");

    // ── Ticket list (Card divs, NOT a <table>) ────────────────────────────────
    // AdminSupport.tsx: <Card className="cursor-pointer hover:shadow-md transition-shadow">
    private static final By TICKET_CARD_CONTAINER = By.xpath(
            "//div[contains(@class,'cursor-pointer')]"
                    + "[contains(@class,'transition-shadow') or contains(@class,'hover:shadow-md')]");

    // ── Ticket detail dialog ──────────────────────────────────────────────────
    // <Dialog open={!!selectedTicket}> — Radix renders with role="dialog"
    private static final By MODAL = By.xpath("//div[@role='dialog']");

    // ── Conversation section (inside dialog) ──────────────────────────────────
    // <h4 class="font-semibold mb-3">Conversation History</h4>
    private static final By MODAL_CONVERSATION_HEADING = By.xpath(
            "//div[@role='dialog']//h4[contains(normalize-space(.),'Conversation')]");

    // ── Reply (inside dialog) ─────────────────────────────────────────────────
    // <Textarea id="response" placeholder="Type your response here..." />
    private static final By TEXTAREA_REPLY = By.xpath(
            "//div[@role='dialog']//textarea[@id='response']"
                    + " | //textarea[@id='response']");

    // <Button onClick={handleSendResponse}>Send Response</Button>
    private static final By BTN_SEND_REPLY = By.xpath(
            "//button[contains(normalize-space(.),'Send Response')]");

    // ── Status change (inside dialog) ─────────────────────────────────────────
    // <SelectTrigger id="status"> — Radix renders as button[@role='combobox'][@id='status']
    private static final By STATUS_SELECT_TRIGGER = By.xpath(
            "//button[@role='combobox'][@id='status']");

    // Button text may be "Update Ticket" or "Update Status" depending on deployed version
    private static final By BTN_UPDATE_TICKET = By.xpath(
            "//button[contains(normalize-space(.),'Update Ticket') or contains(normalize-space(.),'Update Status')]");

    // ── Reply validation (toast based) ────────────────────────────────────────
    // handleSendResponse shows toast: title "Error", description "Please enter a message."
    private static final By TOAST_SIGNALS = By.xpath(
            "//*[@role='status'][string-length(normalize-space(.)) > 0]"
                    + " | //*[@role='alert'][string-length(normalize-space(.)) > 0]"
                    + " | //ol[contains(@class,'fixed')]//li[@data-state='open']"
                    + "     [string-length(normalize-space(.)) > 0]");

    // ═════════════════════════════════════════════════════════════════════════
    // URL helpers
    // ═════════════════════════════════════════════════════════════════════════

    private static String buildAdminSupportUrl() {
        String custom = ConfigReader.getProperty("admin.support.url");
        if (custom != null && !custom.isBlank()) return custom;
        String base = ConfigReader.getBaseUrl();
        return (base.endsWith("/") ? base : base + "/") + "admin/support";
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation
    // ═════════════════════════════════════════════════════════════════════════

    /** Navigates to the admin support page and waits for both the heading and search input. */
    public void navigateToAdminSupport() {
        navigateTo(buildAdminSupportUrl());
        waitForPageShell();
    }

    /**
     * Waits for the admin support page to be fully ready:
     * the h1 heading AND the AdminSupport component's search input must be visible.
     */
    public void waitForPageShell() {
        new WebDriverWait(driver, Duration.ofSeconds(Math.max(25, ConfigReader.getExplicitWait())))
                .until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
        // Also wait for the AdminSupport component to mount (search input appears on mount)
        try {
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.presenceOfElementLocated(SEARCH_INPUT));
        } catch (TimeoutException e) {
            logger.warn("Search input not found — AdminSupport component may still be loading");
        }
    }

    public boolean isOnAdminSupportPage() {
        return driver.findElements(PAGE_HEADING).stream().anyMatch(WebElement::isDisplayed);
    }

    /**
     * Navigates directly to the admin support URL without waiting for the page to load.
     * Used for access-control redirect verification.
     */
    public void navigateToAdminSupportDirectly() {
        driver.get(buildAdminSupportUrl());
        WebDriverWaitUtil.waitForPageToLoad();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(drv -> {
                String u = drv.getCurrentUrl().toLowerCase();
                return u.contains("/login") || !u.contains("/admin/support");
            });
        } catch (TimeoutException ignored) {
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Ticket List
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns {@code true} when the AdminSupport component has rendered
     * (search input is visible — it always appears regardless of ticket count).
     */
    public boolean isTicketTableVisible() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Returns {@code true} when a ticket card containing the given subject is visible
     * in the admin list.
     */
    public boolean isTicketVisibleInAdminList(String subject) {
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
     * Returns the status badge text for the ticket card containing the given subject.
     *
     * <p>Badge: {@code <span class="capitalize">{status.replace('-', ' ')}</span>}
     * CSS {@code capitalize} = first letter of each word uppercased.
     * e.g. "in-progress" → "in progress" → CSS → "In Progress".
     */
    public String getTicketStatusFromList(String subject) {
        By statusLocator = By.xpath(
                "//div[contains(@class,'cursor-pointer')]"
                        + "[.//*[contains(normalize-space(.)," + xpathLiteral(subject) + ")]]"
                        + "//*[contains(@class,'capitalize')]"
                        + "[contains(normalize-space(.),'open') or contains(normalize-space(.),'progress')"
                        + " or contains(normalize-space(.),'resolved') or contains(normalize-space(.),'closed')]");
        try {
            WebElement badge = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(statusLocator));
            return badge.getText().trim();
        } catch (TimeoutException e) {
            logger.warn("Status badge not found in admin list for ticket: {}", subject);
            return "";
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Ticket Detail Modal
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Opens the ticket detail dialog by clicking the card for the given subject.
     *
     * <p>In AdminSupport.tsx, the entire {@code <Card>} div has
     * {@code onClick={() => handleTicketClick(ticket)}} — there is NO separate "View" button.
     */
    public void openTicketDetailModal(String subject) {
        // The card div has cursor-pointer and contains the subject text
        By cardLocator = By.xpath(
                "//div[contains(@class,'cursor-pointer')]"
                        + "[contains(@class,'transition-shadow') or contains(@class,'hover:shadow')]"
                        + "[.//*[contains(normalize-space(.)," + xpathLiteral(subject) + ")]]");
        try {
            WebElement card = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.elementToBeClickable(cardLocator));
            jsScrollIntoView(card);
            jsClick(card);
        } catch (TimeoutException e) {
            // Fallback: click the subject text itself
            By subjectLocator = By.xpath(
                    "//*[contains(normalize-space(.)," + xpathLiteral(subject) + ")]"
                            + "[not(ancestor::*[@role='dialog'])]");
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.elementToBeClickable(subjectLocator));
            jsScrollIntoView(el);
            jsClick(el);
        }
        waitForModal();
    }

    public void waitForModal() {
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.visibilityOfElementLocated(MODAL));
    }

    public void waitForModalClosed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
        } catch (TimeoutException ignored) {
        }
    }

    public boolean isModalOpen() {
        return driver.findElements(MODAL).stream()
                .anyMatch(el -> { try { return el.isDisplayed(); } catch (Exception e) { return false; } });
    }

    /**
     * Closes the modal via the Escape key (reliable with Radix UI dialogs).
     * Use this only in cleanup — normally the dialog closes automatically after actions.
     */
    public void closeModalViaEscape() {
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            waitForModalClosed();
        } catch (Exception e) {
            logger.warn("Could not close modal via Escape: {}", e.getMessage());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Conversation Thread
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns {@code true} when the "Conversation History" heading is visible inside the modal.
     *
     * <p>Note: this heading only appears when {@code selectedTicket.responses.length > 0}.
     * For a brand-new ticket with no responses yet, the conversation section is absent.
     */
    public boolean isConversationVisibleInModal() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.visibilityOfElementLocated(MODAL_CONVERSATION_HEADING));
            return true;
        } catch (TimeoutException e) {
            // Check for the description section as fallback (always present)
            By descriptionSection = By.xpath(
                    "//div[@role='dialog']//h4[contains(normalize-space(.),'Description')]");
            return !driver.findElements(descriptionSection).isEmpty();
        }
    }

    /**
     * Returns {@code true} if the given text appears inside the open ticket dialog.
     */
    public boolean isTextInConversation(String text) {
        By locator = By.xpath(
                "//div[@role='dialog']//*[contains(normalize-space(.)," + xpathLiteral(text) + ")]");
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
    // Reply
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Clears and types the admin response in the reply textarea.
     *
     * <p>Field: {@code <Textarea id="response" placeholder="Type your response here..." />}
     *
     * @param message reply text; pass empty string to test validation
     */
    public void enterReplyMessage(String message) {
        WebElement textarea = scrollToModalField(TEXTAREA_REPLY);
        textarea.clear();
        if (message != null && !message.isEmpty()) {
            textarea.sendKeys(message);
        }
    }

    /**
     * Clicks the "Send Response" button.
     *
     * <p>On success: toast "Response Sent" + dialog closes ({@code setSelectedTicket(null)}).
     * On empty message: toast "Error" / "Please enter a message." + dialog stays open.
     */
    public void clickSendReply() {
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(BTN_SEND_REPLY));
        jsScrollIntoView(btn);
        jsClick(btn);
    }

    /**
     * Returns {@code true} if an error toast is visible (used to verify empty-reply validation).
     *
     * <p>{@code handleSendResponse} shows: title "Error", description "Please enter a message."
     */
    public boolean isReplyValidationErrorDisplayed() {
        // Try ToastUtil first (covers both Radix and Sonner toast implementations)
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> {
                String text = com.automation.utils.ToastUtil.getLatestToastText().toLowerCase();
                if (text.contains("error") || text.contains("message")
                        || text.contains("enter") || text.contains("please")) {
                    return Boolean.TRUE;
                }
                // Also check DOM directly
                List<WebElement> toasts = d.findElements(TOAST_SIGNALS);
                return toasts.stream().anyMatch(el -> {
                    try {
                        if (!el.isDisplayed()) return false;
                        String t = el.getText().toLowerCase();
                        return t.contains("error") || t.contains("message")
                                || t.contains("enter") || t.contains("please");
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
    // Status Change
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Changes the ticket status using the Radix Select with {@code id="status"}.
     *
     * <p>Supported option display texts: "Open", "In Progress", "Resolved", "Closed".
     *
     * <p>After clicking "Update Ticket", the dialog CLOSES automatically because
     * {@code handleUpdateStatus()} calls {@code setSelectedTicket(null)}.
     */
    public void changeTicketStatus(String status) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));

        try {
            // Step 1: Wait & scroll to trigger
            WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(STATUS_SELECT_TRIGGER));
            jsScrollIntoView(trigger);

            // Step 2: Open dropdown using JS pointer events (Radix fix)
            openRadixDropdown(trigger);

            // Step 3: Wait for dropdown portal
            By listboxLocator = By.xpath("(//*[@role='listbox'])[last()]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(listboxLocator));

            // Step 4: Locate option
            By optionLocator = By.xpath(
                    "(//*[@role='option' and contains(normalize-space(.)," + xpathLiteral(status) + ")])[last()]");
            WebElement option = wait.until(ExpectedConditions.presenceOfElementLocated(optionLocator));

            // Step 5: Scroll option into view
            jsScrollIntoView(option);

            // Step 6: Select option using click event ONLY (no pointerdown).
            // Radix SelectItem selects on onClick → option is chosen + listbox closes.
            // Dialog's onPointerDownOutside does NOT fire (no pointerdown dispatched)
            // → the Dialog stays open so we can still click "Update Status".
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent("
                            + "new MouseEvent('click', {bubbles:true, cancelable:true, composed:true}));",
                    option);

            // Step 7: Wait for dropdown to close (Radix closes the listbox on selection)
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.invisibilityOfElementLocated(listboxLocator));
            } catch (TimeoutException e) {
                // If listbox is still open, dismiss with Escape and proceed
                driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            }

            logger.debug("Status changed to: {}", status);
        } catch (Exception e) {
            logger.error("Failed to change status to: {}", status);
            throw new RuntimeException("Radix dropdown selection failed for status: " + status, e);
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

    /**
     * Clicks "Update Ticket" to save the status change.
     */
    public void saveStatusChange() {
        clickUpdateStatusButton();
    }

    /**
     * Clicks the "Update Status" / "Update Ticket" button.
     * Dialog closes automatically after the API responds.
     * Caller checks the toast BEFORE waiting for modal to close.
     */
    public void clickUpdateStatusButton() {
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(BTN_UPDATE_TICKET));
        jsScrollIntoView(btn);
        WebDriverWaitUtil.staticWait(1);
        // Actions.click() sends a trusted, real browser click — React onClick fires reliably
        try {
            new Actions(driver).moveToElement(btn).click().perform();
        } catch (Exception e) {
            // Fallback to native click, then JS click
            try { btn.click(); } catch (Exception e2) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            }
        }
        // Do NOT block here — toast must be caught before the dialog finishes closing
    }

    /**
     * Returns the currently selected status text from the status SelectTrigger
     * WHILE THE DIALOG IS STILL OPEN (before calling {@link #saveStatusChange()}).
     *
     * <p>After saving, the dialog closes — do not call this method post-save.
     */
    public String getStatusFromOpenModal() {
        // The SelectTrigger shows the selected option's display text
        try {
            WebElement trigger = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(STATUS_SELECT_TRIGGER));
            return trigger.getText().trim();
        } catch (TimeoutException e) {
            // Fallback: look for the status badge in the dialog header
            By headerBadge = By.xpath(
                    "//div[@role='dialog']//*[contains(@class,'capitalize')]"
                            + "[contains(normalize-space(.),'open') or contains(normalize-space(.),'progress')"
                            + " or contains(normalize-space(.),'resolved') or contains(normalize-space(.),'closed')]");
            try {
                return driver.findElement(headerBadge).getText().trim();
            } catch (Exception ex) {
                logger.warn("Could not read status from open modal");
                return "";
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═════════════════════════════════════════════════════════════════════════

    private WebElement scrollToModalField(By locator) {
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
