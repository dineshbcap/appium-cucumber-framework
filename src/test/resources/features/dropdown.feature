@dropdown @smoke
Feature: Dropdown / Spinner Controls
  As a mobile user
  I want to interact with dropdowns and spinners
  So that I can verify selection from a list

  Background:
    Given the dropdown controls screen is displayed

  @open
  Scenario: Open and close dropdown
    When the user opens dropdown 1
    Then dropdown 1 should show "Option 1"

  @selectByText
  Scenario: Select option by text from dropdown 1
    When the user selects "Option 2" from dropdown 1
    Then dropdown 1 should show "Option 2"
    And  the dropdown result should contain "Option 2"

  @selectByIndex
  Scenario: Select option by index from dropdown 1
    When the user selects index 0 from dropdown 1
    Then dropdown 1 should show "Option 1"

  @multipleDropdowns
  Scenario: Interact with two dropdowns independently
    When the user selects "Option 1" from dropdown 1
    And  the user selects "Option 2" from dropdown 2
    Then dropdown 1 should show "Option 1"
    And  dropdown 2 should show "Option 2"

  @parameterized
  Scenario Outline: Select various options from dropdown
    When the user selects "<option>" from dropdown 1
    Then dropdown 1 should show "<option>"
    Examples:
      | option   |
      | Option 1 |
      | Option 2 |
      | Option 3 |
