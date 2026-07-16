package com.appium.framework.pages.controls;

import com.appium.framework.driver.DriverManager;
import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import com.appium.framework.utils.MobileGestureUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Gesture interactions. No dedicated "Gestures" demo screen exists anywhere
 * in ApiDemos, so only what has a real, verifiable target is covered here:
 * <ul>
 *   <li>Drag and drop, on the real "Views &gt; Drag and Drop" screen
 *       ({@code drag_dot_1}/{@code drag_dot_2}, result label {@code drag_result_text}
 *       which literally reads "Dropped!" after a successful drop)</li>
 *   <li>Generic full-screen swipes/scrolls/fling, which don't target any specific
 *       element — verified by confirming the app is still responsive afterward,
 *       not a fictional per-gesture result label</li>
 * </ul>
 * Tap, long-press, double-tap, and pinch-zoom have no real target in this app
 * and are intentionally not covered.
 */
public class GestureControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/drag_dot_1")
    @iOSXCUITFindBy(accessibility = "draggableItem")
    private WebElement draggableItem;

    @AndroidFindBy(id = "io.appium.android.apis:id/drag_dot_2")
    @iOSXCUITFindBy(accessibility = "dropZone")
    private WebElement dropZone;

    @AndroidFindBy(id = "io.appium.android.apis:id/drag_result_text")
    @iOSXCUITFindBy(accessibility = "gestureResult")
    private WebElement gestureResult;

    // ── W3C Actions gestures ─────────────────────────────────────────────────

    public void performSwipeUp() {
        log.info("Performing swipe up");
        GestureUtils.swipeUp();
    }

    public void performSwipeLeft() {
        log.info("Performing swipe left");
        GestureUtils.swipeLeft();
    }

    public void performDragAndDrop() {
        log.info("Performing drag and drop");
        GestureUtils.dragAndDrop(draggableItem, dropZone);
    }

    public String getGestureResult() {
        return gestureResult.getText();
    }

    /**
     * Confirms the app is still alive and responsive after a gesture that has
     * no dedicated result element — full-screen swipes/scrolls/flings don't
     * target any specific widget.
     */
    public boolean isAppResponsive() {
        return !DriverManager.getDriver().getPageSource().isEmpty();
    }

    // ── mobile: gesture commands ───────────────────────────────────────────────

    /**
     * Scrolls down using the platform-native {@code mobile:} command.
     * Android: {@code mobile: scrollGesture} | iOS: {@code mobile: scroll}
     */
    public void performMobileScrollDown() {
        log.info("mobile: scroll down");
        MobileGestureUtils.scroll("down");
    }

    /**
     * Swipes left using the platform-native {@code mobile:} command.
     * Android: {@code mobile: swipeGesture} | iOS: {@code mobile: swipe}
     *
     * <p>{@code draggableItem} only exists on ApiDemos' Drag and Drop screen —
     * on iOS this swipes on the root table instead, since no drag/drop screen
     * exists there and the scenario only checks the app stays responsive.</p>
     */
    public void performMobileSwipeLeft() {
        log.info("mobile: swipe left");
        if (isIOS()) {
            WebElement table = driver().findElement(By.className("XCUIElementTypeTable"));
            MobileGestureUtils.swipe(table, "left");
        } else {
            MobileGestureUtils.swipe(draggableItem, "left");
        }
    }

    /**
     * Performs an Android-only fling gesture using {@code mobile: flingGesture}.
     *
     * <p>A fling is a fast swipe that continues scrolling with inertia after the finger
     * lifts. This has no W3C Actions equivalent — it must use the {@code mobile:} command.</p>
     *
     * @param direction "up", "down", "left", or "right"
     */
    public void performMobileFling(String direction) {
        log.info("mobile: flingGesture direction={}", direction);
        if (!isAndroid()) {
            log.warn("Fling gesture is Android-only — skipping on iOS");
            return;
        }
        MobileGestureUtils.flingAndroid(draggableItem, direction, 7500);
    }
}
