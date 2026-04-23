package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.WebDriverWaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the Message / Chat module ({@code /chat}).
 *
 * <p>Source reference: {@code src/pages/Chat.tsx}
 *
 * <p>Key UI facts (Chat.tsx):
 * <ul>
 *   <li>Page heading: {@code <h1>Messages</h1>}</li>
 *   <li>Search box: {@code <Input placeholder="Search messages..." />}</li>
 *   <li>Filter tabs (Radix Tabs): "All (n)", "Connections (n)", "Groups (n)"</li>
 *   <li>Chat list items are {@code <button>} elements containing an {@code <h3>}
 *       with the chat name and a {@code <p>} with the last message preview</li>
 *   <li>Selected chat header: {@code <h2>} with chat name, {@code <p>} with
 *       "Active now" (1:1) or "Group chat"</li>
 *   <li>Message input: {@code <Input placeholder="Message {name}..." />}
 *       — placeholder is dynamic, always starts with "Message "</li>
 *   <li>Send button: icon-only sibling of message input, disabled when input is empty</li>
 *   <li>Message bubbles render inside {@code <Card>} elements; own-messages have
 *       {@code flex-row-reverse} on the outer wrapper and {@code bg-primary}
 *       on the card</li>
 *   <li>Messages are stored in local React state only — there is no backend
 *       persistence, so receiver-side verification requires a second browser
 *       session (see {@code sendMessage} javadoc)</li>
 * </ul>
 */
public class MessagePage extends BasePage {

    // ── Page shell ────────────────────────────────────────────────────────────
    // <h1>Messages</h1>
    private static final By PAGE_HEADING = By.xpath(
            "//h1[normalize-space(.)='Messages']");

    // ── Search ────────────────────────────────────────────────────────────────
    // <Input placeholder="Search messages..." />
    private static final By INPUT_SEARCH = By.xpath(
            "//input[@placeholder='Search messages...']");

    // ── Filter tabs (Radix Tabs render as button[role='tab']) ────────────────
    // Match the leading word only — the label also shows a count e.g. "All (5)".
    private static final By TAB_ALL = By.xpath(
            "//button[@role='tab' and starts-with(normalize-space(.),'All')]");
    private static final By TAB_CONNECTIONS = By.xpath(
            "//button[@role='tab' and starts-with(normalize-space(.),'Connections')]");
    private static final By TAB_GROUPS = By.xpath(
            "//button[@role='tab' and starts-with(normalize-space(.),'Groups')]");

    // ── Chat list ─────────────────────────────────────────────────────────────
    // Each chat is: <button> ... <h3>name</h3> ... </button>
    // Empty-state marker in the list panel
    private static final By LIST_EMPTY_STATE = By.xpath(
            "//p[normalize-space(.)='No chats found']");

    // ── Chat window / header ──────────────────────────────────────────────────
    // <h2>{selectedChat.name}</h2> inside the chat header
    private static final By CHAT_HEADER_NAME = By.xpath(
            "//h2[contains(@class,'font-semibold')]");

    // ── Message composer ──────────────────────────────────────────────────────
    // Placeholder is dynamic: "Message John..." / "Message Batch of 2020..."
    private static final By INPUT_MESSAGE = By.xpath(
            "//input[starts-with(@placeholder,'Message ')]");
    // Send button is the sibling button next to the message input (icon-only).
    // It sits in the same flex row and has h-11 w-11 flex-shrink-0.
    private static final By BTN_SEND = By.xpath(
            "//input[starts-with(@placeholder,'Message ')]"
                    + "/following-sibling::button[1]");

    // ── Message thread ────────────────────────────────────────────────────────
    // Messages container: <div class="flex-1 p-4 overflow-y-auto space-y-3">
    // Each message wrapper: <div class="flex gap-3 [flex-row-reverse]">
    // Bubble: <Card class="p-3 max-w-md [bg-primary text-primary-foreground]"><p/></Card>
    private static final By ALL_MESSAGE_BUBBLES = By.xpath(
            "//div[contains(@class,'overflow-y-auto')]"
                    + "//div[contains(@class,'flex') and contains(@class,'gap-3')]"
                    + "//p[contains(@class,'text-sm')]");

    // Own messages: wrapper has flex-row-reverse
    private static final By OWN_MESSAGE_BUBBLES = By.xpath(
            "//div[contains(@class,'overflow-y-auto')]"
                    + "//div[contains(@class,'flex-row-reverse')]"
                    + "//p[contains(@class,'text-sm')]");

    // ═════════════════════════════════════════════════════════════════════════
    // URL helpers
    // ═════════════════════════════════════════════════════════════════════════

    private static String buildMessageUrl() {
        String custom = ConfigReader.getProperty("alumni.message.url");
        if (custom != null && !custom.isBlank()) {
            return custom;
        }
        String base = ConfigReader.getBaseUrl();
        return (base.endsWith("/") ? base : base + "/") + "chat";
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation
    // ═════════════════════════════════════════════════════════════════════════

    /** Navigates to the message module and waits for the page shell. */
    public void openMessageModule() {
        navigateTo(buildMessageUrl());
        waitForPageShell();
    }

    public void waitForPageShell() {
        new WebDriverWait(driver, Duration.ofSeconds(Math.max(20, ConfigReader.getExplicitWait())))
                .until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING));
    }

    public boolean isOnMessagePage() {
        return driver.findElements(PAGE_HEADING).stream()
                .anyMatch(el -> { try { return el.isDisplayed(); } catch (Exception e) { return false; } });
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Chat list filter + search
    // ═════════════════════════════════════════════════════════════════════════

    /** Switches the chat list to one of: {@code all}, {@code connections}, {@code groups}. */
    public void filterChatList(String filter) {
        By tab;
        switch (filter == null ? "" : filter.trim().toLowerCase()) {
            case "connections":
            case "personal":
                tab = TAB_CONNECTIONS;
                break;
            case "groups":
            case "group":
                tab = TAB_GROUPS;
                break;
            default:
                tab = TAB_ALL;
        }
        WebElement el = WebDriverWaitUtil.waitForElementClickable(tab);
        jsScrollIntoView(el);
        clickWithRetry(el, 3);
    }

    /** Types into the chat-list search box and waits for the list to settle. */
    public void searchUser(String query) {
        WebElement box = WebDriverWaitUtil.waitForElementVisible(INPUT_SEARCH);
        box.clear();
        if (query != null && !query.isEmpty()) {
            box.sendKeys(query);
        }
        WebDriverWaitUtil.staticWait(1);
    }

    /** Returns {@code true} if the given name appears in the (currently filtered) chat list. */
    public boolean isUserVisibleInChatList(String name) {
        By locator = chatListItemByName(name);
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Returns {@code true} if the "No chats found" empty state is visible. */
    public boolean isChatListEmpty() {
        return driver.findElements(LIST_EMPTY_STATE).stream()
                .anyMatch(el -> { try { return el.isDisplayed(); } catch (Exception e) { return false; } });
    }

    /**
     * Returns the name of the first chat currently rendered in the list.
     * Waits for the list to populate; returns empty string if the list is
     * still empty after the explicit-wait timeout.
     */
    public String getFirstChatName() {
        By firstH3 = By.xpath(
                "(//div[contains(@class,'overflow-y-auto')]//button//h3)[1]");
        try {
            WebElement h3 = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.visibilityOfElementLocated(firstH3));
            return h3.getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Select a chat / open a thread
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Selects a chat (user or group) from the chat list by visible name.
     * Waits for the chat header to reflect the selection.
     */
    public void selectUser(String name) {
        By locator = chatListItemByName(name);
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(locator));
        jsScrollIntoView(btn);
        clickWithRetry(btn, 3);

        // Wait for the chat header to update to the selected name
        By header = By.xpath("//h2[contains(@class,'font-semibold')"
                + " and contains(normalize-space(.)," + xpathLiteral(name) + ")]");
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.visibilityOfElementLocated(header));
    }

    /** Returns the name displayed in the chat header of the currently open thread. */
    public String getOpenChatHeaderName() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(CHAT_HEADER_NAME))
                    .getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Send / read messages
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Sends a message in the currently open thread.
     *
     * <p>Implementation notes:
     * <ul>
     *   <li>Chat.tsx disables the Send button while the input is empty
     *       ({@code disabled={!message.trim()}}) — we wait for it to become
     *       clickable after typing.</li>
     *   <li>Chat.tsx also submits on Enter (without Shift). If the Send button
     *       click fails for any reason, we fall back to pressing Enter.</li>
     *   <li>Messages are kept in local React state (no backend persistence),
     *       so a second browser session will NOT see this message. To verify
     *       the receiver view, run the full flow in a second WebDriver session
     *       and open the conversation from the receiver's account.</li>
     * </ul>
     */
    public void sendMessage(String text) {
        WebElement input = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(INPUT_MESSAGE));
        input.clear();
        input.sendKeys(text);

        try {
            WebElement send = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(BTN_SEND));
            jsScrollIntoView(send);
            clickWithRetry(send, 2);
        } catch (Exception e) {
            logger.debug("Send button click failed ({}) — falling back to Enter", e.getMessage());
            input.sendKeys(Keys.ENTER);
        }

        // Wait for the new message bubble to appear as an "own" bubble
        By ownBubbleWithText = By.xpath(
                "//div[contains(@class,'overflow-y-auto')]"
                        + "//div[contains(@class,'flex-row-reverse')]"
                        + "//p[contains(normalize-space(.)," + xpathLiteral(text) + ")]");
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.visibilityOfElementLocated(ownBubbleWithText));

        // Confirm the input was cleared (React resets it on send)
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> {
                    try {
                        return input.getAttribute("value").isEmpty();
                    } catch (Exception ex) {
                        return true;
                    }
                });
    }

    /**
     * Returns the text of the last message bubble rendered in the open thread.
     * Returns empty string if no messages are visible.
     */
    public String getLastMessage() {
        try {
            List<WebElement> bubbles = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(ALL_MESSAGE_BUBBLES));
            return bubbles.isEmpty() ? "" : bubbles.get(bubbles.size() - 1).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    /** Returns the text of the last OWN message bubble (sender side). */
    public String getLastOwnMessage() {
        try {
            List<WebElement> bubbles = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(OWN_MESSAGE_BUBBLES));
            return bubbles.isEmpty() ? "" : bubbles.get(bubbles.size() - 1).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    /**
     * Reads and returns the first message bubble containing the given text.
     * Polls up to the configured explicit-wait timeout to give the thread time
     * to render after a send.
     */
    public String readMessage(String expectedText) {
        By locator = By.xpath(
                "//div[contains(@class,'overflow-y-auto')]"
                        + "//p[contains(normalize-space(.)," + xpathLiteral(expectedText) + ")]");
        try {
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return el.getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    /** Returns {@code true} if a message bubble containing the given text is visible. */
    public boolean isMessageVisible(String text) {
        return !readMessage(text).isEmpty();
    }

    /** Returns the total number of message bubbles currently visible in the open thread. */
    public int getMessageCount() {
        return driver.findElements(ALL_MESSAGE_BUBBLES).size();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Builds a locator for the chat-list button containing the given name.
     * Anchors on the {@code <h3>} element Chat.tsx uses for the chat name,
     * then walks back up to the enclosing {@code <button>}.
     */
    private By chatListItemByName(String name) {
        return By.xpath(
                "//button[.//h3[contains(normalize-space(.)," + xpathLiteral(name) + ")]]");
    }

    private void jsScrollIntoView(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", el);
        } catch (Exception ignored) {
            // Best-effort — scrollIntoView is a hint, not a hard requirement.
        }
    }

    private void clickWithRetry(WebElement element, int maxAttempts) {
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                element.click();
                return;
            } catch (Exception e) {
                attempts++;
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                if (attempts == maxAttempts) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                }
            }
        }
    }

    /** Safely embeds arbitrary text (including quotes) into an XPath literal. */
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
