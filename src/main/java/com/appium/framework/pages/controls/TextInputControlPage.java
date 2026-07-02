package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "Views &gt; TextFields" screen. The first field
 * ({@code edit}) is a plain text field with hint text; the second ({@code edit1})
 * is a real password-masked field. This screen has no submit button or result
 * label — those don't exist here.
 */
public class TextInputControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/edit")
    @iOSXCUITFindBy(accessibility = "textField")
    private WebElement textField;

    @AndroidFindBy(id = "io.appium.android.apis:id/edit1")
    @iOSXCUITFindBy(accessibility = "passwordField")
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
     * than user-entered text — UiAutomator2 reports the hint via getText() when empty.
     */
    public boolean isTextFieldEmpty() {
        return "hint text".equals(textField.getText());
    }

    public boolean isPasswordFieldFocused() {
        return Boolean.parseBoolean(passwordField.getAttribute("focused"));
    }
}
