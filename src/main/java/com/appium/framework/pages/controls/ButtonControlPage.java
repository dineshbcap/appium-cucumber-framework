package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import org.openqa.selenium.By;

/**
 * Page object for ApiDemos' real "Views &gt; Buttons" screen, which has three
 * widgets: a "Normal" button, a "Small" button, and an ON/OFF toggle button.
 *
 * <p>Locators live in {@code locators_android.properties} / {@code locators_ios.properties}
 * under the {@code button.*} keys.</p>
 */
public class ButtonControlPage extends BasePage {

    // ── Actions ───────────────────────────────────────────────────────────────

    public void tapNormalButton() {
        log.info("Tapping Normal button");
        GestureUtils.tap(element("button.normal"));
    }

    public void tapSmallButton() {
        log.info("Tapping Small button");
        GestureUtils.tap(element("button.small"));
    }

    public void tapToggleButton() {
        log.info("Tapping Toggle button");
        GestureUtils.tap(element("button.toggle"));
    }

    public void tapButtonLabeled(String label) {
        log.info("Tapping button labeled: {}", label);
        click(By.xpath("//*[@text='" + label + "' or @content-desc='" + label + "']"));
    }

    public boolean isNormalButtonDisplayed() {
        return isDisplayed("button.normal");
    }

    public boolean isNormalButtonEnabled() {
        return isEnabled("button.normal");
    }

    public String getToggleButtonText() {
        return getText("button.toggle");
    }
}
