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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;


public class TranslucencyPreferenceTab implements IPreferenceTab {
	/**
	 * 
	 */
	public TranslucencyPreferenceTab() {
		super();
	}

	
	/* (non-Javadoc)
	 */
	public Control createContents(Composite tabFolder) {
		Label label = new Label(tabFolder, SWT.NONE);
		WorkbenchHelp.setHelp(label, IHelpContextIds.PREFWEBX_READONLY_HELPID);
		return label;
	}

	/* (non-Javadoc)
	 */
	public String getTitle() {
		return ResourceHandler.getString("TranslucencyPreferenceTab.0"); //$NON-NLS-1$
	}
	/* (non-Javadoc)
	 */
	public void performApply() {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 */
	public void performDefaults() {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 */
	public void performOk() {
		// TODO Auto-generated method stub

	}
}
