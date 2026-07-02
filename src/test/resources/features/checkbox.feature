@checkbox @smoke
Feature: Checkbox Controls
  As a mobile user
  I want to interact with checkboxes
  So that I can verify checkbox selection behaviors

  Background:
    Given the checkbox controls screen is displayed

  @check
  Scenario: Check a single checkbox
    When the user checks checkbox 1
    Then checkbox 1 should be checked

  @uncheck
  Scenario: Uncheck a checked checkbox
    When the user checks checkbox 1
    And  the user unchecks checkbox 1
    Then checkbox 1 should be unchecked

  @toggle
  Scenario: Toggle multiple checkboxes
    When the user toggles checkbox 1
    And  the user toggles checkbox 2
    Then checkbox 1 should be checked
    And  checkbox 2 should be checked

  @count
  Scenario: Verify total number of checkboxes
    Then the page should have 3 checkboxes
