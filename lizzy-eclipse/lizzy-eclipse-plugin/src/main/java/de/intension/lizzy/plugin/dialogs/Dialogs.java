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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

/**
 * Collection of static methods to provide easy access to SWT Dialog elements.
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class Dialogs
{

    /**
     * Displays a MessageBox.
     * 
     * @param title The title of the window.
     * @param msg The main message of the window.
     * @param style Icon, buttons, etc.
     * @return {@link MessageBox#open()}
     */
    public static int message(String title, String msg, int style)
    {
        org.eclipse.swt.widgets.MessageBox messageBox = new org.eclipse.swt.widgets.MessageBox(Display.getDefault().getActiveShell(), style);
        messageBox.setText(title);
        messageBox.setMessage(msg);
        return messageBox.open();
    }

    /**
     * Displays a user and password input prompt.
     * 
     * @return {@link Window#open()}
     */
    public static int credentials()
    {
        PasswordDialog dialog = new PasswordDialog(Display.getDefault().getActiveShell());
        return dialog.open();
    }

    /**
     * Displays a dialog with a drop down selection to choose a project from the workspace
     * and input fields for source folder location and package name.
     * 
     * @return {@link ConverterConfigurationDialog} instance
     */
    public static ConverterConfigurationDialog converterConfiguration()
    {
        return new ConverterConfigurationDialog(Display.getDefault().getActiveShell());
    }
}
