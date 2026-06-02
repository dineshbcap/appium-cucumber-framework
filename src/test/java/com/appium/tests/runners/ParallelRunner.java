package com.appium.tests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Parallel Cucumber runner — each scenario runs on its own thread simultaneously.
 *
 * <p><b>Concept covered: Parallel Execution</b><br>
 * Mobile test suites can be dramatically faster when scenarios run simultaneously
 * across multiple emulators or real devices. This runner enables that by:
 * <ol>
 *   <li>Overriding {@link #scenarios()} with {@code @DataProvider(parallel = true)}</li>
 *   <li>Setting {@code data-provider-thread-count} in {@code testng-parallel.xml}</li>
 *   <li>Using {@link com.appium.framework.driver.DriverManager} with {@link ThreadLocal}
 *       to give each thread its own isolated Appium driver session</li>
 * </ol>
 * </p>
 *
 * <p><b>How to run:</b>
 * <pre>
 *   mvn test                           # runs parallel suite (default)
 *   mvn test -Dtags="@smoke"           # filter by tag in parallel
 *   mvn test -Dplatform=ios            # run on iOS in parallel
 *   mvn test -Dparallel.thread.count=2 # override thread count (must match device count)
 * </pre>
 * </p>
 *
 * <p><b>Plugins included:</b>
 * <ul>
 *   <li>{@code pretty} — human-readable console output</li>
 *   <li>{@code html} — Cucumber HTML report with step-level details</li>
 *   <li>{@code json} — machine-readable JSON for third-party reporting tools</li>
 *   <li>{@code junit} — JUnit XML for CI system test result parsing</li>
 *   <li>{@code ExtentCucumberAdapter} — rich HTML report with screenshots</li>
 * </ul>
 * </p>
 *
 * <p><b>Thread safety requirements:</b>
 * <ul>
 *   <li>Each scenario must create its own Appium session (done in
 *       {@link com.appium.tests.hooks.Hooks#setUp(io.cucumber.java.Scenario)})</li>
 *   <li>Page objects are instantiated per scenario (not shared static instances)</li>
 *   <li>All state is stored in {@link ThreadLocal} fields</li>
 * </ul>
 * </p>
 */
@CucumberOptions(
        features   = "src/test/resources/features",
        glue       = {"com.appium.tests.stepdefs", "com.appium.tests.hooks"},
        plugin     = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "junit:target/cucumber-reports/cucumber.xml",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true,
        dryRun     = false
)
public class ParallelRunner extends AbstractTestNGCucumberTests {

    /**
     * Overrides the default scenarios data provider to enable parallel execution.
     *
     * <p>{@code parallel = true} instructs TestNG to dispatch each scenario to a
     * thread pool rather than running them sequentially. The pool size is controlled
     * by {@code data-provider-thread-count} in {@code testng-parallel.xml}.</p>
     *
     * @return array of [scenario] objects — one row per Cucumber scenario
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
