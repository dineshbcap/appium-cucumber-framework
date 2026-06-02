package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class ListControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/list")
    @iOSXCUITFindBy(accessibility = "mainList")
    private WebElement listView;

    @AndroidFindBy(className = "android.widget.ListView")
    @iOSXCUITFindBy(className = "XCUIElementTypeTable")
    private WebElement scrollableList;

    @AndroidFindBy(xpath = "//android.widget.ListView//android.widget.TextView")
    @iOSXCUITFindBy(className = "XCUIElementTypeCell")
    private List<WebElement> listItems;

    @AndroidFindBy(id = "io.appium.android.apis:id/list_result")
    @iOSXCUITFindBy(accessibility = "listResult")
    private WebElement resultLabel;

    // ── Actions ───────────────────────────────────────────────────────────────

    public void tapItemAtIndex(int index) {
        log.info("Tapping list item at index: {}", index);
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
        click(uiScrollable);
    }

    public String getItemTextAtIndex(int index) {
        return listItems.get(index).getText();
    }

    public List<String> getAllItemTexts() {
        return listItems.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public int getItemCount() {
        return listItems.size();
    }

    public String getResultText() {
        return resultLabel.getText();
    }

    public boolean isItemDisplayed(String text) {
        return isDisplayed(By.xpath("//*[@text='" + text + "']"));
    }
}
