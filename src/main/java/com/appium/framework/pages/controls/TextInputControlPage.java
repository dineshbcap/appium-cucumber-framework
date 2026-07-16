package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.KeyboardUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "Views &gt; TextFields" screen (Android) and
 * UIKitCatalog's real "Text Fields" screen (iOS). The first field is a plain
 * text field with placeholder text; the second is a real password-masked
 * field. Neither screen has a submit button or result label.
 *
 * <p>Neither of iOS's two text fields carries an accessibility id, so they're
 * matched positionally: the plain field is the first {@code XCUIElementTypeTextField}
 * in document order (a second, unrelated search-style field appears later on screen).</p>
 */
public class TextInputControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/edit")
    @iOSXCUITFindBy(xpath = "(//XCUIElementTypeTextField)[1]")
    private WebElement textField;

    @AndroidFindBy(id = "io.appium.android.apis:id/edit1")
    @iOSXCUITFindBy(className = "XCUIElementTypeSecureTextField")
    private WebElement passwordField;

    // ── Actions ───────────────────────────────────────────────────────────────

    public void enterText(String text) {
        log.info("Entering text: {}", text);
        textField.click();
        textField.clear();
        textField.sendKeys(text);
    }

    public void clearTextField() {
        log.info("Clearing text field");
        textField.clear();
    }

    public void enterPassword(String password) {
        log.info("Entering password");
        passwordField.click();
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void appendText(String text) {
        log.info("Appending text: {}", text);
        // UiAutomator2's sendKeys replaces an EditText's content rather than
        // inserting at the cursor, so a true append needs the full string set at once.
        String existing = textField.getText();
        textField.clear();
        textField.sendKeys(existing + text);
    }

    public String getTextFieldValue() {
        return textField.getText();
    }

    /**
     * Returns {@code true} if the field is showing its placeholder hint rather
     * than user-entered text — both UiAutomator2 and XCUITest report the
     * placeholder via getText() when the field is empty, but the literal
     * placeholder string differs per app.
     */
    public boolean isTextFieldEmpty() {
        String placeholder = isIOS() ? "Placeholder text" : "hint text";
        return placeholder.equals(textField.getText());
    }

    /**
     * Returns {@code true} if the password field currently has focus.
     * On iOS, WDA doesn't populate a reliable {@code focused} attribute for
     * text fields, so keyboard visibility is used as a proxy — valid here
     * since this page only ever focuses one field at a time.
     */
    public boolean isPasswordFieldFocused() {
        if (isIOS()) {
            return KeyboardUtils.isKeyboardShown();
        }
        return Boolean.parseBoolean(passwordField.getAttribute("focused"));
    }
}
