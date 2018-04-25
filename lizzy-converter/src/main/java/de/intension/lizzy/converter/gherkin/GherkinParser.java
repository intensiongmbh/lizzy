package de.intension.lizzy.converter.gherkin;

import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.Feature;
import gherkin.ast.GherkinDocument;

/**
 * Parses a gherkin string to a cucumber feature object.
 * Gherkin is a BusinessReadableDSL format for test scenarios.
 *
 * @see https://cucumber.io/docs/reference
 */
public class GherkinParser
{

    /**
     * Parses the feature string to an object.
     *
     * @param gherkin String containing the feature with its scenarios
     * @return Feature object
     */
    public static Feature parseFeature(String gherkin)
    {
        Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        TokenMatcher matcher = new TokenMatcher();
        Feature feature = parser.parse(gherkin, matcher).getFeature();
        return feature;
    }
}
