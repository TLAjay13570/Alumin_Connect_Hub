package com.automation.factory;

import com.automation.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;

/**
 * Driver Factory class to create and manage WebDriver instances
 * Supports multiple browsers and thread-safe driver management
 */
public class DriverFactory {
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Initializes and returns WebDriver instance based on configuration
     *
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            String browser = ConfigReader.getBrowser();
            logger.info("Initializing {} browser driver", browser);

            WebDriver driver = createDriver(browser);
            configureDriver(driver);
            driverThreadLocal.set(driver);
        }
        return driverThreadLocal.get();
    }

    /**
     * Creates WebDriver instance based on browser type
     *
     * @param browser Browser name
     * @return WebDriver instance
     */
    private static WebDriver createDriver(String browser) {
        WebDriver driver;

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (ConfigReader.isHeadless()) {
                    chromeOptions.addArguments("--headless");
                    chromeOptions.addArguments("--disable-gpu");
                }
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.addArguments("--disable-infobars");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.NORMAL);
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (ConfigReader.isHeadless()) {
                    firefoxOptions.addArguments("--headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (ConfigReader.isHeadless()) {
                    edgeOptions.addArguments("--headless");
                }
                edgeOptions.addArguments("--start-maximized");
                driver = new EdgeDriver(edgeOptions);
                break;

            case "safari":
                driver = new SafariDriver();
                break;

            default:
                logger.error("Unsupported browser: {}", browser);
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        logger.info("{} browser driver created successfully", browser);
        return driver;
    }

    /**
     * Configures WebDriver with timeouts and window settings
     *
     * @param driver WebDriver instance
     */
    private static void configureDriver(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        driver.manage().window().maximize();
        logger.info("Driver configured with timeouts and window settings");
    }

    /**
     * Quits the current WebDriver instance
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("Driver closed successfully");
            } catch (Exception e) {
                logger.error("Error closing driver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Gets the current WebDriver instance
     *
     * @return WebDriver instance
     */
    public static WebDriver getCurrentDriver() {
        return driverThreadLocal.get();
    }
}

