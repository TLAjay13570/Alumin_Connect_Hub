package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Runs Fundraiser Management scenarios (admin CRUD, validation, alumni/guest access).
 *
 * <p>PowerShell: {@code mvn test "-Dtest=com.automation.runners.FundraiserTestRunner"}
 */
@CucumberOptions(
        features = {
                "src/test/resources/features/FundraiserManagement.feature",
                "src/test/resources/features/FundraiserManagementValidation.feature"
        },
        glue = {"com.automation.stepdefinitions", "com.automation.hooks"},
        plugin = {
                "pretty",
                "html:test-output/cucumber-reports/fundraiser-management.html",
                "json:test-output/cucumber-reports/fundraiser-management.json"
        },
        monochrome = true,
        tags = "@Fundraiser"
)
public class FundraiserTestRunner extends AbstractTestNGCucumberTests {
}
