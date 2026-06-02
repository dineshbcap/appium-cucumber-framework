package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.DropdownControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class DropdownStepDefs {

    private final DropdownControlPage page = new DropdownControlPage();

    @When("the user opens dropdown {int}")
    public void userOpensDropdown(int number) {
        if (number == 1) page.openDropdown1();
        else page.openDropdown2();
    }

    @When("the user selects {string} from dropdown {int}")
    public void userSelectsFromDropdown(String option, int number) {
        if (number == 1) page.selectDropdown1ByText(option);
        else page.selectDropdown2ByText(option);
    }

    @When("the user selects index {int} from dropdown {int}")
    public void userSelectsIndexFromDropdown(int index, int dropdown) {
        if (dropdown == 1) page.selectDropdown1ByIndex(index);
    }

    @Then("dropdown {int} should show {string}")
    public void dropdownShouldShow(int number, String expected) {
        String actual = number == 1
                ? page.getSelectedDropdown1Value()
                : page.getSelectedDropdown2Value();
        Assertions.assertThat(actual)
                .as("Dropdown " + number + " selection")
                .isEqualTo(expected);
    }

    @Then("the dropdown result should contain {string}")
    public void dropdownResultShouldContain(String expected) {
        Assertions.assertThat(page.getResultText())
                .as("Dropdown result")
                .contains(expected);
    }
}
