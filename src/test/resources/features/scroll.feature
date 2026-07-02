@scroll @smoke
Feature: Scroll and Swipe Controls
  As a mobile user
  I want to scroll and swipe through content
  So that I can reach elements off-screen

  Background:
    Given the scroll controls screen is displayed

  @scrollDown
  Scenario: Scroll down to the bottom of a list
    When the user scrolls to the bottom of the list
    Then the bottom of the list should be visible

  @scrollUp
  Scenario: Scroll back up to the top
    When the user scrolls to the bottom of the list
    And  the user scrolls to the top of the list
    Then the top of the list should be visible

  @swipeDirections
  Scenario: Scroll down and back up
    # This list has no horizontal scroll behavior — a horizontal swipe here
    # doesn't scroll anything, it just risks landing as a tap on whatever
    # item is under the touch point. Only vertical scrolling is meaningful.
    When the user scrolls down
    And  the user scrolls to the top of the list
    Then the top of the list should be visible

  @scrollToText
  Scenario: Scroll to a specific element by text
    When the user scrolls to the element with text "WebView3"
    Then the bottom of the list should be visible
