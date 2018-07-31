package de.intension.lizzy.plugin.parts;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class SearchView
{

    public static final String ID = "lizzy-eclipse-plugin.partdescriptor.searchticket";

    private Text               searchInput;
    private List               issues;

    @PostConstruct
    public void createPartControl(Composite parent)
    {
        parent.setLayout(new GridLayout(1, true));

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        searchInput = new Text(composite, SWT.BORDER);
        searchInput.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        Button searchButton = new Button(composite, SWT.BORDER);
        searchButton.setText("Search");

        issues = new List(parent, SWT.BORDER);
        issues.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    }

    @Focus
    public void setFocus()
    {
        searchInput.setFocus();
    }
}
