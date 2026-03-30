@Fundraiser @FundraiserValidation
Feature: Fundraiser Management — create form validation
  The SPA blocks submit when required fields are missing (toast: Missing information).
  Matches AdminFundraiser handleSubmit checks: title, goal, start, end.

  Background:
    Given I am logged in as MIT admin on the fundraiser management page

  @Fundraiser @FundraiserValidation @Regression
  Scenario: Create fundraiser fails when campaign title is missing
    When I open the create fundraiser dialog
    And I enter fundraiser goal amount "10000"
    And I enter fundraiser donation link "https://example.org/donate"
    And I set fundraiser dates to active campaign range
    And I attempt to submit the fundraiser create form
    Then I should see a fundraiser toast containing "Missing information"

  @Fundraiser @FundraiserValidation @Regression
  Scenario: Create fundraiser fails when goal amount is missing
    When I open the create fundraiser dialog
    And I enter fundraiser campaign title "AUTO_FR_UNIQUE"
    And I enter fundraiser description "Validation test without goal."
    And I enter fundraiser donation link "https://example.org/donate"
    And I set fundraiser dates to active campaign range
    And I clear the fundraiser goal amount field
    And I attempt to submit the fundraiser create form
    Then I should see a fundraiser toast containing "Missing information"

  @Fundraiser @FundraiserValidation @Regression
  Scenario: Create fundraiser fails when start or end date is missing
    When I open the create fundraiser dialog
    And I enter fundraiser campaign title "AUTO_FR_UNIQUE"
    And I enter fundraiser goal amount "5000"
    And I enter fundraiser donation link "https://example.org/donate"
    And I attempt to submit the fundraiser create form
    Then I should see a fundraiser toast containing "Missing information"
