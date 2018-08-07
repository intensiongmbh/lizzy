package de.intension.lizzy.plugin.parts;

import static com.google.common.collect.Lists.newArrayList;
import static de.intension.lizzy.plugin.dialogs.Dialogs.message;
import static de.intension.lizzy.plugin.parts.SearchView.ISSUE_KEY;
import static de.intension.lizzy.plugin.provider.SecureStorageNodeProvider.PROJECT;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import de.intension.lizzy.converter.gherkin.GherkinConverter;
import de.intension.lizzy.plugin.dialogs.ConverterConfigurationDialog;
import de.intension.lizzy.plugin.dialogs.Dialogs;
import de.intension.lizzy.plugin.listener.ExecuteActionListener;
import de.intension.lizzy.plugin.listener.ExecuteActionListener.ListenerAction;
import de.intension.lizzy.plugin.provider.SecureStorageNodeProvider;

/**
 * Eclipse view to display ticket contents (built with SWT).
 */
@SuppressWarnings("restriction")
public class DisplayView
{

    public static final String ID = "lizzy-eclipse-plugin.partdescriptor.displayticket";

    @Inject
    private EPartService       partService;
    @Inject
    private IEclipseContext    context;
    @Inject
    private Logger             logger;

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
        generateButton.setToolTipText("Generate code from description");
        generateButton.addSelectionListener(generateCodeListener());
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

    private SelectionAdapter generateCodeListener()
    {
        return new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                String desc = description.getText();
                if (!desc.isEmpty()) {
                    try {
                        ConverterConfigurationDialog dialog = Dialogs.converterConfiguration();
                        if (dialog.open() == Window.OK) {
                            String projectName = SecureStorageNodeProvider.get(PROJECT);
                            if (projectName.isEmpty()) {
                                return;
                            }
                            String location = dialog.getLocation();
                            String packageName = dialog.getPackageName();

                            String srcFolder = getFullPath(projectName, location);
                            if (srcFolder == null) {
                                message("Folder not found", "The source folder '" + location + "' does not exist.",
                                        SWT.OK | SWT.ICON_WARNING);
                                return;
                            }

                            GherkinConverter converter = new GherkinConverter().setPackageName(packageName).setLocation(srcFolder);
                            converter.convert(desc);
                            message("Generation successful", "Test class was successfully generated. Refresh your project to inspect the changes.",
                                    SWT.OK | SWT.ICON_INFORMATION);
                        }
                    } catch (IOException | StorageException ex) {
                        logger.error(ex);
                    }
                }
            }
        };
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

    private String getFullPath(String projectName, String location)
    {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(projectName);
        IFolder folder = project.getFolder(location);
        if (!folder.exists()) {
            return null;
        }
        return folder.getLocation().toString();
    }
}
