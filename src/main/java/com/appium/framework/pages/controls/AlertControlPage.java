package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.WaitUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "App &gt; Alert Dialogs" screen. Two real
 * dialogs are used here: {@code two_buttons} ("OK Cancel dialog with a
 * message" — its message occupies the standard {@code alertTitle} slot since
 * it has no separate title) and {@code text_entry_button} ("Text Entry
 * dialog", with real {@code username_edit}/{@code password_edit} fields).
 * This screen has no result label — dialogs are simply dismissed.
 */
public class AlertControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/two_buttons")
    @iOSXCUITFindBy(accessibility = "showAlertButton")
    private WebElement twoButtonsAlertButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/text_entry_button")
    @iOSXCUITFindBy(accessibility = "showPromptButton")
    private WebElement textEntryButton;

    private static final By ALERT_OK_BUTTON = By.id("android:id/button1");
    private static final By ALERT_CANCEL_BUTTON = By.id("android:id/button2");
    private static final By ALERT_TITLE = By.id("android:id/alertTitle");
    private static final By PROMPT_INPUT = By.id("io.appium.android.apis:id/username_edit");

    // ── Actions ───────────────────────────────────────────────────────────────

    public void triggerSimpleAlert() {
        log.info("Triggering simple alert dialog");
        twoButtonsAlertButton.click();
    }

    public void triggerConfirmDialog() {
        log.info("Triggering confirm dialog");
        twoButtonsAlertButton.click();
    }

    public void triggerPromptDialog() {
        log.info("Triggering prompt dialog");
        textEntryButton.click();
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
        // This dialog style has no separate message element — its message
        // occupies the alertTitle slot.
        return getText(ALERT_TITLE);
    }

    public boolean isAlertDisplayed() {
        // isDisplayed() checks instantly and races the dialog's open animation
        // right after a trigger click; wait briefly before concluding it's absent.
        try {
            WaitUtils.waitForVisible(ALERT_OK_BUTTON, 5);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
