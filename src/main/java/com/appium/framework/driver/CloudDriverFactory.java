package com.appium.framework.driver;

import com.appium.framework.config.ConfigReader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates Appium drivers for cloud device farms (BrowserStack, Sauce Labs).
 *
 * <h2>Concept covered: Cloud Execution with Appium 2.x</h2>
 *
 * <p>Cloud providers offer grids of real physical devices for Appium testing,
 * eliminating the need to maintain a local device lab. Key differences from local execution:</p>
 * <ul>
 *   <li>Connect to the provider's cloud hub URL instead of a local Appium server</li>
 *   <li>Authenticate via credentials (username/access key) in W3C capability namespaces</li>
 *   <li>Specify device selection through cloud-specific capability keys</li>
 *   <li>The cloud provider manages Appium, driver installation, and device provisioning</li>
 * </ul>
 *
 * <h2>BrowserStack Setup</h2>
 *
 * <h3>Step 1: Upload your app</h3>
 * <pre>
 *   # Android
 *   curl -u "username:access_key" \
 *     -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
 *     -F "file=@apps/ApiDemos-debug.apk"
 *   # Returns: {"app_url": "bs://abc123..."}
 *
 *   # iOS
 *   curl -u "username:access_key" \
 *     -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
 *     -F "file=@apps/MyApp.ipa"
 * </pre>
 *
 * <h3>Step 2: Set config.properties</h3>
 * <pre>
 *   cloud.provider=browserstack
 *   cloud.username=your_bs_username
 *   cloud.access.key=your_bs_access_key
 *   cloud.app.url=bs://abc123...     # URL returned by upload API
 *   cloud.device.name=Samsung Galaxy S22
 *   cloud.os.version=12.0
 *   cloud.project.name=My App Tests
 *   cloud.build.name=CI Build #42
 * </pre>
 *
 * <h3>Step 3: Run cloud tests</h3>
 * <pre>
 *   mvn test -Dtags="@cloud" \
 *            -Dcloud.provider=browserstack \
 *            -Dcloud.username=$BS_USER \
 *            -Dcloud.access.key=$BS_KEY
 * </pre>
 *
 * <h2>W3C Capability Format</h2>
 * <p>Appium 2.x requires W3C-compliant capabilities. Cloud providers use their own
 * W3C namespaces for cloud-specific settings:</p>
 * <ul>
 *   <li>BrowserStack: {@code bstack:options} namespace</li>
 *   <li>Sauce Labs: {@code sauce:options} namespace</li>
 *   <li>LambdaTest: {@code lt:options} namespace</li>
 * </ul>
 *
 * <h2>BrowserStack Dashboard Integration</h2>
 * <p>Use {@link #markSessionPassed(String)} and {@link #markSessionFailed(String)}
 * to update the test status visible in the BrowserStack dashboard after each scenario.</p>
 */
public class CloudDriverFactory {

    private static final Logger log = LogManager.getLogger(CloudDriverFactory.class);

    /** BrowserStack Appium 2.x hub endpoint. */
    public static final String BROWSERSTACK_HUB_URL = "https://hub.browserstack.com/wd/hub";

    /** Sauce Labs US West real device cloud endpoint. */
    public static final String SAUCE_LABS_HUB_URL =
            "https://ondemand.us-west-1.saucelabs.com/wd/hub";

    /** LambdaTest real device cloud endpoint. */
    public static final String LAMBDATEST_HUB_URL =
            "https://mobile-hub.lambdatest.com/wd/hub";

    private CloudDriverFactory() {}

    /**
     * Creates a cloud driver based on the configured {@code cloud.provider}.
     * Reads provider from config.properties; supports: {@code browserstack}, {@code saucelabs}.
     *
     * @return initialized {@link AppiumDriver} stored in {@link DriverManager}
     */
    public static AppiumDriver createCloudDriver() {
        String provider = ConfigReader.get("cloud.provider", "browserstack").toLowerCase();
        log.info("Creating cloud driver — provider={}, platform={}",
                provider, ConfigReader.getPlatform());

        return switch (provider) {
            case "browserstack" -> createBrowserStackDriver();
            case "saucelabs"   -> createSauceLabsDriver();
            case "lambdatest"  -> createLambdaTestDriver();
            default -> throw new IllegalArgumentException(
                    "Unknown cloud provider: '" + provider
                    + "'. Supported values: browserstack, saucelabs, lambdatest");
        };
    }

    // ── BrowserStack ───────────────────────────────────────────────────────────

    /**
     * Creates an Appium driver for BrowserStack using W3C capability format.
     *
     * <p>BrowserStack uses the {@code bstack:options} W3C namespace for all
     * cloud-specific settings. Standard Appium capabilities (app, platform) use
     * {@link UiAutomator2Options} or {@link XCUITestOptions} as usual.</p>
     *
     * <p>Notable {@code bstack:options} keys:
     * <ul>
     *   <li>{@code deviceName} — exact device name (see BrowserStack device list)</li>
     *   <li>{@code osVersion} — Android/iOS version (e.g., "12.0", "16")</li>
     *   <li>{@code appiumVersion} — "2.0.0" for Appium 2.x</li>
     *   <li>{@code projectName} — groups sessions in the dashboard</li>
     *   <li>{@code buildName} — identifies the CI build/sprint</li>
     *   <li>{@code sessionName} — identifies the specific test scenario</li>
     *   <li>{@code networkLogs} — capture network activity (HTTP/HTTPS)</li>
     *   <li>{@code consoleLogs} — capture device console logs ("verbose", "info", "warnings")</li>
     *   <li>{@code deviceLogs} — capture Android logcat / iOS device logs</li>
     *   <li>{@code video} — enable session video recording (true by default)</li>
     * </ul>
     * </p>
     *
     * @return initialized {@link AppiumDriver} for BrowserStack
     */
    public static AppiumDriver createBrowserStackDriver() {
        String platform = ConfigReader.getPlatform();
        AppiumDriver driver = "ios".equals(platform)
                ? buildBrowserStackIOSDriver()
                : buildBrowserStackAndroidDriver();
        DriverManager.setDriver(driver);
        log.info("BrowserStack driver created — platform={}", platform);
        return driver;
    }

    private static AndroidDriver buildBrowserStackAndroidDriver() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("android");
        // Use the bs:// URL returned after uploading the APK to BrowserStack
        options.setApp(ConfigReader.get("cloud.app.url"));

        options.setCapability("bstack:options", buildBrowserStackOptions("android"));

        log.debug("BrowserStack Android options — device={}, os={}",
                ConfigReader.get("cloud.device.name", "Samsung Galaxy S22"),
                ConfigReader.get("cloud.os.version", "12.0"));

        try {
            return new AndroidDriver(new URL(BROWSERSTACK_HUB_URL), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid BrowserStack URL: " + BROWSERSTACK_HUB_URL, e);
        }
    }

    private static IOSDriver buildBrowserStackIOSDriver() {
        XCUITestOptions options = new XCUITestOptions();
        options.setPlatformName("iOS");
        options.setApp(ConfigReader.get("cloud.app.url"));

        options.setCapability("bstack:options", buildBrowserStackOptions("ios"));

        log.debug("BrowserStack iOS options — device={}, os={}",
                ConfigReader.get("cloud.device.name", "iPhone 14"),
                ConfigReader.get("cloud.os.version", "16"));

        try {
            return new IOSDriver(new URL(BROWSERSTACK_HUB_URL), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid BrowserStack URL: " + BROWSERSTACK_HUB_URL, e);
        }
    }

    private static Map<String, Object> buildBrowserStackOptions(String platform) {
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("userName", ConfigReader.get("cloud.username"));
        bstackOptions.put("accessKey", ConfigReader.get("cloud.access.key"));
        bstackOptions.put("deviceName", ConfigReader.get("cloud.device.name",
                "ios".equals(platform) ? "iPhone 14" : "Samsung Galaxy S22"));
        bstackOptions.put("osVersion", ConfigReader.get("cloud.os.version",
                "ios".equals(platform) ? "16" : "12.0"));
        bstackOptions.put("projectName", ConfigReader.get("cloud.project.name",
                "Appium BDD Framework"));
        bstackOptions.put("buildName", ConfigReader.get("cloud.build.name", "Local Build"));
        bstackOptions.put("sessionName", "Test - " + System.currentTimeMillis());
        // Specify Appium 2.x — BrowserStack defaults to 1.x if not set
        bstackOptions.put("appiumVersion", "2.0.0");
        // Debugging tools
        bstackOptions.put("networkLogs", true);
        bstackOptions.put("consoleLogs", "warnings");
        bstackOptions.put("deviceLogs", true);
        return bstackOptions;
    }

    // ── Sauce Labs ─────────────────────────────────────────────────────────────

    /**
     * Creates an Appium driver for Sauce Labs using the {@code sauce:options} W3C namespace.
     *
     * @return initialized {@link AppiumDriver} for Sauce Labs
     */
    public static AppiumDriver createSauceLabsDriver() {
        String platform = ConfigReader.getPlatform();
        AppiumDriver driver = "ios".equals(platform)
                ? buildSauceLabsIOSDriver()
                : buildSauceLabsAndroidDriver();
        DriverManager.setDriver(driver);
        log.info("Sauce Labs driver created — platform={}", platform);
        return driver;
    }

    private static AndroidDriver buildSauceLabsAndroidDriver() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setPlatformVersion(ConfigReader.get("cloud.os.version", "12"));
        options.setDeviceName(ConfigReader.get("cloud.device.name", "Samsung Galaxy S22 FE.*"));
        options.setApp(ConfigReader.get("cloud.app.url"));
        options.setAutomationName("UiAutomator2");

        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username", ConfigReader.get("cloud.username"));
        sauceOptions.put("accessKey", ConfigReader.get("cloud.access.key"));
        sauceOptions.put("build", ConfigReader.get("cloud.build.name", "Local Build"));
        sauceOptions.put("name", "Android Test - " + System.currentTimeMillis());
        sauceOptions.put("deviceOrientation", "PORTRAIT");
        options.setCapability("sauce:options", sauceOptions);

        try {
            return new AndroidDriver(new URL(SAUCE_LABS_HUB_URL), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Sauce Labs URL: " + SAUCE_LABS_HUB_URL, e);
        }
    }

    private static IOSDriver buildSauceLabsIOSDriver() {
        XCUITestOptions options = new XCUITestOptions();
        options.setPlatformName("iOS");
        options.setPlatformVersion(ConfigReader.get("cloud.os.version", "16.2"));
        options.setDeviceName(ConfigReader.get("cloud.device.name", "iPhone 14 Simulator"));
        options.setApp(ConfigReader.get("cloud.app.url"));

        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username", ConfigReader.get("cloud.username"));
        sauceOptions.put("accessKey", ConfigReader.get("cloud.access.key"));
        sauceOptions.put("build", ConfigReader.get("cloud.build.name", "Local Build"));
        sauceOptions.put("name", "iOS Test - " + System.currentTimeMillis());
        options.setCapability("sauce:options", sauceOptions);

        try {
            return new IOSDriver(new URL(SAUCE_LABS_HUB_URL), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Sauce Labs URL: " + SAUCE_LABS_HUB_URL, e);
        }
    }

    // ── LambdaTest ─────────────────────────────────────────────────────────────

    /**
     * Creates an Appium driver for LambdaTest using the {@code lt:options} W3C namespace.
     *
     * @return initialized {@link AppiumDriver} for LambdaTest
     */
    public static AppiumDriver createLambdaTestDriver() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("android");
        options.setApp(ConfigReader.get("cloud.app.url"));

        Map<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("username", ConfigReader.get("cloud.username"));
        ltOptions.put("accessKey", ConfigReader.get("cloud.access.key"));
        ltOptions.put("deviceName", ConfigReader.get("cloud.device.name", "Galaxy S22"));
        ltOptions.put("platformVersion", ConfigReader.get("cloud.os.version", "12"));
        ltOptions.put("build", ConfigReader.get("cloud.build.name", "Local Build"));
        ltOptions.put("name", "LambdaTest - " + System.currentTimeMillis());
        ltOptions.put("isRealMobile", true);
        options.setCapability("lt:options", ltOptions);

        try {
            AppiumDriver driver = new AndroidDriver(new URL(LAMBDATEST_HUB_URL), options);
            DriverManager.setDriver(driver);
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid LambdaTest URL: " + LAMBDATEST_HUB_URL, e);
        }
    }

    // ── Dashboard Status Reporting ─────────────────────────────────────────────

    /**
     * Marks the current BrowserStack session as PASSED in the cloud dashboard.
     *
     * <p>This is important for tracking test results in the BrowserStack dashboard.
     * Without this call, sessions show as "unknown" status even when tests pass.
     * Call this in a Cucumber {@code @After} hook when the scenario passes.</p>
     *
     * @param reason optional message shown in the dashboard (e.g., "All assertions passed")
     */
    public static void markSessionPassed(String reason) {
        markBrowserStackSession("passed", reason);
    }

    /**
     * Marks the current BrowserStack session as FAILED in the cloud dashboard.
     *
     * @param reason failure reason shown in the dashboard
     */
    public static void markSessionFailed(String reason) {
        markBrowserStackSession("failed", reason);
    }

    /**
     * Updates the BrowserStack session status via the JavascriptExecutor API.
     *
     * <p>BrowserStack exposes a special executor command for updating session metadata.
     * This is the standard way to set pass/fail status from within the test code.</p>
     */
    private static void markBrowserStackSession(String status, String reason) {
        try {
            String script = String.format(
                    "browserstack_executor: {\"action\": \"setSessionStatus\","
                    + " \"arguments\": {\"status\": \"%s\", \"reason\": \"%s\"}}",
                    status, reason.replace("\"", "\\\""));
            ((JavascriptExecutor) DriverManager.getDriver()).executeScript(script);
            log.info("BrowserStack session marked as: {} — {}", status, reason);
        } catch (Exception e) {
            log.warn("Could not update BrowserStack session status: {}", e.getMessage());
        }
    }

    /**
     * Verifies that the cloud config keys are set before attempting to create a cloud driver.
     *
     * @throws IllegalStateException if required cloud config keys are missing
     */
    public static void validateCloudConfig() {
        String[] required = {"cloud.username", "cloud.access.key", "cloud.app.url"};
        StringBuilder missing = new StringBuilder();
        for (String key : required) {
            try {
                ConfigReader.get(key);
            } catch (RuntimeException e) {
                missing.append(key).append(", ");
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "Missing cloud configuration keys: " + missing
                    + "\nSet these in config.properties or as -D system properties.");
        }
    }

    /**
     * Returns true if cloud execution is configured and credentials are present.
     *
     * @return {@code true} if cloud config is available
     */
    public static boolean isCloudConfigured() {
        try {
            validateCloudConfig();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
}
