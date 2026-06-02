package com.appium.framework.utils;

import com.appium.framework.driver.DriverManager;
import io.appium.java_client.clipboard.ClipboardContentType;
import io.appium.java_client.clipboard.HasClipboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;

/**
 * Clipboard (copy/paste) interaction utilities for both Android and iOS.
 *
 * <p><b>Concept covered:</b> Appium's {@link HasClipboard} interface allows tests to
 * read and write the device clipboard — essential for verifying copy-to-clipboard buttons,
 * share sheet content, and auto-fill behaviors.</p>
 *
 * <p><b>Android:</b> Works on emulators and real devices.
 * Requires Android API 28 or lower without restrictions, or the app must be in the foreground
 * on API 29+ (clipboard background access was restricted in Android 10).</p>
 *
 * <p><b>iOS:</b> Works on Simulators without additional configuration.
 * On real devices, requires {@code pasteboard} entitlement or the app being in the foreground.
 * iOS 14+ shows a banner notification when an app reads the clipboard.</p>
 *
 * <p><b>Content types:</b> The {@link ClipboardContentType} enum supports:
 * <ul>
 *   <li>{@code PLAINTEXT} — text strings (most common)</li>
 *   <li>{@code IMAGE} — Base64-encoded image data</li>
 *   <li>{@code URL} — URL strings (iOS only)</li>
 * </ul>
 * </p>
 */
public class ClipboardUtils {

    private static final Logger log = LogManager.getLogger(ClipboardUtils.class);

    private ClipboardUtils() {}

    // ── Text Clipboard ─────────────────────────────────────────────────────────

    /**
     * Sets plain text content on the device clipboard.
     *
     * <p>Equivalent to a "Copy" action. After calling this, the text is available
     * for pasting into any text field via long-press → Paste or programmatically.</p>
     *
     * @param text the text to place on the clipboard
     */
    public static void setClipboardText(String text) {
        log.info("Setting clipboard text: '{}'", text);
        clipboardDriver().setClipboardText(text);
    }

    /**
     * Retrieves the current plain text content from the device clipboard.
     *
     * <p>Returns an empty string if the clipboard is empty or contains non-text content.</p>
     *
     * @return clipboard text, or empty string if clipboard is empty
     */
    public static String getClipboardText() {
        String text = clipboardDriver().getClipboardText();
        log.info("Clipboard text: '{}'", text);
        return text != null ? text : "";
    }

    /**
     * Checks whether the clipboard contains the expected text.
     *
     * @param expectedText text expected to be on the clipboard
     * @return {@code true} if the clipboard text matches the expected value
     */
    public static boolean clipboardContains(String expectedText) {
        return expectedText.equals(getClipboardText());
    }

    /**
     * Clears the clipboard by setting it to an empty string.
     */
    public static void clearClipboard() {
        log.info("Clearing clipboard");
        clipboardDriver().setClipboardText("");
    }

    // ── Image Clipboard ────────────────────────────────────────────────────────

    /**
     * Sets Base64-encoded image data on the clipboard.
     *
     * <p>Useful for testing image-paste workflows in apps that accept pasted images
     * (chat apps, document editors, etc.). In Appium 8.x, setClipboard accepts a
     * Base64 String directly for binary content types.</p>
     *
     * @param base64ImageData Base64-encoded PNG or JPEG image data string
     */
    public static void setClipboardImage(String base64ImageData) {
        log.info("Setting clipboard image (base64 length: {})", base64ImageData.length());
        // setClipboard takes byte[] in Appium 8.x; decode Base64 string to bytes
        clipboardDriver().setClipboard(ClipboardContentType.IMAGE,
                Base64.getDecoder().decode(base64ImageData));
    }

    /**
     * Retrieves image data from the clipboard as a Base64-encoded string.
     * In Appium Java Client 8.x, getClipboard() returns a Base64 String directly.
     *
     * @return Base64 string of the clipboard image, or empty string if no image
     */
    public static String getClipboardImage() {
        log.info("Getting clipboard image");
        String data = clipboardDriver().getClipboard(ClipboardContentType.IMAGE);
        if (data == null || data.isEmpty()) return "";
        return data;
    }

    // ── URL Clipboard (iOS) ────────────────────────────────────────────────────

    /**
     * Sets a URL on the iOS clipboard.
     *
     * <p>iOS distinguishes between plain-text clipboard content and URL content.
     * Setting a URL allows other apps to detect and open it from the pasteboard.
     * In Appium 8.x, setClipboard accepts the URL as a String directly.</p>
     *
     * @param url URL string to place on the clipboard (e.g., "https://example.com")
     */
    public static void setClipboardUrl(String url) {
        log.info("Setting clipboard URL: {}", url);
        // setClipboard takes byte[] in Appium 8.x; encode URL string to UTF-8 bytes
        clipboardDriver().setClipboard(ClipboardContentType.URL,
                url.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    // ── Private Helper ─────────────────────────────────────────────────────────

    /**
     * Casts the current driver to {@link HasClipboard}.
     * Both AndroidDriver and IOSDriver implement this interface.
     *
     * @return driver cast to HasClipboard
     */
    private static HasClipboard clipboardDriver() {
        return (HasClipboard) DriverManager.getDriver();
    }
}
