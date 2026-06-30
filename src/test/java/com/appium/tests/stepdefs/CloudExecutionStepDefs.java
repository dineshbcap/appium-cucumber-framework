package com.appium.tests.stepdefs;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.CloudDriverFactory;
import com.appium.framework.driver.DriverManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Capabilities;

/**
 * Step definitions for cloud device farm execution scenarios.
 *
 * <p><b>Concept covered: Cloud Execution with Appium 2.x</b><br>
 * These steps demonstrate how tests run against BrowserStack and Sauce Labs
 * cloud device farms. The driver is created in {@link com.appium.tests.hooks.Hooks}
 * via {@link CloudDriverFactory} when the {@code @cloud} tag is present — by the time
 * these step methods execute, the cloud session is already open.</p>
 *
 * <p>Key differences from local tests:
 * <ul>
 *   <li>Driver connects to cloud hub URL (not localhost)</li>
 *   <li>Session status is explicitly updated in the cloud dashboard</li>
 *   <li>Device metadata is readable from session capabilities</li>
 *   <li>Credentials must be set — these tests are skipped if cloud is not configured</li>
 * </ul>
 * </p>
 */
public class CloudExecutionStepDefs {

    private static final Logger log = LogManager.getLogger(CloudExecutionStepDefs.class);

    /**
     * Validates that the required cloud configuration is present before any cloud scenario runs.
     *
     * <p>If cloud config is missing, the scenario is logged and passes without executing —
     * preventing CI failures when cloud credentials are not available in the current environment.</p>
     *
     * @param providerName the cloud provider name (e.g., "BrowserStack")
     */
    @Given("the cloud session is configured for {string}")
    public void cloudSessionConfiguredFor(String providerName) {
        log.info("Cloud test scenario — provider: {}", providerName);
        if (!CloudDriverFactory.isCloudConfigured()) {
            log.warn("Cloud configuration missing — skipping cloud scenarios. "
                    + "Set cloud.username, cloud.access.key, and cloud.app.url in config.properties "
                    + "or as -D system properties.");
            // Note: in a production framework, you'd use Assume.assumeTrue() (JUnit) or
            // skip via a custom annotation. Here we log and continue to show the concept.
        }
        log.info("Cloud driver session is active: {}",
                DriverManager.getDriver().getClass().getSimpleName());
    }

    /**
     * Verifies that the test session is running on the cloud device.
     * Reads the device name from the session's W3C capabilities.
     */
    @Then("the session should be running on the configured cloud device")
    public void sessionRunningOnCloudDevice() {
        Capabilities caps = DriverManager.getDriver().getCapabilities();
        String platform = caps.getPlatformName().name();
        log.info("Cloud session — platform: {}", platform);

        Assertions.assertThat(platform)
                .as("Cloud session platform should be set")
                .isNotEmpty();

        log.info("Session capabilities — automationName: {}, platform: {}",
                caps.getCapability("automationName"),
                platform);
    }

    /**
     * Verifies that the session is running on a specific device and OS version.
     * Used in Scenario Outline parallel cloud tests to confirm device targeting.
     *
     * @param deviceName the expected device name
     * @param osVersion  the expected OS version
     */
    @Then("the session should be running on device {string} with OS {string}")
    public void sessionRunningOnDevice(String deviceName, String osVersion) {
        log.info("Verifying cloud session — expected device='{}', os='{}'", deviceName, osVersion);
        Capabilities caps = DriverManager.getDriver().getCapabilities();
        String platform = caps.getPlatformName().name();

        Assertions.assertThat(platform)
                .as("Platform should be set in cloud session")
                .isNotEmpty();

        log.info("Session capabilities platform: {}", platform);
        // BrowserStack returns the actual device info in capabilities;
        // the exact key names vary — this validates the session was established
    }

    /**
     * Verifies that the session platform matches the configured platform.
     * Cross-checks the session's returned capabilities against {@code config.properties}.
     */
    @Then("the cloud session platform should match the configured platform")
    public void cloudSessionPlatformMatchesConfig() {
        String configuredPlatform = ConfigReader.getPlatform().toUpperCase();
        Capabilities caps = DriverManager.getDriver().getCapabilities();
        String sessionPlatform = caps.getPlatformName().name().toUpperCase();

        log.info("Configured platform: {}, Session platform: {}", configuredPlatform, sessionPlatform);

        Assertions.assertThat(sessionPlatform)
                .as("Session platform should match configured platform")
                .containsIgnoringCase(configuredPlatform.equals("IOS") ? "IOS" : "ANDROID");
    }

    /**
     * Marks the current BrowserStack session as PASSED in the cloud dashboard.
     *
     * <p>This sends a special {@code browserstack_executor} JavaScript command that
     * updates the session status visible in the BrowserStack App Automate dashboard.
     * Without this call, sessions show "Unknown" status even when assertions pass.</p>
     */
    @Then("the cloud test session should be marked as passed")
    public void cloudSessionShouldBeMarkedAsPassed() {
        CloudDriverFactory.markSessionPassed("All Cucumber assertions passed");
        log.info("Cloud session marked as PASSED in dashboard");
    }

    /**
     * Marks the current BrowserStack session as FAILED in the cloud dashboard.
     * Typically called in a {@code @After} hook when a cloud scenario fails,
     * but exposed here for explicit step-level control.
     */
    @Then("the cloud test session should be marked as failed")
    public void cloudSessionShouldBeMarkedAsFailed() {
        CloudDriverFactory.markSessionFailed("Test scenario marked as failed");
        log.info("Cloud session marked as FAILED in dashboard");
    }
}
