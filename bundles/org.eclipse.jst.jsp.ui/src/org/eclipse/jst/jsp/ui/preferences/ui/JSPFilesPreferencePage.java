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
package org.eclipse.jst.jsp.ui.preferences.ui;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jst.jsp.core.JSPCorePlugin;
import org.eclipse.jst.jsp.ui.JSPEditorPlugin;
import org.eclipse.jst.jsp.ui.internal.editor.IHelpContextIds;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage;

public class JSPFilesPreferencePage extends HTMLFilesPreferencePage {

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.preferences.ui.AbstractPreferencePage#getModelPreferences()
	 */
	protected Preferences getModelPreferences() {
		return JSPCorePlugin.getDefault().getPluginPreferences();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore() {
		return JSPEditorPlugin.getDefault().getPreferenceStore();
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.preferences.ui.XMLFilesPreferencePage#doSavePreferenceStore()
	 */
	protected void doSavePreferenceStore() {
		JSPCorePlugin.getDefault().savePluginPreferences(); // model
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		WorkbenchHelp.setHelp(c, IHelpContextIds.JSP_PREFWEBX_FILES_HELPID);
		return c;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#createContentsForLoadingGroup(org.eclipse.swt.widgets.Composite)
	 */
	protected void createContentsForLoadingGroup(Composite parent) {
		// no loading preferences
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#initializeValuesForLoadingGroup()
	 */
	protected void initializeValuesForLoadingGroup() {
		// no loading preferences
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#performDefaultsForLoadingGroup()
	 */
	protected void performDefaultsForLoadingGroup() {
		// no loading preferences
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#storeValuesForLoadingGroup()
	 */
	protected void storeValuesForLoadingGroup() {
		// no loading preferences
	}
}