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
  Scenario Outline: Add a new university
    When I click on "Add University" button
    And I enter university ID "<universityId>"
    And I enter university name "<universityName>"
    And I enter university logo URL "<logoUrl>"
    And I click on "Create" or "Save" button
    Then I should see success message containing "created" or "added"
    And I should see university "<universityName>" in the university list

    Examples:
      | universityId  | universityName      | logoUrl                                                           |
      | test-auto-uni | Test Auto University | https://dummyimage.com/256x256/000/fff.png&text=University+Logo |

  @Smoke @Positive @Edit
  Scenario Outline: Edit an existing university
    When I click on "Add University" button
    And I enter university ID "<createId>"
    And I enter university name "<createName>"
    And I enter university logo URL "<createLogoUrl>"
    And I click on "Create" or "Save" button
    Then I should see success message containing "created" or "added"
    When I click on edit button for university "<createName>"
    And I update university name to "<updatedName>"
    And I click on "Update" or "Save" button
    Then I should see success message containing "updated" or "saved"
    And I should see university "<updatedName>" in the university list

    Examples:
      | createId      | createName           | createLogoUrl                                                     | updatedName           |
      | edit-test-uni | Edit Test University | https://dummyimage.com/256x256/222/fff.png&text=University+Logo | Updated Test University |

  @Smoke @Positive @Delete
  Scenario Outline: Delete a university
    When I click on "Add University" button
    And I enter university ID "<createId>"
    And I enter university name "<createName>"
    And I enter university logo URL "<createLogoUrl>"
    And I click on "Create" or "Save" button
    Then I should see success message containing "created" or "added"
    When I click on delete button for university "<createName>"
    And I confirm the deletion
    Then I should see success message containing "deleted" or "removed"
    And I should not see university "<createName>" in the university list

    Examples:
      | createId       | createName            | createLogoUrl                                                     |
      | delete-test-uni | Delete Test University | https://dummyimage.com/256x256/444/fff.png&text=University+Logo |
