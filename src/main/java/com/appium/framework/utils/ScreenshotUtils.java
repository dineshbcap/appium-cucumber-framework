package com.appium.framework.utils;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Screenshot capture utilities for embedding test evidence in reports.
 *
 * <p><b>Concept covered:</b> Appium drivers implement the Selenium {@link TakesScreenshot}
 * interface, which captures a PNG image of the current device screen. Screenshots
 * are the primary debugging artifact when a test fails — they show exactly what was
 * on screen at the moment of failure.</p>
 *
 * <p><b>Two capture modes:</b>
 * <ul>
 *   <li>{@link #capture(String)} — saves to a file on disk (named with timestamp and thread name)</li>
 *   <li>{@link #captureAsBytes()} — returns raw bytes for embedding in Cucumber HTML reports</li>
 * </ul>
 * </p>
 *
 * <p><b>Cucumber integration:</b> Call {@link #captureAsBytes()} in {@code @After} or
 * {@code @AfterStep} hooks and attach with {@code scenario.attach(bytes, "image/png", "label")}.
 * The Cucumber HTML report will display the screenshot inline next to the failing step.</p>
 *
 * <p><b>Thread safety:</b> File names include the thread name to prevent parallel test
 * threads from overwriting each other's screenshots.</p>
 *
 * <p><b>OutputType choices:</b>
 * <ul>
 *   <li>{@link OutputType#BYTES} — raw PNG bytes (best for embedding in reports)</li>
 *   <li>{@link OutputType#FILE} — temporary file (requires copy to preserve after session)</li>
 *   <li>{@link OutputType#BASE64} — Base64 string (useful for JavaScript embedding)</li>
 * </ul>
 * </p>
 */
public class ScreenshotUtils {

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {}

    /**
     * Captures a screenshot and saves it to the configured directory on disk.
     *
     * <p>The file name format is: {@code <testName>_<threadName>_<timestamp>.png}.
     * This ensures unique names even when multiple threads capture at the same time.</p>
     *
     * @param testName prefix for the screenshot file name (e.g., scenario name)
     * @return absolute path to the saved screenshot file, or {@code null} if capture failed
     */
    public static String capture(String testName) {
        String screenshotDir = ConfigReader.get("screenshot.dir", "target/screenshots");
        String timestamp = LocalDateTime.now().format(FORMATTER);
        // Thread name sanitized to remove characters illegal in file names
        String threadName = Thread.currentThread().getName().replaceAll("[^a-zA-Z0-9]", "_");
        String fileName = testName + "_" + threadName + "_" + timestamp + ".png";
        Path destPath = Paths.get(screenshotDir, fileName);

        try {
            Files.createDirectories(destPath.getParent());
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            Files.write(destPath, screenshot);
            log.info("Screenshot saved: {}", destPath.toAbsolutePath());
            return destPath.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to save screenshot to '{}': {}", destPath, e.getMessage());
            return null;
        }
    }

    /**
     * Captures a screenshot and returns the raw PNG bytes.
     *
     * <p>This is the preferred method for Cucumber report attachment because the bytes
     * can be passed directly to {@code scenario.attach(bytes, "image/png", "label")}
     * without creating a temporary file.</p>
     *
     * @return PNG image bytes of the current screen state, or {@code null} if capture failed
     */
    public static byte[] captureAsBytes() {
        try {
            return ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error("Failed to capture screenshot as bytes: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Captures a screenshot as a Base64-encoded string.
     *
     * <p>Useful when screenshots need to be embedded in JSON responses, sent to
     * external reporting APIs, or stored in databases as text.</p>
     *
     * @return Base64-encoded PNG string, or {@code null} if capture failed
     */
    public static String captureAsBase64() {
        try {
            return ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("Failed to capture screenshot as Base64: {}", e.getMessage());
            return null;
        }
    }
}
