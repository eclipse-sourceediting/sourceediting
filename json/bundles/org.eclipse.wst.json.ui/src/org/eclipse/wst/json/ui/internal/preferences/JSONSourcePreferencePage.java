/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;
import org.eclipse.wst.json.ui.internal.JSONUIMessages;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;

/**
 */
public class JSONSourcePreferencePage extends AbstractPreferencePage {

	protected Button fSplitMultiAttrs;
	private final int MAX_INDENTATION_SIZE = 16;
	private final int MIN_INDENTATION_SIZE = 0;
	private Label fLineWidthLabel;
	private Text fLineWidthText;
	private Button fIndentUsingTabs;
	private Button fIndentUsingSpaces;
	private Spinner fIndentationSize;


	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		final Composite composite = super.createComposite(parent, 1);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
		// IHelpContextIds.CSS_PREFWEBX_SOURCE_HELPID);

		new PreferenceLinkArea(composite, SWT.WRAP | SWT.MULTI, "org.eclipse.wst.sse.ui.preferences.editor", //$NON-NLS-1$
				JSONUIMessages._UI_STRUCTURED_TEXT_EDITOR_PREFS_LINK,
					(IWorkbenchPreferenceContainer) getContainer(), null).getControl().setLayoutData(GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).create());
		new Label(composite, SWT.NONE).setLayoutData(GridDataFactory.swtDefaults().create());

		createContentsForFormattingGroup(composite);
		setSize(composite);
		loadPreferences();

		return composite;
	}

	@SuppressWarnings("restriction")
	private void createContentsForFormattingGroup(Composite parent) {
		Group formattingGroup = createGroup(parent, 2);
		formattingGroup.setText(JSONUIMessages.Formatting_UI_);

		fLineWidthLabel = createLabel(formattingGroup, JSONUIMessages.Line_width__UI_);
		fLineWidthText = new Text(formattingGroup, SWT.SINGLE | SWT.BORDER);
		GridData gData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.BEGINNING);
		gData.widthHint = 25;
		fLineWidthText.setLayoutData(gData);
		fLineWidthText.addModifyListener(this);

		// [269224] - Place the indent controls in their own composite for proper tab ordering
		Composite indentComposite = createComposite(formattingGroup, 1);
		((GridData) indentComposite.getLayoutData()).horizontalSpan = 2;
		((GridLayout) indentComposite.getLayout()).marginWidth = 0;
		((GridLayout) indentComposite.getLayout()).marginHeight = 0;

		fIndentUsingTabs = createRadioButton(indentComposite, JSONUIMessages.Indent_using_tabs_);
		((GridData) fIndentUsingTabs.getLayoutData()).horizontalSpan = 1;
		fIndentUsingSpaces = createRadioButton(indentComposite, JSONUIMessages.Indent_using_spaces);
		((GridData) fIndentUsingSpaces.getLayoutData()).horizontalSpan = 1;

		createLabel(formattingGroup, JSONUIMessages.Indentation_size);
		fIndentationSize = new Spinner(formattingGroup, SWT.BORDER);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		fIndentationSize.setLayoutData(gd);
		fIndentationSize.setToolTipText(JSONUIMessages.Indentation_size_tip);
		fIndentationSize.setMinimum(MIN_INDENTATION_SIZE);
		fIndentationSize.setMaximum(MAX_INDENTATION_SIZE);
		fIndentationSize.setIncrement(1);
		fIndentationSize.setPageIncrement(4);
		fIndentationSize.addModifyListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return JSONUIPlugin.getDefault().getPreferenceStore();
	}

	private void doSavePreferenceStore() {
		JSONUIPlugin.getDefault().savePluginPreferences();
		JSONCorePlugin.getDefault().savePluginPreferences(); // model
	}

	@Override
	protected Preferences getModelPreferences() {
		return JSONCorePlugin.getDefault().getPluginPreferences();
	}

	@Override
	protected void initializeValues() {
		initializeValuesForFormattingGroup();
	}


	private void initializeValuesForFormattingGroup() {
		// Formatting
		Preferences prefs = getModelPreferences();
		fLineWidthText.setText(prefs.getString(JSONCorePreferenceNames.LINE_WIDTH));

		if (JSONCorePreferenceNames.TAB
				.equals(getModelPreferences().getString(JSONCorePreferenceNames.INDENTATION_CHAR))) {
			fIndentUsingTabs.setSelection(true);
			fIndentUsingSpaces.setSelection(false);
		} else {
			fIndentUsingSpaces.setSelection(true);
			fIndentUsingTabs.setSelection(false);
		}

		fIndentationSize.setSelection(getModelPreferences().getInt(JSONCorePreferenceNames.INDENTATION_SIZE));
	}

	@Override
	protected void performDefaults() {
		performDefaultsForFormattingGroup();
		validateValues();
		enableValues();
		super.performDefaults();
	}

	private void performDefaultsForFormattingGroup() {
		// Formatting
		Preferences prefs = getModelPreferences();
		fLineWidthText.setText(prefs.getDefaultString(JSONCorePreferenceNames.LINE_WIDTH));

		if (JSONCorePreferenceNames.TAB
				.equals(getModelPreferences().getDefaultString(JSONCorePreferenceNames.INDENTATION_CHAR))) {
			fIndentUsingTabs.setSelection(true);
			fIndentUsingSpaces.setSelection(false);
		} else {
			fIndentUsingSpaces.setSelection(true);
			fIndentUsingTabs.setSelection(false);
		}
		fIndentationSize.setSelection(getModelPreferences().getDefaultInt(JSONCorePreferenceNames.INDENTATION_SIZE));
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();

		doSavePreferenceStore();

		return result;
	}

	@Override
	protected void storeValues() {
		storeValuesForFormattingGroup();
	}

	private void storeValuesForFormattingGroup() {
		// Formatting
		Preferences prefs = getModelPreferences();
		prefs.setValue(JSONCorePreferenceNames.LINE_WIDTH, fLineWidthText.getText());

		if (fIndentUsingTabs.getSelection()) {
			getModelPreferences().setValue(JSONCorePreferenceNames.INDENTATION_CHAR, JSONCorePreferenceNames.TAB);
		} else {
			getModelPreferences().setValue(JSONCorePreferenceNames.INDENTATION_CHAR, JSONCorePreferenceNames.SPACE);
		}
		getModelPreferences().setValue(JSONCorePreferenceNames.INDENTATION_SIZE, fIndentationSize.getSelection());
	}

	@Override
	protected void validateValues() {
		boolean isError = false;
		String widthText = null;

		if (fLineWidthText != null) {
			try {
				widthText = fLineWidthText.getText();
				int formattingLineWidth = Integer.parseInt(widthText);
				if ((formattingLineWidth < WIDTH_VALIDATION_LOWER_LIMIT) || (formattingLineWidth > WIDTH_VALIDATION_UPPER_LIMIT)) {
					throw new NumberFormatException();
				}
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
}
