package de.intension.lizzy.plugin.parts;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class DisplayView
{

    public static final String ID = "lizzy-eclipse-plugin.partdescriptor.displayticket";

    @PostConstruct
    public void createPartControl(Composite parent)
    {
        parent.setLayout(new GridLayout(1, false));
        // title:
        Group group = new Group(parent, SWT.BORDER);
        group.setText("No ticket selected.");
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        // description:
        Group description = new Group(group, SWT.BORDER);
        description.setLayout(new GridLayout(2, false));
        description.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        description.setText("Description");
        Label descText = new Label(description, SWT.NONE);
        descText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        Button generateButton = new Button(description, SWT.BORDER);
        generateButton.setText("Generate");
        // attachments:
        List attachments = new List(group, SWT.BORDER);
        attachments.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    }
}
