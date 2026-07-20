package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.WaitUtils;

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
 *
 * <p>Locators live in {@code locators_android.properties} / {@code locators_ios.properties}
 * under the {@code alert.*} keys.</p>
 */
public class AlertControlPage extends BasePage {

    // ── Actions ───────────────────────────────────────────────────────────────

    public void triggerSimpleAlert() {
        log.info("Triggering simple alert dialog");
        if (isIOS()) {
            click("alert.simpleCell");
        } else {
            click("alert.twoButtonsButton");
        }
    }

    public void triggerConfirmDialog() {
        log.info("Triggering confirm dialog");
        if (isIOS()) {
            click("alert.confirmCell");
        } else {
            click("alert.twoButtonsButton");
        }
    }

    public void triggerPromptDialog() {
        log.info("Triggering prompt dialog");
        if (isIOS()) {
            click("alert.textEntryCell");
        } else {
            click("alert.textEntryButton");
        }
    }

    public void acceptAlert() {
        log.info("Accepting alert (OK)");
        click("alert.okButton");
    }

    public void dismissAlert() {
        log.info("Dismissing alert (Cancel)");
        click("alert.cancelButton");
    }

    public void enterPromptText(String text) {
        log.info("Entering prompt text: {}", text);
        sendKeys("alert.promptInput", text);
    }

    public String getAlertTitle() {
        return getText("alert.title");
    }

    public String getAlertMessage() {
        // This dialog style has no separate message element — its message
        // occupies the alertTitle slot.
        return getText("alert.title");
    }

    public boolean isAlertDisplayed() {
        // isDisplayed() checks instantly and races the dialog's open animation
        // right after a trigger click; wait briefly before concluding it's absent.
        try {
            WaitUtils.waitForVisible(locator("alert.displayedIndicator"), 5);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
