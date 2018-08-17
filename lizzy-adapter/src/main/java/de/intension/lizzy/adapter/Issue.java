package de.intension.lizzy.adapter;

/**
 * Wrapper object for issues from requirement management systems.
 */
public class Issue
{

    private String key;
    private String title;
    private String description;

    /**
     * Unique identifier of the issue.
     */
    public String getKey()
    {
        return key;
    }

    public Issue setKey(String key)
    {
        this.key = key;
        return this;
    }

    /**
     * Summarizing title of the issue.
     */
    public String getTitle()
    {
        return title;
    }

    public Issue setTitle(String title)
    {
        this.title = title;
        return this;
    }

    /**
     * Content of the description text.
     */
    public String getDescription()
    {
        return description;
    }

    public Issue setDescription(String description)
    {
        this.description = description;
        return this;
    }
}
