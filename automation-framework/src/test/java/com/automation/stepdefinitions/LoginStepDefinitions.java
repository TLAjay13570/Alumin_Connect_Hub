package com.automation.stepdefinitions;

import com.automation.pages.LoginPage;
import com.automation.utils.ExtentReportUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Step Definitions for Login feature
 */
public class LoginStepDefinitions {
    private static final Logger logger = LogManager.getLogger(LoginStepDefinitions.class);
    private LoginPage loginPage;

    public LoginStepDefinitions() {
        this.loginPage = new LoginPage();
    }

    @Given("I navigate to the login page")
    public void i_navigate_to_the_login_page() {
        logger.info("Navigating to login page");
        ExtentReportUtil.logInfo("Navigating to login page");
        loginPage.navigateToLoginPage();
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        logger.info("User is on login page");
        ExtentReportUtil.logInfo("User is on login page");
        loginPage.navigateToLoginPage();
    }

    @When("I enter username {string}")
    public void i_enter_username(String username) {
        logger.info("Entering username: {}", username);
        ExtentReportUtil.logInfo("Entering username: " + username);
        loginPage.enterUsername(username);
    }

    @When("I enter password {string}")
    public void i_enter_password(String password) {
        logger.info("Entering password");
        ExtentReportUtil.logInfo("Entering password");
        loginPage.enterPassword(password);
    }

    @When("I click on the login button")
    public void i_click_on_the_login_button() {
        logger.info("Clicking login button");
        ExtentReportUtil.logInfo("Clicking login button");
        loginPage.clickLoginButton();
    }

    @When("I login with username {string} and password {string}")
    public void i_login_with_username_and_password(String username, String password) {
        logger.info("Logging in with username: {}", username);
        ExtentReportUtil.logInfo("Logging in with username: " + username);
        loginPage.login(username, password);
    }

    @When("I login with valid credentials from config")
    public void i_login_with_valid_credentials_from_config() {
        logger.info("Logging in with config credentials");
        ExtentReportUtil.logInfo("Logging in with config credentials");
        loginPage.loginWithConfigCredentials();
    }

    @Then("I should be logged in successfully")
    public void i_should_be_logged_in_successfully() {
        logger.info("Verifying successful login");
        ExtentReportUtil.logInfo("Verifying successful login");
        boolean isDashboardDisplayed = loginPage.isDashboardDisplayed();
        Assert.assertTrue(isDashboardDisplayed, "Dashboard is not displayed. Login may have failed.");
        ExtentReportUtil.logPass("Login successful - Dashboard is displayed");
    }

    @Then("I should see the dashboard")
    public void i_should_see_the_dashboard() {
        logger.info("Verifying dashboard is displayed");
        ExtentReportUtil.logInfo("Verifying dashboard is displayed");
        boolean isDashboardDisplayed = loginPage.isDashboardDisplayed();
        Assert.assertTrue(isDashboardDisplayed, "Dashboard is not displayed");
        ExtentReportUtil.logPass("Dashboard is displayed");
    }

    @Then("I should see an error message")
    public void i_should_see_an_error_message() {
        logger.info("Verifying error message is displayed");
        ExtentReportUtil.logInfo("Verifying error message is displayed");
        boolean isErrorMessageDisplayed = loginPage.isErrorMessageDisplayed();
        Assert.assertTrue(isErrorMessageDisplayed, "Error message is not displayed");
        String errorMessage = loginPage.getErrorMessage();
        ExtentReportUtil.logInfo("Error message: " + errorMessage);
    }

    @Then("I should see error message {string}")
    public void i_should_see_error_message(String expectedErrorMessage) {
        logger.info("Verifying error message: {}", expectedErrorMessage);
        ExtentReportUtil.logInfo("Verifying error message: " + expectedErrorMessage);
        boolean isErrorMessageDisplayed = loginPage.isErrorMessageDisplayed();
        Assert.assertTrue(isErrorMessageDisplayed, "Error message is not displayed");
        String actualErrorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(actualErrorMessage.contains(expectedErrorMessage),
                "Expected error message: " + expectedErrorMessage + ", but got: " + actualErrorMessage);
        ExtentReportUtil.logPass("Error message verified: " + actualErrorMessage);
    }

    @Then("I should remain on the login page")
    public void i_should_remain_on_the_login_page() {
        logger.info("Verifying user is still on login page");
        ExtentReportUtil.logInfo("Verifying user is still on login page");
        String currentUrl = loginPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("login") || currentUrl.contains("auth"),
                "User is not on login page. Current URL: " + currentUrl);
        ExtentReportUtil.logPass("User is on login page");
    }

    @Then("I should be able to logout")
    public void i_should_be_able_to_logout() {
        logger.info("Logging out");
        ExtentReportUtil.logInfo("Logging out");
        loginPage.logout();
        ExtentReportUtil.logPass("Logout successful");
    }
}

