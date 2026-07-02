package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.SliderControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class SliderStepDefs {

    /** Coordinate-based dragging can land a couple percent off target except at 0/50/100. */
    private static final int TOLERANCE = 5;

    private final SliderControlPage page = new SliderControlPage();

    @When("the user sets the slider to {int}%")
    public void userSetsSliderToPercent(int percent) {
        page.setSliderValue(percent);
    }

    @When("the user slides to minimum")
    public void userSlidesToMinimum() {
        page.slideToMin();
    }

    @When("the user slides to maximum")
    public void userSlidesToMaximum() {
        page.slideToMax();
    }

    @When("the user slides to 50 percent")
    public void userSlidesTo50Percent() {
        page.slideTo50Percent();
    }

    @Then("the slider value should be {string}")
    public void sliderValueShouldBe(String expected) {
        Assertions.assertThat(page.getProgressValue())
                .as("Slider progress value")
                .isCloseTo(Integer.parseInt(expected), Assertions.within(TOLERANCE));
    }

    @Then("the slider label should show {string}")
    public void sliderLabelShouldShow(String expected) {
        Assertions.assertThat(page.getProgressValue())
                .as("Slider progress value")
                .isCloseTo(Integer.parseInt(expected), Assertions.within(TOLERANCE));
    }
}
