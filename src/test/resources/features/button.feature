@button @smoke
Feature: Button Controls
  As a mobile user
  I want to interact with various button types
  So that I can verify button behaviors

  Background:
    Given the button controls screen is displayed

  @tap
  Scenario: Single tap on a button
    When the user taps the button
    Then the button result should contain "Tapped"

  @longPress
  Scenario: Long press on a button
    When the user long presses the button
    Then the button result should contain "Long Pressed"

  @doubleTap
  Scenario: Double tap on a button
    When the user double taps the button
    Then the button result should contain "Double Tapped"

  @visibility
  Scenario: Button visibility and enabled state
    Then the tap button should be displayed
    And  the tap button should be enabled

  @parameterized
  Scenario Outline: Tap named buttons
    When the user taps the button with text "<buttonText>"
    Then the button result should contain "<result>"
    Examples:
      | buttonText | result    |
      | Submit     | Submitted |
      | Cancel     | Cancelled |
      | Reset      | Reset     |
