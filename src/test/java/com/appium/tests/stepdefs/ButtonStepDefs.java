package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.ButtonControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class ButtonStepDefs {

    private final ButtonControlPage page = new ButtonControlPage();

    @When("the user taps the Normal button")
    public void userTapsNormalButton() {
        page.tapNormalButton();
    }

    @When("the user taps the Small button")
    public void userTapsSmallButton() {
        page.tapSmallButton();
    }

    @When("the user taps the toggle button")
    public void userTapsToggleButton() {
        page.tapToggleButton();
    }

    @When("the user taps the button labeled {string}")
    public void userTapsButtonLabeled(String label) {
        page.tapButtonLabeled(label);
    }

    @Then("the Normal button should remain displayed and enabled")
    public void normalButtonShouldRemainDisplayedAndEnabled() {
        Assertions.assertThat(page.isNormalButtonDisplayed()).as("Normal button displayed").isTrue();
        Assertions.assertThat(page.isNormalButtonEnabled()).as("Normal button enabled").isTrue();
    }

    @Then("the toggle button should show {string}")
    public void toggleButtonShouldShow(String expected) {
        Assertions.assertThat(page.getToggleButtonText())
                .as("Toggle button text")
                .isEqualTo(expected);
    }

    @Then("the tap button should be displayed")
    public void tapButtonShouldBeDisplayed() {
        Assertions.assertThat(page.isNormalButtonDisplayed())
                .as("Normal button should be visible")
                .isTrue();
    }

    @Then("the tap button should be enabled")
    public void tapButtonShouldBeEnabled() {
        Assertions.assertThat(page.isNormalButtonEnabled())
                .as("Normal button should be enabled")
                .isTrue();
    }
}
