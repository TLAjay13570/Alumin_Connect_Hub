# Selenium + Cucumber + Java Automation Framework

A production-ready, scalable automation testing framework built with Selenium WebDriver, Cucumber BDD, Java, and TestNG. This framework follows industry best practices and includes comprehensive utilities for web automation testing.

## 🚀 Features

- **Page Object Model (POM)** - Clean, maintainable page object pattern
- **Cucumber BDD** - Behavior-driven development with Gherkin syntax
- **TestNG Integration** - Test execution with parallel execution support
- **WebDriverManager** - Automatic browser driver management
- **Extent Reports** - Beautiful HTML reports with screenshots
- **Log4j2 Logging** - Comprehensive logging throughout the framework
- **Screenshot on Failure** - Automatic screenshots for failed tests
- **Reusable Utilities** - Waits, Actions, Dropdowns, JavaScript Executor
- **Configuration Management** - Centralized config.properties
- **Thread-Safe** - ThreadLocal for parallel execution support

## 📁 Project Structure

```
automation-framework/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── automation/
│   │   │           ├── factory/
│   │   │           │   └── DriverFactory.java
│   │   │           ├── pages/
│   │   │           │   ├── BasePage.java
│   │   │           │   └── LoginPage.java
│   │   │           └── utils/
│   │   │               ├── ActionsUtil.java
│   │   │               ├── ConfigReader.java
│   │   │               ├── DropdownUtil.java
│   │   │               ├── ExtentReportUtil.java
│   │   │               ├── JavaScriptExecutorUtil.java
│   │   │               ├── ScreenshotUtil.java
│   │   │               └── WebDriverWaitUtil.java
│   │   └── resources/
│   │       ├── config.properties
│   │       └── log4j2.xml
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── automation/
│       │           ├── hooks/
│       │           │   └── Hooks.java
│       │           ├── listeners/
│       │           │   └── TestListener.java
│       │           ├── runners/
│       │           │   └── TestRunner.java
│       │           └── stepdefinitions/
│       │               └── LoginStepDefinitions.java
│       └── resources/
│           ├── features/
│           │   └── Login.feature
│           ├── extent.properties
│           └── spark-config.xml
├── test-output/
│   ├── reports/
│   ├── screenshots/
│   └── logs/
├── pom.xml
├── testng.xml
└── README.md
```

## 🛠️ Prerequisites

- **Java JDK 11 or higher**
- **Maven 3.6+**
- **IDE** (IntelliJ IDEA / Eclipse / VS Code)
- **Internet connection** (for WebDriverManager to download drivers)

## 📦 Installation

1. **Clone or download the project**
   ```bash
   cd automation-framework
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Update configuration**
   - Edit `src/main/resources/config.properties` with your application URLs and credentials

## ⚙️ Configuration

### config.properties

Update the following in `src/main/resources/config.properties`:

```properties
# Browser Configuration
browser=chrome                    # Options: chrome, firefox, edge, safari
headless=false                    # true for headless mode
implicit.wait=10                  # Implicit wait in seconds
explicit.wait=20                  # Explicit wait in seconds
page.load.timeout=30              # Page load timeout in seconds

# Application URLs
base.url=https://your-app-url.com/
login.url=https://your-app-url.com/login

# Credentials
username=your-username
password=your-password

# Screenshot Configuration
screenshot.on.failure=true
screenshot.path=test-output/screenshots/

# Report Configuration
report.path=test-output/reports/
report.name=Automation_Report
```

## 🏃 Running Tests

### Run All Tests

**Using Maven:**
```bash
mvn clean test
```

**Using TestNG XML:**
```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

**Using IDE:**
- Right-click on `testng.xml` → Run As → TestNG Suite
- Or right-click on `TestRunner.java` → Run As → TestNG Test

### Run Specific Tags

Edit `TestRunner.java` and update the `tags` parameter:

```java
tags = "@Smoke"           // Run only Smoke tests
tags = "@Login"           // Run only Login tests
tags = "@Smoke or @Login" // Run Smoke OR Login tests
tags = "@Smoke and @Login" // Run Smoke AND Login tests
tags = "not @Negative"   // Exclude Negative tests
```

### Run Specific Feature File

Edit `TestRunner.java` and update the `features` parameter:

```java
features = "src/test/resources/features/Login.feature"
```

### Parallel Execution

Parallel execution is configured in `testng.xml`:

```xml
<suite name="Automation Test Suite" parallel="methods" thread-count="2">
```

- `parallel="methods"` - Run test methods in parallel
- `thread-count="2"` - Number of parallel threads

## 📊 Reports

### Extent Reports

After test execution, Extent Reports are generated in:
```
test-output/reports/Automation_Report_YYYYMMDD_HHMMSS.html
```

Open the HTML file in a browser to view:
- Test execution summary
- Pass/Fail status
- Screenshots on failure
- System information
- Timeline view

### Cucumber Reports

Cucumber reports are generated in:
```
test-output/cucumber-reports/
├── cucumber.html
├── cucumber.json
└── cucumber.xml
```

### Logs

Logs are generated in:
```
test-output/logs/automation.log
```

## 📝 Writing Tests

### 1. Create Feature File

Create a `.feature` file in `src/test/resources/features/`:

```gherkin
@FeatureTag
Feature: Feature Name
  As a user
  I want to perform an action
  So that I can achieve a goal

  @ScenarioTag
  Scenario: Scenario description
    Given I am on the page
    When I perform an action
    Then I should see the result
```

### 2. Create Step Definitions

Create step definitions in `src/test/java/com/automation/stepdefinitions/`:

```java
@Given("I am on the page")
public void i_am_on_the_page() {
    // Implementation
}
```

### 3. Create Page Object

Create page object in `src/main/java/com/automation/pages/`:

```java
public class MyPage extends BasePage {
    @FindBy(id = "element-id")
    private WebElement element;
    
    public void performAction() {
        click(element);
    }
}
```

## 🔧 Framework Components

### Driver Factory

- Thread-safe WebDriver management
- Automatic driver setup via WebDriverManager
- Supports Chrome, Firefox, Edge, Safari
- Headless mode support

### Utilities

- **WebDriverWaitUtil** - Explicit waits for elements
- **ActionsUtil** - Mouse and keyboard actions
- **DropdownUtil** - Dropdown operations
- **JavaScriptExecutorUtil** - JavaScript execution
- **ScreenshotUtil** - Screenshot capture
- **ConfigReader** - Configuration management

### Hooks

- **@Before** - Initialize driver, create Extent test
- **@After** - Take screenshot on failure, quit driver, flush reports

## 🎯 Best Practices

1. **Page Object Model** - All page interactions in page classes
2. **Reusable Methods** - Common operations in utility classes
3. **Explicit Waits** - Use WebDriverWaitUtil instead of Thread.sleep()
4. **Meaningful Names** - Clear method and variable names
5. **Logging** - Log important steps and actions
6. **Screenshots** - Automatic on failure
7. **Configuration** - Externalize all configurable values
8. **Tags** - Use tags to organize and filter tests

## 🐛 Troubleshooting

### Driver Issues

If you encounter driver issues:
- Ensure internet connection (WebDriverManager downloads drivers)
- Check browser version compatibility
- Update WebDriverManager version in pom.xml

### Test Failures

- Check logs in `test-output/logs/automation.log`
- Review screenshots in `test-output/screenshots/`
- Verify application URLs in config.properties
- Check element locators in page objects

### Build Issues

- Clean and rebuild: `mvn clean install`
- Update Maven dependencies: `mvn clean install -U`
- Check Java version: `java -version` (should be 11+)

## 📚 Dependencies

- **Selenium WebDriver** 4.15.0
- **Cucumber** 7.14.0
- **TestNG** 7.8.0
- **WebDriverManager** 5.6.2
- **Extent Reports** 5.1.1
- **Log4j2** 2.21.1

## 🤝 Contributing

1. Follow the existing code structure
2. Add appropriate logging
3. Update documentation
4. Follow naming conventions
5. Add comments for complex logic

## 📄 License

This framework is provided as-is for automation testing purposes.

## 👤 Author

Created with ❤️ for automation testing

---

**Happy Testing! 🚀**






