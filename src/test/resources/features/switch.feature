@switch @smoke @androidOnly
# UIKitCatalog's Switches screen has exactly 1 unnamed switch, vs. the 3
# individually-named switches and count of 9 this feature requires —
# Android-only by app content.
Feature: Switch / Toggle Controls
  As a mobile user
  I want to interact with switch and toggle controls
  So that I can verify on/off state changes

  Background:
    Given the switch controls screen is displayed

  @toggle
  Scenario: Toggle the standard switch on
    When the user toggles the standard switch
    Then the standard switch should be on

  @toggleOff
  Scenario: Toggle the standard switch off after enabling
    When the user toggles the standard switch
    And  the user toggles the standard switch
    Then the standard switch should be off

  @defaultOn
  Scenario: Default-on switch starts enabled
    Then the default-on switch should be on

  @monitored
  Scenario: Toggle the monitored switch
    When the user toggles the monitored switch
    Then the monitored switch should be on

  @count
  Scenario: Verify number of switches on screen
    Then the page should have 9 switches
