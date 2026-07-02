package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page object for ApiDemos' real "Views &gt; Switches" screen: 10 {@code Switch}
 * widgets identified by their content-desc/text (e.g. "Standard switch",
 * "Default is on"), only one of which ({@code monitored_switch}) has a
 * resource-id — most are matched by accessibility id instead.
 */
public class SwitchControlPage extends BasePage {

    @AndroidFindBy(accessibility = "Standard switch")
    @iOSXCUITFindBy(accessibility = "standardSwitch")
    private WebElement standardSwitch;

    @AndroidFindBy(accessibility = "Default is on")
    @iOSXCUITFindBy(accessibility = "defaultOnSwitch")
    private WebElement defaultOnSwitch;

    @AndroidFindBy(id = "io.appium.android.apis:id/monitored_switch")
    @iOSXCUITFindBy(accessibility = "monitoredSwitch")
    private WebElement monitoredSwitch;

    @AndroidFindBy(className = "android.widget.Switch")
    @iOSXCUITFindBy(className = "XCUIElementTypeSwitch")
    private List<WebElement> allSwitches;

    // ── Actions ───────────────────────────────────────────────────────────────

    public void toggleStandardSwitch() {
        log.info("Toggling standard switch");
        standardSwitch.click();
    }

    public void toggleDefaultOnSwitch() {
        log.info("Toggling default-on switch");
        defaultOnSwitch.click();
    }

    public void toggleMonitoredSwitch() {
        log.info("Toggling monitored switch");
        monitoredSwitch.click();
    }

    public boolean isStandardSwitchOn() {
        return isChecked(standardSwitch);
    }

    public boolean isDefaultOnSwitchOn() {
        return isChecked(defaultOnSwitch);
    }

    public boolean isMonitoredSwitchOn() {
        return isChecked(monitoredSwitch);
    }

    public int getSwitchCount() {
        return allSwitches.size();
    }

    private boolean isChecked(WebElement element) {
        return "true".equals(element.getAttribute("checked"));
    }
}
