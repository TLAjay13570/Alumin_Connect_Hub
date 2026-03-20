package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.JavaScriptExecutorUtil;
import com.automation.utils.WebDriverWaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;

/**
 * University Management Page Object Model class
 * Contains all elements and methods related to university management page
 */
public class UniversityManagementPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(UniversityManagementPage.class);

    // Page Elements using PageFactory
    @FindBy(xpath = "//button[contains(text(),'Add University')] | //button[.//*[contains(text(),'Add University')]] | //button[contains(.,'Add University')]")
    private WebElement addUniversityButton;

    // Modal/Dialog form fields
    @FindBy(xpath = "//input[@id='id'] | //input[@name='id'] | //input[contains(@placeholder,'ID')] | //input[contains(@placeholder,'University ID')] | //label[contains(text(),'University ID')]/following-sibling::input")
    private WebElement universityIdField;

    @FindBy(xpath = "//input[@id='name'] | //input[@name='name'] | //input[contains(@placeholder,'Name')] | //input[contains(@placeholder,'University Name')] | //label[contains(text(),'University Name')]/following-sibling::input")
    private WebElement universityNameField;

    @FindBy(xpath = "//input[@id='logo'] | //input[@name='logo'] | //input[contains(@placeholder,'Logo')] | //input[contains(@placeholder,'Logo URL')] | //label[contains(text(),'Logo')]/following-sibling::input")
    private WebElement universityLogoField;

    // Create button in modal - exact text "Create University"
    @FindBy(xpath = "//button[normalize-space()='Create University']")
    private WebElement createButton;

    // Update button in modal - exact text "Update University"
    @FindBy(xpath = "//button[normalize-space()='Update University']")
    private WebElement updateButton;

    // Toast notification success message (using multiple patterns)
    @FindBy(xpath = "//div[contains(@class,'toast')] | //div[contains(@role,'status')] | //div[contains(@class,'notification')] | //div[contains(@data-radix-toast-viewport)]//div | //div[contains(text(),'created') or contains(text(),'added') or contains(text(),'updated') or contains(text(),'saved') or contains(text(),'deleted') or contains(text(),'removed')]")
    private WebElement successMessage;

    // Confirm button for deletion (if confirmation dialog exists)
    @FindBy(xpath = "//button[contains(@class,'confirm')] | //button[contains(text(),'Confirm')] | //button[contains(text(),'Yes')] | //button[contains(text(),'OK')] | //button[contains(text(),'Delete')]")
    private WebElement confirmButton;

    /**
     * Navigates to universities management page
     */
    public void navigateToUniversitiesPage() {
        logger.info("Navigating to universities management page");
        String baseUrl = ConfigReader.getBaseUrl();
        String universitiesUrl = baseUrl.endsWith("/") ? baseUrl + "superadmin/universities" : baseUrl + "/superadmin/universities";
        navigateTo(universitiesUrl);
        
        // Wait for page to load
        try {
            WebDriverWaitUtil.staticWait(3);
        } catch (Exception e) {
            logger.debug("Wait interrupted: {}", e.getMessage());
        }
    }

    /**
     * Clicks on Add University button
     */
    public void clickAddUniversityButton() {
        logger.info("Clicking Add University button");
        try {
            click(addUniversityButton);
            
            // Wait for modal/dialog to open - check for dialog content
            try {
                WebDriverWaitUtil.waitForElementVisible(By.xpath("//input[@id='id']"));
            } catch (Exception ex) {
                WebDriverWaitUtil.staticWait(2);
            }
            logger.info("Modal dialog opened successfully");
        } catch (Exception e) {
            logger.warn("Modal may not have opened, continuing anyway: {}", e.getMessage());
            try {
                WebDriverWaitUtil.staticWait(2);
            } catch (Exception ex) {
                logger.debug("Wait interrupted: {}", ex.getMessage());
            }
        }
    }

    /**
     * Enters university ID
     *
     * @param universityId University ID to enter
     */
    public void enterUniversityId(String universityId) {
        logger.info("Entering university ID: {}", universityId);
        try {
            // Wait for field to be visible in modal
            WebDriverWaitUtil.waitForElementVisible(universityIdField);
            sendKeys(universityIdField, universityId);
        } catch (Exception e) {
            logger.error("Error entering university ID: {}", e.getMessage());
            // Try alternative locator
            try {
                WebElement field = driver.findElement(By.xpath("//input[@id='id']"));
                sendKeys(field, universityId);
            } catch (Exception ex) {
                throw new RuntimeException("Could not find university ID field", ex);
            }
        }
    }

    /**
     * Enters university name
     *
     * @param universityName University name to enter
     */
    public void enterUniversityName(String universityName) {
        logger.info("Entering university name: {}", universityName);
        try {
            // Wait for field to be visible in modal
            WebDriverWaitUtil.waitForElementVisible(universityNameField);
            sendKeys(universityNameField, universityName);
        } catch (Exception e) {
            logger.error("Error entering university name: {}", e.getMessage());
            // Try alternative locator
            try {
                WebElement field = driver.findElement(By.xpath("//input[@id='name']"));
                sendKeys(field, universityName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not find university name field", ex);
            }
        }
    }

    /**
     * Enters university logo URL
     *
     * @param logoUrl Logo URL to enter
     */
    public void enterUniversityLogo(String logoUrl) {
        logger.info("Entering university logo URL: {}", logoUrl);
        // The app supports both "upload file" and "paste a URL". In CI/environments without outbound access,
        // pasting external URLs often fails silently -> upload a locally generated PNG if a file input exists.

        List<By> fileInputCandidates = List.of(
                By.xpath("//div[@role='dialog']//input[@type='file' and contains(@accept,'image')]"),
                By.xpath("//div[@role='dialog']//input[@type='file']"),
                By.cssSelector("input[type='file']")
        );

        try {
            // File inputs are frequently hidden; we only need them to exist in the DOM.
            WebElement fileInputFound = null;
            for (By candidate : fileInputCandidates) {
                List<WebElement> inputs = driver.findElements(candidate);
                if (!inputs.isEmpty()) {
                    fileInputFound = inputs.get(0);
                    break;
                }
            }

            if (fileInputFound != null) {
                File tmpLogo = generateTempLogoPng();
                logger.info("Uploading temp university logo file: {}", tmpLogo.getAbsolutePath());
                fileInputFound.sendKeys(tmpLogo.getAbsolutePath());

                // Wait for preview/remove UI to appear (best-effort; not required if toast handles validation).
                try {
                    WebDriverWaitUtil.waitForAnyElementVisible(List.of(
                            By.xpath("//*[contains(.,'Remove logo')]"),
                            By.xpath("//img[contains(@alt,'logo') or contains(@alt,'Logo')]"),
                            By.xpath("//div[contains(@class,'preview')]//img")
                    ));
                } catch (Exception ignored) {
                    // Continue; create flow will validate.
                }
                return;
            }
        } catch (Exception fileInputEx) {
            logger.debug("File input not available; falling back to pasting logo URL. Reason: {}", fileInputEx.getMessage());
        }

        // Fallback: paste URL into the URL input field.
        List<By> urlInputCandidates = List.of(
                By.id("logo"),
                By.id("logoUrl"),
                By.id("logo_url"),
                By.id("logoURL"),
                By.xpath("//div[@role='dialog']//label[contains(.,'Logo')]/following::input[1]"),
                By.xpath("//div[@role='dialog']//input[contains(@placeholder,'Logo') or contains(@aria-label,'Logo')][1]"),
                By.xpath("//div[@role='dialog']//input[contains(@name,'logo') or contains(@id,'logo')][1]")
        );

        try {
            WebElement field = WebDriverWaitUtil.waitForAnyElementVisible(urlInputCandidates);
            sendKeys(field, logoUrl);
        } catch (Exception e) {
            logger.warn("Could not set university logo URL; continuing without logo. Reason: {}", e.getMessage());
        }
    }

    private File generateTempLogoPng() {
        try {
            int w = 128;
            int h = 128;
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();

            // Deterministic-ish but varied color so repeated runs aren't identical.
            int seed = UUID.randomUUID().hashCode();
            Color bg = new Color(Math.abs(seed % 255), Math.abs((seed / 2) % 255), Math.abs((seed / 3) % 255));
            g2.setColor(bg);
            g2.fillRect(0, 0, w, h);

            g2.setColor(Color.WHITE);
            g2.drawString("UNI", 10, 70);
            g2.dispose();

            Path tmp = Files.createTempFile("university-logo-", ".png");
            File tmpFile = tmp.toFile();
            ImageIO.write(image, "png", tmpFile);
            tmpFile.deleteOnExit();
            return tmpFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate temporary logo PNG", e);
        }
    }

    /**
     * Clicks on Create or Save button - button text is "Create University"
     */
    public void clickCreateOrSaveButton() {
        logger.info("Clicking Create University button");
        try {
            ensureThemeColors();
            // Try to find "Create University" button first (exact text)
            WebElement createBtn = null;
            try {
                createBtn = WebDriverWaitUtil.waitForElementVisible(createButton);
            } catch (Exception e) {
                // Try alternative locator
                createBtn = driver.findElement(By.xpath("//button[normalize-space()='Create University']"));
            }
            click(createBtn);
            logger.info("Clicked Create University button");
        } catch (Exception e) {
            logger.warn("Create University button not found, trying alternatives: {}", e.getMessage());
            // Try alternative locators
            try {
                WebElement btn = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //dialog//button[@type='submit']"));
                click(btn);
                logger.info("Clicked alternative create button");
            } catch (Exception ex) {
                throw new RuntimeException("Could not find Create button", ex);
            }
        }
        
        // Wait for modal to close and toast notification to appear
        try {
            WebDriverWaitUtil.staticWait(3);
        } catch (Exception e) {
            logger.debug("Wait interrupted: {}", e.getMessage());
        }
    }

    /**
     * Clicks on Update or Save button - button text is "Update University"
     */
    public void clickUpdateOrSaveButton() {
        logger.info("Clicking Update University button");
        try {
            ensureThemeColors();
            // Try to find "Update University" button first (exact text)
            WebElement updateBtn = null;
            try {
                updateBtn = WebDriverWaitUtil.waitForElementVisible(updateButton);
            } catch (Exception e) {
                // Try alternative locator
                updateBtn = driver.findElement(By.xpath("//button[normalize-space()='Update University']"));
            }
            click(updateBtn);
            logger.info("Clicked Update University button");
        } catch (Exception e) {
            logger.warn("Update University button not found, trying alternatives: {}", e.getMessage());
            // Try alternative locators
            try {
                WebElement btn = driver.findElement(By.xpath("//button[contains(text(),'Update')] | //dialog//button[@type='submit']"));
                click(btn);
                logger.info("Clicked alternative update button");
            } catch (Exception ex) {
                throw new RuntimeException("Could not find Update button", ex);
            }
        }
        
        // Wait for modal to close and toast notification to appear
        try {
            WebDriverWaitUtil.staticWait(3);
        } catch (Exception e) {
            logger.debug("Wait interrupted: {}", e.getMessage());
        }
    }

    /**
     * Some deployments validate that all theme color pickers have values.
     * We populate <input type="color"> fields before submit (best-effort).
     */
    private void ensureThemeColors() {
        try {
            List<WebElement> colorInputs = driver.findElements(By.cssSelector("input[type='color']"));
            if (colorInputs == null || colorInputs.isEmpty()) {
                return;
            }

            // Order assumption: Light(Primary, Secondary, Accent) then Dark(Primary, Secondary, Accent).
            String[] desiredColors = new String[] {
                    "#3b82f6", // Light Primary (blue)
                    "#6b7280", // Light Secondary (gray)
                    "#10b981", // Light Accent (green)
                    "#2563eb", // Dark Primary (blue)
                    "#4b5563", // Dark Secondary (gray)
                    "#059669"  // Dark Accent (teal)
            };

            int count = Math.min(desiredColors.length, colorInputs.size());
            for (int i = 0; i < count; i++) {
                WebElement input = colorInputs.get(i);
                try {
                    input.sendKeys(desiredColors[i]);
                } catch (Exception e) {
                    // Fallback for color inputs that don't accept direct typing.
                    try {
                        JavaScriptExecutorUtil.setAttribute(input, "value", desiredColors[i]);
                    } catch (Exception ignored) {
                        // best-effort only
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Could not ensure theme colors: {}", e.getMessage());
        }
    }

    /**
     * Clicks on edit button for a specific university
     *
     * @param universityName University name to edit
     */
    public void clickEditButtonForUniversity(String universityName) {
        logger.info("Clicking edit button for university: {}", universityName);
        try {
            // Wait for university list to load
            WebDriverWaitUtil.staticWait(2);
            
            // Try multiple xpath patterns to find edit button near the university name
            // The university cards have h3 headings with the name and Edit button in the same card
            String[] xpaths = {
                String.format("//h3[contains(text(),'%s')]/ancestor::div[contains(@class,'rounded') or contains(@class,'card')]//button[contains(text(),'Edit')]", universityName),
                String.format("//h3[normalize-space()='%s']/ancestor::div//button[contains(text(),'Edit')]", universityName),
                String.format("//h3[contains(text(),'%s')]/following::button[contains(text(),'Edit')][1]", universityName),
                String.format("//*[contains(text(),'%s')]/ancestor::div//button[contains(.,'Edit')]", universityName),
                String.format("//h3[contains(text(),'%s')]/ancestor::*//button[contains(text(),'Edit')]", universityName)
            };
            
            WebElement editButton = null;
            for (String xpath : xpaths) {
                try {
                    List<WebElement> buttons = driver.findElements(By.xpath(xpath));
                    if (!buttons.isEmpty()) {
                        editButton = buttons.get(0);
                        break;
                    }
                } catch (Exception e) {
                    // Continue to next xpath
                }
            }
            
            if (editButton == null) {
                throw new RuntimeException("Edit button not found for university: " + universityName);
            }
            
            click(editButton);
            
            // Wait for edit modal to open - check for dialog with name field
            try {
                WebDriverWaitUtil.waitForElementVisible(By.xpath("//dialog//input | //div[@role='dialog']//input"));
            } catch (Exception ex) {
                WebDriverWaitUtil.staticWait(2);
            }
            logger.info("Edit modal opened successfully");
        } catch (Exception e) {
            logger.error("Error finding edit button for university {}: {}", universityName, e.getMessage());
            throw new RuntimeException("Could not find edit button for university: " + universityName, e);
        }
    }

    /**
     * Updates university name
     *
     * @param updatedName Updated university name
     */
    public void updateUniversityName(String updatedName) {
        logger.info("Updating university name to: {}", updatedName);
        try {
            // Wait for name field to be visible in edit modal
            WebDriverWaitUtil.waitForElementVisible(universityNameField);
            // Clear existing name and enter new name
            universityNameField.clear();
            sendKeys(universityNameField, updatedName);
        } catch (Exception e) {
            logger.error("Error updating university name: {}", e.getMessage());
            // Try alternative locator
            try {
                WebElement field = driver.findElement(By.xpath("//input[@id='name']"));
                field.clear();
                sendKeys(field, updatedName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not find university name field for update", ex);
            }
        }
    }

    /**
     * Clicks on delete button for a specific university
     * The delete button is an icon-only button with class "text-destructive" containing a Trash2 icon
     *
     * @param universityName University name to delete
     */
    public void clickDeleteButtonForUniversity(String universityName) {
        logger.info("Clicking delete button for university: {}", universityName);
        try {
            // Wait for university list to load
            WebDriverWaitUtil.staticWait(2);
            
            // The delete button structure from frontend:
            // <Button size="sm" variant="outline" onClick={() => handleDelete(uni)} className="text-destructive hover:text-destructive">
            //   <Trash2 className="w-3 h-3" />
            // </Button>
            // The button is the last one in the button row, after Edit and Enable/Disable buttons
            String[] xpaths = {
                // Look for button with destructive class in the same card as the university name
                String.format("//h3[normalize-space()='%s']/ancestor::div[contains(@class,'p-4')]//button[contains(@class,'destructive')]", universityName),
                String.format("//h3[contains(text(),'%s')]/ancestor::div[contains(@class,'p-4')]//button[contains(@class,'destructive')]", universityName),
                // Look for the last button in the button row (after Edit and Enable/Disable)
                String.format("//h3[normalize-space()='%s']/ancestor::div[contains(@class,'p-4')]//div[contains(@class,'flex') and contains(@class,'gap-2')]/button[last()]", universityName),
                String.format("//h3[contains(text(),'%s')]/ancestor::div[contains(@class,'p-4')]//div[contains(@class,'flex')]/button[last()]", universityName),
                // Alternative: look for button without text content (icon only) after Edit button
                String.format("//h3[contains(text(),'%s')]/ancestor::div//button[contains(text(),'Edit')]/following-sibling::button[last()]", universityName)
            };
            
            WebElement deleteButton = null;
            for (String xpath : xpaths) {
                try {
                    List<WebElement> buttons = driver.findElements(By.xpath(xpath));
                    if (!buttons.isEmpty()) {
                        deleteButton = buttons.get(0);
                        logger.debug("Found delete button with xpath: {}", xpath);
                        break;
                    }
                } catch (Exception e) {
                    // Continue to next xpath
                }
            }
            
            if (deleteButton == null) {
                throw new RuntimeException("Delete button not found for university: " + universityName);
            }
            
            click(deleteButton);
            logger.info("Clicked delete button for university: {}", universityName);
            
            // Wait a moment for the browser confirm dialog to appear
            try {
                WebDriverWaitUtil.staticWait(1);
            } catch (Exception e) {
                logger.debug("Wait interrupted: {}", e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error finding delete button for university {}: {}", universityName, e.getMessage());
            throw new RuntimeException("Could not find delete button for university: " + universityName, e);
        }
    }

    /**
     * Confirms deletion - The app uses window.confirm() which creates a native browser alert
     */
    public void confirmDeletion() {
        logger.info("Confirming deletion via browser alert");
        boolean alertAccepted = false;

        // 1) Native browser confirm (window.confirm)
        try {
            Alert alert = WebDriverWaitUtil.waitForAlert();
            String alertText = alert.getText();
            logger.info("Browser confirm dialog found with text: {}", alertText);
            alert.accept();
            alertAccepted = true;
            logger.info("Accepted browser confirm dialog for deletion");
        } catch (Exception e) {
            logger.debug("No native browser alert present: {}", e.getMessage());
        }

        // 2) In-app confirmation modal (Radix/shadcn confirm button)
        if (!alertAccepted) {
            try {
                WebDriverWaitUtil.waitForElementClickable(confirmButton).click();
                logger.info("Clicked in-app confirm button for deletion");
            } catch (Exception e) {
                logger.debug("No in-app confirm button clicked: {}", e.getMessage());
            }
        }

        // Give UI a chance to render the toast. The step assertions will wait for expected text.
        try {
            WebDriverWaitUtil.waitForAnyElementVisible(List.of(
                    By.xpath("//*[@data-state='open' and contains(@class,'group')]"),
                    By.xpath("//li[@data-state='open']"),
                    By.xpath("//*[@role='status']"),
                    By.xpath("//ol//li[contains(@class,'group') and @data-state='open']")
            ));
        } catch (Exception e) {
            logger.debug("Toast not immediately visible after delete confirmation: {}", e.getMessage());
        }
    }

    /**
     * Checks if success message is displayed
     *
     * @return true if success message is displayed
     */
    public boolean isSuccessMessageDisplayed() {
        logger.debug("Checking if success message is displayed");
        try {
            // The app uses Radix UI Toast with data-state="open"
            String[] xpaths = {
                "//*[@data-state='open' and contains(@class,'group')]",
                "//li[@data-state='open']",
                "//*[@role='status']",
                "//ol//li[contains(@class,'group')]",
                "//*[contains(@class,'toast') and @data-state='open']"
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
            // The app uses Radix UI Toast with data-state="open"
            // Toast structure: li[data-state=open] > div > div (title) + div (description)
            String[] xpaths = {
                "//*[@data-state='open' and contains(@class,'group')]",
                "//li[@data-state='open']",
                "//*[@role='status']",
                "//ol//li[contains(@class,'group') and @data-state='open']"
            };
            
            String latestMessage = "";
            for (String xpath : xpaths) {
                try {
                    List<WebElement> elements = driver.findElements(By.xpath(xpath));
                    for (WebElement element : elements) {
                        if (element.isDisplayed()) {
                            String text = element.getText();
                            if (text != null && !text.trim().isEmpty()) {
                                // Keep updating so we return the last visible toast candidate.
                                latestMessage = text;
                                logger.debug("Success message text found (candidate): {}", text);
                            }
                        }
                    }
                } catch (Exception e) {
                    // Continue to next xpath
                }
            }

            return latestMessage;
        } catch (Exception e) {
            logger.debug("Could not get success message: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Checks if success message contains specific text
     *
     * @param text Text to check for
     * @return true if message contains the text
     */
    public boolean isSuccessMessageContaining(String text) {
        logger.debug("Checking if success message contains: {}", text);
        try {
            String message = getSuccessMessage();
            logger.debug("Got success message: '{}'", message);
            if (message != null && !message.isEmpty()) {
                boolean contains = message.toLowerCase().contains(text.toLowerCase());
                logger.debug("Message contains '{}': {}", text, contains);
                return contains;
            }
            return false;
        } catch (Exception e) {
            logger.debug("Error checking success message: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Waits until the currently visible toast message contains either expected text.
     * This reduces flakiness for delete flows where previous toast messages might still be visible briefly.
     *
     * @return the toast message that matched
     */
    public String waitForSuccessMessageContainingEither(String text1, String text2) {
        String lower1 = text1.toLowerCase();
        String lower2 = text2.toLowerCase();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        return wait.until(d -> {
            String msg = getSuccessMessage();
            if (msg == null || msg.trim().isEmpty()) {
                return null;
            }
            String lower = msg.toLowerCase();
            if (lower.contains(lower1) || lower.contains(lower2)) {
                return msg;
            }
            return null;
        });
    }

    /**
     * Checks if university exists in the list
     *
     * @param universityName University name to check
     * @return true if university exists in the list
     */
    public boolean isUniversityInList(String universityName) {
        logger.debug("Checking if university '{}' exists in the list", universityName);
        try {
            // Wait for list to load
            WebDriverWaitUtil.staticWait(2);
            
            // Try multiple xpath patterns to find university in cards
            // The university name is displayed in h3 heading (level 3) inside a card
            String[] xpaths = {
                String.format("//h3[normalize-space()='%s']", universityName),
                String.format("//h3[contains(text(),'%s')]", universityName),
                String.format("//main//h3[contains(text(),'%s')]", universityName),
                String.format("//*[@role='main']//h3[contains(text(),'%s')]", universityName),
                String.format("//div[contains(@class,'rounded')]//h3[contains(text(),'%s')]", universityName)
            };
            
            for (String xpath : xpaths) {
                try {
                    List<WebElement> elements = driver.findElements(By.xpath(xpath));
                    for (WebElement element : elements) {
                        if (element.isDisplayed()) {
                            String text = element.getText();
                            if (text != null && text.contains(universityName)) {
                                logger.debug("University '{}' found in list using xpath: {}", universityName, xpath);
                                return true;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Continue to next xpath
                }
            }
            
            logger.debug("University '{}' not found in list", universityName);
            return false;
        } catch (Exception e) {
            logger.error("Error checking if university exists: {}", e.getMessage());
            return false;
        }
    }

    private boolean isUniversityPresentWithoutWait(String universityName) {
        // Same lookup logic as isUniversityInList, but without any artificial sleeps.
        try {
            String[] xpaths = {
                    String.format("//h3[normalize-space()='%s']", universityName),
                    String.format("//h3[contains(text(),'%s')]", universityName),
                    String.format("//main//h3[contains(text(),'%s')]", universityName),
                    String.format("//*[@role='main']//h3[contains(text(),'%s')]", universityName),
                    String.format("//div[contains(@class,'rounded')]//h3[contains(text(),'%s')]", universityName)
            };

            for (String xpath : xpaths) {
                try {
                    List<WebElement> elements = driver.findElements(By.xpath(xpath));
                    for (WebElement element : elements) {
                        if (element.isDisplayed()) {
                            String text = element.getText();
                            if (text != null && text.contains(universityName)) {
                                return true;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Continue to next xpath
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Waits until a university card disappears from the list.
     * Useful for delete flows where the toast can appear before the UI refreshes.
     */
    /**
     * Waits until a university name appears in the list (after create/update and list refresh).
     */
    public boolean waitForUniversityInList(String universityName, int timeoutSeconds) {
        logger.debug("Waiting up to {}s for university '{}' in list", timeoutSeconds, universityName);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(d -> isUniversityPresentWithoutWait(universityName));
    }

    public boolean waitForUniversityNotInList(String universityName) {
        logger.debug("Waiting for university '{}' to disappear from list", universityName);
        int timeout = Math.max(45, ConfigReader.getExplicitWait() * 2);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(d -> !isUniversityPresentWithoutWait(universityName));
    }

    /**
     * Checks if university does not exist in the list
     *
     * @param universityName University name to check
     * @return true if university does not exist in the list
     */
    public boolean isUniversityNotInList(String universityName) {
        logger.debug("Checking if university '{}' does not exist in the list", universityName);
        return !isUniversityInList(universityName);
    }

    /**
     * Verifies that a university exists in the system (for Given steps)
     *
     * @param universityName University name to verify
     * @return true if university exists
     */
    public boolean verifyUniversityExists(String universityName) {
        logger.info("Verifying university '{}' exists in the system", universityName);
        return isUniversityInList(universityName);
    }
}

