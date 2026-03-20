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
 * Page Object Model for the Event Management page.
 * Handles all event-related operations: create, edit, delete.
 */
public class EventManagementPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(EventManagementPage.class);

    // Page Elements
    @FindBy(xpath = "//button[contains(.,'Create Event')]")
    private WebElement createEventButton;

    // Modal input fields
    @FindBy(xpath = "//input[@placeholder='e.g., Tech Networking Mixer']")
    private WebElement titleInput;

    @FindBy(xpath = "//input[@placeholder='What is this event about?']")
    private WebElement descriptionInput;

    @FindBy(xpath = "//input[@placeholder='e.g., Campus Main Hall, San Francisco']")
    private WebElement locationInput;

    @FindBy(xpath = "//input[@placeholder='https://example.com/image.jpg']")
    private WebElement imageUrlInput;

    // Date and Time inputs
    @FindBy(xpath = "//div[contains(.,'Date')]/input")
    private WebElement dateInput;

    @FindBy(xpath = "//div[contains(.,'Time')]/input")
    private WebElement timeInput;

    // Virtual Event toggle
    @FindBy(xpath = "//button[@role='switch']")
    private WebElement virtualEventToggle;

    // Category dropdown
    @FindBy(xpath = "//button[@role='combobox']")
    private WebElement categoryDropdown;

    // Modal buttons
    @FindBy(xpath = "//div[@role='dialog']//button[contains(.,'Create Event')]")
    private WebElement modalCreateButton;

    // Toast notification
    @FindBy(xpath = "//div[contains(@class,'radix-toast-viewport')]//div[@role='status']")
    private WebElement toastNotification;

    public EventManagementPage() {
        super();
    }

    /**
     * Navigates to the events management page (admin)
     */
    public void navigateToEventsPage() {
        logger.info("Navigating to events management page");
        String eventsUrl = ConfigReader.getBaseUrl() + "/admin/events";
        navigateTo(eventsUrl);
        WebDriverWaitUtil.staticWait(3);
    }

    /**
     * Clicks on Create Event button to open the modal
     */
    public void clickCreateEventButton() {
        logger.info("Clicking Create Event button");
        WebDriverWaitUtil.staticWait(2);
        
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                WebElement btn = WebDriverWaitUtil.waitForElementClickable(createEventButton);
                click(btn);
                WebDriverWaitUtil.staticWait(2);
                // Wait for modal to open - look for the dialog
                WebDriverWaitUtil.waitForElementVisible(By.xpath("//div[@role='dialog']"));
                logger.info("Create Event modal opened");
                return;
            } catch (Exception e) {
                logger.warn("Attempt {} to open Create Event modal failed, retrying...", i + 1);
                WebDriverWaitUtil.staticWait(2);
            }
        }
        throw new RuntimeException("Failed to open Create Event modal after " + maxRetries + " attempts");
    }

    /**
     * Enters event title
     *
     * @param title Event title
     */
    public void enterEventTitle(String title) {
        logger.info("Entering event title: {}", title);
        WebDriverWaitUtil.staticWait(1);
        // Find all text inputs in the dialog and use the first one for title
        java.util.List<WebElement> inputs = driver.findElements(By.xpath("//div[@role='dialog']//input[@type='text' or not(@type)]"));
        if (!inputs.isEmpty()) {
            WebElement titleInput = inputs.get(0);
            titleInput.clear();
            titleInput.sendKeys(title);
            logger.info("Entered title in first input field");
        } else {
            throw new RuntimeException("Could not find title input field");
        }
    }

    /**
     * Enters event description
     *
     * @param description Event description
     */
    public void enterEventDescription(String description) {
        logger.info("Entering event description: {}", description);
        WebDriverWaitUtil.staticWait(1);
        // Find all text inputs in the dialog and use the second one for description
        java.util.List<WebElement> inputs = driver.findElements(By.xpath("//div[@role='dialog']//input[@type='text' or not(@type)]"));
        if (inputs.size() >= 2) {
            WebElement descInput = inputs.get(1);
            descInput.clear();
            descInput.sendKeys(description);
            logger.info("Entered description in second input field");
        } else {
            logger.warn("Could not find description input field, found {} inputs", inputs.size());
        }
    }

    /**
     * Enters event date
     *
     * @param date Event date (format: MM/DD/YYYY)
     */
    public void enterEventDate(String date) {
        logger.info("Entering event date: {}", date);
        try {
            // Find date input near the Date label
            WebElement input = driver.findElement(By.xpath("//div[.//text()[contains(.,'Date')]]//input[@type='text' or not(@type)]"));
            WebDriverWaitUtil.waitForElementClickable(input);
            input.clear();
            input.sendKeys(date);
            WebDriverWaitUtil.staticWait(1);
        } catch (Exception e) {
            logger.warn("Could not enter date: {}", e.getMessage());
        }
    }

    /**
     * Enters event time
     *
     * @param time Event time (format: HH:MM AM/PM)
     */
    public void enterEventTime(String time) {
        logger.info("Entering event time: {}", time);
        try {
            // Find time input near the Time label
            WebElement input = driver.findElement(By.xpath("//div[.//text()[contains(.,'Time')]]//input[@type='text' or not(@type)]"));
            WebDriverWaitUtil.waitForElementClickable(input);
            input.clear();
            input.sendKeys(time);
            WebDriverWaitUtil.staticWait(1);
        } catch (Exception e) {
            logger.warn("Could not enter time: {}", e.getMessage());
        }
    }

    /**
     * Enters event location
     *
     * @param location Event location
     */
    public void enterEventLocation(String location) {
        logger.info("Entering event location: {}", location);
        try {
            WebElement input = WebDriverWaitUtil.waitForElementVisible(By.xpath("//input[@placeholder='e.g., Campus Main Hall, San Francisco']"));
            input.clear();
            sendKeys(input, location);
        } catch (Exception e) {
            logger.warn("Could not enter location: {}", e.getMessage());
        }
    }

    /**
     * Toggles virtual event switch
     */
    public void toggleVirtualEvent() {
        logger.info("Toggling virtual event switch");
        try {
            WebElement toggle = WebDriverWaitUtil.waitForElementClickable(By.xpath("//button[@role='switch']"));
            click(toggle);
            WebDriverWaitUtil.staticWait(1);
        } catch (Exception e) {
            logger.warn("Could not toggle virtual event: {}", e.getMessage());
        }
    }

    /**
     * Selects event category from dropdown
     *
     * @param category Category to select
     */
    public void selectCategory(String category) {
        logger.info("Selecting category: {}", category);
        try {
            // Find the category dropdown (combobox)
            WebElement dropdown = WebDriverWaitUtil.waitForElementClickable(By.xpath("//button[@role='combobox']"));
            click(dropdown);
            WebDriverWaitUtil.staticWait(1);

            // Select the option
            String optionXpath = String.format("//div[@role='option' and contains(.,'%s')]", category);
            WebElement option = WebDriverWaitUtil.waitForElementClickable(By.xpath(optionXpath));
            click(option);
            logger.info("Selected category: {}", category);
            WebDriverWaitUtil.staticWait(1);
        } catch (Exception e) {
            logger.warn("Could not select category: {}", e.getMessage());
        }
    }

    /**
     * Enters event image URL
     *
     * @param imageUrl Image URL
     */
    public void enterImageUrl(String imageUrl) {
        logger.info("Entering image URL: {}", imageUrl);
        try {
            WebElement input = WebDriverWaitUtil.waitForElementVisible(By.xpath("//input[@placeholder='https://example.com/image.jpg']"));
            input.clear();
            sendKeys(input, imageUrl);
        } catch (Exception e) {
            logger.warn("Could not enter image URL: {}", e.getMessage());
        }
    }

    /**
     * Clicks Create Event button in modal
     */
    public void clickCreateEventButtonInModal() {
        logger.info("Clicking Create Event button in modal");
        try {
            // Wait for button to be enabled
            WebDriverWaitUtil.staticWait(1);
            WebElement btn = WebDriverWaitUtil.waitForElementClickable(By.xpath("//div[@role='dialog']//button[contains(.,'Create Event') and not(@disabled)]"));
            click(btn);
            WebDriverWaitUtil.staticWait(3);
            logger.info("Clicked Create Event button");
        } catch (Exception e) {
            logger.warn("Could not find modal create button: {}", e.getMessage());
            // Try alternative
            try {
                WebElement btn = driver.findElement(By.xpath("//div[@role='dialog']//button[text()='Create Event']"));
                click(btn);
                WebDriverWaitUtil.staticWait(3);
            } catch (Exception ex) {
                throw new RuntimeException("Could not click Create Event button in modal", ex);
            }
        }
    }

    /**
     * Clicks delete button for a specific event
     *
     * @param eventTitle Event title to delete
     */
    public void clickDeleteButtonForEvent(String eventTitle) {
        logger.info("Clicking delete button for event: {}", eventTitle);
        try {
            WebDriverWaitUtil.staticWait(2);
            // Find the event card containing the title and click its Delete button
            String xpath = String.format("//h3[normalize-space()='%s']/ancestor::div[contains(@class,'rounded')]//button[contains(.,'Delete')]", eventTitle);
            WebElement deleteButton = WebDriverWaitUtil.waitForElementClickable(By.xpath(xpath));
            click(deleteButton);
            WebDriverWaitUtil.staticWait(2);
            logger.info("Clicked delete button for event: {}", eventTitle);
        } catch (Exception e) {
            logger.error("Could not find delete button for event {}: {}", eventTitle, e.getMessage());
            throw new RuntimeException("Could not find delete button for event: " + eventTitle, e);
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
     * Checks if event exists in the list
     *
     * @param eventTitle Event title to check
     * @return true if event exists
     */
    public boolean isEventInList(String eventTitle) {
        logger.debug("Checking if event '{}' exists in the list", eventTitle);
        try {
            WebDriverWaitUtil.staticWait(2);
            String escaped = eventTitle.replace("'", "\\'");

            List<By> locators = List.of(
                    // Exact match
                    By.xpath(String.format("//h3[normalize-space()='%s']", eventTitle)),
                    // Title in h tags (new UI may use different tags)
                    By.xpath(String.format("//*[self::h1 or self::h2 or self::h3 or self::h4][contains(normalize-space(),'%s')]", eventTitle)),
                    // Card/grid container
                    By.xpath(String.format("//div[contains(@class,'rounded')]//*[contains(normalize-space(),'%s')]", eventTitle)),
                    // Fallback: main content
                    By.xpath(String.format("//*[@role='main']//*[contains(normalize-space(),'%s')]", eventTitle))
            );

            for (By locator : locators) {
                List<WebElement> elements = driver.findElements(locator);
                for (WebElement element : elements) {
                    if (element != null && element.isDisplayed()) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking if event exists: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if event is NOT in the list
     *
     * @param eventTitle Event title to check
     * @return true if event does NOT exist
     */
    public boolean isEventNotInList(String eventTitle) {
        logger.debug("Checking if event '{}' is NOT in the list", eventTitle);
        try {
            WebDriverWaitUtil.staticWait(2);
            return !isEventInList(eventTitle);
        } catch (Exception e) {
            logger.error("Error checking if event is not in list: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Deletes an event if it exists in the list (handles events with timestamp suffix)
     *
     * @param eventTitlePrefix Event title prefix to search for
     * @return true if an event was found and deleted, false otherwise
     */
    public boolean deleteEventIfExists(String eventTitlePrefix) {
        logger.info("Checking for events starting with '{}' to delete", eventTitlePrefix);
        try {
            WebDriverWaitUtil.staticWait(2);
            
            // Find any event title that starts with the given prefix
            String xpath = String.format("//h3[starts-with(normalize-space(),'%s')]", eventTitlePrefix);
            List<WebElement> eventElements = driver.findElements(By.xpath(xpath));
            
            if (eventElements.isEmpty()) {
                logger.info("No events found starting with '{}'", eventTitlePrefix);
                return false;
            }
            
            // Get the full title of the first matching event
            String fullTitle = eventElements.get(0).getText().trim();
            logger.info("Found event to delete: '{}'", fullTitle);
            
            // Click delete button for this event
            String deleteXpath = String.format("//h3[normalize-space()='%s']/ancestor::div[contains(@class,'rounded')]//button[contains(.,'Delete')]", fullTitle);
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
            logger.info("Successfully deleted event: '{}'", fullTitle);
            return true;
            
        } catch (Exception e) {
            logger.warn("Error while trying to delete event '{}': {}", eventTitlePrefix, e.getMessage());
            return false;
        }
    }
}

