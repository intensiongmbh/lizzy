package de.intension.lizzy.converter.gherkin;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import de.intension.lizzy.converter.gherkin.GherkinParser;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.Step;

public class GherkinParserTest
{

    public static final String TEST_FEATURE = "Feature: The name of the feature here\n" +
            "    Description of the feature.\n" +
            "    Description can also have two lines.\n" +
            "\n" +
            "    Scenario: Scenario 1\n" +
            "        Given some precondition\n" +
            "        And more preconditions\n" +
            "        When some action\n" +
            "        And more actions\n" +
            "        Then some result\n" +
            "        And more results";

    /**
     * GIVEN Valid gherkin feature text
     * WHEN parsing it to a gherkin feature object
     * THEN the name of the feature is set
     */
    @Test
    public void should_parse_feature_name()
    {
        Feature feature = GherkinParser.parseFeature(TEST_FEATURE);

        assertThat(feature.getName(), equalTo("The name of the feature here"));
    }

    /**
     * GIVEN Valid gherkin feature text
     * WHEN parsing it to a gherkin feature object
     * THEN the description of the feature is set
     */
    @Test
    public void should_parse_feature_description()
    {
        Feature feature = GherkinParser.parseFeature(TEST_FEATURE);

        assertThat(feature.getDescription(), allOf(containsString("Description of the feature."),
                                                   containsString("Description can also have two lines.")));
    }

    /**
     * GIVEN Valid gherkin feature text
     * WHEN parsing it to a gherkin feature object
     * THEN the name of the scenario is set
     */
    @Test
    public void should_parse_scenario_name()
    {
        Feature feature = GherkinParser.parseFeature(TEST_FEATURE);

        List<ScenarioDefinition> scenarios = feature.getChildren();
        assertThat(scenarios.size(), equalTo(1));
        ScenarioDefinition scenario = scenarios.get(0);
        assertThat(scenario.getName(), equalTo("Scenario 1"));
    }

    /**
     * GIVEN Valid gherkin feature text
     * WHEN parsing it to a gherkin feature object
     * THEN the givens of the scenario are set
     */
    @Test
    public void should_parse_scenario_given()
    {
        Feature feature = GherkinParser.parseFeature(TEST_FEATURE);

        List<ScenarioDefinition> scenarios = feature.getChildren();
        assertThat(scenarios.size(), equalTo(1));
        ScenarioDefinition scenario = scenarios.get(0);
        List<Step> steps = scenario.getSteps();
        assertThat(steps.size(), equalTo(6));
        assertThat(steps.get(0), allOf(hasProperty("keyword", startsWith("Given")),
                                       hasProperty("text", equalTo("some precondition"))));
        assertThat(steps.get(1), allOf(hasProperty("keyword", startsWith("And")),
                                       hasProperty("text", equalTo("more preconditions"))));
    }

    /**
     * GIVEN Valid gherkin feature text
     * WHEN parsing it to a gherkin feature object
     * THEN the whens of the scenario are set
     */
    @Test
    public void should_parse_scenario_when()
    {
        Feature feature = GherkinParser.parseFeature(TEST_FEATURE);

        List<ScenarioDefinition> scenarios = feature.getChildren();
        assertThat(scenarios.size(), equalTo(1));
        ScenarioDefinition scenario = scenarios.get(0);
        List<Step> steps = scenario.getSteps();
        assertThat(steps.size(), equalTo(6));
        assertThat(steps.get(2), allOf(hasProperty("keyword", startsWith("When")),
                                       hasProperty("text", equalTo("some action"))));
        assertThat(steps.get(3), allOf(hasProperty("keyword", startsWith("And")),
                                       hasProperty("text", equalTo("more actions"))));
    }

    /**
     * GIVEN Valid gherkin feature text
     * WHEN parsing it to a gherkin feature object
     * THEN the thens of the scenario are set
     */
    @Test
    public void should_parse_scenario_then()
    {
        Feature feature = GherkinParser.parseFeature(TEST_FEATURE);

        List<ScenarioDefinition> scenarios = feature.getChildren();
        assertThat(scenarios.size(), equalTo(1));
        ScenarioDefinition scenario = scenarios.get(0);
        List<Step> steps = scenario.getSteps();
        assertThat(steps.size(), equalTo(6));
        assertThat(steps.get(4), allOf(hasProperty("keyword", startsWith("Then")),
                                       hasProperty("text", equalTo("some result"))));
        assertThat(steps.get(5), allOf(hasProperty("keyword", startsWith("And")),
                                       hasProperty("text", equalTo("more results"))));
    }
}
