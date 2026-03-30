package com.automation.stepdefinitions;

import com.automation.factory.DriverFactory;
import com.automation.pages.FundraiserManagementPage;
import com.automation.pages.LoginPage;
import com.automation.utils.ConfigReader;
import com.automation.utils.ExtentReportUtil;
import com.automation.utils.ToastUtil;
import com.automation.utils.WebDriverWaitUtil;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * BDD glue for {@code FundraiserManagement.feature} and {@code FundraiserManagementValidation.feature}.
 */
public class FundraiserStepDefinitions {
    private static final Logger logger = LogManager.getLogger(FundraiserStepDefinitions.class);
    private static final ThreadLocal<String> LAST_FUNDRAISER_TITLE = new ThreadLocal<>();

    private FundraiserManagementPage fundraiserPage;

    private FundraiserManagementPage fp() {
        if (fundraiserPage == null) {
            fundraiserPage = new FundraiserManagementPage();
        }
        return fundraiserPage;
    }

    @After
    public void clearFundraiserThreadLocals() {
        LAST_FUNDRAISER_TITLE.remove();
    }

    public static String lastFundraiserTitleOrFail() {
        String t = LAST_FUNDRAISER_TITLE.get();
        Assert.assertNotNull(t, "No fundraiser title captured in this scenario");
        return t;
    }

    @When("I navigate to the admin fundraiser management page")
    public void navigate_admin_fundraiser() {
        ExtentReportUtil.logInfo("Navigate to admin fundraiser management");
        fp().navigateToFundraiserManagement();
    }

    @Then("I should see the fundraiser management page")
    public void assert_fundraiser_page() {
        Assert.assertTrue(fp().isOnFundraiserManagementPage(), "Fundraiser management page not loaded");
    }

    @And("the fundraiser statistics section should be visible")
    public void assert_stats() {
        Assert.assertTrue(fp().statisticsSectionVisible(), "Statistics (Total Fundraisers) not visible");
    }

    @When("I open the create fundraiser dialog")
    public void open_create_dialog() {
        fp().clickOpenCreateFundraiserDialog();
        fp().waitForDialog();
    }

    @When("I enter fundraiser campaign title {string}")
    public void enter_title(String title) {
        String resolved = title.contains("UNIQUE")
                ? title.replace("UNIQUE", UUID.randomUUID().toString().substring(0, 8))
                : title;
        LAST_FUNDRAISER_TITLE.set(resolved);
        fp().enterTitle(resolved);
    }

    @And("I enter fundraiser description {string}")
    public void enter_description(String text) {
        fp().enterDescription(text);
    }

    @And("I enter fundraiser goal amount {string}")
    public void enter_goal(String g) {
        fp().enterGoalAmount(g);
    }

    @And("I enter fundraiser current amount {string}")
    public void enter_current(String c) {
        fp().enterCurrentAmount(c);
    }

    @And("I enter fundraiser donation link {string}")
    public void enter_link(String url) {
        fp().enterDonationLink(url);
    }

    @And("I enter fundraiser image URL {string}")
    public void enter_image(String url) {
        fp().enterImageUrl(url);
    }

    @And("I set fundraiser dates to active campaign range")
    public void set_dates_active() {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        String start = LocalDate.now().minusDays(1).format(fmt);
        String end = LocalDate.now().plusYears(1).format(fmt);
        fp().enterStartDate(start);
        fp().enterEndDate(end);
    }

    @And("I submit the fundraiser create form")
    public void submit_create() {
        fp().submitFundraiserDialog(false);
        fp().waitForDialogClosed();
    }

    @And("I attempt to submit the fundraiser create form")
    public void submit_create_validation_attempt() {
        fp().submitFundraiserDialog(false);
    }

    @And("I submit the fundraiser update form")
    public void submit_update() {
        fp().submitFundraiserDialog(true);
        fp().waitForDialogClosed();
    }

    @Then("I should see a fundraiser toast containing {string}")
    public void toast_contains(String phrase) {
        try {
            String msg = ToastUtil.waitForToastContaining(phrase);
            Assert.assertTrue(msg.toLowerCase().contains(phrase.toLowerCase()),
                    "Toast missing phrase: " + phrase + " got: " + msg);
        } catch (TimeoutException e) {
            Assert.fail("No toast containing: " + phrase);
        }
    }

    @Then("the fundraiser list should show a card with my tracked title")
    public void card_tracked_title() {
        String t = lastFundraiserTitleOrFail();
        WebDriverWait wait = new WebDriverWait(DriverFactory.getCurrentDriver(),
                java.time.Duration.ofSeconds(Math.max(20, ConfigReader.getExplicitWait())));
        wait.until(d -> fp().isCardWithTitleVisible(t));
        Assert.assertTrue(fp().isCardWithTitleVisible(t), "Card not found for title: " + t);
    }

    @And("I open edit fundraiser for my tracked title")
    public void edit_tracked() {
        fp().clickEditOnCardWithTitle(lastFundraiserTitleOrFail());
    }

    @And("I change fundraiser description to {string}")
    public void change_description(String text) {
        fp().enterDescription(text);
    }

    @When("I disable the fundraiser with my tracked title")
    public void disable_tracked() {
        fp().clickDisableOnCardWithTitle(lastFundraiserTitleOrFail());
    }

    @When("I enable the fundraiser with my tracked title")
    public void enable_tracked() {
        fp().clickEnableOnCardWithTitle(lastFundraiserTitleOrFail());
    }

    @Then("the fundraiser card should show badge text {string}")
    public void badge_visible(String badge) {
        String t = lastFundraiserTitleOrFail();
        WebDriverWait wait = new WebDriverWait(DriverFactory.getCurrentDriver(),
                java.time.Duration.ofSeconds(15));
        wait.until(d -> fp().isBadgeVisibleNearTitle(t, badge));
        Assert.assertTrue(fp().isBadgeVisibleNearTitle(t, badge),
                "Badge '" + badge + "' not visible for " + t);
    }

    @When("I delete the fundraiser with my tracked title")
    public void delete_tracked() {
        fp().clickDeleteOnCardWithTitle(lastFundraiserTitleOrFail());
        fp().acceptBrowserConfirmIfPresent(5);
        WebDriverWaitUtil.waitForPageToLoad();
    }

    @Then("the fundraiser list should not show a card with my tracked title")
    public void card_not_visible() {
        String t = lastFundraiserTitleOrFail();
        WebDriverWait wait = new WebDriverWait(DriverFactory.getCurrentDriver(),
                java.time.Duration.ofSeconds(15));
        wait.until(d -> !fp().isCardWithTitleVisible(t));
        Assert.assertFalse(fp().isCardWithTitleVisible(t), "Card still visible for: " + t);
    }

    @When("I open the admin fundraiser page without authentication")
    public void open_fundraiser_guest() {
        WebDriver d = DriverFactory.getCurrentDriver();
        d.get(ConfigReader.getAdminFundraiserUrl());
        WebDriverWaitUtil.waitForPageToLoad();
        try {
            new WebDriverWait(d, java.time.Duration.ofSeconds(20)).until(drv -> {
                String u = drv.getCurrentUrl().toLowerCase();
                return u.contains("/login") || !u.contains("/admin/fundraiser");
            });
        } catch (TimeoutException ignored) {
        }
    }

    @Then("I should be redirected to the login flow or away from admin fundraiser management")
    public void guest_redirect() {
        WebDriver d = DriverFactory.getCurrentDriver();
        String url = d.getCurrentUrl().toLowerCase();
        boolean ok = url.contains("/login")
                || !url.contains("/admin/fundraiser");
        Assert.assertTrue(ok, "Guest should not stay on admin fundraiser. URL=" + url);
    }

    @When("I navigate directly to the admin fundraiser URL as alumni session")
    public void alumni_hits_fundraiser_admin() {
        WebDriver d = DriverFactory.getCurrentDriver();
        d.get(ConfigReader.getAdminFundraiserUrl());
        WebDriverWaitUtil.waitForPageToLoad();
        try {
            new WebDriverWait(d, java.time.Duration.ofSeconds(15)).until(drv -> {
                String u = drv.getCurrentUrl();
                return u.contains("/dashboard") || u.contains("/login") || !u.contains("/admin/fundraiser");
            });
        } catch (TimeoutException ignored) {
        }
    }

    @Then("I should not remain on the admin fundraiser management page")
    public void alumni_not_on_fundraiser_admin() {
        WebDriver d = DriverFactory.getCurrentDriver();
        String url = d.getCurrentUrl();
        Assert.assertFalse(url.contains("/admin/fundraiser"),
                "Alumni should be redirected off admin fundraiser. URL=" + url);
    }

    @And("I clear the fundraiser title field")
    public void clear_title() {
        fp().clearTitleField();
    }

    @And("I clear the fundraiser goal amount field")
    public void clear_goal() {
        fp().clearGoalField();
    }

    @Given("I am logged in as MIT admin on the fundraiser management page")
    public void given_admin_on_fundraiser() {
        new LoginPage().navigateToLoginPage();
        new LoginPage().loginAsAdmin();
        Assert.assertTrue(new LoginPage().isLoginSuccessful(), "Admin login failed");
        fp().navigateToFundraiserManagement();
    }
}
