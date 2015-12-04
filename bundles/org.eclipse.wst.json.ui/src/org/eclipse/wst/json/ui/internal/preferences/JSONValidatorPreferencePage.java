/*******************************************************************************
 * Copyright (c) 2001, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver - STAR - [205989] - [validation] validate XML after XInclude resolution
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.preferences.XMLValidatorPreferencePage
 *                                           modified in order to process JSON Objects.                              
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;
import org.eclipse.wst.json.ui.internal.JSONUIMessages;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractValidationSettingsPage;

public class JSONValidatorPreferencePage extends AbstractValidationSettingsPage {
	private static final String SETTINGS_SECTION_NAME = "JSONValidationSeverities";//$NON-NLS-1$

	boolean fOriginalUseXIncludeButtonSelected;

	boolean fOriginalUseHonourAllButtonSelected;

	boolean fOriginalUseExtendedSyntaxValidation;

	private Combo fIndicateNoGrammar = null;

	private Combo fIndicateNoDocumentElement = null;

	private Button fHonourAllSchemaLocations = null;

	private Button fUseXinclude = null;

	private Button fExtendedSyntaxValidation;

	private Combo fMissingStartTag;

	private Combo fMissingEndTag;

	private Combo fMissingTagName;

	private Combo fEmptyElementTag;

	private Combo fEndTagWithAttributes;

	private Combo fInvalidWhitespaceBeforeTagname;

	private Combo fMissingClosingBracket;

	private Combo fMissingClosingQuote;

	private Combo fMissingQuotes;

	private Combo fInvalidNamespaceInPI;

	private Combo fInvalidWhitespaceAtStart;

	private Group fSyntaxValidationGroup;
	private ControlEnableState fSyntaxState;

	private static final int[] JSON_SEVERITIES = { ValidationMessage.WARNING,
			ValidationMessage.ERROR, ValidationMessage.IGNORE };

	private static final String[] SYNTAX_SEVERITIES = {
			JSONUIMessages.Severity_error, JSONUIMessages.Severity_warning,
			JSONUIMessages.Severity_ignore };

	private boolean getBooleanPreference(String key, boolean defaultValue,
			IScopeContext[] contexts) {
		return Platform.getPreferencesService().getBoolean(
				getPreferenceNodeQualifier(), key, defaultValue, contexts);
	}

	private void handleSyntaxSeveritySelection(boolean selection) {
		if (selection) {
			if (fSyntaxState != null) {
				fSyntaxState.restore();
				fSyntaxState = null;
			}
		} else {
			if (fSyntaxState == null)
				fSyntaxState = ControlEnableState
						.disable(fSyntaxValidationGroup);
		}
	}

	protected void createContentsForSyntaxValidationGroup(Composite parent) {

		IScopeContext[] contexts = createPreferenceScopes();

		fOriginalUseExtendedSyntaxValidation = getBooleanPreference(
				JSONCorePreferenceNames.SYNTAX_VALIDATION, false, contexts);
		fExtendedSyntaxValidation = createCheckBox(parent,
				JSONUIMessages.SyntaxValidation_files);

		((GridData) fExtendedSyntaxValidation.getLayoutData()).horizontalSpan = 2;
		fExtendedSyntaxValidation
				.setSelection(fOriginalUseExtendedSyntaxValidation);

		fExtendedSyntaxValidation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleSyntaxSeveritySelection(fExtendedSyntaxValidation
						.getSelection());
			}
		});

		fSyntaxValidationGroup = createGroup(parent, 3);
		((GridLayout) fSyntaxValidationGroup.getLayout()).makeColumnsEqualWidth = false;
		fSyntaxValidationGroup
				.setText(JSONUIMessages.SyntaxValidation_files_label);
		GridLayout layout = new GridLayout(3, false);
		fSyntaxValidationGroup.setLayout(layout);

		if (fMissingStartTag == null) {
			fMissingStartTag = addComboBox(fSyntaxValidationGroup,
					JSONUIMessages.Missing_start_object,
					JSONCorePreferenceNames.MISSING_START_OBJECT, JSON_SEVERITIES,
					SYNTAX_SEVERITIES, 0);
		}
		if (fMissingEndTag == null) {
			fMissingEndTag = addComboBox(fSyntaxValidationGroup,
					JSONUIMessages.Missing_end_object,
					JSONCorePreferenceNames.MISSING_END_OBJECT, JSON_SEVERITIES,
					SYNTAX_SEVERITIES, 0);
		}
//		if (fMissingEndTag == null)
//			fMissingEndTag = addComboBox(fSyntaxValidationGroup,
//					JSONUIMessages.Missing_end_tag,
//					JSONCorePreferenceNames.MISSING_END_TAG, JSON_SEVERITIES,
//					SYNTAX_SEVERITIES, 0);
//		if (fMissingTagName == null)
//			fMissingTagName = addComboBox(fSyntaxValidationGroup,
//					JSONUIMessages.Tag_name_missing,
//					JSONCorePreferenceNames.MISSING_TAG_NAME, JSON_SEVERITIES,
//					SYNTAX_SEVERITIES, 0);
//		if (fMissingQuotes == null)
//			fMissingQuotes = addComboBox(fSyntaxValidationGroup,
//					JSONUIMessages.Missing_quotes,
//					JSONCorePreferenceNames.MISSING_QUOTES, JSON_SEVERITIES,
//					SYNTAX_SEVERITIES, 0);
//		if (fMissingClosingBracket == null)
//			fMissingClosingBracket = addComboBox(fSyntaxValidationGroup,
//					JSONUIMessages.Missing_closing_bracket,
//					JSONCorePreferenceNames.MISSING_CLOSING_BRACKET,
//					JSON_SEVERITIES, SYNTAX_SEVERITIES, 0);
//		if (fMissingClosingQuote == null)
//			fMissingClosingQuote = addComboBox(fSyntaxValidationGroup,
//					JSONUIMessages.Missing_closing_quote,
//					JSONCorePreferenceNames.MISSING_CLOSING_QUOTE,
//					JSON_SEVERITIES, SYNTAX_SEVERITIES, 0);
//		if (fEmptyElementTag == null)
//			fEmptyElementTag = addComboBox(fSyntaxValidationGroup,
//					JSONUIMessages.Empty_element_tag,
//					JSONCorePreferenceNames.ATTRIBUTE_HAS_NO_VALUE,
//					JSON_SEVERITIES, SYNTAX_SEVERITIES, 0);
//		if (fEndTagWithAttributes == null)
//			fEndTagWithAttributes = addComboBox(fSyntaxValidationGroup,
//					JSONUIMessages.End_tag_with_attributes,
//					JSONCorePreferenceNames.END_TAG_WITH_ATTRIBUTES,
//					JSON_SEVERITIES, SYNTAX_SEVERITIES, 0);
//		if (fInvalidWhitespaceBeforeTagname == null)
//			fInvalidWhitespaceBeforeTagname = addComboBox(
//					fSyntaxValidationGroup,
//					JSONUIMessages.Invalid_whitespace_before_tagname,
//					JSONCorePreferenceNames.WHITESPACE_BEFORE_TAGNAME,
//					JSON_SEVERITIES, SYNTAX_SEVERITIES, 0);
//		if (fInvalidNamespaceInPI == null)
//			fInvalidNamespaceInPI = addComboBox(fSyntaxValidationGroup,
//					JSONUIMessages.Namespace_in_pi_target,
//					JSONCorePreferenceNames.NAMESPACE_IN_PI_TARGET,
//					JSON_SEVERITIES, SYNTAX_SEVERITIES, 0);
//		if (fInvalidWhitespaceAtStart == null)
//			fInvalidWhitespaceAtStart = addComboBox(fSyntaxValidationGroup,
//					JSONUIMessages.Whitespace_at_start,
//					JSONCorePreferenceNames.WHITESPACE_AT_START,
//					JSON_SEVERITIES, SYNTAX_SEVERITIES, 0);

		handleSyntaxSeveritySelection(fOriginalUseExtendedSyntaxValidation);

	}

//	protected void performDefaultsForValidatingGroup() {
//		IEclipsePreferences modelPreferences = new DefaultScope()
//				.getNode(getPreferenceNodeQualifier());
//		boolean useXIncludeButtonSelected = modelPreferences.getBoolean(
//				JSONCorePreferenceNames.USE_XINCLUDE, false);
//
//		if (fUseXinclude != null) {
//			fUseXinclude.setSelection(useXIncludeButtonSelected);
//		}
//		boolean useHonourAllButtonSelected = modelPreferences.getBoolean(
//				JSONCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS, true);
//		if (fHonourAllSchemaLocations != null) {
//			fHonourAllSchemaLocations.setSelection(useHonourAllButtonSelected);
//		}
//	}

	protected void performDefaultsForSyntaxValidationGroup() {
		IEclipsePreferences modelPreferences = new DefaultScope()
				.getNode(getPreferenceNodeQualifier());
		boolean useExtendedSyntaxValidation = modelPreferences.getBoolean(
				JSONCorePreferenceNames.SYNTAX_VALIDATION, false);

		if (fExtendedSyntaxValidation != null) {
			if (fExtendedSyntaxValidation.getSelection() != useExtendedSyntaxValidation) {
				handleSyntaxSeveritySelection(useExtendedSyntaxValidation);
			}
			fExtendedSyntaxValidation.setSelection(useExtendedSyntaxValidation);
		}
	}

//	protected void storeValuesForValidatingGroup(IScopeContext[] contexts) {
//		if (fUseXinclude != null) {
//			boolean useXIncludeButtonSelected = fUseXinclude.getSelection();
//			contexts[0].getNode(getPreferenceNodeQualifier()).putBoolean(
//					JSONCorePreferenceNames.USE_XINCLUDE,
//					useXIncludeButtonSelected);
//		}
//		if (fHonourAllSchemaLocations != null) {
//			boolean honourAllButtonSelected = fHonourAllSchemaLocations
//					.getSelection();
//			contexts[0].getNode(getPreferenceNodeQualifier()).putBoolean(
//					JSONCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS,
//					honourAllButtonSelected);
//		}
//	}

	protected void storeValuesForSyntaxValidationGroup(IScopeContext[] contexts) {
		if (fExtendedSyntaxValidation != null) {
			boolean extendedSyntaxValidation = fExtendedSyntaxValidation
					.getSelection();
			contexts[0].getNode(getPreferenceNodeQualifier()).putBoolean(
					JSONCorePreferenceNames.SYNTAX_VALIDATION,
					extendedSyntaxValidation);
		}
	}

	protected void performDefaults() {
		resetSeverities();
		//performDefaultsForValidatingGroup();
		performDefaultsForSyntaxValidationGroup();
		super.performDefaults();
	}

	protected Preferences getModelPreferences() {
		return JSONCorePlugin.getDefault().getPluginPreferences();
	}

	protected void doSavePreferenceStore() {
		JSONCorePlugin.getDefault().savePluginPreferences(); // model
	}

	protected void storeValues() {
		super.storeValues();
		IScopeContext[] contexts = createPreferenceScopes();

		//storeValuesForValidatingGroup(contexts);
		storeValuesForSyntaxValidationGroup(contexts);
	}

	@Override
	protected Control createCommonContents(Composite parent) {
		final Composite page = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		page.setLayout(layout);

		//Group validatingGroup = createGroup(page, 3);
		//validatingGroup.setText(JSONUIMessages.Validating_files);
		//createContentsForValidatingGroup(validatingGroup);

		createContentsForSyntaxValidationGroup(page);

		return page;
	}

	protected String getPreferenceNodeQualifier() {
		return JSONCorePlugin.getDefault().getBundle().getSymbolicName();
	}

	protected String getPreferencePageID() {
		return "org.eclipse.wst.sse.ui.preferences.json.validation";//$NON-NLS-1$
	}

	protected String getProjectSettingsKey() {
		return JSONCorePreferenceNames.USE_PROJECT_SETTINGS;
	}

	protected String getPropertyPageID() {
		return "org.eclipse.wst.json.ui.propertyPage.project.validation";//$NON-NLS-1$
	}

	public void init(IWorkbench workbench) {
	}

	private Group createGroup(Composite parent, int numColumns) {

		Group group = new Group(parent, SWT.NULL);

		// GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		group.setLayout(layout);

		// GridData
		GridData data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		group.setLayoutData(data);

		return group;
	}

	private Button createCheckBox(Composite group, String label) {
		Button button = new Button(group, SWT.CHECK | SWT.LEFT);
		button.setText(label);

		// button.setLayoutData(GridDataFactory.fillDefaults().create());

		// GridData
		GridData data = new GridData(GridData.FILL);
		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		button.setLayoutData(data);

		return button;
	}

	public void dispose() {
		storeSectionExpansionStates(getDialogSettings().addNewSection(
				SETTINGS_SECTION_NAME));
		super.dispose();
	}

	protected IDialogSettings getDialogSettings() {
		return JSONUIPlugin.getDefault().getDialogSettings();
	}

	protected boolean shouldRevalidateOnSettingsChange() {
		return fOriginalUseExtendedSyntaxValidation != fExtendedSyntaxValidation
				.getSelection()
				|| fOriginalUseXIncludeButtonSelected != fUseXinclude
						.getSelection()
				|| fOriginalUseHonourAllButtonSelected != fHonourAllSchemaLocations
						.getSelection()
				|| super.shouldRevalidateOnSettingsChange();
	}
}
