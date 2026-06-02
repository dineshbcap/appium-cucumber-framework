package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SwitchControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/toggle_wifi")
    @iOSXCUITFindBy(accessibility = "wifiSwitch")
    private WebElement wifiSwitch;

    @AndroidFindBy(id = "io.appium.android.apis:id/toggle_bluetooth")
    @iOSXCUITFindBy(accessibility = "bluetoothSwitch")
    private WebElement bluetoothSwitch;

    @AndroidFindBy(id = "io.appium.android.apis:id/toggle_notifications")
    @iOSXCUITFindBy(accessibility = "notificationsSwitch")
    private WebElement notificationsSwitch;

    @AndroidFindBy(className = "android.widget.Switch")
    @iOSXCUITFindBy(className = "XCUIElementTypeSwitch")
    private List<WebElement> allSwitches;

    @AndroidFindBy(id = "io.appium.android.apis:id/switch_result")
    @iOSXCUITFindBy(accessibility = "switchResult")
    private WebElement resultLabel;

    // ── Actions ───────────────────────────────────────────────────────────────

    public void toggleWifiSwitch() {
        log.info("Toggling Wi-Fi switch");
        wifiSwitch.click();
    }

    public void toggleBluetoothSwitch() {
        log.info("Toggling Bluetooth switch");
        bluetoothSwitch.click();
    }

    public void toggleNotificationsSwitch() {
        log.info("Toggling Notifications switch");
        notificationsSwitch.click();
    }

    public void enableWifi() {
        if (!isWifiEnabled()) wifiSwitch.click();
    }

    public void disableWifi() {
        if (isWifiEnabled()) wifiSwitch.click();
    }

    public boolean isWifiEnabled() {
        return "true".equals(wifiSwitch.getAttribute("checked"));
    }

    public boolean isBluetoothEnabled() {
        return "true".equals(bluetoothSwitch.getAttribute("checked"));
    }

    public boolean isNotificationsEnabled() {
        return "true".equals(notificationsSwitch.getAttribute("checked"));
    }

    public int getSwitchCount() {
        return allSwitches.size();
    }

    public String getResultText() {
        return resultLabel.getText();
    }
}
