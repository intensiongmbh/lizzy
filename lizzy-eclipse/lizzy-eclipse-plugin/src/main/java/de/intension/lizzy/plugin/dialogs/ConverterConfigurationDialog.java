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
package de.intension.lizzy.plugin.dialogs;

import static de.intension.lizzy.plugin.dialogs.Dialogs.message;
import static de.intension.lizzy.plugin.provider.SecureStorageNodeProvider.PROJECT;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.intension.lizzy.plugin.provider.SecureStorageNodeProvider;

/**
 * SWT Dialog element to select a project from the workspace
 * and to enter source folder location and package name.
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class ConverterConfigurationDialog extends Dialog
{

    private Combo  projects;
    private Text   locationInput;
    private Text   packageInput;

    private String location;
    private String packageName;

    protected ConverterConfigurationDialog(Shell parentShell)
    {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite container = (Composite)super.createDialogArea(parent);
        GridLayout layout = new GridLayout(3, false);
        layout.marginRight = 5;
        layout.marginLeft = 10;
        container.setLayout(layout);

        if (createComboBox(container)) {
            createInputFields(container);
        }

        return container;
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton)
    {
        Button button = super.createButton(parent, id, label, defaultButton);
        if (id == IDialogConstants.OK_ID) {
            button.setEnabled(false);
        }
        return button;
    }

    /**
     * Create input fields for source folder location and package name.
     * 
     * @param parent Container where the elements are created in.
     */
    private void createInputFields(Composite parent)
    {
        // source folder location
        Label lblLocation = new Label(parent, SWT.NONE);
        lblLocation.setText("Source folder:");
        locationInput = new Text(parent, SWT.BORDER);
        locationInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        locationInput.setText("src/test/java");
        requiredField(locationInput);
        requiredDecorator(parent);
        // package name
        Label lblPackage = new Label(parent, SWT.NONE);
        lblPackage.setText("Package:");
        Composite cmpPackage = new Composite(parent, SWT.NONE);
        cmpPackage.setLayout(new GridLayout(2, false));
        cmpPackage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        packageInput = new Text(cmpPackage, SWT.BORDER);
        packageInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        Button selectPackage = new Button(cmpPackage, SWT.PUSH);
        selectPackage.setText("…");
        selectPackage.setToolTipText("Select package");
        selectPackage.addSelectionListener(packageBrowser());
        requiredField(packageInput);
        requiredDecorator(parent);
    }

    /**
     * Display a {@link PackageBrowserDialog} when the selectPackage button is clicked.
     */
    private SelectionListener packageBrowser()
    {
        return new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                String projectName = projects.getItem(projects.getSelectionIndex());
                IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
                if (project == null) {
                    return;
                }
                PackageBrowserDialog dialog = Dialogs.packageBrowser(project, locationInput.getText());
                if (dialog.open() == Window.OK) {
                    packageInput.setText(dialog.getSelectedPackage());
                }
            }
        };
    }

    /**
     * Creates a small star to indicate a required field.
     */
    private void requiredDecorator(Composite parent)
    {
        Label reqLoc = new Label(parent, SWT.NONE);
        reqLoc.setText("*");
        reqLoc.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
        reqLoc.setToolTipText("required");
    }

    /**
     * Adds a {@link ModifyListener} to the text field which disables the ok button
     * of the dialog when the field is empty.
     */
    private void requiredField(Text field)
    {
        field.addModifyListener(listener -> new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e)
            {
                if (field.getText().isEmpty()) {
                    toggleButton(false);
                }
                else {
                    toggleButton(true);
                }
            }
        });
    }

    /**
     * Creates a combo box and selects the project stored in the
     * secured storage with key {@link SecureStorageNodeProvider#PROJECT}.
     * <p>
     * Displays an error text instead when there are no projects in the workspace.
     * 
     * @param parent Container where the combo box is created in.
     * @return <code>true</code> if workspace contains projects,
     *         otherwise <code>false</code>
     */
    private boolean createComboBox(Composite parent)
    {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] availableProjects = root.getProjects();
        if (availableProjects.length > 0) {
            projects = new Combo(parent, SWT.READ_ONLY);
            projects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
            String[] projectNames = getProjectNames(availableProjects);
            projects.setItems(projectNames);
            projects.select(getSelectedProject(projectNames));
            return true;
        }
        else {
            Label error = new Label(parent, SWT.NONE);
            error.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
            error.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
            error.setText("There are no projects in the workspace.");
            return false;
        }
    }

    /**
     * Checks the secure storage for a project name
     * which is available in the <code>projectNames</code> array.
     * 
     * @param projectNames Should not be empty.
     * @return index of the found project name or 0 if it was not found in the array.
     */
    private int getSelectedProject(String[] projectNames)
    {
        try {
            String projectName = SecureStorageNodeProvider.get(PROJECT);
            if (!projectName.isEmpty()) {
                for (int i = 0; i < projectNames.length; i++) {
                    if (projectNames[i].equals(projectName)) {
                        return i;
                    }
                }
            }
        } catch (StorageException e) {
            Dialogs.error(e);
        }
        return 0;
    }

    private String[] getProjectNames(IProject[] projects)
    {
        String[] names = new String[projects.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = projects[i].getName();
        }
        return names;
    }

    private void toggleButton(boolean enable)
    {
        Button ok = getButton(IDialogConstants.OK_ID);
        if (ok != null) {
            ok.setEnabled(enable);
        }
    }

    @Override
    protected void okPressed()
    {
        if (projects != null) {
            try {
                SecureStorageNodeProvider.put(PROJECT, projects.getItem(projects.getSelectionIndex()));
            } catch (StorageException e) {
                message("Storage failure", "Could not store in Secure Storage.", SWT.ICON_ERROR | SWT.OK);
                Dialogs.error(e);
            }
            location = locationInput.getText();
            packageName = packageInput.getText();
        }
        super.okPressed();
    }

    public String getLocation()
    {
        return location;
    }

    public String getPackageName()
    {
        return packageName;
    }
}
