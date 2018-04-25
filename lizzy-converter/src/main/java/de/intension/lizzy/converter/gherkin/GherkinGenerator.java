package de.intension.lizzy.converter.gherkin;

import javax.lang.model.element.Modifier;

import org.junit.Test;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import cucumber.runtime.table.CamelCaseStringConverter;
import cucumber.runtime.table.PascalCaseStringConverter;
import de.intension.lizzy.converter.CaseFormat;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.Step;

/**
 * Generates a java test class file from a cucumber feature object.
 * <ul>
 * <li>Class
 * <ul>
 * <li>Class commentary: feature.description
 * <li>Name: feature.name
 * </ul>
 * <li>Methods: (scenarios)
 * <ul>
 * <li>Method commentary: scenario.steps
 * <li>Name: scenario.name
 * </ul>
 * e.g. The gherkin snippet:
 *
 * <pre>
 * <code>Feature: Putting on pants
 *    A human puts on pants to wear.
 *
 *   Scenario: Human puts on pants leg by leg
 *      Given a human with normal pants
 *      When the human puts the first leg in the pants
 *      And he puts the second leg in the pants
 *      Then the pants are successfully worn
 * </code>
 * </pre>
 *
 * generates the following code when calling {@link #generate(Feature, String, CaseFormat)}
 * with methodFormat={@link CaseFormat#UNDERSCORE_CASE}:
 *
 * <pre>
 * <code>
 * &#47;**
 *  * A human puts on pants to wear.
 *  *&#47;
 * public void PuttingOnPantsTest
 * {
 *
 *    &#47;**
 *     * Given a human with normal pants
 *     * When the human puts the first leg in the pants
 *     * And he puts the second leg in the pants
 *     * Then the pants are successfully worn
 *     *&#47;
 *    &#64;Test
 *    public void human_puts_on_pants_leg_by_leg()
 *    {
 *
 *    }
 * }
 * </code>
 * </pre>
 *
 * @see https://github.com/square/javapoet
 */
public class GherkinGenerator
{

    /**
     * Generates an object representing the java test class.
     *
     * @param feature The feature to be processed
     * @param packageName The package name of the class to generate
     * @param methodFormat Use this format to determine whether the method name
     *            should be generated in underscore or camel case
     * @return The generated java file object
     */
    public static JavaFile generate(Feature feature, String packageName, CaseFormat methodFormat)
    {
        String className = formatClassName(feature.getName());
        TypeSpec.Builder testClass = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC);
        String javaDoc = feature.getDescription();
        if ((javaDoc != null) && !javaDoc.isEmpty()) {
            testClass.addJavadoc(javaDoc);
        }
        for (ScenarioDefinition scenario : feature.getChildren()) {
            String methodName = format(scenario.getName(), methodFormat);
            MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Test.class);
            for (Step step : scenario.getSteps()) {
                method.addJavadoc(step.getKeyword() + step.getText() + "\n");
            }
            testClass.addMethod(method.build());
        }
        return JavaFile.builder(packageName, testClass.build()).build();
    }

    /**
     * Formats a string to a given format.
     * <ul>
     * e.g. 'This is a string' gets formatted to:
     * <li>Camel case: 'thisIsAString'
     * <li>Underscore case: 'this_is_a_string'
     * </ul>
     */
    private static String format(String string, CaseFormat format)
    {
        switch (format) {
        case CAMEL_CASE:
            return new CamelCaseStringConverter().map(string);
        case UNDERSCORE_CASE:
            return string.substring(0, 1).toLowerCase() + string.substring(1).replace(' ', '_');
        default:
            throw new IllegalStateException("Unknown case format '" + format + "'.");
        }
    }

    /**
     * Formats a string to java test class name format.
     * e.g. 'user is stored' is formatted to 'UserIsStoredTest'.
     *
     * @param string The unformatted string to represent the class name.
     * @return Formatted class name in pascal case.
     */
    private static String formatClassName(String string)
    {
        String className = new PascalCaseStringConverter().map(string);
        if (!className.endsWith("Test")) {
            return className + "Test";
        }
        return className;
    }
}
