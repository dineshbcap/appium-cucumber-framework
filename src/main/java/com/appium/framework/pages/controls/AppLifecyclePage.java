package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.AppUtils;
import com.appium.framework.utils.WaitUtils;
import org.openqa.selenium.By;

/**
 * Page object demonstrating <b>App Lifecycle management</b> — one of the most important
 * advanced Appium concepts.
 *
 * <p><b>Scenarios covered:</b>
 * <ul>
 *   <li>Sending the app to background and restoring it (simulate Home button press)</li>
 *   <li>Terminating and re-activating the app (simulate force-close)</li>
 *   <li>Querying the current app state using {@link ApplicationState}</li>
 *   <li>Verifying app behavior after resuming from background</li>
 * </ul>
 * </p>
 *
 * <p><b>Why this matters:</b> Real users frequently switch between apps. Your tests
 * must verify that the app resumes correctly, session data is preserved, and
 * background refresh logic fires at the right time.</p>
 *
 * <p><b>Locators:</b> These represent a generic home/main screen that shows
 * immediately after the app launches or resumes. Adjust to match your app's landing screen.</p>
 */
public class AppLifecyclePage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    // Resolved from locators_android.properties / locators_ios.properties via
    // BasePage#locator. See "appLifecycle.*" keys — the iOS mainScreenIndicator
    // deliberately uses an NSPredicate query rather than XPath: WDA builds XPath
    // matches against a cached XML snapshot of the tree, and that snapshot cache
    // doesn't reliably invalidate across a terminateApp/activateApp cycle within
    // the same WDA session — polling the same XPath repeatedly then keeps
    // resolving to the same stale (pre-terminate) element reference and never
    // recovers. Predicate strings query the live accessibility tree directly.

    private By mainScreenIndicator() {
        return locator("appLifecycle.mainScreenIndicator");
    }

    // ── App Background / Foreground ───────────────────────────────────────────

    /**
     * Sends the app to the background for the specified number of seconds.
     * Simulates the user pressing the Home button and returning after a pause.
     *
     * <p>Under the hood, Appium presses Home (Android) or the Home button gesture (iOS),
     * waits for the duration, then brings the app back to foreground.</p>
     *
     * @param seconds how long the app should remain in the background
     */
    public void backgroundApp(int seconds) {
        log.info("Sending app to background for {} seconds", seconds);
        AppUtils.backgroundApp(seconds);
    }

    /**
     * Sends the app to background indefinitely (no automatic restore).
     * Call {@link #restoreApp()} to bring it back.
     */
    public void backgroundAppIndefinitely() {
        log.info("Sending app to background indefinitely");
        AppUtils.backgroundAppIndefinitely();
    }

    // ── Activate / Terminate ──────────────────────────────────────────────────

    /**
     * Restores (activates) the app after it has been backgrounded.
     * Equivalent to tapping the app icon in the App Switcher.
     */
    public void restoreApp() {
        log.info("Restoring app to foreground");
        AppUtils.activateApp();
    }

    /**
     * Force-terminates the app (simulates force-close from the App Switcher).
     * The app process is killed; any unsaved state is lost.
     */
    public void forceCloseApp() {
        log.info("Force closing app");
        AppUtils.terminateApp();
    }

    /**
     * Re-launches the app after it has been terminated.
     * Tests cold-start behavior (no cached state from a previous session).
     */
    public void relaunchApp() {
        log.info("Re-launching app");
        AppUtils.activateApp();
        // Wait for the main screen to appear, confirming a successful cold start
        WaitUtils.waitForVisible(mainScreenIndicator(), 20);
    }

    // ── State Queries ─────────────────────────────────────────────────────────

    /**
     * Returns the current state of the app as an integer code.
     *
     * <p>State codes (from Appium mobile:queryAppState):
     * <ul>
     *   <li>0 = NOT_INSTALLED</li>
     *   <li>1 = NOT_RUNNING</li>
     *   <li>2 = RUNNING_IN_BACKGROUND_SUSPENDED (iOS only)</li>
     *   <li>3 = RUNNING_IN_BACKGROUND</li>
     *   <li>4 = RUNNING_IN_FOREGROUND</li>
     * </ul>
     * </p>
     *
     * @return integer state code
     */
    public int getAppState() {
        return AppUtils.getAppState();
    }

    /**
     * Returns {@code true} if the app is currently in the foreground.
     *
     * @return {@code true} if app state is RUNNING_IN_FOREGROUND
     */
    public boolean isAppInForeground() {
        return AppUtils.isAppInForeground();
    }

    /**
     * Returns {@code true} if the app is running (either foreground or background).
     *
     * @return {@code true} if the app process is alive
     */
    public boolean isAppRunning() {
        return AppUtils.isAppRunning();
    }

    // ── Screen Verification ────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the main screen (home/landing) is currently displayed.
     * Used to verify successful launch or resume after backgrounding.
     *
     * <p>isDisplayed() checks instantly and races the foreground-restore animation
     * right after a background/relaunch transition; wait briefly before concluding
     * the main screen is absent.</p>
     *
     * @return {@code true} if main screen indicator is visible
     */
    public boolean isMainScreenDisplayed() {
        try {
            WaitUtils.waitForVisible(mainScreenIndicator(), 5);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the text of the main screen title element.
     *
     * @return main screen title text
     */
    public String getMainScreenTitle() {
        return getText("appLifecycle.mainScreenTitle");
    }

    /**
     * Waits for the main screen to appear after a lifecycle event (launch, resume, reopen).
     * Timeout is set generously to account for cold-start latency.
     */
    public void waitForMainScreen() {
        log.info("Waiting for main screen to appear");
        WaitUtils.waitForVisible(mainScreenIndicator(), 30);
    }
}
