package com.appium.framework.utils;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for Appium 2.x {@code mobile:} gesture commands.
 *
 * <p><b>Concept covered: mobile: gesture commands</b><br>
 * Appium 2.x exposes a set of driver-native gesture shortcuts via the
 * {@code driver.executeScript("mobile: commandName", args)} interface.
 * These differ from the W3C Actions API in that they delegate gesture execution
 * directly to the native automation engine (UiAutomator2 or XCUITest), which
 * often produces more reliable results for complex gestures.</p>
 *
 * <h2>When to use mobile: commands vs W3C Actions ({@link GestureUtils})</h2>
 * <table border="1">
 *   <tr><th>Scenario</th><th>Recommended</th></tr>
 *   <tr><td>Simple tap, swipe, long press</td><td>W3C Actions — universal</td></tr>
 *   <tr><td>Scroll with inertia (fling)</td><td>{@code mobile: flingGesture} — Android only</td></tr>
 *   <tr><td>iOS scroll to predicate/element</td><td>{@code mobile: scroll} — iOS preferred</td></tr>
 *   <tr><td>Pinch/zoom without coordinate math</td><td>{@code mobile:} commands — cleaner API</td></tr>
 *   <tr><td>Cross-platform gesture</td><td>W3C Actions — works on both</td></tr>
 * </table>
 *
 * <h2>Android UiAutomator2 {@code mobile:} commands</h2>
 * <pre>
 *   mobile: scrollGesture       — scroll within a container element
 *   mobile: swipeGesture        — swipe in a direction on element
 *   mobile: tapGesture          — tap at coordinates or on element
 *   mobile: longClickGesture    — long press on element
 *   mobile: doubleClickGesture  — double tap on element
 *   mobile: flingGesture        — fast inertial scroll (no W3C equivalent)
 *   mobile: pinchOpenGesture    — spread two fingers apart (zoom in)
 *   mobile: pinchCloseGesture   — pinch two fingers together (zoom out)
 *   mobile: dragGesture         — drag from one point to another
 * </pre>
 *
 * <h2>iOS XCUITest {@code mobile:} commands</h2>
 * <pre>
 *   mobile: scroll     — scroll by direction, predicate, or to visible element
 *   mobile: swipe      — swipe in a direction on element
 *   mobile: tap        — tap on element
 *   mobile: doubleTap  — double tap on element
 *   mobile: longPress  — long press on element
 *   mobile: pinch      — pinch gesture with scale and velocity
 *   mobile: rotate     — rotate gesture
 * </pre>
 *
 * @see GestureUtils for W3C Actions-based gestures (cross-platform)
 */
public class MobileGestureUtils {

    private static final Logger log = LogManager.getLogger(MobileGestureUtils.class);

    private MobileGestureUtils() {}

    // ── Cross-Platform Dispatchers ─────────────────────────────────────────────

    /**
     * Scrolls using the platform-appropriate {@code mobile:} command.
     * Dispatches to Android's {@code mobile: scrollGesture} or iOS's {@code mobile: scroll}.
     *
     * @param direction "up", "down", "left", or "right"
     */
    public static void scroll(String direction) {
        if (ConfigReader.isAndroid()) {
            scrollAndroid(direction);
        } else {
            scrollIos(direction);
        }
    }

    /**
     * Swipes on an element using the platform-appropriate {@code mobile:} command.
     *
     * @param element   element to swipe on
     * @param direction "up", "down", "left", or "right"
     */
    public static void swipe(WebElement element, String direction) {
        if (ConfigReader.isAndroid()) {
            swipeAndroid(element, direction);
        } else {
            swipeIos(element, direction);
        }
    }

    /**
     * Double taps using the platform-appropriate {@code mobile:} command.
     *
     * @param element element to double tap
     */
    public static void doubleTap(WebElement element) {
        if (ConfigReader.isAndroid()) {
            doubleClickAndroid(element);
        } else {
            doubleTapIos(element);
        }
    }

    /**
     * Long presses using the platform-appropriate {@code mobile:} command.
     *
     * @param element    element to long press
     * @param durationMs hold duration in milliseconds
     */
    public static void longPress(WebElement element, long durationMs) {
        if (ConfigReader.isAndroid()) {
            longClickAndroid(element, durationMs);
        } else {
            longPressIos(element, durationMs);
        }
    }

    // ── Android UiAutomator2 mobile: commands ──────────────────────────────────

    /**
     * Scrolls on Android using {@code mobile: scrollGesture}.
     *
     * <p>Unlike W3C Actions-based scrolling, {@code mobile: scrollGesture} uses the
     * Android UiAutomator2 engine to perform a native scroll, which respects the
     * scrollable container's physics and boundaries automatically.</p>
     *
     * <p>The bounding box defaults to full screen (0,0,1080,1920). For scrolling
     * within a specific container, use {@link #scrollAndroidElement} instead.</p>
     *
     * @param direction "up", "down", "left", or "right"
     */
    public static void scrollAndroid(String direction) {
        log.info("Android mobile: scrollGesture — direction={}", direction);
        Map<String, Object> args = new HashMap<>();
        args.put("left", 0);
        args.put("top", 0);
        args.put("width", 1080);
        args.put("height", 1920);
        args.put("direction", direction);
        args.put("percent", 0.75); // scroll 75% of the container height/width
        executeScript("mobile: scrollGesture", args);
    }

    /**
     * Scrolls within a specific container element on Android using {@code mobile: scrollGesture}.
     *
     * @param container scrollable container element
     * @param direction "up", "down", "left", or "right"
     * @param percent   fraction of the container to scroll (0.0–1.0, e.g., 0.5 = half)
     */
    public static void scrollAndroidElement(WebElement container, String direction, double percent) {
        log.info("Android mobile: scrollGesture on element — direction={}, percent={}", direction, percent);
        Map<String, Object> args = new HashMap<>();
        args.put("elementId", elementId(container));
        args.put("direction", direction);
        args.put("percent", percent);
        executeScript("mobile: scrollGesture", args);
    }

    /**
     * Swipes on an element on Android using {@code mobile: swipeGesture}.
     *
     * <p>The element's bounding rectangle is used as the gesture area.
     * A swipe differs from a scroll in that it doesn't trigger inertial scrolling.</p>
     *
     * @param element   element to swipe on
     * @param direction "up", "down", "left", or "right"
     */
    public static void swipeAndroid(WebElement element, String direction) {
        log.info("Android mobile: swipeGesture — direction={}", direction);
        Map<String, Object> args = new HashMap<>();
        args.put("elementId", elementId(element));
        args.put("direction", direction);
        args.put("percent", 0.75);
        executeScript("mobile: swipeGesture", args);
    }

    /**
     * Taps at specific pixel coordinates using {@code mobile: tapGesture}.
     *
     * @param x horizontal pixel coordinate from left edge
     * @param y vertical pixel coordinate from top edge
     */
    public static void tapAndroid(int x, int y) {
        log.info("Android mobile: tapGesture at ({},{})", x, y);
        Map<String, Object> args = new HashMap<>();
        args.put("x", x);
        args.put("y", y);
        executeScript("mobile: tapGesture", args);
    }

    /**
     * Taps the center of an element using {@code mobile: tapGesture}.
     *
     * @param element element to tap
     */
    public static void tapAndroid(WebElement element) {
        log.info("Android mobile: tapGesture on element");
        Map<String, Object> args = new HashMap<>();
        args.put("elementId", elementId(element));
        executeScript("mobile: tapGesture", args);
    }

    /**
     * Long presses an element on Android using {@code mobile: longClickGesture}.
     *
     * <p>UiAutomator2's long click implementation is more reliable than the W3C Actions
     * equivalent because it uses the Android AccessibilityNodeInfo long-click action
     * directly, avoiding timing issues with pointer hold durations.</p>
     *
     * @param element    element to long press
     * @param durationMs hold duration in milliseconds (default 2000ms if ≤0)
     */
    public static void longClickAndroid(WebElement element, long durationMs) {
        long duration = durationMs > 0 ? durationMs : 2000;
        log.info("Android mobile: longClickGesture — duration={}ms", duration);
        Map<String, Object> args = new HashMap<>();
        args.put("elementId", elementId(element));
        args.put("duration", duration);
        executeScript("mobile: longClickGesture", args);
    }

    /**
     * Double taps an element on Android using {@code mobile: doubleClickGesture}.
     *
     * <p>This is preferred over two rapid W3C Action taps because UiAutomator2
     * handles the inter-tap timing to ensure the OS registers it as a double-click.</p>
     *
     * @param element element to double click
     */
    public static void doubleClickAndroid(WebElement element) {
        log.info("Android mobile: doubleClickGesture");
        Map<String, Object> args = new HashMap<>();
        args.put("elementId", elementId(element));
        executeScript("mobile: doubleClickGesture", args);
    }

    /**
     * Performs a fling (fast inertial scroll) on Android using {@code mobile: flingGesture}.
     *
     * <p><b>Key difference from swipe/scroll:</b> A fling continues scrolling after the
     * finger lifts, using Android's physics engine. The scroll distance is determined by
     * the {@code speed} parameter (pixels/second). There is no W3C Actions equivalent
     * for this behavior — this is uniquely available via {@code mobile: flingGesture}.</p>
     *
     * @param element   the scrollable container to fling within
     * @param direction "up", "down", "left", or "right"
     * @param speed     fling speed in pixels/second (recommended: 5000–15000)
     */
    public static void flingAndroid(WebElement element, String direction, int speed) {
        log.info("Android mobile: flingGesture — direction={}, speed={}", direction, speed);
        Map<String, Object> args = new HashMap<>();
        args.put("elementId", elementId(element));
        args.put("direction", direction);
        args.put("speed", speed);
        executeScript("mobile: flingGesture", args);
    }

    /**
     * Zooms in on an element using {@code mobile: pinchOpenGesture}.
     *
     * <p>The {@code percent} parameter defines how much to spread the fingers relative
     * to the element's dimensions. Higher percent = more zoom. The gesture is centered
     * on the element automatically.</p>
     *
     * @param element the element to zoom in on
     * @param percent spread fraction (0.1–1.0, e.g., 0.5 = spread 50% of element width)
     * @param speed   gesture speed in pixels/second
     */
    public static void pinchOpenAndroid(WebElement element, double percent, int speed) {
        log.info("Android mobile: pinchOpenGesture — percent={}, speed={}", percent, speed);
        Map<String, Object> args = new HashMap<>();
        args.put("elementId", elementId(element));
        args.put("percent", percent);
        args.put("speed", speed);
        executeScript("mobile: pinchOpenGesture", args);
    }

    /**
     * Zooms out on an element using {@code mobile: pinchCloseGesture}.
     *
     * @param element the element to zoom out on
     * @param percent pinch fraction (0.1–1.0)
     * @param speed   gesture speed in pixels/second
     */
    public static void pinchCloseAndroid(WebElement element, double percent, int speed) {
        log.info("Android mobile: pinchCloseGesture — percent={}, speed={}", percent, speed);
        Map<String, Object> args = new HashMap<>();
        args.put("elementId", elementId(element));
        args.put("percent", percent);
        args.put("speed", speed);
        executeScript("mobile: pinchCloseGesture", args);
    }

    /**
     * Drags from start coordinates to end coordinates using {@code mobile: dragGesture}.
     *
     * @param startX starting X pixel coordinate
     * @param startY starting Y pixel coordinate
     * @param endX   ending X pixel coordinate
     * @param endY   ending Y pixel coordinate
     * @param speed  drag speed in pixels/second
     */
    public static void dragAndroid(int startX, int startY, int endX, int endY, int speed) {
        log.info("Android mobile: dragGesture ({},{}) → ({},{}) speed={}", startX, startY, endX, endY, speed);
        Map<String, Object> args = new HashMap<>();
        args.put("startX", startX);
        args.put("startY", startY);
        args.put("endX", endX);
        args.put("endY", endY);
        args.put("speed", speed);
        executeScript("mobile: dragGesture", args);
    }

    // ── iOS XCUITest mobile: commands ──────────────────────────────────────────

    /**
     * Scrolls on iOS using {@code mobile: scroll}.
     *
     * <p>This is the recommended scroll method for iOS because it uses the XCUITest
     * engine's native scroll implementation, which correctly handles scroll indicators,
     * momentum, and list/table scroll snapping.</p>
     *
     * <p>Argument options (at least one required):
     * <ul>
     *   <li>{@code direction} — "up", "down", "left", "right"</li>
     *   <li>{@code element} — scroll within a specific container</li>
     *   <li>{@code predicateString} — NSPredicate to scroll until element matches</li>
     *   <li>{@code toVisible} — true/false, scroll until first visible element</li>
     * </ul>
     * </p>
     *
     * @param direction "up", "down", "left", or "right"
     */
    public static void scrollIos(String direction) {
        log.info("iOS mobile: scroll — direction={}", direction);
        Map<String, Object> args = new HashMap<>();
        args.put("direction", direction);
        executeScript("mobile: scroll", args);
    }

    /**
     * Scrolls on iOS within a specific container element.
     *
     * @param container the scroll container element
     * @param direction "up", "down", "left", or "right"
     */
    public static void scrollIosElement(WebElement container, String direction) {
        log.info("iOS mobile: scroll on element — direction={}", direction);
        Map<String, Object> args = new HashMap<>();
        args.put("element", elementId(container));
        args.put("direction", direction);
        executeScript("mobile: scroll", args);
    }

    /**
     * Scrolls on iOS until an element matching the NSPredicate is visible.
     *
     * <p>This is the most reliable way to scroll to a specific element on iOS:
     * <pre>
     *   MobileGestureUtils.scrollIosToElement("name == 'Submit'");
     *   MobileGestureUtils.scrollIosToElement("label CONTAINS 'Save'");
     * </pre>
     * </p>
     *
     * @param predicateString NSPredicate expression targeting the destination element
     */
    public static void scrollIosToElement(String predicateString) {
        log.info("iOS mobile: scroll to predicateString='{}'", predicateString);
        Map<String, Object> args = new HashMap<>();
        args.put("predicateString", predicateString);
        executeScript("mobile: scroll", args);
    }

    /**
     * Swipes on an element on iOS using {@code mobile: swipe}.
     *
     * @param element   element to swipe on
     * @param direction "up", "down", "left", or "right"
     */
    public static void swipeIos(WebElement element, String direction) {
        log.info("iOS mobile: swipe — direction={}", direction);
        Map<String, Object> args = new HashMap<>();
        args.put("element", elementId(element));
        args.put("direction", direction);
        executeScript("mobile: swipe", args);
    }

    /**
     * Taps an element on iOS using {@code mobile: tap}.
     *
     * @param element element to tap
     */
    public static void tapIos(WebElement element) {
        log.info("iOS mobile: tap");
        Map<String, Object> args = new HashMap<>();
        args.put("element", elementId(element));
        executeScript("mobile: tap", args);
    }

    /**
     * Double taps an element on iOS using {@code mobile: doubleTap}.
     *
     * @param element element to double tap
     */
    public static void doubleTapIos(WebElement element) {
        log.info("iOS mobile: doubleTap");
        Map<String, Object> args = new HashMap<>();
        args.put("element", elementId(element));
        executeScript("mobile: doubleTap", args);
    }

    /**
     * Long presses an element on iOS using {@code mobile: longPress}.
     *
     * <p>Note: iOS uses seconds for duration, not milliseconds.
     * This method accepts milliseconds and converts automatically.</p>
     *
     * @param element    element to long press
     * @param durationMs hold duration in milliseconds
     */
    public static void longPressIos(WebElement element, long durationMs) {
        double seconds = durationMs / 1000.0;
        log.info("iOS mobile: longPress — duration={}s", seconds);
        Map<String, Object> args = new HashMap<>();
        args.put("element", elementId(element));
        args.put("duration", seconds); // XCUITest uses seconds
        executeScript("mobile: longPress", args);
    }

    /**
     * Pinches on iOS using {@code mobile: pinch}.
     *
     * <p>Scale values:
     * <ul>
     *   <li>{@code scale > 1.0} — spread (zoom in), e.g., 2.0 doubles the view</li>
     *   <li>{@code scale < 1.0} — pinch (zoom out), e.g., 0.5 halves the view</li>
     * </ul>
     * Velocity: 1.0 = normal speed; higher = faster gesture.</p>
     *
     * @param element  element to pinch
     * @param scale    scale factor (>1 for zoom in, <1 for zoom out)
     * @param velocity pinch speed (recommended: 1.0–2.0)
     */
    public static void pinchIos(WebElement element, double scale, double velocity) {
        log.info("iOS mobile: pinch — scale={}, velocity={}", scale, velocity);
        Map<String, Object> args = new HashMap<>();
        args.put("element", elementId(element));
        args.put("scale", scale);
        args.put("velocity", velocity);
        executeScript("mobile: pinch", args);
    }

    /**
     * Rotates an element on iOS using {@code mobile: rotate}.
     *
     * @param element  element to rotate
     * @param rotation rotation in radians (positive = clockwise, negative = counter-clockwise)
     * @param velocity rotation speed (recommended: 1.0–2.0)
     */
    public static void rotateIos(WebElement element, double rotation, double velocity) {
        log.info("iOS mobile: rotate — rotation={}rad, velocity={}", rotation, velocity);
        Map<String, Object> args = new HashMap<>();
        args.put("element", elementId(element));
        args.put("rotation", rotation);
        args.put("velocity", velocity);
        executeScript("mobile: rotate", args);
    }

    // ── Private Helpers ────────────────────────────────────────────────────────

    /**
     * Executes a {@code mobile:} script via {@link JavascriptExecutor}.
     *
     * @param command mobile: command name (e.g., "mobile: scrollGesture")
     * @param args    command argument map
     */
    private static void executeScript(String command, Map<String, Object> args) {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript(command, args);
    }

    /**
     * Extracts the native element ID string for use in {@code mobile:} command args.
     *
     * <p>Appium 2.x {@code mobile:} commands accept the W3C element reference ID
     * (a UUID string) in their argument maps. This is distinct from passing the
     * {@link WebElement} object directly — the native ID string is needed here.</p>
     *
     * @param element the WebElement to extract the ID from
     * @return element ID string (W3C element reference)
     */
    private static String elementId(WebElement element) {
        return ((RemoteWebElement) element).getId();
    }
}
