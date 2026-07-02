package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "Views &gt; Buttons" screen, which has three
 * widgets: a "Normal" button, a "Small" button, and an ON/OFF toggle button.
 */
public class ButtonControlPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    @AndroidFindBy(id = "io.appium.android.apis:id/button_normal")
    @iOSXCUITFindBy(accessibility = "normalButton")
    private WebElement normalButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/button_small")
    @iOSXCUITFindBy(accessibility = "smallButton")
    private WebElement smallButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/button_toggle")
    @iOSXCUITFindBy(accessibility = "toggleButton")
    private WebElement toggleButton;

    // ── Actions ───────────────────────────────────────────────────────────────

    public void tapNormalButton() {
        log.info("Tapping Normal button");
        GestureUtils.tap(normalButton);
    }

    public void tapSmallButton() {
        log.info("Tapping Small button");
        GestureUtils.tap(smallButton);
    }

    public void tapToggleButton() {
        log.info("Tapping Toggle button");
        GestureUtils.tap(toggleButton);
    }

    public void tapButtonLabeled(String label) {
        log.info("Tapping button labeled: {}", label);
        click(By.xpath("//*[@text='" + label + "' or @content-desc='" + label + "']"));
    }

    public boolean isNormalButtonDisplayed() {
        return normalButton.isDisplayed();
    }

    public boolean isNormalButtonEnabled() {
        return normalButton.isEnabled();
    }

    public String getToggleButtonText() {
        return toggleButton.getText();
    }
}
