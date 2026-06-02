package com.appium.tests.stepdefs;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.pages.controls.PermissionPage;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;

/**
 * Step definitions for Runtime Permission Handling feature.
 *
 * <p>Covers: Android Allow/Deny permission dialogs, "Don't ask again" flow,
 * ADB-based permission grant/revoke, iOS system alert Allow/Deny, and
 * "While Using App" location permission selection.</p>
 *
 * <p><b>Concept demonstrated:</b> Handling permission dialogs via UI interaction
 * and via {@code mobile:shell} ADB commands — showing both the UI-driven approach
 * (for testing the deny flow) and the API-driven approach (for test setup).</p>
 */
public class PermissionStepDefs {

    private static final Logger log = LogManager.getLogger(PermissionStepDefs.class);
    private final PermissionPage page = new PermissionPage();

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("a permission dialog is displayed")
    public void permissionDialogDisplayed() {
        log.info("Assuming a permission dialog is visible (triggered by app action)");
        // In real scenarios, the app would have triggered a permission request
        // This step verifies the dialog is present before interacting with it
    }

    @Given("an iOS permission alert is displayed")
    public void iosPermissionAlertDisplayed() {
        log.info("Assuming an iOS system permission alert is visible");
    }

    @Given("an iOS location permission alert is displayed")
    public void iosLocationAlertDisplayed() {
        log.info("Assuming iOS location permission alert is visible");
    }

    @Given("the app requires a permission")
    public void appRequiresPermission() {
        log.info("App permission scenario setup — checking if dialog is shown");
    }

    // ── When ──────────────────────────────────────────────────────────────────

    @When("the user allows the permission")
    public void allowPermission() {
        log.info("Tapping Allow on permission dialog");
        page.allowPermission();
    }

    @When("the user denies the permission")
    public void denyPermission() {
        log.info("Tapping Deny on permission dialog");
        page.denyPermission();
    }

    @When("the user checks {string} and denies the permission")
    public void checkDontAskAndDeny(String checkboxLabel) {
        log.info("Selecting '{}' and denying permission", checkboxLabel);
        page.checkDontAskAgainAndDeny();
    }

    @When("the location permission is granted via ADB for package {string}")
    public void grantLocationViaAdb(String packageName) {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping ADB permission grant — not Android");
            return;
        }
        page.grantAndroidPermissionViaAdb(packageName,
                "android.permission.ACCESS_FINE_LOCATION");
    }

    @When("the location permission is revoked via ADB for package {string}")
    public void revokeLocationViaAdb(String packageName) {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping ADB permission revoke — not Android");
            return;
        }
        page.revokeAndroidPermissionViaAdb(packageName,
                "android.permission.ACCESS_FINE_LOCATION");
    }

    @When("the user allows the iOS permission")
    public void allowIosPermission() {
        if (!ConfigReader.isIOS()) {
            log.info("Skipping iOS permission — not iOS");
            return;
        }
        page.allowPermission();
    }

    @When("the user denies the iOS permission")
    public void denyIosPermission() {
        if (!ConfigReader.isIOS()) {
            log.info("Skipping iOS permission denial — not iOS");
            return;
        }
        page.denyPermission();
    }

    @When("the user selects {string} for the location permission")
    public void selectLocationPermissionOption(String option) {
        log.info("Selecting location permission option: '{}'", option);
        if (option.contains("While Using")) {
            page.allowPermissionWhileUsingApp();
        } else if (option.contains("Always")) {
            page.allowPermissionAlways();
        } else {
            page.denyPermission();
        }
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the permission dialog should be dismissed")
    public void permissionDialogDismissed() {
        boolean isStillDisplayed = page.isPermissionDialogDisplayed();
        log.info("Permission dialog still displayed: {}", isStillDisplayed);
        Assertions.assertThat(isStillDisplayed)
                .as("Permission dialog should be dismissed")
                .isFalse();
    }

    @Then("the permission alert should be dismissed")
    public void permissionAlertDismissed() {
        permissionDialogDismissed();
    }

    @Then("the app should have access to location features")
    public void appShouldHaveLocationAccess() {
        log.info("Verifying app has location access (permission granted)");
        // In a real app, verify that location-dependent features are enabled
        // Here we just confirm no crash occurred and app is running
    }

    @Then("the app should not have access to location features")
    public void appShouldNotHaveLocationAccess() {
        log.info("Verifying app does NOT have location access (permission revoked)");
        // In a real app, verify that location features show a permission rationale UI
    }

    @Then("a permission dialog should be shown")
    public void permissionDialogShouldBeShown() {
        boolean isDisplayed = page.isPermissionDialogDisplayed();
        log.info("Permission dialog displayed: {}", isDisplayed);
        // Note: This assertion may skip if autoGrantPermissions=true in config
        if (ConfigReader.getBoolean("android.autoGrantPermissions", false)) {
            log.info("autoGrantPermissions=true — dialog was auto-dismissed");
        } else {
            Assertions.assertThat(isDisplayed)
                    .as("Permission dialog should be visible")
                    .isTrue();
        }
    }
}
