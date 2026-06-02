package com.appium.framework.pages.controls;

import com.appium.framework.driver.DriverManager;
import com.appium.framework.pages.BasePage;
import io.appium.java_client.remote.SupportsContextSwitching;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Set;

public class WebViewControlPage extends BasePage {

    private static final String NATIVE_APP  = "NATIVE_APP";
    private static final String WEBVIEW_KEY = "WEBVIEW";

    private static final By WEB_URL_BAR = By.id("url_bar");
    private static final By WEB_CONTENT = By.id("webview_content");
    private static final By BACK_BUTTON = By.id("io.appium.android.apis:id/back_button");
    private static final By FORWARD_BTN = By.id("io.appium.android.apis:id/forward_button");
    private static final By RELOAD_BTN  = By.id("io.appium.android.apis:id/reload_button");

    // Both AndroidDriver and IOSDriver implement SupportsContextSwitching; AppiumDriver does not
    private SupportsContextSwitching contextDriver() {
        return (SupportsContextSwitching) DriverManager.getDriver();
    }

    // ── Context switching ─────────────────────────────────────────────────────

    public void switchToWebView() {
        log.info("Switching to WebView context");
        Set<String> contexts = contextDriver().getContextHandles();
        log.debug("Available contexts: {}", contexts);
        contexts.stream()
                .filter(c -> c.contains(WEBVIEW_KEY))
                .findFirst()
                .ifPresentOrElse(
                        ctx -> contextDriver().context(ctx),
                        () -> { throw new RuntimeException("No WebView context found"); }
                );
    }

    public void switchToNativeApp() {
        log.info("Switching to NATIVE_APP context");
        contextDriver().context(NATIVE_APP);
    }

    public String getCurrentContext() {
        return contextDriver().getContext();
    }

    public Set<String> getAllContexts() {
        return contextDriver().getContextHandles();
    }

    // ── WebView interactions ──────────────────────────────────────────────────

    public void navigateToUrl(String url) {
        log.info("Navigating to URL: {}", url);
        DriverManager.getDriver().navigate().to(url);
    }

    public String getPageTitle() {
        return DriverManager.getDriver().getTitle();
    }

    public String getCurrentUrl() {
        return DriverManager.getDriver().getCurrentUrl();
    }

    public void goBack() {
        log.info("WebView: navigating back");
        DriverManager.getDriver().navigate().back();
    }

    public void goForward() {
        log.info("WebView: navigating forward");
        DriverManager.getDriver().navigate().forward();
    }

    public void refresh() {
        log.info("WebView: refreshing page");
        DriverManager.getDriver().navigate().refresh();
    }

    public WebElement findWebElement(By locator) {
        return DriverManager.getDriver().findElement(locator);
    }

    public void clickWebElement(By locator) {
        log.info("Clicking web element: {}", locator);
        findWebElement(locator).click();
    }

    public void enterTextInWebField(By locator, String text) {
        log.info("Entering text '{}' in web field", text);
        WebElement el = findWebElement(locator);
        el.clear();
        el.sendKeys(text);
    }

    public String executeJavaScript(String script) {
        log.info("Executing JS: {}", script);
        Object result = DriverManager.getDriver().executeScript(script);
        return result != null ? result.toString() : null;
    }

    public boolean isInWebViewContext() {
        return !NATIVE_APP.equals(getCurrentContext());
    }
}
