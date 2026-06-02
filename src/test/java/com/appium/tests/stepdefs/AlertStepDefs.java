package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.AlertControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class AlertStepDefs {

    private final AlertControlPage page = new AlertControlPage();

    @When("the user triggers a simple alert")
    public void userTriggersSimpleAlert() {
        page.triggerSimpleAlert();
    }

    @When("the user triggers a confirm dialog")
    public void userTriggersConfirmDialog() {
        page.triggerConfirmDialog();
    }

    @When("the user triggers a prompt dialog")
    public void userTriggersPromptDialog() {
        page.triggerPromptDialog();
    }

    @When("the user accepts the alert")
    public void userAcceptsAlert() {
        page.acceptAlert();
    }

    @When("the user dismisses the alert")
    public void userDismissesAlert() {
        page.dismissAlert();
    }

    @When("the user enters {string} in the prompt")
    public void userEntersInPrompt(String text) {
        page.enterPromptText(text);
    }

    @Then("the alert should be displayed")
    public void alertShouldBeDisplayed() {
        Assertions.assertThat(page.isAlertDisplayed())
                .as("Alert dialog should be displayed")
                .isTrue();
    }

    @Then("the alert title should be {string}")
    public void alertTitleShouldBe(String expectedTitle) {
        Assertions.assertThat(page.getAlertTitle())
                .as("Alert title")
                .isEqualTo(expectedTitle);
    }

    @Then("the alert message should contain {string}")
    public void alertMessageShouldContain(String expected) {
        Assertions.assertThat(page.getAlertMessage())
                .as("Alert message")
                .contains(expected);
    }

    @Then("the alert result should show {string}")
    public void alertResultShouldShow(String expected) {
        Assertions.assertThat(page.getResultText())
                .as("Alert result")
                .contains(expected);
    }
}
