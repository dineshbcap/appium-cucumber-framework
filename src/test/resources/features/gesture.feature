@gesture @smoke
Feature: Gesture Controls
  As a mobile user
  I want to perform various touch gestures
  So that I can verify gesture recognition behaviors

  Background:
    Given the gesture controls screen is displayed

  @swipeUp
  Scenario: Swipe up gesture
    When the user swipes up on the screen
    Then the app should remain responsive

  @swipeLeft
  Scenario: Swipe left gesture
    When the user swipes left on the screen
    Then the app should remain responsive

  # UIKitCatalog has no screen with a "Dropped" result label — that content
  # only exists in Appium's separate TestApp fixture. Android-only for now.
  @dragDrop @androidOnly
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
    Then the app should remain responsive

  @mobileSwipe
  Scenario: Swipe left using mobile: swipe command
    # mobile: swipeGesture (Android) / mobile: swipe (iOS)
    When the user swipes left using mobile command
    Then the app should remain responsive

  @mobileFling @androidOnly
  Scenario: Fling scroll using mobile: fling command (Android only)
    # mobile: flingGesture — Android UiAutomator2 only.
    # Simulates a fast swipe that continues scrolling with physics-based inertia.
    # There is NO W3C Actions equivalent for fling behavior.
    When the user flings down using mobile command
    Then the app should remain responsive
