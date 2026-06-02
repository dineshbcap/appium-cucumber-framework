package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class TextInputControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/edit")
    @iOSXCUITFindBy(accessibility = "textField")
    private WebElement textField;

    @AndroidFindBy(id = "io.appium.android.apis:id/password")
    @iOSXCUITFindBy(accessibility = "passwordField")
    private WebElement passwordField;

    @AndroidFindBy(id = "io.appium.android.apis:id/multiline_edit")
    @iOSXCUITFindBy(accessibility = "multilineField")
    private WebElement multilineField;

    @AndroidFindBy(id = "io.appium.android.apis:id/submit_button")
    @iOSXCUITFindBy(accessibility = "submitButton")
    private WebElement submitButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/text_result")
    @iOSXCUITFindBy(accessibility = "textResult")
    private WebElement textResult;

    private static final By TEXT_FIELD =
            AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"io.appium.android.apis:id/edit\")");

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

    public void enterMultilineText(String text) {
        log.info("Entering multiline text");
        multilineField.click();
        multilineField.clear();
        multilineField.sendKeys(text);
    }

    public void submitForm() {
        log.info("Submitting form");
        submitButton.click();
    }

    public String getTextFieldValue() {
        return textField.getText();
    }

    public String getPasswordFieldValue() {
        return passwordField.getText();
    }

    public String getResultText() {
        return textResult.getText();
    }

    public boolean isTextFieldFocused() {
        return Boolean.parseBoolean(textField.getAttribute("focused"));
    }

    public void appendText(String text) {
        log.info("Appending text: {}", text);
        textField.sendKeys(text);
    }

    public void tapOutsideKeyboard() {
        By outsideArea = By.xpath("//*[@content-desc='screen']");
        if (isDisplayed(outsideArea)) {
            click(outsideArea);
        } else {
            navigateBack();
        }
    }
}
