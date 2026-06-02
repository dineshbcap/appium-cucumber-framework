package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.ButtonControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class ButtonStepDefs {

    private final ButtonControlPage page = new ButtonControlPage();

    @When("the user taps the button")
    public void userTapsButton() {
        page.tapButton();
    }

    @When("the user long presses the button")
    public void userLongPressesButton() {
        page.longPressButton();
    }

    @When("the user double taps the button")
    public void userDoubleTapsButton() {
        page.doubleTapButton();
    }

    @When("the user taps the button with text {string}")
    public void userTapsButtonWithText(String text) {
        page.tapButtonByText(text);
    }

    @Then("the button result should contain {string}")
    public void buttonResultShouldContain(String expected) {
        Assertions.assertThat(page.getResultText())
                .as("Button result text")
                .contains(expected);
    }

    @Then("the tap button should be displayed")
    public void tapButtonShouldBeDisplayed() {
        Assertions.assertThat(page.isTapButtonDisplayed())
                .as("Tap button should be visible")
                .isTrue();
    }

    @Then("the tap button should be enabled")
    public void tapButtonShouldBeEnabled() {
        Assertions.assertThat(page.isTapButtonEnabled())
                .as("Tap button should be enabled")
                .isTrue();
    }
}
