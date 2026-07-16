@locatorStrategies
Feature: Appium Locator Strategies
  As a mobile automation engineer
  I want to understand all Appium element locator strategies
  So that I can choose the right strategy for each element and platform

  Background:
    Given the app main screen is loaded for locator strategy tests

  # ── By.accessibilityId — Most Portable ────────────────────────────────────────

  # "Text" doesn't exist as literal element content in UIKitCatalog. Android-only.
  @smoke @accessibilityId @androidOnly
  Scenario: Find element by accessibility ID (most portable strategy)
    # Maps to content-desc on Android, accessibilityLabel on iOS
    # Best choice for cross-platform elements
    When the user finds element by accessibility ID "Text"
    Then the element should be found and visible

  # ── By.id — Fastest on Android ────────────────────────────────────────────────

  # "android:id/list" is an Android resource-id format — has no iOS equivalent.
  @smoke @byId @androidOnly
  Scenario: Find element by resource ID on Android
    # Uses the Android resource-id (e.g., "com.package:id/element_id")
    # Fastest locator strategy on Android when IDs are stable
    When the user finds element by ID "android:id/list"
    Then the element should be found and visible

  # ── By.className ──────────────────────────────────────────────────────────────

  # "android.widget.TextView" is an Android-only class name literal. Android-only.
  @className @androidOnly
  Scenario: Find all elements of a class type
    # Returns all TextViews on Android or all Labels on iOS
    # Useful for counting or iterating over collections
    When the user finds all elements by class name "android.widget.TextView"
    Then at least 1 element should be found

  # ── By.xpath — Most Flexible ───────────────────────────────────────────────────

  # @text is an Android-only XML attribute; doesn't exist on iOS elements. Android-only.
  @smoke @xpath @androidOnly
  Scenario: Find element by XPath
    # XPath is the most flexible but slowest strategy
    # Use short, stable XPaths — avoid deep hierarchical paths
    When the user finds element by XPath "//*[@text='Text']"
    Then the element should be found and visible

  # XPath itself is cross-platform-safe here, but no iOS element is literally
  # named "Text" in UIKitCatalog, so the content assumption doesn't hold. Android-only.
  @xpath @crossPlatform @androidOnly
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
    When the user finds iOS element by predicate string "name == 'Text Fields'"
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
    # for hierarchical queries.
    # The cell itself carries no "name" — only its child StaticText label does —
    # so the chain targets that leaf rather than the XCUIElementTypeCell wrapper.
    When the user finds iOS element by class chain "**/XCUIElementTypeStaticText[`name == 'Text Fields'`]"
    Then the element should be found and visible

  # ── PageFactory (Advanced) ────────────────────────────────────────────────────

  @pageFactory @advanced
  Scenario: Locate element using PageFactory dual-platform annotation
    # @AndroidFindBy and @iOSXCUITFindBy on same field — resolved at runtime
    When the user clicks the text nav item via PageFactory
    Then the app should navigate successfully

  # ── Selenium 4 Relative Locators (Appium 2.x support) ────────────────────────
  # RelativeLocator.with() finds elements based on their visual position
  # relative to a known anchor element on screen. Supported by Appium 2.x
  # via the W3C WebDriver protocol's "relative locator" endpoint.

  # "Text"/"Views" are ApiDemos-specific anchor labels with no iOS equivalent. Android-only.
  @relativeLocator @advanced @androidOnly
  Scenario: Find element below an anchor using RelativeLocator
    # RelativeLocator.with(By.className(...)).below(anchor)
    # Finds the first matching element whose top edge is below the anchor's bottom edge.
    # Use case: find a value/label that always appears directly below a known header.
    When the user finds element below the anchor with text "Text"
    Then the element should be found and visible

  @relativeLocator @advanced @androidOnly
  Scenario: Find element near an anchor using RelativeLocator with distance
    # RelativeLocator.with(By.className(...)).near(anchor, maxDistancePixels)
    # Useful for finding unlabeled elements (icons, buttons) adjacent to a known label.
    When the user finds element near the anchor with text "Text" within 200 pixels
    Then the element should be found and visible

  @relativeLocator @advanced @androidOnly
  Scenario: Chain multiple relative conditions on a single locator
    # RelativeLocator supports chaining: .below(topAnchor).above(bottomAnchor)
    # Finds an element positioned between two known anchors — useful for dense lists.
    When the user finds element between anchors "Text" and "Views"
    Then the element should be found and visible
