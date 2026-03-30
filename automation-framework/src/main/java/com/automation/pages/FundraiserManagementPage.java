package com.automation.pages;

import com.automation.utils.ConfigReader;
import com.automation.utils.WebDriverWaitUtil;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for admin Fundraiser Management ({@code /admin/fundraiser}).
 */
public class FundraiserManagementPage extends BasePage {

    private static final By PAGE_HEADING = By.xpath(
            "//h1[contains(normalize-space(.),'Fundraiser Management')]");
    private static final By SECTION_HEADING = By.xpath(
            "//h2[contains(normalize-space(.),'Fundraiser Management')]");
    /** Opens the modal; must not match the in-dialog submit button (first matching trigger in page). */
    private static final By BTN_OPEN_CREATE_DIALOG = By.xpath(
            "//div[contains(@class,'p-6')][.//h2[contains(normalize-space(.),'Fundraiser Management')]]"
                    + "//button[contains(normalize-space(.),'Create Fundraiser')][.//*[local-name()='svg']]");
    private static final By DIALOG = By.xpath("//div[@role='dialog']");
    private static final By INPUT_TITLE = By.id("title");
    private static final By INPUT_DESCRIPTION = By.id("description");
    private static final By INPUT_IMAGE = By.id("image");
    private static final By INPUT_GOAL = By.id("goal");
    private static final By INPUT_CURRENT = By.id("current");
    private static final By INPUT_LINK = By.id("link");
    private static final By INPUT_START = By.id("start");
    private static final By INPUT_END = By.id("end");
    private static final By STAT_TOTAL_LABEL = By.xpath(
            "//p[contains(normalize-space(.),'Total Fundraisers')]");

    public void navigateToFundraiserManagement() {
        navigateTo(ConfigReader.getAdminFundraiserUrl());
        waitForFundraiserShell();
    }

    public void waitForFundraiserShell() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(Math.max(25, ConfigReader.getExplicitWait())));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING),
                ExpectedConditions.visibilityOfElementLocated(SECTION_HEADING)));
    }

    public boolean isOnFundraiserManagementPage() {
        return driver.findElements(PAGE_HEADING).stream().anyMatch(WebElement::isDisplayed)
                || driver.findElements(SECTION_HEADING).stream().anyMatch(WebElement::isDisplayed);
    }

    public void clickOpenCreateFundraiserDialog() {
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(BTN_OPEN_CREATE_DIALOG));
        jsScrollIntoView(btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.visibilityOfElementLocated(DIALOG));
    }

    public void waitForDialog() {
        WebDriverWaitUtil.waitForElementVisible(DIALOG);
    }

    public void waitForDialogClosed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.invisibilityOfElementLocated(DIALOG));
        } catch (TimeoutException ignored) {
        }
    }

    public void enterTitle(String title) {
        WebElement el = scrollToDialogField(INPUT_TITLE);
        el.clear();
        el.sendKeys(title);
    }

    public void enterDescription(String text) {
        WebElement el = scrollToDialogField(INPUT_DESCRIPTION);
        el.clear();
        el.sendKeys(text);
    }

    public void enterImageUrl(String url) {
        WebElement el = scrollToDialogField(INPUT_IMAGE);
        el.clear();
        el.sendKeys(url);
    }

    public void enterGoalAmount(String amount) {
        WebElement el = scrollToDialogField(INPUT_GOAL);
        el.clear();
        el.sendKeys(amount);
    }

    public void enterCurrentAmount(String amount) {
        WebElement el = scrollToDialogField(INPUT_CURRENT);
        el.clear();
        el.sendKeys(amount);
    }

    public void enterDonationLink(String url) {
        WebElement el = scrollToDialogField(INPUT_LINK);
        el.clear();
        el.sendKeys(url);
    }

    public void enterStartDate(String yyyyMmDd) {
        WebElement el = scrollToDialogField(INPUT_START);
        el.clear();
        el.sendKeys(yyyyMmDd);
    }

    public void enterEndDate(String yyyyMmDd) {
        WebElement el = scrollToDialogField(INPUT_END);
        el.clear();
        el.sendKeys(yyyyMmDd);
    }

    public void clearTitleField() {
        WebElement el = scrollToDialogField(INPUT_TITLE);
        el.clear();
    }

    public void clearGoalField() {
        WebElement el = scrollToDialogField(INPUT_GOAL);
        el.clear();
    }

    /**
     * The dialog uses {@code max-h-[90vh] overflow-y-auto} so fields below the fold
     * aren't "visible" to Selenium. Wait for presence, then scroll inside the dialog.
     */
    private WebElement scrollToDialogField(By locator) {
        WebElement el = new WebDriverWait(driver, Duration.ofSeconds(Math.max(15, ConfigReader.getExplicitWait())))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', behavior:'instant'});", el);
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOf(el));
        return el;
    }

    public void submitFundraiserDialog(boolean editing) {
        By submit = editing
                ? By.xpath("//div[@role='dialog']//button[contains(normalize-space(.),'Update Fundraiser')]")
                : By.xpath("//div[@role='dialog']//button[contains(normalize-space(.),'Create Fundraiser')]");
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.presenceOfElementLocated(submit));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', behavior:'instant'});", btn);
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(btn));
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public boolean isCardWithTitleVisible(String title) {
        By h3 = By.xpath("//h3[contains(normalize-space(.)," + xpathLiteral(title) + ")]");
        return driver.findElements(h3).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isBadgeVisibleNearTitle(String title, String badgeText) {
        String lit = xpathLiteral(title);
        String badgeLit = xpathLiteral(badgeText);
        By badge = By.xpath("//h3[contains(normalize-space(.)," + lit + ")]"
                + "/ancestor::div[contains(@class,'p-6')][1]"
                + "//*[self::div or self::span][contains(normalize-space(.)," + badgeLit + ")]");
        return driver.findElements(badge).stream().anyMatch(WebElement::isDisplayed);
    }

    public void clickEditOnCardWithTitle(String title) {
        WebElement edit = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//h3[contains(normalize-space(.)," + xpathLiteral(title) + ")]"
                                + "/ancestor::div[contains(@class,'p-6')][1]"
                                + "//button[contains(normalize-space(.),'Edit')]")));
        jsScrollIntoView(edit);
        try {
            edit.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", edit);
        }
        waitForDialog();
    }

    public void clickDisableOnCardWithTitle(String title) {
        clickToggleOnCard(title, "Disable");
    }

    public void clickEnableOnCardWithTitle(String title) {
        clickToggleOnCard(title, "Enable");
    }

    private void clickToggleOnCard(String title, String label) {
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//h3[contains(normalize-space(.)," + xpathLiteral(title) + ")]"
                                + "/ancestor::div[contains(@class,'p-6')][1]"
                                + "//button[contains(normalize-space(.),'" + label + "')]")));
        jsScrollIntoView(btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public void clickDeleteOnCardWithTitle(String title) {
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//h3[contains(normalize-space(.)," + xpathLiteral(title) + ")]"
                                + "/ancestor::div[contains(@class,'p-6')][1]"
                                + "//button[contains(@class,'text-destructive')]")));
        jsScrollIntoView(btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    /**
     * Confirms native {@code window.confirm} from delete.
     */
    public void acceptBrowserConfirmIfPresent(int waitSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitSeconds));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (TimeoutException ignored) {
        }
    }

    public boolean statisticsSectionVisible() {
        try {
            WebElement stat = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.presenceOfElementLocated(STAT_TOTAL_LABEL));
            jsScrollIntoView(stat);
            return stat.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void jsScrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", el);
    }

    private static String xpathLiteral(String s) {
        if (s == null) {
            return "''";
        }
        if (!s.contains("'")) {
            return "'" + s + "'";
        }
        if (!s.contains("\"")) {
            return "\"" + s + "\"";
        }
        String[] parts = s.split("'");
        StringBuilder sb = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(", \"'\", ");
            }
            sb.append("'").append(parts[i].replace("\\", "\\\\")).append("'");
        }
        sb.append(")");
        return sb.toString();
    }
}
