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

    @When("the user appends {string} to the text field")
    public void userAppendsText(String text) {
        page.appendText(text);
    }

    @Then("the text field should contain {string}")
    public void textFieldShouldContain(String expected) {
        Assertions.assertThat(page.getTextFieldValue())
                .as("Text field value")
                .isEqualTo(expected);
    }

    @Then("the text field should be empty")
    public void textFieldShouldBeEmpty() {
        Assertions.assertThat(page.isTextFieldEmpty())
                .as("Text field should be empty (showing hint text)")
                .isTrue();
    }

    @Then("the password field should be focused")
    public void passwordFieldShouldBeFocused() {
        Assertions.assertThat(page.isPasswordFieldFocused())
                .as("Password field should be focused")
                .isTrue();
    }
}
