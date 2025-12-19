package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Login Page Object Model class
 * Contains all elements and methods related to login page
 */
public class LoginPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    // Page Elements using PageFactory
    @FindBy(id = "email")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement loginButton;

    @FindBy(xpath = "//div[contains(@class,'destructive')]//p | //div[contains(text(),'Login failed')] | //div[contains(text(),'credentials')]")
    private WebElement errorMessage;

    @FindBy(xpath = "//h1[contains(text(),'Dashboard')] | //h1[contains(text(),'Super Admin')] | //h1[contains(text(),'Admin')] | //nav[contains(@class,'sidebar')] | //div[contains(@class,'DesktopNav')]")
    private WebElement dashboardHeader;

    @FindBy(xpath = "//button[contains(@class,'user')] | //div[contains(@class,'user')] | //span[contains(@class,'user')]")
    private WebElement userDropdown;

    @FindBy(xpath = "//button[contains(text(),'Logout')] | //a[contains(text(),'Logout')] | //div[contains(text(),'Logout')]")
    private WebElement logoutLink;

    /**
     * Navigates to login page
     */
    public void navigateToLoginPage() {
        logger.info("Navigating to login page");
        navigateTo(ConfigReader.getLoginUrl());
    }

    /**
     * Enters username
     *
     * @param username Username to enter
     */
    public void enterUsername(String username) {
        logger.info("Entering username: {}", username);
        sendKeys(usernameField, username);
    }

    /**
     * Enters password
     *
     * @param password Password to enter
     */
    public void enterPassword(String password) {
        logger.info("Entering password");
        sendKeys(passwordField, password);
    }

    /**
     * Clicks login button
     */
    public void clickLoginButton() {
        logger.info("Clicking login button");
        click(loginButton);
    }

    /**
     * Performs login with username and password
     *
     * @param username Username
     * @param password Password
     */
    public void login(String username, String password) {
        logger.info("Performing login with username: {}", username);
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        
        // Wait a moment for login to process
        try {
            WebDriverWaitUtil.staticWait(2);
        } catch (Exception e) {
            logger.debug("Wait interrupted: {}", e.getMessage());
        }
    }

    /**
     * Performs login with credentials from config
     */
    public void loginWithConfigCredentials() {
        logger.info("Performing login with config credentials");
        login(ConfigReader.getUsername(), ConfigReader.getPassword());
    }

    /**
     * Gets error message text
     *
     * @return Error message text
     */
    public String getErrorMessage() {
        logger.debug("Getting error message");
        return getText(errorMessage);
    }

    /**
     * Checks if error message is displayed
     *
     * @return true if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        logger.debug("Checking if error message is displayed");
        return isElementDisplayed(errorMessage);
    }

    /**
     * Checks if dashboard is displayed (login successful)
     *
     * @return true if dashboard is displayed
     */
    public boolean isDashboardDisplayed() {
        logger.debug("Checking if dashboard is displayed");
        try {
            // Wait for URL to change from login page (wait up to explicit wait time)
            try {
                WebDriverWaitUtil.waitForUrlToContain("/admin");
                logger.debug("URL contains /admin - login successful");
                return true;
            } catch (Exception e1) {
                try {
                    WebDriverWaitUtil.waitForUrlToContain("/dashboard");
                    logger.debug("URL contains /dashboard - login successful");
                    return true;
                } catch (Exception e2) {
                    try {
                        WebDriverWaitUtil.waitForUrlToContain("/superadmin");
                        logger.debug("URL contains /superadmin - login successful");
                        return true;
                    } catch (Exception e3) {
                        // Check current URL
                        String currentUrl = getCurrentUrl();
                        boolean urlChanged = !currentUrl.contains("/login");
                        logger.debug("Current URL: {}, URL changed: {}", currentUrl, urlChanged);
                        
                        if (urlChanged) {
                            return true;
                        }
                        
                        // Try to find dashboard element as fallback
                        boolean elementFound = isElementDisplayed(dashboardHeader);
                        logger.debug("Dashboard element found: {}", elementFound);
                        return elementFound;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error checking dashboard: {}", e.getMessage());
            // Final fallback: check if URL changed
            try {
                String currentUrl = getCurrentUrl();
                return !currentUrl.contains("/login");
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * Performs logout
     */
    public void logout() {
        logger.info("Performing logout");
        click(userDropdown);
        click(logoutLink);
    }

    /**
     * Gets page title
     *
     * @return Page title
     */
    public String getLoginPageTitle() {
        return getPageTitle();
    }
}

