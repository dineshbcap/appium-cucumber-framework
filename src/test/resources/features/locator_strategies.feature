@locatorStrategies
Feature: Appium Locator Strategies
  As a mobile automation engineer
  I want to understand all Appium element locator strategies
  So that I can choose the right strategy for each element and platform

  Background:
    Given the app main screen is loaded for locator strategy tests

  # ── By.accessibilityId — Most Portable ────────────────────────────────────────

  @smoke @accessibilityId
  Scenario: Find element by accessibility ID (most portable strategy)
    # Maps to content-desc on Android, accessibilityLabel on iOS
    # Best choice for cross-platform elements
    When the user finds element by accessibility ID "Text"
    Then the element should be found and visible

  # ── By.id — Fastest on Android ────────────────────────────────────────────────

  @smoke @byId
  Scenario: Find element by resource ID on Android
    # Uses the Android resource-id (e.g., "com.package:id/element_id")
    # Fastest locator strategy on Android when IDs are stable
    When the user finds element by ID "io.appium.android.apis:id/list"
    Then the element should be found and visible

  # ── By.className ──────────────────────────────────────────────────────────────

  @className
  Scenario: Find all elements of a class type
    # Returns all TextViews on Android or all Labels on iOS
    # Useful for counting or iterating over collections
    When the user finds all elements by class name "android.widget.TextView"
    Then at least 1 element should be found

  # ── By.xpath — Most Flexible ───────────────────────────────────────────────────

  @smoke @xpath
  Scenario: Find element by XPath
    # XPath is the most flexible but slowest strategy
    # Use short, stable XPaths — avoid deep hierarchical paths
    When the user finds element by XPath "//*[@text='Text']"
    Then the element should be found and visible

  @xpath @crossPlatform
  Scenario: Find element by text using cross-platform XPath
    # @text works on Android, @label/@name works on iOS
    # Use OR to create a single XPath that works on both
    When the user finds element by visible text "Text"
    Then the element should be found and visible

  # ── UiAutomator2 (Android Only) ───────────────────────────────────────────────

  @androidOnly @uiAutomator
  Scenario: Find element using UiSelector text query
    # Android-native UI Automator — fast and supports complex queries
    When the user finds Android element by UiSelector "new UiSelector().text(\"Text\")"
    Then the element should be found and visible

  @androidOnly @uiAutomator
  Scenario: Find element using UiSelector textContains
    When the user finds Android element by UiSelector "new UiSelector().textContains(\"Text\")"
    Then the element should be found and visible

  @androidOnly @uiAutomator
  Scenario: Find element using UiSelector className and instance
    # Find the 1st TextView in the list (0-based index)
    When the user finds Android element by UiSelector "new UiSelector().className(\"android.widget.TextView\").instance(0)"
    Then the element should be found and visible

  # ── iOS Predicate String (iOS Only) ───────────────────────────────────────────

  @iosOnly @predicateString
  Scenario: Find element using NSPredicate string
    # NSPredicate is faster than XPath and supports compound queries
    When the user finds iOS element by predicate string "name == 'Text Controls'"
    Then the element should be found and visible

  @iosOnly @predicateString
  Scenario: Find element using NSPredicate with CONTAINS
    When the user finds iOS element by predicate string "name CONTAINS 'Text'"
    Then the element should be found and visible

  @iosOnly @predicateString
  Scenario: Find element using compound NSPredicate
    # Multiple conditions joined with AND/OR — very powerful for precise targeting
    When the user finds iOS element by predicate string "type == 'XCUIElementTypeCell' AND visible == true"
    Then the element should be found and visible

  # ── iOS Class Chain (iOS Only) ────────────────────────────────────────────────

  @iosOnly @classChain
  Scenario: Find element using iOS Class Chain
    # Class Chain is faster than XPath and more readable than Predicate String
    # for hierarchical queries
    When the user finds iOS element by class chain "**/XCUIElementTypeCell[`name == 'Text Controls'`]"
    Then the element should be found and visible

  # ── PageFactory (Advanced) ────────────────────────────────────────────────────

  @pageFactory @advanced
  Scenario: Locate element using PageFactory dual-platform annotation
    # @AndroidFindBy and @iOSXCUITFindBy on same field — resolved at runtime
    When the user clicks the text nav item via PageFactory
    Then the app should navigate successfully
