package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "Views &gt; Seek Bar" screen (Android) and
 * UIKitCatalog's real "Sliders" screen (iOS). Android has a separate SeekBar
 * + "progress" label whose text looks like "77 from touch=true". iOS has a
 * single unnamed {@code UISlider} whose own {@code value} attribute already
 * reports its position as a percentage string (e.g. "42%") — no separate
 * label element exists there.
 */
public class SliderControlPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    // Resolved from locators_android.properties / locators_ios.properties via
    // BasePage#element. See "slider.*" keys.

    // ── Actions ───────────────────────────────────────────────────────────────

    public void setSliderValue(int percent) {
        log.info("Setting slider to {}%", percent);
        setSliderToPercent(percent);
    }

    public void slideToMin() {
        log.info("Sliding to minimum");
        setSliderToPercent(0);
    }

    public void slideToMax() {
        log.info("Sliding to maximum");
        setSliderToPercent(100);
    }

    public void slideTo50Percent() {
        setSliderToPercent(50);
    }

    /**
     * Returns the numeric progress value: parsed from Android's "NN from touch=..."
     * label, or from iOS's own slider {@code value} attribute (e.g. "42%").
     * Coordinate-based dragging lands exactly on 0/50/100 but can be off by a
     * couple of percent at other targets — callers should compare with tolerance.
     */
    public int getProgressValue() {
        if (isIOS()) {
            String value = element("slider.seekBar").getAttribute("value").trim(); // e.g. "42%"
            return Integer.parseInt(value.replace("%", ""));
        }
        String text = getText("slider.progressLabel").trim();
        return Integer.parseInt(text.split("\\s+")[0]);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setSliderToPercent(int percent) {
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;

        WebElement seekBar = element("slider.seekBar");
        Point location = seekBar.getLocation();
        Dimension size = seekBar.getSize();
        int y = location.getY() + size.height / 2;
        int targetX = location.getX() + (int) (size.width * percent / 100.0);

        int startX;
        if (isIOS()) {
            // UISlider only responds to a drag that starts ON the thumb — unlike
            // Android's SeekBar, tapping/dragging from the track center does
            // nothing if the thumb isn't already there. Start from the thumb's
            // actual current position, read from the slider's own value attribute.
            double currentPercent = Double.parseDouble(
                    seekBar.getAttribute("value").replace("%", "").trim()) / 100.0;
            startX = location.getX() + (int) (size.width * currentPercent);
        } else {
            startX = location.getX() + size.width / 2;
        }
        // A zero-distance drag (start == target) registers as a tap, not a
        // slide, and never fires the progress-changed listener.
        if (startX == targetX) {
            startX += 20;
        }

        GestureUtils.swipe(startX, y, targetX, y, 600);
    }
}
