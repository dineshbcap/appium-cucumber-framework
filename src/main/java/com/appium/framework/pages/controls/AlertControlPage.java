package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.WaitUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "App &gt; Alert Dialogs" screen (Android) and
 * UIKitCatalog's real "Alert Views" screen (iOS).
 *
 * <p>Android uses two real dialogs: {@code two_buttons} ("OK Cancel dialog with
 * a message" — its message occupies the standard {@code alertTitle} slot since
 * it has no separate title) and {@code text_entry_button} ("Text Entry dialog",
 * with real {@code username_edit}/{@code password_edit} fields).</p>
 *
 * <p>iOS's "Simple" cell shows a single-button ("OK") alert; its "Other" cell
 * shows a 3-button alert ("Choice One"/"Choice Two"/"Cancel") — the only
 * iOS alert style with a real Cancel button, so it stands in for the
 * "confirm dialog" (dismiss) flow. Both reuse Apple's generic sample title
 * ("A Short Title Is Best") and message, so title/message content assertions
 * are Android-only (see alert.feature).</p>
 */
public class AlertControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/two_buttons")
    private WebElement twoButtonsAlertButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/text_entry_button")
    private WebElement textEntryButton;

    @iOSXCUITFindBy(accessibility = "Simple")
    private WebElement iosSimpleAlertCell;

    @iOSXCUITFindBy(accessibility = "Other")
    private WebElement iosConfirmAlertCell;

    @iOSXCUITFindBy(accessibility = "Text Entry")
    private WebElement iosTextEntryCell;

    private static final By ALERT_OK_BUTTON =
            By.xpath("//*[@resource-id='android:id/button1' or @name='OK']");
    private static final By ALERT_CANCEL_BUTTON =
            By.xpath("//*[@resource-id='android:id/button2' or @name='Cancel']");
    // "Other"'s alert has no OK button, so displayed-state must key off the
    // alert container itself, not a specific button, to cover both styles.
    private static final By ALERT_DISPLAYED =
            By.xpath("//*[@resource-id='android:id/button1' or @type='XCUIElementTypeAlert']");
    private static final By ALERT_TITLE = By.id("android:id/alertTitle");
    private static final By PROMPT_INPUT = By.id("io.appium.android.apis:id/username_edit");

    // ── Actions ───────────────────────────────────────────────────────────────

    public void triggerSimpleAlert() {
        log.info("Triggering simple alert dialog");
        if (isIOS()) {
            iosSimpleAlertCell.click();
        } else {
            twoButtonsAlertButton.click();
        }
    }

    public void triggerConfirmDialog() {
        log.info("Triggering confirm dialog");
        if (isIOS()) {
            iosConfirmAlertCell.click();
        } else {
            twoButtonsAlertButton.click();
        }
    }

    public void triggerPromptDialog() {
        log.info("Triggering prompt dialog");
        if (isIOS()) {
            iosTextEntryCell.click();
        } else {
            textEntryButton.click();
        }
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
            WaitUtils.waitForVisible(ALERT_DISPLAYED, 5);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
