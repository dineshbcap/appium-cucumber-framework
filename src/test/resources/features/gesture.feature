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

  # ── mobile: gesture commands (Appium 2.x native engine delegation) ────────────
  # These use driver.executeScript("mobile: commandName", args) which delegates
  # to UiAutomator2 (Android) or XCUITest (iOS) for native gesture handling.
  # Benefit: simpler API, more reliable for complex gestures like fling/scroll.

  @mobileScroll
  Scenario: Scroll down using mobile: scroll command
    # mobile: scrollGesture (Android) / mobile: scroll (iOS)
    # Delegates scrolling to the native engine — preferred for iOS scroll
    When the user scrolls down using mobile command
    Then the gesture result should be displayed

  @mobileSwipe
  Scenario: Swipe left using mobile: swipe command
    # mobile: swipeGesture (Android) / mobile: swipe (iOS)
    When the user swipes left using mobile command
    Then the gesture result should be displayed

  @mobileDoubleTap
  Scenario: Double tap using mobile: double tap command
    # mobile: doubleClickGesture (Android) / mobile: doubleTap (iOS)
    # More reliable than two consecutive W3C Action taps
    When the user double taps using mobile command
    Then the gesture result should be displayed

  @mobileLongPress
  Scenario: Long press using mobile: long press command
    # mobile: longClickGesture (Android) / mobile: longPress (iOS)
    When the user long presses using mobile command for 2000 milliseconds
    Then the gesture result should be displayed

  @mobileFling @androidOnly
  Scenario: Fling scroll using mobile: fling command (Android only)
    # mobile: flingGesture — Android UiAutomator2 only.
    # Simulates a fast swipe that continues scrolling with physics-based inertia.
    # There is NO W3C Actions equivalent for fling behavior.
    When the user flings down using mobile command
    Then the gesture result should be displayed

  @mobilePinch
  Scenario: Pinch open (zoom in) using mobile: pinch command
    # Android: mobile: pinchOpenGesture / iOS: mobile: pinch with scale > 1
    When the user pinches open using mobile command
    Then the gesture result should be displayed

  @mobilePinchClose
  Scenario: Pinch close (zoom out) using mobile: pinch command
    # Android: mobile: pinchCloseGesture / iOS: mobile: pinch with scale < 1
    When the user pinches close using mobile command
    Then the gesture result should be displayed
