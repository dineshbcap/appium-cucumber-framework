@webView @smoke
Feature: WebView / Hybrid App Controls
  As a mobile user
  I want to interact with WebView content
  So that I can verify hybrid app behaviors

  Background:
    Given the WebView controls screen is displayed

  @contextSwitch
  Scenario: Switch between native and WebView contexts
    When the user switches to the WebView context
    Then the current context should be WebView
    When the user switches to the native app context
    Then the current context should be native

  @availableContexts
  Scenario: Verify multiple contexts are available
    Then there should be at least 2 available contexts

  @navigation
  Scenario: Navigate within WebView
    When the user switches to the WebView context
    And  the user navigates to URL "https://example.com"
    Then the page title should contain "Example"
    And  the current URL should contain "example.com"

  @webViewBack
  Scenario: Navigate back in WebView
    When the user switches to the WebView context
    And  the user navigates to URL "https://example.com"
    And  the user goes back in the WebView

  @refresh
  Scenario: Refresh WebView page
    When the user switches to the WebView context
    And  the user refreshes the WebView
