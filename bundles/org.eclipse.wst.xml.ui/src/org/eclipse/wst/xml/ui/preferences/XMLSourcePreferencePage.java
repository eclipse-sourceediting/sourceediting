/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;

public class XMLSourcePreferencePage extends AbstractPreferencePage implements ModifyListener, SelectionListener, IWorkbenchPreferencePage {
	// Content Assist
	protected Button fAutoPropose;
	protected Label fAutoProposeLabel;
	protected Text fAutoProposeText;
	protected Button fClearAllBlankLines;
	protected Button fIndentUsingTabs;
	// Formatting
	protected Label fLineWidthLabel;
	protected Text fLineWidthText;
	protected Button fSplitMultiAttrs;
	// grammar constraints
	protected Button fUseInferredGrammar;

	protected Control createContents(Composite parent) {
		Composite composite = (Composite) super.createContents(parent);
		WorkbenchHelp.setHelp(composite, IHelpContextIds.XML_PREFWEBX_SOURCE_HELPID);

		createContentsForFormattingGroup(composite);
		createContentsForContentAssistGroup(composite);
		createContentsForGrammarConstraintsGroup(composite);
		setSize(composite);
		loadPreferences();

		return composite;
	}

	protected void createContentsForContentAssistGroup(Composite parent) {
		Group contentAssistGroup = createGroup(parent, 2);
		contentAssistGroup.setText(SSEUIPlugin.getResourceString("%Content_assist_UI_")); //$NON-NLS-1$ = "Content assist"

		fAutoPropose = createCheckBox(contentAssistGroup, SSEUIPlugin.getResourceString("%Automatically_make_suggest_UI_")); //$NON-NLS-1$ = "Automatically make suggestions"
		((GridData) fAutoPropose.getLayoutData()).horizontalSpan = 2;
		fAutoPropose.addSelectionListener(this);

		fAutoProposeLabel = createLabel(contentAssistGroup, SSEUIPlugin.getResourceString("%Prompt_when_these_characte_UI_")); //$NON-NLS-1$ = "Prompt when these characters are inserted:"
		fAutoProposeText = createTextField(contentAssistGroup);
	}

	protected void createContentsForFormattingGroup(Composite parent) {
		Group formattingGroup = createGroup(parent, 2);
		formattingGroup.setText(SSEUIPlugin.getResourceString("%Formatting_UI_")); //$NON-NLS-1$ = "Formatting"

		fLineWidthLabel = createLabel(formattingGroup, SSEUIPlugin.getResourceString("%Line_width__UI_")); //$NON-NLS-1$ = "Line width:"
		fLineWidthText = new Text(formattingGroup, SWT.SINGLE | SWT.BORDER);
		GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.BEGINNING);
		gData.widthHint = 25;
		fLineWidthText.setLayoutData(gData);
		fLineWidthText.addModifyListener(this);

		fSplitMultiAttrs = createCheckBox(formattingGroup, SSEUIPlugin.getResourceString("%Split_&multiple_attributes_2")); //$NON-NLS-1$
		((GridData) fSplitMultiAttrs.getLayoutData()).horizontalSpan = 2;

		fIndentUsingTabs = createCheckBox(formattingGroup, SSEUIPlugin.getResourceString("%&Indent_using_tabs_3")); //$NON-NLS-1$
		((GridData) fIndentUsingTabs.getLayoutData()).horizontalSpan = 2;

		fClearAllBlankLines = createCheckBox(formattingGroup, SSEUIPlugin.getResourceString("%Clear_all_blank_lines_UI_")); //$NON-NLS-1$ = "Clear all blank lines"
		((GridData) fClearAllBlankLines.getLayoutData()).horizontalSpan = 2;
	}

	protected void createContentsForGrammarConstraintsGroup(Composite parent) {
		Group grammarConstraintsGroup = createGroup(parent, 1);
		grammarConstraintsGroup.setText(SSEUIPlugin.getResourceString("%Grammar_Constraints")); //$NON-NLS-1$
		grammarConstraintsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		fUseInferredGrammar = createCheckBox(grammarConstraintsGroup, SSEUIPlugin.getResourceString("%Use_inferred_grammar_in_absence_of_DTD/Schema")); //$NON-NLS-1$
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return XMLUIPlugin.getDefault().getPreferenceStore();
	}

	protected void doSavePreferenceStore() {
		XMLUIPlugin.getDefault().savePluginPreferences(); // editor
		XMLCorePlugin.getDefault().savePluginPreferences(); // model
	}

	protected void enableValues() {
		if (fAutoPropose != null) {
			if (fAutoPropose.getSelection()) {
				fAutoProposeLabel.setEnabled(true);
				fAutoProposeText.setEnabled(true);
			} else {
				fAutoProposeLabel.setEnabled(false);
				fAutoProposeText.setEnabled(false);
			}
		}
	}

	/**
	 * @deprecated key no longer needed (preference should be stored in their own preference store)
	 */
	protected String getKey(String key) {
//		String contentTypeId = IContentTypeIdentifier.ContentTypeID_SSEXML;
//		return PreferenceKeyGenerator.generateKey(key, contentTypeId);
		return key;
	}

	protected Preferences getModelPreferences() {
		return XMLCorePlugin.getDefault().getPluginPreferences();
	}

	protected void initializeValues() {
		initializeValuesForFormattingGroup();
		initializeValuesForContentAssistGroup();
		initializeValuesForGrammarConstraintsGroup();
	}

	protected void initializeValuesForContentAssistGroup() {
		// Content Assist
		fAutoPropose.setSelection(getPreferenceStore().getBoolean(CommonEditorPreferenceNames.AUTO_PROPOSE));
		fAutoProposeText.setText(getPreferenceStore().getString(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE));
	}

	protected void initializeValuesForFormattingGroup() {
		// Formatting
		fLineWidthText.setText(getModelPreferences().getString(CommonModelPreferenceNames.LINE_WIDTH));
		fSplitMultiAttrs.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.SPLIT_MULTI_ATTRS));
		fIndentUsingTabs.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.INDENT_USING_TABS));
		fClearAllBlankLines.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES));
	}

	protected void initializeValuesForGrammarConstraintsGroup() {
		fUseInferredGrammar.setSelection(getPreferenceStore().getBoolean(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR));
	}

	protected void performDefaults() {
		performDefaultsForFormattingGroup();
		performDefaultsForContentAssistGroup();
		performDefaultsForGrammarConstraintsGroup();

		validateValues();
		enableValues();

		super.performDefaults();
	}

	protected void performDefaultsForContentAssistGroup() {
		// Content Assist
		fAutoPropose.setSelection(getPreferenceStore().getDefaultBoolean(CommonEditorPreferenceNames.AUTO_PROPOSE));
		fAutoProposeText.setText(getPreferenceStore().getDefaultString(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE));
	}

	protected void performDefaultsForFormattingGroup() {
		// Formatting
		fLineWidthText.setText(getModelPreferences().getDefaultString(CommonModelPreferenceNames.LINE_WIDTH));
		fSplitMultiAttrs.setSelection(getModelPreferences().getDefaultBoolean(CommonModelPreferenceNames.SPLIT_MULTI_ATTRS));
		fIndentUsingTabs.setSelection(getModelPreferences().getDefaultBoolean(CommonModelPreferenceNames.INDENT_USING_TABS));
		fClearAllBlankLines.setSelection(getModelPreferences().getDefaultBoolean(CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES));
	}

	protected void performDefaultsForGrammarConstraintsGroup() {
		fUseInferredGrammar.setSelection(getPreferenceStore().getDefaultBoolean(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR));
	}

	public boolean performOk() {
		boolean result = super.performOk();

		doSavePreferenceStore();

		return result;
	}

	protected void storeValues() {
		storeValuesForFormattingGroup();
		storeValuesForContentAssistGroup();
		storeValuesForGrammarConstraintsGroup();
	}

	protected void storeValuesForContentAssistGroup() {
		// Content Assist
		getPreferenceStore().setValue(CommonEditorPreferenceNames.AUTO_PROPOSE, fAutoPropose.getSelection());
		getPreferenceStore().setValue(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, fAutoProposeText.getText());
	}

	protected void storeValuesForFormattingGroup() {
		// Formatting
		getModelPreferences().setValue(CommonModelPreferenceNames.LINE_WIDTH, fLineWidthText.getText());
		getModelPreferences().setValue(CommonModelPreferenceNames.SPLIT_MULTI_ATTRS, fSplitMultiAttrs.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.INDENT_USING_TABS, fIndentUsingTabs.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES, fClearAllBlankLines.getSelection());
	}

	protected void storeValuesForGrammarConstraintsGroup() {
		getPreferenceStore().setValue(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR, fUseInferredGrammar.getSelection());
	}

	protected void validateValues() {
		boolean isError = false;
		String widthText = null;

		if (fLineWidthText != null) {
			try {
				widthText = fLineWidthText.getText();
				int formattingLineWidth = Integer.parseInt(widthText);
				if ((formattingLineWidth < WIDTH_VALIDATION_LOWER_LIMIT) || (formattingLineWidth > WIDTH_VALIDATION_UPPER_LIMIT))
					throw new NumberFormatException();
			} catch (NumberFormatException nfexc) {
				setInvalidInputMessage(widthText);
				setValid(false);
				isError = true;
			}
		}

		if (!isError) {
			setErrorMessage(null);
			setValid(true);
		}
	}
}
