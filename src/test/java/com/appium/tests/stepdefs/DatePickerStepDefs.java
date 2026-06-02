package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.DatePickerControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class DatePickerStepDefs {

    private final DatePickerControlPage page = new DatePickerControlPage();

    @When("the user opens the date picker")
    public void userOpensDatePicker() {
        page.openDatePicker();
    }

    @When("the user opens the time picker")
    public void userOpensTimePicker() {
        page.openTimePicker();
    }

    @When("the user selects date {int}-{int}-{int}")
    public void userSelectsDate(int year, int month, int day) {
        page.selectDate(year, month, day);
    }

    @When("the user confirms the date picker")
    public void userConfirmsDatePicker() {
        page.confirmDatePicker();
    }

    @When("the user cancels the date picker")
    public void userCancelsDatePicker() {
        page.cancelDatePicker();
    }

    @When("the user sets time to {int}:{int} {string}")
    public void userSetsTime(int hour, int minute, String amPm) {
        page.setTime(hour, minute, "AM".equalsIgnoreCase(amPm));
    }

    @Then("the selected date should contain {string}")
    public void selectedDateShouldContain(String expected) {
        Assertions.assertThat(page.getSelectedDate())
                .as("Selected date")
                .contains(expected);
    }
}
