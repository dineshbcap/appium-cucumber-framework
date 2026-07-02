package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.ClipboardPage;
import com.appium.framework.utils.ClipboardUtils;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;

/**
 * Step definitions for Clipboard Operations feature.
 *
 * <p>Covers: setting clipboard via API, reading clipboard, copy via UI,
 * paste via UI, clipboard clearing, and content verification.</p>
 *
 * <p><b>Concept demonstrated:</b> Using {@link ClipboardUtils} backed by
 * {@link io.appium.java_client.clipboard.HasClipboard} to read/write the device
 * clipboard programmatically — enabling fast clipboard tests without relying
 * solely on OS-level copy/paste UI gestures.</p>
 */
public class ClipboardStepDefs {

    private static final Logger log = LogManager.getLogger(ClipboardStepDefs.class);
    private final ClipboardPage page = new ClipboardPage();

    // ── When ──────────────────────────────────────────────────────────────────

    @When("the clipboard is set to {string}")
    public void setClipboard(String text) {
        log.info("Setting clipboard to: '{}'", text);
        page.setClipboardDirectly(text);
    }

    @When("the clipboard is cleared")
    public void clearClipboard() {
        log.info("Clearing clipboard");
        page.clearClipboard();
    }

    @When("the user taps the copy button")
    public void tapCopyButton() {
        log.info("Tapping copy button");
        page.tapCopyPlainText();
    }

    @When("the user pastes into the text field")
    public void userPastesIntoTextField() {
        page.pasteIntoTextField();
    }

    // Text field tap is handled by KeyboardStepDefs.tapTextField() — shared step definition.

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the clipboard should contain {string}")
    public void clipboardShouldContain(String expectedText) {
        String actual = ClipboardUtils.getClipboardText();
        log.info("Clipboard content: '{}' | expected: '{}'", actual, expectedText);
        Assertions.assertThat(actual)
                .as("Clipboard content")
                .isEqualTo(expectedText);
    }

    @Then("the text field should contain the pasted text {string}")
    public void textFieldShouldContainPastedText(String expected) {
        Assertions.assertThat(page.getPasteTargetText())
                .as("Pasted text field value")
                .isEqualTo(expected);
    }
}
