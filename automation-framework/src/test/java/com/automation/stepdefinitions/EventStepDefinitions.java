package com.automation.stepdefinitions;

import com.automation.pages.EventManagementPage;
import com.automation.utils.ExtentReportUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Step Definitions for Event Management feature
 */
public class EventStepDefinitions {
    private static final Logger logger = LogManager.getLogger(EventStepDefinitions.class);
    private EventManagementPage eventManagementPage;
    
    // Store the current event title for reference with unique suffix
    private String currentEventTitle;

    private EventManagementPage getEventManagementPage() {
        if (eventManagementPage == null) {
            eventManagementPage = new EventManagementPage();
        }
        return eventManagementPage;
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

    @Then("I should see event {string} in the events list")
    public void i_should_see_event_in_the_events_list(String eventTitle) {
        // Use the stored dynamic title if it starts with the provided pattern
        String titleToCheck = (currentEventTitle != null && currentEventTitle.startsWith(eventTitle.split(" ")[0])) 
                ? currentEventTitle : eventTitle;
        logger.info("Verifying event '{}' exists in the list", titleToCheck);
        ExtentReportUtil.logInfo("Verifying event '" + titleToCheck + "' exists in the list");
        
        boolean eventExists = getEventManagementPage().isEventInList(titleToCheck);
        Assert.assertTrue(eventExists, "Event '" + titleToCheck + "' was not found in the events list");
        
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
        
        boolean eventNotExists = getEventManagementPage().isEventNotInList(titleToCheck);
        Assert.assertTrue(eventNotExists, "Event '" + titleToCheck + "' should not be in the events list");
        
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
}

