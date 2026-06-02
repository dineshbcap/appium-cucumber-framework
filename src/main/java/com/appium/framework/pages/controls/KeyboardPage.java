package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.KeyboardUtils;
import com.appium.framework.utils.WaitUtils;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object demonstrating <b>Keyboard Interaction</b> testing.
 *
 * <p><b>Concepts covered:</b>
 * <ul>
 *   <li>Detecting whether the on-screen keyboard is visible</li>
 *   <li>Hiding the keyboard using platform-appropriate methods</li>
 *   <li>Pressing Android hardware key events (Enter, Backspace, Tab, Volume)</li>
 *   <li>Using Selenium's {@code sendKeys} for typed input</li>
 *   <li>Clearing focused input fields</li>
 *   <li>Cross-platform "Done" key behavior</li>
 * </ul>
 * </p>
 *
 * <p><b>Keyboard handling is a common pain point in mobile automation</b> because:
 * <ul>
 *   <li>The keyboard can cover elements below the fold, making them unclickable</li>
 *   <li>Different iOS keyboards (number pad, URL, email) have different dismiss mechanisms</li>
 *   <li>Android IME state can linger between test scenarios if not explicitly dismissed</li>
 * </ul>
 * </p>
 */
public class KeyboardPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    /** Standard single-line text input field that triggers the keyboard on focus. */
    @AndroidFindBy(id = "io.appium.android.apis:id/edit")
    @iOSXCUITFindBy(accessibility = "textField")
    private WebElement textField;

    /** A search field that uses the "Search" keyboard action button. */
    @AndroidFindBy(accessibility = "Search input")
    @iOSXCUITFindBy(accessibility = "searchField")
    private WebElement searchField;

    /** Submit / Done button on the screen (not the keyboard button). */
    @AndroidFindBy(id = "io.appium.android.apis:id/submit_button")
    @iOSXCUITFindBy(accessibility = "submitButton")
    private WebElement submitButton;

    /** Label that shows the result after submitting text input. */
    @AndroidFindBy(id = "io.appium.android.apis:id/text_result")
    @iOSXCUITFindBy(accessibility = "textResult")
    private WebElement resultLabel;

    private static final By KEYBOARD_INDICATOR =
            By.xpath("//android.inputmethodservice.InputMethodService | //XCUIElementTypeKeyboard");

    // ── Text Entry ────────────────────────────────────────────────────────────

    /**
     * Taps the text field to focus it, bringing up the on-screen keyboard.
     */
    public void focusTextField() {
        log.info("Focusing text field (keyboard should appear)");
        textField.click();
    }

    /**
     * Types text into the text field using Appium's sendKeys.
     * The keyboard must already be visible or will appear on first tap.
     *
     * @param text the text to type
     */
    public void typeText(String text) {
        log.info("Typing: '{}'", text);
        textField.click();
        textField.sendKeys(text);
    }

    /**
     * Clears the current content and types new text.
     *
     * @param text new text to enter
     */
    public void clearAndType(String text) {
        log.info("Clearing and typing: '{}'", text);
        textField.clear();
        textField.sendKeys(text);
    }

    // ── Keyboard Visibility ───────────────────────────────────────────────────

    /**
     * Returns whether the on-screen keyboard is currently showing.
     *
     * @return {@code true} if keyboard is visible
     */
    public boolean isKeyboardVisible() {
        return KeyboardUtils.isKeyboardShown();
    }

    // ── Dismiss Keyboard ──────────────────────────────────────────────────────

    /**
     * Hides the on-screen keyboard using the platform-appropriate method.
     * On Android: calls hideKeyboard().
     * On iOS: tries to tap "Done" toolbar button, falls back to mobile:hideKeyboard.
     */
    public void dismissKeyboard() {
        log.info("Dismissing keyboard");
        KeyboardUtils.hideKeyboard();
    }

    /**
     * Presses the keyboard's Done/Return button to submit the input and dismiss the keyboard.
     * Preferred over {@link #dismissKeyboard()} when the app expects the IME action.
     */
    public void pressKeyboardDone() {
        log.info("Pressing keyboard Done/Return");
        KeyboardUtils.pressKeyboardDone();
    }

    // ── Android Key Events ─────────────────────────────────────────────────────

    /**
     * Presses the Enter key on Android.
     * When the text field has android:imeOptions="actionDone", this submits the form.
     */
    public void pressEnterKey() {
        log.info("Pressing Enter key");
        KeyboardUtils.pressEnter();
    }

    /**
     * Presses the Backspace key once on Android.
     * Deletes the last character in the focused text field.
     */
    public void pressBackspaceKey() {
        log.info("Pressing Backspace key");
        KeyboardUtils.pressBackspace();
    }

    /**
     * Presses the Tab key to move focus to the next input field.
     * Useful for tabbing through form fields in order.
     */
    public void pressTabKey() {
        log.info("Pressing Tab key");
        KeyboardUtils.pressTab();
    }

    /**
     * Presses the Volume Up key on Android.
     * Demonstrates that ANY hardware key can be pressed via Appium, not just keyboard keys.
     */
    public void pressVolumeUpKey() {
        log.info("Pressing Volume Up key");
        KeyboardUtils.pressVolumeUp();
    }

    /**
     * Presses the Volume Down key on Android.
     */
    public void pressVolumeDownKey() {
        log.info("Pressing Volume Down key");
        KeyboardUtils.pressVolumeDown();
    }

    /**
     * Presses any arbitrary Android key by its {@link AndroidKey} enum value.
     *
     * @param key the Android key to press
     */
    public void pressAndroidKey(AndroidKey key) {
        log.info("Pressing Android key: {}", key);
        KeyboardUtils.pressAndroidKey(key);
    }

    // ── Results ───────────────────────────────────────────────────────────────

    /**
     * Returns the current value of the text field.
     *
     * @return text field value
     */
    public String getTextFieldValue() {
        return textField.getText();
    }

    /**
     * Returns the result label text after form submission.
     *
     * @return result text
     */
    public String getResultText() {
        WaitUtils.waitForVisible(By.xpath("//*[@resource-id='io.appium.android.apis:id/text_result'" +
                " or @name='textResult']"), 5);
        return resultLabel.getText();
    }

    /**
     * Submits the form by clicking the screen Submit button (not the keyboard action).
     */
    public void submitForm() {
        log.info("Submitting form");
        submitButton.click();
    }
}
