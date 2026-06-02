package com.appium.framework.utils;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Screen recording utilities for capturing video during test execution.
 *
 * <p><b>Concept covered:</b> Appium's {@link CanRecordScreen} interface enables tests
 * to record video of what happens on screen. Videos are invaluable for debugging failures
 * that screenshots alone cannot explain (race conditions, animations, multi-step flows).</p>
 *
 * <p><b>How it works:</b>
 * <ol>
 *   <li>Call {@link #startRecording()} before the test action begins.</li>
 *   <li>Perform test actions as normal.</li>
 *   <li>Call {@link #stopAndSaveRecording(String)} to stop capture and save the video.</li>
 * </ol>
 * The recording is returned as a Base64-encoded string (MP4 on Android, MP4/MOV on iOS)
 * which is then decoded and written to disk.</p>
 *
 * <p><b>Android options:</b>
 * <ul>
 *   <li>Max duration: up to 3 minutes per recording (Android limitation)</li>
 *   <li>Video size, bit rate, and codec are configurable</li>
 * </ul>
 * </p>
 *
 * <p><b>iOS options:</b>
 * <ul>
 *   <li>Max duration: configurable (default 180 seconds for XCUITest)</li>
 *   <li>Video quality: low, medium, high, photo</li>
 *   <li>Works on both Simulator and real device</li>
 * </ul>
 * </p>
 *
 * <p><b>Cucumber integration tip:</b> Start recording in {@code @Before} and stop/save in
 * {@code @After}. Only save if the scenario failed to keep storage usage manageable.</p>
 */
public class RecordingUtils {

    private static final Logger log = LogManager.getLogger(RecordingUtils.class);
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /** Flag to track whether a recording was actually started (avoids double-stop). */
    private static final ThreadLocal<Boolean> recordingStarted = ThreadLocal.withInitial(() -> false);

    private RecordingUtils() {}

    // ── Start Recording ────────────────────────────────────────────────────────

    /**
     * Starts screen recording with default options.
     *
     * <p>On Android: records at default resolution (device screen size), MP4 format.
     * On iOS: records at medium quality, MP4 format, using default timeout.</p>
     */
    public static void startRecording() {
        log.info("Starting screen recording (platform: {})", ConfigReader.getPlatform());
        if (ConfigReader.isAndroid()) {
            startAndroidRecording();
        } else {
            startIosRecording();
        }
        recordingStarted.set(true);
    }

    /**
     * Starts Android screen recording with configurable options.
     *
     * <p>{@link AndroidStartScreenRecordingOptions} supports:
     * <ul>
     *   <li>{@code withVideoSize} — specify WxH (e.g., "1280x720")</li>
     *   <li>{@code withBitRate} — bitrate in bits/sec (default 4Mbps)</li>
     *   <li>{@code withTimeLimit} — max duration (Android hard-limit: 3 min)</li>
     * </ul>
     * </p>
     */
    private static void startAndroidRecording() {
        AndroidStartScreenRecordingOptions options = new AndroidStartScreenRecordingOptions()
                .withTimeLimit(Duration.ofSeconds(
                        ConfigReader.getInt("recording.max.duration.seconds", 180)));
        ((AndroidDriver) DriverManager.getDriver()).startRecordingScreen(options);
        log.debug("Android recording started");
    }

    /**
     * Starts iOS screen recording with configurable options.
     *
     * <p>{@link IOSStartScreenRecordingOptions} supports:
     * <ul>
     *   <li>{@code withVideoQuality} — LOW, MEDIUM, HIGH, PHOTO</li>
     *   <li>{@code withVideoType} — "mp4", "libx264"</li>
     *   <li>{@code withTimeLimit} — max duration</li>
     * </ul>
     * </p>
     */
    private static void startIosRecording() {
        IOSStartScreenRecordingOptions options = new IOSStartScreenRecordingOptions()
                .withVideoQuality(IOSStartScreenRecordingOptions.VideoQuality.MEDIUM)
                .withTimeLimit(Duration.ofSeconds(
                        ConfigReader.getInt("recording.max.duration.seconds", 180)));
        ((IOSDriver) DriverManager.getDriver()).startRecordingScreen(options);
        log.debug("iOS recording started");
    }

    // ── Stop Recording ─────────────────────────────────────────────────────────

    /**
     * Stops the screen recording and returns the raw Base64-encoded video data.
     *
     * <p>The Base64 string can be decoded to MP4 bytes and written to disk, or
     * attached directly to a Cucumber scenario report.</p>
     *
     * @return Base64-encoded MP4 video data, or empty string if no recording was started
     */
    public static String stopRecording() {
        if (!recordingStarted.get()) {
            log.warn("stopRecording called but no recording was started on this thread");
            return "";
        }
        log.info("Stopping screen recording");
        try {
            String base64Video = ((CanRecordScreen) DriverManager.getDriver()).stopRecordingScreen();
            recordingStarted.set(false);
            log.info("Recording stopped ({} base64 chars)", base64Video.length());
            return base64Video;
        } catch (Exception e) {
            log.error("Error stopping recording: {}", e.getMessage());
            recordingStarted.set(false);
            return "";
        }
    }

    /**
     * Stops the recording and saves the video file to the configured recordings directory.
     *
     * <p>File name is: {@code prefix_threadName_timestamp.mp4}.
     * The directory is created automatically if it does not exist.</p>
     *
     * @param fileNamePrefix prefix for the saved video file (e.g., scenario name)
     * @return absolute path of the saved video file, or null if recording failed
     */
    public static String stopAndSaveRecording(String fileNamePrefix) {
        String base64Video = stopRecording();
        if (base64Video.isEmpty()) return null;

        String recordingsDir = ConfigReader.get("recording.dir", "target/recordings");
        String timestamp = LocalDateTime.now().format(FORMATTER);
        // Thread name sanitized to avoid illegal filename characters
        String threadName = Thread.currentThread().getName().replaceAll("[^a-zA-Z0-9]", "_");
        String fileName = fileNamePrefix + "_" + threadName + "_" + timestamp + ".mp4";
        Path videoPath = Paths.get(recordingsDir, fileName);

        try {
            Files.createDirectories(videoPath.getParent());
            Files.write(videoPath, Base64.getDecoder().decode(base64Video));
            log.info("Video saved: {}", videoPath.toAbsolutePath());
            return videoPath.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to save video file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Stops the recording and returns the raw MP4 bytes (decoded from Base64).
     * Convenient for embedding video directly in Cucumber's scenario report.
     *
     * @return MP4 video bytes, or empty byte array if no recording
     */
    public static byte[] stopAndGetRecordingBytes() {
        String base64Video = stopRecording();
        if (base64Video.isEmpty()) return new byte[0];
        return Base64.getDecoder().decode(base64Video);
    }

    // ── State ──────────────────────────────────────────────────────────────────

    /**
     * Returns whether a screen recording is currently in progress on this thread.
     *
     * @return {@code true} if a recording has been started and not yet stopped
     */
    public static boolean isRecording() {
        return recordingStarted.get();
    }

    /**
     * Safely stops any in-progress recording on this thread without saving.
     * Should be called in finally blocks to avoid leaving ghost recordings.
     */
    public static void safeStopRecording() {
        if (recordingStarted.get()) {
            try {
                stopRecording();
            } catch (Exception e) {
                log.warn("safeStopRecording: {}", e.getMessage());
                recordingStarted.set(false);
            }
        }
    }
}
