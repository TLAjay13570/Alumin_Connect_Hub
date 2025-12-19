package com.automation.hooks;

import com.automation.factory.DriverFactory;
import com.automation.utils.ExtentReportUtil;
import com.automation.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber Hooks for setup and teardown operations
 */
public class Hooks {
    private static final Logger logger = LogManager.getLogger(Hooks.class);

    /**
     * Before hook - runs before each scenario
     *
     * @param scenario Cucumber scenario
     */
    @Before
    public void setUp(Scenario scenario) {
        logger.info("=========================================");
        logger.info("Starting Scenario: {}", scenario.getName());
        logger.info("=========================================");

        // Initialize Extent Reports if not already initialized
        ExtentReportUtil.initExtentReports();

        // Create test in Extent Reports
        ExtentReportUtil.createTest(scenario.getName(), scenario.getId());

        // Initialize WebDriver
        DriverFactory.getDriver();
        logger.info("WebDriver initialized");
    }

    /**
     * After hook - runs after each scenario
     *
     * @param scenario Cucumber scenario
     */
    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverFactory.getCurrentDriver();

        // Take screenshot on failure
        if (scenario.isFailed() && driver != null) {
            logger.error("Scenario failed: {}", scenario.getName());

            try {
                // Take screenshot
                String screenshotPath = ScreenshotUtil.takeScreenshot("FAILED_" + scenario.getName().replaceAll(" ", "_"));
                
                // Add screenshot to Extent Reports
                ExtentReportUtil.logFail("Scenario failed: " + scenario.getName());
                ExtentReportUtil.addScreenshot(screenshotPath, "Failure Screenshot");

                // Embed screenshot in Cucumber report
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot on Failure");
            } catch (Exception e) {
                logger.error("Error taking screenshot: {}", e.getMessage());
            }
        } else if (driver != null) {
            logger.info("Scenario passed: {}", scenario.getName());
            ExtentReportUtil.logPass("Scenario passed: " + scenario.getName());
        }

        // Quit driver
        DriverFactory.quitDriver();
        logger.info("WebDriver closed");

        // Remove test from thread local
        ExtentReportUtil.removeTest();

        logger.info("=========================================");
        logger.info("Completed Scenario: {}", scenario.getName());
        logger.info("Status: {}", scenario.getStatus());
        logger.info("=========================================");
    }

    /**
     * After hook with order 0 - runs last to flush reports
     * This will flush reports after all scenarios complete
     */
    @After(order = 0)
    public void flushReports() {
        // Flush Extent Reports after all scenarios
        ExtentReportUtil.flushReports();
        logger.info("All scenarios completed. Reports flushed.");
    }
}

