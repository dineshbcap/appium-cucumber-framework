package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Locators live in {@code locators_android.properties} / {@code locators_ios.properties}
 * under the {@code list.*} keys.
 */
public class ListControlPage extends BasePage {

    // ── Actions ───────────────────────────────────────────────────────────────

    public void tapItemAtIndex(int index) {
        log.info("Tapping list item at index: {}", index);
        List<WebElement> listItems = elements("list.items");
        if (index < 0 || index >= listItems.size()) {
            throw new IndexOutOfBoundsException("List index out of range: " + index);
        }
        listItems.get(index).click();
    }

    public void tapItemByText(String text) {
        log.info("Tapping list item: {}", text);
        By item = By.xpath("//*[@text='" + text + "']");
        click(item);
    }

    public void scrollToItemByText(String text) {
        log.info("Scrolling to list item: {}", text);
        By uiScrollable = AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().text(\"" + text + "\"))");
        findElement(uiScrollable);
    }

    public String getItemTextAtIndex(int index) {
        return elements("list.items").get(index).getText();
    }

    public List<String> getAllItemTexts() {
        return elements("list.items").stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public int getItemCount() {
        return elements("list.items").size();
    }

    public boolean isItemDisplayed(String text) {
        return isDisplayed(By.xpath("//*[@text='" + text + "']"));
    }
}
