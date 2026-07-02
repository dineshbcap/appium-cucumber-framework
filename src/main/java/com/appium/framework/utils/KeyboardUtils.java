package com.appium.framework.utils;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.util.Map;

/**
 * Keyboard interaction utilities for both Android and iOS.
 *
 * <p><b>Concepts covered:</b>
 * <ul>
 *   <li><b>Hide/Show keyboard</b> — the {@code mobile:hideKeyboard} execute script command
 *       works on both platforms. Android also supports {@code AppiumDriver.hideKeyboard()}</li>
 *   <li><b>Android Key Events</b> — {@link AndroidDriver#pressKey(KeyEvent)} sends hardware key
 *       events such as Enter, Backspace, Back, Home, Volume keys</li>
 *   <li><b>iOS keyboard keys</b> — Use {@code WebElement.sendKeys(Keys.RETURN)} or
 *       locate the "Done"/"Return" button on the virtual keyboard by accessibility ID</li>
 *   <li><b>isKeyboardShown</b> — polling keyboard visibility using the mobile:isKeyboardShown
 *       execute script command, which is consistent across driver versions</li>
 * </ul>
 * </p>
 *
 * <p><b>Android vs iOS differences:</b>
 * <ul>
 *   <li>Android exposes hardware key codes via {@link AndroidKey} enum (Enter=66, Backspace=67)</li>
 *   <li>iOS uses the XCUITest tap mechanic on virtual keyboard keys by label</li>
 *   <li>On iOS, {@code mobile:hideKeyboard} offers a strategy parameter ("pressKey", "tapOutside")</li>
 * </ul>
 * </p>
 */
public class KeyboardUtils {

    private static final Logger log = LogManager.getLogger(KeyboardUtils.class);

    private KeyboardUtils() {}

    // ── Visibility ─────────────────────────────────────────────────────────────

    /**
     * Checks whether the on-screen keyboard is currently visible.
     *
     * <p>Uses the {@code mobile:isKeyboardShown} execute script command which is
     * supported by both UiAutomator2 (Android) and XCUITest (iOS) drivers.</p>
     *
     * @return {@code true} if the software keyboard is visible on screen
     */
    public static boolean isKeyboardShown() {
        try {
            Object result = DriverManager.getDriver()
                    .executeScript("mobile:isKeyboardShown");
            boolean shown = Boolean.TRUE.equals(result);
            log.debug("Keyboard shown: {}", shown);
            return shown;
        } catch (Exception e) {
            log.debug("Could not check keyboard visibility: {}", e.getMessage());
            return false;
        }
    }

    // ── Hide Keyboard ──────────────────────────────────────────────────────────

    /**
     * Hides the on-screen keyboard if it is currently visible.
     *
     * <p>On <b>Android</b>: uses the UiAutomator2 hide keyboard command.</p>
     * <p>On <b>iOS</b>: uses the {@code mobile:hideKeyboard} command with the
     * "pressKey" strategy targeting the "Done" key. If that fails it falls back to
     * tapping outside the text field.</p>
     */
    public static void hideKeyboard() {
        if (!isKeyboardShown()) {
            log.debug("Keyboard already hidden — skipping");
            return;
        }
        log.info("Hiding keyboard");
        try {
            if (ConfigReader.isAndroid()) {
                DriverManager.getDriver().executeScript("mobile:hideKeyboard");
            } else {
                // iOS: try pressing Done first, then fall back to tapOutside
                DriverManager.getDriver().executeScript("mobile:hideKeyboard",
                        Map.of("strategy", "pressKey", "key", "Done"));
            }
        } catch (Exception e) {
            log.warn("Could not hide keyboard via script, trying tapOutside: {}", e.getMessage());
            try {
                DriverManager.getDriver().executeScript("mobile:hideKeyboard",
                        Map.of("strategy", "tapOutside"));
            } catch (Exception e2) {
                log.warn("Could not hide keyboard: {}", e2.getMessage());
            }
        }
    }

    /**
     * Hides the iOS keyboard using a specific strategy.
     *
     * <p>iOS strategies supported by Appium XCUITest driver:
     * <ul>
     *   <li>{@code "pressKey"} — presses the named key (e.g., "Done", "Return")</li>
     *   <li>{@code "tapOutside"} — taps an area outside the keyboard</li>
     * </ul>
     * </p>
     *
     * @param strategy the hide strategy to use ("pressKey" or "tapOutside")
     * @param key      the key to press when strategy is "pressKey" (e.g., "Done")
     */
    public static void hideIosKeyboardWithStrategy(String strategy, String key) {
        if (!ConfigReader.isIOS()) {
            log.warn("hideIosKeyboardWithStrategy is iOS-only");
            return;
        }
        log.info("Hiding iOS keyboard with strategy='{}', key='{}'", strategy, key);
        DriverManager.getDriver().executeScript("mobile:hideKeyboard",
                Map.of("strategy", strategy, "key", key));
    }

    // ── Android Key Events ─────────────────────────────────────────────────────

    /**
     * Sends an Android hardware key event using the {@link AndroidDriver#pressKey(KeyEvent)} API.
     *
     * <p>This simulates physical key presses — invaluable for testing features that rely on
     * hardware buttons (volume, media controls, navigation keys) and keyboard actions
     * (Enter, Backspace, Tab) without needing to tap the virtual keyboard directly.</p>
     *
     * <p>Example usages:
     * <ul>
     *   <li>{@code pressAndroidKey(AndroidKey.ENTER)} — submit a form</li>
     *   <li>{@code pressAndroidKey(AndroidKey.DEL)} — backspace / delete</li>
     *   <li>{@code pressAndroidKey(AndroidKey.BACK)} — navigate back</li>
     *   <li>{@code pressAndroidKey(AndroidKey.VOLUME_UP)} — increase volume</li>
     *   <li>{@code pressAndroidKey(AndroidKey.HOME)} — go to launcher</li>
     * </ul>
     * </p>
     *
     * @param key the {@link AndroidKey} to press
     */
    public static void pressAndroidKey(AndroidKey key) {
        if (!ConfigReader.isAndroid()) {
            log.warn("pressAndroidKey is Android-only — key: {}", key);
            return;
        }
        log.info("Pressing Android key: {}", key);
        ((AndroidDriver) DriverManager.getDriver()).pressKey(new KeyEvent(key));
    }

    /**
     * Presses the Enter/Return key on Android.
     * Commonly used after filling a text field to submit it or move to the next field.
     */
    public static void pressEnter() {
        pressAndroidKey(AndroidKey.ENTER);
    }

    /**
     * Presses the Backspace (Delete) key on Android.
     * Removes the last character in the focused text field.
     */
    public static void pressBackspace() {
        pressAndroidKey(AndroidKey.DEL);
    }

    /**
     * Presses the Tab key on Android to move focus to the next input field.
     */
    public static void pressTab() {
        pressAndroidKey(AndroidKey.TAB);
    }

    /**
     * Presses the hardware Back button on Android.
     * Equivalent to the physical Back button on the device.
     */
    public static void pressBack() {
        pressAndroidKey(AndroidKey.BACK);
    }

    /**
     * Presses the Volume Up key on Android.
     * Used in tests that verify media volume behavior.
     */
    public static void pressVolumeUp() {
        pressAndroidKey(AndroidKey.VOLUME_UP);
    }

    /**
     * Presses the Volume Down key on Android.
     */
    public static void pressVolumeDown() {
        pressAndroidKey(AndroidKey.VOLUME_DOWN);
    }

    // ── Cross-Platform Actions ─────────────────────────────────────────────────

    /**
     * Sends the native "Done" action to dismiss the keyboard across both platforms.
     *
     * <p>On Android: presses {@link AndroidKey#ENTER} (keycode 66).<br>
     * On iOS: looks for a "Done" toolbar button on the keyboard and taps it.
     * Falls back to {@code mobile:hideKeyboard} if not found.</p>
     */
    public static void pressKeyboardDone() {
        log.info("Pressing Done/Return on keyboard");
        if (ConfigReader.isAndroid()) {
            pressAndroidKey(AndroidKey.ENTER);
        } else {
            // iOS toolbar "Done" button — common on number pad keyboards
            By doneButton = By.xpath("//XCUIElementTypeButton[@name='Done']");
            try {
                DriverManager.getDriver().findElement(doneButton).click();
            } catch (Exception e) {
                // No visible Done button — try to hide keyboard directly
                hideKeyboard();
            }
        }
    }

    /**
     * Selects all text in the currently focused field using the platform-appropriate shortcut.
     *
     * <p>Android: uses the Ctrl+A key combination via mobile command.
     * iOS: uses {@code mobile:selectAll} execute script.</p>
     */
    public static void selectAllText() {
        log.info("Selecting all text in focused field");
        if (ConfigReader.isAndroid()) {
            // Use mobile:pressKey with CTRL+A
            try {
                DriverManager.getDriver().executeScript("mobile:pressKey",
                        Map.of("keycode", 29, "metastate", 4096)); // 29=A, 4096=CTRL_ON
            } catch (Exception e) {
                log.warn("Ctrl+A select-all failed: {}", e.getMessage());
            }
        } else {
            DriverManager.getDriver().executeScript("mobile:selectAll");
        }
    }

    /**
     * Pastes the current clipboard content into the focused field using the
     * platform-appropriate shortcut.
     *
     * <p>Android: uses the Ctrl+V key combination via mobile command — more
     * reliable than tapping the floating "Paste" popup/toolbar, which is
     * transient and not always present in the accessibility tree.
     * iOS: uses {@code mobile:pasteboard}.</p>
     */
    public static void pasteText() {
        log.info("Pasting clipboard content into focused field");
        if (ConfigReader.isAndroid()) {
            try {
                DriverManager.getDriver().executeScript("mobile:pressKey",
                        Map.of("keycode", 50, "metastate", 4096)); // 50=V, 4096=CTRL_ON
            } catch (Exception e) {
                log.warn("Ctrl+V paste failed: {}", e.getMessage());
            }
        } else {
            DriverManager.getDriver().executeScript("mobile:pasteboard", Map.of("content", ""));
        }
    }

    /**
     * Clears all text from the focused field by selecting all and deleting.
     * More reliable than {@code WebElement.clear()} on some iOS configurations.
     */
    public static void clearFocusedField() {
        log.info("Clearing focused field");
        selectAllText();
        if (ConfigReader.isAndroid()) {
            pressBackspace();
        } else {
            try {
                DriverManager.getDriver().findElement(
                        By.xpath("//*[@focused='true']")).sendKeys(Keys.DELETE);
            } catch (Exception e) {
                log.warn("Could not clear focused field: {}", e.getMessage());
            }
        }
    }
}
