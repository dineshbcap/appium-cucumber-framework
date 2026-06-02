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
  Scenario: Swipe in all four directions
    When the user swipes up on the screen
    And  the user swipes down on the screen
    And  the user swipes left on the screen
    And  the user swipes right on the screen
    Then the top of the list should be visible

  @scrollToText
  Scenario: Scroll to a specific element by text
    When the user scrolls to the element with text "Item 20"
    Then the top of the list should be visible
