package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
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
     * Performs login as Super Admin using config credentials
     */
    public void loginAsSuperAdmin() {
        String username = ConfigReader.getSuperAdminUsername();
        String password = ConfigReader.getSuperAdminPassword();
        
        logger.info("Performing super admin login with username: {}", username);
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        
        // Wait for page to load after login
        WebDriverWaitUtil.staticWait(3);
    }

    /**
     * Checks if specific text is displayed on the page
     *
     * @param text Text to search for
     * @return true if text is found on page
     */
    public boolean isTextDisplayedOnPage(String text) {
        logger.info("Checking if text '{}' is displayed on page", text);
        try {
            By textLocator = By.xpath("//*[contains(text(),'" + text + "')]");
            WebElement element = WebDriverWaitUtil.waitForElementVisible(textLocator);
            boolean isDisplayed = element != null && element.isDisplayed();
            logger.info("Text '{}' found: {}", text, isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.error("Text '{}' not found on page: {}", text, e.getMessage());
            return false;
        }
    }

    /**
     * Checks if login was successful by verifying URL change or dashboard elements
     *
     * @return true if login was successful
     */
    public boolean isLoginSuccessful() {
        logger.info("Checking if login was successful");
        try {
            // Wait a bit more for login to complete (API call + redirect)
            int maxRetries = 5;
            int retryCount = 0;
            
            while (retryCount < maxRetries) {
                WebDriverWaitUtil.staticWait(2);
                String currentUrl = driver.getCurrentUrl();
                
                // Check if we've navigated away from login page
                boolean urlChanged = !currentUrl.contains("/login");
                
                if (urlChanged) {
                    logger.info("Login success check - URL changed to: {}", currentUrl);
                    return true;
                }
                
                // Check for any error messages
                boolean hasError = false;
                try {
                    By errorLocator = By.xpath("//*[contains(@class,'error') or contains(@class,'alert-danger') or contains(text(),'Invalid') or contains(text(),'incorrect')]");
                    java.util.List<WebElement> errors = driver.findElements(errorLocator);
                    hasError = !errors.isEmpty() && errors.stream().anyMatch(e -> e.isDisplayed());
                } catch (Exception e) {
                    // No error found, that's good
                }
                
                if (hasError) {
                    logger.error("Login failed - error message displayed on page");
                    return false;
                }
                
                retryCount++;
                logger.debug("Login check retry {} of {} - still on login page", retryCount, maxRetries);
            }
            
            // Final check
            String currentUrl = driver.getCurrentUrl();
            boolean urlChanged = !currentUrl.contains("/login");
            logger.info("Login success check final - URL changed: {}, Current URL: {}", urlChanged, currentUrl);
            return urlChanged;
        } catch (Exception e) {
            logger.error("Error checking login success: {}", e.getMessage());
            return false;
        }
    }
}
