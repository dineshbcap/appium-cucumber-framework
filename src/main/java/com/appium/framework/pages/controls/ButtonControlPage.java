package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import com.appium.framework.utils.WaitUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ButtonControlPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    @AndroidFindBy(accessibility = "Tap Button")
    @iOSXCUITFindBy(accessibility = "tapButton")
    private WebElement tapButton;

    @AndroidFindBy(accessibility = "Long Press Button")
    @iOSXCUITFindBy(accessibility = "longPressButton")
    private WebElement longPressButton;

    @AndroidFindBy(accessibility = "Double Tap Button")
    @iOSXCUITFindBy(accessibility = "doubleTapButton")
    private WebElement doubleTapButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/button_result")
    @iOSXCUITFindBy(accessibility = "buttonResult")
    private WebElement resultLabel;

    private static final By RESULT_LABEL =
            AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"io.appium.android.apis:id/button_result\")");

    // ── Actions ───────────────────────────────────────────────────────────────

    public void tapButton() {
        log.info("Performing single tap on button");
        GestureUtils.tap(tapButton);
    }

    public void longPressButton() {
        log.info("Performing long press on button");
        GestureUtils.longPress(longPressButton);
    }

    public void doubleTapButton() {
        log.info("Performing double tap on button");
        GestureUtils.doubleTap(doubleTapButton);
    }

    public String getResultText() {
        WaitUtils.waitForVisible(RESULT_LABEL);
        return resultLabel.getText();
    }

    public boolean isTapButtonDisplayed() {
        return tapButton.isDisplayed();
    }

    public boolean isTapButtonEnabled() {
        return tapButton.isEnabled();
    }

    public void tapButtonByText(String text) {
        log.info("Tapping button with text: {}", text);
        click(By.xpath("//*[@text='" + text + "' or @label='" + text + "']"));
    }
}
