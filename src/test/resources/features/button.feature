@button @smoke
Feature: Button Controls
  As a mobile user
  I want to interact with various button types
  So that I can verify button behaviors

  Background:
    Given the button controls screen is displayed

  @tap
  Scenario: Tap the Normal button
    When the user taps the Normal button
    Then the Normal button should remain displayed and enabled

  @tap
  Scenario: Tap the Small button
    When the user taps the Small button
    Then the Normal button should remain displayed and enabled

  @toggle
  Scenario: Toggle button switches between ON and OFF
    Then the toggle button should show "OFF"
    When the user taps the toggle button
    Then the toggle button should show "ON"
    When the user taps the toggle button
    Then the toggle button should show "OFF"

  @visibility
  Scenario: Button visibility and enabled state
    Then the tap button should be displayed
    And  the tap button should be enabled

  @parameterized
  Scenario Outline: Tap named buttons
    When the user taps the button labeled "<label>"
    Then the Normal button should remain displayed and enabled
    Examples:
      | label  |
      | Normal |
      | Small  |
