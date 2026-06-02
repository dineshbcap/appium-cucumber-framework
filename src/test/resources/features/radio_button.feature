@radioButton @smoke
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
    When the user selects radio button at index 2
    Then the selected radio index should be 2

  @count
  Scenario: Verify number of radio buttons
    Then there should be 3 radio buttons

  @parameterized
  Scenario Outline: Select each radio button
    When the user selects radio button <number>
    Then radio button <number> should be selected
    Examples:
      | number |
      | 1      |
      | 2      |
      | 3      |
