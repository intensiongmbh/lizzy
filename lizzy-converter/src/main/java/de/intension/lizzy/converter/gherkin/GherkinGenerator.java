/*******************************************************************************
 * Copyright 2018 Intension GmbH (https://www.intension.de)
 * and other contributors as indicated by the @author tags.
 * Licensed under the Eclipse Public License - v 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.eclipse.org/legal/epl-2.0/
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.intension.lizzy.converter.gherkin;

import java.util.function.Consumer;

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
 * with methodFormat={@link CaseFormat#SNAKE_CASE}:
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
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class GherkinGenerator
{

    private GherkinGenerator()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates an object representing the java test class.
     *
     * @param feature The feature to be processed
     * @param packageName The package name of the class to generate
     * @param methodFormat Use this format to determine whether the method name
     *            should be generated in snake or camel case
     * @return The generated java file object
     */
    public static JavaFile generate(Feature feature, String packageName, CaseFormat methodFormat)
    {
        String className = prepare(formatClassName(feature.getName()), true);
        TypeSpec.Builder testClass = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC);
        String javaDoc = feature.getDescription();
        if ((javaDoc != null) && !javaDoc.isEmpty()) {
            testClass.addJavadoc(javaDoc);
        }
        feature.getChildren().forEach(generateMethods(testClass, methodFormat));
        return JavaFile.builder(packageName, testClass.build()).build();
    }

    /**
     * Generates the methods name and java documentation based on the scenario.
     */
    private static Consumer<? super ScenarioDefinition> generateMethods(TypeSpec.Builder testClass, CaseFormat methodFormat)
    {
        return scenario -> {
            String methodName = prepare(format(scenario.getName(), methodFormat), false);
            MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Test.class);
            scenario.getSteps().forEach(addJavadocTo(method));
            testClass.addMethod(method.build());
        };
    }

    /**
     * Adds java documentation based on the scenarios steps.
     */
    private static Consumer<? super Step> addJavadocTo(MethodSpec.Builder method)
    {
        return step -> method.addJavadoc(step.getKeyword() + step.getText() + "\n");
    }

    /**
     * Formats a string to a given format.
     * <ul>
     * e.g. 'This is a string' gets formatted to:
     * <li>Camel case: 'thisIsAString'
     * <li>Snake case: 'this_is_a_string'
     * </ul>
     */
    private static String format(String string, CaseFormat format)
    {
        switch (format) {
        case CAMEL_CASE:
            return new CamelCaseStringConverter().map(string);
        case SNAKE_CASE:
            return string.toLowerCase().replace(' ', '_');
        default:
            throw new IllegalStateException("Unknown case format '" + format + "'.");
        }
    }

    /**
     * Removes all prohibited characters from the class or method name.
     * 
     * @param string The unformatted string to represent the class or method name.
     * @see https://docs.oracle.com/javase/specs/
     */
    private static String prepare(String string, boolean isClass)
    {
        String result = string.replaceAll("[^\\w$']+", "").replaceAll("^\\d+", "");
        return isClass ? result : decapitalize(result);
    }

    /**
     * Decapitalizes the first character of a string.
     */
    private static String decapitalize(String string)
    {
        char[] c = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
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
