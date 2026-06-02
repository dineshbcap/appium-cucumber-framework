package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

public class RadioButtonControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/radio_button_1")
    @iOSXCUITFindBy(accessibility = "radioButton1")
    private WebElement radio1;

    @AndroidFindBy(id = "io.appium.android.apis:id/radio_button_2")
    @iOSXCUITFindBy(accessibility = "radioButton2")
    private WebElement radio2;

    @AndroidFindBy(id = "io.appium.android.apis:id/radio_button_3")
    @iOSXCUITFindBy(accessibility = "radioButton3")
    private WebElement radio3;

    @AndroidFindBy(className = "android.widget.RadioButton")
    @iOSXCUITFindBy(className = "XCUIElementTypeRadioButton")
    private List<WebElement> allRadioButtons;

    @AndroidFindBy(id = "io.appium.android.apis:id/radio_result")
    @iOSXCUITFindBy(accessibility = "radioResult")
    private WebElement resultLabel;

    // ── Actions ───────────────────────────────────────────────────────────────

    public void selectRadio1() {
        log.info("Selecting radio button 1");
        radio1.click();
    }

    public void selectRadio2() {
        log.info("Selecting radio button 2");
        radio2.click();
    }

    public void selectRadio3() {
        log.info("Selecting radio button 3");
        radio3.click();
    }

    public boolean isRadio1Selected() {
        return isChecked(radio1);
    }

    public boolean isRadio2Selected() {
        return isChecked(radio2);
    }

    public boolean isRadio3Selected() {
        return isChecked(radio3);
    }

    public void selectRadioByIndex(int index) {
        if (index < 0 || index >= allRadioButtons.size()) {
            throw new IndexOutOfBoundsException("Radio button index out of range: " + index);
        }
        log.info("Selecting radio button at index: {}", index);
        allRadioButtons.get(index).click();
    }

    public int getSelectedRadioIndex() {
        for (int i = 0; i < allRadioButtons.size(); i++) {
            if (isChecked(allRadioButtons.get(i))) return i;
        }
        return -1;
    }

    public int getRadioButtonCount() {
        return allRadioButtons.size();
    }

    public String getResultText() {
        return resultLabel.getText();
    }

    private boolean isChecked(WebElement element) {
        return "true".equals(element.getAttribute("checked"));
    }
}
