package com.automation.stepdefinitions;

import com.automation.pages.AdsManagementPage;
import com.automation.utils.ExtentReportUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Step Definitions for Ads Management feature
 */
public class AdsStepDefinitions {
    private static final Logger logger = LogManager.getLogger(AdsStepDefinitions.class);
    private AdsManagementPage adsManagementPage;
    
    // Store the current ad title for reference with unique suffix
    private String currentAdTitle;

    public AdsStepDefinitions() {
        this.adsManagementPage = new AdsManagementPage();
    }

    @And("I navigate to the ads management page")
    public void i_navigate_to_the_ads_management_page() {
        logger.info("Navigating to ads management page");
        ExtentReportUtil.logInfo("Navigating to ads management page");
        adsManagementPage.navigateToAdsPage();
    }

    @When("I click on the Create Ad button")
    public void i_click_on_create_ad_button() {
        logger.info("Clicking on Create Ad button");
        ExtentReportUtil.logInfo("Clicking on Create Ad button");
        adsManagementPage.clickCreateAdButton();
    }

    @And("I enter ad title {string}")
    public void i_enter_ad_title(String title) {
        // Add timestamp to make ad title unique
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(6);
        currentAdTitle = title + " " + timestamp;
        logger.info("Entering ad title: {}", currentAdTitle);
        ExtentReportUtil.logInfo("Entering ad title: " + currentAdTitle);
        adsManagementPage.enterAdTitle(currentAdTitle);
    }

    @And("I enter ad description {string}")
    public void i_enter_ad_description(String description) {
        logger.info("Entering ad description: {}", description);
        ExtentReportUtil.logInfo("Entering ad description: " + description);
        adsManagementPage.enterAdDescription(description);
    }

    @And("I select media type {string}")
    public void i_select_media_type(String mediaType) {
        logger.info("Selecting media type: {}", mediaType);
        ExtentReportUtil.logInfo("Selecting media type: " + mediaType);
        adsManagementPage.selectMediaType(mediaType);
    }

    @And("I enter media URL {string}")
    public void i_enter_media_url(String mediaUrl) {
        logger.info("Entering media URL: {}", mediaUrl);
        ExtentReportUtil.logInfo("Entering media URL: " + mediaUrl);
        adsManagementPage.enterMediaUrl(mediaUrl);
    }

    @And("I enter link URL {string}")
    public void i_enter_link_url(String linkUrl) {
        logger.info("Entering link URL: {}", linkUrl);
        ExtentReportUtil.logInfo("Entering link URL: " + linkUrl);
        adsManagementPage.enterLinkUrl(linkUrl);
    }

    @And("I select ad placement {string}")
    public void i_select_ad_placement(String placement) {
        logger.info("Selecting ad placement: {}", placement);
        ExtentReportUtil.logInfo("Selecting ad placement: " + placement);
        adsManagementPage.selectAdPlacement(placement);
    }

    @And("I check {string} checkbox")
    public void i_check_checkbox(String checkboxLabel) {
        logger.info("Checking checkbox: {}", checkboxLabel);
        ExtentReportUtil.logInfo("Checking checkbox: " + checkboxLabel);
        if (checkboxLabel.contains("all universities")) {
            adsManagementPage.checkTargetAllUniversities();
        }
    }

    @And("I uncheck {string} checkbox")
    public void i_uncheck_checkbox(String checkboxLabel) {
        logger.info("Unchecking checkbox: {}", checkboxLabel);
        ExtentReportUtil.logInfo("Unchecking checkbox: " + checkboxLabel);
        if (checkboxLabel.contains("all universities")) {
            adsManagementPage.uncheckTargetAllUniversities();
        }
    }

    @And("I select target university {string}")
    public void i_select_target_university(String universityName) {
        logger.info("Selecting target university: {}", universityName);
        ExtentReportUtil.logInfo("Selecting target university: " + universityName);
        adsManagementPage.selectTargetUniversity(universityName);
    }

    @And("I click on \"Create Ad\" button in modal")
    public void i_click_on_create_ad_button_in_modal() {
        logger.info("Clicking Create Ad button in modal");
        ExtentReportUtil.logInfo("Clicking Create Ad button in modal");
        adsManagementPage.clickCreateAdButtonInModal();
    }

    @And("I click on \"Update Ad\" button in modal")
    public void i_click_on_update_ad_button_in_modal() {
        logger.info("Clicking Update Ad button in modal");
        ExtentReportUtil.logInfo("Clicking Update Ad button in modal");
        adsManagementPage.clickUpdateAdButtonInModal();
    }

    @Then("I should see ad {string} in the ads list")
    public void i_should_see_ad_in_the_ads_list(String adTitle) {
        // Use the stored dynamic title if it starts with the provided pattern
        String titleToCheck = (currentAdTitle != null && currentAdTitle.startsWith(adTitle.split(" ")[0])) 
                ? currentAdTitle : adTitle;
        logger.info("Verifying ad '{}' exists in the list", titleToCheck);
        ExtentReportUtil.logInfo("Verifying ad '" + titleToCheck + "' exists in the list");
        
        boolean adExists = adsManagementPage.isAdInList(titleToCheck);
        Assert.assertTrue(adExists, "Ad '" + titleToCheck + "' was not found in the ads list");
        
        logger.info("Ad '{}' found in the list", titleToCheck);
        ExtentReportUtil.logPass("Ad '" + titleToCheck + "' found in the list");
    }

    @Then("I should not see ad {string} in the ads list")
    public void i_should_not_see_ad_in_the_ads_list(String adTitle) {
        // Use the stored dynamic title if it starts with the provided pattern
        String titleToCheck = (currentAdTitle != null && currentAdTitle.startsWith(adTitle.split(" ")[0])) 
                ? currentAdTitle : adTitle;
        logger.info("Verifying ad '{}' does NOT exist in the list", titleToCheck);
        ExtentReportUtil.logInfo("Verifying ad '" + titleToCheck + "' does NOT exist in the list");
        
        boolean adNotExists = adsManagementPage.isAdNotInList(titleToCheck);
        Assert.assertTrue(adNotExists, "Ad '" + titleToCheck + "' should not be in the ads list");
        
        logger.info("Ad '{}' successfully removed from the list", titleToCheck);
        ExtentReportUtil.logPass("Ad '" + titleToCheck + "' successfully removed from the list");
    }

    @Then("I should see ad {string} with status {string}")
    public void i_should_see_ad_with_status(String adTitle, String expectedStatus) {
        // Use the stored dynamic title if it starts with the provided pattern
        String titleToCheck = (currentAdTitle != null && currentAdTitle.startsWith(adTitle.split(" ")[0])) 
                ? currentAdTitle : adTitle;
        logger.info("Verifying ad '{}' has status '{}'", titleToCheck, expectedStatus);
        ExtentReportUtil.logInfo("Verifying ad '" + titleToCheck + "' has status '" + expectedStatus + "'");
        
        boolean statusMatch = adsManagementPage.isAdWithStatus(titleToCheck, expectedStatus);
        Assert.assertTrue(statusMatch, "Ad '" + titleToCheck + "' does not have status '" + expectedStatus + "'");
        
        logger.info("Ad '{}' has expected status '{}'", titleToCheck, expectedStatus);
        ExtentReportUtil.logPass("Ad '" + titleToCheck + "' has status '" + expectedStatus + "'");
    }

    @When("I click on edit button for ad {string}")
    public void i_click_on_edit_button_for_ad(String adTitle) {
        // Use the stored dynamic title if it starts with the provided pattern
        String titleToEdit = (currentAdTitle != null && currentAdTitle.startsWith(adTitle.split(" ")[0])) 
                ? currentAdTitle : adTitle;
        logger.info("Clicking edit button for ad: {}", titleToEdit);
        ExtentReportUtil.logInfo("Clicking edit button for ad: " + titleToEdit);
        adsManagementPage.clickEditButtonForAd(titleToEdit);
    }

    @And("I update ad title to {string}")
    public void i_update_ad_title_to(String newTitle) {
        // Add timestamp to make the updated title unique
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(6);
        currentAdTitle = newTitle + " " + timestamp;
        logger.info("Updating ad title to: {}", currentAdTitle);
        ExtentReportUtil.logInfo("Updating ad title to: " + currentAdTitle);
        adsManagementPage.updateAdTitle(currentAdTitle);
    }

    @And("I update ad description to {string}")
    public void i_update_ad_description_to(String newDescription) {
        logger.info("Updating ad description to: {}", newDescription);
        ExtentReportUtil.logInfo("Updating ad description to: " + newDescription);
        adsManagementPage.updateAdDescription(newDescription);
    }

    @When("I click on toggle visibility button for ad {string}")
    public void i_click_on_toggle_visibility_button_for_ad(String adTitle) {
        // Use the stored dynamic title if it starts with the provided pattern
        String titleToToggle = (currentAdTitle != null && currentAdTitle.startsWith(adTitle.split(" ")[0])) 
                ? currentAdTitle : adTitle;
        logger.info("Clicking toggle visibility button for ad: {}", titleToToggle);
        ExtentReportUtil.logInfo("Clicking toggle visibility button for ad: " + titleToToggle);
        adsManagementPage.clickToggleVisibilityButtonForAd(titleToToggle);
    }

    @When("I click on delete button for ad {string}")
    public void i_click_on_delete_button_for_ad(String adTitle) {
        // Use the stored dynamic title if it starts with the provided pattern
        String titleToDelete = (currentAdTitle != null && currentAdTitle.startsWith(adTitle.split(" ")[0])) 
                ? currentAdTitle : adTitle;
        logger.info("Clicking delete button for ad: {}", titleToDelete);
        ExtentReportUtil.logInfo("Clicking delete button for ad: " + titleToDelete);
        adsManagementPage.clickDeleteButtonForAd(titleToDelete);
    }

    @Then("I should see error message containing {string} or {string}")
    public void i_should_see_error_message_containing_or(String text1, String text2) {
        logger.info("Verifying error message contains '{}' or '{}'", text1, text2);
        ExtentReportUtil.logInfo("Verifying error message contains '" + text1 + "' or '" + text2 + "'");

        String actualMessage = adsManagementPage.getToastMessage();
        boolean containsText1 = adsManagementPage.isToastMessageContaining(text1);
        boolean containsText2 = adsManagementPage.isToastMessageContaining(text2);

        Assert.assertTrue(containsText1 || containsText2,
            "Error message does not contain '" + text1 + "' or '" + text2 + "'. Actual: '" + actualMessage + "'");

        ExtentReportUtil.logPass("Error message verified: " + actualMessage);
    }

    // ============================================
    // CLEANUP STEP DEFINITIONS
    // ============================================

    @When("I delete ad {string} if it exists")
    public void i_delete_ad_if_it_exists(String adTitle) {
        logger.info("Checking and deleting ad '{}' if it exists", adTitle);
        ExtentReportUtil.logInfo("Checking and deleting ad '" + adTitle + "' if it exists");
        
        // Check if any ad with this title prefix exists (to handle timestamp suffix)
        boolean deleted = adsManagementPage.deleteAdIfExists(adTitle);
        
        if (deleted) {
            logger.info("Ad starting with '{}' was found and deleted", adTitle);
            ExtentReportUtil.logPass("Ad starting with '" + adTitle + "' was found and deleted");
        } else {
            logger.info("No ad starting with '{}' found - skipping", adTitle);
            ExtentReportUtil.logInfo("No ad starting with '" + adTitle + "' found - skipping");
        }
    }

    @Then("all test ads should be cleaned up")
    public void all_test_ads_should_be_cleaned_up() {
        logger.info("All test ads cleanup completed");
        ExtentReportUtil.logPass("All test ads cleanup completed successfully");
    }
}

