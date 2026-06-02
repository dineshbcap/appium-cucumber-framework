package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.ScrollControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class ScrollStepDefs {

    private final ScrollControlPage page = new ScrollControlPage();

    @When("the user scrolls up")
    public void userScrollsUp() {
        page.scrollUp();
    }

    @When("the user scrolls down")
    public void userScrollsDown() {
        page.scrollDown();
    }

    @When("the user scrolls left")
    public void userScrollsLeft() {
        page.scrollLeft();
    }

    @When("the user scrolls right")
    public void userScrollsRight() {
        page.scrollRight();
    }

    @When("the user scrolls to the bottom of the list")
    public void userScrollsToBottom() {
        page.scrollToBottom();
    }

    @When("the user scrolls to the top of the list")
    public void userScrollsToTop() {
        page.scrollToTop();
    }

    @When("the user scrolls to the element with text {string}")
    public void userScrollsToElement(String text) {
        page.scrollToElementWithText(text);
    }

    @Then("the bottom of the list should be visible")
    public void bottomOfListShouldBeVisible() {
        Assertions.assertThat(page.isBottomTextVisible())
                .as("Bottom of list should be visible after scrolling down")
                .isTrue();
    }

    @Then("the top of the list should be visible")
    public void topOfListShouldBeVisible() {
        Assertions.assertThat(page.isTopTextVisible())
                .as("Top of list should be visible after scrolling up")
                .isTrue();
    }
}
