package de.intension.lizzy.adapter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class MarkdownRemoverTest
{

    /**
     * Given a text with bold style
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the bold style is removed
     */
    @Test
    public void should_remove_bold_style()
    {
        String text = "*this is bold*";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("this is bold"));
    }

    /**
     * Given a text with italic style
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the italic style is removed
     */
    @Test
    public void should_remove_italic_style()
    {
        String text = "_this is italic_";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("this is italic"));
    }

    /**
     * Given a text with underline
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the underline is removed
     */
    @Test
    public void should_remove_underline()
    {
        String text = "+this is underlined+";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("this is underlined"));
    }

    /**
     * Given a text with color
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the color is removed
     */
    @Test
    @Ignore("Not implemented")
    public void should_remove_text_color()
    {
        String text = "{color:#FF0000}this is red{color}";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("this is red"));
    }

    /**
     * Given a text with strikethrough decorator
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the strikethrough is removed
     */
    @Test
    public void should_remove_strikethrough()
    {
        String text = "-this is striked-";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("this is striked"));
    }

    /**
     * Given a text with subscript decorator
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the subscript is removed
     */
    @Test
    public void should_remove_subscript()
    {
        String text = "~this is subscript~";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("this is subscript"));
    }

    /**
     * Given a text with superscript decorator
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the superscript is removed
     */
    @Test
    public void should_remove_superscript()
    {
        String text = "^this is superscript^";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("this is superscript"));
    }

    /**
     * Given a text with citation decorator
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the citation is removed
     */
    @Test
    public void should_remove_citation()
    {
        String text = "??this is a citation??";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("this is a citation"));
    }

    /**
     * Given a text with monospaced decorator
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the monospace is removed
     */
    @Test
    public void should_remove_monospace()
    {
        String text = "{{this is monospace}}";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("this is monospace"));
    }

    /**
     * Given a text with two list elements
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the list decorators are removed
     */
    @Test
    public void should_remove_list_decorators()
    {
        String text = "* Element 1\n" +
                "* Element 2";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("Element 1Element 2"));
    }

    /**
     * Given a text with two numbered list elements
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the list decorators are removed
     */
    @Test
    public void should_remove_numbered_list_decorators()
    {
        String text = "# Element 1\n" +
                "# Element 2";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("Element 1Element 2"));
    }

    /**
     * Given a text with a table
     * When parsing it with {@link MarkdownRemover#toPlainText(String)}
     * Then the table decorators are removed
     */
    @Test
    @Ignore("Not implemented")
    public void should_remove_table_decorators()
    {
        String text = "||Heading 1||Heading 2||\n" +
                "|Col A1|Col A2|";

        String parsed = MarkdownRemover.toPlainText(text);

        assertThat(parsed, equalTo("Heading 1Heading 2Col A1ColA2"));
    }

    /**
     * Given an empty string
     * When attempting to parse the string
     * Then <code>null</code> is returned
     */
    @Test
    public void should_not_fail_for_empty_string()
    {
        String empty = "";

        String parsed = MarkdownRemover.toPlainText(empty);

        assertThat(parsed, equalTo(null));
    }

    /**
     * Given a null string string
     * When attempting to parse the string
     * Then <code>null</code> is returned
     */
    @Test
    public void should_not_fail_for_null_string()
    {
        String nulled = null;

        String parsed = MarkdownRemover.toPlainText(nulled);

        assertThat(parsed, equalTo(null));
    }
}
