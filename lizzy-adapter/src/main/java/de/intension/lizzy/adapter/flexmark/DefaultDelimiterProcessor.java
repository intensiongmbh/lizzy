package de.intension.lizzy.adapter.flexmark;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.internal.Delimiter;
import com.vladsch.flexmark.parser.InlineParser;
import com.vladsch.flexmark.parser.delimiter.DelimiterProcessor;
import com.vladsch.flexmark.parser.delimiter.DelimiterRun;

/**
 * Default delimiter processor to match a string with minimum length of one.
 */
public class DefaultDelimiterProcessor
    implements DelimiterProcessor
{

    @Override
    public char getOpeningCharacter()
    {
        return 0;
    }

    @Override
    public char getClosingCharacter()
    {
        return 0;
    }

    @Override
    public int getMinLength()
    {
        return 1;
    }

    @Override
    public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer)
    {
        if (opener.length() >= 1 && closer.length() >= 1) {
            return 1;
        }
        else {
            return 0;
        }
    }

    @Override
    public void process(Delimiter opener, Delimiter closer, int delimitersUsed)
    {
        // only needed for html parsing
    }

    @Override
    public Node unmatchedDelimiterNode(InlineParser inlineParser, DelimiterRun delimiter)
    {
        return null;
    }

    @Override
    public boolean canBeOpener(String before, String after, boolean leftFlanking, boolean rightFlanking, boolean beforeIsPunctuation,
                               boolean afterIsPunctuation, boolean beforeIsWhitespace, boolean afterIsWhiteSpace)
    {
        return true;
    }

    @Override
    public boolean canBeCloser(String before, String after, boolean leftFlanking, boolean rightFlanking, boolean beforeIsPunctuation,
                               boolean afterIsPunctuation, boolean beforeIsWhitespace, boolean afterIsWhiteSpace)
    {
        return true;
    }

    @Override
    public boolean skipNonOpenerCloser()
    {
        return false;
    }
}
