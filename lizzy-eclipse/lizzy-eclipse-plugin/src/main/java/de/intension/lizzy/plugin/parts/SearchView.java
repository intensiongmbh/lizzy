package de.intension.lizzy.plugin.parts;

import static de.intension.lizzy.plugin.dialogs.Dialogs.message;
import static de.intension.lizzy.plugin.provider.SecureStorageNodeProvider.CONTENT_PROVIDER_URL;
import static de.intension.lizzy.plugin.provider.SecureStorageNodeProvider.PASSWORD;
import static de.intension.lizzy.plugin.provider.SecureStorageNodeProvider.USER;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.eclipse.e4.ui.workbench.modeling.EPartService.PartState.VISIBLE;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;

import de.intension.lizzy.adapter.jira.JiraAdapter;
import de.intension.lizzy.plugin.dialogs.Dialogs;
import de.intension.lizzy.plugin.dialogs.PasswordDialog;
import de.intension.lizzy.plugin.provider.SecureStorageNodeProvider;

/**
 * Eclipse view to search for a ticket (built with SWT).
 */
@SuppressWarnings("restriction")
public class SearchView
{

    public static final String ID                 = "lizzy-eclipse-plugin.partdescriptor.searchticket";
    public static final String ISSUE_KEY          = "selectedIssue";

    @Inject
    private EPartService       partService;
    @Inject
    private IEclipseContext    context;
    @Inject
    private Logger             logger;

    private Text               urlInput;
    private Text               searchInput;
    private List               issues;

    private Issue              issue;

    private boolean            invalidCredentials = false;

    @PostConstruct
    public void createPartControl(Composite parent)
        throws StorageException
    {
        parent.setLayout(new GridLayout(1, true));

        Composite urlComp = new Composite(parent, SWT.NONE);
        urlComp.setLayout(new GridLayout(2, false));
        urlComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        Label urlLbl = new Label(urlComp, SWT.NONE);
        urlLbl.setText("URL:");
        urlInput = new Text(urlComp, SWT.NONE);
        urlInput.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        urlInput.setText(SecureStorageNodeProvider.get(CONTENT_PROVIDER_URL));

        Composite searchComp = new Composite(parent, SWT.NONE);
        searchComp.setLayout(new GridLayout(2, false));
        searchComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        searchInput = new Text(searchComp, SWT.BORDER);
        searchInput.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        searchInput.addListener(SWT.Traverse, searchTicketListener(SWT.TRAVERSE_RETURN));
        Button searchButton = new Button(searchComp, SWT.BORDER);
        searchButton.setText("Search");
        searchButton.addListener(SWT.Selection, searchTicketListener(SWT.Selection));

        issues = new List(parent, SWT.BORDER);
        issues.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        issues.addSelectionListener(displayTicketListener());
    }

    /**
     * Listener to display the selected ticket on {@link DisplayView}.
     */
    private SelectionAdapter displayTicketListener()
    {
        return new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                context.getParent().set(ISSUE_KEY, issue);
                partService.showPart(DisplayView.ID, VISIBLE);
            }
        };
    }

    /**
     * Listener to search for a ticket.
     * 
     * @param eventType SWT event type to trigger the search.
     */
    private Listener searchTicketListener(int eventType)
    {
        return new Listener() {

            @Override
            public void handleEvent(Event event)
            {
                if (event.type == eventType) {
                    searchTicket();
                }
            }
        };
    }

    private void searchTicket()
    {
        try {
            String url = urlInput.getText();
            if (url.isEmpty()) {
                message("URL missing", "Please enter a valid URL to JIRA.", SWT.ICON_WARNING | SWT.OK);
                return;
            }
            else {
                SecureStorageNodeProvider.put(CONTENT_PROVIDER_URL, url);
            }

            String ticketId = searchInput.getText();
            if (ticketId.isEmpty()) {
                message("No search filter", "Please enter a search filter for the ticket.", SWT.ICON_WARNING | SWT.OK);
                return;
            }
            JiraAdapter adapter = login(url);

            issue = adapter.getIssue(ticketId);
            issues.setItems(issue.getKey() + ": " + issue.getSummary());
            invalidCredentials = false;
        } catch (Exception ex) {
            RestClientException rce = getThrowableFromCause(ex, RestClientException.class);
            if (rce != null && (rce.getStatusCode().get() == UNAUTHORIZED.getStatusCode() || rce.getStatusCode().get() == FORBIDDEN.getStatusCode())) {
                invalidCredentials = true;
                message(Status.fromStatusCode(rce.getStatusCode().get()).toString(), "Access is denied due to invalid credentials.",
                        SWT.ICON_ERROR | SWT.OK);
                return;
            }
            logger.error(ex);
        }
    }

    /**
     * Fetches jira credentials via {@link PasswordDialog} and opens Jira connection.
     * 
     * @param uri URI to the ticket provider instance.
     */
    private JiraAdapter login(String uri)
        throws Exception
    {
        String user = SecureStorageNodeProvider.get(USER);
        String password = SecureStorageNodeProvider.get(PASSWORD);
        if (invalidCredentials || user.isEmpty() || password.isEmpty()) {
            if (Dialogs.credentials() == Window.OK) {
                user = SecureStorageNodeProvider.get(USER);
                password = SecureStorageNodeProvider.get(PASSWORD);
            }
        }
        return new JiraAdapter(uri, user, password);
    }

    @Focus
    public void setFocus()
    {
        searchInput.setFocus();
    }

    @SuppressWarnings("unchecked")
    private <T extends Throwable> T getThrowableFromCause(Throwable t, Class<T> type)
    {
        if (t == null) {
            return null;
        }
        if (type.isInstance(t)) {
            return (T)t;
        }
        return getThrowableFromCause(t.getCause(), type);
    }
}
