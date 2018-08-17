package de.intension.lizzy.adapter.flexmark;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.Parser.Builder;
import com.vladsch.flexmark.util.options.MutableDataHolder;

/**
 * Extension to call {@link StrikethroughExtension} to match strings surrounded by '-'.
 */
public class StrikethroughExtension
    implements Parser.ParserExtension
{

    public static Extension create()
    {
        return new StrikethroughExtension();
    }

    @Override
    public void parserOptions(MutableDataHolder options)
    {
        // parser options are not needed for this extension
    }

    @Override
    public void extend(Builder parserBuilder)
    {
        parserBuilder.customDelimiterProcessor(new StrikethroughDelimiterProcessor());
    }
}
