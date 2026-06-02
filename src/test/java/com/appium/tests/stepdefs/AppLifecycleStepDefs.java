package com.appium.tests.stepdefs;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.pages.controls.AppLifecyclePage;
import com.appium.framework.utils.AppUtils;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;

/**
 * Step definitions for App Lifecycle Management feature.
 *
 * <p>Covers: background/foreground transitions, terminate/relaunch,
 * app state queries, and install verification.</p>
 *
 * <p><b>Concept demonstrated:</b> Using {@link AppUtils} to control the app lifecycle
 * without touching the UI — essential for testing background refresh, cold-start
 * behavior, and session preservation.</p>
 */
public class AppLifecycleStepDefs {

    private static final Logger log = LogManager.getLogger(AppLifecycleStepDefs.class);
    private final AppLifecyclePage page = new AppLifecyclePage();

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("the app is running in the foreground")
    public void appIsRunningInForeground() {
        log.info("Verifying app is in foreground");
        // Activate the app if it's not already in foreground
        if (!AppUtils.isAppInForeground()) {
            AppUtils.activateApp();
        }
        page.waitForMainScreen();
    }

    // ── When ──────────────────────────────────────────────────────────────────

    @When("the user sends the app to the background for {int} seconds")
    public void sendAppToBackground(int seconds) {
        log.info("Sending app to background for {} seconds", seconds);
        page.backgroundApp(seconds);
    }

    @When("the user sends the app to the background indefinitely")
    public void sendAppToBackgroundIndefinitely() {
        page.backgroundAppIndefinitely();
    }

    @When("the app is force-closed")
    public void forceCloseApp() {
        page.forceCloseApp();
    }

    @When("the app is relaunched")
    public void relaunchApp() {
        page.relaunchApp();
    }

    @When("the app is restored to foreground")
    public void restoreApp() {
        page.restoreApp();
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the app should be restored to the foreground")
    public void appShouldBeRestoredToForeground() {
        // After background + auto-restore, app should be in foreground
        Assertions.assertThat(AppUtils.isAppInForeground())
                .as("App should be running in foreground after restore")
                .isTrue();
    }

    @Then("the main screen should be displayed")
    public void mainScreenShouldBeDisplayed() {
        Assertions.assertThat(page.isMainScreenDisplayed())
                .as("Main screen should be visible")
                .isTrue();
    }

    @Then("the app state should be {string}")
    public void appStateShouldBe(String expectedStateName) {
        // Map human-readable names to integer state codes (Appium mobile:queryAppState)
        int expectedCode;
        switch (expectedStateName) {
            case "NOT_INSTALLED" -> expectedCode = AppUtils.STATE_NOT_INSTALLED;
            case "NOT_RUNNING"   -> expectedCode = AppUtils.STATE_NOT_RUNNING;
            case "RUNNING_IN_FOREGROUND" -> expectedCode = AppUtils.STATE_FOREGROUND;
            case "RUNNING_IN_BACKGROUND" -> expectedCode = AppUtils.STATE_BACKGROUND;
            default -> expectedCode = -1;
        }
        int currentState = page.getAppState();
        log.info("App state: {} (expected: {} = {})", currentState, expectedStateName, expectedCode);
        if (expectedCode >= 0) {
            Assertions.assertThat(currentState)
                    .as("App state should be " + expectedStateName)
                    .isEqualTo(expectedCode);
        }
    }

    @Then("the app should be running in the foreground")
    public void appShouldBeRunningInForeground() {
        Assertions.assertThat(AppUtils.isAppInForeground())
                .as("App should be running in foreground")
                .isTrue();
    }

    @Then("the app should not be in the foreground")
    public void appShouldNotBeInForeground() {
        Assertions.assertThat(AppUtils.isAppInForeground())
                .as("App should NOT be in foreground")
                .isFalse();
    }

    @Then("the app should be running")
    public void appShouldBeRunning() {
        Assertions.assertThat(AppUtils.isAppRunning())
                .as("App should be running in some capacity")
                .isTrue();
    }

    @Then("the app should be installed on the device")
    public void appShouldBeInstalled() {
        String appId = AppUtils.getAppId();
        Assertions.assertThat(AppUtils.isAppInstalled(appId))
                .as("App '%s' should be installed on the device", appId)
                .isTrue();
    }

    @Then("the current Android activity should contain {string}")
    public void currentActivityShouldContain(String expectedActivity) {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping Android activity check on iOS");
            return;
        }
        String currentActivity = AppUtils.getCurrentAndroidActivity();
        log.info("Current activity: {}", currentActivity);
        Assertions.assertThat(currentActivity)
                .as("Current Android activity")
                .contains(expectedActivity);
    }
}
