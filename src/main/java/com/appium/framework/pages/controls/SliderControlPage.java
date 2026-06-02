package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.GestureUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public class SliderControlPage extends BasePage {

    @AndroidFindBy(id = "io.appium.android.apis:id/seekBar1")
    @iOSXCUITFindBy(accessibility = "slider1")
    private WebElement seekBar;

    @AndroidFindBy(id = "io.appium.android.apis:id/seekBar2")
    @iOSXCUITFindBy(accessibility = "slider2")
    private WebElement seekBar2;

    @AndroidFindBy(id = "io.appium.android.apis:id/slider_value")
    @iOSXCUITFindBy(accessibility = "sliderValue")
    private WebElement valueLabel;

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Sets slider to a percentage value (0-100).
     */
    public void setSliderValue(int percent) {
        log.info("Setting slider to {}%", percent);
        setSliderToPercent(seekBar, percent);
    }

    public void setSlider2Value(int percent) {
        log.info("Setting slider2 to {}%", percent);
        setSliderToPercent(seekBar2, percent);
    }

    public void slideToMin() {
        log.info("Sliding to minimum");
        setSliderToPercent(seekBar, 0);
    }

    public void slideToMax() {
        log.info("Sliding to maximum");
        setSliderToPercent(seekBar, 100);
    }

    public void slideTo50Percent() {
        setSliderToPercent(seekBar, 50);
    }

    public String getSliderValue() {
        return seekBar.getAttribute("value");
    }

    public String getValueLabelText() {
        return valueLabel.getText();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setSliderToPercent(WebElement slider, int percent) {
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;

        Point location = slider.getLocation();
        Dimension size = slider.getSize();

        int startX = location.getX();
        int endX = location.getX() + size.width;
        int targetX = startX + (int) ((endX - startX) * percent / 100.0);
        int y = location.getY() + size.height / 2;

        GestureUtils.swipe(startX + size.width / 2, y, targetX, y, 600);
    }
}
