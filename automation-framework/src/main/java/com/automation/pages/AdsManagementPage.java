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
 * Page Object Model for the Ads Management page.
 * Handles all ad-related operations: create, edit, toggle visibility, delete.
 */
public class AdsManagementPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(AdsManagementPage.class);

    // Page Elements
    @FindBy(xpath = "//button[contains(.,'Create Ad')]")
    private WebElement createAdButton;

    @FindBy(id = "title")
    private WebElement titleInput;

    @FindBy(id = "description")
    private WebElement descriptionInput;

    @FindBy(id = "mediaUrl")
    private WebElement mediaUrlInput;

    @FindBy(id = "linkUrl")
    private WebElement linkUrlInput;

    @FindBy(id = "targetAll")
    private WebElement targetAllCheckbox;

    // Toast notification
    @FindBy(xpath = "//div[contains(@class,'radix-toast-viewport')]//div[@role='status']")
    private WebElement toastNotification;

    // Modal buttons
    @FindBy(xpath = "//div[@role='dialog']//button[contains(.,'Create Ad')]")
    private WebElement modalCreateButton;

    @FindBy(xpath = "//div[@role='dialog']//button[contains(.,'Update Ad')]")
    private WebElement modalUpdateButton;

    public AdsManagementPage() {
        super();
    }

    /**
     * Navigates to the ads management page
     */
    public void navigateToAdsPage() {
        logger.info("Navigating to ads management page");
        String adsUrl = ConfigReader.getBaseUrl() + "/superadmin/ads";
        navigateTo(adsUrl);
        WebDriverWaitUtil.staticWait(3);
    }

    /**
     * Clicks on Create Ad button to open the modal
     */
    public void clickCreateAdButton() {
        logger.info("Clicking Create Ad button");
        // Wait for page to fully load
        WebDriverWaitUtil.staticWait(3);
        
        // Try clicking the button multiple times if modal doesn't open
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                WebElement btn = WebDriverWaitUtil.waitForElementClickable(createAdButton);
                click(btn);
                WebDriverWaitUtil.staticWait(2);
                // Wait for modal to open
                WebDriverWaitUtil.waitForElementVisible(By.id("title"));
                logger.info("Create Ad modal opened");
                return;
            } catch (Exception e) {
                logger.warn("Attempt {} to open Create Ad modal failed, retrying...", i + 1);
                WebDriverWaitUtil.staticWait(2);
            }
        }
        throw new RuntimeException("Failed to open Create Ad modal after " + maxRetries + " attempts");
    }

    /**
     * Enters ad title
     *
     * @param title Ad title
     */
    public void enterAdTitle(String title) {
        logger.info("Entering ad title: {}", title);
        WebElement input = WebDriverWaitUtil.waitForElementVisible(titleInput);
        input.clear();
        sendKeys(input, title);
    }

    /**
     * Enters ad description
     *
     * @param description Ad description
     */
    public void enterAdDescription(String description) {
        logger.info("Entering ad description: {}", description);
        WebElement input = WebDriverWaitUtil.waitForElementVisible(descriptionInput);
        input.clear();
        sendKeys(input, description);
    }

    /**
     * Selects media type from dropdown
     *
     * @param mediaType Media type (Image or Video)
     */
    public void selectMediaType(String mediaType) {
        logger.info("Selecting media type: {}", mediaType);
        try {
            // Find the media type select trigger inside the modal.
            // UI text/labels changed recently, so we match using generic "Media" label first and fall back to the first combobox.
            WebElement trigger;
            try {
                trigger = driver.findElement(By.xpath(
                        "//div[@role='dialog']//label[contains(.,'Media')]/following::button[@role='combobox'][1]"));
            } catch (Exception ignored) {
                trigger = driver.findElement(By.xpath("//div[@role='dialog']//button[@role='combobox'][1]"));
            }
            WebDriverWaitUtil.waitForElementClickable(trigger);
            click(trigger);
            WebDriverWaitUtil.staticWait(1);

            // Select the option - handle both div-based and aria-based option rendering
            List<By> optionLocators = List.of(
                    By.xpath("//div[@role='dialog']//div[@role='listbox']//div[@role='option' and contains(.,'" + mediaType + "')]"),
                    By.xpath("//div[@role='option' and contains(.,'" + mediaType + "')]")
            );
            WebElement option = WebDriverWaitUtil.waitForAnyElementVisible(optionLocators);
            click(option);
            logger.info("Selected media type: {}", mediaType);
            WebDriverWaitUtil.staticWait(1);
        } catch (Exception e) {
            logger.warn("Could not select media type using dropdown, may already be selected: {}", e.getMessage());
        }
    }

    /**
     * Enters media URL
     *
     * @param mediaUrl Media URL (image or video)
     */
    public void enterMediaUrl(String mediaUrl) {
        logger.info("Entering media URL: {}", mediaUrl);
        // Field id/label changed; locate the input by multiple heuristics inside the modal.
        List<By> candidates = List.of(
                By.id("mediaUrl"),
                By.id("mediaURL"),
                By.id("media_url"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Media')]/following::input[1]"),
                By.xpath("//div[@role='dialog']//input[contains(@placeholder,'Media') or contains(@placeholder,'media') or contains(@placeholder,'URL') or contains(@aria-label,'Media')][1]"),
                By.xpath("//div[@role='dialog']//input[@type='url' or contains(@type,'url')][1]"),
                By.xpath("//div[@role='dialog']//input[contains(@id,'media') or contains(@name,'media')][1]")
        );
        WebElement input = WebDriverWaitUtil.waitForAnyElementVisible(candidates);
        input.clear();
        sendKeys(input, mediaUrl);
    }

    /**
     * Enters link URL
     *
     * @param linkUrl Link URL for "Learn More"
     */
    public void enterLinkUrl(String linkUrl) {
        logger.info("Entering link URL: {}", linkUrl);
        List<By> candidates = List.of(
                By.id("linkUrl"),
                By.id("linkURL"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Link') or contains(.,'Learn')]/following::input[1]"),
                By.xpath("//div[@role='dialog']//input[contains(@placeholder,'Link') or contains(@placeholder,'Learn') or contains(@placeholder,'https') or contains(@placeholder,'URL')][1]"),
                By.xpath("//div[@role='dialog']//input[@type='url' or contains(@type,'url')][2]")
        );
        WebElement input = WebDriverWaitUtil.waitForAnyElementVisible(candidates);
        input.clear();
        sendKeys(input, linkUrl);
    }

    /**
     * Selects ad placement from dropdown
     *
     * @param placement Placement (Left Sidebar, Right Sidebar, In Feed)
     */
    public void selectAdPlacement(String placement) {
        logger.info("Selecting ad placement: {}", placement);
        try {
            // Find the placement select trigger - the parent div contains both label and button
            WebElement trigger = driver.findElement(By.xpath("//label[contains(text(),'Ad Placement')]/..//button[@role='combobox']"));
            WebDriverWaitUtil.waitForElementClickable(trigger);
            click(trigger);
            WebDriverWaitUtil.staticWait(1);

            // Select the option - handle partial matching for placement text
            String optionXpath;
            if (placement.equalsIgnoreCase("In Feed")) {
                optionXpath = "//div[@role='option' and contains(.,'In Feed')]";
            } else if (placement.equalsIgnoreCase("Left Sidebar")) {
                optionXpath = "//div[@role='option' and contains(.,'Left Sidebar')]";
            } else if (placement.equalsIgnoreCase("Right Sidebar")) {
                optionXpath = "//div[@role='option' and contains(.,'Right Sidebar')]";
            } else {
                optionXpath = String.format("//div[@role='option' and contains(.,'%s')]", placement);
            }
            
            WebElement option = WebDriverWaitUtil.waitForElementClickable(By.xpath(optionXpath));
            click(option);
            logger.info("Selected ad placement: {}", placement);
            WebDriverWaitUtil.staticWait(1);
        } catch (Exception e) {
            logger.warn("Could not select ad placement: {}", e.getMessage());
        }
    }

    /**
     * Checks the "Show to all universities" checkbox
     */
    public void checkTargetAllUniversities() {
        logger.info("Checking 'Show to all universities' checkbox");
        try {
            WebElement checkbox = driver.findElement(By.id("targetAll"));
            String currentState = checkbox.getAttribute("data-state");
            if (!"checked".equals(currentState)) {
                click(checkbox);
                logger.info("Checkbox checked");
            } else {
                logger.info("Checkbox already checked");
            }
        } catch (Exception e) {
            logger.warn("Could not find targetAll checkbox: {}", e.getMessage());
        }
    }

    /**
     * Unchecks the "Show to all universities" checkbox
     */
    public void uncheckTargetAllUniversities() {
        logger.info("Unchecking 'Show to all universities' checkbox");
        try {
            WebElement checkbox = driver.findElement(By.id("targetAll"));
            String currentState = checkbox.getAttribute("data-state");
            if ("checked".equals(currentState)) {
                click(checkbox);
                logger.info("Checkbox unchecked");
                WebDriverWaitUtil.staticWait(1);
            } else {
                logger.info("Checkbox already unchecked");
            }
        } catch (Exception e) {
            logger.warn("Could not find targetAll checkbox: {}", e.getMessage());
        }
    }

    /**
     * Selects a specific university for targeting
     *
     * @param universityName University name to select
     */
    public void selectTargetUniversity(String universityName) {
        logger.info("Selecting target university: {}", universityName);
        try {
            // Find the university checkbox by label
            String xpath = String.format("//label[contains(text(),'%s')]/preceding-sibling::button[@role='checkbox']", universityName);
            WebElement checkbox = WebDriverWaitUtil.waitForElementClickable(By.xpath(xpath));
            String currentState = checkbox.getAttribute("data-state");
            if (!"checked".equals(currentState)) {
                click(checkbox);
                logger.info("Selected university: {}", universityName);
            } else {
                logger.info("University already selected: {}", universityName);
            }
        } catch (Exception e) {
            logger.warn("Could not select university {}: {}", universityName, e.getMessage());
        }
    }

    /**
     * Clicks Create Ad button in modal
     */
    public void clickCreateAdButtonInModal() {
        logger.info("Clicking Create Ad button in modal");
        try {
            WebElement btn = WebDriverWaitUtil.waitForElementClickable(modalCreateButton);
            click(btn);
            WebDriverWaitUtil.staticWait(3);
            logger.info("Clicked Create Ad button");
        } catch (Exception e) {
            // Try alternative button location
            logger.warn("Could not find modal create button, trying alternative: {}", e.getMessage());
            WebElement btn = driver.findElement(By.xpath("//div[@role='dialog']//button[text()='Create Ad']"));
            click(btn);
            WebDriverWaitUtil.staticWait(3);
        }
    }

    /**
     * Clicks Update Ad button in modal
     */
    public void clickUpdateAdButtonInModal() {
        logger.info("Clicking Update Ad button in modal");
        try {
            WebElement btn = WebDriverWaitUtil.waitForElementClickable(modalUpdateButton);
            click(btn);
            WebDriverWaitUtil.staticWait(3);
            logger.info("Clicked Update Ad button");
        } catch (Exception e) {
            logger.warn("Could not find modal update button: {}", e.getMessage());
        }
    }

    /**
     * Clicks edit button for a specific ad
     *
     * @param adTitle Ad title to edit
     */
    public void clickEditButtonForAd(String adTitle) {
        logger.info("Clicking edit button for ad: {}", adTitle);
        try {
            WebDriverWaitUtil.staticWait(2);
            // Find the ad card containing the title and click its Edit button
            String xpath = String.format("//h3[normalize-space()='%s']/ancestor::div[contains(@class,'p-4')]//button[contains(.,'Edit')]", adTitle);
            WebElement editButton = WebDriverWaitUtil.waitForElementClickable(By.xpath(xpath));
            click(editButton);
            WebDriverWaitUtil.staticWait(2);
            // Wait for modal to open
            WebDriverWaitUtil.waitForElementVisible(By.id("title"));
            logger.info("Edit modal opened for ad: {}", adTitle);
        } catch (Exception e) {
            logger.error("Could not find edit button for ad {}: {}", adTitle, e.getMessage());
            throw new RuntimeException("Could not find edit button for ad: " + adTitle, e);
        }
    }

    /**
     * Updates ad title
     *
     * @param newTitle New title
     */
    public void updateAdTitle(String newTitle) {
        logger.info("Updating ad title to: {}", newTitle);
        WebElement input = WebDriverWaitUtil.waitForElementVisible(titleInput);
        input.clear();
        sendKeys(input, newTitle);
    }

    /**
     * Updates ad description
     *
     * @param newDescription New description
     */
    public void updateAdDescription(String newDescription) {
        logger.info("Updating ad description to: {}", newDescription);
        WebElement input = WebDriverWaitUtil.waitForElementVisible(descriptionInput);
        input.clear();
        sendKeys(input, newDescription);
    }

    /**
     * Clicks toggle visibility button for a specific ad
     *
     * @param adTitle Ad title to toggle
     */
    public void clickToggleVisibilityButtonForAd(String adTitle) {
        logger.info("Clicking toggle visibility button for ad: {}", adTitle);
        try {
            WebDriverWaitUtil.staticWait(2);
            // Find the ad card and click the toggle button (Eye or EyeOff icon)
            String xpath = String.format("//h3[normalize-space()='%s']/ancestor::div[contains(@class,'p-4')]//button[.//*[name()='svg' and (@data-lucide='eye' or @data-lucide='eye-off')]]", adTitle);
            WebElement toggleButton = WebDriverWaitUtil.waitForElementClickable(By.xpath(xpath));
            click(toggleButton);
            WebDriverWaitUtil.staticWait(2);
            logger.info("Clicked toggle visibility button for ad: {}", adTitle);
        } catch (Exception e) {
            logger.error("Could not find toggle button for ad {}: {}", adTitle, e.getMessage());
            throw new RuntimeException("Could not find toggle button for ad: " + adTitle, e);
        }
    }

    /**
     * Clicks delete button for a specific ad
     *
     * @param adTitle Ad title to delete
     */
    public void clickDeleteButtonForAd(String adTitle) {
        logger.info("Clicking delete button for ad: {}", adTitle);
        try {
            WebDriverWaitUtil.staticWait(2);
            // Find the ad card and click the delete button (Trash icon)
            String xpath = String.format("//h3[normalize-space()='%s']/ancestor::div[contains(@class,'p-4')]//button[contains(@class,'destructive') or .//*[name()='svg' and @data-lucide='trash-2']]", adTitle);
            WebElement deleteButton = WebDriverWaitUtil.waitForElementClickable(By.xpath(xpath));
            click(deleteButton);
            WebDriverWaitUtil.staticWait(2);
            logger.info("Clicked delete button for ad: {}", adTitle);
        } catch (Exception e) {
            logger.error("Could not find delete button for ad {}: {}", adTitle, e.getMessage());
            throw new RuntimeException("Could not find delete button for ad: " + adTitle, e);
        }
    }

    /**
     * Confirms deletion in the browser alert
     */
    public void confirmDeletion() {
        logger.info("Confirming deletion");
        try {
            driver.switchTo().alert().accept();
            logger.info("Accepted browser alert for deletion");
            WebDriverWaitUtil.staticWait(2);
        } catch (Exception e) {
            logger.debug("No browser alert present, deletion may be immediate: {}", e.getMessage());
        }
    }

    /**
     * Gets the success/error message from toast notification
     *
     * @return Toast message text
     */
    public String getToastMessage() {
        logger.debug("Getting toast message");
        try {
            WebDriverWaitUtil.staticWait(2);
            WebElement toast = WebDriverWaitUtil.waitForElementVisible(toastNotification);
            if (toast != null) {
                String title = "";
                String description = "";
                try {
                    WebElement titleElement = toast.findElement(By.xpath(".//div[contains(@class,'title')]"));
                    title = titleElement.getText();
                } catch (Exception e) {
                    logger.debug("Toast title not found");
                }
                try {
                    WebElement descriptionElement = toast.findElement(By.xpath(".//div[contains(@class,'description')]"));
                    description = descriptionElement.getText();
                } catch (Exception e) {
                    logger.debug("Toast description not found");
                }

                String fullMessage = (title + " " + description).trim();
                if (!fullMessage.isEmpty()) {
                    logger.info("Toast message: {}", fullMessage);
                    return fullMessage;
                }
            }
            return "";
        } catch (Exception e) {
            logger.debug("Could not get toast message: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Checks if toast message contains expected text
     *
     * @param expectedText Expected text
     * @return true if message contains the text
     */
    public boolean isToastMessageContaining(String expectedText) {
        String actualMessage = getToastMessage();
        return actualMessage.toLowerCase().contains(expectedText.toLowerCase());
    }

    /**
     * Checks if ad exists in the list
     *
     * @param adTitle Ad title to check
     * @return true if ad exists
     */
    public boolean isAdInList(String adTitle) {
        logger.debug("Checking if ad '{}' exists in the list", adTitle);
        try {
            WebDriverWaitUtil.staticWait(2);
            String xpath = String.format("//h3[normalize-space()='%s']", adTitle);
            List<WebElement> elements = driver.findElements(By.xpath(xpath));
            return elements.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            logger.error("Error checking if ad exists: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if ad is NOT in the list
     *
     * @param adTitle Ad title to check
     * @return true if ad does NOT exist
     */
    public boolean isAdNotInList(String adTitle) {
        logger.debug("Checking if ad '{}' is NOT in the list", adTitle);
        try {
            WebDriverWaitUtil.staticWait(2);
            String xpath = String.format("//h3[normalize-space()='%s']", adTitle);
            List<WebElement> elements = driver.findElements(By.xpath(xpath));
            return elements.isEmpty() || elements.stream().noneMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            logger.error("Error checking if ad is not in list: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Gets the status of an ad (Active/Inactive)
     *
     * @param adTitle Ad title
     * @return Status string ("Active" or "Inactive")
     */
    public String getAdStatus(String adTitle) {
        logger.debug("Getting status for ad: {}", adTitle);
        try {
            WebDriverWaitUtil.staticWait(1);
            // The badge is a sibling of h3 within the flex container - it's a div with rounded-full class
            // Look for the div that contains "Active" or "Inactive" text near the title
            String xpath = String.format("//h3[normalize-space()='%s']/following-sibling::div[contains(@class,'rounded-full') or contains(@class,'inline-flex')]", adTitle);
            List<WebElement> badges = driver.findElements(By.xpath(xpath));
            if (!badges.isEmpty()) {
                String status = badges.get(0).getText();
                logger.info("Ad '{}' status: {}", adTitle, status);
                return status;
            }
            
            // Alternative: look for any div in the same parent that contains Active/Inactive
            xpath = String.format("//h3[normalize-space()='%s']/..//div[text()='Active' or text()='Inactive']", adTitle);
            badges = driver.findElements(By.xpath(xpath));
            if (!badges.isEmpty()) {
                String status = badges.get(0).getText();
                logger.info("Ad '{}' status (alt): {}", adTitle, status);
                return status;
            }
            
            logger.warn("Could not find status badge for ad: {}", adTitle);
            return "";
        } catch (Exception e) {
            logger.warn("Could not get ad status: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Checks if ad has specific status
     *
     * @param adTitle Ad title
     * @param expectedStatus Expected status (Active/Inactive)
     * @return true if status matches
     */
    public boolean isAdWithStatus(String adTitle, String expectedStatus) {
        String actualStatus = getAdStatus(adTitle);
        return actualStatus.equalsIgnoreCase(expectedStatus);
    }

    /**
     * Deletes an ad if it exists in the list (handles ads with timestamp suffix)
     *
     * @param adTitlePrefix Ad title prefix to search for (ads may have timestamp suffix)
     * @return true if an ad was found and deleted, false otherwise
     */
    public boolean deleteAdIfExists(String adTitlePrefix) {
        logger.info("Checking for ads starting with '{}' to delete", adTitlePrefix);
        try {
            WebDriverWaitUtil.staticWait(2);
            
            // Find any ad title that starts with the given prefix (handles timestamp suffix)
            String xpath = String.format("//h3[starts-with(normalize-space(),'%s')]", adTitlePrefix);
            List<WebElement> adElements = driver.findElements(By.xpath(xpath));
            
            if (adElements.isEmpty()) {
                logger.info("No ads found starting with '{}'", adTitlePrefix);
                return false;
            }
            
            // Get the full title of the first matching ad
            String fullTitle = adElements.get(0).getText().trim();
            logger.info("Found ad to delete: '{}'", fullTitle);
            
            // Click delete button for this ad
            String deleteXpath = String.format("//h3[normalize-space()='%s']/ancestor::div[contains(@class,'p-4')]//button[contains(@class,'destructive') or .//*[name()='svg' and @data-lucide='trash-2']]", fullTitle);
            WebElement deleteButton = WebDriverWaitUtil.waitForElementClickable(By.xpath(deleteXpath));
            click(deleteButton);
            WebDriverWaitUtil.staticWait(1);
            
            // Confirm deletion if alert is present
            try {
                driver.switchTo().alert().accept();
                logger.info("Accepted browser alert for deletion");
            } catch (Exception e) {
                logger.debug("No browser alert present");
            }
            
            WebDriverWaitUtil.staticWait(2);
            logger.info("Successfully deleted ad: '{}'", fullTitle);
            return true;
            
        } catch (Exception e) {
            logger.warn("Error while trying to delete ad '{}': {}", adTitlePrefix, e.getMessage());
            return false;
        }
    }
}

