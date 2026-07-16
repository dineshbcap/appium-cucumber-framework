@datePicker @smoke @androidOnly
# iOS's real date picker here is an inline compact control (tap to expand,
# no modal dialog, no OK/Cancel, no paged month grid) — a fundamentally
# different interaction model, not just different locators. Android-only.
Feature: Date Picker and Time Picker Controls
  As a mobile user
  I want to interact with date and time pickers
  So that I can verify temporal input behaviors

  Background:
    Given the date picker controls screen is displayed

  @openDatePicker
  Scenario: Open the date picker
    When the user opens the date picker
    Then the alert should be displayed

  @selectDate
  Scenario: Select a specific date
    When the user selects date 2025-6-15
    Then the selected date should contain "2025"

  @cancelDatePicker
  Scenario: Cancel the date picker without selecting
    When the user opens the date picker
    And  the user cancels the date picker

  @timePicker
  Scenario: Set a specific time
    When the user sets time to 10:30 "AM"
    Then the selected date should contain "10"

  @parameterized
  Scenario Outline: Select various dates
    When the user selects date <year>-<month>-<day>
    Then the selected date should contain "<yearStr>"
    Examples:
      | year | month | day | yearStr |
      | 2025 | 1     | 1   | 2025    |
      | 2025 | 12    | 31  | 2025    |
      | 2026 | 6     | 15  | 2026    |
