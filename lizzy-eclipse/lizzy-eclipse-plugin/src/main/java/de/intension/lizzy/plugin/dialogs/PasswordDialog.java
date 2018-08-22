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
import static de.intension.lizzy.plugin.provider.SecureStorageNodeProvider.IDENTIFICATION;
import static de.intension.lizzy.plugin.provider.SecureStorageNodeProvider.USER;

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.intension.lizzy.plugin.provider.SecureStorageNodeProvider;

/**
 * SWT Dialog element to enter username and password for the ticket system.
 *
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
@SuppressWarnings("restriction")
public class PasswordDialog extends Dialog
{

    @Inject
    private Logger logger;

    private Text   txtUser;
    private Text   txtPassword;

    public PasswordDialog(Shell parentShell)
    {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite container = (Composite)super.createDialogArea(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.marginRight = 5;
        layout.marginLeft = 10;
        container.setLayout(layout);

        Label lblUser = new Label(container, SWT.NONE);
        lblUser.setText("User:");

        txtUser = new Text(container, SWT.BORDER);
        txtUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));
        try {
            txtUser.setText(SecureStorageNodeProvider.get(USER));
        } catch (StorageException se) {
            logger.error(se);
            return container;
        }

        Label lblPassword = new Label(container, SWT.NONE);
        GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false,
                false, 1, 1);
        gd_lblNewLabel.horizontalIndent = 1;
        lblPassword.setLayoutData(gd_lblNewLabel);
        lblPassword.setText("Password:");

        txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        return container;
    }

    @Override
    protected void okPressed()
    {

        try {
            SecureStorageNodeProvider.put(USER, txtUser.getText());
            SecureStorageNodeProvider.put(IDENTIFICATION, txtPassword.getText());
        } catch (StorageException e) {
            message("Storage failure", "Could not store in Secure Storage.", SWT.ICON_ERROR | SWT.OK);
            logger.error(e);
            return;
        }
        super.okPressed();
    }
}
