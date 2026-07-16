package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Page object for ApiDemos' real "Views" submenu (~40 items, Android) and
 * UIKitCatalog's real root screen (18 items, iOS) — both used as a
 * scroll/swipe test surface since neither app has a dedicated "Scroll View"
 * screen with top/bottom markers. Android's "WebView3" is reachable only by
 * scrolling; iOS's 18 items all fit on one screen, so "scrolling to the
 * bottom" is a no-op scroll followed by a visibility check.
 */
public class ScrollControlPage extends BasePage {

    private String topItem() {
        return isIOS() ? "Activity Indicators" : "Animation";
    }

    private String bottomItem() {
        return isIOS() ? "Web View" : "WebView3";
    }

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
        scrollToElementWithText(bottomItem());
    }

    public void scrollToTop() {
        log.info("Scrolling to top of list");
        for (int i = 0; i < 10; i++) {
            if (isDisplayed(textLocator(topItem()))) break;
            GestureUtils.swipeDown();
        }
    }

    public void scrollToElementWithText(String text) {
        log.info("Scrolling to element with text: {}", text);
        if (isAndroid()) {
            By uiScrollable = AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                    ".scrollIntoView(new UiSelector().text(\"" + text + "\"))");
            findElement(uiScrollable);
        } else {
            By target = textLocator(text);
            for (int i = 0; i < 5; i++) {
                if (isDisplayed(target)) return;
                GestureUtils.swipeUp();
            }
        }
    }

    public boolean isBottomTextVisible() {
        return isDisplayed(textLocator(bottomItem()));
    }

    public boolean isTopTextVisible() {
        return isDisplayed(textLocator(topItem()));
    }

    private By textLocator(String text) {
        return By.xpath("//*[@text='" + text + "' or @name='" + text + "' or @label='" + text + "']");
    }
}
