package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page object for ApiDemos' real "Views &gt; Controls &gt; 1. Light Theme" screen,
 * which has a two-button {@code RadioGroup} ({@code radio1}, {@code radio2}) and
 * no separate result label — selection state is read directly from each button.
 *
 * <p>Locators live in {@code locators_android.properties} / {@code locators_ios.properties}
 * under the {@code radio.*} keys.</p>
 */
public class RadioButtonControlPage extends BasePage {

    // ── Actions ───────────────────────────────────────────────────────────────

    public void selectRadio1() {
        log.info("Selecting radio button 1");
        click("radio.one");
    }

    public void selectRadio2() {
        log.info("Selecting radio button 2");
        click("radio.two");
    }

    public boolean isRadio1Selected() {
        return isChecked(element("radio.one"));
    }

    public boolean isRadio2Selected() {
        return isChecked(element("radio.two"));
    }

    public void selectRadioByIndex(int index) {
        List<WebElement> allRadioButtons = elements("radio.all");
        if (index < 0 || index >= allRadioButtons.size()) {
            throw new IndexOutOfBoundsException("Radio button index out of range: " + index);
        }
        log.info("Selecting radio button at index: {}", index);
        allRadioButtons.get(index).click();
    }

    public int getSelectedRadioIndex() {
        List<WebElement> allRadioButtons = elements("radio.all");
        for (int i = 0; i < allRadioButtons.size(); i++) {
            if (isChecked(allRadioButtons.get(i))) return i;
        }
        return -1;
    }

    public int getRadioButtonCount() {
        return elements("radio.all").size();
    }

    private boolean isChecked(WebElement element) {
        return "true".equals(element.getAttribute("checked"));
    }
}
