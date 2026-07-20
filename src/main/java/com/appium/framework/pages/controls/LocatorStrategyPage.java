package com.appium.framework.pages.controls;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import com.appium.framework.pages.BasePage;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.locators.RelativeLocator;

import java.util.List;

/**
 * Page object dedicated to demonstrating ALL Appium locator strategies.
 *
 * <p><b>Concept covered:</b> Locating elements is the foundation of every Appium test.
 * Choosing the wrong strategy leads to flaky, slow, or unmaintainable tests.
 * This page object demonstrates each strategy, explains when to use it, and shows
 * the trade-offs between them.</p>
 *
 * <hr>
 *
 * <h2>Locator Strategy Reference</h2>
 *
 * <h3>1. By.id (resource-id on Android, value on iOS)</h3>
 * <pre>By.id("io.appium.android.apis:id/text1")  // Android resource-id
 * By.id("submitButton")                       // iOS accessibility id value</pre>
 * Best for: elements with a stable, unique resource ID. Fastest on Android.
 *
 * <h3>2. AppiumBy.accessibilityId</h3>
 * <pre>AppiumBy.accessibilityId("Submit")  // content-desc on Android, accessibilityId on iOS</pre>
 * Best for: elements shared between Android and iOS — the single most portable strategy.
 * On Android maps to {@code content-desc}; on iOS maps to the {@code accessibilityLabel}.
 *
 * <h3>3. By.className</h3>
 * <pre>By.className("android.widget.Button")     // Android widget class
 * By.className("XCUIElementTypeButton")       // iOS element type</pre>
 * Best for: finding all instances of a UI component type (e.g., all buttons).
 * Avoid for specific elements — too broad, fragile if layout changes.
 *
 * <h3>4. By.xpath</h3>
 * <pre>By.xpath("//android.widget.TextView[@text='Submit']")  // Android
 * By.xpath("//XCUIElementTypeButton[@name='Submit']")     // iOS</pre>
 * Most flexible but slowest. Avoid deep XPath; use short, stable expressions.
 *
 * <h3>5. AppiumBy.androidUIAutomator (Android only)</h3>
 * <pre>AppiumBy.androidUIAutomator("new UiSelector().text(\"Submit\")")</pre>
 * Best for: Android scroll-to, complex queries, UiScrollable. Fast native Android API.
 *
 * <h3>6. AppiumBy.iOSNsPredicateString (iOS only)</h3>
 * <pre>AppiumBy.iOSNsPredicateString("name == 'Submit' AND value CONTAINS 'button'")</pre>
 * Best for: iOS compound queries. Faster than XPath, supports NSPredicate syntax.
 *
 * <h3>7. AppiumBy.iOSClassChain (iOS only)</h3>
 * Best for: iOS hierarchical queries. More readable than XPath, faster too.
 * Example: AppiumBy.iOSClassChain("XCUIElementTypeButton[name == Submit]")
 *
 * <h3>8. AppiumBy.image (image-based, advanced)</h3>
 * <pre>AppiumBy.image(base64TemplateImage)</pre>
 * Best for: elements with no text or ID (CAPTCHA images, chart elements, custom widgets).
 * Requires the Appium Images plugin and is sensitive to screen resolution.
 *
 * <h3>9. PageFactory @AndroidFindBy / @iOSXCUITFindBy (advanced)</h3>
 * Declarative locators on class fields. Evaluated lazily on first access.
 * Best for: Page Object Model where element definitions live at the field level.
 *
 * <h3>10. AppiumBy.custom (custom locator plugins)</h3>
 * For teams with custom Appium plugins that expose their own locator engines.
 */
public class LocatorStrategyPage extends BasePage {

    // ── 9. Dual-Platform Locators via properties ──────────────────────────────
    // These demonstrate the same "one field, two platforms" idea PageFactory's
    // @AndroidFindBy/@iOSXCUITFindBy annotations provided, but resolved from
    // locators_android.properties / locators_ios.properties instead — see the
    // "locatorStrategy.*" keys. locator(key) picks the right strategy/value pair
    // for the current platform at call time.

    // ── Actions using various locator strategies ───────────────────────────────

    /**
     * Finds and returns an element using <b>By.id</b> (resource-id on Android).
     *
     * @param resourceId full resource ID (e.g., "io.appium.android.apis:id/text1")
     * @return found element
     */
    public WebElement findById(String resourceId) {
        log.info("[Strategy: By.id] Finding: {}", resourceId);
        return DriverManager.getDriver().findElement(By.id(resourceId));
    }

    /**
     * Finds and returns an element using <b>AppiumBy.accessibilityId</b>.
     * This is the most portable strategy — works identically on Android and iOS.
     *
     * @param accessibilityId the content-description (Android) or accessibilityLabel (iOS)
     * @return found element
     */
    public WebElement findByAccessibilityId(String accessibilityId) {
        log.info("[Strategy: accessibilityId] Finding: {}", accessibilityId);
        return DriverManager.getDriver().findElement(AppiumBy.accessibilityId(accessibilityId));
    }

    /**
     * Finds and returns an element using <b>By.className</b>.
     * Returns the first element of that class type.
     *
     * @param className Android widget class or iOS element type
     * @return first found element of that class
     */
    public WebElement findByClassName(String className) {
        log.info("[Strategy: By.className] Finding: {}", className);
        return DriverManager.getDriver().findElement(By.className(className));
    }

    /**
     * Returns all elements of a given class name.
     *
     * @param className Android widget class or iOS element type
     * @return list of matching elements (may be empty)
     */
    public List<WebElement> findAllByClassName(String className) {
        log.info("[Strategy: By.className - all] Finding all: {}", className);
        return DriverManager.getDriver().findElements(By.className(className));
    }

    /**
     * Finds an element using <b>By.xpath</b>.
     *
     * <p>XPath tips:
     * <ul>
     *   <li>Use short, relative XPaths — avoid starting with {@code /html/body/...}</li>
     *   <li>Prefer {@code @text} or {@code @content-desc} over positional indexes</li>
     *   <li>Compound: {@code //*[@text='OK' or @name='OK']} works cross-platform</li>
     * </ul>
     * </p>
     *
     * @param xpath XPath expression
     * @return found element
     */
    public WebElement findByXpath(String xpath) {
        log.info("[Strategy: By.xpath] Finding: {}", xpath);
        return DriverManager.getDriver().findElement(By.xpath(xpath));
    }

    /**
     * Finds an element by its visible text using a cross-platform XPath.
     * Works on both Android ({@code @text}) and iOS ({@code @label}, {@code @name}).
     *
     * @param text the exact visible text of the element
     * @return found element
     */
    public WebElement findByText(String text) {
        log.info("[Strategy: XPath text] Finding element with text: '{}'", text);
        // Cross-platform: @text for Android, @label/@name for iOS
        By locator = By.xpath(
                String.format("//*[@text='%s' or @label='%s' or @name='%s']", text, text, text));
        return DriverManager.getDriver().findElement(locator);
    }

    /**
     * Finds an element using <b>AppiumBy.androidUIAutomator</b> with a UiSelector expression.
     *
     * <p>UiSelector is Android-specific and provides the fastest, most powerful
     * query mechanism for native Android UI:
     * <ul>
     *   <li>{@code new UiSelector().text("Submit")} — exact text match</li>
     *   <li>{@code new UiSelector().textContains("Sub")} — partial text</li>
     *   <li>{@code new UiSelector().className("android.widget.Button").instance(0)} — nth button</li>
     *   <li>{@code new UiSelector().resourceId("com.example:id/btn")} — by resource-id</li>
     * </ul>
     * </p>
     *
     * @param uiSelectorExpression UiSelector Java expression as a string
     * @return found element
     */
    public WebElement findByUiAutomator(String uiSelectorExpression) {
        log.info("[Strategy: androidUIAutomator] UiSelector: {}", uiSelectorExpression);
        if (!ConfigReader.isAndroid()) {
            throw new UnsupportedOperationException("UiAutomator2 is Android-only");
        }
        return DriverManager.getDriver()
                .findElement(AppiumBy.androidUIAutomator(uiSelectorExpression));
    }

    /**
     * Finds an element using <b>AppiumBy.iOSNsPredicateString</b>.
     *
     * <p>NSPredicate syntax (iOS only) is fast and supports compound expressions:
     * <ul>
     *   <li>{@code "name == 'Submit'"} — exact name match</li>
     *   <li>{@code "name CONTAINS 'Sub'"} — partial match</li>
     *   <li>{@code "type == 'XCUIElementTypeButton' AND visible == true"} — multiple conditions</li>
     *   <li>{@code "label BEGINSWITH 'Log'"} — starts-with pattern</li>
     *   <li>{@code "enabled == true AND name != 'Cancel'"} — negation</li>
     * </ul>
     * </p>
     *
     * @param predicateString NSPredicate expression string
     * @return found element
     */
    public WebElement findByIosPredicateString(String predicateString) {
        log.info("[Strategy: iOSNsPredicateString] Predicate: {}", predicateString);
        if (!ConfigReader.isIOS()) {
            throw new UnsupportedOperationException("NSPredicate is iOS-only");
        }
        return DriverManager.getDriver()
                .findElement(AppiumBy.iOSNsPredicateString(predicateString));
    }

    /**
     * Finds an element using <b>AppiumBy.iOSClassChain</b>.
     *
     * <p>Class Chain is an iOS-specific locator that is faster than XPath and more
     * readable than NSPredicate for hierarchical queries:
     * <ul>
     *   <li>Button named Submit: {@code XCUIElementTypeButton[name == Submit]}</li>
     *   <li>2nd table cell: {@code XCUIElementTypeTable/XCUIElementTypeCell[2]}</li>
     *   <li>Any nav bar button: {@code XCUIElementTypeNavigationBar/XCUIElementTypeButton}</li>
     * </ul>
     * </p>
     *
     * @param classChainExpression Class Chain expression string
     * @return found element
     */
    public WebElement findByIosClassChain(String classChainExpression) {
        log.info("[Strategy: iOSClassChain] Chain: {}", classChainExpression);
        if (!ConfigReader.isIOS()) {
            throw new UnsupportedOperationException("Class Chain is iOS-only");
        }
        return DriverManager.getDriver()
                .findElement(AppiumBy.iOSClassChain(classChainExpression));
    }

    // ── Properties-Resolved Fields (strategy 9) ───────────────────────────────

    /**
     * Demonstrates clicking an element located via a dual-platform properties key.
     * The correct strategy/value ({@code locatorStrategy.textNavItem} in
     * locators_android.properties vs. locators_ios.properties) is resolved
     * automatically by {@link com.appium.framework.healing.HealingSupport}.
     */
    public void clickTextNavItem() {
        log.info("[Strategy: properties-resolved] Clicking text nav item");
        click("locatorStrategy.textNavItem");
    }

    /**
     * Returns the text of the first list item found via a dual-platform XPath key.
     *
     * @return text of the first list item
     */
    public String getFirstListItemText() {
        return getText("locatorStrategy.firstListItem");
    }

    // ── Attribute Reading (bonus: element inspection) ─────────────────────────

    /**
     * Reads any attribute from an element — useful for debugging locator issues
     * and verifying element state properties.
     *
     * <p>Common attributes:
     * <ul>
     *   <li>Android: {@code text}, {@code content-desc}, {@code resource-id}, {@code enabled},
     *       {@code checked}, {@code focused}, {@code clickable}, {@code bounds}</li>
     *   <li>iOS: {@code name}, {@code label}, {@code value}, {@code type}, {@code enabled},
     *       {@code visible}, {@code frame}</li>
     * </ul>
     * </p>
     *
     * @param element       the element to inspect
     * @param attributeName attribute name to read
     * @return attribute value string, or null if not present
     */
    public String getAttribute(WebElement element, String attributeName) {
        String value = element.getAttribute(attributeName);
        log.info("Element attribute '{}' = '{}'", attributeName, value);
        return value;
    }

    /**
     * Returns the bounds rectangle string of an Android element.
     * Android element bounds are in the format {@code [left,top][right,bottom]}.
     *
     * @param element the element to inspect
     * @return bounds string
     */
    public String getAndroidBounds(WebElement element) {
        return getAttribute(element, "bounds");
    }

    /**
     * Returns the frame (position and size) of an iOS element.
     * iOS frame is in the format {@code {{x, y}, {width, height}}}.
     *
     * @param element the element to inspect
     * @return frame string
     */
    public String getIosFrame(WebElement element) {
        return getAttribute(element, "frame");
    }

    // ── 10. Relative Locators (Selenium 4 / Appium 2.x) ──────────────────────

    /**
     * Finds an element <b>above</b> the anchor element using Selenium 4's
     * {@link RelativeLocator}.
     *
     * <p><b>Concept: Relative Locators</b><br>
     * Selenium 4 introduced relative locators (also called Friendly Locators) that find
     * elements based on their visual position relative to another element on screen.
     * Appium 2.x supports these via the W3C WebDriver protocol.</p>
     *
     * <p><b>How it works:</b> The WebDriver server evaluates the positions of all matching
     * elements and returns those that are spatially above/below/left/right of the anchor.
     * "Above" means the bottom edge of the candidate is above the top edge of the anchor.</p>
     *
     * <p><b>Available relative directions:</b>
     * <ul>
     *   <li>{@code above(anchor)} — element whose bottom is above anchor's top</li>
     *   <li>{@code below(anchor)} — element whose top is below anchor's bottom</li>
     *   <li>{@code toLeftOf(anchor)} — element whose right edge is left of anchor's left</li>
     *   <li>{@code toRightOf(anchor)} — element whose left edge is right of anchor's right</li>
     *   <li>{@code near(anchor)} — element within 50px of the anchor (default proximity)</li>
     *   <li>{@code near(anchor, atMostDistanceInPixels)} — element within custom distance</li>
     * </ul>
     * </p>
     *
     * <p><b>Usage note:</b> Relative locators work best for stable UI layouts.
     * On mobile, they are most useful for finding unlabeled elements (e.g., icons)
     * that appear in a fixed position relative to a labeled anchor element.</p>
     *
     * @param anchorLocator locator for the reference element (the anchor)
     * @param targetBy      the {@link By} type of element to find above the anchor
     *                      (e.g., {@code By.className("android.widget.TextView")})
     * @return the first matching element above the anchor
     */
    public WebElement findAbove(By anchorLocator, By targetBy) {
        log.info("[Strategy: RelativeLocator.above] anchor={}, target={}", anchorLocator, targetBy);
        WebElement anchor = DriverManager.getDriver().findElement(anchorLocator);
        return DriverManager.getDriver().findElement(
                RelativeLocator.with(targetBy).above(anchor));
    }

    /**
     * Finds an element <b>below</b> the anchor element.
     *
     * @param anchorLocator locator for the reference anchor element
     * @param targetBy      the element type to find below the anchor
     * @return the first matching element below the anchor
     */
    public WebElement findBelow(By anchorLocator, By targetBy) {
        log.info("[Strategy: RelativeLocator.below] anchor={}, target={}", anchorLocator, targetBy);
        WebElement anchor = DriverManager.getDriver().findElement(anchorLocator);
        return DriverManager.getDriver().findElement(
                RelativeLocator.with(targetBy).below(anchor));
    }

    /**
     * Finds an element to the <b>left</b> of the anchor element.
     *
     * @param anchorLocator locator for the reference anchor element
     * @param targetBy      the element type to find left of the anchor
     * @return the first matching element left of the anchor
     */
    public WebElement findToLeftOf(By anchorLocator, By targetBy) {
        log.info("[Strategy: RelativeLocator.toLeftOf] anchor={}, target={}", anchorLocator, targetBy);
        WebElement anchor = DriverManager.getDriver().findElement(anchorLocator);
        return DriverManager.getDriver().findElement(
                RelativeLocator.with(targetBy).toLeftOf(anchor));
    }

    /**
     * Finds an element to the <b>right</b> of the anchor element.
     *
     * @param anchorLocator locator for the reference anchor element
     * @param targetBy      the element type to find right of the anchor
     * @return the first matching element right of the anchor
     */
    public WebElement findToRightOf(By anchorLocator, By targetBy) {
        log.info("[Strategy: RelativeLocator.toRightOf] anchor={}, target={}", anchorLocator, targetBy);
        WebElement anchor = DriverManager.getDriver().findElement(anchorLocator);
        return DriverManager.getDriver().findElement(
                RelativeLocator.with(targetBy).toRightOf(anchor));
    }

    /**
     * Finds an element <b>near</b> the anchor element within a given pixel distance.
     *
     * <p>"Near" uses the center-to-center distance between the anchor and candidate
     * elements. The default proximity in Selenium's implementation is 50 pixels.</p>
     *
     * @param anchorLocator      locator for the reference anchor element
     * @param targetBy           the element type to find near the anchor
     * @param maxDistancePixels  maximum pixel distance between element centers
     * @return the first matching element within the given proximity
     */
    public WebElement findNear(By anchorLocator, By targetBy, int maxDistancePixels) {
        log.info("[Strategy: RelativeLocator.near] anchor={}, target={}, maxDist={}px",
                anchorLocator, targetBy, maxDistancePixels);
        WebElement anchor = DriverManager.getDriver().findElement(anchorLocator);
        return DriverManager.getDriver().findElement(
                RelativeLocator.with(targetBy).near(anchor, maxDistancePixels));
    }

    /**
     * Chains multiple relative conditions — finds an element <b>below</b> one anchor
     * and <b>above</b> another anchor simultaneously.
     *
     * <p>Chaining is useful when a single direction isn't unique enough —
     * e.g., find the element that is below a header AND above a footer.</p>
     *
     * @param upperAnchorLocator locator for the element the target must be below
     * @param lowerAnchorLocator locator for the element the target must be above
     * @param targetBy           the element type to find
     * @return element positioned between the two anchors
     */
    public WebElement findBetween(By upperAnchorLocator, By lowerAnchorLocator, By targetBy) {
        log.info("[Strategy: RelativeLocator.chained] between two anchors, target={}", targetBy);
        WebElement upperAnchor = DriverManager.getDriver().findElement(upperAnchorLocator);
        WebElement lowerAnchor = DriverManager.getDriver().findElement(lowerAnchorLocator);
        return DriverManager.getDriver().findElement(
                RelativeLocator.with(targetBy).below(upperAnchor).above(lowerAnchor));
    }
}
