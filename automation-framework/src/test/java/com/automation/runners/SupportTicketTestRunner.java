package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Runs all Support Ticket scenarios: lifecycle, admin management, validation, and access control.
 *
 * <p>Run from CLI:
 * <pre>
 *   mvn test "-Dtest=com.automation.runners.SupportTicketTestRunner"
 * </pre>
 *
 * <p>Run only smoke tests:
 * <pre>
 *   mvn test "-Dtest=com.automation.runners.SupportTicketTestRunner" "-Dcucumber.filter.tags=@Smoke"
 * </pre>
 */
@CucumberOptions(
        features = {
                "src/test/resources/features/SupportTicket.feature",
                "src/test/resources/features/SupportTicketValidation.feature"
        },
        glue = {"com.automation.stepdefinitions", "com.automation.hooks"},
        plugin = {
                "pretty",
                "html:test-output/cucumber-reports/support-ticket.html",
                "json:test-output/cucumber-reports/support-ticket.json"
        },
        monochrome = true,
        tags = "@SupportTicket"
)
public class SupportTicketTestRunner extends AbstractTestNGCucumberTests {
}
