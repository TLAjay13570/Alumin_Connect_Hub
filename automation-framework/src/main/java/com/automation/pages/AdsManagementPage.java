package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.JavaScriptExecutorUtil;
import com.automation.utils.ToastUtil;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
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
        String mt = mediaType.trim();
        try {
            // Radix Select: trigger stays in the dialog; SelectContent is portaled (often under a popover layer).
            List<By> triggerLocators = List.of(
                    By.xpath("//div[@role='dialog']//label[contains(.,'Media Type')]/following::button[1]"),
                    By.xpath("//div[@role='dialog']//label[@for='mediaType']/following::button[1]"),
                    By.xpath("//div[@role='dialog']//label[contains(.,'Media')]/following::button[@role='combobox'][1]"),
                    By.xpath("//div[@role='dialog']//button[@role='combobox'][1]")
            );
            WebElement trigger = WebDriverWaitUtil.waitForAnyElementVisible(triggerLocators, 25);
            WebDriverWaitUtil.waitForElementClickable(trigger);
            click(trigger);

            boolean picked = false;
            List<By> optionLocators = List.of(
                    By.xpath("//div[contains(@class,'popover')]//*[normalize-space()=\"" + mt + "\"]"),
                    By.xpath("//*[@role='listbox']//*[@role='option' and contains(normalize-space(),\"" + mt + "\")]"),
                    By.xpath("//*[@role='option' and contains(normalize-space(),\"" + mt + "\")]"),
                    By.xpath("//div[contains(@class,'SelectItem') or contains(@class,'select-item')][contains(.,\"" + mt + "\")]")
            );
            try {
                WebElement option = WebDriverWaitUtil.waitForAnyElementVisible(optionLocators, 12);
                JavaScriptExecutorUtil.scrollToElement(option);
                WebDriverWaitUtil.waitForElementClickable(option);
                click(option);
                picked = true;
            } catch (Exception ex) {
                logger.debug("Clicking portaled option failed, trying keyboard: {}", ex.getMessage());
            }
            if (!picked) {
                WebDriverWaitUtil.waitForElementClickable(trigger);
                click(trigger);
                Actions actions = new Actions(driver);
                int arrowDowns = mt.equalsIgnoreCase("Video") ? 2 : 1;
                for (int i = 0; i < arrowDowns; i++) {
                    actions.pause(Duration.ofMillis(120)).sendKeys(Keys.ARROW_DOWN).perform();
                }
                actions.sendKeys(Keys.ENTER).perform();
            }
            logger.info("Selected media type: {}", mediaType);
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
        // Do not send Escape here — Radix/shadcn Dialog treats Escape as close and the modal will disappear.
        List<By> candidates = List.of(
                By.cssSelector("[role='dialog'] #mediaUrl"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Image URL') or contains(.,'Video URL')]/following::input[1]"),
                By.id("mediaUrl"),
                By.id("mediaURL"),
                By.id("media_url"),
                By.id("imageUrl"),
                By.xpath("//div[@role='dialog']//input[contains(@placeholder,'.jpg') or contains(@placeholder,'.mp4') or contains(@placeholder,'image.jpg') or contains(@placeholder,'video.mp4')][1]"),
                By.xpath("//div[@role='dialog']//input[contains(@id,'media') or contains(@name,'media')][1]")
        );
        WebElement input;
        try {
            input = WebDriverWaitUtil.waitForAnyElementVisible(candidates, 20);
        } catch (Exception primary) {
            logger.debug("Primary media URL locators failed, using dialog field order heuristic: {}", primary.getMessage());
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            input = wait.until(d -> findMediaUrlInputInOpenDialog());
        }
        JavaScriptExecutorUtil.scrollToElement(input);
        try {
            WebDriverWaitUtil.waitForElementClickable(input);
            input.clear();
            sendKeys(input, mediaUrl);
        } catch (Exception e) {
            logger.debug("Direct input failed; using JS set on mediaUrl: {}", e.getMessage());
            JavaScriptExecutorUtil.executeScript(
                    "arguments[0].removeAttribute('readonly');"
                            + "arguments[0].value=arguments[1];"
                            + "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));"
                            + "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                    input, mediaUrl);
        }
    }

    /**
     * When ids differ per environment, pick the first dialog text/url input that is not title, description, or link URL.
     */
    private WebElement findMediaUrlInputInOpenDialog() {
        List<WebElement> inputs = driver.findElements(By.xpath(
                "//div[@role='dialog']//input[not(@type='hidden') and not(@type='checkbox') "
                        + "and not(@type='file') and not(@type='radio')]"));
        for (WebElement el : inputs) {
            try {
                if (!el.isDisplayed()) {
                    continue;
                }
                String id = nullToEmpty(el.getAttribute("id")).toLowerCase();
                if (id.equals("title") || id.equals("description")) {
                    continue;
                }
                if (id.contains("link") && id.contains("url")) {
                    continue;
                }
                String ph = nullToEmpty(el.getAttribute("placeholder")).toLowerCase();
                if (ph.contains("learn more") || ph.contains("/offer")) {
                    continue;
                }
                if (id.contains("media") || id.contains("image") || ph.contains("image") || ph.contains("video") || ph.contains(".jpg") || ph.contains(".mp4")) {
                    return el;
                }
            } catch (Exception ignored) {
            }
        }
        for (WebElement el : inputs) {
            try {
                if (!el.isDisplayed()) {
                    continue;
                }
                String id = nullToEmpty(el.getAttribute("id")).toLowerCase();
                if (id.equals("title") || id.equals("description")) {
                    continue;
                }
                if (id.contains("link") && id.contains("url")) {
                    continue;
                }
                String ph = nullToEmpty(el.getAttribute("placeholder")).toLowerCase();
                if (ph.contains("learn more")) {
                    continue;
                }
                return el;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
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
            WebElement trigger = driver.findElement(By.xpath(
                    "//div[@role='dialog']//label[contains(.,'Ad Placement')]/following::button[@role='combobox'][1]"));
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
            
            List<By> placementOptions = List.of(
                    By.xpath(optionXpath),
                    By.xpath("//*[@role='listbox']//*[@role='option' and contains(normalize-space(),\"" + placement + "\")]")
            );
            WebElement option = WebDriverWaitUtil.waitForAnyElementVisible(placementOptions, 20);
            WebDriverWaitUtil.waitForElementClickable(option);
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
        String fullMessage = ToastUtil.getLatestToastText();
        if (!fullMessage.isEmpty()) {
            logger.info("Toast message: {}", fullMessage);
        }
        return fullMessage;
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

