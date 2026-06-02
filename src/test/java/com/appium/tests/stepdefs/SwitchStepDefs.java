package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.SwitchControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class SwitchStepDefs {

    private final SwitchControlPage page = new SwitchControlPage();

    @When("the user toggles the Wi-Fi switch")
    public void userTogglesWifiSwitch() {
        page.toggleWifiSwitch();
    }

    @When("the user toggles the Bluetooth switch")
    public void userTogglesBluetoothSwitch() {
        page.toggleBluetoothSwitch();
    }

    @When("the user toggles the Notifications switch")
    public void userTogglesNotificationsSwitch() {
        page.toggleNotificationsSwitch();
    }

    @When("the user enables Wi-Fi")
    public void userEnablesWifi() {
        page.enableWifi();
    }

    @When("the user disables Wi-Fi")
    public void userDisablesWifi() {
        page.disableWifi();
    }

    @Then("the Wi-Fi switch should be on")
    public void wifiSwitchShouldBeOn() {
        Assertions.assertThat(page.isWifiEnabled()).as("Wi-Fi switch on").isTrue();
    }

    @Then("the Wi-Fi switch should be off")
    public void wifiSwitchShouldBeOff() {
        Assertions.assertThat(page.isWifiEnabled()).as("Wi-Fi switch off").isFalse();
    }

    @Then("the Bluetooth switch should be on")
    public void bluetoothSwitchShouldBeOn() {
        Assertions.assertThat(page.isBluetoothEnabled()).as("Bluetooth switch on").isTrue();
    }

    @Then("the page should have {int} switches")
    public void pageShouldHaveSwitches(int count) {
        Assertions.assertThat(page.getSwitchCount()).as("Switch count").isEqualTo(count);
    }
}
