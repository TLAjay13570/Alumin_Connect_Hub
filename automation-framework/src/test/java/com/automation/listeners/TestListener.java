package com.automation.listeners;

import com.automation.utils.ExtentReportUtil;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG Test Listener for additional reporting
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        ExtentReportUtil.createTest(result.getMethod().getMethodName(),
                result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportUtil.logPass("Test passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentReportUtil.logFail("Test failed: " + result.getMethod().getMethodName());
        if (result.getThrowable() != null) {
            ExtentReportUtil.logFail("Error: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportUtil.logSkip("Test skipped: " + result.getMethod().getMethodName());
    }
}





