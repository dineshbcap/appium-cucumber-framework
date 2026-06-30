package com.appium.tests.stepdefs;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import com.appium.framework.pages.controls.LocatorStrategyPage;
import io.appium.java_client.AppiumBy;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

// RelativeLocator step definitions are at the bottom of this class

/**
 * Step definitions for Appium Locator Strategies feature.
 *
 * <p>Covers: By.id, accessibilityId, className, xpath, UiAutomator (Android),
 * NSPredicateString (iOS), ClassChain (iOS), and PageFactory annotations.</p>
 *
 * <p><b>Concept demonstrated:</b> Each step uses a different {@link By} or
 * {@link AppiumBy} locator strategy, logging the strategy name for educational clarity.</p>
 */
public class LocatorStrategyStepDefs {

    private static final Logger log = LogManager.getLogger(LocatorStrategyStepDefs.class);
    private final LocatorStrategyPage page = new LocatorStrategyPage();

    /** Holds the last element found — reused in Then steps. */
    private WebElement lastFoundElement;
    private List<WebElement> lastFoundElements;

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("the app main screen is loaded for locator strategy tests")
    public void appMainScreenLoaded() {
        log.info("App main screen should be visible for locator strategy tests");
        // ApiDemos starts on the main list — no navigation needed
    }

    // ── When ──────────────────────────────────────────────────────────────────

    @When("the user finds element by accessibility ID {string}")
    public void findByAccessibilityId(String id) {
        log.info("[Strategy: accessibilityId] '{}'", id);
        lastFoundElement = page.findByAccessibilityId(id);
    }

    @When("the user finds element by ID {string}")
    public void findById(String resourceId) {
        log.info("[Strategy: By.id] '{}'", resourceId);
        lastFoundElement = page.findById(resourceId);
    }

    @When("the user finds all elements by class name {string}")
    public void findAllByClassName(String className) {
        log.info("[Strategy: By.className (all)] '{}'", className);
        lastFoundElements = page.findAllByClassName(className);
    }

    @When("the user finds element by XPath {string}")
    public void findByXpath(String xpath) {
        log.info("[Strategy: By.xpath] '{}'", xpath);
        lastFoundElement = page.findByXpath(xpath);
    }

    @When("the user finds element by visible text {string}")
    public void findByVisibleText(String text) {
        log.info("[Strategy: XPath text] '{}'", text);
        lastFoundElement = page.findByText(text);
    }

    @When("the user finds Android element by UiSelector {string}")
    public void findByUiSelector(String uiSelectorExpression) {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping UiSelector — not Android");
            return;
        }
        log.info("[Strategy: androidUIAutomator] '{}'", uiSelectorExpression);
        lastFoundElement = page.findByUiAutomator(uiSelectorExpression);
    }

    @When("the user finds iOS element by predicate string {string}")
    public void findByPredicateString(String predicateString) {
        if (!ConfigReader.isIOS()) {
            log.info("Skipping NSPredicate — not iOS");
            return;
        }
        log.info("[Strategy: iOSNsPredicateString] '{}'", predicateString);
        lastFoundElement = page.findByIosPredicateString(predicateString);
    }

    @When("the user finds iOS element by class chain {string}")
    public void findByClassChain(String classChain) {
        if (!ConfigReader.isIOS()) {
            log.info("Skipping Class Chain — not iOS");
            return;
        }
        log.info("[Strategy: iOSClassChain] '{}'", classChain);
        lastFoundElement = page.findByIosClassChain(classChain);
    }

    @When("the user clicks the text nav item via PageFactory")
    public void clickTextNavViaPageFactory() {
        log.info("[Strategy: PageFactory @AndroidFindBy/@iOSXCUITFindBy]");
        page.clickTextNavItem();
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the element should be found and visible")
    public void elementShouldBeFoundAndVisible() {
        Assertions.assertThat(lastFoundElement)
                .as("Element should have been found")
                .isNotNull();
        Assertions.assertThat(lastFoundElement.isDisplayed())
                .as("Found element should be visible")
                .isTrue();
        log.info("Element found and visible: tag={}, text='{}'",
                lastFoundElement.getTagName(),
                lastFoundElement.getText());
    }

    @Then("at least {int} element should be found")
    public void atLeastElementsFound(int minimum) {
        int count = lastFoundElements != null ? lastFoundElements.size() : 0;
        log.info("Elements found: {}", count);
        Assertions.assertThat(count)
                .as("At least %d elements should be found", minimum)
                .isGreaterThanOrEqualTo(minimum);
    }

    @Then("the app should navigate successfully")
    public void appShouldNavigateSuccessfully() {
        // Just verify app is still alive after navigation
        Assertions.assertThat(DriverManager.getDriver().getPageSource())
                .as("App page source should not be empty after navigation")
                .isNotEmpty();
    }

    // ── Relative Locator step definitions (Selenium 4 / Appium 2.x) ──────────
    // RelativeLocator.with() finds elements by their visual position relative
    // to a known anchor element. Appium 2.x supports this via W3C protocol.

    @When("the user finds element below the anchor with text {string}")
    public void findElementBelowAnchor(String anchorText) {
        log.info("[Strategy: RelativeLocator.below] anchor text='{}'", anchorText);
        // Find the first TextView/element below the anchor text label
        By anchorLocator = By.xpath(
                String.format("//*[@text='%s' or @label='%s']", anchorText, anchorText));
        By targetBy = By.className(
                ConfigReader.isAndroid() ? "android.widget.TextView" : "XCUIElementTypeStaticText");
        lastFoundElement = page.findBelow(anchorLocator, targetBy);
    }

    @When("the user finds element near the anchor with text {string} within {int} pixels")
    public void findElementNearAnchor(String anchorText, int maxDistancePx) {
        log.info("[Strategy: RelativeLocator.near] anchor='{}', maxDist={}px",
                anchorText, maxDistancePx);
        By anchorLocator = By.xpath(
                String.format("//*[@text='%s' or @label='%s']", anchorText, anchorText));
        By targetBy = By.className(
                ConfigReader.isAndroid() ? "android.widget.TextView" : "XCUIElementTypeStaticText");
        lastFoundElement = page.findNear(anchorLocator, targetBy, maxDistancePx);
    }

    @When("the user finds element between anchors {string} and {string}")
    public void findElementBetweenAnchors(String topAnchorText, String bottomAnchorText) {
        log.info("[Strategy: RelativeLocator.chained] between '{}' and '{}'",
                topAnchorText, bottomAnchorText);
        By topAnchor = By.xpath(
                String.format("//*[@text='%s' or @label='%s']", topAnchorText, topAnchorText));
        By bottomAnchor = By.xpath(
                String.format("//*[@text='%s' or @label='%s']", bottomAnchorText, bottomAnchorText));
        By targetBy = By.className(
                ConfigReader.isAndroid() ? "android.widget.TextView" : "XCUIElementTypeStaticText");
        lastFoundElement = page.findBetween(topAnchor, bottomAnchor, targetBy);
    }
}
