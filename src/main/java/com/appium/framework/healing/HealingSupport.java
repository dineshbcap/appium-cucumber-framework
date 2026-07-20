package com.appium.framework.healing;

import com.appium.framework.config.ConfigReader;
import com.appium.framework.driver.DriverManager;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import com.dinesh.healing.HealingCache;
import com.dinesh.healing.HealingConfig;
import com.dinesh.healing.HealingReporter;
import com.dinesh.healing.LocatorRepository;
import com.dinesh.healing.Platform;
import com.dinesh.healing.SelfHealingElementLocator;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.nio.file.Path;

/**
 * Wires the {@code com.dinesh.healing} self-healing-locators library into this framework.
 *
 * <p><b>Why this class exists:</b> {@link SelfHealingElementLocator} wraps a specific
 * {@link AppiumDriver}, but drivers are thread-scoped (see {@link DriverManager}) — one
 * per parallel scenario. This class mirrors {@link DriverManager}'s thread-local pattern:
 * a single, process-wide {@link LocatorRepository}/{@link HealingCache}/{@link HealingConfig}
 * are shared read-only across threads, while each thread gets its own
 * {@link SelfHealingElementLocator} bound to its own driver.</p>
 *
 * <p><b>Locator source:</b> {@code locators/locators_android.properties} and
 * {@code locators/locators_ios.properties} on the classpath, loaded via
 * {@link LocatorRepository#fromProperties(String, String)}. These happen to share their
 * relative path with the healing library's own bundled example resources of the same
 * name — verified (via a real Maven/Surefire run, not just classpath inspection) that
 * this project's copies are resolved first, since Maven always places
 * {@code target/classes}/{@code target/test-classes} ahead of dependency jars on the
 * runtime classpath.</p>
 *
 * <p><b>Config source:</b> {@code healing-config.properties} — likewise a distinct name
 * from the library's bundled {@code healing.properties}, for the same reason. See that
 * file for the available {@code healing.*} settings (enabled, failOnHeal, cache/report
 * file locations, LLM engine toggle).</p>
 *
 * <p><b>Cache build-id:</b> the healing cache is keyed by a build identifier — a version
 * name, CI build number, or APK/IPA checksum, anything that changes when the app under
 * test changes. Configure via {@code app.build.id} in config.properties, or override per
 * run with {@code -Dapp.build.id=...} (e.g. {@code -Dapp.build.id=$BUILD_NUMBER} in CI).
 * A stale cache entry from a previous build's healed locator is far more likely to be
 * wrong than helpful, so changing the build id effectively starts the cache fresh.</p>
 */
public final class HealingSupport {

    private static final Logger log = LogManager.getLogger(HealingSupport.class);

    private static final String ANDROID_LOCATORS_CLASSPATH_FILE = "locators/locators_android.properties";
    private static final String IOS_LOCATORS_CLASSPATH_FILE = "locators/locators_ios.properties";
    private static final String HEALING_CONFIG_CLASSPATH_FILE = "healing-config.properties";

    private static final LocatorRepository REPOSITORY =
            LocatorRepository.fromProperties(ANDROID_LOCATORS_CLASSPATH_FILE, IOS_LOCATORS_CLASSPATH_FILE);

    private static final HealingConfig CONFIG = new HealingConfig(HEALING_CONFIG_CLASSPATH_FILE);

    private static final HealingCache CACHE = buildCache();

    static {
        // HealingReporter's listener list is a single process-wide static, so this
        // registration only needs to happen once per JVM, at class-init time —
        // not per-thread like the locator itself.
        HealingReporter.addListener(HealingSupport::mirrorToExtentReport);
    }

    /**
     * Mirrors a heal event into the currently active Extent node as a warning, so a
     * heal is visible directly in the HTML report instead of only in
     * {@code healing-report.json} / the log. Falls back from the current step to the
     * current scenario ({@link ExtentCucumberAdapter} tracks both) in case a heal
     * happens outside step context (e.g. from a hook); does nothing if neither is
     * active for this thread.
     *
     * @param record the heal event reported by {@link SelfHealingElementLocator}
     */
    private static void mirrorToExtentReport(HealingReporter.HealingRecord record) {
        ExtentTest test = ExtentCucumberAdapter.getCurrentStep();
        if (test == null) {
            test = ExtentCucumberAdapter.getCurrentScenario();
        }
        if (test == null) {
            return;
        }
        test.warning("⚠ Locator healed: " + record.locatorKey() + " "
                + record.originalLocator() + " → " + record.healedLocator()
                + " (via " + record.healingStrategy() + ") — update locators_<platform>.properties");
    }

    /**
     * Thread-local self-healing locator, bound to the current thread's Appium driver.
     * InheritableThreadLocal to match {@link DriverManager}'s child-thread behavior.
     */
    private static final InheritableThreadLocal<SelfHealingElementLocator> LOCATOR_THREAD =
            new InheritableThreadLocal<>();

    private HealingSupport() {}

    private static HealingCache buildCache() {
        String buildId = ConfigReader.get("app.build.id", "unknown");
        HealingCache cache = new HealingCache(buildId, Path.of(CONFIG.cacheFile()));
        cache.persistOnShutdown();
        log.info("Self-healing cache ready for build '{}' ({} cached entries) -> {}",
                buildId, cache.size(), CONFIG.cacheFile());
        return cache;
    }

    // ── Lifecycle (mirrors DriverManager) ──────────────────────────────────────

    /**
     * Creates and stores a self-healing locator for the current thread, bound to the
     * given driver. Called from {@link DriverManager#setDriver(AppiumDriver)} —
     * every driver factory (local, BrowserStack, Sauce Labs, LambdaTest) goes through
     * that single choke point, so this never needs to be wired per-factory.
     *
     * @param driver the driver just created for this thread
     */
    public static void init(AppiumDriver driver) {
        LOCATOR_THREAD.set(new SelfHealingElementLocator(driver, REPOSITORY, CACHE, CONFIG));
        log.debug("Self-healing locator initialized for thread: {}", Thread.currentThread().getName());
    }

    /**
     * Returns the current thread's self-healing locator.
     *
     * @return this thread's {@link SelfHealingElementLocator}
     * @throws IllegalStateException if {@link #init(AppiumDriver)} was not called on this thread
     */
    public static SelfHealingElementLocator locator() {
        SelfHealingElementLocator locator = LOCATOR_THREAD.get();
        if (locator == null) {
            throw new IllegalStateException(
                    "Self-healing locator is not initialized for thread '" +
                    Thread.currentThread().getName() + "'. " +
                    "Ensure DriverManager.setDriver() was called (it initializes this internally).");
        }
        return locator;
    }

    /**
     * Clears the current thread's self-healing locator. Called from
     * {@link DriverManager#removeDriver()} during scenario teardown.
     */
    public static void remove() {
        LOCATOR_THREAD.remove();
    }

    // ── Raw (non-healing) access ────────────────────────────────────────────────

    /**
     * Resolves the raw, declared (non-healing) {@link By} for a locator key.
     *
     * <p>Used for bulk lookups ({@code findElements}) and wait-condition locators —
     * the healing library's {@link SelfHealingElementLocator} only heals single-element
     * {@code find()} calls, so multi-element queries intentionally bypass it.</p>
     *
     * @param key dotted locator key (e.g. {@code "button.normal"})
     * @return the declared {@link By} for the current platform
     */
    public static By rawLocator(String key) {
        return REPOSITORY.get(key, currentPlatform()).toBy();
    }

    /**
     * Returns the plain-English description of a locator key.
     *
     * @param key dotted locator key
     * @return description text
     */
    public static String describe(String key) {
        return REPOSITORY.get(key, currentPlatform()).description();
    }

    private static Platform currentPlatform() {
        return Platform.fromDriver(DriverManager.getDriver());
    }

    /**
     * Forces an immediate write of the healing cache to disk, in addition to the
     * shutdown-hook persistence {@link HealingCache#persistOnShutdown()} already sets up —
     * cheap extra insurance against an abnormal JVM exit (CI timeout, forced kill)
     * losing healed locators discovered mid-run. Safe to call after every scenario.
     */
    public static void persistCache() {
        CACHE.persist();
    }

    /**
     * Writes the accumulated {@link HealingReporter} report — every heal recorded
     * across the whole suite, not just the current scenario — to {@code healing.report.file}.
     * Intended for a TestNG {@code @AfterSuite} hook (see {@code BaseCucumberRunner}),
     * since {@link HealingReporter}'s records are a single process-wide list, not
     * per-thread like {@link #CACHE}.
     */
    public static void writeReport() {
        Path reportPath = Path.of(CONFIG.reportFile());
        HealingReporter.writeReport(reportPath);
        log.info("Self-healing report written -> {}", reportPath);
    }
}
