package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.OrientationPage;
import com.appium.framework.utils.DeviceUtils;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;

/**
 * Step definitions for Device Orientation feature.
 *
 * <p>Covers: rotating to portrait/landscape, toggling orientation,
 * verifying screen dimensions change accordingly.</p>
 *
 * <p><b>Concept demonstrated:</b> Using {@code mobile:setOrientation} and
 * {@code mobile:getOrientation} execute script commands through the
 * {@link OrientationPage} to test responsive UI layouts without physical device tilt.</p>
 */
public class OrientationStepDefs {

    private static final Logger log = LogManager.getLogger(OrientationStepDefs.class);
    private final OrientationPage page = new OrientationPage();

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("the device is in portrait orientation")
    public void deviceIsInPortraitOrientation() {
        log.info("Setting device to portrait orientation");
        page.rotateToPortrait();
        Assertions.assertThat(page.isPortrait())
                .as("Device should be in portrait")
                .isTrue();
    }

    @Given("the device is in landscape orientation")
    public void deviceIsInLandscapeOrientation() {
        log.info("Setting device to landscape orientation");
        page.rotateToLandscape();
        Assertions.assertThat(page.isLandscape())
                .as("Device should be in landscape")
                .isTrue();
    }

    // ── When ──────────────────────────────────────────────────────────────────

    @When("the device is rotated to landscape")
    public void rotateToLandscape() {
        log.info("Rotating to landscape");
        page.rotateToLandscape();
    }

    @When("the device is rotated to portrait")
    public void rotateToPortrait() {
        log.info("Rotating to portrait");
        page.rotateToPortrait();
    }

    @When("the orientation is toggled")
    public void toggleOrientation() {
        log.info("Toggling orientation");
        page.toggleOrientation();
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the device should be in landscape orientation")
    public void deviceShouldBeInLandscape() {
        String orientation = page.getCurrentOrientationName();
        log.info("Current orientation: {}", orientation);
        Assertions.assertThat(orientation)
                .as("Device orientation should be LANDSCAPE")
                .isEqualToIgnoringCase(DeviceUtils.LANDSCAPE);
    }

    @Then("the device should be in portrait orientation")
    public void deviceShouldBeInPortrait() {
        String orientation = page.getCurrentOrientationName();
        log.info("Current orientation: {}", orientation);
        Assertions.assertThat(orientation)
                .as("Device orientation should be PORTRAIT")
                .isEqualToIgnoringCase(DeviceUtils.PORTRAIT);
    }

    @Then("the screen width should be greater than the screen height")
    public void screenWidthGreaterThanHeight() {
        int width = page.getCurrentScreenWidth();
        int height = page.getCurrentScreenHeight();
        log.info("Screen dimensions: {}x{}", width, height);
        Assertions.assertThat(width)
                .as("Screen width (%d) should be greater than height (%d) in landscape", width, height)
                .isGreaterThan(height);
    }

    @Then("the screen height should be greater than the screen width")
    public void screenHeightGreaterThanWidth() {
        int width = page.getCurrentScreenWidth();
        int height = page.getCurrentScreenHeight();
        log.info("Screen dimensions: {}x{}", width, height);
        Assertions.assertThat(height)
                .as("Screen height (%d) should be greater than width (%d) in portrait", height, width)
                .isGreaterThan(width);
    }
}
