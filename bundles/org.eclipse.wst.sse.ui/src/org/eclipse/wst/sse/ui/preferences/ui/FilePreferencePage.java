/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.preferences.ui;



import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.sse.ui.internal.preferences.TabFolderLayout;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;


public class FilePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private IPreferenceTab[] fTabs = null;

	public void init(IWorkbench desktop) {
	}

	protected Control createContents(Composite parent) {
		Composite composite = createComposite(parent, 1);

		String description = ResourceHandler.getString("FilePreferencePage.0"); //$NON-NLS-1$
		createLabel(composite, description);
		createLabel(composite, ""); //$NON-NLS-1$

		TabFolder folder = new TabFolder(composite, SWT.NONE);
		folder.setLayout(new TabFolderLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem taskItem = new TabItem(folder, SWT.NONE);
		IPreferenceTab tasksTab = new TaskTagPreferenceTab();
		taskItem.setText(tasksTab.getTitle());
		Control taskTags = tasksTab.createContents(folder);
		taskItem.setControl(taskTags);

		TabItem translucenceItem = new TabItem(folder, SWT.NONE);
		IPreferenceTab translucenceTab = new TranslucencyPreferenceTab();
		translucenceItem.setText(translucenceTab.getTitle());
		Control translucenceControl = translucenceTab.createContents(folder);
		translucenceItem.setControl(translucenceControl);

		fTabs = new IPreferenceTab[]{tasksTab, translucenceTab};

		return composite;
	}

	protected Composite createComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);

		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		composite.setLayout(layout);

		//GridData
		GridData data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL_VERTICAL;
		data.horizontalAlignment = GridData.FILL_HORIZONTAL;
		composite.setLayoutData(data);

		return composite;
	}

	protected Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);

		//GridData
		GridData data = new GridData(GridData.FILL);
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_FILL;
		label.setLayoutData(data);

		return label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	protected void performApply() {
		super.performApply();
		for (int i = 0; i < fTabs.length; i++) {
			fTabs[i].performApply();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		super.performDefaults();
		for (int i = 0; i < fTabs.length; i++) {
			fTabs[i].performDefaults();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		boolean ok = super.performOk();
		for (int i = 0; i < fTabs.length; i++) {
			fTabs[i].performOk();
		}
		return ok;
	}
}
