/*******************************************************************************
 * Copyright 2018 Intension GmbH (https://www.intension.de)
 * and other contributors as indicated by the @author tags.
 * Licensed under the Eclipse Public License - v 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.eclipse.org/legal/epl-2.0/
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.intension.lizzy.plugin.parts;

import static de.intension.lizzy.plugin.dialogs.Dialogs.message;
import static de.intension.lizzy.plugin.provider.SecureStorageNodeProvider.CONTENT_PROVIDER_URL;
import static de.intension.lizzy.plugin.provider.SecureStorageNodeProvider.IDENTIFICATION;
import static de.intension.lizzy.plugin.provider.SecureStorageNodeProvider.USER;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.eclipse.e4.ui.workbench.modeling.EPartService.PartState.VISIBLE;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.eclipse.e4.core.contexts.IEclipseContext;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.atlassian.jira.rest.client.api.RestClientException;

import de.intension.lizzy.adapter.Issue;
import de.intension.lizzy.adapter.jira.JiraAdapter;
import de.intension.lizzy.plugin.dialogs.Dialogs;
import de.intension.lizzy.plugin.dialogs.PasswordDialog;
import de.intension.lizzy.plugin.provider.SecureStorageNodeProvider;

/**
 * Eclipse view to search for a ticket (built with SWT).
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class SearchView
{

    public static final String    ID                 = "lizzy-eclipse-plugin.partdescriptor.searchticket";
    public static final String    ISSUE_KEY          = "selectedIssue";

    @Inject
    private EPartService          partService;
    @Inject
    private IEclipseContext       context;

    private Text                  urlInput;
    private Text                  searchInput;
    private List                  issueListView;

    private java.util.List<Issue> issues;

    private boolean               invalidCredentials = false;

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
        searchComp.setLayout(new GridLayout(3, false));
        searchComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        Button help = new Button(searchComp, SWT.NONE);
        help.setText(" ? ");
        help.setToolTipText("Advanced searching - Jira");
        help.addSelectionListener(webpageListener("https://confluence.atlassian.com/jirasoftwarecloud/advanced-searching-764478330.html"));
        searchInput = new Text(searchComp, SWT.BORDER);
        searchInput.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        searchInput.setMessage("JQL search filter");
        searchInput.addListener(SWT.Traverse, searchTicketListener(SWT.TRAVERSE_RETURN));
        Button searchButton = new Button(searchComp, SWT.BORDER);
        searchButton.setText("Search");
        searchButton.addListener(SWT.Selection, searchTicketListener(SWT.Selection));

        issueListView = new List(parent, SWT.BORDER | SWT.V_SCROLL);
        issueListView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        issueListView.addSelectionListener(displayTicketListener());
    }

    /**
     * Listener to open a web page with desired url.
     * Uses the eclipse default browser.
     * 
     * @param url address to the page.
     */
    private SelectionAdapter webpageListener(String url)
    {
        return new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                try {
                    IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
                    IWebBrowser browser = support.createBrowser(null);
                    browser.openURL(new URL(url));
                } catch (PartInitException | MalformedURLException ex) {
                    Dialogs.error(ex);
                }
            }
        };
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
                context.getParent().set(ISSUE_KEY, issues.get(issueListView.getSelectionIndex()));
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
                    searchTickets();
                }
            }
        };
    }

    private void searchTickets()
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

            String filterString = searchInput.getText();
            if (filterString.isEmpty()) {
                message("No search filter", "Please enter a search filter for the ticket.", SWT.ICON_WARNING | SWT.OK);
                return;
            }
            JiraAdapter adapter = login(url);

            issues = adapter.getIssues(filterString, 10);
            issueListView.setItems(getDisplayNames(issues));
            invalidCredentials = false;
        } catch (Exception ex) {
            RestClientException rce = getThrowableFromCause(ex, RestClientException.class);
            if (rce != null) {
                if (rce.getStatusCode().get() == UNAUTHORIZED.getStatusCode() || rce.getStatusCode().get() == FORBIDDEN.getStatusCode()) {
                    invalidCredentials = true;
                    message(Status.fromStatusCode(rce.getStatusCode().get()).toString(), "Access is denied due to invalid credentials.",
                            SWT.ICON_ERROR | SWT.OK);
                    return;
                }
                else if (rce.getStatusCode().get() == BAD_REQUEST.getStatusCode()) {
                    message(Status.fromStatusCode(rce.getStatusCode().get()).toString(), "Ticket was not found.\n" + rce.getMessage(),
                            SWT.ICON_WARNING | SWT.OK);
                    issueListView.setItems();
                    return;
                }
            }
            Dialogs.error(ex);
        }
    }

    private String[] getDisplayNames(Iterable<Issue> issues)
    {
        Collection<String> result = new ArrayList<>();
        issues.forEach(issue -> result.add(issue.getKey() + ": " + issue.getTitle()));
        return result.toArray(new String[0]);
    }

    /**
     * Fetches jira credentials via {@link PasswordDialog} and opens Jira connection.
     * 
     * @param uri URI to the ticket provider instance.
     */
    private JiraAdapter login(String uri)
        throws StorageException, URISyntaxException
    {
        String user = SecureStorageNodeProvider.get(USER);
        String password = SecureStorageNodeProvider.get(IDENTIFICATION);
        if ((invalidCredentials || user.isEmpty() || password.isEmpty()) && Dialogs.credentials() == Window.OK) {
            user = SecureStorageNodeProvider.get(USER);
            password = SecureStorageNodeProvider.get(IDENTIFICATION);
        }
        return new JiraAdapter(uri, user, password);
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
