package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Page object for ApiDemos' real "Views" submenu (~40 items) used as a
 * scroll/swipe test surface — there's no dedicated "Scroll View" screen with
 * top/bottom markers in this app, but this long, naturally scrollable list
 * serves the same purpose. "Animation" is the first visible item; "WebView3"
 * is the last, reachable only by scrolling.
 */
public class ScrollControlPage extends BasePage {

    private static final String TOP_ITEM = "Animation";
    private static final String BOTTOM_ITEM = "WebView3";

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
        scrollToElementWithText(BOTTOM_ITEM);
    }

    public void scrollToTop() {
        log.info("Scrolling to top of list");
        for (int i = 0; i < 10; i++) {
            if (isDisplayed(By.xpath("//*[@text='" + TOP_ITEM + "']"))) break;
            GestureUtils.swipeDown();
        }
    }

    public void scrollToElementWithText(String text) {
        log.info("Scrolling to element with text: {}", text);
        By uiScrollable = AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().text(\"" + text + "\"))");
        findElement(uiScrollable);
    }

    public boolean isBottomTextVisible() {
        return isDisplayed(By.xpath("//*[@text='" + BOTTOM_ITEM + "']"));
    }

    public boolean isTopTextVisible() {
        return isDisplayed(By.xpath("//*[@text='" + TOP_ITEM + "']"));
    }
}
