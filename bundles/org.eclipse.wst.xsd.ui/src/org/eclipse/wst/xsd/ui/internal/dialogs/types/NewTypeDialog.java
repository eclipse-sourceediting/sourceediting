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
package org.eclipse.wst.xsd.ui.internal.dialogs.types;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;

/*
 * This class extends NewComponentDialog to allow additional widgets.  More
 * specifically, the 'create complex type or simple type' checkbox
 */
public class NewTypeDialog extends NewComponentDialog implements SelectionListener {
	protected Button complexRadio;
	protected Button simpleRadio;
	private boolean createComplexTypeBoolean;
	
	private List complexNames;
	private List simpleNames;
	
	public NewTypeDialog(Shell parentShell, String defaultName) {
		super(parentShell, "New Type", defaultName);
	}
	
	/*
	 * The argument usedNames is the used names for the Complex Types.
	 * See method setUsedComplexTypeNames(List) for more information.
	 */
	public NewTypeDialog(Shell parentShell, String defaultName, List usedNames) {
		super(parentShell, "New Type", defaultName, usedNames);
		setUsedComplexTypeNames(usedNames);
	}
	
	protected void createExtendedContent(Composite parent) {
		Composite child = new Composite (parent, SWT.NONE);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 1;
	    layout.marginWidth = 0;
	    layout.marginHeight = 0;
	    child.setLayout(layout);
		
	    complexRadio = new Button(child, SWT.RADIO);
	    complexRadio.addSelectionListener(this);
	    complexRadio.setSelection(true);
	    complexRadio.setText(XSDEditorPlugin.getXSDString("_UI_PAGE_HEADING_COMPLEXTYPE"));
	    
	    simpleRadio = new Button(child, SWT.RADIO);
	    simpleRadio.addSelectionListener(this);
	    simpleRadio.setText(XSDEditorPlugin.getXSDString("_UI_PAGE_HEADING_SIMPLETYPE"));	    
	}

	public boolean isComplexType() {
		return createComplexTypeBoolean;
	}
	
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			createComplexTypeBoolean = complexRadio.getSelection();
		}
		super.buttonPressed(buttonId);
	}

	/*
	 * Used to set the used names for complex types.  If the user enters a name (for a complex
	 * type) which is in this list, a warning message will appear.
	 * 
	 * Note:
	 * The used names list supplied in the constructor is meant for the complex names list.
	 * So, this method does not need to be called.  If this method is called, the list
	 * supplied by this method will be used instead of the list supplied from the constructor.
	 */
	public void setUsedComplexTypeNames(List complexNames) {
		this.complexNames = complexNames;
	}
	
	/*
	 * Used to set the used names for simple types.  If the user enters a name (for a simple
	 * type) which is in this list, a warning message will appear.
	 */
	public void setUsedSimpleTypeNames(List simpleNames) {
		this.simpleNames = simpleNames;
	}

	/*
	 * SelectionListener methods
	 */
	public void widgetSelected(SelectionEvent event) {
		// We need to update the usedNames list
		if (complexRadio.getSelection()) {
			if (simpleRadio.getSelection()) {
				simpleRadio.setSelection(false);
			}

			usedNames = complexNames;
			updateErrorMessage();
		}
		else if (simpleRadio.getSelection()){
			if (complexRadio.getSelection()) {
				complexRadio.setSelection(false);
			}

			usedNames = simpleNames;
			updateErrorMessage();
		}
	}
	
	public void widgetDefaultSelected(SelectionEvent event) {		
	}
}
