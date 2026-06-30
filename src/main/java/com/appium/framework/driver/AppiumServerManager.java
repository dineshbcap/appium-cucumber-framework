package com.appium.framework.driver;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.Duration;

/**
 * Manages the Appium 2.x server lifecycle programmatically.
 *
 * <h2>Concept covered: Appium 2.x Driver and Plugin Management</h2>
 *
 * <p>Appium 2.x introduced a plugin architecture that separates the core server from
 * platform drivers and optional plugins. Unlike Appium 1.x (which bundled everything),
 * Appium 2.x requires explicitly installing each driver and plugin.</p>
 *
 * <h2>One-time CLI Setup (run once after installing Appium)</h2>
 *
 * <h3>Install Appium 2.x</h3>
 * <pre>
 *   npm install -g appium@latest
 *   appium --version   # verify installation
 * </pre>
 *
 * <h3>Install Platform Drivers</h3>
 * Each automation driver is installed separately in Appium 2.x:
 * <pre>
 *   appium driver install uiautomator2      # Android (UiAutomator2 engine)
 *   appium driver install xcuitest          # iOS / iPadOS (XCUITest engine)
 *   appium driver install espresso          # Android Espresso (in-process)
 *   appium driver install mac2              # macOS native apps
 *   appium driver install safari            # Safari browser on macOS/iOS
 * </pre>
 *
 * <h3>Manage Installed Drivers</h3>
 * <pre>
 *   appium driver list                      # list all available drivers
 *   appium driver list --installed          # list installed drivers
 *   appium driver update uiautomator2       # update to latest
 *   appium driver uninstall uiautomator2    # remove driver
 * </pre>
 *
 * <h3>Install Plugins</h3>
 * Plugins extend Appium behavior without modifying the core server:
 * <pre>
 *   appium plugin install images            # image-based element finding
 *   appium plugin install execute-driver    # execute WebdriverIO/JS scripts
 *   appium plugin install relaxed-caps      # accept unknown/vendor capabilities
 *   appium plugin install gestures          # simplified gesture shortcuts
 *   # 3rd-party plugin from npm:
 *   appium plugin install --source=npm appium-device-farm
 * </pre>
 *
 * <h3>Manage Installed Plugins</h3>
 * <pre>
 *   appium plugin list --installed          # list installed plugins
 *   appium plugin update images             # update plugin
 *   appium plugin uninstall images          # remove plugin
 * </pre>
 *
 * <h3>Start Appium with Specific Drivers/Plugins</h3>
 * <pre>
 *   # Start with specific drivers and plugins activated
 *   appium --use-drivers=uiautomator2,xcuitest \
 *          --use-plugins=images,relaxed-caps \
 *          --port=4723 \
 *          --base-path=/
 *
 *   # Start with all installed drivers and plugins
 *   appium --port=4723
 *
 *   # Start allowing insecure features (needed for some advanced capabilities)
 *   appium --allow-insecure=no_reset,adb_shell
 *
 *   # Remote access (be careful in production)
 *   appium --address=0.0.0.0 --port=4723
 * </pre>
 *
 * <h2>Programmatic Server Management (this class)</h2>
 * <p>This class uses {@link AppiumServiceBuilder} to start and stop an embedded Appium
 * server from within the test process. This is useful for fully self-contained CI runs
 * where you don't want to maintain a separate Appium process.</p>
 *
 * <h3>When to use programmatic vs external server</h3>
 * <ul>
 *   <li><b>Programmatic start</b> — CI pipelines, Docker containers, fully self-contained suites</li>
 *   <li><b>External server</b> — local development (faster restart, visible logs in terminal)</li>
 *   <li><b>Cloud providers</b> (BrowserStack, Sauce Labs) — never start a local server;
 *       connect directly to the cloud hub URL</li>
 * </ul>
 *
 * <h3>Usage in TestNG suite</h3>
 * <pre>
 *   // In TestNG @BeforeSuite:
 *   AppiumServerManager.startServer();
 *
 *   // In TestNG @AfterSuite:
 *   AppiumServerManager.stopServer();
 * </pre>
 */
public class AppiumServerManager {

    private static final Logger log = LogManager.getLogger(AppiumServerManager.class);

    /** Appium 2.x default base path (changed from /wd/hub in 1.x to / in 2.x). */
    public static final String APPIUM_BASE_PATH = "/";

    /** Default Appium server port. */
    public static final int DEFAULT_PORT = 4723;

    private static AppiumDriverLocalService service;

    private AppiumServerManager() {}

    /**
     * Starts an Appium 2.x server on the default port (4723) with sensible defaults.
     *
     * <p>Requires:
     * <ul>
     *   <li>Node.js and npm installed</li>
     *   <li>{@code appium} installed globally: {@code npm install -g appium}</li>
     *   <li>At least one driver installed: {@code appium driver install uiautomator2}</li>
     * </ul>
     * </p>
     *
     * @return the URL string of the started server (e.g., "http://127.0.0.1:4723")
     */
    public static String startServer() {
        return startServer(DEFAULT_PORT);
    }

    /**
     * Starts an Appium 2.x server on the specified port.
     *
     * <p>Configuration applied:
     * <ul>
     *   <li>Binds to localhost only (127.0.0.1)</li>
     *   <li>Base path set to {@code /} (Appium 2.x default)</li>
     *   <li>Session override enabled — new sessions replace existing ones on same port</li>
     *   <li>Server logs written to {@code target/logs/appium-server.log}</li>
     *   <li>Startup timeout: 60 seconds</li>
     * </ul>
     * </p>
     *
     * <p>Override the Appium binary location via the {@code APPIUM_BINARY_PATH}
     * environment variable — useful when Appium is not on {@code PATH}.</p>
     *
     * @param port the TCP port to bind the server to
     * @return server URL string
     */
    public static String startServer(int port) {
        log.info("Starting Appium 2.x server on port {}...", port);

        // Ensure log directory exists before writing server logs
        new File("target/logs").mkdirs();

        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(port)
                // Appium 2.x uses "/" as the base path by default
                // (Appium 1.x used "/wd/hub" — this is a common migration gotcha)
                .withArgument(GeneralServerFlag.BASEPATH, APPIUM_BASE_PATH)
                // Allows overwriting an existing session on the same port
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                // Write Appium server logs to a file for CI artifact collection
                .withLogFile(new File("target/logs/appium-server.log"))
                // Fail fast if Appium doesn't start within 60 seconds
                .withTimeout(Duration.ofSeconds(60));

        // Allow a custom Appium binary path for non-standard installations
        String binaryPath = System.getenv("APPIUM_BINARY_PATH");
        if (binaryPath != null && !binaryPath.isBlank()) {
            builder.withAppiumJS(new File(binaryPath));
            log.debug("Using custom Appium binary: {}", binaryPath);
        }

        service = AppiumDriverLocalService.buildService(builder);
        service.start();

        String serverUrl = service.getUrl().toString();
        log.info("Appium 2.x server started at: {}", serverUrl);
        return serverUrl;
    }

    /**
     * Stops the running Appium server.
     *
     * <p>Should be invoked in a TestNG {@code @AfterSuite} or JVM shutdown hook
     * to prevent orphan Appium processes from lingering after test execution.</p>
     */
    public static void stopServer() {
        if (service != null && service.isRunning()) {
            log.info("Stopping Appium 2.x server...");
            service.stop();
            log.info("Appium server stopped");
        } else {
            log.debug("stopServer() called but server is not running — no-op");
        }
    }

    /**
     * Returns whether the server is currently running.
     *
     * @return {@code true} if an Appium server started by this class is running
     */
    public static boolean isRunning() {
        return service != null && service.isRunning();
    }

    /**
     * Returns the URL of the currently running Appium server.
     *
     * @return server URL (e.g., "http://127.0.0.1:4723")
     * @throws IllegalStateException if no server is running
     */
    public static String getServerUrl() {
        if (!isRunning()) {
            throw new IllegalStateException(
                    "Appium server is not running. Call AppiumServerManager.startServer() first.");
        }
        return service.getUrl().toString();
    }

    /**
     * Registers a JVM shutdown hook to automatically stop the server when the JVM exits.
     *
     * <p>This prevents orphan Appium processes when the test JVM is killed abruptly
     * (e.g., CTRL+C, OOM kill in CI). Call this once after {@link #startServer()}.</p>
     */
    public static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("JVM shutdown — stopping Appium server");
            stopServer();
        }, "appium-shutdown-hook"));
    }
}
