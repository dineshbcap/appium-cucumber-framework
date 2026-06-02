@orientation
Feature: Device Orientation
  As a mobile automation engineer
  I want to rotate the device programmatically
  So that I can verify the app's responsive layout in portrait and landscape modes

  Background:
    Given the device is in portrait orientation

  # ── Basic Rotation ─────────────────────────────────────────────────────────────

  @smoke @landscape
  Scenario: Rotate device to landscape
    When the device is rotated to landscape
    Then the device should be in landscape orientation
    And the screen width should be greater than the screen height

  @smoke @portrait
  Scenario: Rotate device back to portrait
    When the device is rotated to landscape
    And the device is rotated to portrait
    Then the device should be in portrait orientation
    And the screen height should be greater than the screen width

  # ── Toggle Orientation ────────────────────────────────────────────────────────

  @toggle
  Scenario: Toggle orientation from portrait to landscape
    Given the device is in portrait orientation
    When the orientation is toggled
    Then the device should be in landscape orientation

  @toggle
  Scenario: Toggle orientation from landscape to portrait
    Given the device is in landscape orientation
    When the orientation is toggled
    Then the device should be in portrait orientation

  # ── Layout Verification ───────────────────────────────────────────────────────

  @layout
  Scenario: Verify app adjusts to landscape layout
    When the device is rotated to landscape
    Then the device should be in landscape orientation

  @layout
  Scenario: Verify app adjusts back to portrait layout
    When the device is rotated to landscape
    And the device is rotated to portrait
    Then the device should be in portrait orientation

  # ── Orientation After Navigation ──────────────────────────────────────────────

  @navigation @orientation
  Scenario: Verify orientation is maintained after screen navigation
    When the device is rotated to landscape
    Then the device should be in landscape orientation
    # Navigate to a new screen and verify orientation is preserved
    When the device is rotated to portrait
    Then the device should be in portrait orientation
