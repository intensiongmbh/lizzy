package de.intension.lizzy.plugin.dialogs;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

/**
 * Collection of static methods to provide easy access to SWT Dialog elements.
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
}
