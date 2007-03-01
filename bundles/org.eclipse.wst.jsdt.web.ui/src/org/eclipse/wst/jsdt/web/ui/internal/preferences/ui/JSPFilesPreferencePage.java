/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.preferences.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.jsdt.web.core.internal.JSPCorePlugin;
import org.eclipse.wst.jsdt.web.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.wst.jsdt.web.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.jsdt.web.ui.internal.JSPUIMessages;
import org.eclipse.wst.jsdt.web.ui.internal.JSPUIPlugin;
import org.eclipse.wst.jsdt.web.ui.internal.editor.IHelpContextIds;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.html.ui.internal.preferences.ui.HTMLFilesPreferencePage;

public class JSPFilesPreferencePage extends HTMLFilesPreferencePage {
	private Button fValidateFragments;

	@Override
	protected Preferences getModelPreferences() {
		return JSPCorePlugin.getDefault().getPluginPreferences();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void doSavePreferenceStore() {
		JSPCorePlugin.getDefault().savePluginPreferences(); // model
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);

		Group g = createGroup((Composite) c, 1);
		g.setText(JSPUIMessages.JSPFilesPreferencePage_0);
		fValidateFragments = createCheckBox(g,
				JSPUIMessages.JSPFilesPreferencePage_1);
		boolean validateFragments = getModelPreferences().getBoolean(
				JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
		fValidateFragments.setSelection(validateFragments);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(c,
				IHelpContextIds.JSP_PREFWEBX_FILES_HELPID);

		setSize((Composite) c);
		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#createContentsForLoadingGroup(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContentsForLoadingGroup(Composite parent) {
		// no loading preferences
	}

	@Override
	protected IContentType getContentType() {
		return Platform.getContentTypeManager().getContentType(
				ContentTypeIdForJSP.ContentTypeID_JSP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#initializeValuesForLoadingGroup()
	 */
	@Override
	protected void initializeValuesForLoadingGroup() {
		// no loading preferences
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		boolean validateFragments = getModelPreferences().getDefaultBoolean(
				JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
		fValidateFragments.setSelection(validateFragments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#performDefaultsForLoadingGroup()
	 */
	@Override
	protected void performDefaultsForLoadingGroup() {
		// no loading preferences
	}

	@Override
	protected void storeValues() {
		boolean validateFragments = fValidateFragments.getSelection();
		getModelPreferences().setValue(
				JSPCorePreferenceNames.VALIDATE_FRAGMENTS, validateFragments);
		super.storeValues();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.html.ui.preferences.ui.HTMLFilesPreferencePage#storeValuesForLoadingGroup()
	 */
	@Override
	protected void storeValuesForLoadingGroup() {
		// no loading preferences
	}
}
