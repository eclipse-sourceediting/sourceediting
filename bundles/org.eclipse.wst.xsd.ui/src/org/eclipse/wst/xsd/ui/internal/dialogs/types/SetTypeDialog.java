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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

/*
 * Abstract class used to set the Type or Element.
 */
public abstract class SetTypeDialog extends Dialog {
	protected Object input;
	protected String kind;
	private String dialogTitle;

	// widgets
	protected Text textFilter;
	private Button createButton;
	private Button removeButton;
	private Button importButton;
	protected Button viewTypeCheckBox;
	
	protected SetTypeTreeView treeView;					  // A Tree Viewer helper class
	protected TypesDialogTreeObject treeRootViewerInput;  // Root Input object for our Tree

	public SetTypeDialog(Shell shell, Object input, String dialogTitle, String kind) {
		super(shell);
		this.input = input;
		this.kind = kind;
		this.dialogTitle = dialogTitle;
	}

	public Control createDialogArea(Composite parent) {
		getShell().setText(dialogTitle);

		Composite base = (Composite) super.createDialogArea(parent);
		GridData gData = (GridData) base.getLayoutData();
		gData.heightHint = 500;
		gData.widthHint = 400;

		// Create Text textFilter
		Label filterLabel = new Label(base, SWT.NONE);
		filterLabel.setText("(? = any character, * = any string)");
		textFilter = new Text(base, SWT.SINGLE | SWT.BORDER);
		textFilter.addModifyListener(new SetTypeModifyAdapter());
		GridData textFilterData = new GridData();
		textFilterData.horizontalAlignment = GridData.FILL;
		textFilterData.grabExcessHorizontalSpace = true;
		textFilter.setLayoutData(textFilterData);

		// Create and expand entire Tree when we first open the dialog
		createTree(base);
		treeView.expandAll();
		
		SetTypeSelectionAdapter selectionAdapter = new SetTypeSelectionAdapter();

		// Create viewTypeCheckBox
		viewTypeCheckBox = createCheckBox(base, "Flat View");
		viewTypeCheckBox.setSelection(false);
		viewTypeCheckBox.addSelectionListener(selectionAdapter);

		// Create Composite 2
		Composite comp2 = new Composite(base, SWT.NONE);
		GridLayout layout2 = new GridLayout();
		layout2.numColumns = 2;
		comp2.setLayout(layout2);

		GridData gdk = new GridData();
		gdk.grabExcessHorizontalSpace = true;
		gdk.horizontalAlignment = SWT.CENTER;
		comp2.setLayoutData(gdk);

		// Create Add and Remove Buttons
		createButton = createPushButton(comp2, "Create New");
		//	     removeButton = createPushButton(comp2, "Remove");
		createButton.addSelectionListener(selectionAdapter);

		// Create Composite 3
		Composite comp3 = new Composite(base, SWT.NONE);
		GridLayout layout3 = new GridLayout();
		layout3.numColumns = 2;
		comp3.setLayout(layout3);

		// Create Browse Button
		importButton = createPushButton(comp2, "import...");
		importButton.addSelectionListener(selectionAdapter);

		// Build the separator line
		Label titleBarSeparator = new Label(base, SWT.HORIZONTAL
				| SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return base;
	}

	/**
	 * Helper method for creating buttons.
	 */
	public static Button createPushButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		button.setLayoutData(data);

		return button;
	}

	/**
	 * Helper method for creating check box
	 */
	public static Button createCheckBox(Composite parent, String label) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(label);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		button.setLayoutData(data);
		return button;
	}

	private void createTree(Composite base) {
		String title;
		if (kind.equalsIgnoreCase("type")) {
			title = "Avaliable Types";
		} else {
			title = "Avaliable Elements";
		}

		treeView = new SetTypeTreeView(base, title);
		treeView.addTreeSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateCanFinish(event.getSelection());
			}
		});

		populateTreeViewer(false, "", false, false);
	}

	protected void populateTreeViewer(boolean isFlatView, String filterString,
			boolean saveExpansionState, boolean restoreExpansionState) {
		if (saveExpansionState) {
			treeView.storeExpansionState();
		}

		treeRootViewerInput = new TypesDialogTreeObject("root");
		if (isFlatView) {
			// Flat View
			createFlatView(treeRootViewerInput, filterString);
		}
		else {
			// Tree View
			createTreeView(treeRootViewerInput, filterString);
		}
		treeView.setInput(treeRootViewerInput);
		
		if (restoreExpansionState) {
			treeView.restoreExpansionState();
		}
	}

	private String insertString(String target, String newString, String string) {
		ArrayList list = new ArrayList();
		StringBuffer stringBuffer = new StringBuffer(string);

		int index = stringBuffer.indexOf(target);
		while (index != -1) {
			stringBuffer = stringBuffer.insert(index, newString);
			index = stringBuffer.indexOf(target, index + newString.length()
					+ target.length());
		}

		return stringBuffer.toString();
	}

	/*
	 * If supported metacharacters are used in the filter string, we need to insert
	 * a "." before each metacharacter.
	 */
	private String processFilterString(String inputString) {
		if (!(inputString.equals(""))) {
			inputString = insertString("*", ".", inputString);
			inputString = insertString("?", ".", inputString);
			inputString = inputString + ".*";
		} else {
			inputString = ".*";
		}

		return inputString.toLowerCase();
	}

	private class SetTypeModifyAdapter implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			if (e.widget == textFilter) {
				if (delayedEvent != null) {
					delayedEvent.CANCEL = true;
				}
				
				delayedEvent = new DelayedEvent();
				Display.getCurrent().timerExec(400, delayedEvent);				
			}
		}
	}
	
	private DelayedEvent delayedEvent;
	
	private class DelayedEvent implements Runnable {
		public boolean CANCEL = false;
		
		public void run() {
			if (!CANCEL) {
				populateTreeViewer(viewTypeCheckBox.getSelection(),
						processFilterString(textFilter.getText()),
						false, false);
	
				// Expand all and select first item which matches
				treeView.expandAll();
				treeView.selectFirstItem();
			}
		}
	}	

	private class SetTypeSelectionAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			if (e.widget == viewTypeCheckBox) {
				// Toggle between Flat view and Tree view
				if (viewTypeCheckBox.getSelection()) {
					populateTreeViewer(viewTypeCheckBox.getSelection(),
							processFilterString(textFilter.getText()), true,
							false);
				}
				else {
					populateTreeViewer(viewTypeCheckBox.getSelection(),
							processFilterString(textFilter.getText()), false,
							true);
				}
			}
			else if (e.widget == createButton) {
				createButtonPressed();
			}
			else if (e.widget == importButton) {
				importButtonPressed();
			}
		}
	}
	
    protected void createButtonsForButtonBar(Composite parent) {
    	super.createButtonsForButtonBar(parent);
    	getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
    
    
    protected List getUsedComplexTypeNames(XSDSchema schema) {
      	List usedNames = new ArrayList();
      	Iterator names = schema.getTypeDefinitions().iterator();
      	while (names.hasNext()) {
      		XSDNamedComponent comp = (XSDNamedComponent) names.next();
      		if (comp instanceof XSDComplexTypeDefinition) {
      			usedNames.add(comp.getName());
      		}
      	}
      	
      	return usedNames;
      }

      protected List getUsedSimpleTypeNames(XSDSchema schema) {
      	List usedNames = new ArrayList();
      	Iterator names = schema.getTypeDefinitions().iterator();
      	while (names.hasNext()) {
      		XSDNamedComponent comp = (XSDNamedComponent) names.next();
      		if (comp instanceof XSDSimpleTypeDefinition) {
      			usedNames.add(comp.getName());
      		}
      	}
      	
      	return usedNames;
      }

      protected List getUsedElementNames(XSDSchema schema) {
      	List usedNames = new ArrayList();
      	Iterator names = schema.getElementDeclarations().iterator();
      	while (names.hasNext()) {
      		XSDNamedComponent comp = (XSDNamedComponent) names.next();
      		usedNames.add(comp.getName());
      	}
      	
      	return usedNames;
      }

	/*
	 * This method should be subclassed.
	 */
	protected abstract void createButtonPressed();

	/*
	 * This method should be subclassed
	 */
	protected abstract void importButtonPressed();

	/*
	 * This method should be subclassed
	 */
	protected abstract void createTreeView(TypesDialogTreeObject root, String filterString);

	/*
	 * This methods should be subclassed
	 */
	protected abstract void createFlatView(TypesDialogTreeObject root, String filterString);

	/*
	 * This method should be subclassed
	 */
	protected abstract void updateCanFinish(Object object);

	/*
	 * This method should be subclassed
	 */
	protected void okPressed() {
		super.okPressed();
	}
}
