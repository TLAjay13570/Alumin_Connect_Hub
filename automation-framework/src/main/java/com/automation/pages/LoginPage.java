package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Login Page Object Model class
 * Contains all elements and methods related to login page
 */
public class LoginPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement loginButton;

    private WebElement resolveUsernameField() {
        List<By> candidates = List.of(
                By.id("email"),
                By.id("emailOrUsername"),
                By.name("email"),
                By.cssSelector("input[autocomplete='username']"),
                By.xpath("//form//input[@type='email']"),
                By.xpath("//form//input[@type='text' or not(@type)][not(@type='password')][1]")
        );
        return WebDriverWaitUtil.waitForAnyElementVisible(candidates);
    }

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
        WebElement field = resolveUsernameField();
        sendKeys(field, username);
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
        waitForLoginNavigation();
    }

    /**
     * Performs login as Admin using config credentials
     */
    public void loginAsAdmin() {
        String username = ConfigReader.getAdminUsername();
        String password = ConfigReader.getAdminPassword();

        logger.info("Performing admin login with username: {}", username);
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        waitForLoginNavigation();
    }

    /**
     * Performs login as a university alumni user using config credentials.
     */
    public void loginAsAlumni() {
        String username = ConfigReader.getAlumniUsername();
        String password = ConfigReader.getAlumniPassword();
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException("alumni.username and alumni.password must be set in config.properties");
        }
        logger.info("Performing alumni login with username: {}", username);
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        waitForLoginNavigation();
    }

    /**
     * Clears session cookies and opens the login page (switch users in the same browser).
     */
    public void logoutClearSession() {
        logger.info("Clearing cookies and navigating to login");
        driver.manage().deleteAllCookies();
        navigateToLoginPage();
    }

    /**
     * Waits until we leave the login route or an error is shown.
     */
    private void waitForLoginNavigation() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        wait.until(d -> !d.getCurrentUrl().contains("/login") || isLoginErrorVisible());
        if (!driver.getCurrentUrl().contains("/login")) {
            WebDriverWaitUtil.waitForPageToLoad();
        }
    }

    private boolean isLoginErrorVisible() {
        try {
            By errorLocator = By.xpath(
                    "//div[contains(@class,'destructive/10') or contains(@class,'border-destructive')]"
                            + "//*[contains(@class,'text-destructive')][string-length(normalize-space())>3]");
            List<WebElement> errors = driver.findElements(errorLocator);
            return errors.stream().anyMatch(e -> {
                try {
                    return e.isDisplayed() && e.getText() != null && !e.getText().isBlank();
                } catch (Exception ex) {
                    return false;
                }
            });
        } catch (Exception e) {
            return false;
        }
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
            By textLocator = By.xpath("//*[contains(normalize-space(),'" + text + "')]");
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
            if (isLoginErrorVisible()) {
                logger.error("Login failed - error message displayed on page");
                return false;
            }
            boolean navigatedAway = WebDriverWaitUtil.waitForUrlToNotContain("/login", ConfigReader.getExplicitWait());
            if (navigatedAway) {
                logger.info("Login success check - URL changed to: {}", driver.getCurrentUrl());
                return true;
            }
            logger.warn("Still on login URL after wait");
            return false;
        } catch (Exception e) {
            logger.error("Error checking login success: {}", e.getMessage());
            return false;
        }
    }
}
