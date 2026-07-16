@dropdown @smoke @androidOnly
# UIKitCatalog's only picker (Picker View) is 3 numeric RGB wheels (0-255),
# not a single-column text picker — none of this feature's text options
# ("red", "Mars", etc.) exist on iOS here. Android-only by app content.
Feature: Dropdown / Spinner Controls
  As a mobile user
  I want to interact with dropdowns and spinners
  So that I can verify selection from a list

  Background:
    Given the dropdown controls screen is displayed

  @open
  Scenario: Open and close dropdown
    When the user opens dropdown 1
    And  the user closes the dropdown
    Then dropdown 1 should show "red"

  @selectByText
  Scenario: Select option by text from dropdown 1
    When the user selects "green" from dropdown 1
    Then dropdown 1 should show "green"

  @selectByIndex
  Scenario: Select option by index from dropdown 1
    When the user selects index 0 from dropdown 1
    Then dropdown 1 should show "red"

  @multipleDropdowns
  Scenario: Interact with two dropdowns independently
    When the user selects "blue" from dropdown 1
    And  the user selects "Mars" from dropdown 2
    Then dropdown 1 should show "blue"
    And  dropdown 2 should show "Mars"

  @parameterized
  Scenario Outline: Select various options from dropdown
    When the user selects "<option>" from dropdown 1
    Then dropdown 1 should show "<option>"
    Examples:
      | option |
      | red    |
      | orange |
      | yellow |
      | green  |
      | blue   |
      | violet |
