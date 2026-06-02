@permissions
Feature: Runtime Permission Handling
  As a mobile automation engineer
  I want to handle runtime permission dialogs
  So that I can test both the allowed and denied permission flows

  # ── Android Permissions ────────────────────────────────────────────────────────

  @androidOnly @smoke @allowPermission
  Scenario: Allow a runtime permission dialog
    # Simulates the user granting a permission when prompted
    Given a permission dialog is displayed
    When the user allows the permission
    Then the permission dialog should be dismissed

  @androidOnly @denyPermission
  Scenario: Deny a runtime permission dialog
    Given a permission dialog is displayed
    When the user denies the permission
    Then the permission dialog should be dismissed

  @androidOnly @doNotAskAgain
  Scenario: Deny permission with "Don't ask again"
    # After this, the dialog will never show again — tests the permanently denied flow
    Given a permission dialog is displayed
    When the user checks "Don't ask again" and denies the permission
    Then the permission dialog should be dismissed

  # ── Android ADB Permission Grant ───────────────────────────────────────────────

  @androidOnly @adbPermission @advanced
  Scenario: Grant location permission via ADB shell command
    # Bypasses UI dialog entirely — most reliable for test setup
    When the location permission is granted via ADB for package "io.appium.android.apis"
    Then the app should have access to location features

  @androidOnly @adbPermission @advanced
  Scenario: Revoke location permission via ADB shell command
    # Resets the permission state — use in @After hooks for test isolation
    When the location permission is revoked via ADB for package "io.appium.android.apis"
    Then the app should not have access to location features

  # ── iOS Permissions ────────────────────────────────────────────────────────────

  @iosOnly @smoke @allowPermission
  Scenario: Allow iOS system permission alert
    Given an iOS permission alert is displayed
    When the user allows the iOS permission
    Then the permission alert should be dismissed

  @iosOnly @denyPermission
  Scenario: Deny iOS system permission alert
    Given an iOS permission alert is displayed
    When the user denies the iOS permission
    Then the permission alert should be dismissed

  @iosOnly @locationPermission
  Scenario: Allow iOS location permission - While Using App
    Given an iOS location permission alert is displayed
    When the user selects "While Using App" for the location permission
    Then the permission alert should be dismissed

  # ── Check Permission Dialog Presence ──────────────────────────────────────────

  @smoke @checkDialog
  Scenario: Verify permission dialog appears when expected
    # Tests that the app correctly requests permission at the right time
    Given the app requires a permission
    Then a permission dialog should be shown
