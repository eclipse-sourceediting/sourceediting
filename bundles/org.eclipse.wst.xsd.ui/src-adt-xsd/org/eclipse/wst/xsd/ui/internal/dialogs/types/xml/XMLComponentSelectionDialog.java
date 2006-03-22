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
package org.eclipse.wst.xsd.ui.internal.dialogs.types.xml;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.ComponentSelectionDialog;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.IComponentSelectionProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSelectionProvider.XMLComponentTreeObject;

public class XMLComponentSelectionDialog extends ComponentSelectionDialog {

    protected final static String DEFAULT_NAME_FIELD_TITLE = XSDEditorPlugin.getXSDString("_UI_LABEL_COMPONENT_NAME");
    protected final static String DEFAULT_LIST_TITLE = XSDEditorPlugin.getXSDString("_UI_LABEL_MATCHING_COMPONENTS");
	
	public static final String SCOPE_SPECIFIED_FILE = XSDEditorPlugin.getXSDString("_UI_LABEL_SPECIFIED_FILE");

	public static final String SCOPE_ENCLOSING_PROJECT = XSDEditorPlugin.getXSDString("_UI_LABEL_ENCLOSING_PROJECT");

	public static final String SCOPE_WORKSPACE = XSDEditorPlugin.getXSDString("_UI_LABEL_WORKSPACE");

	public static final String SCOPE_CURRENT_RESOURCE = XSDEditorPlugin.getXSDString("_UI_LABEL_CURRENT_RESOURCE");

	private String currentSearchScope = SCOPE_CURRENT_RESOURCE;

	protected Button chooseButton;
	protected Button[] radioButton = new Button[3];

	public XMLComponentSelectionDialog(Shell shell, String dialogTitle,
			IComponentSelectionProvider provider) {
		super(shell, dialogTitle, provider);
	}

public Control createDialogArea(Composite parent) {

        setFilterLabel(provider.getNameFieldTitle() != null ? provider.getNameFieldTitle() : DEFAULT_NAME_FIELD_TITLE);         
        setComponentTreeLabel(provider.getListTitle() != null ? provider.getListTitle() : DEFAULT_LIST_TITLE);
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
        group.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_SEARCH_SCOPE"));
        
        ScopeChangeListener scopeChangeListener = new ScopeChangeListener();
        radioButton[0] = new Button(group, SWT.RADIO);       
        radioButton[0].setText(SCOPE_WORKSPACE);
        
        radioButton[1] = new Button(group, SWT.RADIO);
        radioButton[1].setText(SCOPE_ENCLOSING_PROJECT);
        
        radioButton[2] = new Button(group, SWT.RADIO);
        radioButton[2].setText(SCOPE_CURRENT_RESOURCE);
        
        //radioButton[3] = new Button(group, SWT.RADIO);
        //radioButton[3].setText("Resource Set");

        
        for (int i = 0; i < radioButton.length; i++)
        {          	
          if (radioButton[i].getText().equals(currentSearchScope))
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
        check.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_NARROW_SEARCH_SCOPE_RESOURCE"));
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        check.setLayoutData(gd);
        check.setEnabled(false);

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
			}
		}
	}
}
