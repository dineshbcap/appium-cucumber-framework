package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.ClipboardUtils;
import com.appium.framework.utils.WaitUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object demonstrating <b>Clipboard</b> interactions (copy and paste testing).
 *
 * <p><b>Concept covered:</b> Many apps expose "Copy to Clipboard" buttons (OTP codes,
 * share links, confirmation numbers). Appium's {@link io.appium.java_client.clipboard.HasClipboard}
 * interface lets tests read the clipboard content after a copy action to assert correctness.</p>
 *
 * <p><b>Common testing scenarios:</b>
 * <ul>
 *   <li>Tap "Copy" button → assert clipboard contains expected text</li>
 *   <li>Pre-load clipboard → long-press input → Paste → assert field is populated</li>
 *   <li>Share flow: copy link from share sheet → assert URL format</li>
 * </ul>
 * </p>
 */
public class ClipboardPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    /** Text field where content can be typed and then "copied". */
    @AndroidFindBy(id = "io.appium.android.apis:id/edit")
    @iOSXCUITFindBy(accessibility = "textField")
    private WebElement inputField;

    /** Button that copies the input field's content to the clipboard. */
    @AndroidFindBy(accessibility = "Copy to clipboard")
    @iOSXCUITFindBy(accessibility = "copyButton")
    private WebElement copyButton;

    /** Target field where pasted content appears. */
    @AndroidFindBy(id = "io.appium.android.apis:id/paste_target")
    @iOSXCUITFindBy(accessibility = "pasteTarget")
    private WebElement pasteTargetField;

    /** Displays the current clipboard content for verification. */
    @AndroidFindBy(id = "io.appium.android.apis:id/clipboard_content")
    @iOSXCUITFindBy(accessibility = "clipboardContent")
    private WebElement clipboardContentLabel;

    private static final By CLIPBOARD_LABEL =
            By.xpath("//*[@resource-id='io.appium.android.apis:id/clipboard_content'" +
                     " or @name='clipboardContent']");

    // ── Copy Actions ──────────────────────────────────────────────────────────

    /**
     * Types text into the input field then taps the "Copy" button to copy it.
     *
     * @param text the text to type and copy
     */
    public void typeAndCopy(String text) {
        log.info("Typing '{}' and copying to clipboard", text);
        inputField.click();
        inputField.clear();
        inputField.sendKeys(text);
        copyButton.click();
    }

    /**
     * Sets text directly on the clipboard using the Appium API.
     * Bypasses the UI copy flow — useful for pre-loading clipboard before a paste test.
     *
     * @param text the text to place on the clipboard
     */
    public void setClipboardDirectly(String text) {
        log.info("Setting clipboard directly: '{}'", text);
        ClipboardUtils.setClipboardText(text);
    }

    // ── Paste Actions ──────────────────────────────────────────────────────────

    /**
     * Long-presses the paste target field and selects "Paste" from the context menu.
     *
     * <p>This simulates the user's paste gesture: long-press an input → context menu appears
     * → tap Paste. The clipboard content is inserted at the cursor position.</p>
     */
    public void longPressAndPaste() {
        log.info("Long-pressing paste target and selecting Paste");
        // Long-press brings up the context menu with Paste option
        com.appium.framework.utils.GestureUtils.longPress(pasteTargetField, 1500);
        // Tap the "Paste" option in the context menu
        By pasteMenuItem = By.xpath(
                "//*[@text='Paste' or @name='Paste' or @label='Paste']");
        WaitUtils.waitForVisible(pasteMenuItem, 5).click();
    }

    // ── Read Clipboard ─────────────────────────────────────────────────────────

    /**
     * Reads the current clipboard text using the Appium API.
     *
     * @return clipboard text content
     */
    public String getClipboardText() {
        String text = ClipboardUtils.getClipboardText();
        log.info("Clipboard content: '{}'", text);
        return text;
    }

    /**
     * Returns the text displayed in the clipboard content label element on the screen.
     * Some apps display the clipboard content inline for testing/debugging.
     *
     * @return clipboard content label text
     */
    public String getClipboardLabelText() {
        WaitUtils.waitForVisible(CLIPBOARD_LABEL, 5);
        return clipboardContentLabel.getText();
    }

    /**
     * Returns the text currently in the paste target field after pasting.
     *
     * @return paste target field content
     */
    public String getPasteTargetText() {
        return pasteTargetField.getText();
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    /**
     * Clears the clipboard by setting it to an empty string.
     */
    public void clearClipboard() {
        log.info("Clearing clipboard");
        ClipboardUtils.clearClipboard();
    }

    /**
     * Verifies that the clipboard contains the expected text.
     *
     * @param expectedText text expected to be on the clipboard
     * @return {@code true} if clipboard matches expected text
     */
    public boolean doesClipboardContain(String expectedText) {
        return ClipboardUtils.clipboardContains(expectedText);
    }
}
