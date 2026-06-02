package com.appium.tests.runners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG annotation transformer that automatically applies {@link RetryAnalyzer}
 * to every {@code @Test} method in the test suite.
 *
 * <p><b>Concept covered:</b> Normally, a retry analyzer must be set on each
 * {@code @Test} annotation individually: {@code @Test(retryAnalyzer = RetryAnalyzer.class)}.
 * This transformer eliminates that boilerplate by applying it globally at the TestNG
 * listener level — especially useful when working with Cucumber's generated test methods.</p>
 *
 * <p><b>Registration:</b> Add this as a TestNG listener in your suite XML:
 * <pre>{@code
 * <listeners>
 *   <listener class-name="com.appium.tests.runners.RetryAnnotationTransformer"/>
 * </listeners>
 * }</pre>
 * </p>
 *
 * <p><b>Cucumber with TestNG:</b> {@link io.cucumber.testng.AbstractTestNGCucumberTests}
 * generates a {@code @Test} method named {@code runScenario} for each scenario.
 * This transformer attaches the retry analyzer to those generated methods automatically.</p>
 */
public class RetryAnnotationTransformer implements IAnnotationTransformer {

    private static final Logger log = LogManager.getLogger(RetryAnnotationTransformer.class);

    /**
     * Called by TestNG for every {@code @Test} method found in the suite.
     * Sets {@link RetryAnalyzer} as the retry handler for all test methods.
     *
     * @param annotation  the {@code @Test} annotation instance (mutable)
     * @param testClass   the class declaring the test (may be null for method-level)
     * @param testConstructor the constructor of the test class (may be null)
     * @param testMethod  the actual test method being annotated
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        Class<?> retryAnalyzerClass = annotation.getRetryAnalyzerClass();
        // Only set the retry analyzer if one hasn't been explicitly configured
        if (retryAnalyzerClass == null || retryAnalyzerClass.equals(Class.class)) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
            log.debug("RetryAnalyzer applied to: {}#{}",
                    testMethod != null ? testMethod.getDeclaringClass().getSimpleName() : "unknown",
                    testMethod != null ? testMethod.getName() : "unknown");
        }
    }
}
