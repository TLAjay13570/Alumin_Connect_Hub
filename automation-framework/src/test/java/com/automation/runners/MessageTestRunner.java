package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Runs all Message / Chat module scenarios.
 *
 * <p>Run from CLI:
 * <pre>
 *   mvn test "-Dtest=com.automation.runners.MessageTestRunner"
 * </pre>
 *
 * <p>Run only smoke tests:
 * <pre>
 *   mvn test "-Dtest=com.automation.runners.MessageTestRunner" "-Dcucumber.filter.tags=@Smoke"
 * </pre>
 */
@CucumberOptions(
        features = {
                "src/test/resources/features/Message.feature"
        },
        glue = {"com.automation.stepdefinitions", "com.automation.hooks"},
        plugin = {
                "pretty",
                "html:test-output/cucumber-reports/message.html",
                "json:test-output/cucumber-reports/message.json"
        },
        monochrome = true,
        tags = "@Message"
)
public class MessageTestRunner extends AbstractTestNGCucumberTests {
}
