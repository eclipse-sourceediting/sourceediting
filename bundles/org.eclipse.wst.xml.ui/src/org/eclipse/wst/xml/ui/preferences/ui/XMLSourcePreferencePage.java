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
package org.eclipse.wst.xml.ui.preferences.ui;



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
import org.eclipse.wst.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.sse.ui.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.xml.core.XMLModelPlugin;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;


public class XMLSourcePreferencePage extends AbstractPreferencePage implements ModifyListener, SelectionListener, IWorkbenchPreferencePage {

	/**
	 * @deprecated there is no longer a source group.  all source group preferences were moved to StructuredTextEditorPreferencePage
	 */
	protected Button fShowHoverHelp;

	// Formatting
	protected Label fLineWidthLabel;
	protected Text fLineWidthText;
	protected Button fSplitMultiAttrs;
	protected Button fIndentUsingTabs;
	protected Button fClearAllBlankLines;
	// Content Assist
	protected Button fAutoPropose;
	protected Label fAutoProposeLabel;
	protected Text fAutoProposeText;
	// grammar constraints
	protected Button fUseInferredGrammar;

	protected IPreferenceStore doGetPreferenceStore() {
		return EditorPlugin.getDefault().getPreferenceStore();
	}

	protected Preferences getModelPreferences() {
		return XMLModelPlugin.getDefault().getPluginPreferences();
	}

	protected void doSavePreferenceStore() {
		EditorPlugin.getDefault().savePluginPreferences(); // editor
		XMLModelPlugin.getDefault().savePluginPreferences(); // model
	}

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

	/**
	 * @deprecated there is no longer a source group.  all source group preferences were moved to StructuredTextEditorPreferencePage
	 */
	protected void createContentsForSourceGroup(Composite parent) {
		Group sourcePageGroup = createGroup(parent, 1);
		sourcePageGroup.setText(ResourceHandler.getString("Source_page_UI_")); //$NON-NLS-1$ = "Source page"
	}

	protected void createContentsForFormattingGroup(Composite parent) {
		Group formattingGroup = createGroup(parent, 2);
		formattingGroup.setText(ResourceHandler.getString("Formatting_UI_")); //$NON-NLS-1$ = "Formatting"
		
		fLineWidthLabel = createLabel(formattingGroup, ResourceHandler.getString("Line_width__UI_")); //$NON-NLS-1$ = "Line width:"
		fLineWidthText = new Text(formattingGroup, SWT.SINGLE | SWT.BORDER);
		GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.BEGINNING);
		gData.widthHint = 25;
		fLineWidthText.setLayoutData(gData);
		fLineWidthText.addModifyListener(this);

		fSplitMultiAttrs = createCheckBox(formattingGroup, ResourceHandler.getString("Split_&multiple_attributes_2")); //$NON-NLS-1$
		((GridData) fSplitMultiAttrs.getLayoutData()).horizontalSpan = 2;

		fIndentUsingTabs = createCheckBox(formattingGroup, ResourceHandler.getString("&Indent_using_tabs_3")); //$NON-NLS-1$
		((GridData) fIndentUsingTabs.getLayoutData()).horizontalSpan = 2;

		fClearAllBlankLines = createCheckBox(formattingGroup, ResourceHandler.getString("Clear_all_blank_lines_UI_")); //$NON-NLS-1$ = "Clear all blank lines"
		((GridData) fClearAllBlankLines.getLayoutData()).horizontalSpan = 2;
	}

	protected void createContentsForContentAssistGroup(Composite parent) {
		Group contentAssistGroup = createGroup(parent, 2);
		contentAssistGroup.setText(ResourceHandler.getString("Content_assist_UI_")); //$NON-NLS-1$ = "Content assist"

		fAutoPropose = createCheckBox(contentAssistGroup, ResourceHandler.getString("Automatically_make_suggest_UI_")); //$NON-NLS-1$ = "Automatically make suggestions"
		((GridData) fAutoPropose.getLayoutData()).horizontalSpan = 2;
		fAutoPropose.addSelectionListener(this);

		fAutoProposeLabel = createLabel(contentAssistGroup, ResourceHandler.getString("Prompt_when_these_characte_UI_")); //$NON-NLS-1$ = "Prompt when these characters are inserted:"
		fAutoProposeText = createTextField(contentAssistGroup);
	}

	protected void createContentsForGrammarConstraintsGroup(Composite parent) {
		Group grammarConstraintsGroup = createGroup(parent, 1);
		grammarConstraintsGroup.setText(ResourceHandler.getString("Grammar_Constraints")); //$NON-NLS-1$
		grammarConstraintsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		fUseInferredGrammar = createCheckBox(grammarConstraintsGroup, ResourceHandler.getString("Use_inferred_grammar_in_absence_of_DTD/Schema")); //$NON-NLS-1$
	}

	protected void performDefaults() {
		performDefaultsForFormattingGroup();
		performDefaultsForContentAssistGroup();
		performDefaultsForGrammarConstraintsGroup();

		validateValues();
		enableValues();

		super.performDefaults();
	}

	/**
	 * @deprecated there is no longer a source group.  all source group preferences were moved to StructuredTextEditorPreferencePage
	 */
	protected void performDefaultsForSourceGroup() {
	}

	protected void performDefaultsForFormattingGroup() {
		// Formatting
		fLineWidthText.setText(getModelPreferences().getDefaultString(CommonModelPreferenceNames.LINE_WIDTH));
		fSplitMultiAttrs.setSelection(getModelPreferences().getDefaultBoolean(CommonModelPreferenceNames.SPLIT_MULTI_ATTRS));
		fIndentUsingTabs.setSelection(getModelPreferences().getDefaultBoolean(CommonModelPreferenceNames.INDENT_USING_TABS));
		fClearAllBlankLines.setSelection(getModelPreferences().getDefaultBoolean(CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES));
	}

	protected void performDefaultsForContentAssistGroup() {
		// Content Assist
		fAutoPropose.setSelection(getPreferenceStore().getDefaultBoolean(getKey(CommonEditorPreferenceNames.AUTO_PROPOSE)));
		fAutoProposeText.setText(getPreferenceStore().getDefaultString(getKey(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE)));
	}

	protected void performDefaultsForGrammarConstraintsGroup() {
		fUseInferredGrammar.setSelection(getPreferenceStore().getDefaultBoolean(getKey(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR)));
	}

	protected void initializeValues() {
		initializeValuesForFormattingGroup();
		initializeValuesForContentAssistGroup();
		initializeValuesForGrammarConstraintsGroup();
	}

	/**
	 * @deprecated there is no longer a source group.  all source group preferences were moved to StructuredTextEditorPreferencePage
	 */
	protected void initializeValuesForSourceGroup() {
	}

	/*
	 * helper method to generate content type id specific preference keys
	 */
	protected String getKey(String key) {
		String contentTypeId = IContentTypeIdentifier.ContentTypeID_SSEXML;
		return PreferenceKeyGenerator.generateKey(key, contentTypeId);
	}

	protected void initializeValuesForFormattingGroup() {
		// Formatting
		fLineWidthText.setText(getModelPreferences().getString(CommonModelPreferenceNames.LINE_WIDTH));
		fSplitMultiAttrs.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.SPLIT_MULTI_ATTRS));
		fIndentUsingTabs.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.INDENT_USING_TABS));
		fClearAllBlankLines.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES));
	}

	protected void initializeValuesForContentAssistGroup() {
		// Content Assist
		fAutoPropose.setSelection(getPreferenceStore().getBoolean(getKey(CommonEditorPreferenceNames.AUTO_PROPOSE)));
		fAutoProposeText.setText(getPreferenceStore().getString(getKey(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE)));
	}

	protected void initializeValuesForGrammarConstraintsGroup() {
		fUseInferredGrammar.setSelection(getPreferenceStore().getBoolean(getKey(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR)));
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
			}
			catch (NumberFormatException nfexc) {
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

	protected void enableValues() {
		if (fAutoPropose != null) {
			if (fAutoPropose.getSelection()) {
				fAutoProposeLabel.setEnabled(true);
				fAutoProposeText.setEnabled(true);
			}
			else {
				fAutoProposeLabel.setEnabled(false);
				fAutoProposeText.setEnabled(false);
			}
		}
	}

	protected void storeValues() {
		storeValuesForFormattingGroup();
		storeValuesForContentAssistGroup();
		storeValuesForGrammarConstraintsGroup();
	}

	/**
	 * @deprecated there is no longer a source group.  all source group preferences were moved to StructuredTextEditorPreferencePage
	 */
	protected void storeValuesForSourceGroup() {
	}

	protected void storeValuesForFormattingGroup() {
		// Formatting
		getModelPreferences().setValue(CommonModelPreferenceNames.LINE_WIDTH, fLineWidthText.getText());
		getModelPreferences().setValue(CommonModelPreferenceNames.SPLIT_MULTI_ATTRS, fSplitMultiAttrs.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.INDENT_USING_TABS, fIndentUsingTabs.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES, fClearAllBlankLines.getSelection());
	}

	protected void storeValuesForContentAssistGroup() {
		// Content Assist
		getPreferenceStore().setValue(getKey(CommonEditorPreferenceNames.AUTO_PROPOSE), fAutoPropose.getSelection());
		getPreferenceStore().setValue(getKey(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE), fAutoProposeText.getText());
	}

	protected void storeValuesForGrammarConstraintsGroup() {
		getPreferenceStore().setValue(getKey(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR), fUseInferredGrammar.getSelection());
	}

	public boolean performOk() {
		boolean result = super.performOk();

		doSavePreferenceStore();

		return result;
	}
}
