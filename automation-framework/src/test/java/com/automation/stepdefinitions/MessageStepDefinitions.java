package com.automation.stepdefinitions;

import com.automation.pages.MessagePage;
import com.automation.utils.ExtentReportUtil;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.UUID;

/**
 * Step definitions for the Message / Chat module ({@code /chat}).
 *
 * <p>Covers the happy-path lifecycle:
 * <ol>
 *   <li>Open the Message module</li>
 *   <li>Filter / search the chat list</li>
 *   <li>Select a user or group chat</li>
 *   <li>Send a message</li>
 *   <li>Verify the message is rendered in the sender's thread</li>
 * </ol>
 *
 * <p>Reuses the login steps from {@link LoginStepDefinitions} via Cucumber's
 * shared glue — no duplicate login code here.
 *
 * <p>Note: Chat.tsx stores messages in local React state only, so the receiver
 * side of the conversation cannot be verified from a second browser session
 * against the current build. These scenarios assert sender-side rendering.
 */
public class MessageStepDefinitions {

    private static final Logger logger = LogManager.getLogger(MessageStepDefinitions.class);

    /**
     * Thread-safe storage for the chat name and last sent message in the current scenario.
     * Lets later steps refer to "the remembered chat" / "the selected chat" without
     * hardcoding values in the feature file.
     */
    private static final ThreadLocal<String> REMEMBERED_CHAT = new ThreadLocal<>();
    private static final ThreadLocal<String> LAST_SENT_MESSAGE = new ThreadLocal<>();

    private MessagePage messagePage;

    private MessagePage mp() {
        if (messagePage == null) {
            messagePage = new MessagePage();
        }
        return messagePage;
    }

    @After
    public void clearMessageThreadLocals() {
        REMEMBERED_CHAT.remove();
        LAST_SENT_MESSAGE.remove();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation
    // ═════════════════════════════════════════════════════════════════════════

    @When("I open the Message module")
    public void openMessageModule() {
        logger.info("Opening Message module");
        ExtentReportUtil.logInfo("Open Message module");
        mp().openMessageModule();
    }

    @Then("I should see the Message page")
    public void assertOnMessagePage() {
        Assert.assertTrue(mp().isOnMessagePage(),
                "Message page did not load — 'Messages' heading not visible");
        ExtentReportUtil.logPass("Message page is displayed");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Chat list filter + search
    // ═════════════════════════════════════════════════════════════════════════

    @When("I switch the chat list filter to {string}")
    public void switchChatFilter(String filter) {
        logger.info("Switching chat list filter to: {}", filter);
        ExtentReportUtil.logInfo("Chat filter: " + filter);
        mp().filterChatList(filter);
    }

    @When("I search the chat list for {string}")
    public void searchChatList(String query) {
        logger.info("Searching chat list for: {}", query);
        ExtentReportUtil.logInfo("Search chat list: " + query);
        mp().searchUser(query);
    }

    @When("I search the chat list for the remembered name")
    public void searchRememberedName() {
        String name = rememberedChatOrFail();
        logger.info("Searching chat list for remembered name: {}", name);
        ExtentReportUtil.logInfo("Search chat list: " + name);
        mp().searchUser(name);
    }

    @Then("the chat {string} should be visible in the list")
    public void assertChatVisibleInList(String name) {
        Assert.assertTrue(mp().isUserVisibleInChatList(name),
                "Chat '" + name + "' is not visible in the chat list");
        ExtentReportUtil.logPass("Chat visible in list: " + name);
    }

    @Then("the remembered chat should be visible in the list")
    public void assertRememberedChatVisible() {
        String name = rememberedChatOrFail();
        Assert.assertTrue(mp().isUserVisibleInChatList(name),
                "Remembered chat '" + name + "' is not visible in the list");
        ExtentReportUtil.logPass("Remembered chat visible: " + name);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Select a chat / remember a chat name
    // ═════════════════════════════════════════════════════════════════════════

    @When("I remember the first chat name from the list")
    public void rememberFirstChatName() {
        String name = mp().getFirstChatName();
        Assert.assertFalse(name.isEmpty(),
                "Chat list is empty — cannot remember a chat name");
        REMEMBERED_CHAT.set(name);
        logger.info("Remembered chat name: {}", name);
        ExtentReportUtil.logInfo("Remembered chat: " + name);
    }

    @When("I select the first available chat from the list")
    public void selectFirstChat() {
        String name = mp().getFirstChatName();
        Assert.assertFalse(name.isEmpty(),
                "Chat list is empty — no chat available to select. "
                        + "Ensure the test user has at least one connection or joined group.");
        REMEMBERED_CHAT.set(name);
        logger.info("Selecting first chat: {}", name);
        ExtentReportUtil.logInfo("Select chat: " + name);
        mp().selectUser(name);
    }

    @When("I select the chat {string} from the list")
    public void selectChatByName(String name) {
        REMEMBERED_CHAT.set(name);
        logger.info("Selecting chat: {}", name);
        ExtentReportUtil.logInfo("Select chat: " + name);
        mp().selectUser(name);
    }

    @When("I select the remembered chat from the list")
    public void selectRememberedChat() {
        String name = rememberedChatOrFail();
        logger.info("Selecting remembered chat: {}", name);
        ExtentReportUtil.logInfo("Select remembered chat: " + name);
        mp().selectUser(name);
    }

    @Then("the chat thread should be open for {string}")
    public void assertChatOpenForName(String name) {
        String actual = mp().getOpenChatHeaderName();
        Assert.assertTrue(actual.contains(name),
                "Expected chat header to contain '" + name + "' but got: '" + actual + "'");
        ExtentReportUtil.logPass("Chat thread open for: " + actual);
    }

    @Then("the chat thread should be open for the selected chat")
    public void assertChatOpenForSelected() {
        String expected = rememberedChatOrFail();
        String actual = mp().getOpenChatHeaderName();
        Assert.assertTrue(actual.contains(expected),
                "Expected chat header to contain '" + expected + "' but got: '" + actual + "'");
        ExtentReportUtil.logPass("Chat thread open for selected chat: " + actual);
    }

    @Then("the chat thread should be open for the remembered chat")
    public void assertChatOpenForRemembered() {
        assertChatOpenForSelected();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Send / read messages
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Sends a message. If the text contains the token {@code UNIQUE} it is
     * replaced with a short UUID suffix, so repeated runs produce distinct
     * content that can be reliably located in the thread.
     */
    @And("I send the message {string}")
    public void sendMessage(String text) {
        String resolved = text.contains("UNIQUE")
                ? text.replace("UNIQUE", UUID.randomUUID().toString().substring(0, 8))
                : text;
        LAST_SENT_MESSAGE.set(resolved);
        logger.info("Sending message: {}", resolved);
        ExtentReportUtil.logInfo("Send message: " + resolved);
        mp().sendMessage(resolved);
    }

    @Then("the message {string} should appear in the conversation")
    public void assertMessageVisible(String text) {
        // Resolve UNIQUE against the most recent sent message so the feature file
        // can reference the same token used in "I send the message".
        String expected = resolveAgainstLastSent(text);
        Assert.assertTrue(mp().isMessageVisible(expected),
                "Message not visible in conversation: '" + expected + "'");
        ExtentReportUtil.logPass("Message visible in conversation: " + expected);
    }

    @Then("the last message in the thread should contain {string}")
    public void assertLastMessageContains(String expected) {
        // Resolve UNIQUE against the most recent sent message so the feature file
        // can reference the same token used in "I send the message".
        String resolved = resolveAgainstLastSent(expected);
        String actual = mp().getLastMessage();
        Assert.assertTrue(actual.toLowerCase().contains(resolved.toLowerCase()),
                "Expected last message to contain '" + resolved + "' but got: '" + actual + "'");
        ExtentReportUtil.logPass("Last message confirmed: " + actual);
    }

    @Then("the conversation should have at least {int} messages")
    public void assertMinimumMessageCount(int minCount) {
        int actual = mp().getMessageCount();
        Assert.assertTrue(actual >= minCount,
                "Expected at least " + minCount + " messages but found " + actual);
        ExtentReportUtil.logPass("Message count: " + actual);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═════════════════════════════════════════════════════════════════════════

    private static String rememberedChatOrFail() {
        String name = REMEMBERED_CHAT.get();
        Assert.assertNotNull(name, "No chat was remembered — ensure a 'remember'/'select' step ran first");
        Assert.assertFalse(name.isEmpty(),
                "Remembered chat name is empty — ensure the chat list had at least one entry");
        return name;
    }

    /**
     * If {@code text} contains "UNIQUE" and we have a previously sent message
     * (with a UUID already substituted in), return that sent message instead —
     * so assertion steps match what was actually typed.
     */
    private static String resolveAgainstLastSent(String text) {
        if (text == null || !text.contains("UNIQUE")) {
            return text;
        }
        String last = LAST_SENT_MESSAGE.get();
        return last != null ? last : text;
    }
}
