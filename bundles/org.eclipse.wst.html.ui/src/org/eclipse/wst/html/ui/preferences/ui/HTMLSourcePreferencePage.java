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
package org.eclipse.wst.html.ui.preferences.ui;



import org.eclipse.core.runtime.Preferences;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.html.core.HTMLCorePlugin;
import org.eclipse.wst.html.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.xml.ui.preferences.XMLSourcePreferencePage;

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
		caseGroup.setText(ResourceHandler.getString("Preferred_markup_case_UI_")); //$NON-NLS-1$ = "Preferred markup case"

		// d257064 need to associate group w/ radio buttons so radio buttons header can be read
		Group tagNameGroup = createGroup(caseGroup, 1);
		tagNameGroup.setText(ResourceHandler.getString("Tag_names__UI_")); //$NON-NLS-1$ = "Tag names:"
		fTagNameUpper = createRadioButton(tagNameGroup, ResourceHandler.getString("Tag_names_Upper_case_UI_")); //$NON-NLS-1$ = "&Upper case"
		fTagNameLower = createRadioButton(tagNameGroup, ResourceHandler.getString("Tag_names_Lower_case_UI_")); //$NON-NLS-1$ = "&Lower case"

		// d257064 need to associate group w/ radio buttons so radio buttons header can be read
		Group attrNameGroup = createGroup(caseGroup, 1);
		attrNameGroup.setText(ResourceHandler.getString("Attribute_names__UI_")); //$NON-NLS-1$ = "Attribute names:"
		fAttrNameUpper = createRadioButton(attrNameGroup, ResourceHandler.getString("Attribute_names_Upper_case_UI_")); //$NON-NLS-1$ = "U&pper case"
		fAttrNameLower = createRadioButton(attrNameGroup, ResourceHandler.getString("Attribute_names_Lower_case_UI_")); //$NON-NLS-1$ = "L&ower case"

		return parent;

	}

	protected void performDefaults() {
		fTagNameUpper.setSelection(getModelPreferences().getDefaultInt(CommonModelPreferenceNames.TAG_NAME_CASE) == CommonModelPreferenceNames.UPPER);
		fTagNameLower.setSelection(getModelPreferences().getDefaultInt(CommonModelPreferenceNames.TAG_NAME_CASE) == CommonModelPreferenceNames.LOWER);
		fAttrNameUpper.setSelection(getModelPreferences().getDefaultInt(CommonModelPreferenceNames.ATTR_NAME_CASE) == CommonModelPreferenceNames.UPPER);
		fAttrNameLower.setSelection(getModelPreferences().getDefaultInt(CommonModelPreferenceNames.ATTR_NAME_CASE) == CommonModelPreferenceNames.LOWER);

		super.performDefaults();
	}

	protected void initializeValues() {
		fTagNameUpper.setSelection(getModelPreferences().getInt(CommonModelPreferenceNames.TAG_NAME_CASE) == CommonModelPreferenceNames.UPPER);
		fTagNameLower.setSelection(getModelPreferences().getInt(CommonModelPreferenceNames.TAG_NAME_CASE) == CommonModelPreferenceNames.LOWER);
		fAttrNameUpper.setSelection(getModelPreferences().getInt(CommonModelPreferenceNames.ATTR_NAME_CASE) == CommonModelPreferenceNames.UPPER);
		fAttrNameLower.setSelection(getModelPreferences().getInt(CommonModelPreferenceNames.ATTR_NAME_CASE) == CommonModelPreferenceNames.LOWER);

		super.initializeValues();
	}

	protected void storeValues() {
		if (fTagNameUpper.getSelection())
			getModelPreferences().setValue(CommonModelPreferenceNames.TAG_NAME_CASE, CommonModelPreferenceNames.UPPER);
		else
			getModelPreferences().setValue(CommonModelPreferenceNames.TAG_NAME_CASE, CommonModelPreferenceNames.LOWER);
		if (fAttrNameUpper.getSelection())
			getModelPreferences().setValue(CommonModelPreferenceNames.ATTR_NAME_CASE, CommonModelPreferenceNames.UPPER);
		else
			getModelPreferences().setValue(CommonModelPreferenceNames.ATTR_NAME_CASE, CommonModelPreferenceNames.LOWER);

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

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.preferences.ui.XMLSourcePreferencePage#getKey(java.lang.String)
	 */
	protected String getKey(String key) {
		String contentTypeId = IContentTypeIdentifier.ContentTypeID_HTML;
		return PreferenceKeyGenerator.generateKey(key, contentTypeId);
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