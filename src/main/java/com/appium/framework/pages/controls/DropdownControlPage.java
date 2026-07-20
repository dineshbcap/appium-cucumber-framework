package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page object for ApiDemos' real "Views &gt; Spinner" screen: a "Color:" spinner
 * (spinner1, values red/orange/yellow/green/blue/violet) and a "Planet:" spinner
 * (spinner2, values Mercury..Pluto). Each spinner's currently-selected value is
 * rendered by a child {@code android:id/text1} TextView, not the spinner node itself.
 *
 * <p>Locators live in {@code locators_android.properties} / {@code locators_ios.properties}
 * under the {@code dropdown.*} keys.</p>
 */
public class DropdownControlPage extends BasePage {

    // ── Actions ───────────────────────────────────────────────────────────────

    public void openDropdown1() {
        log.info("Opening spinner/dropdown 1");
        click("dropdown.spinner1");
        element("dropdown.item"); // wait for the popup to render before returning
    }

    public void openDropdown2() {
        log.info("Opening spinner/dropdown 2");
        click("dropdown.spinner2");
        element("dropdown.item"); // wait for the popup to render before returning
    }

    public void closeDropdown() {
        log.info("Closing dropdown popup");
        navigateBack();
    }

    public void selectByText(String optionText) {
        log.info("Selecting option: {}", optionText);
        By option = By.xpath("//*[@text='" + optionText + "']");
        click(option);
    }

    public void selectDropdown1ByText(String text) {
        log.info("Selecting '{}' from dropdown 1", text);
        openDropdown1();
        selectByText(text);
    }

    public void selectDropdown2ByText(String text) {
        log.info("Selecting '{}' from dropdown 2", text);
        openDropdown2();
        selectByText(text);
    }

    public void selectDropdown1ByIndex(int index) {
        log.info("Selecting index {} from dropdown 1", index);
        openDropdown1();
        List<WebElement> items = elements("dropdown.item");
        if (index >= 0 && index < items.size()) {
            items.get(index).click();
        } else {
            throw new IndexOutOfBoundsException("Dropdown index out of range: " + index);
        }
    }

    public String getSelectedDropdown1Value() {
        return element("dropdown.spinner1").findElement(By.className("android.widget.TextView")).getText();
    }

    public String getSelectedDropdown2Value() {
        return element("dropdown.spinner2").findElement(By.className("android.widget.TextView")).getText();
    }

    public List<String> getAllDropdown1Options() {
        openDropdown1();
        List<WebElement> items = elements("dropdown.item");
        List<String> options = items.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        navigateBack();
        return options;
    }
}
