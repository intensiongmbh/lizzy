package de.intension.lizzy.adapter.flexmark;

/**
 * Processor to match string surrounded by '{' and '}'.
 */
public class MonospaceDelimiterProcessor
        extends DefaultDelimiterProcessor
{

    @Override
    public char getOpeningCharacter()
    {
        return '{';
    }

    @Override
    public char getClosingCharacter()
    {
        return '}';
    }
}
