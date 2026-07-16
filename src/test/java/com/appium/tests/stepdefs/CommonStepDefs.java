package com.appium.tests.stepdefs;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import com.appium.framework.utils.AppUtils;
import io.appium.java_client.AppiumBy;
import io.cucumber.java.en.Given;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

/**
 * Shared Background step definitions that navigate to the correct screen
 * before each feature's scenarios run.
 *
 * <p>These rely on ApiDemos-style navigation on Android and UIKitCatalog on iOS.
 * Adjust screen names to match your target app's navigation labels.</p>
 *
 * <p><b>Concept demonstrated:</b> Using the platform-appropriate scrolling strategy
 * (UiScrollable on Android, XPath on iOS) to reach any screen by name without
 * hard-coding coordinates.</p>
 */
public class CommonStepDefs {

    private static final Logger log = LogManager.getLogger(CommonStepDefs.class);

    // ── Existing Screens ──────────────────────────────────────────────────────

    @Given("the button controls screen is displayed")
    public void navigateToButtonScreen() {
        navigateToScreen("Views", "Buttons");
    }

    @Given("the text input controls screen is displayed")
    public void navigateToTextInputScreen() {
        if (ConfigReader.isIOS()) {
            navigateToScreen("Text Fields");
        } else {
            navigateToScreen("Views", "TextFields");
        }
    }

    @Given("the checkbox controls screen is displayed")
    public void navigateToCheckboxScreen() {
        navigateToScreen("Views", "Controls", "1. Light Theme");
    }

    @Given("the radio button controls screen is displayed")
    public void navigateToRadioScreen() {
        navigateToScreen("Views", "Controls", "1. Light Theme");
    }

    @Given("the dropdown controls screen is displayed")
    public void navigateToDropdownScreen() {
        navigateToScreen("Views", "Spinner");
    }

    @Given("the slider controls screen is displayed")
    public void navigateToSliderScreen() {
        if (ConfigReader.isIOS()) {
            navigateToScreen("Sliders");
        } else {
            navigateToScreen("Views", "Seek Bar");
        }
    }

    @Given("the scroll controls screen is displayed")
    public void navigateToScrollScreen() {
        // Android: the "Views" submenu (~40 items) is a real, naturally scrollable
        // list — no dedicated "Scroll View" screen with top/bottom markers exists.
        // iOS: UIKitCatalog's root screen (18 items) serves the same purpose and
        // is already displayed on launch — no navigation needed.
        if (!ConfigReader.isIOS()) {
            navigateToScreen("Views");
        }
    }

    @Given("the alert controls screen is displayed")
    public void navigateToAlertScreen() {
        if (ConfigReader.isIOS()) {
            navigateToScreen("Alert Views");
        } else {
            navigateToScreen("App", "Alert Dialogs");
        }
    }

    @Given("the date picker controls screen is displayed")
    public void navigateToDatePickerScreen() {
        navigateToScreen("Views", "Date Widgets", "1. Dialog");
    }

    @Given("the gesture controls screen is displayed")
    public void navigateToGestureScreen() {
        // Android: "Drag and Drop" is the only gesture demo with a real,
        // observable result (used by the @dragDrop scenario, @androidOnly).
        // iOS: the swipe/scroll scenarios that remain are content-agnostic —
        // UIKitCatalog's root screen works fine as the swipe surface.
        if (!ConfigReader.isIOS()) {
            navigateToScreen("Views", "Drag and Drop");
        }
    }

    @Given("the switch controls screen is displayed")
    public void navigateToSwitchScreen() {
        navigateToScreen("Views", "Switches");
    }

    @Given("the list controls screen is displayed")
    public void navigateToListScreen() {
        // Both apps open on a list-like root screen by default — no navigation needed
        log.info("List screen is already displayed (app home)");
    }

    @Given("the WebView controls screen is displayed")
    public void navigateToWebViewScreen() {
        if (ConfigReader.isIOS()) {
            navigateToScreen("Web View");
        } else {
            navigateToScreen("Views", "WebView");
        }
    }

    // ── New Advanced Screens ───────────────────────────────────────────────────

    /**
     * Navigates to the App Lifecycle demo screen.
     * On ApiDemos this is the home screen itself (app always starts here).
     */
    @Given("the app lifecycle screen is displayed")
    public void navigateToAppLifecycleScreen() {
        log.info("App lifecycle screen — ensuring app is active and on main screen");
        if (!AppUtils.isAppInForeground()) {
            AppUtils.activateApp();
        }
    }

    /**
     * Navigates to the keyboard/text input screen for keyboard interaction tests.
     * Reuses the text input screen which has a text field and triggers the keyboard.
     */
    @Given("the keyboard screen is displayed")
    public void navigateToKeyboardScreen() {
        if (ConfigReader.isIOS()) {
            navigateToScreen("Text Fields");
        } else {
            navigateToScreen("Views", "TextFields");
        }
    }

    /**
     * Navigates to the real clipboard demo screen (Content &gt; Clipboard &gt; Data Types).
     */
    @Given("the clipboard screen is displayed")
    public void navigateToClipboardScreen() {
        navigateToScreen("Content", "Clipboard", "Data Types");
    }

    /**
     * Navigates to the device orientation test screen.
     * Uses the Views section which has orientable layouts.
     */
    @Given("the orientation screen is displayed")
    public void navigateToOrientationScreen() {
        log.info("Orientation screen — any screen works for rotation testing");
    }

    /**
     * Navigates to the biometric authentication demo screen.
     */
    @Given("the biometric screen is displayed")
    public void navigateToBiometricScreen() {
        navigateToScreen("Security");
    }

    /**
     * Navigates to the permissions demo screen.
     */
    @Given("the permissions screen is displayed")
    public void navigateToPermissionsScreen() {
        navigateToScreen("OS");
    }

    /**
     * Navigates to the deep link demo screen (starts at app root for deep link tests).
     */
    @Given("the deep link screen is displayed")
    public void navigateToDeepLinkScreen() {
        log.info("Deep link test — starting from app root");
    }

    /**
     * Navigates to the locator strategy demo screen (main list page of ApiDemos).
     */
    @Given("the locator strategy screen is displayed")
    public void navigateToLocatorStrategyScreen() {
        log.info("Locator strategy test — ApiDemos main list is the test surface");
    }

    // ── Private Helper ────────────────────────────────────────────────────────

    /**
     * Navigates through a sequence of nested list screens by visible text label,
     * using UiScrollable (Android) or XPath text match (iOS). Each element in
     * {@code screenPath} is tapped in order — e.g. {@code ("Views", "Buttons")}
     * first taps "Views" on the ApiDemos home list, then "Buttons" on the
     * resulting submenu. The UiScrollable approach is much faster than
     * pixel-based gestures because it uses the Android Accessibility framework.
     *
     * @param screenPath the visible text labels to tap, in navigation order
     */
    private void navigateToScreen(String... screenPath) {
        for (String screenName : screenPath) {
            log.info("Navigating to screen: '{}'", screenName);
            if (ConfigReader.isAndroid()) {
                try {
                    // UiScrollable scrolls the list AND returns the element — all in one call
                    By uiScrollable = AppiumBy.androidUIAutomator(
                            "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().text(\"" + screenName + "\"))");
                    DriverManager.getDriver().findElement(uiScrollable).click();
                } catch (Exception e) {
                    log.warn("UiScrollable failed for '{}', trying XPath direct click", screenName);
                    DriverManager.getDriver()
                            .findElement(By.xpath("//*[@text='" + screenName + "']"))
                            .click();
                }
            } else {
                // iOS: scroll through the table to find the named cell
                try {
                    DriverManager.getDriver()
                            .findElement(By.xpath(
                                    "//XCUIElementTypeCell[.//XCUIElementTypeStaticText[@name='" + screenName + "']]"))
                            .click();
                } catch (Exception e) {
                    log.warn("Could not navigate to '{}' on iOS: {}", screenName, e.getMessage());
                }
            }
        }
    }
}
