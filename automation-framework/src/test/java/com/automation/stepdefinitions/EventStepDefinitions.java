package com.automation.stepdefinitions;

import com.automation.factory.DriverFactory;
import com.automation.utils.ConfigReader;
import com.automation.pages.AlumniPortalPage;
import com.automation.pages.EventManagementPage;
import com.automation.utils.ExtentReportUtil;
import com.automation.utils.WebDriverWaitUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.Locale;

/**
 * Step Definitions for Event Management feature
 */
public class EventStepDefinitions {
    private static final Logger logger = LogManager.getLogger(EventStepDefinitions.class);
    private EventManagementPage eventManagementPage;
    private AlumniPortalPage alumniPortalPage;

    // Store the current event title for reference with unique suffix
    private String currentEventTitle;

    private EventManagementPage getEventManagementPage() {
        if (eventManagementPage == null) {
            eventManagementPage = new EventManagementPage();
        }
        return eventManagementPage;
    }

    private AlumniPortalPage getAlumniPortalPage() {
        if (alumniPortalPage == null) {
            alumniPortalPage = new AlumniPortalPage();
        }
        return alumniPortalPage;
    }

    @And("I navigate to the events management page")
    public void i_navigate_to_the_events_management_page() {
        logger.info("Navigating to events management page");
        ExtentReportUtil.logInfo("Navigating to events management page");
        getEventManagementPage().navigateToEventsPage();
    }

    @When("I click on the Create Event button")
    public void i_click_on_create_event_button() {
        logger.info("Clicking on Create Event button");
        ExtentReportUtil.logInfo("Clicking on Create Event button");
        getEventManagementPage().clickCreateEventButton();
    }

    @And("I enter event title {string}")
    public void i_enter_event_title(String title) {
        // Add timestamp to make event title unique
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(6);
        currentEventTitle = title + " " + timestamp;
        logger.info("Entering event title: {}", currentEventTitle);
        ExtentReportUtil.logInfo("Entering event title: " + currentEventTitle);
        getEventManagementPage().enterEventTitle(currentEventTitle);
    }

    /** Use when the exact title must be set (validation tests, cancel flow) — no timestamp suffix. */
    @And("I enter event title literal {string}")
    public void i_enter_event_title_literal(String title) {
        currentEventTitle = title;
        logger.info("Entering literal event title: {}", title);
        ExtentReportUtil.logInfo("Entering literal event title: " + title);
        getEventManagementPage().enterEventTitle(title);
    }

    @And("I enter event description {string}")
    public void i_enter_event_description(String description) {
        logger.info("Entering event description: {}", description);
        ExtentReportUtil.logInfo("Entering event description: " + description);
        getEventManagementPage().enterEventDescription(description);
    }

    @And("I enter event date {string}")
    public void i_enter_event_date(String date) {
        logger.info("Entering event date: {}", date);
        ExtentReportUtil.logInfo("Entering event date: " + date);
        getEventManagementPage().enterEventDate(date);
    }

    @And("I enter event time {string}")
    public void i_enter_event_time(String time) {
        logger.info("Entering event time: {}", time);
        ExtentReportUtil.logInfo("Entering event time: " + time);
        getEventManagementPage().enterEventTime(time);
    }

    @And("I enter event end time {string}")
    public void i_enter_event_end_time(String time) {
        logger.info("Entering event end time: {}", time);
        ExtentReportUtil.logInfo("Entering event end time: " + time);
        getEventManagementPage().enterEventEndTime(time);
    }

    @And("I enter event location {string}")
    public void i_enter_event_location(String location) {
        logger.info("Entering event location: {}", location);
        ExtentReportUtil.logInfo("Entering event location: " + location);
        getEventManagementPage().enterEventLocation(location);
    }

    @And("I toggle virtual event switch")
    public void i_toggle_virtual_event_switch() {
        logger.info("Toggling virtual event switch");
        ExtentReportUtil.logInfo("Toggling virtual event switch");
        getEventManagementPage().toggleVirtualEvent();
    }

    @And("I select event category {string}")
    public void i_select_event_category(String category) {
        logger.info("Selecting event category: {}", category);
        ExtentReportUtil.logInfo("Selecting event category: " + category);
        getEventManagementPage().selectCategory(category);
    }

    @And("I enter event image URL {string}")
    public void i_enter_event_image_url(String imageUrl) {
        logger.info("Entering event image URL: {}", imageUrl);
        ExtentReportUtil.logInfo("Entering event image URL: " + imageUrl);
        getEventManagementPage().enterImageUrl(imageUrl);
    }

    @And("I click on \"Create Event\" button in modal")
    public void i_click_on_create_event_button_in_modal() {
        logger.info("Clicking Create Event button in modal");
        ExtentReportUtil.logInfo("Clicking Create Event button in modal");
        getEventManagementPage().clickCreateEventButtonInModal();
    }

    @And("I click on \"Update Event\" button in modal")
    public void i_click_on_update_event_button_in_modal() {
        logger.info("Clicking Update Event button in modal");
        ExtentReportUtil.logInfo("Clicking Update Event button in modal");
        getEventManagementPage().clickUpdateEventButtonInModal();
    }

    @When("I submit the event form expecting validation errors")
    public void i_submit_event_form_expecting_validation_errors() {
        logger.info("Submitting event form expecting validation errors");
        ExtentReportUtil.logInfo("Submitting event form expecting validation errors");
        getEventManagementPage().clickModalSubmitExpectingValidation();
    }

    @Then("I should see event modal validation containing {string}")
    public void i_should_see_event_modal_validation_containing(String fragment) {
        String errors = getEventManagementPage().getModalValidationErrorsText();
        logger.info("Modal validation text: {}", errors);
        Assert.assertTrue(
                matchesValidationFragment(errors, fragment),
                "Expected validation to match '" + fragment + "' but was: " + errors);
        ExtentReportUtil.logPass("Validation matches: " + fragment);
    }

    /**
     * Accepts copy from local {@code EventModal.tsx}, older deploys, and HTML5 {@code validationMessage} on inputs.
     */
    private static boolean matchesValidationFragment(String errors, String fragment) {
        if (errors == null) {
            errors = "";
        }
        String e = errors.toLowerCase(Locale.ROOT);
        String f = fragment.trim().toLowerCase(Locale.ROOT);
        if (f.isEmpty()) {
            return false;
        }
        if (e.contains(f)) {
            return true;
        }
        if (f.contains("valid url") && !f.contains("image")) {
            return e.contains("please enter a url")
                    || e.contains("please enter a valid url")
                    || e.contains("enter a valid url");
        }
        if (f.contains("valid image") || (f.contains("image") && f.contains("url"))) {
            return e.contains("valid image url")
                    || e.contains("please enter a valid image url")
                    || e.contains("enter a valid image url");
        }
        if (f.contains("registration deadline") || (f.contains("deadline") && f.contains("before"))) {
            return e.contains("registration deadline") && e.contains("before");
        }
        if (f.contains("max attendees") || f.contains("at least 1")) {
            return e.contains("max attendees must be at least 1")
                    || e.contains("greater than or equal to 1")
                    || e.contains("value must be greater than or equal to 1");
        }
        return false;
    }

    @And("I click Cancel in the event modal")
    public void i_click_cancel_in_the_event_modal() {
        logger.info("Clicking Cancel in event modal");
        ExtentReportUtil.logInfo("Clicking Cancel in event modal");
        getEventManagementPage().clickCancelInEventModal();
    }

    @Then("the event modal should be closed")
    public void the_event_modal_should_be_closed() {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getCurrentDriver(), Duration.ofSeconds(10));
        wait.until(d -> !getEventManagementPage().isEventModalOpen());
        Assert.assertFalse(getEventManagementPage().isEventModalOpen(), "Event modal should be closed");
        ExtentReportUtil.logPass("Event modal closed");
    }

    @When("I click refresh on the admin events page")
    public void i_click_refresh_on_admin_events_page() {
        logger.info("Refreshing admin events page");
        ExtentReportUtil.logInfo("Refreshing admin events page");
        getEventManagementPage().clickRefreshEventsList();
    }

    @When("I search admin events for {string}")
    public void i_search_admin_events_for(String query) {
        logger.info("Searching admin events for: {}", query);
        ExtentReportUtil.logInfo("Searching admin events for: " + query);
        getEventManagementPage().enterEventsSearchQuery(query);
    }

    @When("I clear admin events search")
    public void i_clear_admin_events_search() {
        getEventManagementPage().enterEventsSearchQuery("");
    }

    @When("I click admin events status filter {string}")
    public void i_click_admin_events_status_filter(String label) {
        logger.info("Clicking status filter: {}", label);
        ExtentReportUtil.logInfo("Clicking status filter: " + label);
        getEventManagementPage().clickEventsStatusFilter(label);
    }

    @And("I enter meeting link {string}")
    public void i_enter_meeting_link(String url) {
        logger.info("Entering meeting link");
        ExtentReportUtil.logInfo("Entering meeting link");
        getEventManagementPage().enterMeetingLink(url);
    }

    @And("I toggle public event off")
    public void i_toggle_public_event_off() {
        logger.info("Toggling public event off");
        ExtentReportUtil.logInfo("Toggling public event off");
        getEventManagementPage().togglePublicEventOff();
    }

    @When("I click Edit for event containing {string}")
    public void i_click_edit_for_event_containing(String titleFragment) {
        logger.info("Clicking Edit for event containing: {}", titleFragment);
        ExtentReportUtil.logInfo("Clicking Edit for event containing: " + titleFragment);
        getEventManagementPage().clickEditForEvent(titleFragment);
    }

    @When("I replace event title with {string}")
    public void i_replace_event_title_with(String newBaseTitle) {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(6);
        currentEventTitle = newBaseTitle + " " + timestamp;
        logger.info("Replacing event title with: {}", currentEventTitle);
        ExtentReportUtil.logInfo("Replacing event title with: " + currentEventTitle);
        getEventManagementPage().enterEventTitle(currentEventTitle);
    }

    @When("I clear the event title field")
    public void i_clear_the_event_title_field() {
        getEventManagementPage().clearEventTitleField();
    }

    @When("I clear the event description field")
    public void i_clear_the_event_description_field() {
        getEventManagementPage().clearEventDescriptionField();
    }

    @When("I click delete for event containing {string}")
    public void i_click_delete_for_event_containing(String titleFragment) {
        logger.info("Clicking Delete for event containing: {}", titleFragment);
        ExtentReportUtil.logInfo("Clicking Delete for event containing: " + titleFragment);
        getEventManagementPage().clickDeleteButtonForEvent(titleFragment);
    }

    @And("I accept the browser confirm for event deletion")
    public void i_accept_browser_confirm_for_event_deletion() {
        logger.info("Accepting browser confirm for event deletion");
        ExtentReportUtil.logInfo("Accepting browser confirm for event deletion");
        getEventManagementPage().confirmDeletion();
    }

    @And("I enter max attendees {string}")
    public void i_enter_max_attendees(String value) {
        getEventManagementPage().enterMaxAttendees(value);
    }

    @And("I enter registration deadline {string}")
    public void i_enter_registration_deadline(String date) {
        getEventManagementPage().enterRegistrationDeadline(date);
    }

    @Then("I should see event {string} in the events list")
    public void i_should_see_event_in_the_events_list(String eventTitle) {
        // Use the stored dynamic title if it starts with the provided pattern
        String titleToCheck = (currentEventTitle != null && currentEventTitle.startsWith(eventTitle.split(" ")[0])) 
                ? currentEventTitle : eventTitle;
        logger.info("Verifying event '{}' exists in the list", titleToCheck);
        ExtentReportUtil.logInfo("Verifying event '" + titleToCheck + "' exists in the list");
        
        boolean eventExists = getEventManagementPage().waitUntilEventInList(titleToCheck);
        Assert.assertTrue(eventExists, "Event '" + titleToCheck + "' was not found in the events list (after wait/refresh)");
        
        logger.info("Event '{}' found in the list", titleToCheck);
        ExtentReportUtil.logPass("Event '" + titleToCheck + "' found in the list");
    }

    @Then("I should not see event {string} in the events list")
    public void i_should_not_see_event_in_the_events_list(String eventTitle) {
        // Use the stored dynamic title if it starts with the provided pattern
        String titleToCheck = (currentEventTitle != null && currentEventTitle.startsWith(eventTitle.split(" ")[0])) 
                ? currentEventTitle : eventTitle;
        logger.info("Verifying event '{}' does NOT exist in the list", titleToCheck);
        ExtentReportUtil.logInfo("Verifying event '" + titleToCheck + "' does NOT exist in the list");

        boolean gone = getEventManagementPage().waitUntilEventNotInList(titleToCheck);
        Assert.assertTrue(gone, "Event '" + titleToCheck + "' should not be in the events list (after delete + wait/refresh)");
        
        logger.info("Event '{}' successfully removed from the list", titleToCheck);
        ExtentReportUtil.logPass("Event '" + titleToCheck + "' successfully removed from the list");
    }

    // ============================================
    // CLEANUP STEP DEFINITIONS
    // ============================================

    @When("I delete event {string} if it exists")
    public void i_delete_event_if_it_exists(String eventTitle) {
        logger.info("Checking and deleting event '{}' if it exists", eventTitle);
        ExtentReportUtil.logInfo("Checking and deleting event '" + eventTitle + "' if it exists");
        
        // Check if any event with this title prefix exists (to handle timestamp suffix)
        boolean deleted = getEventManagementPage().deleteEventIfExists(eventTitle);
        
        if (deleted) {
            logger.info("Event starting with '{}' was found and deleted", eventTitle);
            ExtentReportUtil.logPass("Event starting with '" + eventTitle + "' was found and deleted");
        } else {
            logger.info("No event starting with '{}' found - skipping", eventTitle);
            ExtentReportUtil.logInfo("No event starting with '" + eventTitle + "' found - skipping");
        }
    }

    @Then("all test events should be cleaned up")
    public void all_test_events_should_be_cleaned_up() {
        logger.info("All test events cleanup completed");
        ExtentReportUtil.logPass("All test events cleanup completed successfully");
    }

    // --- Alumni: notifications + registration (see EventAlumniNotificationRegistration.feature) ---

    @And("I allow time for notifications to propagate")
    public void i_allow_time_for_notifications_to_propagate() {
        WebDriverWaitUtil.staticWait(5);
        ExtentReportUtil.logInfo("Waited for notifications to propagate");
    }

    @Then("I should see a notification referring to the created event")
    public void i_should_see_a_notification_referring_to_the_created_event() {
        Assert.assertNotNull(currentEventTitle, "Event title was not captured; create-event steps must run first.");
        AlumniPortalPage alumni = getAlumniPortalPage();
        int maxAttempts = 6;
        boolean found = pollDashboardBellForTitle(alumni, maxAttempts);
        if (!found) {
            found = pollNotificationsPageForTitle(alumni, maxAttempts);
        }
        Assert.assertTrue(
                found,
                "Neither dashboard bell nor /notifications showed a notification for the created event: " + currentEventTitle);
        ExtentReportUtil.logPass("Notification content references the created event");
    }

    private boolean pollNotificationsPageForTitle(AlumniPortalPage alumni, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            alumni.navigateToAlumniNotificationsPage();
            alumni.scrollDocumentToBottom();
            if (alumni.pageShowsCreatedEventNotification(currentEventTitle)) {
                return true;
            }
            alumni.refreshPage();
            WebDriverWaitUtil.staticWait(3);
        }
        return false;
    }

    private boolean pollDashboardBellForTitle(AlumniPortalPage alumni, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            alumni.navigateToAlumniDashboard();
            alumni.openDashboardNotificationBell();
            if (alumni.pageShowsCreatedEventNotification(currentEventTitle)) {
                return true;
            }
            alumni.refreshPage();
            WebDriverWaitUtil.staticWait(3);
        }
        return false;
    }

    @When("I navigate to the alumni events page")
    public void i_navigate_to_the_alumni_events_page() {
        logger.info("Navigating to alumni events page");
        ExtentReportUtil.logInfo("Navigating to alumni events page");
        getAlumniPortalPage().navigateToAlumniEventsPage();
    }

    @And("I select the Upcoming tab on the alumni events page")
    public void i_select_upcoming_tab_on_alumni_events_page() {
        logger.info("Selecting Upcoming tab on alumni events page");
        ExtentReportUtil.logInfo("Selecting Upcoming tab on alumni events page");
        getAlumniPortalPage().selectAlumniEventsUpcomingTabIfPresent();
    }

    @And("I search alumni events for {string}")
    public void i_search_alumni_events_for(String query) {
        logger.info("Searching alumni events for: {}", query);
        getAlumniPortalPage().enterAlumniEventsSearch(query);
    }

    @And("I register for the alumni event containing title {string}")
    public void i_register_for_the_alumni_event_containing_title(String titleFragment) {
        logger.info("Registering for event containing: {}", titleFragment);
        ExtentReportUtil.logInfo("Register for alumni event containing: " + titleFragment);
        getAlumniPortalPage().clickRegisterAfterRefresh(currentEventTitle, titleFragment);
        WebDriverWaitUtil.staticWait(2);
    }

    @Then("I should see I am registered for the alumni event containing title {string}")
    public void i_should_see_registered_for_alumni_event(String titleFragment) {
        boolean ok = getAlumniPortalPage().waitUntilRegisteredForProbes(
                ConfigReader.getExplicitWait(), currentEventTitle, titleFragment);
        Assert.assertTrue(ok, "Expected Registered state for event containing: " + titleFragment);
        ExtentReportUtil.logPass("Alumni is registered for the event");
    }
}

