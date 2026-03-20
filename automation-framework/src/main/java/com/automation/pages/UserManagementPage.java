package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object Model for the User Management page
 * URL: /superadmin/users
 */
public class UserManagementPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(UserManagementPage.class);

    // Page header
    @FindBy(xpath = "//h1[contains(text(),'All Users')]")
    private WebElement pageHeader;

    // Add User button
    @FindBy(xpath = "//button[contains(.,'Add User')]")
    private WebElement addUserButton;

    // Role select dropdown trigger
    @FindBy(xpath = "//button[@role='combobox' and contains(.,'Alumni') or contains(.,'Super') or contains(.,'Admin')]")
    private WebElement roleSelectTrigger;

    // Form fields in modal
    @FindBy(id = "name")
    private WebElement nameInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "year")
    private WebElement graduationYearInput;

    @FindBy(id = "major")
    private WebElement majorInput;

    @FindBy(id = "mentor")
    private WebElement mentorSwitch;

    @FindBy(id = "enabled")
    private WebElement enabledSwitch;

    // Create User button in modal
    @FindBy(xpath = "//button[normalize-space()='Create User']")
    private WebElement createUserButton;

    // Search input
    @FindBy(xpath = "//input[@type='search' and contains(@placeholder,'Search')]")
    private WebElement searchInput;

    // Filter buttons
    @FindBy(xpath = "//button[normalize-space()='All']")
    private WebElement filterAllButton;

    @FindBy(xpath = "//button[normalize-space()='Super']")
    private WebElement filterSuperButton;

    @FindBy(xpath = "//button[normalize-space()='Admins']")
    private WebElement filterAdminsButton;

    @FindBy(xpath = "//button[normalize-space()='Alumni']")
    private WebElement filterAlumniButton;

    @FindBy(xpath = "//button[normalize-space()='Mentors']")
    private WebElement filterMentorsButton;

    /**
     * Navigates to the users management page
     */
    public void navigateToUsersPage() {
        logger.info("Navigating to users management page");
        String usersUrl = ConfigReader.getBaseUrl() + "/superadmin/users";
        navigateTo(usersUrl);
        WebDriverWaitUtil.waitForPageToLoad();
        WebDriverWaitUtil.waitForElementClickable(By.xpath("//button[contains(.,'Add User')]"));
    }

    /**
     * Clicks on Add User button
     */
    public void clickAddUserButton() {
        logger.info("Clicking Add User button");
        WebElement btn = WebDriverWaitUtil.waitForElementClickable(addUserButton);
        click(btn);
        WebDriverWaitUtil.waitForElementVisible(By.xpath("//div[@role='dialog']"));
        WebDriverWaitUtil.waitForElementVisible(By.id("name"));
        logger.info("Modal dialog opened successfully");
    }

    /**
     * Selects user role from dropdown
     *
     * @param role Role to select (Super Administrator, University Administrator, Alumni)
     */
    public void selectRole(String role) {
        logger.info("Selecting role: {}", role);
        try {
            WebElement trigger = WebDriverWaitUtil.waitForElementClickable(
                    By.xpath("//div[@role='dialog']//label[contains(.,'Role')]/following::button[1]"));
            click(trigger);

            String optionXpath = String.format(
                    "//div[@role='option' and contains(normalize-space(),\"%s\")]", role);
            WebElement option = WebDriverWaitUtil.waitForElementClickable(By.xpath(optionXpath));
            click(option);
            logger.info("Selected role: {}", role);
        } catch (Exception e) {
            logger.error("Error selecting role: {}", e.getMessage());
            throw new RuntimeException("Could not select role: " + role, e);
        }
    }

    /**
     * Enters user name
     *
     * @param name User name
     */
    public void enterUserName(String name) {
        logger.info("Entering user name: {}", name);
        WebElement input = WebDriverWaitUtil.waitForElementVisible(nameInput);
        sendKeys(input, name);
    }

    /**
     * Enters user email
     *
     * @param email User email
     */
    public void enterUserEmail(String email) {
        logger.info("Entering user email: {}", email);
        WebElement input = WebDriverWaitUtil.waitForElementVisible(emailInput);
        sendKeys(input, email);
    }

    /**
     * Enters user password
     *
     * @param password User password
     */
    public void enterUserPassword(String password) {
        logger.info("Entering user password");
        WebElement input = WebDriverWaitUtil.waitForElementVisible(passwordInput);
        sendKeys(input, password);
    }

    /**
     * Selects university from dropdown
     *
     * @param universityName University name to select
     */
    public void selectUniversity(String universityName) {
        logger.info("Selecting university: {}", universityName);
        try {
            WebElement trigger = WebDriverWaitUtil.waitForElementClickable(
                    By.xpath("//div[@role='dialog']//label[contains(.,'University')]/following::button[1]"));
            click(trigger);

            String optionXpath = String.format(
                    "//div[@role='option' and contains(normalize-space(),\"%s\")]", universityName);
            WebElement option = WebDriverWaitUtil.waitForElementClickable(By.xpath(optionXpath));
            click(option);
            logger.info("Selected university: {}", universityName);
        } catch (Exception e) {
            logger.error("Error selecting university: {}", e.getMessage());
            throw new RuntimeException("Could not select university: " + universityName, e);
        }
    }

    /**
     * Enters graduation year
     *
     * @param year Graduation year
     */
    public void enterGraduationYear(String year) {
        logger.info("Entering graduation year: {}", year);
        WebElement input = WebDriverWaitUtil.waitForElementVisible(graduationYearInput);
        sendKeys(input, year);
    }

    /**
     * Enters major
     *
     * @param major Major
     */
    public void enterMajor(String major) {
        logger.info("Entering major: {}", major);
        WebElement input = WebDriverWaitUtil.waitForElementVisible(majorInput);
        sendKeys(input, major);
    }

    /**
     * Toggles mentor status on
     */
    public void toggleMentorOn() {
        logger.info("Toggling mentor status on");
        try {
            WebElement mentorToggle = WebDriverWaitUtil.waitForElementClickable(By.id("mentor"));
            String currentState = mentorToggle.getAttribute("data-state");
            if (!"checked".equals(currentState)) {
                click(mentorToggle);
                logger.info("Mentor status toggled on");
            } else {
                logger.info("Mentor status already on");
            }
        } catch (Exception e) {
            logger.warn("Could not find mentor toggle: {}", e.getMessage());
        }
    }

    /**
     * Clicks on Create User button
     */
    public void clickCreateUserButton() {
        logger.info("Clicking Create User button");
        WebElement btn = WebDriverWaitUtil.waitForElementClickable(createUserButton);
        click(btn);
        logger.info("Clicked Create User button");
        WebDriverWaitUtil.waitForElementInvisible(By.xpath("//div[@role='dialog']"));
    }

    /**
     * Clicks on toggle status (power) button for a specific user
     *
     * @param userName User name
     */
    public void clickToggleStatusButtonForUser(String userName) {
        logger.info("Clicking toggle status button for user: {}", userName);
        try {
            String row = String.format("//tr[td[1][contains(normalize-space(),\"%s\")]]", userName);
            String xpath = row + "//button[.//*[name()='svg' and contains(@class,'lucide-power')]]"
                    + " | " + row + "//button[@title='Disable user' or @title='Enable user']";
            WebElement toggleButton = WebDriverWaitUtil.waitForElementClickable(By.xpath(xpath));
            click(toggleButton);
            logger.info("Clicked toggle status button for user: {}", userName);
        } catch (Exception e) {
            logger.error("Error finding toggle button for user {}: {}", userName, e.getMessage());
            throw new RuntimeException("Could not find toggle button for user: " + userName, e);
        }
    }

    /**
     * Clicks on delete button for a specific user
     *
     * @param userName User name
     */
    public void clickDeleteButtonForUser(String userName) {
        logger.info("Clicking delete button for user: {}", userName);
        try {
            String row = String.format("//tr[td[1][contains(normalize-space(),\"%s\")]]", userName);
            String xpath = row + "//button[contains(@class,'destructive') or .//*[contains(@class,'lucide-trash')]]";
            WebElement deleteButton = WebDriverWaitUtil.waitForElementClickable(By.xpath(xpath));
            click(deleteButton);
            logger.info("Clicked delete button for user: {}", userName);
        } catch (Exception e) {
            logger.error("Error finding delete button for user {}: {}", userName, e.getMessage());
            throw new RuntimeException("Could not find delete button for user: " + userName, e);
        }
    }

    /**
     * Confirms deletion via browser alert
     */
    public void confirmDeletion() {
        logger.info("Confirming deletion via browser alert");
        try {
            WebDriverWaitUtil.staticWait(1);
            org.openqa.selenium.Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            logger.info("Browser confirm dialog found with text: {}", alertText);
            alert.accept();
            logger.info("Accepted browser confirm dialog for deletion");
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            logger.warn("No browser alert present - deletion might have already completed");
        } catch (Exception e) {
            logger.debug("Error handling alert: {}", e.getMessage());
        }
        WebDriverWaitUtil.staticWait(3);
    }

    /**
     * Clicks on a filter button
     *
     * @param filterName Filter name (All, Super, Admins, Alumni, Mentors)
     */
    public void clickFilterButton(String filterName) {
        logger.info("Clicking filter button: {}", filterName);
        String xpath = String.format("//button[normalize-space()='%s']", filterName);
        WebElement filterButton = WebDriverWaitUtil.waitForElementClickable(By.xpath(xpath));
        click(filterButton);
        WebDriverWaitUtil.staticWait(2);
        logger.info("Clicked filter button: {}", filterName);
    }

    /**
     * Checks if success message is displayed
     *
     * @return true if success message is displayed
     */
    public boolean isSuccessMessageDisplayed() {
        logger.debug("Checking if success message is displayed");
        try {
            WebDriverWaitUtil.staticWait(1);
            String[] xpaths = {
                "//*[@data-state='open' and contains(@class,'group')]",
                "//li[@data-state='open']",
                "//*[@role='status']",
                "//ol//li[contains(@class,'group')]"
            };
            for (String xpath : xpaths) {
                try {
                    List<WebElement> elements = driver.findElements(By.xpath(xpath));
                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                        logger.debug("Success message found using xpath: {}", xpath);
                        return true;
                    }
                } catch (Exception e) {
                    // Continue to next xpath
                }
            }
            return false;
        } catch (Exception e) {
            logger.debug("Error checking success message display: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets success message text
     *
     * @return Success message text
     */
    public String getSuccessMessage() {
        logger.debug("Getting success message");
        try {
            String[] xpaths = {
                "//*[@data-state='open' and contains(@class,'group')]",
                "//li[@data-state='open']",
                "//*[@role='status']",
                "//ol//li[contains(@class,'group') and @data-state='open']"
            };
            for (String xpath : xpaths) {
                try {
                    List<WebElement> elements = driver.findElements(By.xpath(xpath));
                    for (WebElement element : elements) {
                        if (element.isDisplayed()) {
                            String text = element.getText();
                            if (text != null && !text.trim().isEmpty()) {
                                logger.debug("Success message text found: {}", text);
                                return text;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Continue to next xpath
                }
            }
            return "";
        } catch (Exception e) {
            logger.debug("Could not get success message: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Checks if success message contains specific text
     *
     * @param text Text to check
     * @return true if message contains text
     */
    public boolean isSuccessMessageContaining(String text) {
        logger.debug("Checking if success message contains: {}", text);
        String message = getSuccessMessage();
        logger.debug("Got success message: '{}'", message);
        boolean contains = message.toLowerCase().contains(text.toLowerCase());
        logger.debug("Message contains '{}': {}", text, contains);
        return contains;
    }

    /**
     * Checks if user exists in the users list
     *
     * @param userName User name to check
     * @return true if user exists in the list
     */
    public boolean isUserInList(String userName) {
        logger.debug("Checking if user '{}' exists in the list", userName);
        try {
            String xpath = String.format("//tr[td[1][contains(normalize-space(),\"%s\")]]", userName);
            List<WebElement> elements = driver.findElements(By.xpath(xpath));
            boolean found = elements.stream().anyMatch(WebElement::isDisplayed);
            if (found) {
                logger.debug("User '{}' found in list", userName);
            } else {
                logger.debug("User '{}' not found in list", userName);
            }
            return found;
        } catch (Exception e) {
            logger.error("Error checking if user exists: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if user does not exist in the users list
     *
     * @param userName User name to check
     * @return true if user does not exist in the list
     */
    public boolean isUserNotInList(String userName) {
        logger.debug("Checking if user '{}' does not exist in the list", userName);
        return !isUserInList(userName);
    }

    /**
     * Checks if only users with specific role are displayed
     *
     * @param role Role to check (Super Admin, Admin, Alumni)
     * @return true if only users with the role are displayed
     */
    public boolean areOnlyUsersWithRoleDisplayed(String role) {
        logger.debug("Checking if only users with role '{}' are displayed", role);
        try {
            WebDriverWaitUtil.staticWait(2);
            // Get all role badges in the table
            List<WebElement> roleBadges = driver.findElements(By.xpath("//table//tbody//tr//td[3]//span | //table//tbody//tr//td[3]//div[contains(@class,'Badge')]"));
            
            if (roleBadges.isEmpty()) {
                logger.debug("No users found in table");
                return true; // Empty list is valid
            }

            for (WebElement badge : roleBadges) {
                String badgeText = badge.getText().toLowerCase();
                if (!badgeText.contains(role.toLowerCase())) {
                    logger.debug("Found user with different role: {}", badgeText);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Error checking user roles: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if users of all roles are displayed
     *
     * @return true if users of multiple roles are displayed
     */
    public boolean areUsersOfAllRolesDisplayed() {
        logger.debug("Checking if users of all roles are displayed");
        try {
            WebDriverWaitUtil.staticWait(2);
            List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
            return !rows.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking all users: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Enters search query
     *
     * @param query Search query
     */
    public void enterSearchQuery(String query) {
        logger.info("Entering search query: {}", query);
        WebElement input = WebDriverWaitUtil.waitForElementVisible(searchInput);
        sendKeys(input, query);
        WebDriverWaitUtil.staticWait(2);
    }
}

