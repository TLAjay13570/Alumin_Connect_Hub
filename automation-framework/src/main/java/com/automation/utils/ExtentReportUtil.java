package com.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for Extent Reports
 */
public class ExtentReportUtil {
    private static final Logger logger = LogManager.getLogger(ExtentReportUtil.class);
    private static ExtentReports extentReports;
    private static ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    /**
     * Initializes Extent Reports
     *
     * @return ExtentReports instance
     */
    public static ExtentReports initExtentReports() {
        if (extentReports == null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportName = ConfigReader.getReportName() + "_" + timestamp + ".html";
            String reportPath = ConfigReader.getReportPath();

            // Create directory if it doesn't exist
            File dir = new File(reportPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath + reportName);
            sparkReporter.config().setDocumentTitle("Automation Test Report");
            sparkReporter.config().setReportName("Selenium Cucumber Framework");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Browser", ConfigReader.getBrowser());
            extentReports.setSystemInfo("Base URL", ConfigReader.getBaseUrl());

            logger.info("Extent Reports initialized: {}", reportPath + reportName);
        }
        return extentReports;
    }

    /**
     * Creates a test in Extent Reports
     *
     * @param testName Test name
     * @return ExtentTest instance
     */
    public static ExtentTest createTest(String testName) {
        // Ensure ExtentReports is initialized
        if (extentReports == null) {
            initExtentReports();
        }
        ExtentTest test = extentReports.createTest(testName);
        extentTestThreadLocal.set(test);
        logger.debug("Extent test created: {}", testName);
        return test;
    }

    /**
     * Creates a test in Extent Reports with description
     *
     * @param testName    Test name
     * @param description Test description
     * @return ExtentTest instance
     */
    public static ExtentTest createTest(String testName, String description) {
        // Ensure ExtentReports is initialized
        if (extentReports == null) {
            initExtentReports();
        }
        ExtentTest test = extentReports.createTest(testName, description);
        extentTestThreadLocal.set(test);
        logger.debug("Extent test created: {} - {}", testName, description);
        return test;
    }

    /**
     * Gets current ExtentTest instance
     *
     * @return ExtentTest instance
     */
    public static ExtentTest getTest() {
        return extentTestThreadLocal.get();
    }

    /**
     * Logs info message
     *
     * @param message Message to log
     */
    public static void logInfo(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.INFO, message);
            logger.info(message);
        }
    }

    /**
     * Logs pass message
     *
     * @param message Message to log
     */
    public static void logPass(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.PASS, MarkupHelper.createLabel(message, ExtentColor.GREEN));
            logger.info("PASS: {}", message);
        }
    }

    /**
     * Logs fail message
     *
     * @param message Message to log
     */
    public static void logFail(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
            logger.error("FAIL: {}", message);
        }
    }

    /**
     * Logs skip message
     *
     * @param message Message to log
     */
    public static void logSkip(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.SKIP, MarkupHelper.createLabel(message, ExtentColor.ORANGE));
            logger.warn("SKIP: {}", message);
        }
    }

    /**
     * Logs warning message
     *
     * @param message Message to log
     */
    public static void logWarning(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.WARNING, MarkupHelper.createLabel(message, ExtentColor.YELLOW));
            logger.warn(message);
        }
    }

    /**
     * Adds screenshot to report
     *
     * @param screenshotPath Path to screenshot
     */
    public static void addScreenshot(String screenshotPath) {
        ExtentTest test = getTest();
        if (test != null && screenshotPath != null) {
            try {
                test.addScreenCaptureFromPath(screenshotPath);
                logger.debug("Screenshot added to report: {}", screenshotPath);
            } catch (Exception e) {
                logger.error("Error adding screenshot to report: {}", e.getMessage());
            }
        }
    }

    /**
     * Adds screenshot to report with title
     *
     * @param screenshotPath Path to screenshot
     * @param title          Screenshot title
     */
    public static void addScreenshot(String screenshotPath, String title) {
        ExtentTest test = getTest();
        if (test != null && screenshotPath != null) {
            try {
                test.addScreenCaptureFromPath(screenshotPath, title);
                logger.debug("Screenshot added to report with title: {} - {}", title, screenshotPath);
            } catch (Exception e) {
                logger.error("Error adding screenshot to report: {}", e.getMessage());
            }
        }
    }

    /**
     * Flushes Extent Reports
     */
    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("Extent Reports flushed");
        }
    }

    /**
     * Removes current test from thread local
     */
    public static void removeTest() {
        extentTestThreadLocal.remove();
    }
}

