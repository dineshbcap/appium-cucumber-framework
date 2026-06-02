package com.appium.framework.driver;

import com.appium.framework.config.ConfigReader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Factory that creates and configures the Appium driver session.
 *
 * <p><b>Concept covered:</b> Driver capability management — the mechanism by which
 * Appium tests tell the server what device, app, and automation engine to use.
 * Different platforms require different option classes:
 * <ul>
 *   <li>Android: {@link UiAutomator2Options} → tells Appium to use UiAutomator2 engine</li>
 *   <li>iOS: {@link XCUITestOptions} → tells Appium to use XCUITest engine</li>
 * </ul>
 * </p>
 *
 * <p><b>Capability concepts:</b>
 * <ul>
 *   <li><b>platformName</b> — "Android" or "iOS" (determines which driver to use)</li>
 *   <li><b>deviceName</b> — logical name of the device or emulator</li>
 *   <li><b>udid</b> — unique device identifier for targeting specific devices in parallel</li>
 *   <li><b>app</b> — path to the .apk/.ipa file (installs before test)</li>
 *   <li><b>appPackage + appActivity</b> — Android: start an already-installed app</li>
 *   <li><b>bundleId</b> — iOS: start an already-installed app</li>
 *   <li><b>automationName</b> — automation driver (UiAutomator2 / XCUITest)</li>
 *   <li><b>noReset</b> — skip app data/cache clearing between sessions</li>
 *   <li><b>fullReset</b> — uninstall app completely before session</li>
 *   <li><b>newCommandTimeout</b> — seconds Appium waits before killing an idle session</li>
 *   <li><b>autoGrantPermissions</b> — grant all runtime permissions automatically (Android)</li>
 *   <li><b>wdaLocalPort</b> — iOS port for WebDriverAgent (must be unique per device)</li>
 * </ul>
 * </p>
 *
 * <p><b>Parallel execution:</b> The {@code udidOverride} parameter allows the same factory
 * to create sessions on different physical devices simultaneously — each thread passes its
 * own device UDID.</p>
 */
public class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {}

    /**
     * Creates an Appium driver using the platform and UDID from {@code config.properties}.
     *
     * @return initialized {@link AppiumDriver} (also stored in {@link DriverManager})
     */
    public static AppiumDriver createDriver() {
        return createDriver(null);
    }

    /**
     * Creates an Appium driver with an optional UDID override for parallel device targeting.
     *
     * <p>In a parallel run, pass the device UDID per test thread so each thread targets
     * its own physical device or emulator port.</p>
     *
     * @param udidOverride device UDID to use instead of the config value; pass {@code null}
     *                     to use the value from config.properties
     * @return initialized {@link AppiumDriver} (also stored in {@link DriverManager})
     */
    public static AppiumDriver createDriver(String udidOverride) {
        String platform = ConfigReader.getPlatform();
        log.info("Creating {} driver (thread: {})", platform, Thread.currentThread().getName());

        AppiumDriver driver = switch (platform) {
            case "android" -> createAndroidDriver(udidOverride);
            case "ios"     -> createIOSDriver(udidOverride);
            default -> throw new IllegalArgumentException(
                    "Unsupported platform: '" + platform + "'. Valid values: android, ios");
        };

        // Store in ThreadLocal so all subsequent DriverManager.getDriver() calls on
        // this thread return the same instance
        DriverManager.setDriver(driver);
        log.info("Driver created successfully for platform: {}", platform);
        return driver;
    }

    // ── Android Driver ────────────────────────────────────────────────────────

    /**
     * Creates an {@link AndroidDriver} with {@link UiAutomator2Options} capabilities.
     *
     * <p>{@link UiAutomator2Options} is the type-safe API for Android capabilities.
     * It validates capability names at compile time, preventing typos that would cause
     * silent failures with the legacy {@code DesiredCapabilities} approach.</p>
     *
     * @param udidOverride device UDID override, or null to use config
     * @return configured {@link AndroidDriver}
     */
    private static AndroidDriver createAndroidDriver(String udidOverride) {
        UiAutomator2Options options = new UiAutomator2Options();

        options.setPlatformName("Android");
        options.setPlatformVersion(ConfigReader.get("android.platformVersion"));
        options.setDeviceName(ConfigReader.get("android.deviceName"));

        String udid = udidOverride != null ? udidOverride : ConfigReader.get("android.udid");
        options.setUdid(udid);
        log.debug("Target Android device UDID: {}", udid);

        // App launch strategy: if path ends in .apk, install and launch;
        // otherwise use appPackage+appActivity to start an already-installed app
        String appPath = resolveAppPath(ConfigReader.get("android.app"));
        if (appPath.endsWith(".apk")) {
            options.setApp(appPath);
            log.debug("Installing APK: {}", appPath);
        } else {
            options.setAppPackage(ConfigReader.get("android.appPackage"));
            options.setAppActivity(ConfigReader.get("android.appActivity"));
            log.debug("Launching existing app: {}/{}",
                    ConfigReader.get("android.appPackage"),
                    ConfigReader.get("android.appActivity"));
        }

        options.setAutomationName(ConfigReader.get("android.automationName", "UiAutomator2"));
        options.setNoReset(ConfigReader.getBoolean("android.noReset", false));
        options.setFullReset(ConfigReader.getBoolean("android.fullReset", false));
        options.setNewCommandTimeout(Duration.ofSeconds(
                ConfigReader.getInt("android.newCommandTimeout", 60)));
        // autoGrantPermissions silently grants all runtime permissions — avoids
        // permission dialog interruptions during automated tests
        options.setAutoGrantPermissions(
                ConfigReader.getBoolean("android.autoGrantPermissions", true));

        // Pass Android SDK root so Appium server can locate ADB and build tools
        String sdkRoot = resolveAndroidSdkRoot();
        if (sdkRoot != null) {
            options.setCapability("appium:androidHome", sdkRoot);
            log.debug("Using Android SDK root: {}", sdkRoot);
        }

        try {
            return new AndroidDriver(new URL(ConfigReader.get("appium.server.url")), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " +
                    ConfigReader.get("appium.server.url"), e);
        }
    }

    // ── iOS Driver ────────────────────────────────────────────────────────────

    /**
     * Creates an {@link IOSDriver} with {@link XCUITestOptions} capabilities.
     *
     * <p>{@link XCUITestOptions} is the type-safe API for iOS capabilities.
     * Key iOS-specific capabilities:
     * <ul>
     *   <li>{@code wdaLocalPort} — each iOS session needs a unique port for WebDriverAgent;
     *       in parallel runs this must differ per device</li>
     *   <li>{@code allowTouchIdEnroll} — enables Touch ID / Face ID simulation in tests</li>
     *   <li>{@code autoAcceptAlerts} — auto-dismiss iOS system alerts (use carefully)</li>
     * </ul>
     * </p>
     *
     * @param udidOverride device UDID override, or null to use config
     * @return configured {@link IOSDriver}
     */
    private static IOSDriver createIOSDriver(String udidOverride) {
        XCUITestOptions options = new XCUITestOptions();

        options.setPlatformName("iOS");
        options.setPlatformVersion(ConfigReader.get("ios.platformVersion"));
        options.setDeviceName(ConfigReader.get("ios.deviceName"));

        String udid = udidOverride != null ? udidOverride : ConfigReader.get("ios.udid", "auto");
        if (!"auto".equals(udid)) {
            options.setUdid(udid);
            log.debug("Target iOS device UDID: {}", udid);
        }

        String appPath = resolveAppPath(ConfigReader.get("ios.app"));
        options.setApp(appPath);
        log.debug("iOS app path: {}", appPath);

        options.setNoReset(ConfigReader.getBoolean("ios.noReset", false));

        // wdaLocalPort must be unique per concurrent iOS device to avoid port conflicts
        int wdaPort = ConfigReader.getInt("ios.wdaLocalPort", 8100);
        options.setWdaLocalPort(wdaPort);
        log.debug("WDA local port: {}", wdaPort);

        // Enable biometric (Touch ID / Face ID) simulation in the Simulator
        if (ConfigReader.getBoolean("ios.allowTouchIdEnroll", false)) {
            options.setCapability("appium:allowTouchIdEnroll", true);
        }

        // Auto-accept iOS system alerts (permission dialogs, location prompts)
        if (ConfigReader.getBoolean("ios.autoAcceptAlerts", false)) {
            options.setCapability("appium:autoAcceptAlerts", true);
        }

        try {
            return new IOSDriver(new URL(ConfigReader.get("appium.server.url")), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " +
                    ConfigReader.get("appium.server.url"), e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Resolves the Android SDK root from config or environment variables.
     *
     * <p>Priority:
     * <ol>
     *   <li>Explicit config key {@code android.sdk.root}</li>
     *   <li>Environment variable {@code ANDROID_HOME}</li>
     *   <li>Environment variable {@code ANDROID_SDK_ROOT}</li>
     *   <li>null — Appium server will use its own ANDROID_HOME</li>
     * </ol>
     * </p>
     *
     * @return SDK root path string, or null if not configured
     */
    private static String resolveAndroidSdkRoot() {
        try {
            String fromConfig = ConfigReader.get("android.sdk.root");
            if (fromConfig != null && !fromConfig.isBlank()) return fromConfig;
        } catch (Exception ignored) {}
        String fromEnv = System.getenv("ANDROID_HOME");
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;
        fromEnv = System.getenv("ANDROID_SDK_ROOT");
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;
        return null;
    }

    /**
     * Resolves an app path to an absolute path.
     * Absolute paths are returned as-is; relative paths are resolved from the project root.
     *
     * @param path the path from config (absolute or relative to project root)
     * @return absolute path string
     */
    private static String resolveAppPath(String path) {
        // Absolute path (Unix) or Windows drive path — return as-is
        if (path.startsWith("/") || path.contains(":\\")) {
            return path;
        }
        // Relative path — resolve from the Maven/Gradle project working directory
        return new File(System.getProperty("user.dir"), path).getAbsolutePath();
    }

    /**
     * Returns the driver capabilities from the current thread's active session.
     * Useful for reading device metadata (platform version, device name) during a test.
     *
     * @return {@link Capabilities} of the active driver
     */
    public static Capabilities getCapabilities() {
        return DriverManager.getDriver().getCapabilities();
    }
}
