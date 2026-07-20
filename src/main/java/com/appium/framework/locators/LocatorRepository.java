package com.appium.framework.locators;

import com.appium.framework.config.ConfigReader;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central registry for element locators, externalized from Page Object classes into
 * platform-specific properties files on the classpath:
 * {@code locators/locators_android.properties} and {@code locators/locators_ios.properties}.
 *
 * <p><b>File format:</b> each entry is a dotted {@code page.element} key mapped to a
 * {@code strategy=value} pair, with a sibling {@code page.element.description} key
 * holding a plain-English description of the element, e.g.:
 * <pre>
 *   button.normal.description=The 'Normal' button on the Buttons screen
 *   button.normal=id=io.appium.android.apis:id/button_normal
 * </pre>
 * The strategy prefix is split on the first {@code =} only, so values containing
 * further {@code =} signs (NSPredicate expressions, XPath attribute tests) are
 * preserved intact. The description is deliberately a real property (not just a
 * {@code #} comment) so it can be read back at runtime via {@link #getDescription(String)}
 * — e.g. by a locator-healing system that needs a semantic description of an element
 * to re-locate it once its {@code strategy=value} locator stops matching.</p>
 *
 * <p><b>Supported strategies:</b>
 * <ul>
 *   <li>{@code id} — {@link By#id(String)}</li>
 *   <li>{@code accessibilityId} — {@link AppiumBy#accessibilityId(String)}</li>
 *   <li>{@code xpath} — {@link By#xpath(String)}</li>
 *   <li>{@code className} — {@link By#className(String)}</li>
 *   <li>{@code uiautomator} — {@link AppiumBy#androidUIAutomator(String)} (Android only)</li>
 *   <li>{@code nsPredicate} — {@link AppiumBy#iOSNsPredicateString(String)} (iOS only)</li>
 *   <li>{@code classChain} — {@link AppiumBy#iOSClassChain(String)} (iOS only)</li>
 * </ul>
 * </p>
 *
 * <p>A key that only makes sense on one platform (a native system dialog, a
 * platform-specific widget) is simply omitted from the other platform's file —
 * looking it up on the wrong platform fails fast with a clear error rather than
 * silently returning a bogus locator.</p>
 */
public final class LocatorRepository {

    private static final Logger log = LogManager.getLogger(LocatorRepository.class);
    private static final String ANDROID_FILE = "locators/locators_android.properties";
    private static final String IOS_FILE = "locators/locators_ios.properties";

    private static final Properties ANDROID_LOCATORS = load(ANDROID_FILE);
    private static final Properties IOS_LOCATORS = load(IOS_FILE);

    private LocatorRepository() {}

    private static Properties load(String classpathFile) {
        Properties props = new Properties();
        try (InputStream stream = LocatorRepository.class.getClassLoader()
                .getResourceAsStream(classpathFile)) {
            if (stream == null) {
                throw new RuntimeException("Locator file not found on classpath: " + classpathFile);
            }
            props.load(stream);
            log.info("Loaded {} locators from '{}'", props.size(), classpathFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + classpathFile, e);
        }
        return props;
    }

    /**
     * Resolves a locator key to a platform-appropriate {@link By}, reading from
     * whichever properties file matches the currently configured platform
     * ({@link ConfigReader#getPlatform()}).
     *
     * @param key dotted locator key (e.g. {@code "button.normal"})
     * @return resolved {@link By}
     * @throws RuntimeException if the key is absent for the current platform or malformed
     */
    public static By get(String key) {
        boolean ios = ConfigReader.isIOS();
        Properties props = ios ? IOS_LOCATORS : ANDROID_LOCATORS;
        String raw = props.getProperty(key);
        if (raw == null) {
            throw new RuntimeException("Locator key '" + key + "' not found for platform '"
                    + ConfigReader.getPlatform() + "' in "
                    + (ios ? IOS_FILE : ANDROID_FILE));
        }
        return parse(key, raw.trim());
    }

    /**
     * Returns the plain-English description of a locator key, or {@code null} if none
     * is defined. Reads the {@code <key>.description} entry from whichever properties
     * file matches the currently configured platform.
     *
     * <p>Intended for diagnostics and locator-healing tooling: when a {@code strategy=value}
     * locator stops matching (app UI changed), the description is a stable, human/AI-readable
     * fallback for re-locating the same semantic element.</p>
     *
     * @param key dotted locator key (e.g. {@code "button.normal"})
     * @return description text, or {@code null} if not defined for the current platform
     */
    public static String getDescription(String key) {
        Properties props = ConfigReader.isIOS() ? IOS_LOCATORS : ANDROID_LOCATORS;
        return props.getProperty(key + ".description");
    }

    private static By parse(String key, String raw) {
        int sep = raw.indexOf('=');
        if (sep < 0) {
            throw new RuntimeException("Malformed locator '" + key + "=" + raw
                    + "' — expected 'strategy=value'");
        }
        String strategy = raw.substring(0, sep).trim();
        String value = raw.substring(sep + 1).trim();
        switch (strategy) {
            case "id":
                return By.id(value);
            case "accessibilityId":
                return AppiumBy.accessibilityId(value);
            case "xpath":
                return By.xpath(value);
            case "className":
                return By.className(value);
            case "uiautomator":
                return AppiumBy.androidUIAutomator(value);
            case "nsPredicate":
                return AppiumBy.iOSNsPredicateString(value);
            case "classChain":
                return AppiumBy.iOSClassChain(value);
            default:
                throw new RuntimeException("Unknown locator strategy '" + strategy
                        + "' for key '" + key + "'");
        }
    }
}
