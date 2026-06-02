package com.appium.framework.pages.controls;

import com.appium.framework.pages.BasePage;
import com.appium.framework.utils.DeviceUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object demonstrating <b>Device Orientation</b> testing.
 *
 * <p><b>Concept covered:</b> Many apps have orientation-dependent layouts (responsive UI).
 * Appium can programmatically rotate the device and verify that the app adjusts correctly
 * without requiring physical device tilting.</p>
 *
 * <p><b>Appium mechanism:</b> Both AndroidDriver and IOSDriver support orientation control
 * via the {@code mobile:setOrientation} and {@code mobile:getOrientation} execute script
 * commands, which work consistently across UiAutomator2 and XCUITest drivers.</p>
 *
 * <p><b>Android Note:</b> The app's Activity must not lock its orientation in the manifest
 * ({@code android:screenOrientation="portrait"}) for this to have effect. If locked,
 * the orientation API call silently fails.</p>
 *
 * <p><b>iOS Note:</b> Works on both Simulator and real device. Real device rotation
 * reflects the physical tilt; on Simulator it's purely programmatic.</p>
 */
public class OrientationPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    /** Orientation indicator label that shows current orientation in the app. */
    @AndroidFindBy(id = "io.appium.android.apis:id/orientation_label")
    @iOSXCUITFindBy(accessibility = "orientationLabel")
    private WebElement orientationLabel;

    /** Content that appears only in landscape mode (e.g., wider charts, side panels). */
    @AndroidFindBy(id = "io.appium.android.apis:id/landscape_content")
    @iOSXCUITFindBy(accessibility = "landscapeContent")
    private WebElement landscapeContent;

    /** Content that appears only in portrait mode (e.g., tall lists, cards). */
    @AndroidFindBy(id = "io.appium.android.apis:id/portrait_content")
    @iOSXCUITFindBy(accessibility = "portraitContent")
    private WebElement portraitContent;

    private static final By LANDSCAPE_CONTENT_INDICATOR =
            By.xpath("//*[@resource-id='io.appium.android.apis:id/landscape_content'" +
                     " or @name='landscapeContent']");
    private static final By PORTRAIT_CONTENT_INDICATOR  =
            By.xpath("//*[@resource-id='io.appium.android.apis:id/portrait_content'" +
                     " or @name='portraitContent']");

    // ── Orientation Actions ────────────────────────────────────────────────────

    /**
     * Rotates the device to landscape orientation.
     * The app should re-layout its UI to fit the wider screen.
     */
    public void rotateToLandscape() {
        log.info("Rotating to LANDSCAPE");
        DeviceUtils.rotateToLandscape();
    }

    /**
     * Rotates the device to portrait orientation (default upright position).
     */
    public void rotateToPortrait() {
        log.info("Rotating to PORTRAIT");
        DeviceUtils.rotateToPortrait();
    }

    /**
     * Toggles between portrait and landscape. If currently portrait goes landscape,
     * if currently landscape goes portrait.
     */
    public void toggleOrientation() {
        if (DeviceUtils.isPortrait()) {
            rotateToLandscape();
        } else {
            rotateToPortrait();
        }
    }

    // ── State Queries ─────────────────────────────────────────────────────────

    /**
     * Returns the current device orientation as a string ("PORTRAIT" or "LANDSCAPE").
     *
     * @return current orientation string
     */
    public String getCurrentOrientationName() {
        return DeviceUtils.getOrientation();
    }

    /**
     * Returns {@code true} if the device is currently in portrait mode.
     *
     * @return {@code true} for portrait
     */
    public boolean isPortrait() {
        return DeviceUtils.isPortrait();
    }

    /**
     * Returns {@code true} if the device is currently in landscape mode.
     *
     * @return {@code true} for landscape
     */
    public boolean isLandscape() {
        return DeviceUtils.isLandscape();
    }

    // ── Content Visibility ────────────────────────────────────────────────────

    /**
     * Returns {@code true} if landscape-specific content is currently visible.
     *
     * @return {@code true} if landscape content indicator is displayed
     */
    public boolean isLandscapeContentVisible() {
        return isDisplayed(LANDSCAPE_CONTENT_INDICATOR);
    }

    /**
     * Returns {@code true} if portrait-specific content is currently visible.
     *
     * @return {@code true} if portrait content indicator is displayed
     */
    public boolean isPortraitContentVisible() {
        return isDisplayed(PORTRAIT_CONTENT_INDICATOR);
    }

    /**
     * Returns the screen width in the current orientation.
     * Width should be larger than height in landscape, smaller in portrait.
     *
     * @return screen width in pixels
     */
    public int getCurrentScreenWidth() {
        return DeviceUtils.getScreenWidth();
    }

    /**
     * Returns the screen height in the current orientation.
     *
     * @return screen height in pixels
     */
    public int getCurrentScreenHeight() {
        return DeviceUtils.getScreenHeight();
    }
}
