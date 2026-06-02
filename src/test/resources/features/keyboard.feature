@keyboard
Feature: Keyboard Interaction
  As a mobile automation engineer
  I want to interact with the on-screen keyboard and hardware key events
  So that I can test text entry, form submission, and key-press behaviors

  Background:
    Given the text input controls screen is displayed

  # ── Keyboard Visibility ────────────────────────────────────────────────────────

  @smoke @visibility
  Scenario: Keyboard appears when text field is focused
    When the user taps the text input field
    Then the keyboard should be visible

  @visibility
  Scenario: Keyboard dismisses when hide keyboard is called
    When the user taps the text input field
    Then the keyboard should be visible
    When the keyboard is dismissed
    Then the keyboard should not be visible

  # ── Text Entry ─────────────────────────────────────────────────────────────────

  @smoke @typing
  Scenario: Type text into a field
    When the user types "Hello Appium" in the keyboard page field
    Then the text field should display "Hello Appium"

  @typing
  Scenario: Clear field using backspace key
    When the user types "ABC" in the keyboard page field
    And the user presses the Backspace key
    Then the text field should display "AB"

  @typing
  Scenario: Clear and retype in a field
    When the user types "First text" in the keyboard page field
    And the user clears the field and types "New text"
    Then the text field should display "New text"

  # ── Android Key Events ─────────────────────────────────────────────────────────

  @androidOnly @keyEvents
  Scenario: Press Enter key to submit text
    When the user types "TestInput" in the keyboard page field
    And the user presses the Enter key
    Then the form result should show "TestInput"

  @androidOnly @keyEvents
  Scenario: Press Tab key to move focus between fields
    When the user taps the text input field
    And the user presses the Tab key
    Then the keyboard should be visible

  @androidOnly @keyEvents
  Scenario: Press Volume Up key
    When the user presses the Volume Up key
    # Volume UI briefly appears — just verify no crash occurred
    Then the keyboard should not be visible

  @androidOnly @keyEvents
  Scenario: Press Volume Down key
    When the user presses the Volume Down key
    Then the keyboard should not be visible

  # ── Done / Return Action ───────────────────────────────────────────────────────

  @smoke @doneAction
  Scenario: Press keyboard Done to dismiss and submit
    When the user types "SubmitMe" in the keyboard page field
    And the user presses Done on the keyboard
    Then the keyboard should not be visible

  # ── Keyboard Type Scenarios ────────────────────────────────────────────────────

  @keyboardTypes
  Scenario Outline: Enter various character types via keyboard
    When the user types "<input>" in the keyboard page field
    Then the text field should display "<input>"
    Examples:
      | input              |
      | Hello World        |
      | 1234567890         |
      | user@example.com   |
      | !@#$%^&*()         |
