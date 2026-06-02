@deepLink
Feature: Deep Linking
  As a mobile automation engineer
  I want to navigate to specific app screens using deep links
  So that I can skip UI navigation steps and test features in isolation

  # ── Android Deep Links ─────────────────────────────────────────────────────────

  @smoke @androidOnly @deepLink
  Scenario: Open Android app via URL scheme deep link
    # Simulates clicking a link in a browser or notification that opens the app
    When the user opens the Android deep link "content://media/external/images/media"
    Then the app should be running in the foreground

  @androidOnly @deepLink @intent
  Scenario: Navigate to app settings via intent deep link
    # Demonstrates using intent URIs to navigate to specific Android system screens
    When the user opens the Android deep link "android-app://io.appium.android.apis/.ApiDemos"
    Then the app should be running in the foreground

  # ── iOS Deep Links ─────────────────────────────────────────────────────────────

  @iosOnly @deepLink
  Scenario: Open iOS app via URL scheme
    # URL scheme must be registered in the app's Info.plist
    When the user opens the iOS URL "uikit://main"
    Then the app should be running in the foreground

  # ── Cross-Platform Deep Links ──────────────────────────────────────────────────

  @smoke @crossPlatform @deepLink
  Scenario: Verify app state after deep link navigation
    When the user opens a deep link to the app
    Then the app should be running in the foreground
    And the main screen should be displayed

  # ── Deep Link Benefits Demo ────────────────────────────────────────────────────

  @advancedUsage @deepLink
  Scenario: Deep link bypasses login to reach a specific screen
    # In real apps with URL schemes: myapp://profile/settings
    # This dramatically reduces test setup time vs tapping through 5 screens
    When the user opens a deep link to the app
    Then the app should be running in the foreground
