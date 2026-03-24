@Events @Alumni @Integration
Feature: Alumni notification and event registration after admin creates an event
  As a university alumni user
  I want to be notified when my university admin publishes an event
  So that I can open notifications and complete registration for that event

  # Runs outside the default suite (@Smoke and @Positive). Execute with:
  #   mvn test -Dcucumber.filter.tags="@Integration and @Events"
  # Logins: MIT admin (admin.username) then MIT alumni (alumni.username) from config.properties.
  # Prerequisites:
  #   - config.properties: admin.* (MIT admin), alumni.* (MIT alumni, same institution)
  #   - Notifications: dashboard bell first; /notifications page as fallback (flexible title match).

  Scenario Outline: MIT admin creates an event — MIT alumni sees notification and registers
    Given I navigate to the login page
    When I login as "MIT admin" using credentials from config
    Then I should be logged in successfully
    And I navigate to the events management page
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
    And I allow time for notifications to propagate
    When I logout and clear session
    When I login as "MIT alumni" using credentials from config
    Then I should be logged in successfully
    Then I should see a notification referring to the created event
    When I navigate to the alumni events page
    And I select the Upcoming tab on the alumni events page
    And I search alumni events for "<titleBase>"
    And I register for the alumni event containing title "<titleBase>"
    Then I should see I am registered for the alumni event containing title "<titleBase>"

    Examples:
      | titleBase                      | description                                         | eventDate  | startTime | endTime  | location              | category    |
      | Alumni E2E Notify Register Ev | Created by automation for alumni notify + register | 12/31/2030 | 10:00 AM  | 11:00 AM | Automation Test Venue | Networking |
