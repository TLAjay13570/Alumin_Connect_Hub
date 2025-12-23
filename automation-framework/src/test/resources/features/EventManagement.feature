@Events @Admin
Feature: Event Management
  As a university admin
  I want to manage events
  So that I can create and delete events for my university

  Background:
    Given I navigate to the login page
    When I login as "admin" using credentials from config
    Then I should be logged in successfully
    And I navigate to the events management page

  @Smoke @Positive @CreateEvent
  Scenario: Create a new event
    When I click on the Create Event button
    And I enter event title "Test Automation Event"
    And I enter event description "This is a test event created by automation"
    And I enter event date "12/31/2025"
    And I enter event time "10:00 AM"
    And I enter event location "Test Location"
    And I select event category "Networking"
    And I click on "Create Event" button in modal
    Then I should see event "Test Automation Event" in the events list

  @Smoke @Positive @DeleteEvent @Cleanup
  Scenario: Delete the created event
    When I delete event "Test Automation Event" if it exists
    Then all test events should be cleaned up

