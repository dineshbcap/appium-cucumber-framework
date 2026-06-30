package com.appium.tests.hooks;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.CloudDriverFactory;
import com.appium.framework.driver.DriverFactory;
import com.appium.framework.driver.DriverManager;
import com.appium.framework.utils.RecordingUtils;
import com.appium.framework.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cucumber lifecycle hooks that manage the Appium driver session and test artifacts.
 *
 * <p><b>Hook order:</b> Cucumber evaluates the {@code order} parameter ascending for
 * {@code @Before} (0 runs first) and descending for {@code @After} (0 runs last).
 * This ensures driver setup happens before any test actions and teardown happens last.</p>
 *
 * <p><b>Concepts covered by these hooks:</b>
 * <ul>
 *   <li><b>Driver lifecycle</b> — creating a fresh Appium driver before each scenario
 *       and quitting it after, ensuring test isolation</li>
 *   <li><b>Screenshot on failure</b> — automatically capturing and embedding a PNG
 *       screenshot in the Cucumber HTML report when a scenario fails</li>
 *   <li><b>Screen recording</b> — optionally recording the entire scenario as an MP4
 *       video (enabled by {@code recording.enabled=true} in config.properties)</li>
 *   <li><b>Step-level screenshots</b> — embedding a screenshot after each failing step
 *       for granular failure diagnosis</li>
 *   <li><b>Thread safety</b> — {@link DriverManager} uses {@link ThreadLocal} so each
 *       parallel thread gets its own driver instance</li>
 * </ul>
 * </p>
 */
public class Hooks {

    private static final Logger log = LogManager.getLogger(Hooks.class);

    /**
     * Runs before every scenario. Creates the Appium driver and optionally starts recording.
     *
     * <p>The driver is stored in a {@link ThreadLocal} inside {@link DriverManager},
     * so parallel scenarios running on different threads each get their own driver.</p>
     *
     * @param scenario the current Cucumber scenario (provides name, tags, status)
     */
    @Before(order = 0)
    public void setUp(Scenario scenario) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("START: {} [Thread: {}]", scenario.getName(), Thread.currentThread().getName());
        log.info("Tags: {}", scenario.getSourceTagNames());
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Cloud scenarios use CloudDriverFactory; all others use the local DriverFactory.
        // The @cloud tag triggers connection to BrowserStack/Sauce Labs instead of localhost.
        if (scenario.getSourceTagNames().contains("@cloud")) {
            log.info("@cloud scenario detected — creating cloud driver");
            CloudDriverFactory.createCloudDriver();
        } else {
            DriverFactory.createDriver();
        }

        // Start screen recording if enabled in config
        if (ConfigReader.getBoolean("recording.enabled", false)) {
            try {
                RecordingUtils.startRecording();
                log.info("Screen recording started for scenario: {}", scenario.getName());
            } catch (Exception e) {
                log.warn("Could not start screen recording: {}", e.getMessage());
            }
        }
    }

    /**
     * Runs after every scenario. Handles failure artifacts and quits the driver.
     *
     * <p>The {@code try/finally} block guarantees driver teardown even if screenshot
     * or recording operations throw exceptions — preventing driver leaks that would
     * leave orphan Appium sessions running.</p>
     *
     * @param scenario the completed scenario (contains pass/fail status)
     */
    @After(order = 0)
    public void tearDown(Scenario scenario) {
        try {
            boolean failed = scenario.isFailed();

            // ── Screenshot on failure ──────────────────────────────────────────
            if (failed && ConfigReader.getBoolean("screenshot.on.failure", true)) {
                log.warn("Scenario FAILED: '{}' — capturing screenshot", scenario.getName());
                try {
                    byte[] screenshot = ScreenshotUtils.captureAsBytes();
                    if (screenshot != null) {
                        // Embed screenshot directly in the Cucumber HTML report
                        scenario.attach(screenshot, "image/png", "Failure Screenshot");
                    }
                } catch (Exception e) {
                    log.error("Failed to capture screenshot: {}", e.getMessage());
                }
            }

            // ── Screen recording ───────────────────────────────────────────────
            if (ConfigReader.getBoolean("recording.enabled", false) && RecordingUtils.isRecording()) {
                try {
                    if (failed) {
                        // Save video to disk when scenario fails — skipped on pass to save space
                        String sanitized = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_");
                        String videoPath = RecordingUtils.stopAndSaveRecording(sanitized);
                        if (videoPath != null) {
                            log.info("Failure recording saved: {}", videoPath);
                        }
                    } else {
                        // Stop recording without saving on passing scenarios
                        RecordingUtils.stopRecording();
                    }
                } catch (Exception e) {
                    log.warn("Error handling screen recording in tearDown: {}", e.getMessage());
                    RecordingUtils.safeStopRecording();
                }
            }

        } finally {
            // Always quit the driver — even if artifacts fail
            log.info("END: '{}' — Status: {}", scenario.getName(), scenario.getStatus());
            DriverManager.removeDriver();
        }
    }

    /**
     * Runs after every step within a scenario.
     *
     * <p>When a step fails, a screenshot is embedded into the report at the step level.
     * This provides more context than a single end-of-scenario screenshot, since you can
     * see exactly what the screen looked like at the moment of each failure.</p>
     *
     * <p>The try/catch is intentionally broad to prevent the reporting logic from masking
     * the actual test failure with a secondary exception.</p>
     *
     * @param scenario the current scenario (checked for failure state)
     */
    @AfterStep
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {
            try {
                byte[] screenshot = ScreenshotUtils.captureAsBytes();
                if (screenshot != null) {
                    scenario.attach(screenshot, "image/png", "Step Failure Screenshot");
                }
            } catch (Exception ignored) {
                // Swallow exceptions here — the step failure is already recorded;
                // we don't want a secondary exception to confuse the report
            }
        }
    }
}
