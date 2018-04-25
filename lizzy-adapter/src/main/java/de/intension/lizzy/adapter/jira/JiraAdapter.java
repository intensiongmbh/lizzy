package de.intension.lizzy.adapter.jira;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

/**
 * Adapter for the Jira Rest API.
 * <p>
 * For example to retrieve an issue with the id 'INT-42' use:
 *
 * <pre>
 * <code>new JiraAdapter("https://jira.atlassian.com/", "admin", "Password123!").getIssue("INT-42")</code>
 * </pre>
 */
public class JiraAdapter
{

    private final URI             uri;
    private final String          username;
    private final String          password;

    private JiraRestClientFactory factory;

    public JiraAdapter(String uri, String username, String password)
        throws URISyntaxException
    {
        this.uri = getURI(uri);
        this.username = username;
        this.password = password;
    }

    /**
     * Retrieves the attachments of a Jira ticket.
     *
     * @param ticketId Ticket id of the issue.
     * @return attachments of the issue as list.
     */
    public Iterable<Attachment> getAttachments(String ticketId)
        throws InterruptedException, ExecutionException
    {
        return getIssue(ticketId).getAttachments();
    }

    /**
     * Retrieves the description of a Jira ticket.
     *
     * @param ticketId Ticket id of the issue.
     * @return description of the issue.
     */
    public String getTicketDescription(String ticketId)
        throws InterruptedException, ExecutionException
    {
        return getIssue(ticketId).getDescription();
    }

    /**
     * Retrieves an issue for the corresponding ticketId.
     *
     * @param ticketId Ticket id of the issue.
     */
    public Issue getIssue(String ticketId)
        throws InterruptedException, ExecutionException
    {
        return getJiraClient().getIssueClient().getIssue(ticketId).get();
    }

    /**
     * Creates a Jira client with the given {@link #uri}, {@link #username} and {@link #password}.
     */
    public JiraRestClient getJiraClient()
    {
        JiraRestClientFactory factory = getFactory();
        return factory.createWithBasicHttpAuthentication(uri, username, password);
    }

    /**
     * Creates an {@link URI} object from the string.
     */
    public URI getURI(String uri)
        throws URISyntaxException
    {
        return new URI(uri);
    }

    public JiraRestClientFactory getFactory()
    {
        if (factory == null) {
            factory = new AsynchronousJiraRestClientFactory();
        }
        return factory;
    }

    public JiraAdapter setFactory(JiraRestClientFactory factory)
    {
        this.factory = factory;
        return this;
    }
}
