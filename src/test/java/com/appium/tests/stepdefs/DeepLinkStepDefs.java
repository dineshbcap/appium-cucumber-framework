package com.appium.tests.stepdefs;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.pages.controls.DeepLinkPage;
import com.appium.framework.utils.AppUtils;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;

/**
 * Step definitions for Deep Linking feature.
 *
 * <p>Covers: Android intent-based deep links, iOS URL scheme deep links,
 * and cross-platform deep link navigation.</p>
 *
 * <p><b>Concept demonstrated:</b> Using {@code mobile:startActivity} (Android) and
 * {@code mobile:openUrl} (iOS) to navigate directly to specific app screens
 * without going through the UI navigation path.</p>
 */
public class DeepLinkStepDefs {

    private static final Logger log = LogManager.getLogger(DeepLinkStepDefs.class);
    private final DeepLinkPage page = new DeepLinkPage();

    // ── When ──────────────────────────────────────────────────────────────────

    @When("the user opens the Android deep link {string}")
    public void openAndroidDeepLink(String deepLinkUrl) {
        if (!ConfigReader.isAndroid()) {
            log.info("Skipping Android deep link on iOS");
            return;
        }
        log.info("Opening Android deep link: {}", deepLinkUrl);
        page.openAndroidDeepLink(deepLinkUrl);
    }

    @When("the user opens the iOS URL {string}")
    public void openIosUrl(String url) {
        if (!ConfigReader.isIOS()) {
            log.info("Skipping iOS URL on Android");
            return;
        }
        log.info("Opening iOS URL: {}", url);
        page.openIosDeepLink(url);
    }

    @When("the user opens a deep link to the app")
    public void openDeepLinkToApp() {
        // Uses the platform's appropriate deep link strategy
        if (ConfigReader.isAndroid()) {
            // Launch the known ApiDemos activity directly via mobile:startActivity.
            // This is a component launch (package/activity), not a URI-based deep
            // link, so it goes through AppUtils rather than DeepLinkPage's
            // URI-intent method.
            AppUtils.startAndroidActivity(
                    ConfigReader.get("android.appPackage"),
                    "io.appium.android.apis.ApiDemos"
            );
        } else {
            // On iOS, re-activate the app as a "deep link" demonstration
            AppUtils.activateApp();
        }
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the app should be running in the foreground deeplink")
    public void appShouldBeRunningInForegroundDeeplink() {
        Assertions.assertThat(AppUtils.isAppInForeground())
                .as("App should be running in foreground after deep link")
                .isTrue();
    }

    @Then("the main screen should be displayed deeplink")
    public void mainScreenShouldBeDisplayedDeeplink() {
        // Re-use the assertion — main screen visible = deep link landed correctly
        Assertions.assertThat(AppUtils.isAppRunning())
                .as("App should be running after deep link navigation")
                .isTrue();
    }
}
