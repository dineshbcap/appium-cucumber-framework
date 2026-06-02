package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AlertControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/alert_button")
    @iOSXCUITFindBy(accessibility = "showAlertButton")
    private WebElement showAlertButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/confirm_button")
    @iOSXCUITFindBy(accessibility = "showConfirmButton")
    private WebElement showConfirmButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/prompt_button")
    @iOSXCUITFindBy(accessibility = "showPromptButton")
    private WebElement showPromptButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/alert_result")
    @iOSXCUITFindBy(accessibility = "alertResult")
    private WebElement resultLabel;

    private static final By ALERT_OK_BUTTON =
            By.xpath("//*[@text='OK' or @text='Ok' or @name='OK']");
    private static final By ALERT_CANCEL_BUTTON =
            By.xpath("//*[@text='Cancel' or @name='Cancel']");
    private static final By ALERT_MESSAGE =
            By.id("android:id/message");
    private static final By ALERT_TITLE =
            By.id("android:id/alertTitle");
    private static final By PROMPT_INPUT =
            By.id("android:id/input");

    // ── Actions ───────────────────────────────────────────────────────────────

    public void triggerSimpleAlert() {
        log.info("Triggering simple alert dialog");
        showAlertButton.click();
    }

    public void triggerConfirmDialog() {
        log.info("Triggering confirm dialog");
        showConfirmButton.click();
    }

    public void triggerPromptDialog() {
        log.info("Triggering prompt dialog");
        showPromptButton.click();
    }

    public void acceptAlert() {
        log.info("Accepting alert (OK)");
        click(ALERT_OK_BUTTON);
    }

    public void dismissAlert() {
        log.info("Dismissing alert (Cancel)");
        click(ALERT_CANCEL_BUTTON);
    }

    public void enterPromptText(String text) {
        log.info("Entering prompt text: {}", text);
        sendKeys(PROMPT_INPUT, text);
    }

    public String getAlertTitle() {
        return getText(ALERT_TITLE);
    }

    public String getAlertMessage() {
        return getText(ALERT_MESSAGE);
    }

    public boolean isAlertDisplayed() {
        return isDisplayed(ALERT_OK_BUTTON);
    }

    public String getResultText() {
        return resultLabel.getText();
    }
}
