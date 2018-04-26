package de.intension.lizzy.converter.gherkin;

import static de.intension.lizzy.converter.CaseFormat.CAMEL_CASE;
import static de.intension.lizzy.converter.CaseFormat.SNAKE_CASE;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.text.ParseException;

import org.junit.Test;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import gherkin.ast.Feature;

public class GherkinGeneratorTest
{

    private static final String SPECIAL_CHARACTER_STRING = "1These characters: #/!\"§$%&()=? are_sp4ecial.";

    /**
     * GIVEN Valid gherkin feature object with a scenario
     * WHEN generating it to a java file
     * THEN the generated java file has all attributes set:
     * <ul>
     * <li>Class commentary
     * <li>Class name
     * <li>Test method:
     * <ul>
     * <li>Method commentary with all given, when, then
     * <li>@Test annotation
     * <li>Method name
     */
    @Test
    public void should_generate_executable_java_file_from_gherkin_object()
    {
        Feature feature = validFeature();

        JavaFile javaFile = GherkinGenerator.generate(feature, "lizzy.generator", CAMEL_CASE);

        TypeSpec testClass = javaFile.typeSpec;
        assertThat(testClass.name, equalTo("TheNameOfTheFeatureHereTest"));
        assertThat(testClass.javadoc.toString(), allOf(containsString("Description of the feature."),
                                                       containsString("Description can also have two lines.")));
        assertThat("Invalid amount of test methods.", testClass.methodSpecs.size(), equalTo(1));
        MethodSpec method = testClass.methodSpecs.get(0);
        assertThat(method.name, equalTo("scenario1"));
        assertThat(method.annotations.size(), equalTo(1));
        assertThat(method.annotations.get(0).type.toString(), equalTo("org.junit.Test"));
        assertThat(method.javadoc.toString(), allOf(containsString("Given some precondition"),
                                                    containsString("And more preconditions"),
                                                    containsString("When some action"),
                                                    containsString("And more actions"),
                                                    containsString("Then some result"),
                                                    containsString("And more results")));
        assertThat(method.name, equalTo("scenario1"));
    }

    /**
     * GIVEN Valid gherkin feature object with a scenario
     * WHEN generating it to a java file
     * THEN the generated test method has a name in snake case.
     */
    @Test
    public void should_generate_method_name_in_snake_case()
        throws ParseException
    {
        Feature feature = GherkinParser.parseFeature(GherkinParserTest.TEST_FEATURE.replace("Scenario 1", "This is snake case"));

        JavaFile javaFile = GherkinGenerator.generate(feature, "lizzy.generator", SNAKE_CASE);

        MethodSpec method = javaFile.typeSpec.methodSpecs.get(0);
        assertThat(method.name, equalTo("this_is_snake_case"));
    }

    /**
     * GIVEN Valid gherkin feature object with a scenario
     * WHEN generating it to a java file
     * THEN the generated test method has a name in camel case format.
     */
    @Test
    public void should_generate_method_name_in_camel_case()
        throws ParseException
    {
        Feature feature = GherkinParser.parseFeature(GherkinParserTest.TEST_FEATURE.replace("Scenario 1", "This is camel case"));

        JavaFile javaFile = GherkinGenerator.generate(feature, "lizzy.generator", CAMEL_CASE);

        MethodSpec method = javaFile.typeSpec.methodSpecs.get(0);
        assertThat(method.name, equalTo("thisIsCamelCase"));
    }

    /**
     * GIVEN Valid feature object without feature description
     * and scenario steps
     * WHEN generating it to a java file
     * THEN the generated test class has no javadoc
     */
    @Test
    public void should_generate_java_file_without_commentary()
    {
        String gherkin = "Feature: Feature\n" +
                "Scenario: Should be a method\n";
        Feature feature = GherkinParser.parseFeature(gherkin);

        JavaFile javaFile = GherkinGenerator.generate(feature, "lizzy.generator", SNAKE_CASE);

        TypeSpec testClass = javaFile.typeSpec;
        assertThat(testClass.javadoc.toString(), isEmptyOrNullString());
        MethodSpec method = testClass.methodSpecs.get(0);
        assertThat(method.javadoc.toString(), isEmptyOrNullString());
        assertThat(method.name, equalTo("should_be_a_method"));
    }

    /**
     * Given Feature with special characters in its name
     * When Generating java class from it
     * Then Special characters are removed from the class name
     */
    @Test
    public void should_remove_special_character_from_feature_name()
    {
        Feature feature = GherkinParser
            .parseFeature(GherkinParserTest.TEST_FEATURE.replace("The name of the feature here", SPECIAL_CHARACTER_STRING));

        JavaFile javaFile = GherkinGenerator.generate(feature, "lizzy.generator", SNAKE_CASE);

        TypeSpec testClass = javaFile.typeSpec;
        assertThat(testClass.name, equalTo("TheseCharacters$Are_sp4ecialTest"));
    }

    /**
     * Given Feature with special characters in scenario name
     * When Generating java class from it
     * Then Special characters are removed from the method name in camel case
     */
    @Test
    public void should_remove_special_character_from_scenario_name_for_camel_case()
    {
        Feature feature = GherkinParser
            .parseFeature(GherkinParserTest.TEST_FEATURE.replace("Scenario 1", SPECIAL_CHARACTER_STRING));

        JavaFile javaFile = GherkinGenerator.generate(feature, "lizzy.generator", CAMEL_CASE);

        MethodSpec method = javaFile.typeSpec.methodSpecs.get(0);
        assertThat(method.name, equalTo("theseCharacters$Are_sp4ecial"));
    }

    /**
     * Given Feature with special characters in scenario name
     * When Generating java class from it
     * Then Special characters are removed from the method name in snake case
     */
    @Test
    public void should_remove_special_character_from_scenario_name_for_snake_case()
    {
        Feature feature = GherkinParser
            .parseFeature(GherkinParserTest.TEST_FEATURE.replace("Scenario 1", SPECIAL_CHARACTER_STRING));

        JavaFile javaFile = GherkinGenerator.generate(feature, "lizzy.generator", SNAKE_CASE);

        MethodSpec method = javaFile.typeSpec.methodSpecs.get(0);
        assertThat(method.name, equalTo("these_characters_$_are_sp4ecial"));
    }

    private Feature validFeature()
    {
        return GherkinParser.parseFeature(GherkinParserTest.TEST_FEATURE);
    }
}
