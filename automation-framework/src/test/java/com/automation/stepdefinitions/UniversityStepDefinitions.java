package com.automation.stepdefinitions;

import com.automation.pages.UniversityManagementPage;
import com.automation.pages.UserManagementPage;
import com.automation.utils.ExtentReportUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Step Definitions for University Management feature
 */
public class UniversityStepDefinitions {
    private static final Logger logger = LogManager.getLogger(UniversityStepDefinitions.class);
    private UniversityManagementPage universityManagementPage;
    private UserManagementPage userManagementPage;

    // Scenario-scoped suffix to avoid collisions from previous runs.
    private String scenarioSuffix;

    private String getScenarioSuffix() {
        if (scenarioSuffix == null) {
            scenarioSuffix = String.valueOf(System.currentTimeMillis());
        }
        return scenarioSuffix;
    }

    private String mapUniversityId(String universityId) {
        if (universityId == null) return null;
        if (universityId.equals("test-auto-uni") ||
                universityId.equals("edit-test-uni") ||
                universityId.equals("delete-test-uni")) {
            return universityId + "-" + getScenarioSuffix();
        }
        return universityId;
    }

    private String mapUniversityName(String universityName) {
        if (universityName == null) return null;
        if (universityName.equals("Test Auto University") ||
                universityName.equals("Edit Test University") ||
                universityName.equals("Updated Test University") ||
                universityName.equals("Delete Test University")) {
            return universityName + "-" + getScenarioSuffix();
        }
        return universityName;
    }

    private UniversityManagementPage getUniversityManagementPage() {
        if (universityManagementPage == null) {
            universityManagementPage = new UniversityManagementPage();
        }
        return universityManagementPage;
    }

    private UserManagementPage getUserManagementPage() {
        if (userManagementPage == null) {
            userManagementPage = new UserManagementPage();
        }
        return userManagementPage;
    }

    @Given("I navigate to the universities management page")
    public void i_navigate_to_the_universities_management_page() {
        logger.info("Navigating to universities management page");
        ExtentReportUtil.logInfo("Navigating to universities management page");
        getUniversityManagementPage().navigateToUniversitiesPage();
    }

    @When("I click on {string} button")
    public void i_click_on_button(String buttonText) {
        logger.info("Clicking on button: {}", buttonText);
        ExtentReportUtil.logInfo("Clicking on button: " + buttonText);
        
        if (buttonText.equalsIgnoreCase("Add University")) {
            getUniversityManagementPage().clickAddUniversityButton();
        } else if (buttonText.equalsIgnoreCase("Create") || buttonText.equalsIgnoreCase("Save")) {
            getUniversityManagementPage().clickCreateOrSaveButton();
        } else if (buttonText.equalsIgnoreCase("Update")) {
            getUniversityManagementPage().clickUpdateOrSaveButton();
        } else if (buttonText.equalsIgnoreCase("Add User")) {
            getUserManagementPage().clickAddUserButton();
        } else if (buttonText.equalsIgnoreCase("Create User")) {
            getUserManagementPage().clickCreateUserButton();
        } else {
            logger.warn("Unknown button: {}", buttonText);
        }
    }

    @And("I enter university ID {string}")
    public void i_enter_university_id(String universityId) {
        logger.info("Entering university ID: {}", universityId);
        ExtentReportUtil.logInfo("Entering university ID: " + universityId);
        String mappedId = mapUniversityId(universityId);
        getUniversityManagementPage().enterUniversityId(mappedId);
    }

    @And("I enter university name {string}")
    public void i_enter_university_name(String universityName) {
        logger.info("Entering university name: {}", universityName);
        ExtentReportUtil.logInfo("Entering university name: " + universityName);
        String mappedName = mapUniversityName(universityName);
        getUniversityManagementPage().enterUniversityName(mappedName);
    }

    @And("I enter university logo URL {string}")
    public void i_enter_university_logo_url(String logoUrl) {
        logger.info("Entering university logo URL: {}", logoUrl);
        ExtentReportUtil.logInfo("Entering university logo URL: " + logoUrl);
        getUniversityManagementPage().enterUniversityLogo(logoUrl);
    }

    @And("I click on {string} or {string} button")
    public void i_click_on_or_button(String button1, String button2) {
        logger.info("Clicking on {} or {} button", button1, button2);
        ExtentReportUtil.logInfo("Clicking on " + button1 + " or " + button2 + " button");
        
        // Determine which button to click based on context
        // If "Update" is present, use update button (for edit scenarios)
        // If "Create" is present, use create button (for add scenarios)
        // If only "Save" is present, try create first (most common for new records)
        boolean hasUpdate = button1.equalsIgnoreCase("Update") || button2.equalsIgnoreCase("Update");
        boolean hasCreate = button1.equalsIgnoreCase("Create") || button2.equalsIgnoreCase("Create");
        boolean hasSave = button1.equalsIgnoreCase("Save") || button2.equalsIgnoreCase("Save");
        
        if (hasUpdate) {
            // Edit scenario - use update button
            getUniversityManagementPage().clickUpdateOrSaveButton();
        } else if (hasCreate) {
            // Add scenario - use create button
            getUniversityManagementPage().clickCreateOrSaveButton();
        } else if (hasSave) {
            // Only Save present - use create button (default for new records)
            // If this is in edit context, the page should handle it appropriately
            getUniversityManagementPage().clickCreateOrSaveButton();
        } else {
            logger.warn("Unknown button combination: {} or {}", button1, button2);
            // Default to create/save button
            getUniversityManagementPage().clickCreateOrSaveButton();
        }
    }

    @Then("I should see success message containing {string} or {string}")
    public void i_should_see_success_message_containing_or(String text1, String text2) {
        logger.info("Verifying success message contains '{}' or '{}'", text1, text2);
        ExtentReportUtil.logInfo("Verifying success message contains '" + text1 + "' or '" + text2 + "'");
        
        // Wait for the toast message to update (delete flows can temporarily keep the previous toast visible).
        String actualMessage = getUniversityManagementPage().waitForSuccessMessageContainingEither(text1, text2);
        logger.info("Actual success message: '{}'", actualMessage);

        boolean containsText1 = actualMessage.toLowerCase().contains(text1.toLowerCase());
        boolean containsText2 = actualMessage.toLowerCase().contains(text2.toLowerCase());
        Assert.assertTrue(containsText1 || containsText2,
                "Success message does not contain '" + text1 + "' or '" + text2 + "'. Actual message: '" + actualMessage + "'");

        ExtentReportUtil.logPass("Success message verified: " + actualMessage);
    }

    @And("I should see university {string} in the university list")
    public void i_should_see_university_in_the_university_list(String universityName) {
        logger.info("Verifying university '{}' exists in the list", universityName);
        ExtentReportUtil.logInfo("Verifying university '" + universityName + "' exists in the list");

        String mappedName = mapUniversityName(universityName);
        boolean exists = getUniversityManagementPage().isUniversityInList(mappedName);
        Assert.assertTrue(exists, "University '" + universityName + "' is not found in the list");
        ExtentReportUtil.logPass("University '" + universityName + "' found in the list");
    }

    @Given("university {string} exists in the system")
    public void university_exists_in_the_system(String universityName) {
        logger.info("Verifying university '{}' exists in the system", universityName);
        ExtentReportUtil.logInfo("Verifying university '" + universityName + "' exists in the system");
        
        boolean exists = getUniversityManagementPage().verifyUniversityExists(universityName);
        if (!exists) {
            logger.warn("University '{}' does not exist. Test may fail if university is required.", universityName);
            ExtentReportUtil.logInfo("University '" + universityName + "' does not exist in the system");
        } else {
            ExtentReportUtil.logPass("University '" + universityName + "' exists in the system");
        }
    }

    @When("I click on edit button for university {string}")
    public void i_click_on_edit_button_for_university(String universityName) {
        logger.info("Clicking edit button for university: {}", universityName);
        ExtentReportUtil.logInfo("Clicking edit button for university: " + universityName);
        String mappedName = mapUniversityName(universityName);
        getUniversityManagementPage().clickEditButtonForUniversity(mappedName);
    }

    @And("I update university name to {string}")
    public void i_update_university_name_to(String updatedName) {
        logger.info("Updating university name to: {}", updatedName);
        ExtentReportUtil.logInfo("Updating university name to: " + updatedName);
        String mappedName = mapUniversityName(updatedName);
        getUniversityManagementPage().updateUniversityName(mappedName);
    }

    @When("I click on delete button for university {string}")
    public void i_click_on_delete_button_for_university(String universityName) {
        logger.info("Clicking delete button for university: {}", universityName);
        ExtentReportUtil.logInfo("Clicking delete button for university: " + universityName);
        String mappedName = mapUniversityName(universityName);
        getUniversityManagementPage().clickDeleteButtonForUniversity(mappedName);
    }

    @And("I confirm the deletion")
    public void i_confirm_the_deletion() {
        logger.info("Confirming deletion");
        ExtentReportUtil.logInfo("Confirming deletion");
        getUniversityManagementPage().confirmDeletion();
    }

    @And("I should not see university {string} in the university list")
    public void i_should_not_see_university_in_the_university_list(String universityName) {
        logger.info("Verifying university '{}' does not exist in the list", universityName);
        ExtentReportUtil.logInfo("Verifying university '" + universityName + "' does not exist in the list");

        String mappedName = mapUniversityName(universityName);
        boolean notInList = getUniversityManagementPage().waitForUniversityNotInList(mappedName);
        Assert.assertTrue(notInList, "University '" + universityName + "' still exists in the list");
        ExtentReportUtil.logPass("University '" + universityName + "' successfully removed from the list");
    }
}

