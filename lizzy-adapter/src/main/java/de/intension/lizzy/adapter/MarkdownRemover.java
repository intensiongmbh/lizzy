/*******************************************************************************
 * Copyright 2018 Intension GmbH (https://www.intension.de)
 * and other contributors as indicated by the @author tags.
 * 
 * Licensed under the Eclipse Public License - v 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.eclipse.org/legal/epl-2.0/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.intension.lizzy.adapter;

import static java.util.Arrays.asList;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ast.util.TextCollectingVisitor;
import com.vladsch.flexmark.ext.gfm.strikethrough.SubscriptExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.superscript.SuperscriptExtension;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import de.intension.lizzy.adapter.flexmark.CitationExtension;
import de.intension.lizzy.adapter.flexmark.MonospaceExtension;
import de.intension.lizzy.adapter.flexmark.StrikethroughExtension;
import de.intension.lizzy.adapter.flexmark.UnderlineExtension;

/**
 * Utility class to remove markdown from a text.
 * Does not support removal of text color (e.g. <code>{color:#FF0000}test{color}</code>)
 * or tables yet.
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class MarkdownRemover
{

    static final DataHolder OPTIONS = new MutableDataSet()
        .set(Parser.EXTENSIONS, asList(SubscriptExtension.create(), SuperscriptExtension.create(),
                                       MonospaceExtension.create(), CitationExtension.create(),
                                       UnderlineExtension.create(), StrikethroughExtension.create()));

    static final Parser     PARSER  = Parser.builder(OPTIONS).build();

    private MarkdownRemover()
    {
        // utility class
    }

    /**
     * Uses <a href="https://github.com/vsch/flexmark-java/wiki">Flexmark</a>
     * to remove markdown from a text.
     * 
     * @param string Possibly containing markdown.
     * @return Plain text without markdown.
     */
    public static String toPlainText(String string)
    {
        Node document = PARSER.parse(string);
        TextCollectingVisitor textCollectingVisitor = new TextCollectingVisitor();
        return textCollectingVisitor.collectAndGetText(document);
    }
}
