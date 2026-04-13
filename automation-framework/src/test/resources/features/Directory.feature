@Directory @Alumni @Smoke
Feature: Alumni Directory
  As a logged-in alumni user
  I want to browse, search and interact with the member directory
  So that I can discover and connect with other alumni

  Background:
    Given I navigate to the login page
    When I login as "MIT alumni" using credentials from config
    Then I should be logged in successfully
    And I navigate to the directory page

  # ============================================
  # NAVIGATION
  # ============================================
  @Positive @Navigation
  Scenario: Alumni can open the Directory page
    Then the directory page header should be "Directory"
    And the directory search input should be visible
    And the directory should show the following tabs:
      | My Connections |
      | Suggestions    |
      | Requests       |
      | Sent           |
      | Global Map     |

  # ============================================
  # VIEW MEMBER LIST
  # ============================================
  @Positive @ListView
  Scenario: Alumni can view the list of members on the directory
    When I select the "My Connections" tab on directory
    Then the directory should display at least 1 member card
    And each directory member card should show name, job title and company

  # ============================================
  # SEARCH
  # ============================================
  @Positive @Search
  Scenario Outline: Alumni can search members by "<field>"
    When I select the "My Connections" tab on directory
    And I search the directory for "<query>"
    Then the directory should display only members matching "<query>"
    And the directory member count should be greater than 0

    Examples:
      | field     | query              |
      | name      | Bob                |
      | company   | Tech Company       |
      | job title | Software Engineer  |

  @Positive @Search @NoResults
  Scenario: Searching for a non-existing member shows no-results state
    When I select the "My Connections" tab on directory
    And I search the directory for "zzz-no-such-member-xyz"
    Then the directory should show the no-results empty state

  # ============================================
  # FILTER VIA TABS
  # ============================================
  @Positive @Filter
  Scenario Outline: Alumni can switch between directory tabs
    When I select the "<tab>" tab on directory
    Then the "<tab>" tab panel should be visible

    Examples:
      | tab            |
      | My Connections |
      | Suggestions    |
      | Requests       |
      | Sent           |
      | Global Map     |

  # ============================================
  # VIEW PROFILE DETAILS
  # ============================================
  @Positive @ProfileView
  Scenario: Alumni can view a member's card details
    When I select the "My Connections" tab on directory
    And I open the first member on the directory
    Then the member's name, job title, company, university and year should be displayed

  @Positive @ProfileView
  Scenario: Alumni can open a member's full profile via "View Profile"
    When I select the "My Connections" tab on directory
    And I click the View Profile button on the first directory member
    Then I should be navigated away from the directory page

  # ============================================
  # PROFILE INTERACTION (HAPPY FLOW)
  # ============================================
  @Positive @Message
  Scenario: Alumni can initiate a chat with a directory member
    When I select the "My Connections" tab on directory
    And I click the Message button on the first directory member
    Then I should be navigated to the chat page

  # ============================================
  # GLOBAL MAP
  # ============================================
  @Positive @GlobalMap
  Scenario: Alumni can view the global alumni distribution map
    When I select the "Global Map" tab on directory
    Then the global alumni distribution map should be visible
