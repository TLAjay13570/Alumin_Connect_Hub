package com.automation.stepdefinitions;

import com.automation.factory.DriverFactory;
import com.automation.pages.AdminSupportPage;
import com.automation.pages.SupportPage;
import com.automation.utils.ConfigReader;
import com.automation.utils.ExtentReportUtil;
import com.automation.utils.ToastUtil;
import com.automation.utils.WebDriverWaitUtil;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.UUID;

/**
 * Step definitions for the Support Ticket module.
 *
 * <p>Covers:
 * <ul>
 *   <li>Alumni: navigate, create ticket, view list, open conversation</li>
 *   <li>Admin: navigate, open detail modal, reply, change status</li>
 *   <li>Validation: empty form, partial form, edge-case inputs</li>
 *   <li>Access control: alumni and unauthenticated redirect checks</li>
 * </ul>
 *
 * <p>Reuses the login steps from {@link LoginStepDefinitions} via Cucumber's
 * shared step-definition glue — no duplicate login code here.
 */
public class SupportTicketStepDefinitions {

    private static final Logger logger = LogManager.getLogger(SupportTicketStepDefinitions.class);

    /**
     * Thread-safe storage for the ticket subject created in the current scenario.
     * Allows later steps (admin reply, alumni verify) to reference the same subject
     * without hardcoding it in the feature file.
     */
    private static final ThreadLocal<String> LAST_TICKET_SUBJECT = new ThreadLocal<>();

    // ── Lazy-initialised page objects ─────────────────────────────────────────
    private SupportPage supportPage;
    private AdminSupportPage adminSupportPage;

    private SupportPage sp() {
        if (supportPage == null) {
            supportPage = new SupportPage();
        }
        return supportPage;
    }

    private AdminSupportPage asp() {
        if (adminSupportPage == null) {
            adminSupportPage = new AdminSupportPage();
        }
        return adminSupportPage;
    }

    /** Returns the tracked subject, failing the scenario if none has been recorded. */
    public static String lastSubjectOrFail() {
        String s = LAST_TICKET_SUBJECT.get();
        Assert.assertNotNull(s, "No ticket subject was captured in this scenario — "
                + "make sure 'I enter ticket subject ...' ran before this step.");
        return s;
    }

    /** Clean up ThreadLocals after every scenario to avoid bleed between tests. */
    @After
    public void clearSupportThreadLocals() {
        LAST_TICKET_SUBJECT.remove();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation — Alumni
    // ═════════════════════════════════════════════════════════════════════════

    @When("I navigate to the support ticket page")
    public void navigateToSupportPage() {
        logger.info("Navigating to alumni support page");
        ExtentReportUtil.logInfo("Navigate to alumni support page");
        sp().navigateToSupportPage();
    }

    @Then("I should see the support ticket page")
    public void assertSupportPage() {
        Assert.assertTrue(sp().isOnSupportPage(),
                "Alumni support ticket page did not load — heading not visible");
        ExtentReportUtil.logPass("Support ticket page is displayed");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Create Ticket — Alumni
    // ═════════════════════════════════════════════════════════════════════════

    @When("I click the Create Ticket button")
    public void clickCreateTicket() {
        logger.info("Clicking Create Ticket button");
        ExtentReportUtil.logInfo("Click Create Ticket button");
        sp().clickCreateTicket();
    }

    /**
     * Enters the subject field.
     *
     * <p>If the value contains the token {@code UNIQUE}, it is replaced with a short
     * UUID suffix so each test run produces a distinct ticket that can be safely
     * identified in the list.
     */
    @When("I enter ticket subject {string}")
    public void enterTicketSubject(String subject) {
        String resolved = subject.contains("UNIQUE")
                ? subject.replace("UNIQUE", UUID.randomUUID().toString().substring(0, 8))
                : subject;
        LAST_TICKET_SUBJECT.set(resolved);
        logger.info("Entering ticket subject: {}", resolved);
        ExtentReportUtil.logInfo("Ticket subject: " + resolved);
        sp().enterSubject(resolved);
    }

    @And("I select ticket category {string}")
    public void selectTicketCategory(String category) {
        logger.info("Selecting category: {}", category);
        ExtentReportUtil.logInfo("Category: " + category);
        sp().selectCategory(category);
    }

    @And("I select ticket priority {string}")
    public void selectTicketPriority(String priority) {
        logger.info("Selecting priority: {}", priority);
        ExtentReportUtil.logInfo("Priority: " + priority);
        sp().selectPriority(priority);
    }

    @And("I enter ticket description {string}")
    public void enterTicketDescription(String description) {
        logger.info("Entering ticket description ({} chars)", description.length());
        ExtentReportUtil.logInfo("Description entered (" + description.length() + " chars)");
        sp().enterDescription(description);
    }

    /** Submits the form and waits for the dialog to close (positive-path). */
    @And("I submit the ticket form")
    public void submitTicketForm() {
        logger.info("Submitting ticket form");
        ExtentReportUtil.logInfo("Submit ticket form");
        sp().submitTicketForm();
    }

    /** Clicks Submit without waiting for dialog close (validation / negative-path). */
    @And("I attempt to submit the ticket form without filling required fields")
    public void attemptSubmitEmpty() {
        logger.info("Attempting to submit ticket form (validation check)");
        ExtentReportUtil.logInfo("Attempt form submission without required fields");
        sp().attemptSubmitTicketForm();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Toast Assertion (shared alumni + admin)
    // ═════════════════════════════════════════════════════════════════════════

    @Then("I should see a ticket toast containing {string}")
    public void assertTicketToast(String phrase) {
        logger.info("Waiting for toast containing: '{}'", phrase);
        try {
            String msg = ToastUtil.waitForToastContaining(phrase);
            Assert.assertTrue(msg.toLowerCase().contains(phrase.toLowerCase()),
                    "Toast missing phrase '" + phrase + "' — got: " + msg);
            ExtentReportUtil.logPass("Toast confirmed: " + msg);
        } catch (TimeoutException e) {
            Assert.fail("No toast appeared containing: '" + phrase + "'");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Ticket List — Alumni
    // ═════════════════════════════════════════════════════════════════════════

    /** Asserts the tracked ticket (set via {@code I enter ticket subject}) is in the list. */
    @Then("the ticket list should show my created ticket")
    public void assertTrackedTicketInList() {
        String subject = lastSubjectOrFail();
        logger.info("Asserting tracked ticket visible in list: {}", subject);
        WebDriverWait wait = new WebDriverWait(DriverFactory.getCurrentDriver(),
                Duration.ofSeconds(Math.max(20, ConfigReader.getExplicitWait())));
        wait.until(d -> sp().isTicketVisibleInList(subject));
        Assert.assertTrue(sp().isTicketVisibleInList(subject),
                "Ticket not visible in alumni list: " + subject);
        ExtentReportUtil.logPass("Ticket visible in list: " + subject);
    }

    @Then("the ticket list should show ticket {string}")
    public void assertSpecificTicketInList(String subject) {
        logger.info("Asserting ticket in list: {}", subject);
        Assert.assertTrue(sp().isTicketVisibleInList(subject),
                "Ticket not visible in list: " + subject);
        ExtentReportUtil.logPass("Ticket visible: " + subject);
    }

    /** Asserts the status badge on the tracked ticket matches the expected value. */
    @Then("the ticket status should be {string}")
    public void assertTicketStatusInAlumniList(String expectedStatus) {
        String subject = lastSubjectOrFail();
        logger.info("Asserting ticket status '{}' for: {}", expectedStatus, subject);
        // Poll for status update (async UI refresh)
        WebDriverWait wait = new WebDriverWait(DriverFactory.getCurrentDriver(),
                Duration.ofSeconds(Math.max(15, ConfigReader.getExplicitWait())));
        wait.until(d -> sp().getTicketStatus(subject).toLowerCase()
                .contains(expectedStatus.toLowerCase()));
        String actual = sp().getTicketStatus(subject);
        Assert.assertTrue(actual.toLowerCase().contains(expectedStatus.toLowerCase()),
                "Expected status '" + expectedStatus + "' but got: '" + actual + "'");
        ExtentReportUtil.logPass("Status confirmed: " + actual);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Ticket Detail / Conversation — Alumni
    // ═════════════════════════════════════════════════════════════════════════

    @When("I open the ticket with my tracked subject")
    public void openTrackedTicket() {
        String subject = lastSubjectOrFail();
        logger.info("Opening ticket: {}", subject);
        ExtentReportUtil.logInfo("Open ticket: " + subject);
        sp().openTicketBySubject(subject);
    }

    @Then("I should see the conversation thread")
    public void assertConversationThread() {
        Assert.assertTrue(sp().isConversationThreadVisible(),
                "Conversation thread is not visible after opening ticket");
        ExtentReportUtil.logPass("Conversation thread is visible");
    }

    @Then("I should see admin reply {string} in the conversation")
    public void assertAdminReplyVisible(String replyText) {
        logger.info("Asserting admin reply visible in conversation: '{}'", replyText);
        Assert.assertTrue(sp().isReplyVisibleInConversation(replyText),
                "Admin reply not visible in conversation: '" + replyText + "'");
        ExtentReportUtil.logPass("Admin reply visible: " + replyText);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Validation — Alumni
    // ═════════════════════════════════════════════════════════════════════════

    @Then("I should see validation errors on the ticket form")
    public void assertValidationErrors() {
        logger.info("Asserting validation errors — app shows a toast on validation failure");
        // Poll up to 5 seconds for the toast/alert to appear
        WebDriverWait shortWait = new WebDriverWait(DriverFactory.getCurrentDriver(), Duration.ofSeconds(5));
        boolean hasError;
        try {
            hasError = shortWait.until(d -> sp().isValidationErrorDisplayed() ? Boolean.TRUE : null);
        } catch (TimeoutException e) {
            hasError = sp().isValidationErrorDisplayed();
        }
        Assert.assertTrue(hasError,
                "Expected validation error toast on empty/partial form submission — none found");
        ExtentReportUtil.logPass("Validation errors confirmed");
    }

    @Then("the ticket form should remain open")
    public void assertFormRemainsOpen() {
        Assert.assertTrue(sp().isCreateFormOpen(),
                "Ticket form closed unexpectedly after invalid submission");
        ExtentReportUtil.logPass("Ticket form remains open after failed submission");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation — Admin
    // ═════════════════════════════════════════════════════════════════════════

    @When("I navigate to the admin support tickets page")
    public void navigateToAdminSupport() {
        logger.info("Navigating to admin support management page");
        ExtentReportUtil.logInfo("Navigate to admin support page");
        asp().navigateToAdminSupport();
    }

    @Then("I should see the admin support tickets page")
    public void assertAdminSupportPage() {
        Assert.assertTrue(asp().isOnAdminSupportPage(),
                "Admin support ticket page did not load — heading not visible");
        ExtentReportUtil.logPass("Admin support page is displayed");
    }

    @Then("I should see the ticket list table")
    public void assertTicketTableVisible() {
        Assert.assertTrue(asp().isTicketTableVisible(),
                "Ticket table / list is not visible on admin support page");
        ExtentReportUtil.logPass("Ticket table is visible");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Admin — Ticket Detail Modal
    // ═════════════════════════════════════════════════════════════════════════

    @When("I open the ticket detail for {string} from admin")
    public void openTicketDetailAdmin(String subject) {
        logger.info("Admin opening ticket detail for: {}", subject);
        ExtentReportUtil.logInfo("Admin opens ticket detail: " + subject);
        asp().openTicketDetailModal(subject);
    }

    @When("I open the ticket detail for my tracked subject from admin")
    public void openTrackedTicketDetailAdmin() {
        String subject = lastSubjectOrFail();
        logger.info("Admin opening tracked ticket detail: {}", subject);
        ExtentReportUtil.logInfo("Admin opens tracked ticket: " + subject);
        asp().openTicketDetailModal(subject);
    }

    @Then("the ticket detail modal should be open")
    public void assertModalOpen() {
        Assert.assertTrue(asp().isModalOpen(),
                "Ticket detail modal is not open");
        ExtentReportUtil.logPass("Ticket detail modal is open");
    }

    @Then("I should see the conversation in the modal")
    public void assertConversationInModal() {
        Assert.assertTrue(asp().isConversationVisibleInModal(),
                "Conversation thread not visible inside admin ticket modal");
        ExtentReportUtil.logPass("Conversation thread visible in modal");
    }

    @Then("I should see {string} in the ticket conversation")
    public void assertTextInConversation(String text) {
        logger.info("Asserting '{}' in ticket conversation", text);
        Assert.assertTrue(asp().isTextInConversation(text),
                "Text '" + text + "' not found in ticket conversation");
        ExtentReportUtil.logPass("Conversation contains: " + text);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Admin — Reply
    // ═════════════════════════════════════════════════════════════════════════

    @When("I enter admin reply {string}")
    public void enterAdminReply(String message) {
        logger.info("Entering admin reply ({} chars)", message.length());
        ExtentReportUtil.logInfo("Admin reply: " + message);
        asp().enterReplyMessage(message);
    }

    @And("I send the admin reply")
    public void sendAdminReply() {
        logger.info("Clicking Send Reply");
        ExtentReportUtil.logInfo("Send reply clicked");
        asp().clickSendReply();
    }

    @Then("I should see validation errors in the reply section")
    public void assertReplyValidationError() {
        logger.info("Asserting reply validation error is displayed");
        Assert.assertTrue(asp().isReplyValidationErrorDisplayed(),
                "Expected a validation error for empty reply — none found");
        ExtentReportUtil.logPass("Reply validation error confirmed");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Admin — Status Change
    // ═════════════════════════════════════════════════════════════════════════

    @When("I change the ticket status to {string}")
    public void changeTicketStatus(String status) {
        logger.info("Changing ticket status to: {}", status);
        ExtentReportUtil.logInfo("Change status to: " + status);
        asp().changeTicketStatus(status);
    }

    @And("I save the ticket status change")
    public void saveStatusChange() {
        logger.info("Saving ticket status change");
        ExtentReportUtil.logInfo("Save status change");
        asp().saveStatusChange();
    }

    @And("I click the update status button")
    public void clickUpdateStatusButton() {
        logger.info("Clicking Update Status button");
        ExtentReportUtil.logInfo("Click Update Status button");
        asp().clickUpdateStatusButton();
    }

    @Then("the ticket status in admin list should be {string}")
    public void assertAdminListStatus(String expectedStatus) {
        String subject = lastSubjectOrFail();
        logger.info("Asserting admin list status '{}' for: {}", expectedStatus, subject);
        // Poll — status badge update is async after the modal closes
        WebDriverWait wait = new WebDriverWait(DriverFactory.getCurrentDriver(),
                Duration.ofSeconds(Math.max(15, ConfigReader.getExplicitWait())));
        wait.until(d -> asp().getTicketStatusFromList(subject).toLowerCase()
                .contains(expectedStatus.toLowerCase()));
        String actual = asp().getTicketStatusFromList(subject);
        Assert.assertTrue(actual.toLowerCase().contains(expectedStatus.toLowerCase()),
                "Expected admin list status '" + expectedStatus + "' but found: '" + actual + "'");
        ExtentReportUtil.logPass("Admin list status confirmed: " + actual);
    }

    @Then("the modal status should show {string}")
    public void assertModalStatus(String expectedStatus) {
        logger.info("Asserting modal status shows: {}", expectedStatus);
        String actual = asp().getStatusFromOpenModal();
        Assert.assertTrue(actual.toLowerCase().contains(expectedStatus.toLowerCase()),
                "Expected modal status '" + expectedStatus + "' but got: '" + actual + "'");
        ExtentReportUtil.logPass("Modal status confirmed: " + actual);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Access Control
    // ═════════════════════════════════════════════════════════════════════════

    @When("I navigate directly to the admin support URL as alumni")
    public void alumniHitsAdminSupport() {
        logger.info("Alumni attempting direct access to admin support URL");
        ExtentReportUtil.logInfo("Alumni hits admin support URL directly");
        asp().navigateToAdminSupportDirectly();
    }

    @Then("I should not remain on the admin support page")
    public void assertNotOnAdminSupport() {
        WebDriver d = DriverFactory.getCurrentDriver();
        String url = d.getCurrentUrl();
        Assert.assertFalse(url.contains("/admin/support"),
                "Alumni should be redirected off admin support page — still at: " + url);
        ExtentReportUtil.logPass("Access control confirmed — alumni redirected from admin support");
    }

    @When("I open the admin support page without authentication")
    public void guestOpensAdminSupport() {
        logger.info("Unauthenticated user attempting to open admin support page");
        ExtentReportUtil.logInfo("Guest hits admin support URL");
        WebDriver d = DriverFactory.getCurrentDriver();
        String url = ConfigReader.getProperty("admin.support.url");
        if (url == null || url.isBlank()) {
            String base = ConfigReader.getBaseUrl();
            url = (base.endsWith("/") ? base : base + "/") + "admin/support";
        }
        d.get(url);
        WebDriverWaitUtil.waitForPageToLoad();
        try {
            new WebDriverWait(d, Duration.ofSeconds(20)).until(drv -> {
                String current = drv.getCurrentUrl().toLowerCase();
                return current.contains("/login") || !current.contains("/admin/support");
            });
        } catch (TimeoutException ignored) {
            // Caller will assert the URL
        }
    }

    @Then("I should be redirected away from admin support page")
    public void assertGuestRedirectedFromAdminSupport() {
        WebDriver d = DriverFactory.getCurrentDriver();
        String url = d.getCurrentUrl().toLowerCase();
        boolean redirected = url.contains("/login") || !url.contains("/admin/support");
        Assert.assertTrue(redirected,
                "Unauthenticated user should be redirected — still at: " + url);
        ExtentReportUtil.logPass("Guest redirect confirmed — current URL: " + url);
    }
}
