package de.intension.lizzy.adapter.jira;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import de.intension.lizzy.adapter.Issue;
import de.intension.lizzy.adapter.MarkdownRemover;
import de.intension.lizzy.adapter.MultilineTrimmer;

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
        return createIssue(getJiraClient().getIssueClient().getIssue(ticketId).get());
    }

    /**
     * Retrieves a issues via a filter string.
     *
     * @param filter Jql filter string.
     * @param maxResult Maximum number of issues to be returned.
     */
    public List<Issue> getIssues(String filter, int maxResult)
    {
        Promise<SearchResult> searchResult = getJiraClient().getSearchClient().searchJql(filter, maxResult, 0, null);
        return createIssues(searchResult.claim().getIssues());
    }

    /**
     * Creates a Jira client with the given {@link #uri}, {@link #username} and {@link #password}.
     */
    private JiraRestClient getJiraClient()
    {
        JiraRestClientFactory factory = getFactory();
        return factory.createWithBasicHttpAuthentication(uri, username, password);
    }

    /**
     * Creates an {@link URI} object from the string.
     */
    private URI getURI(String uri)
        throws URISyntaxException
    {
        return new URI(uri);
    }

    private Issue createIssue(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue)
    {
        if (jiraIssue == null) {
            return null;
        }
        return new Issue().setKey(jiraIssue.getKey()).setTitle(jiraIssue.getSummary()).setDescription(prepare(jiraIssue.getDescription()));
    }

    private List<Issue> createIssues(Iterable<com.atlassian.jira.rest.client.api.domain.Issue> jiraIssues)
    {
        List<Issue> issues = new ArrayList<>();
        if (jiraIssues == null) {
            return issues;
        }
        jiraIssues.forEach(issue -> issues.add(createIssue(issue)));
        return issues;
    }

    private JiraRestClientFactory getFactory()
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

    private String prepare(String string)
    {
        String trimmed = MultilineTrimmer.trim(string);
        return MarkdownRemover.toPlainText(trimmed);
    }
}
