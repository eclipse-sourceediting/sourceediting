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
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.editor.IHelpContextIds;
import org.eclipse.jst.jsp.ui.internal.preferences.JSPUIPreferenceNames;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xml.ui.internal.preferences.XMLFilesPreferencePage;

public class JSPFilesPreferencePage extends XMLFilesPreferencePage {
	private Button fJSPSearchToJavaSearchButton;
	
	/**
	 * <p><b>NOTE: </b>originally copied from {@link XMLFilesPreferencePage#createControl(Composite)}</p>
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite scrolledComposite = createScrolledComposite(parent);
		createContentsForCreatingGroup(scrolledComposite);
		createContentsForSearchGroup(scrolledComposite);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(scrolledComposite, IHelpContextIds.JSP_PREFWEBX_FILES_HELPID);

		setSize(scrolledComposite);
		loadPreferences();

		return scrolledComposite;
	}
	
	/**
	 * @param parent
	 */
	private void createContentsForSearchGroup(Composite parent) {
		Group group = createGroup(parent, 1);
		group.setText(JSPUIMessages.JSPFilesPreferencePage_Search_group);
		fJSPSearchToJavaSearchButton = createCheckBox(group, JSPUIMessages.JSPFilesPreferencePage_Supply_JSP_search_to_Java_search);
	}

	/**
	 * @see org.eclipse.wst.xml.ui.internal.preferences.XMLFilesPreferencePage#initializeValues()
	 */
	protected void initializeValues() {
		super.initializeValues();
		initCheckbox(fJSPSearchToJavaSearchButton, JSPUIPreferenceNames.SUPPLY_JSP_SEARCH_RESULTS_TO_JAVA_SEARCH);
	}

	/**
	 * @see org.eclipse.wst.xml.ui.internal.preferences.XMLFilesPreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		super.performDefaults();
		defaultCheckbox(fJSPSearchToJavaSearchButton, JSPUIPreferenceNames.SUPPLY_JSP_SEARCH_RESULTS_TO_JAVA_SEARCH);
	}

	/**
	 * @see org.eclipse.wst.xml.ui.internal.preferences.XMLFilesPreferencePage#storeValues()
	 */
	protected void storeValues() {
		super.storeValues();
		getPreferenceStore().setValue(JSPUIPreferenceNames.SUPPLY_JSP_SEARCH_RESULTS_TO_JAVA_SEARCH,
				(fJSPSearchToJavaSearchButton != null) ? fJSPSearchToJavaSearchButton.getSelection() : false);
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @see org.eclipse.wst.xml.ui.internal.preferences.XMLFilesPreferencePage#getContentType()
	 */
	protected IContentType getContentType() {
		return Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP);
	}

	/**
	 * @see org.eclipse.wst.xml.ui.internal.preferences.XMLFilesPreferencePage#doSavePreferenceStore()
	 */
	protected void doSavePreferenceStore() {
		JSPCorePlugin.getDefault().savePluginPreferences(); // model
	}

	/**
	 * @see org.eclipse.wst.xml.ui.internal.preferences.XMLFilesPreferencePage#getModelPreferences()
	 */
	protected Preferences getModelPreferences() {
		return JSPCorePlugin.getDefault().getPluginPreferences();
	}
}
