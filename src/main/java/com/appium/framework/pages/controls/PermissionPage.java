package com.appium.framework.pages.controls;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.WaitUtils;
import org.openqa.selenium.By;

import java.util.Map;

/**
 * Page object demonstrating <b>Runtime Permission Dialog</b> handling.
 *
 * <p><b>Concept covered:</b> Modern Android (API 23+) and iOS (8+) require apps to request
 * sensitive permissions at runtime (Camera, Location, Contacts, Microphone, etc.).
 * Tests must handle these dialogs correctly to reach the feature under test.</p>
 *
 * <p><b>Android strategies:</b>
 * <ul>
 *   <li><b>autoGrantPermissions capability</b> — Appium silently grants all permissions
 *       before tests run (simplest; set {@code android.autoGrantPermissions=true} in config)</li>
 *   <li><b>UI interaction</b> — Find and tap the Allow/Deny button on the permission dialog.
 *       Required when testing the deny flow or apps with in-app permission rationale</li>
 *   <li><b>ADB grant</b> — Use {@code mobile:shell} to run {@code pm grant} for real devices</li>
 *   <li><b>UiAutomator2 accessibilityId</b> — Permission dialogs use system package IDs</li>
 * </ul>
 * </p>
 *
 * <p><b>iOS strategies:</b>
 * <ul>
 *   <li><b>XCUITest alert handling</b> — Appium automatically detects iOS permission alerts
 *       and can accept/dismiss them via {@code mobile:alert} or by finding the button</li>
 *   <li><b>Capability autoAcceptAlerts</b> — set to true to auto-accept all iOS alerts</li>
 *   <li><b>Capability autoDismissAlerts</b> — auto-dismiss all alerts</li>
 * </ul>
 * </p>
 *
 * <p><b>Best practice:</b> Use {@code autoGrantPermissions=true} for happy-path scenarios.
 * Write explicit permission dialog tests only when testing the deny/rationale/settings flows.</p>
 *
 * <p>Locators live in {@code locators_android.properties} / {@code locators_ios.properties}
 * under the {@code permission.*} keys — each key resolves to the right platform's dialog
 * button automatically, so the actions below no longer need to branch on platform themselves.</p>
 */
public class PermissionPage extends BasePage {

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Allows the permission by tapping the Allow button on the dialog.
     * Safe to call: returns quietly if no dialog is currently shown.
     */
    public void allowPermission() {
        log.info("Allowing permission dialog");
        clickIfPresent("permission.allowButton", 5);
    }

    /**
     * Allows location permission with "While Using App" option.
     * Preferred over "Always" for apps that only need foreground location access.
     */
    public void allowPermissionWhileUsingApp() {
        log.info("Allowing permission 'While Using App'");
        clickIfPresent("permission.allowWhileUsingButton", 5);
    }

    /**
     * Allows location permission "All the time" (Android) or "Always" (iOS).
     */
    public void allowPermissionAlways() {
        log.info("Allowing permission 'Always'");
        clickIfPresent("permission.allowAlwaysButton", 5);
    }

    /**
     * Denies the permission by tapping the Don't Allow / Deny button.
     * Use this to test the denied permission flow (app's fallback behavior).
     */
    public void denyPermission() {
        log.info("Denying permission dialog");
        clickIfPresent("permission.denyButton", 5);
    }

    /**
     * Checks the "Don't ask again" checkbox on Android before denying.
     * After this, the permission dialog will never show again for this app.
     * Tests the scenario where the user has permanently denied a permission.
     */
    public void checkDontAskAgainAndDeny() {
        if (!ConfigReader.isAndroid()) {
            log.warn("Don't ask again is Android-only");
            return;
        }
        log.info("Selecting 'Don't ask again' and denying");
        clickIfPresent("permission.dontAskAgainCheckbox", 5);
        denyPermission();
    }

    /**
     * Returns {@code true} if a permission dialog is currently showing on screen.
     *
     * @return {@code true} if a permission prompt is visible
     */
    public boolean isPermissionDialogDisplayed() {
        return isDisplayed("permission.allowButton");
    }

    // ── ADB-Based Permission Grant (Android Advanced) ─────────────────────────

    /**
     * Grants an Android permission via ADB shell command (bypasses UI dialog entirely).
     *
     * <p>This is the most reliable way to grant permissions programmatically without
     * depending on the permission dialog UI. Uses Appium's {@code mobile:shell} command
     * which wraps ADB shell access (requires the ADB Permission plugin or
     * {@code allowDelayAdb: false} capability).</p>
     *
     * <p>Example: {@code grantAndroidPermissionViaAdb("io.appium.android.apis",
     * "android.permission.ACCESS_FINE_LOCATION")}</p>
     *
     * @param packageName    the app's package name
     * @param permission     the full Android permission constant string
     */
    public void grantAndroidPermissionViaAdb(String packageName, String permission) {
        if (!ConfigReader.isAndroid()) {
            log.warn("ADB permission grant is Android-only");
            return;
        }
        log.info("Granting permission via ADB: {} to {}", permission, packageName);
        // mobile:shell executes an ADB shell command on the connected device
        DriverManager.getDriver().executeScript("mobile:shell", Map.of(
                "command", "pm grant " + packageName + " " + permission
        ));
    }

    /**
     * Revokes an Android permission via ADB shell.
     * Useful for resetting the app state between test scenarios.
     *
     * @param packageName the app's package name
     * @param permission  the permission to revoke
     */
    public void revokeAndroidPermissionViaAdb(String packageName, String permission) {
        if (!ConfigReader.isAndroid()) return;
        log.info("Revoking permission via ADB: {} from {}", permission, packageName);
        DriverManager.getDriver().executeScript("mobile:shell", Map.of(
                "command", "pm revoke " + packageName + " " + permission
        ));
    }

    // ── Private Helper ────────────────────────────────────────────────────────

    /**
     * Clicks the element resolved from a locator key if it is present within the
     * timeout, ignores if not found. Permission dialogs appear asynchronously —
     * not every test needs them.
     *
     * @param key            dotted locator key
     * @param timeoutSeconds how long to wait for the element before giving up
     */
    private void clickIfPresent(String key, int timeoutSeconds) {
        By locator = locator(key);
        try {
            WaitUtils.waitForVisible(locator, timeoutSeconds).click();
            log.info("Clicked permission dialog button: {}", locator);
        } catch (Exception e) {
            log.debug("Permission dialog not found or already dismissed ({})", locator);
        }
    }
}
