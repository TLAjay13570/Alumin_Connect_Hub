package com.automation.stepdefinitions;

import com.automation.pages.LoginPage;
import com.automation.utils.ExtentReportUtil;
import com.automation.utils.WebDriverWaitUtil;
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

    @When("I login as {string} using credentials from config")
    public void i_login_as_user_using_credentials_from_config(String userType) {
        logger.info("Logging in as {} using config credentials", userType);
        ExtentReportUtil.logInfo("Logging in as " + userType + " using config credentials");
        
        if (userType.equalsIgnoreCase("super admin") || userType.equalsIgnoreCase("superadmin")) {
            loginPage.loginAsSuperAdmin();
        } else if (userType.equalsIgnoreCase("admin")) {
            // Login with admin credentials from config
            loginPage.loginAsAdmin();
        } else {
            logger.warn("Unknown user type: {}, attempting super admin login", userType);
            loginPage.loginAsSuperAdmin();
        }
    }

    @Then("I should be logged in successfully")
    public void i_should_be_logged_in_successfully() {
        logger.info("Verifying successful login");
        ExtentReportUtil.logInfo("Verifying successful login");
        
        // Wait for login to complete and page to load
        WebDriverWaitUtil.staticWait(3);
        
        // Verify we're no longer on login page (URL should change)
        boolean loginSuccess = loginPage.isLoginSuccessful();
        Assert.assertTrue(loginSuccess, "Login was not successful - still on login page or error displayed");
        
        logger.info("Login verified successfully");
        ExtentReportUtil.logPass("User logged in successfully");
    }

    @Then("I should see {string} text on the page")
    public void i_should_see_text_on_the_page(String expectedText) {
        logger.info("Verifying '{}' text is displayed on page", expectedText);
        ExtentReportUtil.logInfo("Verifying '" + expectedText + "' text is displayed");
        
        boolean isTextDisplayed = loginPage.isTextDisplayedOnPage(expectedText);
        Assert.assertTrue(isTextDisplayed, "Expected text '" + expectedText + "' is not displayed on the page.");
        
        logger.info("Text '{}' verified successfully", expectedText);
        ExtentReportUtil.logPass("Text '" + expectedText + "' is displayed on the page");
    }
}
