@biometric
Feature: Biometric Authentication
  As a mobile automation engineer
  I want to simulate biometric authentication events (Touch ID, Face ID, Fingerprint)
  So that I can test authentication flows without physical biometric hardware

  # Note: Biometric tests require Simulator (iOS) or Emulator (Android)
  # For iOS: set "allowTouchIdEnroll: true" in XCUITestOptions
  # For Android: enroll fingerprint ID 1 in emulator settings
  #
  # All scenarios below route through a "biometric trigger" button
  # (BiometricPage.authenticateButton) that only exists on ApiDemos —
  # UIKitCatalog has no screen that invokes LocalAuthentication at all.
  # Tagged @androidOnly until an iOS fixture app with a real trigger exists.

  # ── iOS Touch ID ───────────────────────────────────────────────────────────────

  @iosOnly @touchId @smoke @androidOnly
  Scenario: Successful Touch ID authentication
    Given Touch ID is enrolled on the iOS Simulator
    When the user triggers biometric authentication
    And a successful Touch ID is simulated
    Then the authentication should be successful

  @iosOnly @touchId @androidOnly
  Scenario: Failed Touch ID authentication
    Given Touch ID is enrolled on the iOS Simulator
    When the user triggers biometric authentication
    And a failed Touch ID is simulated
    Then the authentication should fail

  @iosOnly @touchId @androidOnly
  Scenario: Cancel Touch ID authentication
    Given Touch ID is enrolled on the iOS Simulator
    When the user triggers biometric authentication
    And the user cancels the biometric prompt
    Then the app should return to the normal state

  # ── iOS Face ID ────────────────────────────────────────────────────────────────

  @iosOnly @faceId @androidOnly
  Scenario: Successful Face ID authentication
    # Requires Face ID to be enrolled in Simulator (Features -> Face ID -> Enrolled)
    When the user triggers biometric authentication
    And a successful Face ID is simulated
    Then the authentication should be successful

  @iosOnly @faceId @androidOnly
  Scenario: Failed Face ID match
    When the user triggers biometric authentication
    And a failed Face ID is simulated
    Then the authentication should fail

  # ── Android Fingerprint ────────────────────────────────────────────────────────

  @androidOnly @fingerprint @smoke
  Scenario: Successful Android fingerprint authentication
    # Uses the enrolled fingerprint ID 1 in the emulator
    When the user triggers biometric authentication
    And the Android fingerprint ID 1 is simulated
    Then the authentication should be successful

  @androidOnly @fingerprint
  Scenario: Android fingerprint authentication with wrong ID
    # Simulating fingerprint ID 99 (not enrolled) should fail
    When the user triggers biometric authentication
    And the Android fingerprint ID 99 is simulated
    Then the authentication should fail

  # ── Cross-Platform ─────────────────────────────────────────────────────────────
  # Same trigger-button dependency as above — no iOS fixture screen exists.

  @smoke @crossPlatform @androidOnly
  Scenario: Biometric authentication success flow
    When the user triggers biometric authentication
    And biometric success is simulated
    Then the authentication should be successful

  @crossPlatform @androidOnly
  Scenario: Biometric authentication failure flow
    When the user triggers biometric authentication
    And biometric failure is simulated
    Then the authentication should fail
