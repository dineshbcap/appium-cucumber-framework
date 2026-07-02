package com.appium.framework.utils;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.LocksDevice;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.SupportsRotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ScreenOrientation;

import java.time.Duration;
import java.util.Map;

/**
 * Device-level interaction utilities covering screen orientation, lock/unlock,
 * GPS location simulation, battery/connectivity info, and system-level actions.
 *
 * <p><b>Concepts covered:</b>
 * <ul>
 *   <li><b>Screen Orientation</b> — rotating to portrait/landscape using the
 *       {@code mobile:setOrientation} / {@code mobile:getOrientation} execute script
 *       commands, which work consistently across UiAutomator2 and XCUITest drivers</li>
 *   <li><b>Lock / Unlock</b> — {@link LocksDevice} interface on AndroidDriver and IOSDriver</li>
 *   <li><b>GPS Location Mock</b> — {@code mobile:setLocation} executes a script that sets
 *       a simulated GPS fix; no real movement required</li>
 *   <li><b>Screen Size / Dimensions</b> — obtaining device viewport dimensions for
 *       coordinate-based gesture calculations</li>
 *   <li><b>Device Info</b> — platform version, manufacturer, model via driver capabilities</li>
 * </ul>
 * </p>
 *
 * <p><b>Android vs iOS:</b>
 * <ul>
 *   <li>Lock/unlock is available on both platforms via {@link LocksDevice}</li>
 *   <li>Rotation is done via {@code mobile:setOrientation} execute script on both</li>
 *   <li>GPS mock requires {@code allowInvisibleElements} or {@code simulateLocation}
 *       capability on iOS Simulator</li>
 *   <li>Airplane mode toggle is Android-only via {@code mobile:setConnectivity}</li>
 * </ul>
 * </p>
 */
public class DeviceUtils {

    private static final Logger log = LogManager.getLogger(DeviceUtils.class);

    /** Orientation constant used with mobile:setOrientation execute script. */
    public static final String PORTRAIT  = "PORTRAIT";
    public static final String LANDSCAPE = "LANDSCAPE";

    private DeviceUtils() {}

    // ── Screen Orientation ────────────────────────────────────────────────────

    /**
     * Rotates the device to landscape mode via the {@link Rotatable} interface.
     *
     * <p>Triggers screen rotation events in the app just like a physical device tilt.
     * {@code mobile:setOrientation}/{@code mobile:getOrientation} are not implemented
     * by this UiAutomator2 driver version — {@link Rotatable#rotate} is the standard
     * WebDriver-level API both AndroidDriver and IOSDriver actually support.</p>
     */
    public static void rotateToLandscape() {
        log.info("Rotating device to LANDSCAPE");
        rotatable().rotate(ScreenOrientation.LANDSCAPE);
    }

    /**
     * Rotates the device back to portrait mode.
     */
    public static void rotateToPortrait() {
        log.info("Rotating device to PORTRAIT");
        rotatable().rotate(ScreenOrientation.PORTRAIT);
    }

    /**
     * Returns the current screen orientation as a string ("PORTRAIT" or "LANDSCAPE").
     *
     * @return current orientation string
     */
    public static String getOrientation() {
        try {
            String orientation = rotatable().getOrientation().name();
            log.info("Current orientation: {}", orientation);
            return orientation;
        } catch (Exception e) {
            log.warn("Could not get orientation: {}", e.getMessage());
            return PORTRAIT;
        }
    }

    private static SupportsRotation rotatable() {
        return (SupportsRotation) DriverManager.getDriver();
    }

    /**
     * Returns {@code true} if the device is currently in portrait orientation.
     *
     * @return {@code true} for portrait
     */
    public static boolean isPortrait() {
        return PORTRAIT.equalsIgnoreCase(getOrientation());
    }

    /**
     * Returns {@code true} if the device is currently in landscape orientation.
     *
     * @return {@code true} for landscape
     */
    public static boolean isLandscape() {
        return LANDSCAPE.equalsIgnoreCase(getOrientation());
    }

    // ── Lock / Unlock ─────────────────────────────────────────────────────────

    /**
     * Locks the device screen.
     *
     * <p>On Android this presses the power button; on iOS it locks via XCUITest.
     * Both platforms implement {@link LocksDevice}.</p>
     */
    public static void lockDevice() {
        log.info("Locking device");
        locksDevice().lockDevice();
    }

    /**
     * Locks the device screen for the specified duration, then automatically unlocks it.
     *
     * @param seconds how long to keep the device locked
     */
    public static void lockDevice(int seconds) {
        log.info("Locking device for {} seconds", seconds);
        locksDevice().lockDevice(Duration.ofSeconds(seconds));
    }

    /**
     * Unlocks the device screen. On iOS this swipes up; on Android it sends the unlock key.
     */
    public static void unlockDevice() {
        log.info("Unlocking device");
        locksDevice().unlockDevice();
    }

    /**
     * Returns whether the device screen is currently locked.
     *
     * @return {@code true} if the device is locked
     */
    public static boolean isDeviceLocked() {
        boolean locked = locksDevice().isDeviceLocked();
        log.info("Device locked: {}", locked);
        return locked;
    }

    // ── GPS Location ──────────────────────────────────────────────────────────

    /**
     * Sets a simulated GPS location on the device.
     *
     * <p><b>Android:</b> Uses {@code mobile:setLocation} mobile command which calls
     * the LocationManager provider. Works on both emulators and real devices (with
     * mock location permission).</p>
     *
     * <p><b>iOS Simulator:</b> Calls {@code mobile:setSimulatedLocation} via XCUITest.
     * Requires {@code simulateLocation} = true in the session capabilities.</p>
     *
     * @param latitude  GPS latitude  (e.g., 37.7749 for San Francisco)
     * @param longitude GPS longitude (e.g., -122.4194 for San Francisco)
     * @param altitude  altitude in meters above sea level (use 0 if not needed)
     */
    public static void setGpsLocation(double latitude, double longitude, double altitude) {
        log.info("Setting GPS location: lat={}, lon={}, alt={}", latitude, longitude, altitude);
        AppiumDriver driver = DriverManager.getDriver();
        if (ConfigReader.isAndroid()) {
            driver.executeScript("mobile:setLocation", Map.of(
                    "latitude", latitude,
                    "longitude", longitude,
                    "altitude", altitude
            ));
        } else {
            driver.executeScript("mobile:setSimulatedLocation", Map.of(
                    "latitude", latitude,
                    "longitude", longitude
            ));
        }
    }

    /**
     * Resets the GPS location on iOS Simulator to the device's real location.
     * No-op on Android (location mock stops when the session ends).
     */
    public static void resetIosSimulatedLocation() {
        if (!ConfigReader.isIOS()) return;
        log.info("Resetting iOS simulated location");
        DriverManager.getDriver().executeScript("mobile:resetSimulatedLocation");
    }

    // ── Screen Dimensions ─────────────────────────────────────────────────────

    /**
     * Returns the device screen dimensions in pixels.
     * Useful for computing swipe/tap coordinates as percentages of screen size.
     *
     * @return {@link Dimension} containing width and height in pixels
     */
    public static Dimension getScreenSize() {
        Dimension size = DriverManager.getDriver().manage().window().getSize();
        log.debug("Screen size: {}x{}", size.width, size.height);
        return size;
    }

    /**
     * Returns the screen width in pixels.
     *
     * @return screen width
     */
    public static int getScreenWidth() {
        return getScreenSize().width;
    }

    /**
     * Returns the screen height in pixels.
     *
     * @return screen height
     */
    public static int getScreenHeight() {
        return getScreenSize().height;
    }

    // ── Device Info ───────────────────────────────────────────────────────────

    /**
     * Returns the platform version reported by the driver (e.g., "13.0", "17.0").
     *
     * @return platform version string from driver capabilities
     */
    public static String getPlatformVersion() {
        Object ver = DriverManager.getDriver().getCapabilities().getCapability("platformVersion");
        return ver != null ? ver.toString() : "unknown";
    }

    /**
     * Returns the device name from capabilities.
     *
     * @return device name (e.g., "emulator-5554", "iPhone 15")
     */
    public static String getDeviceName() {
        Object name = DriverManager.getDriver().getCapabilities().getCapability("deviceName");
        return name != null ? name.toString() : "unknown";
    }

    // ── Android-Specific ──────────────────────────────────────────────────────

    /**
     * Toggles Airplane Mode on Android.
     *
     * <p>Uses the {@code mobile:setConnectivity} mobile command (Appium 2.x UiAutomator2).
     * Requires real device or API 30+ emulator with Wi-Fi settings permissions.</p>
     *
     * @param enabled {@code true} to enable airplane mode, {@code false} to disable
     */
    public static void setAirplaneMode(boolean enabled) {
        if (!ConfigReader.isAndroid()) {
            log.warn("Airplane mode toggle is Android-only");
            return;
        }
        log.info("Setting airplane mode: {}", enabled);
        DriverManager.getDriver().executeScript("mobile:setConnectivity", Map.of(
                "wifi", !enabled,
                "data", !enabled,
                "airplaneMode", enabled
        ));
    }

    /**
     * Toggles Wi-Fi on/off on Android.
     *
     * @param enabled {@code true} to enable Wi-Fi, {@code false} to disable
     */
    public static void setWifi(boolean enabled) {
        if (!ConfigReader.isAndroid()) {
            log.warn("Wi-Fi toggle is Android-only");
            return;
        }
        log.info("Setting Wi-Fi: {}", enabled);
        DriverManager.getDriver().executeScript("mobile:setConnectivity", Map.of(
                "wifi", enabled
        ));
    }

    /**
     * Opens the Android notification shade (pulls down the status bar).
     * iOS does not support this via Appium.
     */
    public static void openNotificationShade() {
        if (!ConfigReader.isAndroid()) {
            log.warn("Notification shade is Android-only via Appium");
            return;
        }
        log.info("Opening Android notification shade");
        ((AndroidDriver) DriverManager.getDriver()).openNotifications();
    }

    /**
     * Presses the Android Home button using a mobile key event.
     */
    public static void pressHomeButton() {
        if (!ConfigReader.isAndroid()) {
            log.warn("Use activateApp() to return to home on iOS");
            return;
        }
        log.info("Pressing Home button");
        DriverManager.getDriver().executeScript("mobile:pressKey", Map.of("keycode", 3));
    }

    /**
     * Presses the Android Back button programmatically.
     */
    public static void pressBackButton() {
        if (!ConfigReader.isAndroid()) {
            log.warn("Back button press is Android-only");
            return;
        }
        log.info("Pressing Back button");
        DriverManager.getDriver().executeScript("mobile:pressKey", Map.of("keycode", 4));
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    /**
     * Casts the current driver to {@link LocksDevice}.
     * Both AndroidDriver and IOSDriver implement this interface.
     *
     * @return driver cast to LocksDevice
     */
    private static LocksDevice locksDevice() {
        return (LocksDevice) DriverManager.getDriver();
    }
}
