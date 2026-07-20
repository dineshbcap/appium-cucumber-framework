package com.appium.tests.runners;

import com.appium.framework.healing.HealingSupport;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.AfterSuite;

/**
 * Shared base for the Cucumber-TestNG runners ({@link ParallelRunner}, {@link SingleRunner}).
 *
 * <p>TestNG's {@code @AfterSuite} runs once after every scenario in the suite has
 * finished — unlike Cucumber's {@code @After} (per scenario) — making it the right
 * place to flush the self-healing report, which accumulates heals across the whole
 * suite rather than being scoped to one scenario.</p>
 */
public abstract class BaseCucumberRunner extends AbstractTestNGCucumberTests {

    /**
     * Writes the suite-wide self-healing report to {@code healing.report.file}.
     * {@code alwaysRun = true} so it still runs after a suite-level failure — the
     * heals recorded before that failure are exactly what's useful for debugging it.
     * The healing cache itself doesn't need a matching call here: it's already
     * persisted per-scenario ({@code Hooks#tearDown}) and on JVM shutdown
     * ({@code HealingCache#persistOnShutdown()}).
     */
    @AfterSuite(alwaysRun = true)
    public void writeHealingReport() {
        HealingSupport.writeReport();
    }
}
