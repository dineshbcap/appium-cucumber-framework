package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page object for ApiDemos' real "Views &gt; Spinner" screen: a "Color:" spinner
 * (spinner1, values red/orange/yellow/green/blue/violet) and a "Planet:" spinner
 * (spinner2, values Mercury..Pluto). Each spinner's currently-selected value is
 * rendered by a child {@code android:id/text1} TextView, not the spinner node itself.
 */
public class DropdownControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/spinner1")
    @iOSXCUITFindBy(accessibility = "spinner1")
    private WebElement spinner1;

    @AndroidFindBy(id = "io.appium.android.apis:id/spinner2")
    @iOSXCUITFindBy(accessibility = "spinner2")
    private WebElement spinner2;

    private static final By DROPDOWN_ITEM =
            AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.CheckedTextView\")");

    // ── Actions ───────────────────────────────────────────────────────────────

    public void openDropdown1() {
        log.info("Opening spinner/dropdown 1");
        spinner1.click();
        findElement(DROPDOWN_ITEM); // wait for the popup to render before returning
    }

    public void openDropdown2() {
        log.info("Opening spinner/dropdown 2");
        spinner2.click();
        findElement(DROPDOWN_ITEM); // wait for the popup to render before returning
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
        List<WebElement> items = findElements(DROPDOWN_ITEM);
        if (index >= 0 && index < items.size()) {
            items.get(index).click();
        } else {
            throw new IndexOutOfBoundsException("Dropdown index out of range: " + index);
        }
    }

    public String getSelectedDropdown1Value() {
        return spinner1.findElement(By.className("android.widget.TextView")).getText();
    }

    public String getSelectedDropdown2Value() {
        return spinner2.findElement(By.className("android.widget.TextView")).getText();
    }

    public List<String> getAllDropdown1Options() {
        openDropdown1();
        List<WebElement> items = findElements(DROPDOWN_ITEM);
        List<String> options = items.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        navigateBack();
        return options;
    }
}
