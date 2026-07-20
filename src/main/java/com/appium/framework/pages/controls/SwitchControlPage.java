package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "Views &gt; Switches" screen: 10 {@code Switch}
 * widgets identified by their content-desc/text (e.g. "Standard switch",
 * "Default is on"), only one of which ({@code monitored_switch}) has a
 * resource-id — most are matched by accessibility id instead.
 *
 * <p>Locators live in {@code locators_android.properties} / {@code locators_ios.properties}
 * under the {@code switch.*} keys.</p>
 */
public class SwitchControlPage extends BasePage {

    // ── Actions ───────────────────────────────────────────────────────────────

    public void toggleStandardSwitch() {
        log.info("Toggling standard switch");
        click("switch.standard");
    }

    public void toggleDefaultOnSwitch() {
        log.info("Toggling default-on switch");
        click("switch.defaultOn");
    }

    public void toggleMonitoredSwitch() {
        log.info("Toggling monitored switch");
        click("switch.monitored");
    }

    public boolean isStandardSwitchOn() {
        return isChecked(element("switch.standard"));
    }

    public boolean isDefaultOnSwitchOn() {
        return isChecked(element("switch.defaultOn"));
    }

    public boolean isMonitoredSwitchOn() {
        return isChecked(element("switch.monitored"));
    }

    public int getSwitchCount() {
        return elements("switch.all").size();
    }

    private boolean isChecked(WebElement element) {
        return "true".equals(element.getAttribute("checked"));
    }
}
