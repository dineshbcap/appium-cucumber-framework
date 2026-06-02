package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.TextInputControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class TextInputStepDefs {

    private final TextInputControlPage page = new TextInputControlPage();

    @When("the user enters {string} in the text field")
    public void userEntersTextInTextField(String text) {
        page.enterText(text);
    }

    @When("the user clears the text field")
    public void userClearsTextField() {
        page.clearTextField();
    }

    @When("the user enters password {string}")
    public void userEntersPassword(String password) {
        page.enterPassword(password);
    }

    @When("the user enters multiline text {string}")
    public void userEntersMultilineText(String text) {
        page.enterMultilineText(text);
    }

    @When("the user appends {string} to the text field")
    public void userAppendsText(String text) {
        page.appendText(text);
    }

    @When("the user submits the form")
    public void userSubmitsForm() {
        page.submitForm();
    }

    @Then("the text field should contain {string}")
    public void textFieldShouldContain(String expected) {
        Assertions.assertThat(page.getTextFieldValue())
                .as("Text field value")
                .isEqualTo(expected);
    }

    @Then("the text field should be empty")
    public void textFieldShouldBeEmpty() {
        Assertions.assertThat(page.getTextFieldValue())
                .as("Text field should be empty")
                .isEmpty();
    }

    @Then("the text field should be focused")
    public void textFieldShouldBeFocused() {
        Assertions.assertThat(page.isTextFieldFocused())
                .as("Text field should be focused")
                .isTrue();
    }

    @Then("the input result should show {string}")
    public void inputResultShouldShow(String expected) {
        Assertions.assertThat(page.getResultText())
                .as("Input result")
                .contains(expected);
    }
}
