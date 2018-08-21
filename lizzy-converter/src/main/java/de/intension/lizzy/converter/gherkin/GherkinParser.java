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
        Feature feature = parser.parse(prepare(gherkin), matcher).getFeature();
        return feature;
    }

    /**
     * Removes leading and trailing whitespaces from all lines.
     * 
     * @param string Multi-line text
     * @return Formatted string
     */
    private static String prepare(String string)
    {
        String[] lines = string.split("\n");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String trimmed = trim(lines[i]);
            if (!trimmed.isEmpty()) {
                result.append(trimmed + (i == lines.length - 1 ? "" : "\n"));
            }
        }
        return result.toString();
    }

    /**
     * Removes leading and trailing whitespaces and tabulation.
     * 
     * @return Formatted string
     */
    private static String trim(String string)
    {
        char[] chars = string.toCharArray();
        int start = 0;
        // iterate through string from the start
        for (char c : chars) {
            if (isWhitespace(c)) {
                start++;
            }
            else {
                break;
            }
        }
        int end = string.length();
        // iterate through string from the end
        for (int i = chars.length - 1; i > 0; i--) {
            char c = chars[i];
            if (isWhitespace(c)) {
                end--;
            }
            else {
                break;
            }
        }
        return string.substring(start, end);
    }

    /**
     * Matches any whitespace including non-breaking and tabulation.
     */
    private static boolean isWhitespace(char c)
    {
        return (int)c < 33 || (int)c > 126;
    }
}
