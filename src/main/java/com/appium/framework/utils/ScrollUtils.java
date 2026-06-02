package com.appium.framework.utils;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Enhanced scrolling utilities with platform-native strategies for both Android and iOS.
 *
 * <p><b>Concepts covered:</b>
 * <ul>
 *   <li><b>UiScrollable (Android)</b> — the most powerful scroll mechanism on Android.
 *       Uses {@link AppiumBy#androidUIAutomator(String)} to produce a {@code UiScrollable}
 *       command that automatically scrolls the list until the target element is visible.
 *       Much faster and more reliable than W3C swipe gestures for list-based navigation.</li>
 *   <li><b>mobile:scroll (iOS)</b> — XCUITest-native command that scrolls within a container
 *       element until a child becomes visible. Supports direction and predicateString.</li>
 *   <li><b>W3C Gesture Scroll</b> — cross-platform pixel-based scrolling via the Pointer API.
 *       Useful when UiScrollable is not available (e.g., WebViews, custom components).</li>
 *   <li><b>Scroll to text</b> — finding elements by their visible text across both platforms.</li>
 * </ul>
 * </p>
 *
 * <p><b>When to use which approach:</b>
 * <pre>
 *   Android RecyclerView/ListView  → UiScrollable (fastest, no coordinate guessing)
 *   Android custom views/WebView   → W3C gesture swipe
 *   iOS UITableView/UICollectionView → mobile:scroll with predicateString
 *   iOS custom views/WebView        → W3C gesture swipe
 * </pre>
 * </p>
 */
public class ScrollUtils {

    private static final Logger log = LogManager.getLogger(ScrollUtils.class);

    private ScrollUtils() {}

    // ── Android: UiScrollable ─────────────────────────────────────────────────

    /**
     * Scrolls a scrollable Android list until an element with the exact text is visible.
     *
     * <p>Uses {@code UiScrollable.scrollIntoView(UiSelector().text(...))} — Android's
     * built-in scroll mechanic that is faster and more reliable than gesture-based scrolling
     * because it uses the Accessibility framework rather than pixel coordinates.</p>
     *
     * <p>Example: {@code scrollAndroidToText("Settings")} scrolls the main list until the
     * "Settings" item appears.</p>
     *
     * @param text exact text of the target element
     * @return the found element after scrolling
     */
    public static WebElement scrollAndroidToText(String text) {
        log.info("Scrolling Android list to text: '{}'", text);
        String uiScrollable = String.format(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().text(\"%s\"))", text);
        return DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator(uiScrollable));
    }

    /**
     * Scrolls an Android list to an element matching a text that contains the given substring.
     *
     * <p>Uses {@code UiSelector().textContains()} instead of exact text matching.
     * Useful when the target text is dynamic or partially known.</p>
     *
     * @param partialText substring to search within element text
     * @return the found element after scrolling
     */
    public static WebElement scrollAndroidToTextContaining(String partialText) {
        log.info("Scrolling Android to element containing text: '{}'", partialText);
        String uiScrollable = String.format(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().textContains(\"%s\"))", partialText);
        return DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator(uiScrollable));
    }

    /**
     * Scrolls an Android list to an element with a given accessibility/content-description.
     *
     * @param contentDesc the accessibility ID / content-description of the target element
     * @return the found element after scrolling
     */
    public static WebElement scrollAndroidToContentDesc(String contentDesc) {
        log.info("Scrolling Android to content-desc: '{}'", contentDesc);
        String uiScrollable = String.format(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().description(\"%s\"))", contentDesc);
        return DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator(uiScrollable));
    }

    /**
     * Scrolls an Android list to an element by its resource ID.
     *
     * @param resourceId full resource ID (e.g., "com.example:id/item_title")
     * @return the found element after scrolling
     */
    public static WebElement scrollAndroidToResourceId(String resourceId) {
        log.info("Scrolling Android to resource-id: '{}'", resourceId);
        String uiScrollable = String.format(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().resourceId(\"%s\"))", resourceId);
        return DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator(uiScrollable));
    }

    /**
     * Scrolls an Android list to the very end (last item).
     *
     * <p>{@code setMaxSearchSwipes(10)} caps the number of swipes to avoid an infinite loop
     * when the end is unreachable or the list has no definitive last item.</p>
     *
     * @return {@code true} if the end was reached
     */
    public static boolean scrollAndroidToEnd() {
        log.info("Scrolling Android list to end");
        String uiScrollable =
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".setMaxSearchSwipes(10)" +
                ".scrollToEnd(10)";
        try {
            DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator(uiScrollable));
            return true;
        } catch (Exception e) {
            log.warn("Could not scroll to end: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Scrolls an Android list to the very beginning (first item).
     *
     * @return {@code true} if the beginning was reached
     */
    public static boolean scrollAndroidToBeginning() {
        log.info("Scrolling Android list to beginning");
        String uiScrollable =
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".setMaxSearchSwipes(10)" +
                ".scrollToBeginning(10)";
        try {
            DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator(uiScrollable));
            return true;
        } catch (Exception e) {
            log.warn("Could not scroll to beginning: {}", e.getMessage());
            return false;
        }
    }

    // ── iOS: mobile:scroll ────────────────────────────────────────────────────

    /**
     * Scrolls an iOS container (UIScrollView/UITableView) until an element with the
     * given predicate string is visible.
     *
     * <p>The {@code mobile:scroll} command is the recommended XCUITest approach for
     * scrolling to elements. It respects the iOS accessibility tree and handles
     * dequeued cells in UITableView/UICollectionView correctly.</p>
     *
     * @param containerElement the scrollable container element
     * @param predicateString  NSPredicate string to identify the target child element
     *                         (e.g., "name == 'Submit' AND visible == true")
     */
    public static void scrollIosToPredicateString(WebElement containerElement, String predicateString) {
        log.info("iOS mobile:scroll to predicate: {}", predicateString);
        DriverManager.getDriver().executeScript("mobile:scroll", Map.of(
                "element", containerElement,
                "predicateString", predicateString
        ));
    }

    /**
     * Scrolls an iOS container by direction until the named element is found.
     *
     * @param containerElement the scrollable container
     * @param direction        scroll direction: "up", "down", "left", "right"
     * @param name             accessibility name of the target element
     */
    public static void scrollIosToName(WebElement containerElement, String direction, String name) {
        log.info("iOS mobile:scroll direction='{}' to name='{}'", direction, name);
        DriverManager.getDriver().executeScript("mobile:scroll", Map.of(
                "element", containerElement,
                "direction", direction,
                "name", name
        ));
    }

    /**
     * Scrolls an iOS view in a direction by a given distance (fractional viewport).
     *
     * @param containerElement the scrollable element
     * @param direction        "up", "down", "left", or "right"
     */
    public static void scrollIosInDirection(WebElement containerElement, String direction) {
        log.info("iOS mobile:scroll direction='{}'", direction);
        DriverManager.getDriver().executeScript("mobile:scroll", Map.of(
                "element", containerElement,
                "direction", direction
        ));
    }

    // ── Cross-Platform: W3C Gesture Scroll ────────────────────────────────────

    /**
     * Scrolls to an element using repeated W3C swipe gestures (platform-agnostic).
     *
     * <p>Attempts up to {@code maxSwipes} downward swipes. Each swipe checks whether
     * the target element is now in the viewport. Falls back cleanly if not found.</p>
     *
     * @param locator   locator for the target element
     * @param maxSwipes maximum number of swipe attempts before giving up
     * @return the found element, or throws if not found within maxSwipes
     */
    public static WebElement scrollToElementByGesture(By locator, int maxSwipes) {
        log.info("Scrolling to element by gesture (max {} swipes): {}", maxSwipes, locator);
        for (int i = 0; i < maxSwipes; i++) {
            List<WebElement> found = DriverManager.getDriver().findElements(locator);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                log.info("Element found after {} swipes", i);
                return found.get(0);
            }
            swipeUpGesture();
        }
        // Final attempt — let Appium throw naturally if still not found
        return DriverManager.getDriver().findElement(locator);
    }

    /**
     * Performs a single upward swipe gesture (scrolls content upward = moves viewport down).
     *
     * <p>Coordinates are calculated as percentages of screen size to work across
     * different screen resolutions without hard-coded pixel values.</p>
     */
    public static void swipeUpGesture() {
        Dimension size = DriverManager.getDriver().manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.75);
        int endY   = (int) (size.height * 0.25);
        performSwipe(startX, startY, startX, endY, 600);
    }

    /**
     * Performs a single downward swipe gesture (scrolls content downward = moves viewport up).
     */
    public static void swipeDownGesture() {
        Dimension size = DriverManager.getDriver().manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.25);
        int endY   = (int) (size.height * 0.75);
        performSwipe(startX, startY, startX, endY, 600);
    }

    /**
     * Scrolls an element into the visible viewport by scrolling the parent.
     * Uses platform-specific strategy: UiScrollable on Android, W3C gestures on iOS.
     *
     * @param textLabel the visible text label of the target element
     */
    public static WebElement scrollToText(String textLabel) {
        log.info("Scrolling to text '{}' (platform: {})", textLabel, ConfigReader.getPlatform());
        if (ConfigReader.isAndroid()) {
            return scrollAndroidToText(textLabel);
        } else {
            // iOS: XPath scroll fallback (use mobile:scroll with container for production)
            for (int i = 0; i < 10; i++) {
                try {
                    WebElement el = DriverManager.getDriver()
                            .findElement(By.xpath("//*[@label='" + textLabel +
                                    "' or @name='" + textLabel + "' or @value='" + textLabel + "']"));
                    if (el.isDisplayed()) return el;
                } catch (Exception ignored) {}
                swipeUpGesture();
            }
            return DriverManager.getDriver()
                    .findElement(By.xpath("//*[@label='" + textLabel + "']"));
        }
    }

    // ── Private Helpers ────────────────────────────────────────────────────────

    /**
     * Executes a W3C touch swipe action from start to end coordinates.
     *
     * @param startX     starting X pixel
     * @param startY     starting Y pixel
     * @param endX       ending X pixel
     * @param endY       ending Y pixel
     * @param durationMs swipe speed in milliseconds (slower = more reliable scroll)
     */
    private static void performSwipe(int startX, int startY, int endX, int endY, long durationMs) {
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
}
