@textInput @smoke
Feature: Text Input Controls
  As a mobile user
  I want to interact with text input fields
  So that I can verify text entry behaviors

  Background:
    Given the text input controls screen is displayed

  @typing
  Scenario: Enter text in a text field
    When the user enters "Hello Appium" in the text field
    Then the text field should contain "Hello Appium"

  @clear
  Scenario: Clear a text field
    When the user enters "Some text" in the text field
    And  the user clears the text field
    Then the text field should be empty

  @password
  Scenario: Enter password in password field
    When the user enters password "P@ssword123"
    Then the password field should be focused

  @append
  Scenario: Append text to existing input
    When the user enters "Hello" in the text field
    And  the user appends " World" to the text field
    Then the text field should contain "Hello World"

  @parameterized
  Scenario Outline: Enter various input types
    When the user enters "<input>" in the text field
    Then the text field should contain "<input>"
    Examples:
      | input             |
      | Hello World       |
      | 1234567890        |
      | user@example.com  |
      | Special !@#$%     |
