package com.appium.tests.runners;

import com.appium.framework.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry mechanism for flaky mobile tests.
 *
 * <p><b>Concept covered:</b> Mobile tests can be inherently flaky due to timing issues,
 * emulator responsiveness, or transient Appium server glitches. A retry analyzer
 * automatically re-runs failed tests up to {@link #MAX_RETRY} times before marking
 * them as truly failed.</p>
 *
 * <p><b>Configuration:</b> Set {@code test.retry.count} in {@code config.properties}
 * to control the maximum number of retries (default: 1).</p>
 *
 * <p><b>How it works:</b>
 * <ol>
 *   <li>TestNG calls {@link #retry(ITestResult)} when a test fails.</li>
 *   <li>Return {@code true} to retry the test, {@code false} to mark it failed.</li>
 *   <li>The retry count resets per test instance (thread-local for parallel execution).</li>
 * </ol>
 * </p>
 *
 * <p><b>Cucumber integration:</b> This class works at the TestNG level (around
 * the entire scenario). For Cucumber, attach it via
 * {@link RetryAnnotationTransformer} to avoid manual {@code @Test(retryAnalyzer=...)}
 * annotations on every runner method.</p>
 *
 * <p><b>Usage with ParallelRunner:</b> The {@link RetryAnnotationTransformer} is
 * registered in the TestNG XML suite file via {@code <listeners>}.</p>
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);

    /** Maximum number of retry attempts (not counting the original run). */
    private static final int MAX_RETRY = ConfigReader.getInt("test.retry.count", 1);

    /**
     * Per-thread retry counter — essential for parallel execution where multiple
     * tests run concurrently on different threads.
     */
    private final ThreadLocal<Integer> retryCount = ThreadLocal.withInitial(() -> 0);

    /**
     * Called by TestNG when a test method fails.
     *
     * @param result the result of the failing test
     * @return {@code true} if the test should be retried, {@code false} to mark as failed
     */
    @Override
    public boolean retry(ITestResult result) {
        int currentCount = retryCount.get();
        if (currentCount < MAX_RETRY) {
            retryCount.set(currentCount + 1);
            log.warn("Retrying failed test '{}' (attempt {}/{})",
                    result.getName(), currentCount + 1, MAX_RETRY);
            return true;
        }
        log.error("Test '{}' failed after {} retry attempt(s)", result.getName(), MAX_RETRY);
        retryCount.set(0); // Reset for the next test on this thread
        return false;
    }
}
