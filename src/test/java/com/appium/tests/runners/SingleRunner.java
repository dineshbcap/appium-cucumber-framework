package com.appium.tests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Sequential (single-thread) Cucumber runner for debugging and development.
 *
 * <p><b>When to use:</b> Run this runner during feature development to quickly
 * iterate on a single device without the overhead of parallel session management.
 * Use the {@code @smoke} tag to run only the critical scenarios.</p>
 *
 * <p><b>How to run:</b>
 * <pre>
 *   mvn test -Psingle                           # runs @smoke tag on configured device
 *   mvn test -Psingle -Dtags="@gesture"         # override tags at command line
 *   mvn test -Psingle -Dplatform=ios            # override platform
 * </pre>
 * </p>
 *
 * <p><b>Concept covered:</b>
 * <ul>
 *   <li>{@link AbstractTestNGCucumberTests} is the Cucumber-TestNG bridge that generates
 *       one {@code @Test} method per scenario, allowing TestNG to manage lifecycle</li>
 *   <li>{@code features} — path to .feature files</li>
 *   <li>{@code glue} — packages where Cucumber looks for step definitions and hooks</li>
 *   <li>{@code plugin} — reporting plugins: pretty (console), html, json</li>
 *   <li>{@code tags} — Cucumber tag expression to filter which scenarios run</li>
 *   <li>{@code monochrome} — strips ANSI color codes from console output</li>
 * </ul>
 * </p>
 */
@CucumberOptions(
        features   = "src/test/resources/features",
        glue       = {"com.appium.tests.stepdefs", "com.appium.tests.hooks"},
        plugin     = {
                "pretty",
                "html:target/cucumber-reports/single-run.html",
                "json:target/cucumber-reports/single-run.json"
        },
        tags       = "@debug",
        monochrome = true
)
public class SingleRunner extends AbstractTestNGCucumberTests {
    // No overrides needed — sequential execution is the default behaviour
    // of AbstractTestNGCucumberTests. Parallel override is in ParallelRunner.
}
