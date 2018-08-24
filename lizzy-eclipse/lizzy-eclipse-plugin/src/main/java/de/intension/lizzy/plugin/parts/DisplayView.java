/*******************************************************************************
 * Copyright 2018 Intension GmbH (https://www.intension.de)
 * and other contributors as indicated by the @author tags.
 * 
 * Licensed under the Eclipse Public License - v 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.eclipse.org/legal/epl-2.0/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.intension.lizzy.plugin.parts;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.intension.lizzy.adapter.Issue;
import de.intension.lizzy.converter.gherkin.GherkinConverter;
import de.intension.lizzy.plugin.dialogs.ConverterConfigurationDialog;
import de.intension.lizzy.plugin.dialogs.Dialogs;
import de.intension.lizzy.plugin.listener.ExecuteActionListener;
import de.intension.lizzy.plugin.listener.ExecuteActionListener.ListenerAction;
import de.intension.lizzy.plugin.provider.SecureStorageNodeProvider;

/**
 * Eclipse view to display ticket contents (built with SWT).
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class DisplayView
{

    public static final String ID = "lizzy-eclipse-plugin.partdescriptor.displayticket";

    @Inject
    private EPartService       partService;
    @Inject
    private IEclipseContext    context;

    private Label              title;
    private Text               description;

    @PostConstruct
    public void createPartControl(Composite parent)
    {
        parent.setLayout(new GridLayout(2, false));
        // title:
        title = new Label(parent, SWT.NONE);
        title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        title.setText("No ticket selected.");
        // description:
        Group descGroup = new Group(parent, SWT.BORDER);
        descGroup.setText("Description");
        descGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        descGroup.setLayout(new FillLayout());
        description = new Text(descGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        Button generateButton = new Button(parent, SWT.BORDER);
        generateButton.setText("Generate");
        generateButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
        generateButton.setToolTipText("Generate code from description");
        generateButton.addSelectionListener(generateCodeListener());

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
                        Dialogs.error(ex);
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
            title.setText(issue.getKey() + ": " + issue.getTitle());
            description.setText(issue.getDescription());
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
