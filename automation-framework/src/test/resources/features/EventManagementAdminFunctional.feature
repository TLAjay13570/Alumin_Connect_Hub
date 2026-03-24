@Events @Admin @Regression
Feature: Event Management — Admin functional flows
  As a university admin
  I want to create, edit, search, filter, and remove events
  So that the event catalog stays accurate

  # Run with Maven/CLI, e.g. override Cucumber tags to include @Regression
  Background:
    Given I navigate to the login page
    When I login as "admin" using credentials from config
    Then I should be logged in successfully
    And I navigate to the events management page

  @Positive
  Scenario Outline: Refresh keeps event management page usable
    When I click refresh on the admin events page
    Then I should see "<expectedText>" text on the page

    Examples:
      | expectedText     |
      | Event Management |

  @Positive
  Scenario Outline: Cancel create event closes modal without saving
    When I click on the Create Event button
    And I enter event title literal "<literalTitle>"
    And I click Cancel in the event modal
    Then the event modal should be closed
    And I should not see event "<literalTitle>" in the events list

    Examples:
      | literalTitle                    |
      | Should Not Persist Cancel Test  |

  @Positive
  Scenario Outline: Create a virtual event with meeting link
    When I click on the Create Event button
    And I enter event title "<titleBase>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I select event category "<category>"
    And I toggle virtual event switch
    And I enter meeting link "<meetingLink>"
    And I click on "Create Event" button in modal
    Then I should see event "<titleBase>" in the events list
    When I delete event "<titleBase>" if it exists
    Then all test events should be cleaned up

    Examples:
      | titleBase               | description               | eventDate  | startTime | endTime | meetingLink                        | category |
      | Virtual Functional Event | Online session for automation | 12/31/2030 | 2:00 PM   | 3:30 PM | https://zoom.us/j/123456789       | Academic |
      | Virtual Meet Event      | Second virtual dataset    | 12/31/2030 | 3:00 PM   | 4:00 PM | https://meet.google.com/abc-def-ghi | Academic |

  @Positive
  Scenario Outline: Create private event and see Private badge
    When I click on the Create Event button
    And I enter event title "<titleBase>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    And I select event category "<category>"
    And I toggle public event off
    And I click on "Create Event" button in modal
    Then I should see event "<titleBase>" in the events list
    And I should see "<badgeText>" text on the page
    When I delete event "<titleBase>" if it exists
    Then all test events should be cleaned up

    Examples:
      | titleBase                | description              | eventDate  | startTime | endTime  | location      | category   | badgeText |
      | Private Functional Event | Invite-only automation event | 12/31/2030 | 9:00 AM   | 10:00 AM | Boston Campus | Networking | Private   |

  @Positive
  Scenario Outline: Edit event title and persist changes
    When I click on the Create Event button
    And I enter event title "<seedTitleBase>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    And I select event category "<category>"
    And I click on "Create Event" button in modal
    Then I should see event "<seedTitleBase>" in the events list
    When I click Edit for event containing "<seedTitleBase>"
    And I replace event title with "<updatedTitleBase>"
    And I click on "Update Event" button in modal
    Then I should see event "<updatedTitleBase>" in the events list
    When I delete event "<updatedTitleBase>" if it exists
    Then all test events should be cleaned up

    Examples:
      | seedTitleBase      | description      | eventDate  | startTime | endTime  | location       | category | updatedTitleBase       |
      | Func Edit Seed Event | Seed for edit flow | 12/31/2030 | 11:00 AM  | 12:00 PM | Edit Flow Hall | Academic | Func Edit Updated Event |

  @Positive
  Scenario Outline: Search filters admin events list by title
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
    When I search admin events for "<searchQuery>"
    Then I should see event "<titleBase>" in the events list
    When I clear admin events search
    When I delete event "<titleBase>" if it exists
    Then all test events should be cleaned up

    Examples:
      | titleBase               | description           | eventDate  | startTime | endTime | location   | category   | searchQuery       |
      | SearchUniqueMarker Event | Used for search assertion | 12/31/2030 | 4:00 PM   | 5:00 PM | Search City | Technology | SearchUniqueMarker |

  @Positive
  Scenario Outline: Status filter Upcoming still shows a new event
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
    When I click admin events status filter "<filterUpcoming>"
    Then I should see event "<titleBase>" in the events list
    When I click admin events status filter "<filterTotal>"
    When I delete event "<titleBase>" if it exists
    Then all test events should be cleaned up

    Examples:
      | titleBase            | description        | eventDate  | startTime | endTime | location   | category | filterUpcoming | filterTotal |
      | Upcoming Filter Event | Filter chip regression | 12/31/2030 | 6:00 PM   | 7:00 PM | Filter Hall | Career   | Upcoming       | Total       |

  @Positive
  Scenario Outline: Delete event with browser confirm removes it from list
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
    When I click delete for event containing "<titleBase>"
    And I accept the browser confirm for event deletion
    Then I should not see event "<titleBase>" in the events list

    Examples:
      | titleBase           | description              | eventDate  | startTime | endTime | location   | category |
      | Delete Confirm Event | To be deleted with confirm | 12/31/2030 | 8:00 AM   | 9:00 AM | Delete Lane | Social   |

  @Positive
  Scenario Outline: Optional max attendees is accepted on create
    When I click on the Create Event button
    And I enter event title "<titleBase>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    And I select event category "<category>"
    And I enter max attendees "<maxAttendees>"
    And I click on "Create Event" button in modal
    Then I should see event "<titleBase>" in the events list
    When I delete event "<titleBase>" if it exists
    Then all test events should be cleaned up

    Examples:
      | titleBase          | description  | eventDate  | startTime | endTime | location     | category | maxAttendees |
      | Max Attendees Event | Capacity test | 12/31/2030 | 1:00 PM   | 2:00 PM | Capacity Room | Academic | 25           |
      | Max Attendees Event 50 | Capacity fifty | 12/31/2030 | 2:00 PM   | 3:00 PM | Hall B       | Academic | 50           |
