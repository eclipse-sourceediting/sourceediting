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
package org.eclipse.wst.css.ui.preferences.ui;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.preferences.CSSModelPreferenceNames;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;
import org.eclipse.wst.css.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.xml.ui.preferences.XMLSourcePreferencePage;

/**
 */
public class CSSSourcePreferencePage extends XMLSourcePreferencePage {
	// Formatting
	private final static String FORMATTING_GROUP = SSEUIPlugin.getResourceString("%Formatting_UI_"); //$NON-NLS-1$
	private final static String FORMATTING_LINE_WIDTH = SSEUIPlugin.getResourceString("%Line_width__UI_"); //$NON-NLS-1$; 
	private final static String FORMATTING_INDENT_USING_TABS = SSEUIPlugin.getResourceString("%&Indent_using_tabs_3"); //$NON-NLS-1$
	// CSS Formatting
	private final static String FORMATTING_INSERT_LINE_BREAK = CSSUIPlugin.getResourceString("%PrefsLabel.WrappingInsertLineBreak"); //$NON-NLS-1$
	private final static String FORMATTING_WRAPPING_WITHOUT_ATTR = CSSUIPlugin.getResourceString("%PrefsLabel.WrappingWithoutAttr");//$NON-NLS-1$

	// Case
	private final static String CASE_GROUP = CSSUIPlugin.getResourceString("%PrefsLabel.CaseGroup"); //$NON-NLS-1$
	private final static String CASE_IDENT = CSSUIPlugin.getResourceString("%PrefsLabel.CaseIdent"); //$NON-NLS-1$
	private final static String CASE_PROP_NAME = CSSUIPlugin.getResourceString("%PrefsLabel.CasePropName"); //$NON-NLS-1$
	private final static String CASE_PROP_VALUE = CSSUIPlugin.getResourceString("%PrefsLabel.CasePropValue"); //$NON-NLS-1$
	private final static String CASE_IDENT_UPPER = CSSUIPlugin.getResourceString("%PrefsLabel.CaseIdentUpper"); //$NON-NLS-1$
	private final static String CASE_IDENT_LOWER = CSSUIPlugin.getResourceString("%PrefsLabel.CaseIdentLower"); //$NON-NLS-1$
	private final static String CASE_PROP_NAME_UPPER = CSSUIPlugin.getResourceString("%PrefsLabel.CasePropNameUpper"); //$NON-NLS-1$
	private final static String CASE_PROP_NAME_LOWER = CSSUIPlugin.getResourceString("%PrefsLabel.CasePropNameLower"); //$NON-NLS-1$
	private final static String CASE_PROP_VALUE_UPPER = CSSUIPlugin.getResourceString("%PrefsLabel.CasePropValueUpper"); //$NON-NLS-1$
	private final static String CASE_PROP_VALUE_LOWER = CSSUIPlugin.getResourceString("%PrefsLabel.CasePropValueLower"); //$NON-NLS-1$

	// one property per one line
	protected Button fPropertyPerLine;
	// prohibit wrapping if style attribute
	protected Button fNowrapAttr;

	// case of output character
	// case of identifier
	protected Button fIdentUpper;
	protected Button fIdentLower;
	// case of property name
	protected Button fPropNameUpper;
	protected Button fPropNameLower;
	// case of property value
	protected Button fPropValueUpper;
	protected Button fPropValueLower;

	protected void createContentsForFormattingGroup(Composite parent) {
		Group formattingGroup = createGroup(parent, 2);
		formattingGroup.setText(FORMATTING_GROUP);
		//		// assigning one help for whole group
		//		WorkbenchHelp.setHelp(formattingGroup,
		// "com.ibm.etools.webedit.core.cssp1200"); //$NON-NLS-1$

		fLineWidthLabel = createLabel(formattingGroup, FORMATTING_LINE_WIDTH);
		fLineWidthText = new Text(formattingGroup, SWT.SINGLE | SWT.BORDER);
		GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.BEGINNING);
		gData.widthHint = 25;
		fLineWidthText.setLayoutData(gData);
		fLineWidthText.addModifyListener(this);

		fPropertyPerLine = createCheckBox(formattingGroup, FORMATTING_INSERT_LINE_BREAK);
		((GridData) fPropertyPerLine.getLayoutData()).horizontalSpan = 2;

		fIndentUsingTabs = createCheckBox(formattingGroup, FORMATTING_INDENT_USING_TABS);
		((GridData) fIndentUsingTabs.getLayoutData()).horizontalSpan = 2;

		fNowrapAttr = createCheckBox(formattingGroup, FORMATTING_WRAPPING_WITHOUT_ATTR);
		((GridData) fNowrapAttr.getLayoutData()).horizontalSpan = 2;
	}

	protected void performDefaultsForFormattingGroup() {
		// Formatting
		Preferences prefs = getModelPreferences();
		fLineWidthText.setText(prefs.getDefaultString(CommonModelPreferenceNames.LINE_WIDTH));
		fPropertyPerLine.setSelection(prefs.getDefaultBoolean(CSSModelPreferenceNames.WRAPPING_ONE_PER_LINE));
		fIndentUsingTabs.setSelection(prefs.getDefaultBoolean(CommonModelPreferenceNames.INDENT_USING_TABS));
		fNowrapAttr.setSelection(prefs.getDefaultBoolean(CSSModelPreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR));
	}

	protected void initializeValuesForFormattingGroup() {
		// Formatting
		Preferences prefs = getModelPreferences();
		fLineWidthText.setText(prefs.getString(CommonModelPreferenceNames.LINE_WIDTH));
		fPropertyPerLine.setSelection(prefs.getBoolean(CSSModelPreferenceNames.WRAPPING_ONE_PER_LINE));
		fIndentUsingTabs.setSelection(prefs.getBoolean(CommonModelPreferenceNames.INDENT_USING_TABS));
		fNowrapAttr.setSelection(prefs.getBoolean(CSSModelPreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR));
	}

	protected void storeValuesForFormattingGroup() {
		// Formatting
		Preferences prefs = getModelPreferences();
		prefs.setValue(CommonModelPreferenceNames.LINE_WIDTH, fLineWidthText.getText());
		prefs.setValue(CSSModelPreferenceNames.WRAPPING_ONE_PER_LINE, fPropertyPerLine.getSelection());
		prefs.setValue(CommonModelPreferenceNames.INDENT_USING_TABS, fIndentUsingTabs.getSelection());
		prefs.setValue(CSSModelPreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR, fNowrapAttr.getSelection());
	}

	protected void createContentsForContentAssistGroup(Composite parent) {
		// not content assist, but preferred case
		Group caseGroup = createGroup(parent, 3);
		caseGroup.setText(CASE_GROUP);
		//		WorkbenchHelp.setHelp(caseGroup,
		// "com.ibm.etools.webedit.core.cssp1400"); //$NON-NLS-1$

		//		createLabel(caseGroup, CASE_IDENT);
		//		createLabel(caseGroup, CASE_PROP_NAME);
		//		createLabel(caseGroup, CASE_PROP_VALUE);

		// use group for radio buttons so that associated label is read
		//		Composite identGroup = createComposite(caseGroup, 1);
		Group identGroup = createGroup(caseGroup, 1);
		identGroup.setText(CASE_IDENT);
		fIdentUpper = createRadioButton(identGroup, CASE_IDENT_UPPER);
		fIdentLower = createRadioButton(identGroup, CASE_IDENT_LOWER);

		// use group for radio buttons so that associated label is read
		//		Composite propNameGroup = createComposite(caseGroup, 1);
		Group propNameGroup = createGroup(caseGroup, 1);
		propNameGroup.setText(CASE_PROP_NAME);
		fPropNameUpper = createRadioButton(propNameGroup, CASE_PROP_NAME_UPPER);
		fPropNameLower = createRadioButton(propNameGroup, CASE_PROP_NAME_LOWER);

		// use group for radio buttons so that associated label is read
		//		Composite propValueGroup = createComposite(caseGroup, 1);
		Group propValueGroup = createGroup(caseGroup, 1);
		propValueGroup.setText(CASE_PROP_VALUE);
		fPropValueUpper = createRadioButton(propValueGroup, CASE_PROP_VALUE_UPPER);
		fPropValueLower = createRadioButton(propValueGroup, CASE_PROP_VALUE_LOWER);
	}

	protected void performDefaultsForContentAssistGroup() {
		// not content assist, but preferred case
		Preferences prefs = getModelPreferences();
		fIdentUpper.setSelection(prefs.getDefaultInt(CSSModelPreferenceNames.CASE_IDENTIFIER) == CommonModelPreferenceNames.UPPER);
		fIdentLower.setSelection(prefs.getDefaultInt(CSSModelPreferenceNames.CASE_IDENTIFIER) == CommonModelPreferenceNames.LOWER);
		fPropNameUpper.setSelection(prefs.getDefaultInt(CSSModelPreferenceNames.CASE_PROPERTY_NAME) == CommonModelPreferenceNames.UPPER);
		fPropNameLower.setSelection(prefs.getDefaultInt(CSSModelPreferenceNames.CASE_PROPERTY_NAME) == CommonModelPreferenceNames.LOWER);
		fPropValueUpper.setSelection(prefs.getDefaultInt(CSSModelPreferenceNames.CASE_PROPERTY_VALUE) == CommonModelPreferenceNames.UPPER);
		fPropValueLower.setSelection(prefs.getDefaultInt(CSSModelPreferenceNames.CASE_PROPERTY_VALUE) == CommonModelPreferenceNames.LOWER);
	}

	protected void initializeValuesForContentAssistGroup() {
		// not content assist, but preferred case
		Preferences prefs = getModelPreferences();
		fIdentUpper.setSelection(prefs.getInt(CSSModelPreferenceNames.CASE_IDENTIFIER) == CommonModelPreferenceNames.UPPER);
		fIdentLower.setSelection(prefs.getInt(CSSModelPreferenceNames.CASE_IDENTIFIER) == CommonModelPreferenceNames.LOWER);
		fPropNameUpper.setSelection(prefs.getInt(CSSModelPreferenceNames.CASE_PROPERTY_NAME) == CommonModelPreferenceNames.UPPER);
		fPropNameLower.setSelection(prefs.getInt(CSSModelPreferenceNames.CASE_PROPERTY_NAME) == CommonModelPreferenceNames.LOWER);
		fPropValueUpper.setSelection(prefs.getInt(CSSModelPreferenceNames.CASE_PROPERTY_VALUE) == CommonModelPreferenceNames.UPPER);
		fPropValueLower.setSelection(prefs.getInt(CSSModelPreferenceNames.CASE_PROPERTY_VALUE) == CommonModelPreferenceNames.LOWER);
	}

	protected void storeValuesForContentAssistGroup() {
		// not content assist, but preferred case
		Preferences prefs = getModelPreferences();
		prefs.setValue(CSSModelPreferenceNames.CASE_IDENTIFIER, (fIdentUpper.getSelection()) ? CommonModelPreferenceNames.UPPER : CommonModelPreferenceNames.LOWER);
		prefs.setValue(CSSModelPreferenceNames.CASE_PROPERTY_NAME, (fPropNameUpper.getSelection()) ? CommonModelPreferenceNames.UPPER : CommonModelPreferenceNames.LOWER);
		prefs.setValue(CSSModelPreferenceNames.CASE_PROPERTY_VALUE, (fPropValueUpper.getSelection()) ? CommonModelPreferenceNames.UPPER : CommonModelPreferenceNames.LOWER);
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	//	protected IPreferenceStore doGetPreferenceStore() {
	//		return CSSUIPlugin.getDefault().getPreferenceStore();
	//	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.xml.preferences.ui.XMLFilesPreferencePage#doSavePreferenceStore()
	 */
	protected void doSavePreferenceStore() {
		CSSUIPlugin.getDefault().savePluginPreferences();
		CSSCorePlugin.getDefault().savePluginPreferences(); // model
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.preferences.ui.AbstractPreferencePage#getModelPreferences()
	 */
	protected Preferences getModelPreferences() {
		return CSSCorePlugin.getDefault().getPluginPreferences();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		WorkbenchHelp.setHelp(c, IHelpContextIds.CSS_PREFWEBX_SOURCE_HELPID);
		return c;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore() {
		return CSSUIPlugin.getDefault().getPreferenceStore();
	}
}