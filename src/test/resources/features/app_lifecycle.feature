@appLifecycle
Feature: App Lifecycle Management
  As a mobile automation engineer
  I want to control the application lifecycle programmatically
  So that I can test background/foreground transitions, cold starts, and state preservation

  # Background: Ensure the app is in the foreground before each scenario
  Background:
    Given the app is running in the foreground

  # ── Smoke Tests ──────────────────────────────────────────────────────────────

  @smoke @background
  Scenario: Send app to background and restore it
    # Tests the most common lifecycle event: Home button press and return
    When the user sends the app to the background for 2 seconds
    Then the app should be restored to the foreground
    And the main screen should be displayed

  @smoke @terminate
  Scenario: Terminate and relaunch the app
    # Tests cold-start behavior — no cached UI state
    When the app is force-closed
    Then the app state should be "NOT_RUNNING"
    When the app is relaunched
    Then the app should be running in the foreground
    And the main screen should be displayed

  # ── App State Verification ────────────────────────────────────────────────────

  @appState
  Scenario: Verify app state is foreground when running
    Then the app state should be "RUNNING_IN_FOREGROUND"
    And the app should be running

  @appState @background
  Scenario: Verify app state after backgrounding
    When the user sends the app to the background indefinitely
    Then the app should not be in the foreground
    When the app is restored to foreground
    Then the app state should be "RUNNING_IN_FOREGROUND"

  # ── Session Preservation ─────────────────────────────────────────────────────

  @sessionPreservation
  Scenario: Verify session is preserved after background and restore
    # Navigate to a specific screen, background, return, verify same screen shown
    Given the text input controls screen is displayed
    When the user enters "Hello Appium" in the text field
    And  the user sends the app to the background for 3 seconds
    Then the app should be restored to the foreground
    # After restore, the text input screen (and its entered text) should still be showing
    And  the text field should contain "Hello Appium"

  # ── Multiple Background Events ────────────────────────────────────────────────

  @multipleBackground
  Scenario: Background app multiple times
    When the user sends the app to the background for 2 seconds
    Then the app should be restored to the foreground
    When the user sends the app to the background for 2 seconds
    Then the app should be restored to the foreground
    And the main screen should be displayed

  # ── Install / Uninstall (Advanced) ───────────────────────────────────────────

  @installation @advanced
  Scenario: Verify app is currently installed on device
    Then the app should be installed on the device

  # ── Current Activity (Android Only) ──────────────────────────────────────────

  @androidOnly @currentActivity
  Scenario: Verify the current Android activity on launch
    # AndroidDriver.currentActivity() returns the foreground activity class name
    Then the current Android activity should contain "ApiDemos"
