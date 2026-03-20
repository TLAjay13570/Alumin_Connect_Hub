@University @SuperAdmin
Feature: University Management
  As a super admin
  I want to manage universities
  So that I can add, edit, and delete university records

  Background:
    Given I navigate to the login page
    When I login as "super admin" using credentials from config
    Then I should be logged in successfully
    And I navigate to the universities management page

  @Smoke @Positive @Add
  Scenario: Add a new university
    When I click on "Add University" button
    And I enter university ID "test-auto-uni"
    And I enter university name "Test Auto University"
    And I enter university logo URL "https://dummyimage.com/256x256/000/fff.png&text=University+Logo"
    And I click on "Create" or "Save" button
    Then I should see success message containing "created" or "added"
    And I should see university "Test Auto University" in the university list

  @Smoke @Positive @Edit
  Scenario: Edit an existing university
    # First create a university to edit
    When I click on "Add University" button
    And I enter university ID "edit-test-uni"
    And I enter university name "Edit Test University"
    And I enter university logo URL "https://dummyimage.com/256x256/222/fff.png&text=University+Logo"
    And I click on "Create" or "Save" button
    Then I should see success message containing "created" or "added"
    # Now edit it
    When I click on edit button for university "Edit Test University"
    And I update university name to "Updated Test University"
    And I click on "Update" or "Save" button
    Then I should see success message containing "updated" or "saved"
    And I should see university "Updated Test University" in the university list

  @Smoke @Positive @Delete
  Scenario: Delete a university
    # First create a university to delete
    When I click on "Add University" button
    And I enter university ID "delete-test-uni"
    And I enter university name "Delete Test University"
    And I enter university logo URL "https://dummyimage.com/256x256/444/fff.png&text=University+Logo"
    And I click on "Create" or "Save" button
    Then I should see success message containing "created" or "added"
    # Now delete it
    When I click on delete button for university "Delete Test University"
    And I confirm the deletion
    Then I should see success message containing "deleted" or "removed"
    And I should not see university "Delete Test University" in the university list
