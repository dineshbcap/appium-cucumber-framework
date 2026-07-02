package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.RadioButtonControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class RadioButtonStepDefs {

    private final RadioButtonControlPage page = new RadioButtonControlPage();

    @When("the user selects radio button {int}")
    public void userSelectsRadioButton(int number) {
        switch (number) {
            case 1 -> page.selectRadio1();
            case 2 -> page.selectRadio2();
            default -> throw new IllegalArgumentException("Unknown radio button: " + number);
        }
    }

    @When("the user selects radio button at index {int}")
    public void userSelectsRadioAtIndex(int index) {
        page.selectRadioByIndex(index);
    }

    @Then("radio button {int} should be selected")
    public void radioButtonShouldBeSelected(int number) {
        boolean selected = switch (number) {
            case 1 -> page.isRadio1Selected();
            case 2 -> page.isRadio2Selected();
            default -> throw new IllegalArgumentException("Unknown radio button: " + number);
        };
        Assertions.assertThat(selected).as("Radio button " + number + " selected").isTrue();
    }

    @Then("radio button {int} should not be selected")
    public void radioButtonShouldNotBeSelected(int number) {
        boolean selected = switch (number) {
            case 1 -> page.isRadio1Selected();
            case 2 -> page.isRadio2Selected();
            default -> throw new IllegalArgumentException("Unknown radio button: " + number);
        };
        Assertions.assertThat(selected).as("Radio button " + number + " should not be selected").isFalse();
    }

    @Then("the selected radio index should be {int}")
    public void selectedRadioIndexShouldBe(int expectedIndex) {
        Assertions.assertThat(page.getSelectedRadioIndex())
                .as("Selected radio index")
                .isEqualTo(expectedIndex);
    }

    @Then("there should be {int} radio buttons")
    public void thereShouldBeRadioButtons(int count) {
        Assertions.assertThat(page.getRadioButtonCount())
                .as("Radio button count")
                .isEqualTo(count);
    }
}
