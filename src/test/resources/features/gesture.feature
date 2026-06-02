@gesture @smoke
Feature: Gesture Controls
  As a mobile user
  I want to perform various touch gestures
  So that I can verify gesture recognition behaviors

  Background:
    Given the gesture controls screen is displayed

  @tap
  Scenario: Perform a single tap gesture
    When the user performs a tap gesture
    Then the gesture result should contain "Tap"

  @longPress
  Scenario: Perform a long press gesture
    When the user performs a long press gesture
    Then the gesture result should contain "Long Press"

  @doubleTap
  Scenario: Perform a double tap gesture
    When the user performs a double tap gesture
    Then the gesture result should contain "Double Tap"

  @swipeUp
  Scenario: Swipe up gesture
    When the user swipes up on the screen
    Then the gesture result should be displayed

  @swipeLeft
  Scenario: Swipe left gesture
    When the user swipes left on the screen
    Then the gesture result should be displayed

  @pinchZoom
  Scenario: Pinch to zoom in
    When the user pinches to zoom in
    Then the gesture result should be displayed

  @pinchOut
  Scenario: Pinch to zoom out
    When the user pinches to zoom out
    Then the gesture result should be displayed

  @dragDrop
  Scenario: Drag and drop gesture
    When the user drags the item to the drop zone
    Then the gesture result should contain "Dropped"
