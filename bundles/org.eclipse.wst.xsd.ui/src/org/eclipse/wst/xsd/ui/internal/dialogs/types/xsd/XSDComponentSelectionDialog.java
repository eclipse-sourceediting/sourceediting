/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.ComponentSelectionDialog;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.IComponentSelectionProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDComponentSelectionProvider.XMLComponentTreeObject;

public class XSDComponentSelectionDialog extends ComponentSelectionDialog {
    private CCombo scopeCombo;
    
    public static final String explicitlyReferencedString = "Explicitly Referenced Files";
    public static final String enclosingProjectString = "Enclosing Project";
    public static final String entireWorkspaceString = "Entire Workspace";
    
    private String searchScopeString = enclosingProjectString;
    public XSDComponentSelectionDialog(Shell shell, String dialogTitle, IComponentSelectionProvider provider) {
        super(shell, dialogTitle, provider);
    }
    
    public Control createDialogArea(Composite parent) {
        setFilterLabel("Filter by name ");          // TODO: Externalize String
        setComponentTreeLabel("Matching types:");    // TODO: Externalize String
        super.createDialogArea(parent);
        
        // We use the Composite topComposite to create additional widgets
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        topComposite.setLayout(layout);
        
        Label scopeComboLabel = new Label(topComposite, SWT.NONE);
        scopeComboLabel.setText("Search Scope:");       // TODO: Externalize String
        scopeCombo = new CCombo(topComposite, SWT.BORDER);
        scopeCombo.setBackground(ColorConstants.white);
        scopeCombo.addSelectionListener(new ScopeComboSelectionListener());
        
        scopeCombo.add(explicitlyReferencedString);
        scopeCombo.add(enclosingProjectString);
        scopeCombo.add(entireWorkspaceString);
        scopeCombo.setText(enclosingProjectString);
        scopeCombo.setEditable(false);
        topComposite.setFocus();
        
        return parent;
    }
    
    /*
     * Returns the selected search scope.
     */
    public String getSearchScope() {
        return searchScopeString;
    }
    
    public XMLComponentSpecification getSelection() {
        XMLComponentTreeObject treeObject = (XMLComponentTreeObject) componentSelection;
        List specs = treeObject.getXMLComponentSpecification();
        
        int matchingIndex = 0;
        for (int index = 0; index < specs.size(); index++) {
            XMLComponentSpecification spec = (XMLComponentSpecification) specs.get(index);
            IPath path = new Path(spec.getFileLocation());
            String specText = spec.getTargetNamespace() + " - " + path.lastSegment();
            if (specText.equals(qualifierTextSelection)) {
                matchingIndex = index;
                break;
            }
        }
        
        return (XMLComponentSpecification) specs.get(matchingIndex);
    }
    
    private class ScopeComboSelectionListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            if (e.widget == scopeCombo) {
                searchScopeString = scopeCombo.getItem(scopeCombo.getSelectionIndex());
                populateComponentTreeViewer(getProcessedFilterString());
                
                // Select the first matching component.  Though we should be smarter here
                // and determine if there was a selection before the scope switch (and if
                // the component is still available.
                Tree tree = componentTreeViewer.getTree();
                TreeItem items[] = tree.getItems();
                if (items.length > 0) {
                    TreeItem select[] = new TreeItem[1];
                    select[0] = items[0];
                    tree.setSelection(select);
                }
                
                updateCanFinish();
            }
        }
    }
}
