package com.appium.tests.stepdefs;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.pages.controls.BiometricPage;
import com.appium.framework.utils.BiometricUtils;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;

/**
 * Step definitions for Biometric Authentication feature.
 *
 * <p>Covers: Touch ID success/failure (iOS), Face ID success/failure (iOS),
 * Android fingerprint simulation, and cross-platform biometric helpers.</p>
 *
 * <p><b>Concept demonstrated:</b> Using {@link BiometricUtils} to simulate biometric
 * events on iOS Simulator and Android Emulator — enabling automated testing of
 * secure authentication flows without physical devices.</p>
 */
public class BiometricStepDefs {

    private static final Logger log = LogManager.getLogger(BiometricStepDefs.class);
    private final BiometricPage page = new BiometricPage();

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("Touch ID is enrolled on the iOS Simulator")
    public void touchIdIsEnrolled() {
        if (!ConfigReader.isIOS()) {
            log.info("Skipping Touch ID enrollment — not iOS");
            return;
        }
        log.info("Enrolling Touch ID on iOS Simulator");
        page.enrollTouchId();
    }

    // ── When ──────────────────────────────────────────────────────────────────

    @When("the user triggers biometric authentication")
    public void triggerBiometricAuth() {
        log.info("Triggering biometric authentication");
        page.triggerBiometricAuthentication();
    }

    @When("a successful Touch ID is simulated")
    public void simulateTouchIdSuccess() {
        if (!ConfigReader.isIOS()) {
            log.info("Skipping Touch ID — not iOS");
            return;
        }
        log.info("Simulating successful Touch ID");
        BiometricUtils.performTouchIdSuccess();
    }

    @When("a failed Touch ID is simulated")
    public void simulateTouchIdFailure() {
        if (!ConfigReader.isIOS()) {
            log.info("Skipping Touch ID failure — not iOS");
            return;
        }
        log.info("Simulating failed Touch ID");
        BiometricUtils.performTouchIdFailure();
    }

    @When("a successful Face ID is simulated")
    public void simulateFaceIdSuccess() {
        if (!ConfigReader.isIOS()) {
            log.info("Skipping Face ID — not iOS");
            return;
        }
        log.info("Simulating successful Face ID");
        BiometricUtils.performFaceIdSuccess();
    }

    @When("a failed Face ID is simulated")
    public void simulateFaceIdFailure() {
        if (!ConfigReader.isIOS()) {
            log.info("Skipping Face ID failure — not iOS");
            return;
        }
        log.info("Simulating failed Face ID");
        BiometricUtils.performFaceIdFailure();
    }

    @When("the Android fingerprint ID {int} is simulated")
    public void simulateAndroidFingerprint(int fingerprintId) {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping Android fingerprint — not Android");
            return;
        }
        log.info("Simulating Android fingerprint ID: {}", fingerprintId);
        BiometricUtils.simulateAndroidFingerprint(fingerprintId);
    }

    @When("biometric success is simulated")
    public void simulateBiometricSuccess() {
        log.info("Simulating biometric success (cross-platform)");
        BiometricUtils.simulateBiometricSuccess();
    }

    @When("biometric failure is simulated")
    public void simulateBiometricFailure() {
        log.info("Simulating biometric failure (cross-platform)");
        BiometricUtils.simulateBiometricFailure();
    }

    @When("the user cancels the biometric prompt")
    public void cancelBiometricPrompt() {
        page.cancelAuthentication();
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the authentication should be successful")
    public void authShouldBeSuccessful() {
        boolean isSuccessful = page.isAuthenticationSuccessful();
        log.info("Authentication result: {}", isSuccessful ? "SUCCESS" : "FAILURE");
        Assertions.assertThat(isSuccessful)
                .as("Authentication should have succeeded")
                .isTrue();
    }

    @Then("the authentication should fail")
    public void authShouldFail() {
        boolean isSuccessful = page.isAuthenticationSuccessful();
        log.info("Authentication result: {}", isSuccessful ? "SUCCESS" : "FAILURE");
        Assertions.assertThat(isSuccessful)
                .as("Authentication should have failed")
                .isFalse();
    }

    @Then("the app should return to the normal state")
    public void appShouldReturnToNormalState() {
        // After cancellation, the authenticate button should be visible again
        Assertions.assertThat(page.isBiometricPromptAvailable())
                .as("Biometric trigger button should be available after cancellation")
                .isTrue();
    }
}
