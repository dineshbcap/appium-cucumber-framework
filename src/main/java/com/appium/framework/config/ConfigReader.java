package com.appium.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized configuration reader that loads test settings from {@code config.properties}.
 *
 * <p><b>Concept covered:</b> Externalizing configuration (device capabilities, URLs, timeouts)
 * from test code into a properties file enables running the same tests against different
 * environments and platforms without code changes — just swap config values or override
 * via system properties.</p>
 *
 * <p><b>Property resolution order (highest priority first):</b>
 * <ol>
 *   <li>JVM system property: {@code -Dplatform=ios} on the Maven/Gradle command line</li>
 *   <li>{@code config.properties} file on the classpath</li>
 *   <li>Default value passed to the overloaded {@code get(key, default)} method</li>
 * </ol>
 * This allows CI pipelines to override specific properties without modifying the file.</p>
 *
 * <p><b>Usage examples:</b>
 * <pre>
 *   String platform = ConfigReader.getPlatform();          // "android" or "ios"
 *   String serverUrl = ConfigReader.get("appium.server.url");
 *   int timeout = ConfigReader.getInt("explicit.wait", 15);
 *   boolean noReset = ConfigReader.getBoolean("android.noReset", false);
 * </pre>
 * </p>
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        // Load config.properties once at class load time — not on every call
        try (InputStream stream = ConfigReader.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (stream == null) {
                throw new RuntimeException("config.properties not found on classpath. " +
                        "Ensure it exists in src/test/resources/");
            }
            properties.load(stream);
            log.info("Loaded configuration from '{}'", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE, e);
        }
    }

    private ConfigReader() {}

    // ── String Properties ──────────────────────────────────────────────────────

    /**
     * Returns the value of the given property key.
     * System properties take precedence over file values (enables CI overrides).
     *
     * @param key property key (e.g., "platform", "appium.server.url")
     * @return trimmed property value
     * @throws RuntimeException if the property is not found in either source
     */
    public static String get(String key) {
        String value = System.getProperty(key, properties.getProperty(key));
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in " + CONFIG_FILE +
                    " and not set as a system property (-D" + key + "=value)");
        }
        return value.trim();
    }

    /**
     * Returns the value of the given property key, or {@code defaultValue} if not found.
     *
     * @param key          property key
     * @param defaultValue fallback value when key is absent
     * @return property value, or defaultValue if not configured
     */
    public static String get(String key, String defaultValue) {
        return System.getProperty(key, properties.getProperty(key, defaultValue)).trim();
    }

    // ── Numeric Properties ─────────────────────────────────────────────────────

    /**
     * Returns the property value as an integer.
     *
     * @param key property key (e.g., "explicit.wait")
     * @return integer value
     * @throws NumberFormatException if the value is not a valid integer
     */
    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * Returns the property value as an integer, or {@code defaultValue} if not found.
     *
     * @param key          property key
     * @param defaultValue fallback integer value
     * @return integer value or default
     */
    public static int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }

    // ── Boolean Properties ─────────────────────────────────────────────────────

    /**
     * Returns the property value as a boolean.
     * Values "true" (case-insensitive) return {@code true}; all others return {@code false}.
     *
     * @param key property key (e.g., "android.noReset")
     * @return boolean value
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /**
     * Returns the property value as a boolean, or {@code defaultValue} if not found.
     *
     * @param key          property key
     * @param defaultValue fallback boolean
     * @return boolean value or default
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }

    // ── Platform Helpers ───────────────────────────────────────────────────────

    /**
     * Returns the configured test platform in lowercase (e.g., "android" or "ios").
     * Override at runtime: {@code mvn test -Dplatform=ios}
     *
     * @return platform string: "android" or "ios"
     */
    public static String getPlatform() {
        return get("platform", "android").toLowerCase();
    }

    /**
     * Returns {@code true} if the configured platform is Android.
     *
     * @return {@code true} for Android
     */
    public static boolean isAndroid() {
        return "android".equals(getPlatform());
    }

    /**
     * Returns {@code true} if the configured platform is iOS.
     *
     * @return {@code true} for iOS
     */
    public static boolean isIOS() {
        return "ios".equals(getPlatform());
    }
}
