package com.appium.framework.pages;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import com.appium.framework.healing.HealingSupport;
import com.appium.framework.utils.GestureUtils;
import com.appium.framework.utils.WaitUtils;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Abstract base class for all Page Objects in this framework.
 *
 * <p><b>Concept covered: Page Object Model (POM)</b><br>
 * The POM pattern separates element locators and interactions from test logic.
 * Each screen in the app has its own page class that:
 * <ul>
 *   <li>Resolves its element locators by dotted key (e.g. {@code "button.normal"}) via
 *       {@link #el(String)} / {@link #element(String)}, backed by the
 *       {@code com.dinesh.healing} self-healing-locators library
 *       ({@link HealingSupport}) and {@code locators_android.properties / locators_ios.properties}</li>
 *   <li>Provides action methods ({@code click}, {@code typeText}) with logging</li>
 *   <li>Does NOT contain assertions — assertions belong in step definitions</li>
 * </ul>
 * This makes tests more maintainable: when a locator changes, only the properties
 * file needs updating — not the page class or any test that uses that element.
 * And when the app's UI changes out from under a locator, the self-healing layer
 * attempts to re-locate the element by its description before failing the step.</p>
 *
 * <p><b>Logger:</b> Each subclass gets its own {@code log} instance because
 * {@code LogManager.getLogger(getClass())} uses the actual subclass type.
 * This means log messages identify the specific page (e.g., "ButtonControlPage") not "BasePage".</p>
 */
public abstract class BasePage {

    /**
     * Logger using the actual subclass type for accurate log output.
     * Declared {@code protected} so subclasses can use it directly.
     */
    protected final Logger log = LogManager.getLogger(getClass());

    // ── Driver Access ──────────────────────────────────────────────────────────

    /**
     * Returns the current thread's Appium driver.
     * Use this for any operation not covered by the helper methods below.
     *
     * @return current {@link AppiumDriver}
     */
    protected AppiumDriver driver() {
        return DriverManager.getDriver();
    }

    // ── Locator Resolution (self-healing) ──────────────────────────────────────

    /**
     * Resolves a locator key to its element via the self-healing locator layer.
     *
     * <p>Tries the declared {@code strategy=value} locator from
     * {@code locators_android.properties / locators_ios.properties} first; if the app's UI has moved on and that
     * locator no longer matches anything, {@link com.dinesh.healing.SelfHealingElementLocator}
     * attempts to re-locate the element from its {@code description} before giving up.</p>
     *
     * @param key dotted locator key (e.g. {@code "button.normal"})
     * @return the located (possibly healed) {@link WebElement}
     */
    protected WebElement el(String key) {
        return HealingSupport.locator().find(key);
    }

    /**
     * Alias for {@link #el(String)} — resolves a locator key to its element via the
     * self-healing locator layer. Kept as the primary name used throughout the
     * existing page classes; {@link #el(String)} is the shorter form.
     *
     * @param key dotted locator key
     * @return the located (possibly healed) {@link WebElement}
     */
    protected WebElement element(String key) {
        return el(key);
    }

    /**
     * Resolves a locator key to the raw, declared (non-healing) {@link By} for the
     * current platform. Use this for wait-conditions and other {@code By}-based
     * helpers below — self-healing applies to single-element {@link #el(String)}
     * lookups, not raw locator resolution.
     *
     * @param key dotted locator key
     * @return declared {@link By} for the current platform
     */
    protected By locator(String key) {
        return HealingSupport.rawLocator(key);
    }

    /**
     * Resolves a locator key and returns all currently matching elements (no wait,
     * no healing — the self-healing layer only covers single-element lookups).
     * Shorthand for {@code findElements(locator(key))}.
     *
     * @param key dotted locator key
     * @return list of matching elements (may be empty)
     */
    protected List<WebElement> elements(String key) {
        return findElements(locator(key));
    }

    /**
     * Returns the plain-English description of a locator key from
     * {@code locators_android.properties / locators_ios.properties}. Useful in failure logs and is also what the
     * self-healing layer itself uses to re-locate an element once its declared
     * locator stops matching.
     *
     * @param key dotted locator key
     * @return description text
     */
    protected String describe(String key) {
        return HealingSupport.describe(key);
    }

    // ── Element Location ───────────────────────────────────────────────────────

    /**
     * Waits for the element to be visible and returns it.
     * Preferred over {@code driver().findElement()} because it includes an explicit wait.
     *
     * @param locator element locator
     * @return visible {@link WebElement}
     */
    protected WebElement findElement(By locator) {
        return WaitUtils.waitForVisible(locator);
    }

    /**
     * Returns all elements matching the locator (no wait — returns immediately).
     * Returns an empty list if no elements match, never throws.
     *
     * @param locator element locator
     * @return list of matching elements (may be empty)
     */
    protected List<WebElement> findElements(By locator) {
        return driver().findElements(locator);
    }

    // ── Core Actions ───────────────────────────────────────────────────────────

    /**
     * Waits for the element to be clickable and clicks it.
     * "Clickable" means visible AND enabled — more robust than just visible.
     *
     * @param locator element locator
     */
    protected void click(By locator) {
        log.debug("Click: {}", locator);
        WaitUtils.waitForClickable(locator).click();
    }

    /**
     * Resolves a locator key (self-healing) and clicks the matching element.
     * Shorthand for {@code el(key).click()}.
     *
     * @param key dotted locator key
     */
    protected void click(String key) {
        log.debug("Click (self-healing): {}", key);
        el(key).click();
    }

    /**
     * Clicks a pre-found {@link WebElement} directly.
     * Use this with PageFactory fields to avoid redundant waits.
     *
     * @param element the element to click
     */
    protected void click(WebElement element) {
        log.debug("Click element");
        element.click();
    }

    /**
     * Clears the field and types the given text.
     * Uses an explicit wait so the field is visible before typing begins.
     *
     * @param locator element locator (must be an input field)
     * @param text    text to enter
     */
    protected void sendKeys(By locator, String text) {
        log.debug("Type '{}' into: {}", text, locator);
        WebElement el = WaitUtils.waitForVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    /**
     * Resolves a locator key (self-healing) and types text into the matching element.
     *
     * @param key  dotted locator key
     * @param text text to enter
     */
    protected void sendKeys(String key, String text) {
        log.debug("Type '{}' into (self-healing): {}", text, key);
        WebElement el = el(key);
        el.clear();
        el.sendKeys(text);
    }

    /**
     * Returns the visible text of an element.
     *
     * @param locator element locator
     * @return trimmed text content
     */
    protected String getText(By locator) {
        return WaitUtils.waitForVisible(locator).getText();
    }

    /**
     * Resolves a locator key (self-healing) and returns the visible text of the
     * matching element.
     *
     * @param key dotted locator key
     * @return trimmed text content
     */
    protected String getText(String key) {
        return el(key).getText();
    }

    // ── State Checks ───────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the element is currently displayed.
     * Returns {@code false} without throwing if the element is not in the DOM.
     *
     * @param locator element locator
     * @return {@code true} if displayed
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver().findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Resolves a locator key and returns whether the matching element is displayed.
     * Shorthand for {@code HealingSupport.locator().isPresent(key)} — a plain presence
     * check against the declared locator; intentionally does not trigger healing
     * (matches the self-healing library's own {@code isPresent} semantics: "is X
     * currently on screen", not "give me X no matter what").
     *
     * @param key dotted locator key
     * @return {@code true} if displayed
     */
    protected boolean isDisplayed(String key) {
        try {
            return HealingSupport.locator().isPresent(key);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns {@code true} if the element is enabled (interactive).
     *
     * @param locator element locator
     * @return {@code true} if enabled
     */
    protected boolean isEnabled(By locator) {
        try {
            return driver().findElement(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Resolves a locator key (self-healing) and returns whether the matching
     * element is enabled.
     *
     * @param key dotted locator key
     * @return {@code true} if enabled
     */
    protected boolean isEnabled(String key) {
        try {
            return el(key).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns {@code true} if the element is in a selected/checked state.
     * Applies to checkboxes, radio buttons, and toggles.
     *
     * @param locator element locator
     * @return {@code true} if selected/checked
     */
    protected boolean isSelected(By locator) {
        try {
            return driver().findElement(locator).isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Resolves a locator key (self-healing) and returns whether the matching
     * element is selected/checked.
     *
     * @param key dotted locator key
     * @return {@code true} if selected/checked
     */
    protected boolean isSelected(String key) {
        try {
            return el(key).isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Scroll ─────────────────────────────────────────────────────────────────

    /**
     * Scrolls down until the element is visible, up to 5 swipe attempts.
     *
     * <p>For better performance on Android, prefer
     * {@link com.appium.framework.utils.ScrollUtils#scrollAndroidToText(String)}
     * which uses UiScrollable — much faster than gesture-based scrolling.</p>
     *
     * @param locator locator of the element to scroll into view
     */
    protected void scrollToElement(By locator) {
        for (int i = 0; i < 5; i++) {
            if (isDisplayed(locator)) return;
            GestureUtils.swipeUp();
        }
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    /**
     * Navigates back using the system back action.
     * On Android: presses the hardware/software Back button.
     * On iOS: triggers the native back navigation gesture.
     */
    protected void navigateBack() {
        log.debug("Navigating back");
        driver().navigate().back();
    }

    /**
     * Returns the current platform from config (for platform-conditional logic in pages).
     *
     * @return "android" or "ios"
     */
    protected String getPlatform() {
        return ConfigReader.getPlatform();
    }

    /**
     * Returns {@code true} if running on Android.
     *
     * @return {@code true} for Android
     */
    protected boolean isAndroid() {
        return ConfigReader.isAndroid();
    }

    /**
     * Returns {@code true} if running on iOS.
     *
     * @return {@code true} for iOS
     */
    protected boolean isIOS() {
        return ConfigReader.isIOS();
    }
}
