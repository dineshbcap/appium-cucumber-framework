package com.appium.framework.driver;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread-safe Appium driver holder using {@link InheritableThreadLocal}.
 *
 * <p><b>Concept covered:</b> In parallel test execution each scenario runs on its own
 * thread. A plain static driver field would be shared and corrupted by concurrent tests.
 * {@link ThreadLocal} ensures each thread has its own isolated driver instance.</p>
 *
 * <p><b>Why InheritableThreadLocal?</b> Some test frameworks (like TestNG's data
 * provider) spawn child threads. {@link InheritableThreadLocal} allows the child thread
 * to inherit the driver reference from its parent — avoiding a null-driver error when
 * helper methods run in a child context.</p>
 *
 * <p><b>Lifecycle:</b>
 * <pre>
 *   Before scenario → DriverFactory.createDriver() → DriverManager.setDriver(driver)
 *   During scenario → DriverManager.getDriver()        (read-only)
 *   After scenario  → DriverManager.removeDriver()     (quit + remove from ThreadLocal)
 * </pre>
 * Always call {@link #removeDriver()} in a {@code finally} block to guarantee the
 * driver session is closed even when the scenario throws an exception.</p>
 */
public class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);

    /**
     * Thread-local driver store. InheritableThreadLocal so child threads (e.g., TestNG's
     * data provider worker threads) inherit the driver from their parent.
     */
    private static final InheritableThreadLocal<AppiumDriver> driverThread =
            new InheritableThreadLocal<>();

    private DriverManager() {}

    /**
     * Returns the Appium driver for the current thread.
     *
     * @return current thread's {@link AppiumDriver}
     * @throws IllegalStateException if the driver has not been initialized for this thread
     *         (likely a missing {@code @Before} hook or incorrect setup order)
     */
    public static AppiumDriver getDriver() {
        AppiumDriver driver = driverThread.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "Appium driver is not initialized for thread '" +
                    Thread.currentThread().getName() + "'. " +
                    "Ensure DriverFactory.createDriver() is called in @Before.");
        }
        return driver;
    }

    /**
     * Stores the given driver for the current thread.
     * Called by {@link DriverFactory} immediately after creating the session.
     *
     * @param driver the initialized {@link AppiumDriver} to store
     */
    public static void setDriver(AppiumDriver driver) {
        log.debug("Setting driver for thread: {}", Thread.currentThread().getName());
        driverThread.set(driver);
    }

    /**
     * Quits the current thread's driver and removes it from the ThreadLocal store.
     *
     * <p>Always call this in {@code @After} hooks (inside a {@code finally} block)
     * to prevent driver session leaks that leave orphan Appium processes running.</p>
     */
    public static void removeDriver() {
        AppiumDriver driver = driverThread.get();
        if (driver != null) {
            try {
                driver.quit();
                log.debug("Driver quit for thread: {}", Thread.currentThread().getName());
            } catch (Exception e) {
                log.warn("Error quitting driver on thread '{}': {}",
                        Thread.currentThread().getName(), e.getMessage());
            } finally {
                // Remove from ThreadLocal regardless of quit success
                driverThread.remove();
            }
        }
    }

    /**
     * Returns {@code true} if a driver has been initialized for the current thread.
     * Useful for conditional logic in shared helpers that may run in or out of a test session.
     *
     * @return {@code true} if a driver is stored for the current thread
     */
    public static boolean isDriverInitialized() {
        return driverThread.get() != null;
    }
}
