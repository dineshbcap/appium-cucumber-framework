package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.SwitchControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class SwitchStepDefs {

    private final SwitchControlPage page = new SwitchControlPage();

    @When("the user toggles the standard switch")
    public void userTogglesStandardSwitch() {
        page.toggleStandardSwitch();
    }

    @When("the user toggles the monitored switch")
    public void userTogglesMonitoredSwitch() {
        page.toggleMonitoredSwitch();
    }

    @Then("the standard switch should be on")
    public void standardSwitchShouldBeOn() {
        Assertions.assertThat(page.isStandardSwitchOn()).as("Standard switch on").isTrue();
    }

    @Then("the standard switch should be off")
    public void standardSwitchShouldBeOff() {
        Assertions.assertThat(page.isStandardSwitchOn()).as("Standard switch off").isFalse();
    }

    @Then("the default-on switch should be on")
    public void defaultOnSwitchShouldBeOn() {
        Assertions.assertThat(page.isDefaultOnSwitchOn()).as("Default-on switch on").isTrue();
    }

    @Then("the monitored switch should be on")
    public void monitoredSwitchShouldBeOn() {
        Assertions.assertThat(page.isMonitoredSwitchOn()).as("Monitored switch on").isTrue();
    }

    @Then("the page should have {int} switches")
    public void pageShouldHaveSwitches(int count) {
        Assertions.assertThat(page.getSwitchCount()).as("Switch count").isEqualTo(count);
    }
}
