@clipboard
Feature: Clipboard Operations
  As a mobile automation engineer
  I want to interact with the device clipboard
  So that I can verify copy-to-clipboard features and paste behaviors

  # ── Set Clipboard via API ──────────────────────────────────────────────────────

  @smoke @setClipboard
  Scenario: Set clipboard text via Appium API
    When the clipboard is set to "Hello Clipboard"
    Then the clipboard should contain "Hello Clipboard"

  # ── Copy via UI ────────────────────────────────────────────────────────────────

  @smoke @copyViaUi
  Scenario: Copy content using the app's Copy button
    Given the clipboard screen is displayed
    When the user taps the copy button
    Then the clipboard should contain "Plain, bold, italic, bold-italic"

  # ── Paste via UI ───────────────────────────────────────────────────────────────

  @smoke @pasteViaUi
  Scenario: Paste clipboard content into a real text field
    Given the text input controls screen is displayed
    When the clipboard is set to "Pasted Value"
    And  the user pastes into the text field
    Then the text field should contain the pasted text "Pasted Value"

  # ── Clear Clipboard ────────────────────────────────────────────────────────────

  @clearClipboard
  Scenario: Clear clipboard content
    When the clipboard is set to "Some content"
    And the clipboard is cleared
    Then the clipboard should contain ""

  # ── Clipboard Persistence ──────────────────────────────────────────────────────

  @persistence
  Scenario: Clipboard content persists across field interactions
    Given the text input controls screen is displayed
    When the clipboard is set to "Persistent Text"
    And the user taps the text input field
    Then the clipboard should contain "Persistent Text"

  # ── Multiple Clipboard Values ──────────────────────────────────────────────────

  @multiple
  Scenario: Overwrite clipboard with new value
    When the clipboard is set to "First value"
    And the clipboard is set to "Second value"
    Then the clipboard should contain "Second value"

  # ── Parameterized Copy Tests ───────────────────────────────────────────────────

  @parameterized
  Scenario Outline: Copy various text types to clipboard
    When the clipboard is set to "<content>"
    Then the clipboard should contain "<content>"
    Examples:
      | content             |
      | Simple text         |
      | 123456789           |
      | user@example.com    |
      | https://example.com |
