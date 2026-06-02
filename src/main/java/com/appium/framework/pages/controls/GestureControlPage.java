package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
}
