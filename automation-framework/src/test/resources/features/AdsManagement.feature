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
  Scenario Outline: Create an image ad for all universities
    When I click on the Create Ad button
    And I enter ad title "<adTitle>"
    And I enter ad description "<adDescription>"
    And I select media type "<mediaType>"
    And I enter media URL "<mediaUrl>"
    And I enter link URL "<linkUrl>"
    And I check "Show to all universities" checkbox
    And I click on "Create Ad" button in modal
    Then I should see success message containing "Ad created" or "created"
    And I should see ad "<adTitle>" in the ads list

    Examples:
      | adTitle      | adDescription                          | mediaType | mediaUrl                                                                      | linkUrl                    |
      | Test Image Ad | This is a test advertisement for automation | Image     | https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=800 | https://example.com/promo |

  @Smoke @Positive @DeleteAd @Cleanup
  Scenario Outline: Delete the created ad
    When I delete ad "<adTitlePrefix>" if it exists
    Then all test ads should be cleaned up

    Examples:
      | adTitlePrefix |
      | Test Image Ad |
