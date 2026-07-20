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

    // Sample webview locators (webView.urlBar, webView.content, webView.backButton,
    // webView.forwardButton, webView.reloadButton) live in locators_android.properties /
    // locators_ios.properties, ready for a real hybrid screen via locator("webView.*").

    // Both AndroidDriver and IOSDriver implement SupportsContextSwitching; AppiumDriver does not
    private SupportsContextSwitching contextDriver() {
        return (SupportsContextSwitching) DriverManager.getDriver();
    }

    // ── Context switching ─────────────────────────────────────────────────────

    public void switchToWebView() {
        log.info("Switching to WebView context");
        Set<String> contexts = getContextsWithRetry();
        log.debug("Available contexts: {}", contexts);
        contexts.stream()
                .filter(c -> c.contains(WEBVIEW_KEY))
                .findFirst()
                .ifPresentOrElse(
                        ctx -> contextDriver().context(ctx),
                        () -> { throw new RuntimeException("No WebView context found"); }
                );
    }

    /**
     * Polls {@code getContextHandles()} for a WEBVIEW context to register,
     * retrying briefly — a WKWebView's JS environment initializes asynchronously
     * after the native page containing it becomes visible, so an immediate check
     * right after navigation can race ahead of it and see only NATIVE_APP.
     */
    private Set<String> getContextsWithRetry() {
        Set<String> contexts = contextDriver().getContextHandles();
        for (int i = 0; i < 5 && contexts.stream().noneMatch(c -> c.contains(WEBVIEW_KEY)); i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            contexts = contextDriver().getContextHandles();
        }
        return contexts;
    }

    public void switchToNativeApp() {
        log.info("Switching to NATIVE_APP context");
        contextDriver().context(NATIVE_APP);
    }

    public String getCurrentContext() {
        return contextDriver().getContext();
    }

    public Set<String> getAllContexts() {
        return getContextsWithRetry();
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
