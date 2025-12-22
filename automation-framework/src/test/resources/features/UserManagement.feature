@Users @SuperAdmin
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
  Scenario: Create a University Admin user
    When I click on "Add User" button
    And I select role "University Administrator"
    And I enter user name "Test Admin User"
    And I enter user email "test.admin@university.edu"
    And I enter user password "password123"
    And I select university "Massachusetts Institute of Technology"
    And I click on "Create User" button
    Then I should see success message containing "created" or "added"
    And I should see user "Test Admin User" in the users list
    # Cleanup - delete the created user
    When I click on delete button for user "Test Admin User"
    And I confirm the deletion
    Then I should see success message containing "deleted" or "removed"
    And I should not see user "Test Admin User" in the users list

  @Smoke @Positive @CreateUser @AlumniUser
  Scenario: Create an Alumni user
    When I click on "Add User" button
    And I select role "Alumni"
    And I enter user name "Test Alumni User"
    And I enter user email "test.alumni@alumni.edu"
    And I enter user password "password123"
    And I select university "Stanford University"
    And I enter graduation year "2023"
    And I enter major "Computer Science"
    And I click on "Create User" button
    Then I should see success message containing "created" or "added"
    And I should see user "Test Alumni User" in the users list
    # Cleanup - delete the created user
    When I click on delete button for user "Test Alumni User"
    And I confirm the deletion
    Then I should see success message containing "deleted" or "removed"
    And I should not see user "Test Alumni User" in the users list

  # ============================================
  # DELETE USER SCENARIOS
  # ============================================

  @Smoke @Positive @DeleteUser
  Scenario: Delete a user
    # Create a user to delete
    When I click on "Add User" button
    And I select role "Alumni"
    And I enter user name "Delete Test User"
    And I enter user email "delete.test@alumni.edu"
    And I enter user password "password123"
    And I select university "Massachusetts Institute of Technology"
    And I enter graduation year "2022"
    And I enter major "Business"
    And I click on "Create User" button
    Then I should see success message containing "created" or "added"
    # Now delete the user (this is the main test)
    When I click on delete button for user "Delete Test User"
    And I confirm the deletion
    Then I should see success message containing "deleted" or "removed"
    And I should not see user "Delete Test User" in the users list
