package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.WaitUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "Views &gt; Date Widgets &gt; 1. Dialog" screen:
 * a {@code dateDisplay} label plus {@code pickDate}/{@code pickTime} buttons
 * that launch real system Date/Time picker dialogs. The date dialog defaults
 * to today's month, so selecting any other month requires paging via the
 * real {@code next}/{@code prev} buttons. The time dialog opens in radial
 * (tap-the-clock-face) mode by default — {@code toggle_mode} switches it to
 * real, typeable {@code input_hour}/{@code input_minute} EditText fields.
 */
public class DatePickerControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/pickDate")
    @iOSXCUITFindBy(accessibility = "showDatePicker")
    private WebElement showDatePickerButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/pickTime")
    @iOSXCUITFindBy(accessibility = "showTimePicker")
    private WebElement showTimePickerButton;

    @AndroidFindBy(id = "io.appium.android.apis:id/dateDisplay")
    @iOSXCUITFindBy(accessibility = "dateResult")
    private WebElement dateResult;

    // DatePicker dialog elements
    private static final By DATE_PICKER_NEXT   = By.id("android:id/next");
    private static final By DATE_PICKER_PREV   = By.id("android:id/prev");
    private static final By DATE_PICKER_OK     = By.id("android:id/button1");
    private static final By DATE_PICKER_CANCEL = By.id("android:id/button2");
    private static final By DATE_PICKER_HEADER = By.id("android:id/date_picker_header_date");
    // The header only reflects the *selected* date, not the page currently
    // scrolled into view — every visible month always has a "day 1" cell,
    // so its content-desc ("01 January 2026") is what actually identifies
    // which month/year is on screen.
    private static final By FIRST_DAY_CELL = By.xpath("//*[starts-with(@content-desc, '01 ')]");

    // TimePicker dialog elements (keyboard-input mode)
    private static final By TOGGLE_MODE       = By.id("android:id/toggle_mode");
    private static final By TIME_INPUT_HOUR   = By.id("android:id/input_hour");
    private static final By TIME_INPUT_MINUTE = By.id("android:id/input_minute");
    private static final By AM_PM_SPINNER     = By.id("android:id/am_pm_spinner");

    private static final String[] MONTH_NAMES = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

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
        navigateToMonth(year, month);
        By dayCell = By.xpath("//*[@content-desc='"
                + String.format("%02d", day) + " " + getMonthName(month) + " " + year + "']");
        click(dayCell);
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
        click(TOGGLE_MODE); // switch from the radial clock face to typeable fields
        sendKeys(TIME_INPUT_HOUR, String.valueOf(hour));
        sendKeys(TIME_INPUT_MINUTE, String.valueOf(minute));
        click(AM_PM_SPINNER);
        click(By.xpath("//*[@text='" + (isAm ? "AM" : "PM") + "']"));
        confirmDatePicker();
    }

    public String getSelectedDate() {
        return dateResult.getText();
    }

    public String getDatePickerHeaderText() {
        return getText(DATE_PICKER_HEADER);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Pages the date picker's month view via next/prev until it shows the
     * target year and month, reading the current position from the visible
     * "day 1" cell each step rather than assuming a fixed starting month.
     */
    private void navigateToMonth(int targetYear, int targetMonth) {
        for (int i = 0; i < 24; i++) {
            int[] current = getCurrentVisibleMonthYear();
            int diff = (targetYear - current[0]) * 12 + (targetMonth - current[1]);
            if (diff == 0) return;
            click(diff > 0 ? DATE_PICKER_NEXT : DATE_PICKER_PREV);
            // The month grid cross-fades to the new page on click — wait for the
            // page-flip animation to settle before re-reading it, or it's stale.
            WaitUtils.hardWait(400);
        }
        log.warn("Could not reach target month {}/{} after 24 page attempts", targetYear, targetMonth);
    }

    private int[] getCurrentVisibleMonthYear() {
        String desc = findElement(FIRST_DAY_CELL).getAttribute("content-desc"); // e.g. "01 January 2026"
        String[] parts = desc.trim().split("\\s+");
        int year = Integer.parseInt(parts[2]);
        int month = 1;
        for (int i = 0; i < MONTH_NAMES.length; i++) {
            if (MONTH_NAMES[i].equalsIgnoreCase(parts[1])) {
                month = i + 1;
                break;
            }
        }
        return new int[]{year, month};
    }

    private String getMonthName(int month) {
        return MONTH_NAMES[month - 1];
    }
}
