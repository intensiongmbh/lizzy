package de.intension.lizzy.adapter.jira;

import static com.atlassian.fugue.Iterables.iterable;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import de.intension.lizzy.adapter.Issue;

public class JiraAdapterTest
{

    private static final String URI          = "https://hub.intension.de/";
    private static final String USERNAME     = "user123";
    private static final String PASSWORD     = "Password123!";
    private static final String TICKET_ID    = "LIZZY-123";
    private static final String TICKET_DESC  = "test description";
    private static final String VALID_FILTER = "id=LIZZY-1";

    /**
     * GIVEN Jira adapter with valid credentials
     * WHEN requesting ticket description
     * THEN corresponding ticket description is returned
     */
    @Test
    public void should_return_ticket_description()
        throws Exception
    {
        JiraAdapter adapter = new JiraAdapter(URI, USERNAME, PASSWORD).setFactory(setupFactory());

        String description = adapter.getTicketDescription(TICKET_ID);

        assertThat(description, equalTo(TICKET_DESC));
    }

    /**
     * GIVEN Jira adapter with valid credentials
     * WHEN requesting ticket description for unkown ticketId
     * THEN {@link AdapterException} is thrown
     */
    @Test
    public void should_fail_for_unkown_ticket_id()
        throws Exception
    {
        JiraAdapter adapter = new JiraAdapter(URI, USERNAME, PASSWORD).setFactory(setupFactory());

        try {
            adapter.getTicketDescription("UNKNOWN-123");
            fail("Should not succeed for unknown ticket id.");
        } catch (ExecutionException e) {
            RestClientException rce = getExceptionFromCause(e, RestClientException.class);
            assertThat(rce, notNullValue());
            assertThat(rce.getStatusCode().get(), equalTo(Status.NOT_FOUND.getStatusCode()));
        }
    }

    /**
     * GIVEN Invalid URI string
     * WHEN instanciating JiraAdapter with said URI
     * THEN {@link AdapterException} is thrown
     */
    @Test
    public void should_fail_for_invalid_URI()
    {
        try {
            String invalidURI = "invalid uri";

            new JiraAdapter(invalidURI, USERNAME, PASSWORD);
            fail("Should not create adapter instance with invalid URI.");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("invalid uri"));
        }
    }

    /**
     * GIVEN Jira adapter with valid credentials
     * WHEN requesting ticket via filter string
     * THEN matching tickets are returned
     */
    @Test
    public void should_return_tickets_for_matching_filter()
        throws Exception
    {
        JiraAdapter adapter = new JiraAdapter(URI, USERNAME, PASSWORD).setFactory(setupFactory());

        List<Issue> issues = adapter.getIssues(VALID_FILTER, 10);

        assertThat(issues, contains(hasProperty("description", equalTo(TICKET_DESC))));
    }

    /**
     * GIVEN Jira adapter with valid credentials
     * WHEN requesting ticket via filter string for non existent ticket
     * THEN no tickets are returned in the searchresult
     */
    @Test
    public void should_not_return_tickets_for_non_matching_filter()
        throws Exception
    {
        JiraAdapter adapter = new JiraAdapter(URI, USERNAME, PASSWORD).setFactory(setupFactory());

        List<Issue> issues = adapter.getIssues("id = FAIL-1", 10);

        assertThat(issues, iterableWithSize(0));
    }

    /**
     * Given Jira adapter with valid credentials
     * When requesting ticket via ticket id
     * And 'id=' is missing
     * Then filter gets autocompleted correctly
     * And matching ticket gets returned
     */
    @Test
    public void should_return_ticket_for_incomplete_filter_with_ticket_id()
        throws Exception
    {
        JiraAdapter adapter = new JiraAdapter(URI, USERNAME, PASSWORD).setFactory(setupFactory());

        List<Issue> issues = adapter.getIssues("LIZZY-1", 10);

        assertThat(issues, contains(hasProperty("description", equalTo(TICKET_DESC))));
    }

    /**
     * Setup a {@link JiraRestClientFactory} mock.
     */
    @SuppressWarnings("unchecked")
    private JiraRestClientFactory setupFactory()
        throws Exception
    {
        AsynchronousJiraRestClientFactory factory = mock(AsynchronousJiraRestClientFactory.class);
        JiraRestClient client = mock(JiraRestClient.class);
        when(factory.createWithBasicHttpAuthentication(any(URI.class), eq(USERNAME), eq(PASSWORD)))
            .thenReturn(client);
        IssueRestClient issueClient = mock(IssueRestClient.class);
        when(client.getIssueClient()).thenReturn(issueClient);
        Promise<com.atlassian.jira.rest.client.api.domain.Issue> promise = mock(Promise.class);
        when(issueClient.getIssue(TICKET_ID)).thenReturn(promise);
        ExecutionException executionException = new ExecutionException("test message", new RestClientException(mock(Throwable.class), 404));
        Promise<com.atlassian.jira.rest.client.api.domain.Issue> errorPromise = mock(Promise.class);
        when(issueClient.getIssue(not(eq(TICKET_ID)))).thenReturn(errorPromise);
        when(errorPromise.get()).thenThrow(executionException);
        com.atlassian.jira.rest.client.api.domain.Issue issue = mock(com.atlassian.jira.rest.client.api.domain.Issue.class);
        when(promise.get()).thenReturn(issue);
        when(issue.getDescription()).thenReturn(TICKET_DESC);
        Attachment attachment1 = mock(Attachment.class);
        when(attachment1.getFilename()).thenReturn("testFile1.txt");
        Attachment attachment2 = mock(Attachment.class);
        when(attachment2.getFilename()).thenReturn("testFile2.txt");
        Iterable<Attachment> attachments = iterable(attachment1, attachment2);
        when(issue.getAttachments()).thenReturn(attachments);
        // for search:
        SearchRestClient searchClient = mock(SearchRestClient.class);
        when(client.getSearchClient()).thenReturn(searchClient);
        Promise<SearchResult> resultPromise = mock(Promise.class);
        when(searchClient.searchJql(eq(VALID_FILTER), any(Integer.class), any(), any())).thenReturn(resultPromise);
        SearchResult searchResult = mock(SearchResult.class);
        when(resultPromise.claim()).thenReturn(searchResult);
        when(searchResult.getIssues()).thenReturn(iterable(issue));
        // search with empty result:
        Promise<SearchResult> noResultPromise = mock(Promise.class);
        when(searchClient.searchJql(not(eq(VALID_FILTER)), any(), any(), any())).thenReturn(noResultPromise);
        SearchResult noResult = mock(SearchResult.class);
        when(noResultPromise.claim()).thenReturn(noResult);
        when(noResult.getIssues()).thenReturn(iterable());
        return factory;
    }

    private <T extends Throwable> T getExceptionFromCause(Throwable e, Class<T> type)
    {
        if (e == null) {
            return null;
        }
        if (type.isInstance(e)) {
            return type.cast(e);
        }
        return getExceptionFromCause(e.getCause(), type);
    }
}
