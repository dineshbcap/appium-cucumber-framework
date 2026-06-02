package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.WebViewControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;

public class WebViewStepDefs {

    private final WebViewControlPage page = new WebViewControlPage();

    @When("the user switches to the WebView context")
    public void userSwitchesToWebView() {
        page.switchToWebView();
    }

    @When("the user switches to the native app context")
    public void userSwitchesToNativeApp() {
        page.switchToNativeApp();
    }

    @When("the user navigates to URL {string}")
    public void userNavigatesToUrl(String url) {
        page.navigateToUrl(url);
    }

    @When("the user goes back in the WebView")
    public void userGoesBackInWebView() {
        page.goBack();
    }

    @When("the user refreshes the WebView")
    public void userRefreshesWebView() {
        page.refresh();
    }

    @When("the user clicks the web element with id {string}")
    public void userClicksWebElementWithId(String id) {
        page.clickWebElement(By.id(id));
    }

    @When("the user enters {string} in the web field with id {string}")
    public void userEntersInWebField(String text, String id) {
        page.enterTextInWebField(By.id(id), text);
    }

    @Then("the current context should be WebView")
    public void contextShouldBeWebView() {
        Assertions.assertThat(page.isInWebViewContext())
                .as("Should be in WebView context")
                .isTrue();
    }

    @Then("the current context should be native")
    public void contextShouldBeNative() {
        Assertions.assertThat(page.isInWebViewContext())
                .as("Should be in native context")
                .isFalse();
    }

    @Then("the page title should contain {string}")
    public void pageTitleShouldContain(String expected) {
        Assertions.assertThat(page.getPageTitle())
                .as("Page title")
                .contains(expected);
    }

    @Then("the current URL should contain {string}")
    public void currentUrlShouldContain(String expected) {
        Assertions.assertThat(page.getCurrentUrl())
                .as("Current URL")
                .contains(expected);
    }

    @Then("there should be at least {int} available contexts")
    public void thereShouldBeAvailableContexts(int minCount) {
        Assertions.assertThat(page.getAllContexts().size())
                .as("Available context count")
                .isGreaterThanOrEqualTo(minCount);
    }
}
