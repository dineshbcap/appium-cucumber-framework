package com.appium.tests.stepdefs;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.pages.controls.KeyboardPage;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;

/**
 * Step definitions for Keyboard Interaction feature.
 *
 * <p>Covers: keyboard visibility, text entry, Android key events (Enter, Backspace,
 * Tab, Volume), Done/Return action, and keyboard dismissal.</p>
 *
 * <p><b>Concept demonstrated:</b> Using {@link com.appium.framework.utils.KeyboardUtils}
 * and {@link io.appium.java_client.HasOnScreenKeyboard} to control the soft keyboard,
 * and {@link io.appium.java_client.android.nativekey.AndroidKey} for hardware key events.</p>
 */
public class KeyboardStepDefs {

    private static final Logger log = LogManager.getLogger(KeyboardStepDefs.class);
    private final KeyboardPage page = new KeyboardPage();

    // ── When ──────────────────────────────────────────────────────────────────

    @When("the user taps the text input field")
    public void tapTextField() {
        page.focusTextField();
    }

    @When("the keyboard is dismissed")
    public void dismissKeyboard() {
        page.dismissKeyboard();
    }

    @When("the user types {string} in the keyboard page field")
    public void typeInKeyboardField(String text) {
        page.typeText(text);
    }

    @When("the user clears the field and types {string}")
    public void clearAndType(String text) {
        page.clearAndType(text);
    }

    @When("the user presses the Enter key")
    public void pressEnterKey() {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping Android key event on iOS");
            return;
        }
        page.pressEnterKey();
    }

    @When("the user presses the Backspace key")
    public void pressBackspaceKey() {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping Backspace key event on iOS");
            return;
        }
        page.pressBackspaceKey();
    }

    @When("the user presses the Tab key")
    public void pressTabKey() {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping Tab key event on iOS");
            return;
        }
        page.pressTabKey();
    }

    @When("the user presses the Volume Up key")
    public void pressVolumeUp() {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping Volume Up on iOS");
            return;
        }
        page.pressVolumeUpKey();
    }

    @When("the user presses the Volume Down key")
    public void pressVolumeDown() {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping Volume Down on iOS");
            return;
        }
        page.pressVolumeDownKey();
    }

    @When("the user presses Done on the keyboard")
    public void pressDoneOnKeyboard() {
        page.pressKeyboardDone();
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the keyboard should be visible")
    public void keyboardShouldBeVisible() {
        Assertions.assertThat(page.isKeyboardVisible())
                .as("Keyboard should be visible")
                .isTrue();
    }

    @Then("the keyboard should not be visible")
    public void keyboardShouldNotBeVisible() {
        Assertions.assertThat(page.isKeyboardVisible())
                .as("Keyboard should NOT be visible")
                .isFalse();
    }

    @Then("the text field should display {string}")
    public void textFieldShouldDisplay(String expectedText) {
        String actual = page.getTextFieldValue();
        log.info("Text field value: '{}'", actual);
        Assertions.assertThat(actual)
                .as("Text field content")
                .isEqualTo(expectedText);
    }

}
