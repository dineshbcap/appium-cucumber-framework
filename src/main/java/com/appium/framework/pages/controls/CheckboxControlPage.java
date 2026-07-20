package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "Views &gt; Controls &gt; 1. Light Theme" screen,
 * which has exactly two checkboxes ({@code check1}, {@code check2}) and no
 * separate result label — checked state is read directly from each checkbox.
 *
 * <p>Locators live in {@code locators_android.properties} / {@code locators_ios.properties}
 * under the {@code checkbox.*} keys.</p>
 */
public class CheckboxControlPage extends BasePage {

    // ── Actions ───────────────────────────────────────────────────────────────

    public void checkCheckbox1() {
        log.info("Checking checkbox 1");
        WebElement checkbox1 = element("checkbox.one");
        if (!isChecked(checkbox1)) checkbox1.click();
    }

    public void uncheckCheckbox1() {
        log.info("Unchecking checkbox 1");
        WebElement checkbox1 = element("checkbox.one");
        if (isChecked(checkbox1)) checkbox1.click();
    }

    public void toggleCheckbox1() {
        log.info("Toggling checkbox 1");
        click("checkbox.one");
    }

    public void toggleCheckbox2() {
        log.info("Toggling checkbox 2");
        click("checkbox.two");
    }

    public boolean isCheckbox1Checked() {
        return isChecked(element("checkbox.one"));
    }

    public boolean isCheckbox2Checked() {
        return isChecked(element("checkbox.two"));
    }

    public int getTotalCheckboxCount() {
        return elements("checkbox.all").size();
    }

    public void checkAllCheckboxes() {
        log.info("Checking all checkboxes");
        elements("checkbox.all").forEach(cb -> { if (!isChecked(cb)) cb.click(); });
    }

    public void uncheckAllCheckboxes() {
        log.info("Unchecking all checkboxes");
        elements("checkbox.all").forEach(cb -> { if (isChecked(cb)) cb.click(); });
    }

    private boolean isChecked(WebElement element) {
        String checked = element.getAttribute("checked");
        return "true".equals(checked);
    }
}
