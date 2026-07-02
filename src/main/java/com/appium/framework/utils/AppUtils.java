package com.appium.framework.utils;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Map;

/**
 * App lifecycle management utilities covering installation, uninstallation, activation,
 * termination, backgrounding, and state querying for both Android and iOS.
 *
 * <p><b>Concept covered:</b> Appium's {@link InteractsWithApps} interface lets tests
 * fully control an app's lifecycle without relying on the device UI — useful for
 * reset-between-tests, clean-state setups, and background/foreground state transitions.</p>
 *
 * <p><b>App state values returned by queryAppState (via mobile:queryAppState):</b>
 * <ul>
 *   <li>0 = NOT_INSTALLED — app is not on device</li>
 *   <li>1 = NOT_RUNNING — installed but not running</li>
 *   <li>2 = RUNNING_IN_BACKGROUND_SUSPENDED — iOS only; background + suspended</li>
 *   <li>3 = RUNNING_IN_BACKGROUND — running in background</li>
 *   <li>4 = RUNNING_IN_FOREGROUND — active and visible</li>
 * </ul>
 * </p>
 *
 * <p><b>Android vs iOS:</b> Both AndroidDriver and IOSDriver implement
 * {@link InteractsWithApps}. The bundle/package identifier differs:
 * Android uses appPackage, iOS uses bundleId.</p>
 */
public class AppUtils {

    private static final Logger log = LogManager.getLogger(AppUtils.class);

    /** App state integer constants (from Appium mobile:queryAppState). */
    public static final int STATE_NOT_INSTALLED = 0;
    public static final int STATE_NOT_RUNNING   = 1;
    public static final int STATE_BACKGROUND    = 3;
    public static final int STATE_FOREGROUND    = 4;

    private AppUtils() {}

    // ── App Identification ────────────────────────────────────────────────────

    /**
     * Returns the platform-appropriate app identifier.
     * Android → appPackage, iOS → bundleId.
     *
     * @return app identifier string from config.properties
     */
    public static String getAppId() {
        if (ConfigReader.isAndroid()) {
            return ConfigReader.get("android.appPackage");
        }
        return ConfigReader.get("ios.bundleId");
    }

    // ── Activation & Termination ──────────────────────────────────────────────

    /**
     * Activates (brings to foreground) the app under test using its configured app ID.
     * If the app is not running, it will be launched. Safe to call from background state.
     */
    public static void activateApp() {
        String appId = getAppId();
        log.info("Activating app: {}", appId);
        interactsWithApps().activateApp(appId);
    }

    /**
     * Activates any app by its identifier (package name or bundle ID).
     *
     * @param appId Android package name or iOS bundle ID
     */
    public static void activateApp(String appId) {
        log.info("Activating app: {}", appId);
        interactsWithApps().activateApp(appId);
    }

    /**
     * Terminates the app under test. The app process is killed; data is preserved unless
     * noReset=false or fullReset=true was set in capabilities.
     *
     * @return {@code true} if the app was running and is now terminated
     */
    public static boolean terminateApp() {
        String appId = getAppId();
        log.info("Terminating app: {}", appId);
        return interactsWithApps().terminateApp(appId);
    }

    /**
     * Terminates any app by its identifier.
     *
     * @param appId Android package name or iOS bundle ID
     * @return {@code true} if the app was running and is now terminated
     */
    public static boolean terminateApp(String appId) {
        log.info("Terminating app: {}", appId);
        return interactsWithApps().terminateApp(appId);
    }

    // ── Background / Foreground ───────────────────────────────────────────────

    /**
     * Sends the current app to the background for the specified duration, then restores it.
     * Useful for testing background refresh, push notifications, and session resume flows.
     *
     * <p>Appium uses the {@code mobile:backgroundApp} command internally on iOS; on Android
     * it presses the Home button and re-opens the app after the delay.</p>
     *
     * @param seconds number of seconds to keep the app in the background
     */
    public static void backgroundApp(int seconds) {
        log.info("Sending app to background for {} seconds", seconds);
        interactsWithApps().runAppInBackground(Duration.ofSeconds(seconds));
    }

    /**
     * Sends the app to the background indefinitely (no automatic restore).
     * Use {@link #activateApp()} to bring it back.
     *
     * <p>Passing a negative duration signals Appium to leave the app backgrounded
     * without a scheduled restore.</p>
     */
    public static void backgroundAppIndefinitely() {
        log.info("Sending app to background indefinitely");
        interactsWithApps().runAppInBackground(Duration.ofSeconds(-1));
    }

    // ── Install / Uninstall ───────────────────────────────────────────────────

    /**
     * Installs an app from the given path onto the device.
     * Path can be a local file path or a remote URL (Appium handles resolution).
     *
     * @param appPath absolute path to the .apk (Android) or .ipa/.app (iOS)
     */
    public static void installApp(String appPath) {
        log.info("Installing app from: {}", appPath);
        interactsWithApps().installApp(appPath);
    }

    /**
     * Removes/uninstalls the app under test from the device.
     * The app's data is also removed (equivalent to uninstall from device settings).
     */
    public static void removeApp() {
        String appId = getAppId();
        log.info("Removing app: {}", appId);
        interactsWithApps().removeApp(appId);
    }

    /**
     * Removes/uninstalls any app from the device.
     *
     * @param appId Android package name or iOS bundle ID to remove
     */
    public static void removeApp(String appId) {
        log.info("Removing app: {}", appId);
        interactsWithApps().removeApp(appId);
    }

    /**
     * Checks whether an app is currently installed on the device.
     *
     * @param appId Android package name or iOS bundle ID
     * @return {@code true} if installed, {@code false} otherwise
     */
    public static boolean isAppInstalled(String appId) {
        boolean installed = interactsWithApps().isAppInstalled(appId);
        log.info("App '{}' installed: {}", appId, installed);
        return installed;
    }

    // ── App State Query ───────────────────────────────────────────────────────

    /**
     * Returns the current state of the app as an integer.
     *
     * <p>State values (Appium mobile:queryAppState convention):
     * <ul>
     *   <li>0 = NOT_INSTALLED</li>
     *   <li>1 = NOT_RUNNING</li>
     *   <li>2 = RUNNING_IN_BACKGROUND_SUSPENDED (iOS only)</li>
     *   <li>3 = RUNNING_IN_BACKGROUND</li>
     *   <li>4 = RUNNING_IN_FOREGROUND</li>
     * </ul>
     * </p>
     *
     * @return integer state code, or -1 if querying fails
     */
    public static int getAppState() {
        return getAppState(getAppId());
    }

    /**
     * Queries the state of any app by its identifier using the mobile:queryAppState script.
     *
     * @param appId Android package name or iOS bundle ID
     * @return integer state code, or -1 if querying fails
     */
    public static int getAppState(String appId) {
        try {
            String paramKey = ConfigReader.isAndroid() ? "appId" : "bundleId";
            Object result = DriverManager.getDriver().executeScript(
                    "mobile:queryAppState",
                    Map.of(paramKey, appId));
            int state = ((Number) result).intValue();
            log.info("App '{}' state: {}", appId, state);
            return state;
        } catch (Exception e) {
            log.warn("Could not query app state for '{}': {}", appId, e.getMessage());
            return -1;
        }
    }

    /**
     * Returns {@code true} if the app is in the foreground (state == 4).
     *
     * @return {@code true} when app is actively displayed
     */
    public static boolean isAppInForeground() {
        return getAppState() == STATE_FOREGROUND;
    }

    /**
     * Returns {@code true} if the app is running (state >= 2).
     * Includes both foreground and background states.
     *
     * @return {@code true} when the app process is alive
     */
    public static boolean isAppRunning() {
        int state = getAppState();
        return state >= STATE_BACKGROUND; // 3=background, 4=foreground
    }

    // ── Platform-specific: Android ────────────────────────────────────────────

    /**
     * Starts the app activity on Android without relaunching the entire app.
     * Useful for deep-linking into a specific screen via intent.
     *
     * @param appPackage  Android package name (e.g., "io.appium.android.apis")
     * @param appActivity fully-qualified activity class (e.g., ".ApiDemos")
     */
    public static void startAndroidActivity(String appPackage, String appActivity) {
        if (!ConfigReader.isAndroid()) {
            log.warn("startAndroidActivity is Android-only — skipping on iOS");
            return;
        }
        log.info("Starting activity: {}/{}", appPackage, appActivity);
        DriverManager.getDriver().executeScript("mobile:startActivity", Map.of(
                "intent", appPackage + "/" + appActivity
        ));
    }

    /**
     * Returns the current foreground activity name on Android.
     * Useful for verifying that a navigation action reached the correct screen.
     *
     * @return current activity name, or null if not on Android
     */
    public static String getCurrentAndroidActivity() {
        if (!ConfigReader.isAndroid()) return null;
        String activity = ((AndroidDriver) DriverManager.getDriver()).currentActivity();
        log.info("Current activity: {}", activity);
        return activity;
    }

    /**
     * Returns the current package name on Android.
     *
     * @return current package name, or null if not on Android
     */
    public static String getCurrentAndroidPackage() {
        if (!ConfigReader.isAndroid()) return null;
        String pkg = ((AndroidDriver) DriverManager.getDriver()).getCurrentPackage();
        log.info("Current package: {}", pkg);
        return pkg;
    }

    // ── Platform-specific: iOS ────────────────────────────────────────────────

    /**
     * Opens a URL scheme (deep link) on iOS using the Appium {@code mobile:openUrl} command.
     * Example: {@code openIosUrl("myapp://home")} navigates to the home screen via deep link.
     *
     * @param url URL scheme or universal link to open
     */
    public static void openIosUrl(String url) {
        if (!ConfigReader.isIOS()) {
            log.warn("openIosUrl is iOS-only — skipping on Android");
            return;
        }
        log.info("Opening iOS URL: {}", url);
        DriverManager.getDriver().executeScript("mobile:openUrl", Map.of("url", url));
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    /**
     * Casts the current driver to {@link InteractsWithApps}.
     * Both AndroidDriver and IOSDriver implement this interface.
     *
     * @return the driver cast to InteractsWithApps
     */
    private static InteractsWithApps interactsWithApps() {
        return (InteractsWithApps) DriverManager.getDriver();
    }
}
