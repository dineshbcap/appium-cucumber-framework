@switch @smoke
Feature: Switch / Toggle Controls
  As a mobile user
  I want to interact with switch and toggle controls
  So that I can verify on/off state changes

  Background:
    Given the switch controls screen is displayed

  @toggle
  Scenario: Toggle the Wi-Fi switch on
    When the user enables Wi-Fi
    Then the Wi-Fi switch should be on

  @toggleOff
  Scenario: Toggle the Wi-Fi switch off
    When the user enables Wi-Fi
    And  the user disables Wi-Fi
    Then the Wi-Fi switch should be off

  @bluetooth
  Scenario: Toggle the Bluetooth switch
    When the user toggles the Bluetooth switch
    Then the Bluetooth switch should be on

  @notifications
  Scenario: Toggle the Notifications switch
    When the user toggles the Notifications switch

  @count
  Scenario: Verify number of switches on screen
    Then the page should have 3 switches
