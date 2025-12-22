@Ads @SuperAdmin
Feature: Ads Management
  As a super admin
  I want to manage advertisements
  So that I can create, edit, toggle visibility, and delete ads across universities

  Background:
    Given I navigate to the login page
    When I login as "super admin" using credentials from config
    Then I should be logged in successfully
    And I navigate to the ads management page

  # ============================================
  # CREATE AD SCENARIOS
  # ============================================

  @Smoke @Positive @CreateAd @ImageAd
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
    # Cleanup - delete the created ad
    When I click on delete button for ad "Test Image Ad"
    And I confirm the deletion
    Then I should not see ad "Test Image Ad" in the ads list

  @Smoke @Positive @CreateAd @VideoAd
  Scenario: Create a video ad for specific universities
    When I click on the Create Ad button
    And I enter ad title "Test Video Ad"
    And I enter ad description "Video advertisement for selected universities"
    And I select media type "Video"
    And I enter media URL "https://example.com/promo-video.mp4"
    And I enter link URL "https://example.com/video-promo"
    And I select ad placement "Left Sidebar"
    And I uncheck "Show to all universities" checkbox
    And I select target university "Massachusetts Institute of Technology"
    And I click on "Create Ad" button in modal
    Then I should see success message containing "Ad created" or "created"
    And I should see ad "Test Video Ad" in the ads list
    # Cleanup - delete the created ad
    When I click on delete button for ad "Test Video Ad"
    And I confirm the deletion
    Then I should not see ad "Test Video Ad" in the ads list

  @Positive @CreateAd @RightSidebar
  Scenario: Create an ad for right sidebar placement
    When I click on the Create Ad button
    And I enter ad title "Right Sidebar Ad"
    And I enter ad description "This ad will appear in the right sidebar"
    And I select media type "Image"
    And I enter media URL "https://images.unsplash.com/photo-1551434678-e076c223a692?w=400"
    And I enter link URL "https://example.com/sidebar-offer"
    And I check "Show to all universities" checkbox
    And I click on "Create Ad" button in modal
    Then I should see success message containing "Ad created" or "created"
    And I should see ad "Right Sidebar Ad" in the ads list
    # Cleanup
    When I click on delete button for ad "Right Sidebar Ad"
    And I confirm the deletion
    Then I should not see ad "Right Sidebar Ad" in the ads list

  # Skipping negative validation scenario - button is disabled when fields are empty
  # @Negative @Validation
  # Scenario: Cannot create ad without required fields
  #   When I click on the Create Ad button
  #   And I click on "Create Ad" button in modal
  #   Then I should see error message containing "Missing information" or "required"

  # ============================================
  # EDIT AD SCENARIOS
  # ============================================

  @Smoke @Positive @EditAd
  Scenario: Edit an existing ad
    # First create an ad to edit
    When I click on the Create Ad button
    And I enter ad title "Ad To Edit"
    And I enter ad description "Original description"
    And I select media type "Image"
    And I enter media URL "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800"
    And I enter link URL "https://example.com/original"
    And I check "Show to all universities" checkbox
    And I click on "Create Ad" button in modal
    Then I should see success message containing "Ad created" or "created"
    # Now edit the ad
    When I click on edit button for ad "Ad To Edit"
    And I update ad title to "Updated Ad Title"
    And I update ad description to "Updated description for the ad"
    And I click on "Update Ad" button in modal
    Then I should see success message containing "Ad updated" or "updated"
    And I should see ad "Updated Ad Title" in the ads list
    # Cleanup
    When I click on delete button for ad "Updated Ad Title"
    And I confirm the deletion
    Then I should not see ad "Updated Ad Title" in the ads list

  # ============================================
  # TOGGLE VISIBILITY SCENARIOS
  # ============================================

  @Smoke @Positive @ToggleAd
  Scenario: Toggle ad visibility (activate/deactivate)
    # First create an ad
    When I click on the Create Ad button
    And I enter ad title "Toggle Test Ad"
    And I enter ad description "Ad for toggle test"
    And I select media type "Image"
    And I enter media URL "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800"
    And I enter link URL "https://example.com/toggle"
    And I check "Show to all universities" checkbox
    And I click on "Create Ad" button in modal
    Then I should see success message containing "Ad created" or "created"
    And I should see ad "Toggle Test Ad" in the ads list
    # Deactivate the ad
    When I click on toggle visibility button for ad "Toggle Test Ad"
    Then I should see success message containing "deactivated" or "no longer visible"
    # Reactivate the ad
    When I click on toggle visibility button for ad "Toggle Test Ad"
    Then I should see success message containing "activated" or "now visible"
    # Cleanup
    When I click on delete button for ad "Toggle Test Ad"
    And I confirm the deletion
    Then I should not see ad "Toggle Test Ad" in the ads list

  # ============================================
  # DELETE AD SCENARIOS
  # ============================================

  @Smoke @Positive @DeleteAd
  Scenario: Delete an ad
    # First create an ad to delete
    When I click on the Create Ad button
    And I enter ad title "Ad To Delete"
    And I enter ad description "This ad will be deleted"
    And I select media type "Image"
    And I enter media URL "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=800"
    And I enter link URL "https://example.com/delete-test"
    And I check "Show to all universities" checkbox
    And I click on "Create Ad" button in modal
    Then I should see success message containing "Ad created" or "created"
    And I should see ad "Ad To Delete" in the ads list
    # Now delete the ad
    When I click on delete button for ad "Ad To Delete"
    And I confirm the deletion
    Then I should see success message containing "Ad deleted" or "removed"
    And I should not see ad "Ad To Delete" in the ads list

  # ============================================
  # CLEANUP - DELETE ALL TEST ADS
  # ============================================

  @Cleanup @LastScenario
  Scenario: Cleanup - Delete all test ads created by feature file
    # Delete all test ads if they exist (in case any previous scenario failed before cleanup)
    When I delete ad "Test Image Ad" if it exists
    And I delete ad "Test Video Ad" if it exists
    And I delete ad "Right Sidebar Ad" if it exists
    And I delete ad "Ad To Edit" if it exists
    And I delete ad "Updated Ad Title" if it exists
    And I delete ad "Toggle Test Ad" if it exists
    And I delete ad "Ad To Delete" if it exists
    Then all test ads should be cleaned up
