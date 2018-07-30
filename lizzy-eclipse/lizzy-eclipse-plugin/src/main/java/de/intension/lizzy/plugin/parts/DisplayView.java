package de.intension.lizzy.plugin.parts;

import static com.google.common.collect.Lists.newArrayList;
import static de.intension.lizzy.plugin.parts.SearchView.ISSUE_KEY;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Issue;

import de.intension.lizzy.plugin.listener.ExecuteActionListener;
import de.intension.lizzy.plugin.listener.ExecuteActionListener.ListenerAction;

/**
 * Eclipse view to display ticket contents (built with SWT).
 */
public class DisplayView
{

    public static final String ID = "lizzy-eclipse-plugin.partdescriptor.displayticket";

    @Inject
    private EPartService       partService;
    @Inject
    private IEclipseContext    context;

    private Group              title;
    private Text               description;
    private List               attachments;

    @PostConstruct
    public void createPartControl(Composite parent)
    {
        parent.setLayout(new FillLayout(SWT.HORIZONTAL));
        // title:
        title = new Group(parent, SWT.BORDER);
        title.setText("No ticket selected.");
        title.setLayout(new GridLayout(1, false));
        // description:
        Group descGroup = new Group(title, SWT.BORDER);
        descGroup.setText("Description");
        descGroup.setLayout(new GridLayout(2, false));
        descGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        description = new Text(descGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        description.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        Button generateButton = new Button(descGroup, SWT.BORDER);
        generateButton.setText("Generate");
        generateButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
        // attachments:
        Group attGroup = new Group(title, SWT.BORDER);
        attGroup.setText("Attachments");
        attGroup.setLayout(new GridLayout(1, false));
        attGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        attachments = new List(attGroup, SWT.BORDER);
        attachments.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        attachments.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        partService.addPartListener(updateFieldsListener());
    }

    private ExecuteActionListener updateFieldsListener()
    {
        return new ExecuteActionListener(ID, new ListenerAction() {

            @Override
            public void execute()
            {
                updateFields();
            }
        });
    }

    private void updateFields()
    {
        Issue issue = (Issue)context.getParent().get(ISSUE_KEY);
        if (issue != null) {
            title.setText(issue.getKey() + ": " + issue.getSummary());
            description.setText(issue.getDescription());
            for (Attachment attachment : newArrayList(issue.getAttachments())) {
                attachments.add(attachment.getFilename());
            }
        }
    }
}
