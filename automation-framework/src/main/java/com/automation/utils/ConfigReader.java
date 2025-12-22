package com.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read configuration properties from config.properties file
 */
public class ConfigReader {
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "config.properties";

    static {
        loadProperties();
    }

    /**
     * Loads properties from config.properties file
     */
    private static void loadProperties() {
        properties = new Properties();
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_PATH)) {
            if (inputStream == null) {
                logger.error("Configuration file not found: {}", CONFIG_FILE_PATH);
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE_PATH);
            }
            properties.load(inputStream);
            logger.info("Configuration properties loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading configuration file: {}", e.getMessage());
            throw new RuntimeException("Failed to load configuration file", e);
        }
    }

    /**
     * Gets property value by key
     *
     * @param key Property key
     * @return Property value
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property '{}' not found in configuration file", key);
        }
        return value;
    }

    /**
     * Gets property value with default value if key not found
     *
     * @param key          Property key
     * @param defaultValue Default value
     * @return Property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Gets browser name from config
     *
     * @return Browser name
     */
    public static String getBrowser() {
        return getProperty("browser", "chrome").toLowerCase();
    }

    /**
     * Gets base URL from config
     *
     * @return Base URL
     */
    public static String getBaseUrl() {
        return getProperty("base.url");
    }

    /**
     * Gets login URL from config
     *
     * @return Login URL
     */
    public static String getLoginUrl() {
        return getProperty("login.url");
    }

    /**
     * Gets username from config
     *
     * @return Username
     */
    public static String getUsername() {
        return getProperty("username");
    }

    /**
     * Gets password from config
     *
     * @return Password
     */
    public static String getPassword() {
        return getProperty("password");
    }

    /**
     * Gets super admin username from config
     *
     * @return Super admin username
     */
    public static String getSuperAdminUsername() {
        return getProperty("superadmin.username");
    }

    /**
     * Gets super admin password from config
     *
     * @return Super admin password
     */
    public static String getSuperAdminPassword() {
        return getProperty("superadmin.password");
    }

    /**
     * Gets implicit wait time from config
     *
     * @return Implicit wait time in seconds
     */
    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait", "10"));
    }

    /**
     * Gets explicit wait time from config
     *
     * @return Explicit wait time in seconds
     */
    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait", "20"));
    }

    /**
     * Gets page load timeout from config
     *
     * @return Page load timeout in seconds
     */
    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout", "30"));
    }

    /**
     * Checks if headless mode is enabled
     *
     * @return true if headless mode is enabled
     */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    /**
     * Gets screenshot path from config
     *
     * @return Screenshot path
     */
    public static String getScreenshotPath() {
        return getProperty("screenshot.path", "test-output/screenshots/");
    }

    /**
     * Gets report path from config
     *
     * @return Report path
     */
    public static String getReportPath() {
        return getProperty("report.path", "test-output/reports/");
    }

    /**
     * Gets report name from config
     *
     * @return Report name
     */
    public static String getReportName() {
        return getProperty("report.name", "Automation_Report");
    }
}

