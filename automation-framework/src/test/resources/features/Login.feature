@Login
Feature: Login Functionality
  As a user
  I want to login to the application
  So that I can access the dashboard

  Background:
    Given I navigate to the login page

  @Smoke @Positive
  Scenario: Successful login with valid credentials
    When I login with valid credentials from config
    Then I should be logged in successfully
    And I should see the dashboard

