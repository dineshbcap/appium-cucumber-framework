@list @smoke
Feature: List and RecyclerView Controls
  As a mobile user
  I want to interact with lists and collections
  So that I can verify list item behaviors

  Background:
    Given the list controls screen is displayed

  @tapByIndex
  Scenario: Tap a list item by index
    When the user taps list item at index 0
    Then the list result should show "Item 1"

  @tapByText
  Scenario: Tap a list item by text
    When the user taps the list item "Accessibility"
    Then the list result should show "Accessibility"

  @scrollList
  Scenario: Scroll to a list item not visible on screen
    When the user scrolls to list item "Views"
    Then the list should contain item "Views"

  @listCount
  Scenario: Verify list has sufficient items
    Then the list should contain 5 items

  @parameterized
  Scenario Outline: Tap various list items
    When the user taps the list item "<item>"
    Then the list result should show "<item>"
    Examples:
      | item          |
      | Accessibility |
      | Animation     |
      | App           |
