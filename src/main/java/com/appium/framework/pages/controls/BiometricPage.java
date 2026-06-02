package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.BiometricUtils;
import com.appium.framework.utils.WaitUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object demonstrating <b>Biometric Authentication</b> simulation.
 *
 * <p><b>Concept covered:</b> Testing Touch ID, Face ID, and Android fingerprint
 * authentication flows without physical biometric hardware.
 * Appium can simulate both successful and failed biometric events on simulators/emulators.</p>
 *
 * <p><b>When to use biometric simulation:</b>
 * <ul>
 *   <li>Login screens with "Use Touch ID / Face ID" option</li>
 *   <li>App lock screens (re-authenticate after timeout)</li>
 *   <li>Payment confirmation screens</li>
 *   <li>Sensitive settings screens requiring re-authentication</li>
 * </ul>
 * </p>
 *
 * <p><b>Setup requirements (iOS Simulator):</b>
 * <ol>
 *   <li>Set capability {@code "allowTouchIdEnroll": true} when creating the session</li>
 *   <li>Enable Touch ID in Simulator: Features → Touch ID / Face ID → Enrolled</li>
 *   <li>Or call {@link BiometricUtils#toggleTouchIdEnrollment(boolean)} in the test setup</li>
 * </ol>
 * </p>
 *
 * <p><b>Setup requirements (Android Emulator):</b>
 * <ol>
 *   <li>Open Settings → Security → Fingerprint → Enroll fingerprint (ID 1)</li>
 *   <li>This only needs to be done once per emulator state/snapshot</li>
 * </ol>
 * </p>
 */
public class BiometricPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    /** Button that triggers the biometric authentication prompt. */
    @AndroidFindBy(accessibility = "Authenticate with Fingerprint")
    @iOSXCUITFindBy(accessibility = "authenticateButton")
    private WebElement authenticateButton;

    /** Label shown on the biometric system prompt. */
    @AndroidFindBy(accessibility = "Confirm fingerprint")
    @iOSXCUITFindBy(accessibility = "biometricPromptTitle")
    private WebElement biometricPromptTitle;

    /** Result label displayed after authentication success/failure. */
    @AndroidFindBy(id = "io.appium.android.apis:id/auth_result")
    @iOSXCUITFindBy(accessibility = "authResult")
    private WebElement authResultLabel;

    /** Cancel button on the biometric prompt. */
    @AndroidFindBy(accessibility = "Cancel")
    @iOSXCUITFindBy(accessibility = "cancelBiometric")
    private WebElement cancelButton;

    private static final By AUTH_RESULT_LOCATOR =
            By.xpath("//*[@resource-id='io.appium.android.apis:id/auth_result'" +
                     " or @name='authResult']");
    private static final By AUTHENTICATE_BTN_LOCATOR =
            By.xpath("//*[@content-desc='Authenticate with Fingerprint' or @name='authenticateButton']");

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Taps the authenticate button to trigger the biometric authentication prompt.
     * The system-level biometric dialog (or prompt) will appear after this.
     */
    public void triggerBiometricAuthentication() {
        log.info("Triggering biometric authentication prompt");
        authenticateButton.click();
        // Small delay to allow the system biometric prompt to appear
        WaitUtils.hardWait(500);
    }

    /**
     * Simulates a successful biometric authentication (matching fingerprint or face).
     * Must be called after the biometric prompt is shown.
     */
    public void authenticateSuccessfully() {
        log.info("Simulating successful biometric authentication");
        BiometricUtils.simulateBiometricSuccess();
    }

    /**
     * Simulates a failed biometric authentication (non-matching fingerprint or face).
     * The app should show a failure message and may offer a fallback (PIN/password).
     */
    public void authenticateWithFailure() {
        log.info("Simulating failed biometric authentication");
        BiometricUtils.simulateBiometricFailure();
    }

    /**
     * Cancels the biometric prompt (user pressed Cancel or Back button).
     * Tests the cancellation flow — app should handle this gracefully.
     */
    public void cancelAuthentication() {
        log.info("Cancelling biometric prompt");
        try {
            cancelButton.click();
        } catch (Exception e) {
            // On some platforms Cancel is handled by the hardware Back button
            driver().navigate().back();
        }
    }

    // ── Verification ──────────────────────────────────────────────────────────

    /**
     * Returns the text shown in the authentication result label.
     *
     * @return result text (e.g., "Authentication Successful", "Authentication Failed")
     */
    public String getAuthResultText() {
        WaitUtils.waitForVisible(AUTH_RESULT_LOCATOR, 10);
        return authResultLabel.getText();
    }

    /**
     * Returns {@code true} if the authentication was successful based on the result label.
     *
     * @return {@code true} if result contains "success" (case-insensitive)
     */
    public boolean isAuthenticationSuccessful() {
        return getAuthResultText().toLowerCase().contains("success");
    }

    /**
     * Returns {@code true} if the biometric authentication button is visible.
     *
     * @return {@code true} if the authenticate button is displayed
     */
    public boolean isBiometricPromptAvailable() {
        return isDisplayed(AUTHENTICATE_BTN_LOCATOR);
    }

    // ── Enrollment Helpers ────────────────────────────────────────────────────

    /**
     * Enrolls Touch ID on the iOS Simulator before the test runs.
     * After enrollment, Touch ID prompts will respond to {@link #authenticateSuccessfully()}.
     */
    public void enrollTouchId() {
        log.info("Enrolling Touch ID on iOS Simulator");
        BiometricUtils.toggleTouchIdEnrollment(true);
    }

    /**
     * Unenrolls Touch ID on the iOS Simulator.
     * Useful for testing the "Touch ID not enrolled" fallback flow.
     */
    public void unenrollTouchId() {
        log.info("Unenrolling Touch ID on iOS Simulator");
        BiometricUtils.toggleTouchIdEnrollment(false);
    }
}
