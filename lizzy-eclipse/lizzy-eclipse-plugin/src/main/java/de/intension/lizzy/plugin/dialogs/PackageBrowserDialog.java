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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * SWT Dialog element to select a package from the selected project.
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class PackageBrowserDialog extends Dialog
{

    private Tree             packages;
    private Label            error;

    private IProject         project;
    private String           path;

    private String           selectedPackage;

    private static final int IMAGE_MARGIN = 2;

    public PackageBrowserDialog(Shell parentShell)
    {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite container = (Composite)super.createDialogArea(parent);
        GridLayout layout = new GridLayout(1, true);
        layout.marginRight = 5;
        layout.marginLeft = 10;
        container.setLayout(layout);
        packages = new Tree(container, SWT.VIRTUAL | SWT.BORDER);
        packages.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        packages.addListener(SWT.MeasureItem, measureItemListener());
        packages.addListener(SWT.PaintItem, paintItemListener());
        packages.addListener(SWT.Selection, listener -> error.setText(""));
        error = new Label(container, SWT.NONE);
        error.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        error.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
        fillPackageSelection();
        return container;
    }

    @Override
    protected void okPressed()
    {
        TreeItem[] items = packages.getSelection();
        if (items.length == 1) {
            TreeItem item = items[0];
            String text = item.getText();
            if (item.getData() instanceof Image && text != null) {
                selectedPackage = text;
                super.okPressed();
            }
        }
        error.setText("Please select a package!");
    }

    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /**
     * @return {@link SWT#PaintItem} listener for the tree so that the default foreground can be
     *         augmented.
     */
    private Listener paintItemListener()
    {
        return event -> {
            TreeItem item = (TreeItem)event.item;
            Image trailingImage = (Image)item.getData();
            if (trailingImage != null) {
                int itemHeight = packages.getItemHeight();
                int imageHeight = trailingImage.getBounds().height;
                int y = event.y + (itemHeight - imageHeight) / 2;
                event.gc.drawImage(trailingImage, 1, y);
            }
        };
    }

    /**
     * @return {@link SWT#MeasureItem} listener for the tree so that item sizes can be specified.
     */
    private Listener measureItemListener()
    {
        return event -> {
            TreeItem item = (TreeItem)event.item;
            Image trailingImage = (Image)item.getData();
            if (trailingImage != null) {
                event.width += trailingImage.getBounds().width + IMAGE_MARGIN;
            }
        };
    }

    private void fillPackageSelection()
    {
        if (project == null || path == null) {
            return;
        }
        try {
            IJavaProject javaProject = JavaCore.create(project);
            IPackageFragmentRoot testDir = getPackageRoot(javaProject.getPackageFragmentRoots(), path);
            if (testDir == null) {
                return;
            }
            IPackageFragment[] packs = getPackages(testDir.getChildren());
            for (IPackageFragment pack : packs) {
                String packageName = pack.getElementName();
                if (packageName != null) {
                    TreeItem treePackage = new TreeItem(packages, 0);
                    treePackage.setText(packageName);
                    treePackage.setData(getPackageIcon(!pack.containsJavaResources()));
                }
            }
        } catch (JavaModelException jme) {
            Dialogs.error(jme);
        }
    }

    /**
     * Get the icon for the package tree.
     * 
     * @param isEmpty Defines whether the package is empty or not.
     * @return Different package icon for empty/non empty package.
     */
    private Image getPackageIcon(boolean isEmpty)
    {
        String file = "/src/site/resources/images/";
        if (isEmpty) {
            file += "package_empty.png";
        }
        else {
            file += "package.png";
        }
        Image image = new Image(Display.getDefault(), getClass().getResourceAsStream(file));
        GC gc = new GC(image);
        gc.drawImage(image, 16, 16);
        gc.dispose();
        return image;
    }

    /**
     * Filters through an array of java elements (folders, packages, etc)
     * and only returns packages, that contain classes.
     * 
     * @param elements Folders, packages, etc of the java project
     * @return array of java packages
     */
    private IPackageFragment[] getPackages(IJavaElement[] elements)
        throws JavaModelException
    {
        List<IPackageFragment> packageList = new ArrayList<>();
        for (IJavaElement elem : elements) {
            if (elem instanceof IPackageFragment && !((IPackageFragment)elem).hasSubpackages()) {
                packageList.add((IPackageFragment)elem);
            }
        }
        return packageList.toArray(new IPackageFragment[0]);
    }

    private IPackageFragmentRoot getPackageRoot(IPackageFragmentRoot[] roots, String path)
    {
        for (IPackageFragmentRoot root : roots) {
            if (root.getPath().toString().contains(path)) {
                return root;
            }
        }
        return null;
    }

    public PackageBrowserDialog setProject(IProject project)
    {
        this.project = project;
        return this;
    }

    public PackageBrowserDialog setPath(String path)
    {
        this.path = path;
        return this;
    }

    public String getSelectedPackage()
    {
        return selectedPackage;
    }
}
