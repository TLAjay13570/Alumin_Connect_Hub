@Events @Admin @Regression @Negative
Feature: Event Management — Create form validation
  As a university admin
  I want invalid event data to be rejected in the modal
  So that only valid events are saved

  # @RequiresExtendedEventModal: needs Event Image URL + Registration Deadline in the modal (see src/components/EventModal.tsx).
  # Older deploys may omit these — use TestRunner tag "not @RequiresExtendedEventModal" or filter the same when running against dev.

  Background:
    Given I navigate to the login page
    When I login as "admin" using credentials from config
    Then I should be logged in successfully
    And I navigate to the events management page

  @Negative
  Scenario Outline: Title is required
    When I click on the Create Event button
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    When I submit the event form expecting validation errors
    Then I should see event modal validation containing "<expectedMessage>"
    And I click Cancel in the event modal

    Examples:
      | description        | eventDate  | startTime | endTime  | location  | expectedMessage     |
      | Missing title case | 12/31/2030 | 10:00 AM  | 11:00 AM | Somewhere | Title is required   |

  @Negative
  Scenario Outline: Description is required
    When I click on the Create Event button
    And I enter event title literal "<titleLiteral>"
    And I clear the event description field
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    When I submit the event form expecting validation errors
    Then I should see event modal validation containing "<expectedMessage>"
    And I click Cancel in the event modal

    Examples:
      | titleLiteral        | eventDate  | startTime | endTime  | location  | expectedMessage         |
      | No Description Title | 12/31/2030 | 10:00 AM  | 11:00 AM | Somewhere | Description is required |

  @Negative
  Scenario Outline: End time must be after start time
    When I click on the Create Event button
    And I enter event title literal "<titleLiteral>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    When I submit the event form expecting validation errors
    Then I should see event modal validation containing "<expectedMessage>"
    And I click Cancel in the event modal

    Examples:
      | titleLiteral  | description     | eventDate  | startTime | endTime  | location   | expectedMessage                        |
      | Bad Time Range | End before start | 12/31/2030 | 11:00 AM  | 10:00 AM | Clock Tower | End time must be after start time      |
      | Equal Time Bad | Same start end   | 12/31/2030 | 10:00 AM  | 10:00 AM | Clock Tower | End time must be after start time      |

  @Negative
  Scenario Outline: In-person events require location
    When I click on the Create Event button
    And I enter event title literal "<titleLiteral>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    When I submit the event form expecting validation errors
    Then I should see event modal validation containing "<expectedMessage>"
    And I click Cancel in the event modal

    Examples:
      | titleLiteral | description     | eventDate  | startTime | endTime  | location | expectedMessage           |
      | No Location  | Missing location | 12/31/2030 | 10:00 AM  | 11:00 AM |          | Location is required      |

  @Negative
  Scenario Outline: Virtual events require meeting link
    When I click on the Create Event button
    And I enter event title literal "<titleLiteral>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I toggle virtual event switch
    When I submit the event form expecting validation errors
    Then I should see event modal validation containing "<expectedMessage>"
    And I click Cancel in the event modal

    Examples:
      | titleLiteral  | description        | eventDate  | startTime | endTime  | expectedMessage              |
      | Virtual No Link | Virtual without URL | 12/31/2030 | 10:00 AM  | 11:00 AM | Meeting link is required |

  @Negative
  Scenario Outline: Virtual meeting link must be a valid URL
    When I click on the Create Event button
    And I enter event title literal "<titleLiteral>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I toggle virtual event switch
    And I enter meeting link "<meetingLink>"
    When I submit the event form expecting validation errors
    Then I should see event modal validation containing "<expectedMessage>"
    And I click Cancel in the event modal

    Examples:
      | titleLiteral  | description  | eventDate  | startTime | endTime  | meetingLink      | expectedMessage |
      | Bad Meeting URL | Invalid link | 12/31/2030 | 10:00 AM  | 11:00 AM | not-a-valid-url  | valid URL       |
      | Bad Meeting URL 2 | Bad zoom   | 12/31/2030 | 11:00 AM  | 12:00 PM | :::noturl        | valid URL       |

  @Negative @RequiresExtendedEventModal
  Scenario Outline: Optional image URL must be valid when provided
    When I click on the Create Event button
    And I enter event title literal "<titleLiteral>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    And I select event category "Academic"
    And I enter event image URL "<imageUrl>"
    When I submit the event form expecting validation errors
    Then I should see event modal validation containing "<expectedMessage>"
    And I click Cancel in the event modal

    Examples:
      | titleLiteral | description | eventDate  | startTime | endTime  | location | imageUrl  | expectedMessage    |
      | Bad Image URL | Bad banner | 12/31/2030 | 10:00 AM  | 11:00 AM | Gallery  | not-a-url | valid image URL    |

  @Negative
  Scenario Outline: Max attendees must be at least 1 when set
    When I click on the Create Event button
    And I enter event title literal "<titleLiteral>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    And I enter max attendees "<maxAttendees>"
    When I submit the event form expecting validation errors
    Then I should see event modal validation containing "<expectedMessage>"
    And I click Cancel in the event modal

    Examples:
      | titleLiteral  | description | eventDate  | startTime | endTime  | location | maxAttendees | expectedMessage                  |
      | Zero Capacity | Invalid max | 12/31/2030 | 10:00 AM  | 11:00 AM | Hall     | 0            | Max attendees must be at least 1 |

  @Negative @RequiresExtendedEventModal
  Scenario Outline: Registration deadline must be before event date
    When I click on the Create Event button
    And I enter event title literal "<titleLiteral>"
    And I enter event description "<description>"
    And I enter event date "<eventDate>"
    And I enter event time "<startTime>"
    And I enter event end time "<endTime>"
    And I enter event location "<location>"
    And I enter registration deadline "<registrationDeadline>"
    When I submit the event form expecting validation errors
    Then I should see event modal validation containing "<expectedMessage>"
    And I click Cancel in the event modal

    Examples:
      | titleLiteral | description        | eventDate  | startTime | endTime  | location | registrationDeadline | expectedMessage                          |
      | Bad Deadline | Deadline after event | 12/31/2030 | 10:00 AM  | 11:00 AM | Campus   | 01/15/2031           | Registration deadline must be before       |
