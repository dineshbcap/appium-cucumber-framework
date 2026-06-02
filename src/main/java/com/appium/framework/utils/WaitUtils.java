package com.appium.framework.utils;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Explicit and implicit wait utilities for Appium test synchronization.
 *
 * <p><b>Concept covered: Synchronization Strategies</b><br>
 * Mobile apps are asynchronous — network calls, animations, and transitions take
 * variable time. Tests that don't wait properly will fail intermittently (flaky tests).
 * This class provides three levels of synchronization:</p>
 *
 * <ul>
 *   <li><b>Explicit wait (preferred)</b> — polls until a specific condition is met.
 *       Uses {@link WebDriverWait} + {@link ExpectedConditions}.
 *       Fails fast (throws) if the condition is not met within the timeout.</li>
 *   <li><b>Presence wait</b> — waits for an element to exist in the DOM even if hidden.
 *       Useful for elements that animate in or load lazily.</li>
 *   <li><b>Hard wait</b> — {@link Thread#sleep} for a fixed duration. Only use as a
 *       last resort when there is no condition to poll. Never use for test timing.</li>
 * </ul>
 *
 * <p><b>Appium-specific note:</b> Appium sessions have a {@code newCommandTimeout}
 * capability. If no command is sent for that duration, Appium ends the session.
 * Avoid using {@link #hardWait(long)} with large values that could trigger this timeout.</p>
 *
 * <p><b>Thread safety:</b> Each method reads the driver from {@link DriverManager}
 * (which uses {@link ThreadLocal}), making these methods safe for parallel execution.</p>
 */
public class WaitUtils {

    private static final Logger log = LogManager.getLogger(WaitUtils.class);
    private static final int DEFAULT_TIMEOUT = ConfigReader.getInt("explicit.wait", 15);

    private WaitUtils() {}

    // ── Visibility Waits ───────────────────────────────────────────────────────

    /**
     * Waits for the element to be present AND visible (rendered on screen).
     * Uses the default timeout from {@code explicit.wait} in config.properties.
     *
     * @param locator element locator strategy
     * @return the visible {@link WebElement}
     * @throws org.openqa.selenium.TimeoutException if element is not visible within timeout
     */
    public static WebElement waitForVisible(By locator) {
        return waitForVisible(locator, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for the element to be visible with a custom timeout.
     *
     * @param locator        element locator strategy
     * @param timeoutSeconds custom timeout in seconds
     * @return the visible {@link WebElement}
     */
    public static WebElement waitForVisible(By locator, int timeoutSeconds) {
        log.debug("Waiting for visibility ({}s): {}", timeoutSeconds, locator);
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ── Clickability Waits ─────────────────────────────────────────────────────

    /**
     * Waits for the element to be clickable (visible AND enabled).
     *
     * <p>Use this before click operations. "Clickable" is stricter than "visible" —
     * a disabled button is visible but not clickable.</p>
     *
     * @param locator element locator strategy
     * @return the clickable {@link WebElement}
     */
    public static WebElement waitForClickable(By locator) {
        return waitForClickable(locator, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for the element to be clickable with a custom timeout.
     *
     * @param locator        element locator strategy
     * @param timeoutSeconds custom timeout in seconds
     * @return the clickable {@link WebElement}
     */
    public static WebElement waitForClickable(By locator, int timeoutSeconds) {
        log.debug("Waiting for clickable ({}s): {}", timeoutSeconds, locator);
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    // ── Invisibility Waits ─────────────────────────────────────────────────────

    /**
     * Waits for the element to become invisible or be removed from the DOM.
     * Useful for waiting for loading spinners, progress bars, or dialogs to disappear.
     *
     * @param locator element locator strategy
     * @return {@code true} if invisible within timeout, {@code false} otherwise
     */
    public static boolean waitForInvisibility(By locator) {
        return waitForInvisibility(locator, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for the element to become invisible with a custom timeout.
     *
     * @param locator        element locator strategy
     * @param timeoutSeconds custom timeout in seconds
     * @return {@code true} if invisible within timeout
     */
    public static boolean waitForInvisibility(By locator, int timeoutSeconds) {
        log.debug("Waiting for invisibility ({}s): {}", timeoutSeconds, locator);
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ── Presence Waits ─────────────────────────────────────────────────────────

    /**
     * Waits for the element to be present in the DOM (may not be visible).
     *
     * <p>Use this for elements that are added to the DOM but may be hidden or off-screen.
     * For interactive elements prefer {@link #waitForVisible(By)}.</p>
     *
     * @param locator element locator strategy
     * @return the present (possibly hidden) {@link WebElement}
     */
    public static WebElement waitForPresence(By locator) {
        return waitForPresence(locator, DEFAULT_TIMEOUT);
    }

    /**
     * Waits for the element to be present with a custom timeout.
     *
     * @param locator        element locator strategy
     * @param timeoutSeconds custom timeout in seconds
     * @return the present {@link WebElement}
     */
    public static WebElement waitForPresence(By locator, int timeoutSeconds) {
        log.debug("Waiting for DOM presence ({}s): {}", timeoutSeconds, locator);
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ── Text / Content Waits ───────────────────────────────────────────────────

    /**
     * Waits until the element contains the expected text substring.
     *
     * <p>Useful for dynamic text that updates after an API call or animation completes
     * (e.g., a counter that increments, a status label that changes).</p>
     *
     * @param locator element locator strategy
     * @param text    expected text substring
     * @return {@code true} when the text is found within the element
     */
    public static boolean waitForText(By locator, String text) {
        log.debug("Waiting for text '{}' in: {}", text, locator);
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Waits for the element's text to match the expected value exactly.
     *
     * @param locator       element locator strategy
     * @param expectedText  exact text the element should contain
     * @param timeoutSeconds custom timeout in seconds
     * @return {@code true} when the text matches
     */
    public static boolean waitForExactText(By locator, String expectedText, int timeoutSeconds) {
        log.debug("Waiting for exact text '{}' in: {}", expectedText, locator);
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds))
                .until(driver -> {
                    try {
                        return expectedText.equals(driver.findElement(locator).getText());
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    // ── Hard Wait ──────────────────────────────────────────────────────────────

    /**
     * Pauses execution for a fixed duration using {@link Thread#sleep}.
     *
     * <p><b>Warning:</b> Hard waits are a test smell — they add fixed delays regardless
     * of actual app state. Use explicit waits wherever possible. Reserve hard waits for:
     * <ul>
     *   <li>Brief delays after gestures (e.g., 100ms between double-tap events)</li>
     *   <li>Platform-imposed timing gaps (e.g., iOS animation settling)</li>
     *   <li>Test setup steps with no detectable UI condition to poll</li>
     * </ul>
     * </p>
     *
     * @param millis duration to pause in milliseconds
     */
    public static void hardWait(long millis) {
        log.debug("Hard wait: {}ms", millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Restore interrupt status — don't swallow it silently
            Thread.currentThread().interrupt();
        }
    }
}
