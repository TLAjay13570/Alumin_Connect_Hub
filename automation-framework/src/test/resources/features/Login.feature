@Login
Feature: Login Functionality
  As a user
  I want to login to the application
  So that I can access the dashboard

  @Smoke @Positive
  Scenario: Super Admin successful login
    Given I navigate to the login page
    When I login as super admin using credentials from config
    Then I should see "Super Admin Dashboard" text on the page
