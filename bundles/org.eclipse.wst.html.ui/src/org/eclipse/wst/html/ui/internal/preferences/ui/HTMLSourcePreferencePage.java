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
package org.eclipse.wst.html.ui.internal.preferences.ui;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.xml.ui.internal.preferences.XMLSourcePreferencePage;

public class HTMLSourcePreferencePage extends XMLSourcePreferencePage {

	protected Button fTagNameAsIs = null;
	protected Button fTagNameUpper = null;
	protected Button fTagNameLower = null;
	protected Button fAttrNameAsIs = null;
	protected Button fAttrNameUpper = null;
	protected Button fAttrNameLower = null;

	protected void createContentsForGrammarConstraintsGroup(Composite parent) {
		// do nothing
	}

	protected void performDefaultsForGrammarConstraintsGroup() {
		// do nothing
	}

	protected void initializeValuesForGrammarConstraintsGroup() {
		// do nothing
	}

	protected void storeValuesForGrammarConstraintsGroup() {
		// do nothing
	}

	protected void createContentsForContentAssistGroup(Composite parent) {
		super.createContentsForContentAssistGroup(parent);

		// add one more group of preferences
		createContentsForPreferredCaseGroup(parent, 2);
	}

	protected Composite createContentsForPreferredCaseGroup(Composite parent, int columnSpan) {
		Group caseGroup = createGroup(parent, columnSpan);
		caseGroup.setText(HTMLUIMessages.Preferred_markup_case_UI_);

		// d257064 need to associate group w/ radio buttons so radio buttons header can be read
		Group tagNameGroup = createGroup(caseGroup, 1);
		tagNameGroup.setText(HTMLUIMessages.Tag_names__UI_);
		fTagNameUpper = createRadioButton(tagNameGroup, HTMLUIMessages.Tag_names_Upper_case_UI_);
		fTagNameLower = createRadioButton(tagNameGroup, HTMLUIMessages.Tag_names_Lower_case_UI_);

		// d257064 need to associate group w/ radio buttons so radio buttons header can be read
		Group attrNameGroup = createGroup(caseGroup, 1);
		attrNameGroup.setText(HTMLUIMessages.Attribute_names__UI_);
		fAttrNameUpper = createRadioButton(attrNameGroup, HTMLUIMessages.Attribute_names_Upper_case_UI_);
		fAttrNameLower = createRadioButton(attrNameGroup, HTMLUIMessages.Attribute_names_Lower_case_UI_);

		return parent;

	}

	protected void performDefaults() {
		fTagNameUpper.setSelection(getModelPreferences().getDefaultInt(HTMLCorePreferenceNames.TAG_NAME_CASE) == HTMLCorePreferenceNames.UPPER);
		fTagNameLower.setSelection(getModelPreferences().getDefaultInt(HTMLCorePreferenceNames.TAG_NAME_CASE) == HTMLCorePreferenceNames.LOWER);
		fAttrNameUpper.setSelection(getModelPreferences().getDefaultInt(HTMLCorePreferenceNames.ATTR_NAME_CASE) == HTMLCorePreferenceNames.UPPER);
		fAttrNameLower.setSelection(getModelPreferences().getDefaultInt(HTMLCorePreferenceNames.ATTR_NAME_CASE) == HTMLCorePreferenceNames.LOWER);

		super.performDefaults();
	}

	protected void initializeValues() {
		fTagNameUpper.setSelection(getModelPreferences().getInt(HTMLCorePreferenceNames.TAG_NAME_CASE) == HTMLCorePreferenceNames.UPPER);
		fTagNameLower.setSelection(getModelPreferences().getInt(HTMLCorePreferenceNames.TAG_NAME_CASE) == HTMLCorePreferenceNames.LOWER);
		fAttrNameUpper.setSelection(getModelPreferences().getInt(HTMLCorePreferenceNames.ATTR_NAME_CASE) == HTMLCorePreferenceNames.UPPER);
		fAttrNameLower.setSelection(getModelPreferences().getInt(HTMLCorePreferenceNames.ATTR_NAME_CASE) == HTMLCorePreferenceNames.LOWER);

		super.initializeValues();
	}

	protected void storeValues() {
		if (fTagNameUpper.getSelection())
			getModelPreferences().setValue(HTMLCorePreferenceNames.TAG_NAME_CASE, HTMLCorePreferenceNames.UPPER);
		else
			getModelPreferences().setValue(HTMLCorePreferenceNames.TAG_NAME_CASE, HTMLCorePreferenceNames.LOWER);
		if (fAttrNameUpper.getSelection())
			getModelPreferences().setValue(HTMLCorePreferenceNames.ATTR_NAME_CASE, HTMLCorePreferenceNames.UPPER);
		else
			getModelPreferences().setValue(HTMLCorePreferenceNames.ATTR_NAME_CASE, HTMLCorePreferenceNames.LOWER);

		super.storeValues();
	}

	public boolean performOk() {
		boolean result = super.performOk();

		doSavePreferenceStore();

		return result;
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.preferences.ui.xml.XMLSourcePreferencePage#getModelPreferences()
	 */
	protected Preferences getModelPreferences() {
		return HTMLCorePlugin.getDefault().getPluginPreferences();
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return HTMLUIPlugin.getDefault().getPreferenceStore();
	}
	
	protected void doSavePreferenceStore() {
		HTMLUIPlugin.getDefault().savePluginPreferences(); // UI
		HTMLCorePlugin.getDefault().savePluginPreferences(); // model
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		WorkbenchHelp.setHelp(c, IHelpContextIds.HTML_PREFWEBX_SOURCE_HELPID);
		return c;
	}
}