@Ads @SuperAdmin
Feature: Ads Management
  As a super admin
  I want to manage advertisements
  So that I can create and delete ads across universities

  Background:
    Given I navigate to the login page
    When I login as "super admin" using credentials from config
    Then I should be logged in successfully
    And I navigate to the ads management page

  @Smoke @Positive @CreateAd
  Scenario: Create an image ad for all universities
    When I click on the Create Ad button
    And I enter ad title "Test Image Ad"
    And I enter ad description "This is a test advertisement for automation"
    And I select media type "Image"
    And I enter media URL "https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=800"
    And I enter link URL "https://example.com/promo"
    And I check "Show to all universities" checkbox
    And I click on "Create Ad" button in modal
    Then I should see success message containing "Ad created" or "created"
    And I should see ad "Test Image Ad" in the ads list

  @Smoke @Positive @DeleteAd @Cleanup
  Scenario: Delete the created ad
    When I delete ad "Test Image Ad" if it exists
    Then all test ads should be cleaned up
