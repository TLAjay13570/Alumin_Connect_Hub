package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * TestNG Test Runner for Cucumber tests
 * Supports parallel execution
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.automation.stepdefinitions", "com.automation.hooks"},
        plugin = {
                "pretty",
                "html:test-output/cucumber-reports/cucumber.html",
                "json:test-output/cucumber-reports/cucumber.json",
                "junit:test-output/cucumber-reports/cucumber.xml"
        },
        monochrome = true,
        dryRun = false,
        tags = "@Login"
)
public class TestRunner extends AbstractTestNGCucumberTests {

    /**
     * Override to enable parallel execution
     * Set parallel = true in testng.xml to enable parallel execution
     */
    // Parallel execution is controlled via testng.xml
}

