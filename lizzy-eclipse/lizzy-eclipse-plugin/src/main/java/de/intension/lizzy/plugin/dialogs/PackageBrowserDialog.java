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
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * SWT Dialog element to select a package from the selected project.
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class PackageBrowserDialog extends Dialog
{

    private Combo    packages;

    private IProject project;
    private String   path;

    private String   selectedPackage;

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
        packages = new Combo(container, SWT.READ_ONLY);
        packages.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        fillPackageSelection();
        return container;
    }

    @Override
    protected void okPressed()
    {
        selectedPackage = packages.getItem(packages.getSelectionIndex());
        super.okPressed();
    }

    private void fillPackageSelection()
    {
        if (project == null || path == null) {
            return;
        }
        try {
            IJavaProject javaProject = JavaCore.create(project);
            IPackageFragmentRoot testDir = null;
            testDir = getPackageRoot(javaProject.getPackageFragmentRoots(), path);
            if (testDir == null) {
                return;
            }
            IPackageFragment[] packs = getPackages(testDir.getChildren());
            for (IPackageFragment pack : packs) {
                String packageName = pack.getElementName();
                if (packageName != null && !packageName.isEmpty()) {
                    packages.add(packageName);
                }
            }
        } catch (JavaModelException jme) {
            Dialogs.error(jme);
        }
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
            if (elem instanceof IPackageFragment && hasClasses((IPackageFragment)elem)) {
                packageList.add((IPackageFragment)elem);
            }
        }
        return packageList.toArray(new IPackageFragment[0]);
    }

    /**
     * Looks for existing <code>.java</code> classes in the package.
     * 
     * @param pack The java package to search.
     * @return true when existing classes where found.
     */
    private boolean hasClasses(IPackageFragment pack)
        throws JavaModelException
    {
        for (IJavaElement elem : pack.getChildren()) {
            if (elem instanceof ICompilationUnit) {
                return true;
            }
        }
        return false;
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
