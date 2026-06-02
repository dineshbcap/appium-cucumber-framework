package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CheckboxControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/checkbox_1")
    @iOSXCUITFindBy(accessibility = "checkbox1")
    private WebElement checkbox1;

    @AndroidFindBy(id = "io.appium.android.apis:id/checkbox_2")
    @iOSXCUITFindBy(accessibility = "checkbox2")
    private WebElement checkbox2;

    @AndroidFindBy(id = "io.appium.android.apis:id/checkbox_3")
    @iOSXCUITFindBy(accessibility = "checkbox3")
    private WebElement checkbox3;

    @AndroidFindBy(className = "android.widget.CheckBox")
    @iOSXCUITFindBy(className = "XCUIElementTypeSwitch")
    private List<WebElement> allCheckboxes;

    @AndroidFindBy(id = "io.appium.android.apis:id/checkbox_result")
    @iOSXCUITFindBy(accessibility = "checkboxResult")
    private WebElement resultLabel;

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

    public void toggleCheckbox3() {
        log.info("Toggling checkbox 3");
        checkbox3.click();
    }

    public boolean isCheckbox1Checked() {
        return isChecked(checkbox1);
    }

    public boolean isCheckbox2Checked() {
        return isChecked(checkbox2);
    }

    public boolean isCheckbox3Checked() {
        return isChecked(checkbox3);
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

    public String getResultText() {
        return resultLabel.getText();
    }

    private boolean isChecked(WebElement element) {
        String checked = element.getAttribute("checked");
        return "true".equals(checked);
    }
}
