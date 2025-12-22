# Quick Start Guide

## Prerequisites Check

1. **Java Installation**
   ```bash
   java -version
   ```
   Should show Java 11 or higher

2. **Maven Installation**
   ```bash
   mvn -version
   ```
   Should show Maven 3.6 or higher

## Setup Steps

### Step 1: Navigate to Project
```bash
cd automation-framework
```

### Step 2: Install Dependencies
```bash
mvn clean install
```

### Step 3: Update Configuration
Edit `src/main/resources/config.properties`:
- Update `base.url` and `login.url` with your application URLs
- Update `username` and `password` with your credentials
- Adjust browser settings if needed

### Step 4: Run Tests

**Option 1: Run via Maven**
```bash
mvn clean test
```

**Option 2: Run via TestNG XML**
- Open `testng.xml` in your IDE
- Right-click → Run As → TestNG Suite

**Option 3: Run via TestRunner**
- Open `src/test/java/com/automation/runners/TestRunner.java`
- Right-click → Run As → TestNG Test

### Step 5: View Reports

1. **Extent Reports**: Open `test-output/reports/Automation_Report_*.html` in browser
2. **Cucumber Reports**: Open `test-output/cucumber-reports/cucumber.html` in browser
3. **Logs**: Check `test-output/logs/automation.log`

## Sample Test Execution

The framework includes a sample Login test that demonstrates:
- Page Object Model usage
- Step definitions
- Hooks (before/after)
- Screenshot on failure
- Extent Reports integration

## Common Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn clean test

# Run with specific profile
mvn clean test -DsuiteXmlFile=testng.xml

# Skip tests (compile only)
mvn clean install -DskipTests

# Update dependencies
mvn clean install -U
```

## Troubleshooting

### Issue: Tests not running
- Check Java and Maven versions
- Run `mvn clean install` to ensure dependencies are downloaded
- Check `config.properties` for correct URLs

### Issue: Driver not found
- Ensure internet connection (WebDriverManager downloads drivers automatically)
- Check browser is installed
- Verify browser version compatibility

### Issue: Tests failing
- Check application is accessible
- Verify credentials in `config.properties`
- Review logs in `test-output/logs/automation.log`
- Check screenshots in `test-output/screenshots/`

## Next Steps

1. Review the sample Login test in `src/test/resources/features/Login.feature`
2. Check step definitions in `src/test/java/com/automation/stepdefinitions/LoginStepDefinitions.java`
3. Review page object in `src/main/java/com/automation/pages/LoginPage.java`
4. Create your own feature files and step definitions following the same pattern

## Framework Structure Overview

- **Features**: `src/test/resources/features/` - Gherkin feature files
- **Step Definitions**: `src/test/java/com/automation/stepdefinitions/` - Java step implementations
- **Page Objects**: `src/main/java/com/automation/pages/` - Page Object Model classes
- **Utilities**: `src/main/java/com/automation/utils/` - Reusable utility classes
- **Hooks**: `src/test/java/com/automation/hooks/` - Setup/teardown logic
- **Configuration**: `src/main/resources/config.properties` - Test configuration

Happy Testing! 🚀





