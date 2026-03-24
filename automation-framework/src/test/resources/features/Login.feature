@Login
Feature: Login Functionality
  As a user
  I want to login to the application
  So that I can access the dashboard

  @Smoke @Positive
  Scenario Outline: Successful login by role
    Given I navigate to the login page
    When I login as "<role>" using credentials from config
    Then I should be logged in successfully

    Examples:
      | role        |
      | super admin |
      | admin       |
