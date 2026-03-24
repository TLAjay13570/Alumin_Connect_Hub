@Users @SuperAdmin @requires-user-api
Feature: User Management
  As a super admin
  I want to manage users across the platform
  So that I can create, toggle status, and delete different types of users

  Background:
    Given I navigate to the login page
    When I login as "super admin" using credentials from config
    Then I should be logged in successfully
    And I navigate to the users management page

  # ============================================
  # CREATE USER SCENARIOS - Using existing universities
  # ============================================

  @Smoke @Positive @CreateUser @AdminUser
  Scenario Outline: Create a University Admin user
    When I click on "Add User" button
    And I select role "<role>"
    And I enter user name "<userName>"
    And I enter user email "<userEmail>"
    And I enter user password "<password>"
    And I select university "<university>"
    And I click on "Create User" button
    Then I should see success message containing "created" or "added"
    And I should see user "<userName>" in the users list
    When I click on delete button for user "<userName>"
    And I confirm the deletion
    Then I should see success message containing "deleted" or "removed"
    And I should not see user "<userName>" in the users list

    Examples:
      | role                     | userName        | userEmail               | password   | university                          |
      | University Administrator | Test Admin User | test.admin@university.edu | password123 | Massachusetts Institute of Technology |

  @Smoke @Positive @CreateUser @AlumniUser
  Scenario Outline: Create an Alumni user
    When I click on "Add User" button
    And I select role "<role>"
    And I enter user name "<userName>"
    And I enter user email "<userEmail>"
    And I enter user password "<password>"
    And I select university "<university>"
    And I enter graduation year "<graduationYear>"
    And I enter major "<major>"
    And I click on "Create User" button
    Then I should see success message containing "created" or "added"
    And I should see user "<userName>" in the users list
    When I click on delete button for user "<userName>"
    And I confirm the deletion
    Then I should see success message containing "deleted" or "removed"
    And I should not see user "<userName>" in the users list

    Examples:
      | role   | userName         | userEmail            | password   | university         | graduationYear | major            |
      | Alumni | Test Alumni User | test.alumni@alumni.edu | password123 | Stanford University | 2023           | Computer Science |

  # ============================================
  # DELETE USER SCENARIOS
  # ============================================

  @Smoke @Positive @DeleteUser
  Scenario Outline: Delete a user
    When I click on "Add User" button
    And I select role "<role>"
    And I enter user name "<userName>"
    And I enter user email "<userEmail>"
    And I enter user password "<password>"
    And I select university "<university>"
    And I enter graduation year "<graduationYear>"
    And I enter major "<major>"
    And I click on "Create User" button
    Then I should see success message containing "created" or "added"
    When I click on delete button for user "<userName>"
    And I confirm the deletion
    Then I should see success message containing "deleted" or "removed"
    And I should not see user "<userName>" in the users list

    Examples:
      | role   | userName         | userEmail              | password   | university                          | graduationYear | major   |
      | Alumni | Delete Test User | delete.test@alumni.edu | password123 | Massachusetts Institute of Technology | 2022           | Business |
