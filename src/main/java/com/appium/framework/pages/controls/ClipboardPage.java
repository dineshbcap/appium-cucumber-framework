package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.ClipboardUtils;
import com.appium.framework.utils.KeyboardUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "Content &gt; Clipboard &gt; Data Types" screen.
 * Its "Copy Text" (plain) button copies a fixed string to the OS clipboard and
 * reflects it in an on-screen label — a genuine copy-to-clipboard flow, unlike
 * the fictional free-text copy button this page previously assumed.
 */
public class ClipboardPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    /** "Copy Text" button for the plain-text clipboard sample. */
    @AndroidFindBy(id = "io.appium.android.apis:id/copy_plain_text")
    @iOSXCUITFindBy(accessibility = "copyButton")
    private WebElement copyPlainTextButton;

    /** Label reflecting the current clipboard's text content. */
    @AndroidFindBy(id = "io.appium.android.apis:id/clip_text")
    @iOSXCUITFindBy(accessibility = "clipboardContent")
    private WebElement clipTextLabel;

    /**
     * Real EditText on the "Views &gt; TextFields" screen (Android) / the first
     * unnamed {@code XCUIElementTypeTextField} on "Text Fields" (iOS) — used to
     * test pasting.
     */
    @AndroidFindBy(id = "io.appium.android.apis:id/edit")
    @iOSXCUITFindBy(xpath = "(//XCUIElementTypeTextField)[1]")
    private WebElement pasteTargetField;

    // ── Copy Actions ──────────────────────────────────────────────────────────

    /**
     * Taps the "Copy Text" button, copying the screen's fixed plain-text sample
     * to the OS clipboard.
     */
    public void tapCopyPlainText() {
        log.info("Tapping 'Copy Text' (plain text) button");
        copyPlainTextButton.click();
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

    // ── Read Clipboard ─────────────────────────────────────────────────────────

    /**
     * Returns the on-screen label reflecting the clipboard's current text content.
     *
     * @return clip text label content
     */
    public String getClipTextLabel() {
        return clipTextLabel.getText();
    }

    // ── Paste Actions ─────────────────────────────────────────────────────────

    /**
     * Pastes the clipboard content into the real "edit" field on the
     * TextFields screen via the Ctrl+V key combo. More reliable than tapping
     * Android's floating "Paste" popup, which is transient and not always
     * exposed in the accessibility tree.
     */
    public void pasteIntoTextField() {
        log.info("Pasting into text field");
        pasteTargetField.clear();
        pasteTargetField.click();
        KeyboardUtils.pasteText(pasteTargetField);
    }

    /**
     * Returns the current value of the paste target field.
     *
     * @return field content after a paste
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
}
