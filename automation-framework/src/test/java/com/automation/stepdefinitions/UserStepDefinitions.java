package com.automation.stepdefinitions;

import com.automation.pages.UserManagementPage;
import com.automation.utils.ExtentReportUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Step Definitions for User Management feature
 */
public class UserStepDefinitions {
    private static final Logger logger = LogManager.getLogger(UserStepDefinitions.class);
    private UserManagementPage userManagementPage;
    
    // Store the current user name for reference
    private String currentUserName;
    private String currentUserEmail;

    private UserManagementPage getUserManagementPage() {
        if (userManagementPage == null) {
            userManagementPage = new UserManagementPage();
        }
        return userManagementPage;
    }

    @And("I navigate to the users management page")
    public void i_navigate_to_the_users_management_page() {
        logger.info("Navigating to users management page");
        ExtentReportUtil.logInfo("Navigating to users management page");
        getUserManagementPage().navigateToUsersPage();
    }

    @When("I select role {string}")
    public void i_select_role(String role) {
        logger.info("Selecting role: {}", role);
        ExtentReportUtil.logInfo("Selecting role: " + role);
        getUserManagementPage().selectRole(role);
    }

    @And("I enter user name {string}")
    public void i_enter_user_name(String name) {
        // Add timestamp to make user name unique
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(6);
        currentUserName = name + " " + timestamp;
        logger.info("Entering user name: {}", currentUserName);
        ExtentReportUtil.logInfo("Entering user name: " + currentUserName);
        getUserManagementPage().enterUserName(currentUserName);
    }

    @And("I enter user email {string}")
    public void i_enter_user_email(String email) {
        // Add timestamp to make email unique
        String timestamp = String.valueOf(System.currentTimeMillis());
        String[] parts = email.split("@");
        currentUserEmail = parts[0] + "." + timestamp + "@" + (parts.length > 1 ? parts[1] : "test.com");
        logger.info("Entering user email: {}", currentUserEmail);
        ExtentReportUtil.logInfo("Entering user email: " + currentUserEmail);
        getUserManagementPage().enterUserEmail(currentUserEmail);
    }

    @And("I enter user password {string}")
    public void i_enter_user_password(String password) {
        logger.info("Entering user password");
        ExtentReportUtil.logInfo("Entering user password");
        getUserManagementPage().enterUserPassword(password);
    }

    @And("I select university {string}")
    public void i_select_university(String universityName) {
        logger.info("Selecting university: {}", universityName);
        ExtentReportUtil.logInfo("Selecting university: " + universityName);
        getUserManagementPage().selectUniversity(universityName);
    }

    @And("I enter graduation year {string}")
    public void i_enter_graduation_year(String year) {
        logger.info("Entering graduation year: {}", year);
        ExtentReportUtil.logInfo("Entering graduation year: " + year);
        getUserManagementPage().enterGraduationYear(year);
    }

    @And("I enter major {string}")
    public void i_enter_major(String major) {
        logger.info("Entering major: {}", major);
        ExtentReportUtil.logInfo("Entering major: " + major);
        getUserManagementPage().enterMajor(major);
    }

    @And("I toggle mentor status on")
    public void i_toggle_mentor_status_on() {
        logger.info("Toggling mentor status on");
        ExtentReportUtil.logInfo("Toggling mentor status on");
        getUserManagementPage().toggleMentorOn();
    }

    @Then("I should see user {string} in the users list")
    public void i_should_see_user_in_the_users_list(String userName) {
        // Use the stored dynamic name if it starts with the provided pattern
        String nameToCheck = (currentUserName != null && currentUserName.startsWith(userName.split(" ")[0])) 
                ? currentUserName : userName;
        logger.info("Verifying user '{}' exists in the list", nameToCheck);
        ExtentReportUtil.logInfo("Verifying user '" + nameToCheck + "' exists in the list");
        
        boolean userExists = getUserManagementPage().isUserInList(nameToCheck);
        Assert.assertTrue(userExists, "User '" + nameToCheck + "' was not found in the users list");
        
        logger.info("User '{}' found in the list", nameToCheck);
        ExtentReportUtil.logPass("User '" + nameToCheck + "' found in the list");
    }

    @Then("I should not see user {string} in the users list")
    public void i_should_not_see_user_in_the_users_list(String userName) {
        // Use the stored dynamic name if it starts with the provided pattern
        String nameToCheck = (currentUserName != null && currentUserName.startsWith(userName.split(" ")[0])) 
                ? currentUserName : userName;
        logger.info("Verifying user '{}' does not exist in the list", nameToCheck);
        ExtentReportUtil.logInfo("Verifying user '" + nameToCheck + "' does not exist in the list");
        
        boolean userNotExists = getUserManagementPage().isUserNotInList(nameToCheck);
        Assert.assertTrue(userNotExists, "User '" + nameToCheck + "' should not be in the users list");
        
        logger.info("User '{}' successfully removed from the list", nameToCheck);
        ExtentReportUtil.logPass("User '" + nameToCheck + "' successfully removed from the list");
    }

    @When("I click on toggle status button for user {string}")
    public void i_click_on_toggle_status_button_for_user(String userName) {
        // Use the stored dynamic name if it starts with the provided pattern
        String nameToToggle = (currentUserName != null && currentUserName.startsWith(userName.split(" ")[0])) 
                ? currentUserName : userName;
        logger.info("Clicking toggle status button for user: {}", nameToToggle);
        ExtentReportUtil.logInfo("Clicking toggle status button for user: " + nameToToggle);
        getUserManagementPage().clickToggleStatusButtonForUser(nameToToggle);
    }

    @When("I click on delete button for user {string}")
    public void i_click_on_delete_button_for_user(String userName) {
        // Use the stored dynamic name if it starts with the provided pattern
        String nameToDelete = (currentUserName != null && currentUserName.startsWith(userName.split(" ")[0])) 
                ? currentUserName : userName;
        logger.info("Clicking delete button for user: {}", nameToDelete);
        ExtentReportUtil.logInfo("Clicking delete button for user: " + nameToDelete);
        getUserManagementPage().clickDeleteButtonForUser(nameToDelete);
    }

    @When("I click on filter button {string}")
    public void i_click_on_filter_button(String filterName) {
        logger.info("Clicking filter button: {}", filterName);
        ExtentReportUtil.logInfo("Clicking filter button: " + filterName);
        getUserManagementPage().clickFilterButton(filterName);
    }

    @Then("I should see only users with role {string}")
    public void i_should_see_only_users_with_role(String role) {
        logger.info("Verifying only users with role '{}' are displayed", role);
        ExtentReportUtil.logInfo("Verifying only users with role '" + role + "' are displayed");
        
        boolean correctRoles = getUserManagementPage().areOnlyUsersWithRoleDisplayed(role);
        Assert.assertTrue(correctRoles, "Not all displayed users have role: " + role);
        
        logger.info("Only users with role '{}' are displayed", role);
        ExtentReportUtil.logPass("Only users with role '" + role + "' are displayed");
    }

    @Then("I should see users of all roles")
    public void i_should_see_users_of_all_roles() {
        logger.info("Verifying users of all roles are displayed");
        ExtentReportUtil.logInfo("Verifying users of all roles are displayed");
        
        boolean allRoles = getUserManagementPage().areUsersOfAllRolesDisplayed();
        Assert.assertTrue(allRoles, "Users of all roles should be displayed");
        
        logger.info("Users of all roles are displayed");
        ExtentReportUtil.logPass("Users of all roles are displayed");
    }
}

