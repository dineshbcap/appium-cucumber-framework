package com.appium.framework.pages.controls;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.AppUtils;
import com.appium.framework.utils.WaitUtils;

import java.util.Map;

/**
 * Page object demonstrating <b>Deep Linking</b> — navigating directly to a specific
 * screen inside the app via a URL scheme or Universal Link (iOS) / App Link (Android).
 *
 * <p><b>Concept covered:</b> Deep links allow tests to skip intermediate navigation steps
 * and jump directly to the screen under test. This dramatically speeds up test setup
 * and isolates the feature being tested from the navigation path.</p>
 *
 * <p><b>Android Deep Links (Intent URIs):</b>
 * <ul>
 *   <li>Custom URL scheme: {@code myapp://home}, {@code myapp://profile/123}</li>
 *   <li>Opened via {@code mobile:startActivity} with an intent Action VIEW</li>
 *   <li>App Links (verified https links) open directly in the app without a chooser dialog</li>
 * </ul>
 * </p>
 *
 * <p><b>iOS Deep Links (URL Schemes / Universal Links):</b>
 * <ul>
 *   <li>Custom URL scheme: {@code myapp://home}, {@code myapp://profile/123}</li>
 *   <li>Opened via {@code mobile:openUrl} execute script</li>
 *   <li>Universal Links (https://example.com/path) open in the app if installed</li>
 * </ul>
 * </p>
 *
 * <p><b>Benefit over UI navigation:</b> A test that needs to reach "Profile > Settings > Privacy"
 * can use a deep link instead of tapping through three screens — reducing test flakiness
 * and execution time.</p>
 */
public class DeepLinkPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    // Resolved from locators_android.properties / locators_ios.properties via
    // BasePage#locator / #isDisplayed / #getText. See "deepLink.*" keys.

    // ── Android Deep Linking ───────────────────────────────────────────────────

    /**
     * Opens an Android deep link URL using an ADB-style intent via the Appium mobile command.
     *
     * <p>This sends an {@code ACTION_VIEW} intent with the specified URL.
     * The Android OS routes it to the app that handles that URL scheme.</p>
     *
     * <p>Example: {@code openAndroidDeepLink("io.appium.android.apis", "content://media/external/images/media/1")}</p>
     *
     * @param appPackage the target application's package name
     * @param deepLinkUrl the URL/URI to open (custom scheme or http/https)
     */
    public void openAndroidDeepLink(String appPackage, String deepLinkUrl) {
        log.info("Opening Android deep link: {} in {}", deepLinkUrl, appPackage);
        DriverManager.getDriver().executeScript("mobile:startActivity", Map.of(
                "action", "android.intent.action.VIEW",
                "uri", deepLinkUrl,
                "package", appPackage
        ));
    }

    /**
     * Opens a deep link in the currently configured Android app under test.
     * The app package is read from config.properties.
     *
     * @param deepLinkUrl the URL/URI to open
     */
    public void openAndroidDeepLink(String deepLinkUrl) {
        openAndroidDeepLink(ConfigReader.get("android.appPackage"), deepLinkUrl);
    }

    // ── iOS Deep Linking ───────────────────────────────────────────────────────

    /**
     * Opens an iOS deep link (custom URL scheme or Universal Link).
     *
     * <p>Uses the {@code mobile:openUrl} XCUITest command which instructs Safari
     * (or the system) to open the URL. If the URL scheme is registered by the app under test,
     * the OS automatically routes to that app.</p>
     *
     * @param url the URL to open (e.g., "myapp://profile", "https://example.com/settings")
     */
    public void openIosDeepLink(String url) {
        log.info("Opening iOS deep link: {}", url);
        AppUtils.openIosUrl(url);
    }

    // ── Cross-Platform ─────────────────────────────────────────────────────────

    /**
     * Opens a deep link using the platform-appropriate mechanism.
     *
     * <p>On Android: routes through {@link #openAndroidDeepLink(String)}.
     * On iOS: routes through {@link #openIosDeepLink(String)}.</p>
     *
     * @param deepLinkUrl the deep link URL to open on either platform
     */
    public void openDeepLink(String deepLinkUrl) {
        log.info("Opening deep link ({}): {}", ConfigReader.getPlatform(), deepLinkUrl);
        if (ConfigReader.isAndroid()) {
            openAndroidDeepLink(deepLinkUrl);
        } else {
            openIosDeepLink(deepLinkUrl);
        }
    }

    // ── Verification ──────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the deep link successfully navigated to the target screen.
     *
     * @return {@code true} if the target screen indicator is visible
     */
    public boolean isDeepLinkTargetDisplayed() {
        return isDisplayed("deepLink.targetDisplayedIndicator");
    }

    /**
     * Waits for the deep-linked target screen to appear.
     * Allows time for the intent/URL to be processed and the activity to launch.
     */
    public void waitForDeepLinkTarget() {
        log.info("Waiting for deep link target screen");
        WaitUtils.waitForVisible(locator("deepLink.targetDisplayedIndicator"), 15);
    }

    /**
     * Returns the page title of the currently displayed screen.
     * Used to assert that the correct screen was opened by the deep link.
     *
     * @return current page title text
     */
    public String getCurrentPageTitle() {
        return getText("deepLink.pageTitle");
    }
}
