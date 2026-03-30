@Fundraiser
Feature: Fundraiser Management — admin lifecycle and access control
  Admin manages donation campaigns on /admin/fundraiser (localStorage-backed in the SPA).
  Alumni and guests must not stay on the admin fundraiser console.

  Background:
    Given I navigate to the login page
    When I login as "MIT admin" using credentials from config
    Then I should be logged in successfully

  @Fundraiser @Smoke @Positive
  Scenario: Admin opens fundraiser management and sees dashboard widgets
    When I navigate to the admin fundraiser management page
    Then I should see the fundraiser management page
    And the fundraiser statistics section should be visible

  @Fundraiser @Regression
  Scenario: Admin creates a fundraiser and sees it in the list
    When I navigate to the admin fundraiser management page
    And I open the create fundraiser dialog
    And I enter fundraiser campaign title "AUTO_FR_UNIQUE"
    And I enter fundraiser description "Automation campaign supporting students."
    And I enter fundraiser goal amount "50000"
    And I enter fundraiser current amount "1000"
    And I enter fundraiser donation link "https://example.org/donate-automation"
    And I set fundraiser dates to active campaign range
    And I submit the fundraiser create form
    Then I should see a fundraiser toast containing "Fundraiser created"
    And the fundraiser list should show a card with my tracked title

  @Fundraiser @Regression
  Scenario: Admin edits fundraiser description
    When I navigate to the admin fundraiser management page
    And I open the create fundraiser dialog
    And I enter fundraiser campaign title "AUTO_FR_UNIQUE"
    And I enter fundraiser description "Original description."
    And I enter fundraiser goal amount "25000"
    And I enter fundraiser donation link "https://example.org/donate-edit"
    And I set fundraiser dates to active campaign range
    And I submit the fundraiser create form
    Then I should see a fundraiser toast containing "Fundraiser created"
    And I open edit fundraiser for my tracked title
    And I change fundraiser description to "Updated description for automation."
    And I submit the fundraiser update form
    Then I should see a fundraiser toast containing "Fundraiser updated"

  @Fundraiser @Regression
  Scenario: Admin disables and re-enables a fundraiser
    When I navigate to the admin fundraiser management page
    And I open the create fundraiser dialog
    And I enter fundraiser campaign title "AUTO_FR_UNIQUE"
    And I enter fundraiser description "Toggle active state test."
    And I enter fundraiser goal amount "10000"
    And I enter fundraiser donation link "https://example.org/donate-toggle"
    And I set fundraiser dates to active campaign range
    And I submit the fundraiser create form
    Then I should see a fundraiser toast containing "Fundraiser created"
    When I disable the fundraiser with my tracked title
    Then I should see a fundraiser toast containing "deactivated"
    And the fundraiser card should show badge text "Disabled"
    When I enable the fundraiser with my tracked title
    Then I should see a fundraiser toast containing "activated"

  @Fundraiser @Regression @Cleanup
  Scenario: Admin deletes a fundraiser after confirmation
    When I navigate to the admin fundraiser management page
    And I open the create fundraiser dialog
    And I enter fundraiser campaign title "AUTO_FR_UNIQUE"
    And I enter fundraiser description "To be deleted."
    And I enter fundraiser goal amount "5000"
    And I enter fundraiser donation link "https://example.org/donate-delete"
    And I set fundraiser dates to active campaign range
    And I submit the fundraiser create form
    Then I should see a fundraiser toast containing "Fundraiser created"
    When I delete the fundraiser with my tracked title
    Then I should see a fundraiser toast containing "Fundraiser deleted"
    And the fundraiser list should not show a card with my tracked title

  @Fundraiser @Regression
  Scenario: Alumni session cannot use admin fundraiser management URL
    When I logout and clear session
    When I login as "MIT alumni" using credentials from config
    Then I should be logged in successfully
    When I navigate directly to the admin fundraiser URL as alumni session
    Then I should not remain on the admin fundraiser management page

  @Fundraiser @Regression
  Scenario: Unauthenticated user cannot open admin fundraiser management
    When I logout and clear session
    When I open the admin fundraiser page without authentication
    Then I should be redirected to the login flow or away from admin fundraiser management
