package de.intension.lizzy.adapter.jira;

import static com.atlassian.fugue.Iterables.iterable;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

public class JiraAdapterTest
{

    private static final String URI         = "https://hub.intension.de/";
    private static final String USERNAME    = "user123";
    private static final String PASSWORD    = "Password123!";
    private static final String TICKET_ID   = "LIZZY-123";
    private static final String TICKET_DESC = "test description";

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
     * WHEN requesting ticket attachments
     * THEN corresponding ticket attachment URL is returned
     */
    @Test
    @SuppressWarnings("unchecked")
    public void should_return_ticket_attachments()
        throws Exception
    {
        JiraAdapter adapter = new JiraAdapter(URI, USERNAME, PASSWORD).setFactory(setupFactory());

        Iterable<Attachment> attachments = adapter.getAttachments(TICKET_ID);

        assertThat(attachments, containsInAnyOrder(hasProperty("filename", equalTo("testFile1.txt")),
                                                   hasProperty("filename", equalTo("testFile2.txt"))));
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
        Promise<Issue> promise = mock(Promise.class);
        when(issueClient.getIssue(TICKET_ID)).thenReturn(promise);
        ExecutionException executionException = new ExecutionException("test message", new RestClientException(mock(Throwable.class), 404));
        Promise<Issue> errorPromise = mock(Promise.class);
        when(issueClient.getIssue(not(eq(TICKET_ID)))).thenReturn(errorPromise);
        when(errorPromise.get()).thenThrow(executionException);
        Issue issue = mock(Issue.class);
        when(promise.get()).thenReturn(issue);
        when(issue.getDescription()).thenReturn(TICKET_DESC);
        Attachment attachment1 = mock(Attachment.class);
        when(attachment1.getFilename()).thenReturn("testFile1.txt");
        Attachment attachment2 = mock(Attachment.class);
        when(attachment2.getFilename()).thenReturn("testFile2.txt");
        Iterable<Attachment> attachments = iterable(attachment1, attachment2);
        when(issue.getAttachments()).thenReturn(attachments);
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
