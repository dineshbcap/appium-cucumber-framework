@cloud
Feature: Cloud Device Farm Execution
  As a mobile automation engineer
  I want to run Appium 2.x tests on BrowserStack cloud
  So that I can test on real devices without maintaining a local device lab

  # ─── Prerequisites ────────────────────────────────────────────────────────────
  #
  # 1. Upload your app to BrowserStack App Automate:
  #
  #    Android:
  #    curl -u "username:access_key" \
  #         -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
  #         -F "file=@apps/ApiDemos-debug.apk"
  #    # Returns: {"app_url": "bs://abc123..."}
  #
  #    iOS:
  #    curl -u "username:access_key" \
  #         -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
  #         -F "file=@apps/UIKitCatalog.ipa"
  #
  # 2. Set these in config.properties (or pass as -D system properties):
  #
  #    cloud.provider=browserstack
  #    cloud.username=your_bs_username
  #    cloud.access.key=your_bs_access_key
  #    cloud.app.url=bs://abc123...
  #    cloud.device.name=Samsung Galaxy S22
  #    cloud.os.version=12.0
  #    cloud.project.name=Appium BDD Framework
  #    cloud.build.name=CI Build #1
  #
  # 3. Run cloud tests:
  #
  #    mvn test -Dtags="@cloud" \
  #             -Dcloud.provider=browserstack \
  #             -Dcloud.username=$BS_USER \
  #             -Dcloud.access.key=$BS_KEY \
  #             -Dcloud.app.url=$BS_APP_URL
  #
  #    # Or use the cloud Maven profile (see pom.xml):
  #    mvn test -P cloud
  #
  # ─── Notes ────────────────────────────────────────────────────────────────────
  #
  # - The @cloud tag causes Hooks.java to use CloudDriverFactory instead of
  #   DriverFactory — connecting to BrowserStack's hub URL instead of localhost.
  # - Appium 2.x is specified via bstack:options.appiumVersion = "2.0.0"
  # - BrowserStack manages driver installation (uiautomator2/xcuitest) automatically.
  # - Session status (passed/failed) is updated in the BrowserStack dashboard
  #   via the "browserstack_executor: setSessionStatus" JavascriptExecutor command.

  Background:
    Given the cloud session is configured for "BrowserStack"

  # ── Android cloud tests ────────────────────────────────────────────────────────

  @cloud @androidCloud
  Scenario: Find element on Android real device via BrowserStack
    # Connects to a real Samsung/Pixel/OnePlus Android device in BrowserStack's lab.
    # The driver is created with UiAutomator2Options + bstack:options W3C namespace.
    When the user finds element by accessibility ID "Text"
    Then the element should be found and visible
    And the cloud test session should be marked as passed

  @cloud @androidCloud
  Scenario: Verify app launch on Android cloud device
    # The app is referenced by its bs:// URL — BrowserStack installs it before the test.
    Then the session should be running on the configured cloud device
    And the cloud test session should be marked as passed

  # ── iOS cloud tests ────────────────────────────────────────────────────────────

  @cloud @iosCloud
  Scenario: Find element on iOS real device via BrowserStack
    # Connects to a real iPhone/iPad in BrowserStack's lab.
    # The driver is created with XCUITestOptions + bstack:options W3C namespace.
    When the user finds element by accessibility ID "Text Controls"
    Then the element should be found and visible
    And the cloud test session should be marked as passed

  # ── Parallel cloud tests ───────────────────────────────────────────────────────

  @cloud @parallelCloud
  Scenario Outline: Parallel cloud execution across device configurations
    # Run the same test scenario on multiple devices simultaneously.
    # In parallel mode, each thread connects to a different device in BrowserStack.
    # Set cloud.device.name and cloud.os.version per-thread via system properties.
    Then the session should be running on device "<device_name>" with OS "<os_version>"

    Examples:
      | device_name           | os_version |
      | Samsung Galaxy S22    | 12.0       |
      | Google Pixel 6        | 12.0       |
      | OnePlus 9             | 11.0       |

  # ── Session metadata ───────────────────────────────────────────────────────────

  @cloud @sessionMetadata
  Scenario: Verify session metadata matches cloud configuration
    # Confirms that the session was established with the expected device,
    # OS version, and Appium version set in bstack:options.
    Then the cloud session platform should match the configured platform
    And the cloud test session should be marked as passed