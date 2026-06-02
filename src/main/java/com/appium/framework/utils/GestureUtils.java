package com.appium.framework.utils;

import com.appium.framework.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Touch gesture utilities using the W3C Actions API.
 *
 * <p><b>Concept covered: W3C Actions API for Mobile Gestures</b><br>
 * Appium Java Client 8.x moved away from the legacy {@code TouchAction} and
 * {@code MultiTouchAction} APIs and adopted the W3C WebDriver Actions API.
 * Gestures are expressed as sequences of pointer events:</p>
 *
 * <pre>
 *   PointerInput (TOUCH type) → Sequence of:
 *     createPointerMove  — position finger at coordinates
 *     createPointerDown  — press finger down
 *     createPointerMove  — move finger to new coordinates (with duration = swipe speed)
 *     createPointerUp    — lift finger
 * </pre>
 *
 * <p><b>Coordinate system:</b> All coordinates are in pixels relative to the top-left
 * corner of the viewport. Use percentage calculations (e.g., {@code size.width * 0.80})
 * to write device-resolution-independent gestures.</p>
 *
 * <p><b>Multi-finger gestures</b> (pinch, zoom): Create two separate {@link Sequence}
 * objects (one per finger) and pass them both to {@code driver.perform()}. The driver
 * executes them simultaneously, simulating two-finger touch.</p>
 *
 * <p><b>Gesture duration:</b> The duration in {@code createPointerMove} controls swipe
 * speed. Shorter = faster swipe; longer = slower drag. Too fast a swipe may not register
 * as a scroll on some implementations — 500–800ms is typically reliable.</p>
 */
public class GestureUtils {

    private static final Logger log = LogManager.getLogger(GestureUtils.class);

    private GestureUtils() {}

    // ── Tap ───────────────────────────────────────────────────────────────────

    /**
     * Taps at the specified pixel coordinates on the screen.
     *
     * <p>A tap is a press-down followed immediately by a lift, simulating a quick finger touch.</p>
     *
     * @param x horizontal pixel coordinate from left
     * @param y vertical pixel coordinate from top
     */
    public static void tap(int x, int y) {
        log.debug("Tap at ({}, {})", x, y);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        DriverManager.getDriver().perform(List.of(tap));
    }

    /**
     * Taps the center of the given element.
     *
     * @param element the element to tap
     */
    public static void tap(WebElement element) {
        Point center = getCenter(element);
        tap(center.getX(), center.getY());
    }

    // ── Long Press ────────────────────────────────────────────────────────────

    /**
     * Long presses an element for 2 seconds (default duration).
     * Triggers context menus, drag-start events, and other long-press actions.
     *
     * @param element the element to long press
     */
    public static void longPress(WebElement element) {
        longPress(element, 2000);
    }

    /**
     * Long presses an element for the specified duration.
     *
     * <p>The key to a long press is adding a {@code createPointerMove} with a long duration
     * while keeping the finger stationary — this registers as a held press rather than a drag.</p>
     *
     * @param element    the element to long press
     * @param durationMs hold duration in milliseconds (typically 1000–3000ms)
     */
    public static void longPress(WebElement element, long durationMs) {
        log.debug("Long press element for {}ms", durationMs);
        Point center = getCenter(element);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPress = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), center.getX(), center.getY()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                // Hold at same position for durationMs to register as long press
                .addAction(finger.createPointerMove(Duration.ofMillis(durationMs),
                        PointerInput.Origin.viewport(), center.getX(), center.getY()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        DriverManager.getDriver().perform(List.of(longPress));
    }

    // ── Double Tap ────────────────────────────────────────────────────────────

    /**
     * Double taps an element (two rapid taps in succession).
     *
     * <p>A brief pause between taps ensures the OS registers them as separate events.
     * Too long a pause (>500ms) may be interpreted as two single taps rather than a double tap.</p>
     *
     * @param element the element to double tap
     */
    public static void doubleTap(WebElement element) {
        log.debug("Double tap element");
        tap(element);
        WaitUtils.hardWait(100); // 100ms between taps — just enough for the OS to register two events
        tap(element);
    }

    // ── Swipe ─────────────────────────────────────────────────────────────────

    /**
     * Swipes upward on the screen (scrolls content down / page moves up).
     *
     * <p>Percentages of screen height ensure this works across different screen resolutions:
     * start at 80% from top → end at 20% from top = upward finger movement.</p>
     */
    public static void swipeUp() {
        Dimension size = DriverManager.getDriver().manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.80);
        int endY   = (int) (size.height * 0.20);
        swipe(startX, startY, startX, endY, 800);
    }

    /**
     * Swipes downward on the screen (scrolls content up / page moves down).
     */
    public static void swipeDown() {
        Dimension size = DriverManager.getDriver().manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.20);
        int endY   = (int) (size.height * 0.80);
        swipe(startX, startY, startX, endY, 800);
    }

    /**
     * Swipes left across the screen (scrolls content right / moves to next page).
     */
    public static void swipeLeft() {
        Dimension size = DriverManager.getDriver().manage().window().getSize();
        int startX = (int) (size.width * 0.80);
        int startY = size.height / 2;
        int endX   = (int) (size.width * 0.20);
        swipe(startX, startY, endX, startY, 800);
    }

    /**
     * Swipes right across the screen (scrolls content left / moves to previous page).
     */
    public static void swipeRight() {
        Dimension size = DriverManager.getDriver().manage().window().getSize();
        int startX = (int) (size.width * 0.20);
        int startY = size.height / 2;
        int endX   = (int) (size.width * 0.80);
        swipe(startX, startY, endX, startY, 800);
    }

    /**
     * Performs a swipe from start coordinates to end coordinates at the given speed.
     *
     * <p>This is the core swipe method — all directional swipe methods delegate here.
     * The {@code durationMs} controls speed: 300ms = fast flick, 800ms = normal scroll,
     * 1500ms+ = slow drag.</p>
     *
     * @param startX     starting X pixel
     * @param startY     starting Y pixel
     * @param endX       ending X pixel
     * @param endY       ending Y pixel
     * @param durationMs swipe duration in milliseconds
     */
    public static void swipe(int startX, int startY, int endX, int endY, long durationMs) {
        log.debug("Swipe ({},{}) → ({},{}) in {}ms", startX, startY, endX, endY, durationMs);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(durationMs),
                        PointerInput.Origin.viewport(), endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        DriverManager.getDriver().perform(List.of(swipe));
    }

    // ── Pinch / Zoom (Multi-Touch) ────────────────────────────────────────────

    /**
     * Performs a two-finger spread (zoom in / pinch open) gesture on the element.
     *
     * <p>Two separate {@link Sequence} objects (one per finger) are created and passed
     * together to {@code perform()}. The driver executes them simultaneously,
     * simulating a real two-finger gesture on the screen.</p>
     *
     * <p>Zoom in = fingers start close together and move apart.</p>
     *
     * @param element the element to zoom in on (gestures centered on it)
     */
    public static void pinchToZoomIn(WebElement element) {
        log.debug("Pinch zoom in on element");
        Point center = getCenter(element);
        int offset = 150;

        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

        Sequence f1 = new Sequence(finger1, 1)
                .addAction(finger1.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), center.getX() - offset / 2, center.getY()))
                .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger1.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), center.getX() - offset, center.getY()))
                .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence f2 = new Sequence(finger2, 1)
                .addAction(finger2.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), center.getX() + offset / 2, center.getY()))
                .addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger2.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), center.getX() + offset, center.getY()))
                .addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        DriverManager.getDriver().perform(Arrays.asList(f1, f2));
    }

    /**
     * Performs a two-finger pinch (zoom out / pinch close) gesture on the element.
     *
     * <p>Zoom out = fingers start far apart and move toward the center.</p>
     *
     * @param element the element to zoom out on
     */
    public static void pinchToZoomOut(WebElement element) {
        log.debug("Pinch zoom out on element");
        Point center = getCenter(element);
        int offset = 150;

        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

        Sequence f1 = new Sequence(finger1, 1)
                .addAction(finger1.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), center.getX() - offset, center.getY()))
                .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger1.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), center.getX() - offset / 4, center.getY()))
                .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence f2 = new Sequence(finger2, 1)
                .addAction(finger2.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), center.getX() + offset, center.getY()))
                .addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger2.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), center.getX() + offset / 4, center.getY()))
                .addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        DriverManager.getDriver().perform(Arrays.asList(f1, f2));
    }

    // ── Drag and Drop ─────────────────────────────────────────────────────────

    /**
     * Drags an element from its current position and drops it onto a target element.
     *
     * <p>A drag-and-drop is a slow swipe with a long hold at the start to signal "drag start"
     * to the OS. The 1000ms duration ensures the gesture is recognized as a drag, not a scroll.</p>
     *
     * @param source the element to drag
     * @param target the element to drop onto
     */
    public static void dragAndDrop(WebElement source, WebElement target) {
        log.debug("Drag and drop: source → target");
        Point src = getCenter(source);
        Point tgt = getCenter(target);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence drag = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), src.getX(), src.getY()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                // Slow movement (1000ms) ensures the system recognizes this as a drag, not a flick
                .addAction(finger.createPointerMove(Duration.ofMillis(1000),
                        PointerInput.Origin.viewport(), tgt.getX(), tgt.getY()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        DriverManager.getDriver().perform(List.of(drag));
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    /**
     * Calculates the center point of an element.
     * Used to target the middle of an element for tap/press gestures.
     *
     * @param element the element whose center to calculate
     * @return center {@link Point} in viewport pixels
     */
    private static Point getCenter(WebElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        return new Point(
                location.getX() + size.width / 2,
                location.getY() + size.height / 2
        );
    }
}
