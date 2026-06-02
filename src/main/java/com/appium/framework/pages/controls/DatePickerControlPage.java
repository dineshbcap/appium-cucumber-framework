package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class DatePickerControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/date_button")
    @iOSXCUITFindBy(accessibility = "showDatePicker")
    private WebElement showDatePickerButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/time_button")
    @iOSXCUITFindBy(accessibility = "showTimePicker")
    private WebElement showTimePickerButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/date_result")
    @iOSXCUITFindBy(accessibility = "dateResult")
    private WebElement dateResult;

    // DatePicker dialog elements
    private static final By DATE_PICKER_NEXT    = By.id("android:id/next");
    private static final By DATE_PICKER_PREV    = By.id("android:id/prev");
    private static final By DATE_PICKER_OK      = By.id("android:id/button1");
    private static final By DATE_PICKER_CANCEL  = By.id("android:id/button2");
    private static final By DATE_PICKER_HEADER  = By.id("android:id/date_picker_header_date");
    private static final By TIME_PICKER_HOUR    = By.id("android:id/hours");
    private static final By TIME_PICKER_MINUTE  = By.id("android:id/minutes");
    private static final By TIME_PICKER_AM      = By.xpath("//*[@text='AM']");
    private static final By TIME_PICKER_PM      = By.xpath("//*[@text='PM']");

    // ── Actions ───────────────────────────────────────────────────────────────

    public void openDatePicker() {
        log.info("Opening date picker");
        showDatePickerButton.click();
    }

    public void openTimePicker() {
        log.info("Opening time picker");
        showTimePickerButton.click();
    }

    public void selectDate(int year, int month, int day) {
        log.info("Selecting date: {}/{}/{}", year, month, day);
        openDatePicker();
        // Navigate to year/month by clicking header and selecting
        // Tap the day cell
        By dayCell = By.xpath("//*[@content-desc='" + day + " " + getMonthName(month) + " " + year + "']");
        if (isDisplayed(dayCell)) {
            click(dayCell);
        }
        confirmDatePicker();
    }

    public void confirmDatePicker() {
        log.info("Confirming date picker selection");
        click(DATE_PICKER_OK);
    }

    public void cancelDatePicker() {
        log.info("Cancelling date picker");
        click(DATE_PICKER_CANCEL);
    }

    public void setTime(int hour, int minute, boolean isAm) {
        log.info("Setting time: {}:{} {}", hour, minute, isAm ? "AM" : "PM");
        openTimePicker();
        sendKeys(TIME_PICKER_HOUR, String.valueOf(hour));
        sendKeys(TIME_PICKER_MINUTE, String.valueOf(minute));
        click(isAm ? TIME_PICKER_AM : TIME_PICKER_PM);
        confirmDatePicker();
    }

    public String getSelectedDate() {
        return dateResult.getText();
    }

    public String getDatePickerHeaderText() {
        return getText(DATE_PICKER_HEADER);
    }

    private String getMonthName(int month) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                           "July", "August", "September", "October", "November", "December"};
        return months[month - 1];
    }
}
