package com.automation.stepdefinitions;

import com.automation.factory.DriverFactory;
import com.automation.pages.DirectoryPage;
import com.automation.pages.DirectoryPage.MemberCardDetails;
import com.automation.utils.ExtentReportUtil;
import com.automation.utils.WebDriverWaitUtil;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.List;

/**
 * Step definitions for the Alumni Directory (Connections) module.
 *
 * <p>Reuses login steps from {@link LoginStepDefinitions} via Cucumber's shared
 * step-definition glue — no duplicate login code here.
 */
public class DirectoryStepDefinitions {

    private static final Logger logger = LogManager.getLogger(DirectoryStepDefinitions.class);

    private DirectoryPage directoryPage;
    private MemberCardDetails capturedMember;

    private DirectoryPage dp() {
        if (directoryPage == null) {
            directoryPage = new DirectoryPage();
        }
        return directoryPage;
    }

    // ───────────────────────── Navigation ────────────────────────────────────

    @And("I navigate to the directory page")
    public void i_navigate_to_the_directory_page() {
        logger.info("Navigating to directory page");
        ExtentReportUtil.logInfo("Navigating to directory page");
        dp().navigateToDirectoryPage();
    }

    @Then("the directory page header should be {string}")
    public void the_directory_page_header_should_be(String expected) {
        String actual = dp().getHeaderText();
        logger.info("Directory header — expected: '{}', actual: '{}'", expected, actual);
        Assert.assertEquals(actual, expected,
                "Directory page header text mismatch");
        ExtentReportUtil.logPass("Directory header is '" + expected + "'");
    }

    @And("the directory search input should be visible")
    public void the_directory_search_input_should_be_visible() {
        Assert.assertTrue(dp().isSearchInputVisible(),
                "Directory search input is not visible");
        ExtentReportUtil.logPass("Directory search input is visible");
    }

    @And("the directory should show the following tabs:")
    public void the_directory_should_show_the_following_tabs(DataTable table) {
        List<String> tabs = table.asList();
        for (String tab : tabs) {
            Assert.assertTrue(dp().isTabVisible(tab),
                    "Directory tab not visible: " + tab);
        }
        ExtentReportUtil.logPass("All expected directory tabs are visible: " + tabs);
    }

    // ───────────────────────── Tabs / Filters ────────────────────────────────

    @When("I select the {string} tab on directory")
    public void i_select_the_tab_on_directory(String tab) {
        logger.info("Selecting directory tab: {}", tab);
        ExtentReportUtil.logInfo("Selecting directory tab: " + tab);
        dp().selectTab(tab);
    }

    @Then("the {string} tab panel should be visible")
    public void the_tab_panel_should_be_visible(String tab) {
        Assert.assertTrue(dp().isActiveTabPanelVisible(),
                "No active tab panel is visible after selecting '" + tab + "'");
        ExtentReportUtil.logPass("Tab panel '" + tab + "' is visible");
    }

    // ───────────────────────── List view ─────────────────────────────────────

    @Then("the directory should display at least {int} member card")
    public void the_directory_should_display_at_least_member_card(int min) {
        int count = dp().getMemberCardCount();
        logger.info("Directory member card count: {}", count);
        Assert.assertTrue(count >= min,
                "Expected at least " + min + " member card(s), found " + count);
        ExtentReportUtil.logPass("Directory shows " + count + " member card(s)");
    }

    @And("each directory member card should show name, job title and company")
    public void each_directory_member_card_should_show_name_job_title_and_company() {
        Assert.assertTrue(dp().allCardsHaveRequiredFields(),
                "One or more member cards are missing required fields (name / job / company)");
        ExtentReportUtil.logPass("All member cards expose name, job title and company");
    }

    // ───────────────────────── Search ────────────────────────────────────────

    @When("I search the directory for {string}")
    public void i_search_the_directory_for(String query) {
        ExtentReportUtil.logInfo("Searching directory for: " + query);
        dp().searchFor(query);
    }

    @Then("the directory should display only members matching {string}")
    public void the_directory_should_display_only_members_matching(String query) {
        Assert.assertTrue(dp().allCardsContain(query),
                "At least one displayed card does not contain '" + query + "'");
        ExtentReportUtil.logPass("All visible cards contain: " + query);
    }

    @And("the directory member count should be greater than {int}")
    public void the_directory_member_count_should_be_greater_than(int min) {
        int count = dp().getMemberCardCount();
        Assert.assertTrue(count > min,
                "Expected > " + min + " cards after search, found " + count);
        ExtentReportUtil.logPass("Filtered directory has " + count + " card(s)");
    }

    @Then("the directory should show the no-results empty state")
    public void the_directory_should_show_the_no_results_empty_state() {
        Assert.assertTrue(dp().isNoResultsDisplayed(),
                "Expected no-results empty state but it was not displayed");
        Assert.assertEquals(dp().getMemberCardCount(), 0,
                "Member cards should not be visible when search returns no results");
        ExtentReportUtil.logPass("Directory shows the no-results empty state");
    }

    // ───────────────────────── Profile view ──────────────────────────────────

    @When("I open the first member on the directory")
    public void i_open_the_first_member_on_the_directory() {
        capturedMember = dp().openFirstMember();
        ExtentReportUtil.logInfo("Opened first directory member: " + capturedMember.name);
    }

    @Then("the member's name, job title, company, university and year should be displayed")
    public void the_members_details_should_be_displayed() {
        Assert.assertNotNull(capturedMember,
                "No member was captured — did the 'I open the first member' step run?");
        Assert.assertTrue(capturedMember.isComplete(),
                "Member details are incomplete: " + String.format(
                        "name='%s' jobTitle='%s' company='%s' university='%s' year='%s'",
                        capturedMember.name, capturedMember.jobTitle, capturedMember.company,
                        capturedMember.university, capturedMember.year));
        ExtentReportUtil.logPass("Member details are fully displayed");
    }

    // ───────────────────────── Message / chat navigation ─────────────────────

    @When("I click the Message button on the first directory member")
    public void i_click_the_message_button_on_the_first_directory_member() {
        ExtentReportUtil.logInfo("Clicking Message on first directory member");
        dp().clickMessageOnFirstMember();
    }

    @When("I click the View Profile button on the first directory member")
    public void i_click_the_view_profile_button_on_the_first_directory_member() {
        ExtentReportUtil.logInfo("Clicking View Profile on first directory member");
        dp().clickViewProfileOnFirstMember();
    }

    @Then("I should be navigated away from the directory page")
    public void i_should_be_navigated_away_from_the_directory_page() {
        // Verify we leave /directory — exact destination (e.g. /profile/:id, modal
        // route) varies by build, so we only assert the URL changed off the list.
        boolean leftDirectory = WebDriverWaitUtil.waitForUrlToNotContain("/directory");
        Assert.assertTrue(leftDirectory,
                "Expected to navigate away from /directory after clicking View Profile, "
                        + "actual URL: " + DriverFactory.getCurrentDriver().getCurrentUrl());
        ExtentReportUtil.logPass("Navigated away from directory page");
    }

    @Then("I should be navigated to the chat page")
    public void i_should_be_navigated_to_the_chat_page() {
        boolean onChat = WebDriverWaitUtil.waitForUrlToContain("/chat");
        Assert.assertTrue(onChat,
                "Expected to land on /chat, actual URL: "
                        + DriverFactory.getCurrentDriver().getCurrentUrl());
        ExtentReportUtil.logPass("Navigated to chat page");
    }

    // ───────────────────────── Global map ────────────────────────────────────

    @Then("the global alumni distribution map should be visible")
    public void the_global_alumni_distribution_map_should_be_visible() {
        Assert.assertTrue(dp().isGlobalMapVisible(),
                "Global alumni distribution map is not visible on the map tab");
        ExtentReportUtil.logPass("Global alumni distribution map is visible");
    }
}
