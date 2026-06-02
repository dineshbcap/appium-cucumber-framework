package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ScrollControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/scrollView")
    @iOSXCUITFindBy(accessibility = "scrollView")
    private WebElement scrollView;

    @AndroidFindBy(id = "io.appium.android.apis:id/bottom_text")
    @iOSXCUITFindBy(accessibility = "bottomText")
    private WebElement bottomText;

    @AndroidFindBy(id = "io.appium.android.apis:id/top_text")
    @iOSXCUITFindBy(accessibility = "topText")
    private WebElement topText;

    private static final By BOTTOM_ELEMENT =
            AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                    ".scrollIntoView(new UiSelector().text(\"Bottom Item\"))");

    // ── Actions ───────────────────────────────────────────────────────────────

    public void scrollUp() {
        log.info("Scrolling up");
        GestureUtils.swipeDown();
    }

    public void scrollDown() {
        log.info("Scrolling down");
        GestureUtils.swipeUp();
    }

    public void scrollLeft() {
        log.info("Scrolling left");
        GestureUtils.swipeRight();
    }

    public void scrollRight() {
        log.info("Scrolling right");
        GestureUtils.swipeLeft();
    }

    public void scrollToBottom() {
        log.info("Scrolling to bottom of list");
        for (int i = 0; i < 10; i++) {
            if (isDisplayed(By.id("io.appium.android.apis:id/bottom_text"))) break;
            GestureUtils.swipeUp();
        }
    }

    public void scrollToTop() {
        log.info("Scrolling to top of list");
        for (int i = 0; i < 10; i++) {
            if (isDisplayed(By.id("io.appium.android.apis:id/top_text"))) break;
            GestureUtils.swipeDown();
        }
    }

    public void scrollToElementWithText(String text) {
        log.info("Scrolling to element with text: {}", text);
        By target = By.xpath("//*[@text='" + text + "']");
        scrollToElement(target);
    }

    public boolean isBottomTextVisible() {
        return isDisplayed(By.id("io.appium.android.apis:id/bottom_text"));
    }

    public boolean isTopTextVisible() {
        return isDisplayed(By.id("io.appium.android.apis:id/top_text"));
    }

    public String getBottomText() {
        return bottomText.getText();
    }

    public String getTopText() {
        return topText.getText();
    }
}
