package de.intension.lizzy.adapter;

/**
 * Utility class to trim leading and trailing whitespaces from all lines of a
 * multi-line text. Removes all kinds of whitespaces, including non-breaking and
 * tabulation.
 * 
 * @author Ingo Kuba
 */
public class MultilineTrimmer
{

    private MultilineTrimmer()
    {
        // utility class
    }

    /**
     * Removes leading and trailing whitespaces from all lines.
     * 
     * @param string Multi-line text
     * @return Formatted string
     */
    public static String trim(String string)
    {
        String[] lines = string.split("\n");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String trimmed = trimLine(lines[i]);
            if (!trimmed.isEmpty()) {
                result.append(trimmed + (i == lines.length - 1 ? "" : "\n"));
            }
        }
        return result.toString();
    }

    /**
     * Removes leading and trailing whitespaces and tabulation.
     * 
     * @return Formatted string
     */
    private static String trimLine(String string)
    {
        char[] chars = string.toCharArray();
        int start = 0;
        // iterate through string from the start
        for (char c : chars) {
            if (isWhitespace(c)) {
                start++;
            }
            else {
                break;
            }
        }
        int end = string.length();
        // iterate through string from the end
        for (int i = chars.length - 1; i > 0; i--) {
            char c = chars[i];
            if (isWhitespace(c)) {
                end--;
            }
            else {
                break;
            }
        }
        return string.substring(start, end);
    }

    /**
     * Matches any whitespace including non-breaking and tabulation.
     */
    private static boolean isWhitespace(char c)
    {
        return (int)c < 33 || (int)c > 126;
    }
}
