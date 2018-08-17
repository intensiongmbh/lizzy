package de.intension.lizzy.adapter.flexmark;

/**
 * Processor to match string surrounded by '?'.
 */
public class CitationDelimiterProcessor
        extends DefaultDelimiterProcessor
{

    @Override
    public char getOpeningCharacter()
    {
        return '?';
    }

    @Override
    public char getClosingCharacter()
    {
        return '?';
    }
}
