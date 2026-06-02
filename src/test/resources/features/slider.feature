@slider @smoke
Feature: Slider / SeekBar Controls
  As a mobile user
  I want to interact with sliders and seek bars
  So that I can verify range input behaviors

  Background:
    Given the slider controls screen is displayed

  @min
  Scenario: Slide to minimum value
    When the user slides to minimum
    Then the slider value should be "0"

  @max
  Scenario: Slide to maximum value
    When the user slides to maximum
    Then the slider value should be "100"

  @midpoint
  Scenario: Slide to 50 percent
    When the user slides to 50 percent
    Then the slider label should show "50"

  @custom
  Scenario: Set slider to custom percentage
    When the user sets the slider to 75%
    Then the slider label should show "75"

  @parameterized
  Scenario Outline: Set slider to various values
    When the user sets the slider to <percent>%
    Then the slider label should show "<label>"
    Examples:
      | percent | label |
      | 0       | 0     |
      | 25      | 25    |
      | 50      | 50    |
      | 100     | 100   |
