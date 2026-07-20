package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.WaitUtils;
import org.openqa.selenium.By;

/**
 * Page object for ApiDemos' real "Views &gt; Date Widgets &gt; 1. Dialog" screen:
 * a {@code dateDisplay} label plus {@code pickDate}/{@code pickTime} buttons
 * that launch real system Date/Time picker dialogs. The date dialog defaults
 * to today's month, so selecting any other month requires paging via the
 * real {@code next}/{@code prev} buttons. The time dialog opens in radial
 * (tap-the-clock-face) mode by default — {@code toggle_mode} switches it to
 * real, typeable {@code input_hour}/{@code input_minute} EditText fields.
 *
 * <p>Locators live in {@code locators_android.properties} under the
 * {@code datePicker.*} keys — the system Date/Time picker dialog widgets used
 * here have no iOS equivalent screen in this framework.</p>
 */
public class DatePickerControlPage extends BasePage {

    private static final String[] MONTH_NAMES = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    // ── Actions ───────────────────────────────────────────────────────────────

    public void openDatePicker() {
        log.info("Opening date picker");
        click("datePicker.showDateButton");
    }

    public void openTimePicker() {
        log.info("Opening time picker");
        click("datePicker.showTimeButton");
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
        click("datePicker.okButton");
    }

    public void cancelDatePicker() {
        log.info("Cancelling date picker");
        click("datePicker.cancelButton");
    }

    public void setTime(int hour, int minute, boolean isAm) {
        log.info("Setting time: {}:{} {}", hour, minute, isAm ? "AM" : "PM");
        openTimePicker();
        click("datePicker.toggleMode"); // switch from the radial clock face to typeable fields
        sendKeys("datePicker.hourInput", String.valueOf(hour));
        sendKeys("datePicker.minuteInput", String.valueOf(minute));
        click("datePicker.amPmSpinner");
        click(By.xpath("//*[@text='" + (isAm ? "AM" : "PM") + "']"));
        confirmDatePicker();
    }

    public String getSelectedDate() {
        return getText("datePicker.dateResult");
    }

    public String getDatePickerHeaderText() {
        return getText("datePicker.header");
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
            click(diff > 0 ? "datePicker.nextButton" : "datePicker.prevButton");
            // The month grid cross-fades to the new page on click — wait for the
            // page-flip animation to settle before re-reading it, or it's stale.
            WaitUtils.hardWait(400);
        }
        log.warn("Could not reach target month {}/{} after 24 page attempts", targetYear, targetMonth);
    }

    private int[] getCurrentVisibleMonthYear() {
        String desc = element("datePicker.firstDayCell").getAttribute("content-desc"); // e.g. "01 January 2026"
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
