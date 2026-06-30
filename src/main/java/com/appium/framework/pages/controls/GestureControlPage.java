package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import com.appium.framework.utils.MobileGestureUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Gesture interactions for the gesture demo screen.
 *
 * <p>Covers two approaches side-by-side:
 * <ul>
 *   <li><b>W3C Actions</b> via {@link GestureUtils} — cross-platform, fine-grained control</li>
 *   <li><b>mobile: commands</b> via {@link MobileGestureUtils} — native engine delegation,
 *       simpler API, better for flings and iOS scrolls</li>
 * </ul>
 * </p>
 */
public class GestureControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/gesture_target")
    @iOSXCUITFindBy(accessibility = "gestureTarget")
    private WebElement gestureTarget;

    @AndroidFindBy(id = "io.appium.android.apis:id/draggable_item")
    @iOSXCUITFindBy(accessibility = "draggableItem")
    private WebElement draggableItem;

    @AndroidFindBy(id = "io.appium.android.apis:id/drop_zone")
    @iOSXCUITFindBy(accessibility = "dropZone")
    private WebElement dropZone;

    @AndroidFindBy(id = "io.appium.android.apis:id/zoomable_image")
    @iOSXCUITFindBy(accessibility = "zoomableImage")
    private WebElement zoomableImage;

    @AndroidFindBy(id = "io.appium.android.apis:id/gesture_result")
    @iOSXCUITFindBy(accessibility = "gestureResult")
    private WebElement gestureResult;

    // ── Actions ───────────────────────────────────────────────────────────────

    public void performTap() {
        log.info("Performing tap gesture");
        GestureUtils.tap(gestureTarget);
    }

    public void performLongPress() {
        log.info("Performing long press gesture");
        GestureUtils.longPress(gestureTarget);
    }

    public void performDoubleTap() {
        log.info("Performing double tap gesture");
        GestureUtils.doubleTap(gestureTarget);
    }

    public void performSwipeUp() {
        log.info("Performing swipe up");
        GestureUtils.swipeUp();
    }

    public void performSwipeDown() {
        log.info("Performing swipe down");
        GestureUtils.swipeDown();
    }

    public void performSwipeLeft() {
        log.info("Performing swipe left");
        GestureUtils.swipeLeft();
    }

    public void performSwipeRight() {
        log.info("Performing swipe right");
        GestureUtils.swipeRight();
    }

    public void performPinchZoomIn() {
        log.info("Performing pinch zoom in");
        GestureUtils.pinchToZoomIn(zoomableImage);
    }

    public void performPinchZoomOut() {
        log.info("Performing pinch zoom out");
        GestureUtils.pinchToZoomOut(zoomableImage);
    }

    public void performDragAndDrop() {
        log.info("Performing drag and drop");
        GestureUtils.dragAndDrop(draggableItem, dropZone);
    }

    public String getGestureResult() {
        return gestureResult.getText();
    }

    public boolean isGestureResultDisplayed() {
        return isDisplayed(By.id("io.appium.android.apis:id/gesture_result"));
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
     * Scrolls up using the platform-native {@code mobile:} command.
     */
    public void performMobileScrollUp() {
        log.info("mobile: scroll up");
        MobileGestureUtils.scroll("up");
    }

    /**
     * Swipes left on the gesture target using the platform-native {@code mobile:} command.
     * Android: {@code mobile: swipeGesture} | iOS: {@code mobile: swipe}
     */
    public void performMobileSwipeLeft() {
        log.info("mobile: swipe left on gestureTarget");
        MobileGestureUtils.swipe(gestureTarget, "left");
    }

    /**
     * Swipes right on the gesture target using the platform-native {@code mobile:} command.
     */
    public void performMobileSwipeRight() {
        log.info("mobile: swipe right on gestureTarget");
        MobileGestureUtils.swipe(gestureTarget, "right");
    }

    /**
     * Double taps the gesture target using the platform-native {@code mobile:} command.
     * Android: {@code mobile: doubleClickGesture} | iOS: {@code mobile: doubleTap}
     */
    public void performMobileDoubleTap() {
        log.info("mobile: doubleTap/doubleClick on gestureTarget");
        MobileGestureUtils.doubleTap(gestureTarget);
    }

    /**
     * Long presses the gesture target using the platform-native {@code mobile:} command.
     * Android: {@code mobile: longClickGesture} | iOS: {@code mobile: longPress}
     *
     * @param durationMs hold duration in milliseconds
     */
    public void performMobileLongPress(long durationMs) {
        log.info("mobile: longPress for {}ms on gestureTarget", durationMs);
        MobileGestureUtils.longPress(gestureTarget, durationMs);
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
        MobileGestureUtils.flingAndroid(gestureTarget, direction, 7500);
    }

    /**
     * Zooms in on the zoomable image using the platform-native {@code mobile:} pinch command.
     * Android: {@code mobile: pinchOpenGesture} | iOS: {@code mobile: pinch scale>1}
     */
    public void performMobilePinchOpen() {
        log.info("mobile: pinch open (zoom in) on zoomableImage");
        if (isAndroid()) {
            MobileGestureUtils.pinchOpenAndroid(zoomableImage, 0.5, 500);
        } else {
            MobileGestureUtils.pinchIos(zoomableImage, 2.0, 1.0);
        }
    }

    /**
     * Zooms out on the zoomable image using the platform-native {@code mobile:} pinch command.
     * Android: {@code mobile: pinchCloseGesture} | iOS: {@code mobile: pinch scale<1}
     */
    public void performMobilePinchClose() {
        log.info("mobile: pinch close (zoom out) on zoomableImage");
        if (isAndroid()) {
            MobileGestureUtils.pinchCloseAndroid(zoomableImage, 0.5, 500);
        } else {
            MobileGestureUtils.pinchIos(zoomableImage, 0.5, 1.0);
        }
    }
}
