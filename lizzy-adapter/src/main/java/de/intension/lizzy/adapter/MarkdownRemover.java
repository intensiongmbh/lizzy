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
