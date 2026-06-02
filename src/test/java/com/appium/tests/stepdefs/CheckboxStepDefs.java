package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.CheckboxControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class CheckboxStepDefs {

    private final CheckboxControlPage page = new CheckboxControlPage();

    @When("the user checks checkbox {int}")
    public void userChecksCheckbox(int number) {
        switch (number) {
            case 1 -> page.checkCheckbox1();
            case 2 -> page.toggleCheckbox2();
            case 3 -> page.toggleCheckbox3();
            default -> throw new IllegalArgumentException("Unknown checkbox: " + number);
        }
    }

    @When("the user unchecks checkbox {int}")
    public void userUnchecksCheckbox(int number) {
        if (number == 1) page.uncheckCheckbox1();
    }

    @When("the user toggles checkbox {int}")
    public void userTogglesCheckbox(int number) {
        switch (number) {
            case 1 -> page.toggleCheckbox1();
            case 2 -> page.toggleCheckbox2();
            case 3 -> page.toggleCheckbox3();
            default -> throw new IllegalArgumentException("Unknown checkbox: " + number);
        }
    }

    @When("the user checks all checkboxes")
    public void userChecksAllCheckboxes() {
        page.checkAllCheckboxes();
    }

    @When("the user unchecks all checkboxes")
    public void userUnchecksAllCheckboxes() {
        page.uncheckAllCheckboxes();
    }

    @Then("checkbox {int} should be checked")
    public void checkboxShouldBeChecked(int number) {
        boolean checked = switch (number) {
            case 1 -> page.isCheckbox1Checked();
            case 2 -> page.isCheckbox2Checked();
            case 3 -> page.isCheckbox3Checked();
            default -> throw new IllegalArgumentException("Unknown checkbox: " + number);
        };
        Assertions.assertThat(checked).as("Checkbox " + number + " checked state").isTrue();
    }

    @Then("checkbox {int} should be unchecked")
    public void checkboxShouldBeUnchecked(int number) {
        boolean checked = switch (number) {
            case 1 -> page.isCheckbox1Checked();
            case 2 -> page.isCheckbox2Checked();
            case 3 -> page.isCheckbox3Checked();
            default -> throw new IllegalArgumentException("Unknown checkbox: " + number);
        };
        Assertions.assertThat(checked).as("Checkbox " + number + " should be unchecked").isFalse();
    }

    @Then("the page should have {int} checkboxes")
    public void pageShouldHaveCheckboxes(int count) {
        Assertions.assertThat(page.getTotalCheckboxCount())
                .as("Total checkbox count")
                .isEqualTo(count);
    }
}
