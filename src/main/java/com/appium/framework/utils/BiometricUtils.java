package com.appium.framework.utils;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Biometric authentication simulation utilities for Touch ID (iOS) and
 * Fingerprint/Face authentication (Android).
 *
 * <p><b>Concept covered:</b> Modern apps rely on biometric authentication (Touch ID, Face ID,
 * Android Fingerprint/BiometricPrompt). Appium provides APIs to simulate both successful
 * and failed biometric events on simulators/emulators without needing actual fingerprints.</p>
 *
 * <p><b>iOS — Touch ID / Face ID:</b>
 * <ul>
 *   <li>Requires the iOS Simulator (real devices cannot simulate biometrics)</li>
 *   <li>The capability {@code "allowTouchIdEnroll": true} must be set when creating the session</li>
 *   <li>{@code mobile:performTouchId} with {@code match=true} simulates a matching fingerprint</li>
 *   <li>For Face ID: use {@code mobile:enrollBiometric} and {@code mobile:sendBiometricMatch}</li>
 * </ul>
 * </p>
 *
 * <p><b>Android — Fingerprint:</b>
 * <ul>
 *   <li>Only works on Android emulators (API 23+) via the ADB-backed fingerprint command</li>
 *   <li>Uses the {@code mobile:fingerprint} execute script command</li>
 *   <li>Fingerprint ID must match an enrolled ID configured in the emulator's security settings</li>
 * </ul>
 * </p>
 */
public class BiometricUtils {

    private static final Logger log = LogManager.getLogger(BiometricUtils.class);

    private BiometricUtils() {}

    // ── iOS Touch ID ───────────────────────────────────────────────────────────

    /**
     * Simulates a successful Touch ID authentication on the iOS Simulator.
     *
     * <p>Uses {@code mobile:performTouchId} execute script with {@code match=true}.
     * After calling this, any pending Touch ID prompt in the app will be resolved
     * as if the correct fingerprint was presented.</p>
     *
     * <p>Prerequisite: The session capability {@code "allowTouchIdEnroll": true} must be set.</p>
     */
    public static void performTouchIdSuccess() {
        if (!ConfigReader.isIOS()) {
            log.warn("Touch ID simulation is iOS Simulator-only");
            return;
        }
        log.info("Simulating successful Touch ID");
        DriverManager.getDriver().executeScript("mobile:performTouchId",
                Map.of("match", true));
    }

    /**
     * Simulates a failed Touch ID authentication on the iOS Simulator.
     *
     * <p>The app should respond as if the user presented an unrecognized fingerprint.</p>
     */
    public static void performTouchIdFailure() {
        if (!ConfigReader.isIOS()) {
            log.warn("Touch ID simulation is iOS Simulator-only");
            return;
        }
        log.info("Simulating failed Touch ID");
        DriverManager.getDriver().executeScript("mobile:performTouchId",
                Map.of("match", false));
    }

    /**
     * Enrolls or unenrolls Touch ID on the iOS Simulator.
     *
     * <p>Equivalent to going to Simulator menu: Features → Touch ID → Enrolled.
     * Must be called before any Touch ID prompts appear.</p>
     *
     * @param enroll {@code true} to enroll, {@code false} to unenroll
     */
    public static void toggleTouchIdEnrollment(boolean enroll) {
        if (!ConfigReader.isIOS()) {
            log.warn("Touch ID enrollment is iOS Simulator-only");
            return;
        }
        log.info("Touch ID enrollment: {}", enroll);
        DriverManager.getDriver().executeScript("mobile:enrollBiometric",
                Map.of("isEnabled", enroll));
    }

    // ── iOS Face ID ────────────────────────────────────────────────────────────

    /**
     * Simulates a successful Face ID match on the iOS Simulator.
     *
     * <p>Uses the {@code mobile:sendBiometricMatch} XCUITest execute script command.
     * Requires Face ID to be enrolled in the Simulator (Features → Face ID → Enrolled).</p>
     */
    public static void performFaceIdSuccess() {
        if (!ConfigReader.isIOS()) {
            log.warn("Face ID simulation is iOS Simulator-only");
            return;
        }
        log.info("Simulating successful Face ID match");
        DriverManager.getDriver().executeScript("mobile:sendBiometricMatch",
                Map.of("type", "faceId", "match", true));
    }

    /**
     * Simulates a failed Face ID match on the iOS Simulator.
     */
    public static void performFaceIdFailure() {
        if (!ConfigReader.isIOS()) {
            log.warn("Face ID simulation is iOS Simulator-only");
            return;
        }
        log.info("Simulating failed Face ID match");
        DriverManager.getDriver().executeScript("mobile:sendBiometricMatch",
                Map.of("type", "faceId", "match", false));
    }

    /**
     * Enrolls or unenrolls Face ID on the iOS Simulator.
     *
     * @param enroll {@code true} to enroll Face ID, {@code false} to unenroll
     */
    public static void toggleFaceIdEnrollment(boolean enroll) {
        if (!ConfigReader.isIOS()) return;
        log.info("Face ID enrollment: {}", enroll);
        DriverManager.getDriver().executeScript("mobile:enrollBiometric",
                Map.of("isEnabled", enroll));
    }

    // ── Android Fingerprint ────────────────────────────────────────────────────

    /**
     * Simulates a fingerprint authentication event on an Android emulator.
     *
     * <p>Uses the {@code mobile:fingerprint} Appium command which internally calls
     * the emulator's fingerprint control. The {@code fingerprintId} must match one of
     * the fingerprint IDs enrolled in the emulator's security settings.</p>
     *
     * <p><b>Emulator setup:</b>
     * <ol>
     *   <li>Start emulator with Google Play API (API 23+)</li>
     *   <li>Settings → Security → Fingerprint → enroll finger with ID 1</li>
     *   <li>Call this method with ID 1 to simulate a match</li>
     * </ol>
     * </p>
     *
     * @param fingerprintId the enrolled fingerprint ID to simulate (typically 1-10)
     */
    public static void simulateAndroidFingerprint(int fingerprintId) {
        if (!ConfigReader.isAndroid()) {
            log.warn("Android fingerprint simulation is Android emulator-only");
            return;
        }
        log.info("Simulating Android fingerprint ID: {}", fingerprintId);
        DriverManager.getDriver().executeScript("mobile:fingerprint",
                Map.of("fingerprintId", fingerprintId));
    }

    /**
     * Simulates a successful Android fingerprint authentication using the default fingerprint ID 1.
     * Assumes fingerprint with ID 1 is enrolled in the emulator.
     */
    public static void simulateAndroidFingerprintSuccess() {
        simulateAndroidFingerprint(1);
    }

    // ── Cross-Platform Helper ──────────────────────────────────────────────────

    /**
     * Simulates a successful biometric authentication using the platform-appropriate method.
     *
     * <p>Routes to {@link #performTouchIdSuccess()} on iOS and
     * {@link #simulateAndroidFingerprintSuccess()} on Android.</p>
     */
    public static void simulateBiometricSuccess() {
        log.info("Simulating biometric success (platform: {})", ConfigReader.getPlatform());
        if (ConfigReader.isIOS()) {
            performTouchIdSuccess();
        } else {
            simulateAndroidFingerprintSuccess();
        }
    }

    /**
     * Simulates a failed biometric authentication using the platform-appropriate method.
     */
    public static void simulateBiometricFailure() {
        log.info("Simulating biometric failure (platform: {})", ConfigReader.getPlatform());
        if (ConfigReader.isIOS()) {
            performTouchIdFailure();
        } else {
            // Android: use a non-enrolled fingerprint ID to simulate failure
            simulateAndroidFingerprint(99);
        }
    }
}
