package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * Page object for ApiDemos' real "Views &gt; Seek Bar" screen: a single SeekBar
 * ({@code seek}) with a "progress" label whose text looks like
 * "77 from touch=true" — the numeric progress is the leading token, there is
 * no separate numeric-only attribute or second seek bar on this screen.
 */
public class SliderControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/seek")
    @iOSXCUITFindBy(accessibility = "slider1")
    private WebElement seekBar;

    @AndroidFindBy(id = "io.appium.android.apis:id/progress")
    @iOSXCUITFindBy(accessibility = "sliderValue")
    private WebElement progressLabel;

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
     * Returns the numeric progress value parsed from the "NN from touch=..." label.
     * Coordinate-based dragging lands exactly on 0/50/100 but can be off by a
     * couple of percent at other targets — callers should compare with tolerance.
     */
    public int getProgressValue() {
        String text = progressLabel.getText().trim();
        return Integer.parseInt(text.split("\\s+")[0]);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setSliderToPercent(int percent) {
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;

        Point location = seekBar.getLocation();
        Dimension size = seekBar.getSize();

        int startX = location.getX() + size.width / 2;
        int targetX = location.getX() + (int) (size.width * percent / 100.0);
        // A zero-distance drag (target == center, i.e. percent == 50) registers as a tap,
        // not a slide, and never fires the SeekBar's progress-changed listener.
        if (startX == targetX) {
            startX += 20;
        }
        int y = location.getY() + size.height / 2;

        GestureUtils.swipe(startX, y, targetX, y, 600);
    }
}
