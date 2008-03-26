/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.preferences.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.html.ui.internal.preferences.ui.HTMLFilesPreferencePage;

public class JSPFilesPreferencePage extends HTMLFilesPreferencePage {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#createContentsForLoadingGroup(org.eclipse.swt.widgets.Composite)
	 */
	protected void createContentsForLoadingGroup(Composite parent) {
		// no loading preferences
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}

	protected void doSavePreferenceStore() {
		JSPCorePlugin.getDefault().savePluginPreferences(); // model
	}

	protected IContentType getContentType() {
		return Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP);
	}

	protected Preferences getModelPreferences() {
		return JSPCorePlugin.getDefault().getPluginPreferences();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#initializeValuesForLoadingGroup()
	 */
	protected void initializeValuesForLoadingGroup() {
		// no loading preferences
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#performDefaultsForLoadingGroup()
	 */
	protected void performDefaultsForLoadingGroup() {
		// no loading preferences
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#storeValuesForLoadingGroup()
	 */
	protected void storeValuesForLoadingGroup() {
		// no loading preferences
	}
}
