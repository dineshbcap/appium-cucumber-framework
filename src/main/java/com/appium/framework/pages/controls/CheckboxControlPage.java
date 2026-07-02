package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page object for ApiDemos' real "Views &gt; Controls &gt; 1. Light Theme" screen,
 * which has exactly two checkboxes ({@code check1}, {@code check2}) and no
 * separate result label — checked state is read directly from each checkbox.
 */
public class CheckboxControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/check1")
    @iOSXCUITFindBy(accessibility = "checkbox1")
    private WebElement checkbox1;

    @AndroidFindBy(id = "io.appium.android.apis:id/check2")
    @iOSXCUITFindBy(accessibility = "checkbox2")
    private WebElement checkbox2;

    @AndroidFindBy(className = "android.widget.CheckBox")
    @iOSXCUITFindBy(className = "XCUIElementTypeSwitch")
    private List<WebElement> allCheckboxes;

    // ── Actions ───────────────────────────────────────────────────────────────

    public void checkCheckbox1() {
        log.info("Checking checkbox 1");
        if (!isChecked(checkbox1)) checkbox1.click();
    }

    public void uncheckCheckbox1() {
        log.info("Unchecking checkbox 1");
        if (isChecked(checkbox1)) checkbox1.click();
    }

    public void toggleCheckbox1() {
        log.info("Toggling checkbox 1");
        checkbox1.click();
    }

    public void toggleCheckbox2() {
        log.info("Toggling checkbox 2");
        checkbox2.click();
    }

    public boolean isCheckbox1Checked() {
        return isChecked(checkbox1);
    }

    public boolean isCheckbox2Checked() {
        return isChecked(checkbox2);
    }

    public int getTotalCheckboxCount() {
        return allCheckboxes.size();
    }

    public void checkAllCheckboxes() {
        log.info("Checking all checkboxes");
        allCheckboxes.forEach(cb -> { if (!isChecked(cb)) cb.click(); });
    }

    public void uncheckAllCheckboxes() {
        log.info("Unchecking all checkboxes");
        allCheckboxes.forEach(cb -> { if (isChecked(cb)) cb.click(); });
    }

    private boolean isChecked(WebElement element) {
        String checked = element.getAttribute("checked");
        return "true".equals(checked);
    }
}
