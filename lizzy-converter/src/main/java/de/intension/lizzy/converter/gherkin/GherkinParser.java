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
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class GherkinParser
{

    private GherkinParser()
    {
        throw new IllegalStateException("Utility class");
    }

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
        return parser.parse(gherkin, matcher).getFeature();
    }
}
