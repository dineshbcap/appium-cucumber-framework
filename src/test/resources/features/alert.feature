@alert @smoke
Feature: Alert and Dialog Controls
  As a mobile user
  I want to interact with alerts and dialogs
  So that I can verify modal dialog behaviors

  Background:
    Given the alert controls screen is displayed

  @simpleAlert
  Scenario: Trigger and accept a simple alert
    When the user triggers a simple alert
    Then the alert should be displayed
    When the user accepts the alert
    Then the alert should not be displayed

  @confirmDismiss
  Scenario: Dismiss a confirm dialog
    When the user triggers a confirm dialog
    Then the alert should be displayed
    When the user dismisses the alert
    Then the alert should not be displayed

  # UIKitCatalog's real alert title is always "A Short Title Is Best"
  # (Apple's generic sample boilerplate, reused across all alert styles) —
  # not "Text Entry dialog". Android-only until iOS gets its own assertion.
  @prompt @androidOnly
  Scenario: Enter text in a prompt dialog
    When the user triggers a prompt dialog
    Then the alert title should be "Text Entry dialog"
    When the user enters "My Input" in the prompt
    And  the user accepts the alert
    Then the alert should not be displayed

  # Real iOS alert message is "A message should be a short, complete sentence."
  # — no "Lorem ipsum" anywhere. Android-only until iOS gets its own assertion.
  @alertMessage @androidOnly
  Scenario: Verify alert message content
    When the user triggers a simple alert
    Then the alert message should contain "Lorem ipsum"
