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
    Then the alert result should show "OK"

  @confirmAccept
  Scenario: Accept a confirm dialog
    When the user triggers a confirm dialog
    Then the alert should be displayed
    And  the alert title should be "Confirm"
    When the user accepts the alert
    Then the alert result should show "Confirmed"

  @confirmDismiss
  Scenario: Dismiss a confirm dialog
    When the user triggers a confirm dialog
    When the user dismisses the alert
    Then the alert result should show "Cancelled"

  @prompt
  Scenario: Enter text in a prompt dialog
    When the user triggers a prompt dialog
    And  the user enters "My Input" in the prompt
    And  the user accepts the alert
    Then the alert result should show "My Input"

  @alertMessage
  Scenario: Verify alert message content
    When the user triggers a simple alert
    Then the alert message should contain "This is an alert"
