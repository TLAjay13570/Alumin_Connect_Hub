@Events @Admin
Feature: Event Management (smoke)
  As a university admin
  I want to manage events
  So that I can create and delete events for my university

  # Default TestRunner runs @Smoke @Positive — these scenarios stay in the quick suite.

  Background:
    Given I navigate to the login page
    When I login as "admin" using credentials from config
    Then I should be logged in successfully
    And I navigate to the events management page

  @Smoke @Positive @CreateEvent
  Scenario Outline: Create a new event
    When I click on the Create Event button
    And I enter event title "<titleBase>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    And I select event category "<category>"
    And I click on "Create Event" button in modal
    Then I should see event "<titleBase>" in the events list

    Examples:
      | titleBase             | description                              | eventDate  | startTime | endTime  | location     | category   |
      | Test Automation Event | This is a test event created by automation | 12/31/2030 | 10:00 AM  | 11:00 AM | Test Location | Networking |

  @Smoke @Positive @DeleteEvent @Cleanup
  Scenario Outline: Delete the created event
    When I delete event "<titlePrefix>" if it exists
    Then all test events should be cleaned up

    Examples:
      | titlePrefix           |
      | Test Automation Event |
