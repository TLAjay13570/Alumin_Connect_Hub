package com.automation.utils;

import com.automation.factory.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for taking screenshots
 */
public class ScreenshotUtil {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtil.class);

    /**
     * Takes screenshot and saves to file
     *
     * @param screenshotName Name of the screenshot file
     * @return Path to the screenshot file
     */
    public static String takeScreenshot(String screenshotName) {
        WebDriver driver = DriverFactory.getCurrentDriver();
        String screenshotPath = null;

        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = screenshotName + "_" + timestamp + ".png";
            String directory = ConfigReader.getScreenshotPath();

            // Create directory if it doesn't exist
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(directory + fileName);
            FileUtils.copyFile(screenshotFile, destinationFile);

            screenshotPath = destinationFile.getAbsolutePath();
            logger.info("Screenshot saved: {}", screenshotPath);
        } catch (IOException e) {
            logger.error("Error taking screenshot: {}", e.getMessage());
        }

        return screenshotPath;
    }

    /**
     * Takes screenshot as base64 string
     *
     * @return Base64 string of screenshot
     */
    public static String takeScreenshotAsBase64() {
        WebDriver driver = DriverFactory.getCurrentDriver();
        logger.debug("Taking screenshot as base64");
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    /**
     * Takes screenshot as byte array
     *
     * @return Byte array of screenshot
     */
    public static byte[] takeScreenshotAsBytes() {
        WebDriver driver = DriverFactory.getCurrentDriver();
        logger.debug("Taking screenshot as bytes");
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}






