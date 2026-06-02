package com.appium.tests.stepdefs;

import com.appium.framework.pages.controls.ListControlPage;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

public class ListStepDefs {

    private final ListControlPage page = new ListControlPage();

    @When("the user taps list item at index {int}")
    public void userTapsListItemAtIndex(int index) {
        page.tapItemAtIndex(index);
    }

    @When("the user taps the list item {string}")
    public void userTapsListItem(String text) {
        page.tapItemByText(text);
    }

    @When("the user scrolls to list item {string}")
    public void userScrollsToListItem(String text) {
        page.scrollToItemByText(text);
    }

    @Then("the list item at index {int} should have text {string}")
    public void listItemShouldHaveText(int index, String expected) {
        Assertions.assertThat(page.getItemTextAtIndex(index))
                .as("List item text at index " + index)
                .isEqualTo(expected);
    }

    @Then("the list should contain {int} items")
    public void listShouldContainItems(int count) {
        Assertions.assertThat(page.getItemCount())
                .as("List item count")
                .isGreaterThanOrEqualTo(count);
    }

    @Then("the list should contain item {string}")
    public void listShouldContainItem(String text) {
        Assertions.assertThat(page.isItemDisplayed(text))
                .as("List should contain: " + text)
                .isTrue();
    }

    @Then("the list result should show {string}")
    public void listResultShouldShow(String expected) {
        Assertions.assertThat(page.getResultText())
                .as("List result text")
                .contains(expected);
    }
}
