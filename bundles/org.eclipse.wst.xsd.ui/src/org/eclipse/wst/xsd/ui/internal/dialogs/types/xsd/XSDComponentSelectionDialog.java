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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.common.ui.dialogs.SelectSingleFileDialog;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.ComponentSelectionDialog;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.IComponentSelectionProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSelectionProvider.XMLComponentTreeObject;

public class XSDComponentSelectionDialog extends ComponentSelectionDialog {
	private CCombo scopeCombo;

	public static final String SCOPE_SPECIFIED_FILE = "Specified File";

	public static final String SCOPE_ENCLOSING_PROJECT = "Enclosing Project";

	public static final String SCOPE_WORKSPACE = "Workspace";

	public static final String SCOPE_CURRENT_SCHEMA = "Current Schema";

	private String currentSearchScope = SCOPE_CURRENT_SCHEMA;

	protected Button chooseButton;
	protected Button[] radioButton = new Button[3];

	public XSDComponentSelectionDialog(Shell shell, String dialogTitle,
			IComponentSelectionProvider provider) {
		super(shell, dialogTitle, provider);
	}

public Control createDialogArea(Composite parent) {
    /*
	 * setFilterLabel("Type name:"); // TODO: Externalize String
	 * setComponentTreeLabel("Matching types:"); // TODO: Externalize String
	 * super.createDialogArea(parent);
	 *  // We use the Composite topComposite to create additional widgets
	 * GridLayout layout = new GridLayout(); layout.marginWidth = 0;
	 * topComposite.setLayout(layout);
	 * 
	 * Label scopeComboLabel = new Label(topComposite, SWT.NONE);
	 * scopeComboLabel.setText("Search Scope:"); // TODO: Externalize String
	 * scopeCombo = new CCombo(topComposite, SWT.BORDER);
	 * scopeCombo.setBackground(ColorConstants.white);
	 * scopeCombo.addSelectionListener(new ScopeComboSelectionListener());
	 * 
	 * scopeCombo.add(explicitlyReferencedString);
	 * scopeCombo.add(enclosingProjectString);
	 * scopeCombo.add(entireWorkspaceString);
	 * scopeCombo.setText(enclosingProjectString);
	 * scopeCombo.setEditable(false); topComposite.setFocus();
	 */
        setFilterLabel("Type name:");          // TODO: Externalize String
        setComponentTreeLabel("Matching types:");    // TODO: Externalize String
        super.createDialogArea(parent);
        
        // We use the Composite topComposite to create additional widgets
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        bottomComposite.setLayout(layout);
        
        
        Group group = new Group(bottomComposite, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        //gridLayout.marginWidth = 0;
        //gridLayout.marginLeft = 2;
        group.setLayout(gridLayout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setText("Search Scope");
        
        ScopeChangeListener scopeChangeListener = new ScopeChangeListener();
        radioButton[0] = new Button(group, SWT.RADIO);       
        radioButton[0].setText("Workspace");
        
        radioButton[1] = new Button(group, SWT.RADIO);
        radioButton[1].setText("Enclosing Project");
        
        radioButton[2] = new Button(group, SWT.RADIO);
        radioButton[2].setText("Current Schema");
        
        //radioButton[3] = new Button(group, SWT.RADIO);
        //radioButton[3].setText("Resource Set");

        
        for (int i = 0; i < radioButton.length; i++)
        {          	
          if (radioButton[i].getText() == currentSearchScope)
          {
          	radioButton[i].setSelection(true);
          }	
          radioButton[i].addSelectionListener(scopeChangeListener);        	
        }
        if (false){
        Composite selectFileGroup = new Composite(group, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        selectFileGroup.setLayoutData(gd);
        GridLayout gridLayout3 = new GridLayout(2, false);       
        gridLayout3.marginWidth = 0;    
        gridLayout3.marginHeight = 0;
        selectFileGroup.setLayout(gridLayout3);
        Text t = new Text(selectFileGroup, SWT.BORDER | SWT.READ_ONLY);
        t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Button choose = new Button(selectFileGroup, SWT.NONE);
        choose.setText("Choose...");
        choose.addSelectionListener(scopeChangeListener);
        }
        
        
        Button check = new Button(group, SWT.CHECK);
        check.setText("Use resource view to narrow search scope");
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        check.setLayoutData(gd);

        return parent;
    }	/*
		  * Returns the selected search scope.
		  */
	public String getSearchScope() {
		return currentSearchScope;
	}

	public XMLComponentSpecification getSelection() {
		XMLComponentTreeObject treeObject = (XMLComponentTreeObject) componentSelection;
		List specs = treeObject.getXMLComponentSpecification();

		int matchingIndex = 0;
		for (int index = 0; index < specs.size(); index++) {
			XMLComponentSpecification spec = (XMLComponentSpecification) specs
					.get(index);
			IPath path = new Path(spec.getFileLocation());
			String specText = spec.getTargetNamespace() + " - "
					+ path.lastSegment();
			if (specText.equals(qualifierTextSelection)) {
				matchingIndex = index;
				break;
			}
		}

		return (XMLComponentSpecification) specs.get(matchingIndex);
	}

	private class ScopeChangeListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {

			if (e.widget instanceof Button) {
				Button b = (Button) e.widget;
				if ((b.getStyle() & SWT.RADIO) != 0) {
					currentSearchScope = b.getText();
					populateMasterComponentList();
					refreshTreeViewer(getProcessedFilterString());

					// Select the first matching component. Though we should be
					// smarter here
					// and determine if there was a selection before the scope
					// switch (and if
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
				else
				{

				  // this must be the choose button
					if (true)
					{		
					    String[] ext = {"xsd"};
				    SelectSingleFileDialog dialog = new SelectSingleFileDialog(getShell(), null, false);			
				    dialog.addFilterExtensions(ext);
			        dialog.setBlockOnOpen(false);	
			        dialog.create();
			        Rectangle r = dialog.getShell().getBounds();
			        r.x += 420;
			        r.y += 50;
			        dialog.getShell().setBounds(r);			 
			        dialog.open();}
					else 
					{
					  String[] ext = {"*.xsd"};
					  FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE | ~SWT.PRIMARY_MODAL);	
					  fileDialog.setFilterExtensions(ext);
					  fileDialog.open();
					}	
				}
			}
		}
	}
}
