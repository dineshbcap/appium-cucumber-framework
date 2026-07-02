package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.GestureControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class GestureStepDefs {

    private final GestureControlPage page = new GestureControlPage();

    @When("the user swipes up on the screen")
    public void userSwipesUp() {
        page.performSwipeUp();
    }

    @When("the user swipes left on the screen")
    public void userSwipesLeft() {
        page.performSwipeLeft();
    }

    @When("the user drags the item to the drop zone")
    public void userDragsToDropZone() {
        page.performDragAndDrop();
    }

    @Then("the app should remain responsive")
    public void appShouldRemainResponsive() {
        Assertions.assertThat(page.isAppResponsive())
                .as("App should remain responsive after gesture")
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

    @When("the user swipes left using mobile command")
    public void userSwipesLeftUsingMobileCommand() {
        page.performMobileSwipeLeft();
    }

    @When("the user flings down using mobile command")
    public void userFlingsDownUsingMobileCommand() {
        page.performMobileFling("down");
    }
}
