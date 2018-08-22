package de.intension.lizzy.adapter;

import static de.intension.lizzy.adapter.MultilineTrimmer.trim;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
}
