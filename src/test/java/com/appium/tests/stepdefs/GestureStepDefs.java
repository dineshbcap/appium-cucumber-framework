package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.GestureControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class GestureStepDefs {

    private final GestureControlPage page = new GestureControlPage();

    @When("the user performs a tap gesture")
    public void userPerformsTap() {
        page.performTap();
    }

    @When("the user performs a long press gesture")
    public void userPerformsLongPress() {
        page.performLongPress();
    }

    @When("the user performs a double tap gesture")
    public void userPerformsDoubleTap() {
        page.performDoubleTap();
    }

    @When("the user swipes up on the screen")
    public void userSwipesUp() {
        page.performSwipeUp();
    }

    @When("the user swipes down on the screen")
    public void userSwipesDown() {
        page.performSwipeDown();
    }

    @When("the user swipes left on the screen")
    public void userSwipesLeft() {
        page.performSwipeLeft();
    }

    @When("the user swipes right on the screen")
    public void userSwipesRight() {
        page.performSwipeRight();
    }

    @When("the user pinches to zoom in")
    public void userPinchesZoomIn() {
        page.performPinchZoomIn();
    }

    @When("the user pinches to zoom out")
    public void userPinchesZoomOut() {
        page.performPinchZoomOut();
    }

    @When("the user drags the item to the drop zone")
    public void userDragsToDropZone() {
        page.performDragAndDrop();
    }

    @Then("the gesture result should be displayed")
    public void gestureResultShouldBeDisplayed() {
        Assertions.assertThat(page.isGestureResultDisplayed())
                .as("Gesture result should be displayed")
                .isTrue();
    }

    @Then("the gesture result should contain {string}")
    public void gestureResultShouldContain(String expected) {
        Assertions.assertThat(page.getGestureResult())
                .as("Gesture result")
                .contains(expected);
    }

    // ── mobile: gesture command step definitions ───────────────────────────────
    // These demonstrate Appium 2.x mobile: commands via executeScript(),
    // which delegate gesture execution to UiAutomator2 (Android) or XCUITest (iOS).

    @When("the user scrolls down using mobile command")
    public void userScrollsDownUsingMobileCommand() {
        page.performMobileScrollDown();
    }

    @When("the user scrolls up using mobile command")
    public void userScrollsUpUsingMobileCommand() {
        page.performMobileScrollUp();
    }

    @When("the user swipes left using mobile command")
    public void userSwipesLeftUsingMobileCommand() {
        page.performMobileSwipeLeft();
    }

    @When("the user swipes right using mobile command")
    public void userSwipesRightUsingMobileCommand() {
        page.performMobileSwipeRight();
    }

    @When("the user double taps using mobile command")
    public void userDoubleTapsUsingMobileCommand() {
        page.performMobileDoubleTap();
    }

    @When("the user long presses using mobile command for {int} milliseconds")
    public void userLongPressesUsingMobileCommand(int durationMs) {
        page.performMobileLongPress(durationMs);
    }

    @When("the user flings down using mobile command")
    public void userFlingsDownUsingMobileCommand() {
        page.performMobileFling("down");
    }

    @When("the user pinches open using mobile command")
    public void userPinchesOpenUsingMobileCommand() {
        page.performMobilePinchOpen();
    }

    @When("the user pinches close using mobile command")
    public void userPinchesCloseUsingMobileCommand() {
        page.performMobilePinchClose();
    }
}
