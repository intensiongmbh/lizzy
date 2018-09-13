package de.intension.lizzy.adapter;

import static de.intension.lizzy.adapter.MultilineTrimmer.trim;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MultilineTrimmerTest
{

    /**
     * Given a text with leading space
     * When trimming the string
     * Then leading space is removed from all lines
     */
    @Test
    public void should_remove_leading_space()
    {
        String string = "    First line\n" + "     Second line";

        String trimmed = trim(string);

        assertThat(trimmed, equalTo("First line\nSecond line"));
    }

    /**
     * Given a text with trailing space
     * When trimming the string
     * Then trailing space is removed from all lines
     */
    @Test
    public void should_remove_trailing_space()
    {
        String string = "First line      \n" + "Second line    ";

        String trimmed = trim(string);

        assertThat(trimmed, equalTo("First line\nSecond line"));
    }

    /**
     * Given a text with leading non-breaking space
     * When trimming the string
     * Then leading non-breaking space is removed from all lines
     */
    @Test
    public void should_remove_leading_nonbreaking_space()
    {
        String string = "\u00A0First line\n" + "\u00A0Second line";

        String trimmed = trim(string);

        assertThat(trimmed, equalTo("First line\nSecond line"));
    }

    /**
     * Given a text with trailing non-breaking space
     * When trimming the string
     * Then trailing non-breaking space is removed from all lines
     */
    @Test
    public void should_remove_trailing_nonbreaking_space()
    {
        String string = "First line\u00A0\n" + "Second line\u00A0";

        String trimmed = trim(string);

        assertThat(trimmed, equalTo("First line\nSecond line"));
    }

    /**
     * Given a text with leading tabulation
     * When trimming the string
     * Then tabulation is removed from all lines
     */
    @Test
    public void should_remove_leading_tabulation()
    {
        String string = "\tFirst line\n" + "    Second line"; // 1: tabulation via encoding; 2: tabulation via key pressed

        String trimmed = trim(string);

        assertThat(trimmed, equalTo("First line\nSecond line"));
    }

    /**
     * Given a text with trailing tabulation
     * When trimming the string
     * Then trailing tabulation is removed from all lines
     */
    @Test
    public void should_remove_trailing_tabulation()
    {
        String string = "First line\t\n" + "Second line "; // 1: tabulation via encoding; 2: tabulation via key pressed

        String trimmed = trim(string);

        assertThat(trimmed, equalTo("First line\nSecond line"));
    }

    /**
     * Given an empty string
     * When attempting to trim the string
     * Then <code>null</code> is returned
     */
    @Test
    public void should_not_fail_for_empty_string()
    {
        String empty = "";

        String trimmed = trim(empty);

        assertThat(trimmed, equalTo(null));
    }

    /**
     * Given a null string
     * When attempting to trim the string
     * Then <code>null</code> is returned
     */
    @Test
    public void should_not_fail_for_null_string()
    {
        String nulled = null;

        String trimmed = trim(nulled);

        assertThat(trimmed, equalTo(null));
    }

    /**
     * Given a text with a line containing only whitespaces
     * When trimming the string
     * Then whitespace line is completely removed
     */
    @Test
    public void should_trim_line_of_whitespaces()
    {
        String whitespaces = "First line\n   ";
        
        String trimmed = trim(whitespaces);
        
        assertThat(trimmed, equalTo("First line\n"));
    }
}
