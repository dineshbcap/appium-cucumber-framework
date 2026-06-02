package com.appium.framework.utils;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File transfer utilities for pushing files to and pulling files from a device.
 *
 * <p><b>Concept covered:</b> Appium provides {@code pushFile} and {@code pullFile}
 * commands that transfer binary data between the test machine and the device file system
 * without requiring ADB or iTunes. This is critical for testing:
 * <ul>
 *   <li>File upload flows (pre-seeding the device with a test file)</li>
 *   <li>File download verification (pulling and inspecting a downloaded file)</li>
 *   <li>Document-based apps (seeding PDFs, images, CSVs)</li>
 *   <li>Log file retrieval after a test run</li>
 * </ul>
 * </p>
 *
 * <p><b>Android paths:</b> Use the absolute path on the device storage, e.g.:
 * {@code /sdcard/Download/test.pdf}, {@code /data/data/com.example/files/config.json}.</p>
 *
 * <p><b>iOS paths:</b> Use the container-relative path format:
 * {@code @com.example.app/Documents/test.pdf} (for app container)
 * or {@code @com.example.app/Library/Caches/test.pdf}. The {@code @bundleId/} prefix
 * tells XCUITest which app container to access.</p>
 */
public class FileTransferUtils {

    private static final Logger log = LogManager.getLogger(FileTransferUtils.class);

    private FileTransferUtils() {}

    // ── Push File to Device ────────────────────────────────────────────────────

    /**
     * Pushes a local file from the test machine onto the device at the specified path.
     *
     * <p>Use this to pre-seed the device with test data (images, documents, CSVs)
     * before a scenario that tests file-related app features.</p>
     *
     * @param localFilePath  absolute path to the file on the test machine
     * @param deviceFilePath target path on the device (see class-level Javadoc for format)
     * @throws RuntimeException if the local file cannot be read or push fails
     */
    public static void pushFile(String localFilePath, String deviceFilePath) {
        log.info("Pushing file from '{}' to device path '{}'", localFilePath, deviceFilePath);
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(localFilePath));
            if (ConfigReader.isAndroid()) {
                ((AndroidDriver) DriverManager.getDriver()).pushFile(deviceFilePath, fileBytes);
            } else {
                ((IOSDriver) DriverManager.getDriver()).pushFile(deviceFilePath, fileBytes);
            }
            log.info("File pushed successfully ({} bytes)", fileBytes.length);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read local file for push: " + localFilePath, e);
        }
    }

    /**
     * Pushes raw byte content directly to a path on the device.
     * Useful when the content is generated in memory (e.g., test fixtures built dynamically).
     *
     * @param content        raw bytes to write to the device
     * @param deviceFilePath target path on the device
     */
    public static void pushFileBytes(byte[] content, String deviceFilePath) {
        log.info("Pushing {} bytes to device path '{}'", content.length, deviceFilePath);
        if (ConfigReader.isAndroid()) {
            ((AndroidDriver) DriverManager.getDriver()).pushFile(deviceFilePath, content);
        } else {
            ((IOSDriver) DriverManager.getDriver()).pushFile(deviceFilePath, content);
        }
        log.info("Bytes pushed successfully");
    }

    /**
     * Pushes a plain text string as a file to the device.
     * Convenience wrapper around {@link #pushFileBytes(byte[], String)}.
     *
     * @param content        text content to write
     * @param deviceFilePath target path on the device
     */
    public static void pushTextFile(String content, String deviceFilePath) {
        pushFileBytes(content.getBytes(java.nio.charset.StandardCharsets.UTF_8), deviceFilePath);
    }

    // ── Pull File from Device ──────────────────────────────────────────────────

    /**
     * Pulls a file from the device and returns its content as a byte array.
     *
     * <p>Use this to retrieve files created by the app under test (downloaded files,
     * exported reports, log files) and inspect their content in assertions.</p>
     *
     * @param deviceFilePath path of the file on the device to retrieve
     * @return file content as bytes, or empty array if not found
     */
    public static byte[] pullFile(String deviceFilePath) {
        log.info("Pulling file from device path: '{}'", deviceFilePath);
        byte[] data;
        if (ConfigReader.isAndroid()) {
            data = ((AndroidDriver) DriverManager.getDriver()).pullFile(deviceFilePath);
        } else {
            data = ((IOSDriver) DriverManager.getDriver()).pullFile(deviceFilePath);
        }
        log.info("Pulled {} bytes from '{}'", data != null ? data.length : 0, deviceFilePath);
        return data != null ? data : new byte[0];
    }

    /**
     * Pulls a file from the device and saves it to a local path on the test machine.
     *
     * @param deviceFilePath path of the file on the device
     * @param localSavePath  absolute path on the test machine where the file should be saved
     * @return the saved file's {@link Path}
     * @throws RuntimeException if saving the pulled file fails
     */
    public static Path pullFileTo(String deviceFilePath, String localSavePath) {
        log.info("Pulling '{}' and saving to '{}'", deviceFilePath, localSavePath);
        byte[] data = pullFile(deviceFilePath);
        Path destination = Paths.get(localSavePath);
        try {
            Files.createDirectories(destination.getParent());
            Files.write(destination, data);
            log.info("File saved to '{}'", destination.toAbsolutePath());
            return destination;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save pulled file to: " + localSavePath, e);
        }
    }

    /**
     * Pulls a file from the device and returns its content as a UTF-8 string.
     * Convenient for text-based files like logs, JSONs, or CSVs.
     *
     * @param deviceFilePath path of the file on the device
     * @return file content as a String (UTF-8)
     */
    public static String pullFileAsString(String deviceFilePath) {
        byte[] data = pullFile(deviceFilePath);
        String content = new String(data, java.nio.charset.StandardCharsets.UTF_8);
        log.info("Pulled file content ({} chars)", content.length());
        return content;
    }

    // ── Pull Folder (Android) ──────────────────────────────────────────────────

    /**
     * Pulls an entire folder from the Android device as a ZIP archive.
     *
     * <p>Android only: returns the folder contents as a Base64-encoded ZIP byte array.
     * Useful for bulk-retrieving all files generated by the app during a test run.</p>
     *
     * @param deviceFolderPath absolute path to the folder on the Android device
     * @return ZIP archive bytes, or empty array on iOS (not supported)
     */
    public static byte[] pullAndroidFolder(String deviceFolderPath) {
        if (!ConfigReader.isAndroid()) {
            log.warn("pullFolder is Android-only");
            return new byte[0];
        }
        log.info("Pulling folder from Android: '{}'", deviceFolderPath);
        byte[] zipData = ((AndroidDriver) DriverManager.getDriver()).pullFolder(deviceFolderPath);
        log.info("Pulled folder ZIP ({} bytes)", zipData != null ? zipData.length : 0);
        return zipData != null ? zipData : new byte[0];
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Verifies that a file exists on the device by attempting to pull it.
     * Returns {@code false} if the file is empty or the pull throws an exception.
     *
     * @param deviceFilePath path of the file to check
     * @return {@code true} if the file exists and has content
     */
    public static boolean deviceFileExists(String deviceFilePath) {
        try {
            byte[] data = pullFile(deviceFilePath);
            return data.length > 0;
        } catch (Exception e) {
            log.debug("File not found or empty: '{}'", deviceFilePath);
            return false;
        }
    }

    /**
     * Returns the default download directory path for the current platform.
     *
     * @return platform-appropriate downloads path
     */
    public static String getDeviceDownloadPath() {
        if (ConfigReader.isAndroid()) {
            return "/sdcard/Download";
        } else {
            // iOS: apps cannot write to a shared Downloads folder;
            // this returns the app's Documents directory path prefix
            return "@" + ConfigReader.get("ios.bundleId") + "/Documents";
        }
    }
}
