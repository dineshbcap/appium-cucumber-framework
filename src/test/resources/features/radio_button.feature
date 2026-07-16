@radioButton @smoke @androidOnly
# iOS/UIKit has no native radio button control. Segmented Control is the
# closest analog but is a single multi-segment widget (3-5 segments), not
# N independent selectable buttons — Android-only by platform design.
Feature: Radio Button Controls
  As a mobile user
  I want to interact with radio buttons
  So that I can verify single-selection behavior

  Background:
    Given the radio button controls screen is displayed

  @select
  Scenario: Select the first radio button
    When the user selects radio button 1
    Then radio button 1 should be selected

  @mutualExclusion
  Scenario: Selecting a new radio deselects the previous
    When the user selects radio button 1
    And  the user selects radio button 2
    Then radio button 2 should be selected
    And  radio button 1 should not be selected

  @byIndex
  Scenario: Select radio button by index
    When the user selects radio button at index 1
    Then the selected radio index should be 1

  @count
  Scenario: Verify number of radio buttons
    Then there should be 2 radio buttons

  @parameterized
  Scenario Outline: Select each radio button
    When the user selects radio button <number>
    Then radio button <number> should be selected
    Examples:
      | number |
      | 1      |
      | 2      |
