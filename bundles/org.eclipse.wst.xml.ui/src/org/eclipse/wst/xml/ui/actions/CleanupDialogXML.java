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
package org.eclipse.wst.xml.ui.actions;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.common.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.xml.core.XMLModelPlugin;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;


public class CleanupDialogXML extends Dialog implements SelectionListener {
	protected Button fCheckBoxCompressEmptyElementTags;
	protected Button fCheckBoxConvertEOLCodes;
	protected Button fCheckBoxFormatSource;
	protected Button fCheckBoxInsertMissingTags;
	protected Button fCheckBoxInsertRequiredAttrs;
	protected Button fCheckBoxQuoteAttrValues;
	protected IStructuredModel fModel = null;
	protected Preferences fPreferences = null;
	protected Button fRadioButtonAttrNameCaseAsis;
	protected Button fRadioButtonAttrNameCaseLower;
	protected Button fRadioButtonAttrNameCaseUpper;
	protected Button fRadioButtonConvertEOLMac;
	protected Button fRadioButtonConvertEOLUnix;
	protected Button fRadioButtonConvertEOLWindows;

	protected Button fRadioButtonTagNameCaseAsis;
	protected Button fRadioButtonTagNameCaseLower;
	protected Button fRadioButtonTagNameCaseUpper;

	public CleanupDialogXML(Shell shell) {

		super(shell);
	}

	public Control createDialogArea(Composite parent) {

		getShell().setText(ResourceHandler.getString("Cleanup_UI_")); //$NON-NLS-1$ = "Cleanup"
		Composite composite = new Composite(parent, SWT.NULL);
		createDialogAreaInComposite(composite);
		initializeOptions();
		return composite;
	}

	protected void createDialogAreaInComposite(Composite composite) {

		WorkbenchHelp.setHelp(composite, IHelpContextIds.CLEANUP_XML_HELPID); // use
		// XML
		// specific
		// help

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);

		// Compress empty element tags
		fCheckBoxCompressEmptyElementTags = new Button(composite, SWT.CHECK);
		fCheckBoxCompressEmptyElementTags.setText(ResourceHandler.getString("Compress_empty_element_tags_UI_")); //$NON-NLS-1$
		fCheckBoxCompressEmptyElementTags.addSelectionListener(this);

		// Insert missing required attrs
		fCheckBoxInsertRequiredAttrs = new Button(composite, SWT.CHECK);
		fCheckBoxInsertRequiredAttrs.setText(ResourceHandler.getString("Insert_required_attributes_UI_")); //$NON-NLS-1$
		fCheckBoxInsertRequiredAttrs.addSelectionListener(this);

		// Insert missing begin/end tags
		fCheckBoxInsertMissingTags = new Button(composite, SWT.CHECK);
		fCheckBoxInsertMissingTags.setText(ResourceHandler.getString("Insert_missing_tags_UI_")); //$NON-NLS-1$ = "Insert missing tags"
		fCheckBoxInsertMissingTags.addSelectionListener(this);

		// Quote attribute values
		fCheckBoxQuoteAttrValues = new Button(composite, SWT.CHECK);
		fCheckBoxQuoteAttrValues.setText(ResourceHandler.getString("Quote_attribute_values_UI_")); //$NON-NLS-1$ = "Quote attribute values"
		fCheckBoxQuoteAttrValues.addSelectionListener(this);

		// Format source
		fCheckBoxFormatSource = new Button(composite, SWT.CHECK);
		fCheckBoxFormatSource.setText(ResourceHandler.getString("Format_source_UI_")); //$NON-NLS-1$ = "Format source"
		fCheckBoxFormatSource.addSelectionListener(this);

		// Convert EOL code
		fCheckBoxConvertEOLCodes = new Button(composite, SWT.CHECK);
		fCheckBoxConvertEOLCodes.setText(ResourceHandler.getString("Convert_EOL_codes_UI_")); //$NON-NLS-1$ = "Convert end-of-line codes"
		fCheckBoxConvertEOLCodes.addSelectionListener(this);
		Composite EOLCodes = new Composite(composite, SWT.NULL);
		GridLayout hLayout = new GridLayout();
		hLayout.numColumns = 3;
		EOLCodes.setLayout(hLayout);
		fRadioButtonConvertEOLWindows = new Button(EOLCodes, SWT.RADIO);
		fRadioButtonConvertEOLWindows.setText(ResourceHandler.getString("EOL_Windows_UI")); //$NON-NLS-1$ = "Windows"
		fRadioButtonConvertEOLWindows.addSelectionListener(this);
		fRadioButtonConvertEOLUnix = new Button(EOLCodes, SWT.RADIO);
		fRadioButtonConvertEOLUnix.setText(ResourceHandler.getString("EOL_Unix_UI")); //$NON-NLS-1$ = "Unix"
		fRadioButtonConvertEOLUnix.addSelectionListener(this);
		fRadioButtonConvertEOLMac = new Button(EOLCodes, SWT.RADIO);
		fRadioButtonConvertEOLMac.setText(ResourceHandler.getString("EOL_Mac_UI")); //$NON-NLS-1$ = "Mac"
		fRadioButtonConvertEOLMac.addSelectionListener(this);
	}

	protected void enableEOLCodeRadios(boolean enable) {

		if ((fRadioButtonConvertEOLWindows != null) && (fRadioButtonConvertEOLUnix != null) && (fRadioButtonConvertEOLMac != null)) {
			fRadioButtonConvertEOLWindows.setEnabled(enable);
			fRadioButtonConvertEOLUnix.setEnabled(enable);
			fRadioButtonConvertEOLMac.setEnabled(enable);
			if (!fRadioButtonConvertEOLWindows.getSelection() && !fRadioButtonConvertEOLUnix.getSelection() && !fRadioButtonConvertEOLMac.getSelection())
				fRadioButtonConvertEOLWindows.setSelection(true);
		}
	}

	protected Preferences getModelPreferences() {
		return XMLModelPlugin.getDefault().getPluginPreferences();
	}

	protected void initializeOptions() {

		fCheckBoxCompressEmptyElementTags.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.COMPRESS_EMPTY_ELEMENT_TAGS));
		fCheckBoxInsertRequiredAttrs.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.INSERT_REQUIRED_ATTRS));
		fCheckBoxInsertMissingTags.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.INSERT_MISSING_TAGS));
		fCheckBoxQuoteAttrValues.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.QUOTE_ATTR_VALUES));
		fCheckBoxFormatSource.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.FORMAT_SOURCE));
		fCheckBoxConvertEOLCodes.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.CONVERT_EOL_CODES));
		String EOLCode = getModelPreferences().getString(CommonModelPreferenceNames.CLEANUP_EOL_CODE);
		if (EOLCode.compareTo(CommonEncodingPreferenceNames.LF) == 0)
			fRadioButtonConvertEOLUnix.setSelection(true);
		else if (EOLCode.compareTo(CommonEncodingPreferenceNames.CR) == 0)
			fRadioButtonConvertEOLMac.setSelection(true);
		else
			fRadioButtonConvertEOLWindows.setSelection(true);
		enableEOLCodeRadios(fCheckBoxConvertEOLCodes.getSelection());
	}

	protected void okPressed() {

		storeOptions();
		super.okPressed();
	}

	public void setModel(IStructuredModel model) {

		fModel = model;
	}

	protected void storeOptions() {

		getModelPreferences().setValue(CommonModelPreferenceNames.COMPRESS_EMPTY_ELEMENT_TAGS, fCheckBoxCompressEmptyElementTags.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.INSERT_REQUIRED_ATTRS, fCheckBoxInsertRequiredAttrs.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.INSERT_MISSING_TAGS, fCheckBoxInsertMissingTags.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.QUOTE_ATTR_VALUES, fCheckBoxQuoteAttrValues.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.FORMAT_SOURCE, fCheckBoxFormatSource.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.CONVERT_EOL_CODES, fCheckBoxConvertEOLCodes.getSelection());
		if (fRadioButtonConvertEOLUnix.getSelection()) {
			getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_EOL_CODE, CommonEncodingPreferenceNames.LF);
		} else if (fRadioButtonConvertEOLMac.getSelection()) {
			getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_EOL_CODE, CommonEncodingPreferenceNames.CR);
		} else {
			getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_EOL_CODE, CommonEncodingPreferenceNames.CRLF);
		}
		// explicitly save plugin preferences so values are stored
		XMLModelPlugin.getDefault().savePluginPreferences();
	}

	public void widgetDefaultSelected(SelectionEvent e) {

		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {

		getButton(OK).setEnabled((fRadioButtonTagNameCaseLower != null && (fRadioButtonTagNameCaseLower.getSelection() || fRadioButtonTagNameCaseUpper.getSelection())) || (fRadioButtonAttrNameCaseLower != null && (fRadioButtonAttrNameCaseLower.getSelection() || fRadioButtonAttrNameCaseUpper.getSelection())) || fCheckBoxInsertMissingTags.getSelection() || fCheckBoxQuoteAttrValues.getSelection() || fCheckBoxFormatSource.getSelection() || fCheckBoxConvertEOLCodes.getSelection() || (fRadioButtonConvertEOLUnix != null && (fRadioButtonConvertEOLUnix.getSelection() || fRadioButtonConvertEOLMac.getSelection() || fRadioButtonConvertEOLWindows.getSelection())));
		if (e.widget == fCheckBoxConvertEOLCodes)
			enableEOLCodeRadios(fCheckBoxConvertEOLCodes.getSelection());
	}
}
