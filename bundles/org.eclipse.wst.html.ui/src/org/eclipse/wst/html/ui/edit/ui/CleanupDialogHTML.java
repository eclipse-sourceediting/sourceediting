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
package org.eclipse.wst.html.ui.edit.ui;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.html.core.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.xml.core.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.cleanup.XMLCleanupPreferencesImpl;

public class CleanupDialogHTML extends Dialog implements SelectionListener {

	protected Button fRadioButtonTagNameCaseAsis;
	protected Button fRadioButtonTagNameCaseLower;
	protected Button fRadioButtonTagNameCaseUpper;
	protected Button fRadioButtonAttrNameCaseAsis;
	protected Button fRadioButtonAttrNameCaseLower;
	protected Button fRadioButtonAttrNameCaseUpper;
	protected Button fCheckBoxInsertRequiredAttrs;
	protected Button fCheckBoxInsertMissingTags;
	protected Button fCheckBoxQuoteAttrValues;
	protected Button fCheckBoxFormatSource;
	protected Button fCheckBoxConvertEOLCodes;
	protected Button fRadioButtonConvertEOLWindows;
	protected Button fRadioButtonConvertEOLUnix;
	protected Button fRadioButtonConvertEOLMac;
	protected IStructuredModel fModel = null;
	protected Preferences fPreferences = null;

	public CleanupDialogHTML(Shell shell) {
		super(shell);
	}

	public Control createDialogArea(Composite parent) {
		getShell().setText(SSEUIPlugin.getResourceString("%Cleanup_UI_")); //$NON-NLS-1$ = "Cleanup"
		Composite composite = new Composite(parent, SWT.NULL);

		createDialogAreaInComposite(composite);
		initializeOptions();

		return composite;
	}

	protected void createDialogAreaInCompositeForHTML(Composite composite) {
		// Convert tag name case
		// d257064 need to associate group w/ radio buttons so radio buttons header can be read
		Group tagNameCase = new Group(composite, SWT.NONE);
		tagNameCase.setText(SSEUIPlugin.getResourceString("%Tag_name_case_for_HTML_UI_")); //$NON-NLS-1$ = "Tag name case for HTML:"
		GridLayout hLayout = new GridLayout();
		hLayout.numColumns = 3;
		tagNameCase.setLayout(hLayout);
		fRadioButtonTagNameCaseAsis = new Button(tagNameCase, SWT.RADIO);
		fRadioButtonTagNameCaseAsis.setText(SSEUIPlugin.getResourceString("%Tag_name_case_As-is_UI_")); //$NON-NLS-1$ = "&As-is"
		fRadioButtonTagNameCaseAsis.addSelectionListener(this);
		fRadioButtonTagNameCaseLower = new Button(tagNameCase, SWT.RADIO);
		fRadioButtonTagNameCaseLower.setText(SSEUIPlugin.getResourceString("%Tag_name_case_Lower_UI_")); //$NON-NLS-1$ = "&Lower"
		fRadioButtonTagNameCaseLower.addSelectionListener(this);
		fRadioButtonTagNameCaseUpper = new Button(tagNameCase, SWT.RADIO);
		fRadioButtonTagNameCaseUpper.setText(SSEUIPlugin.getResourceString("%Tag_name_case_Upper_UI_")); //$NON-NLS-1$ = "&Upper"
		fRadioButtonTagNameCaseUpper.addSelectionListener(this);

		// Convert attr name case
		// d257064 need to associate group w/ radio buttons so radio buttons header can be read
		Group attrNameCase = new Group(composite, SWT.NONE);
		attrNameCase.setText(SSEUIPlugin.getResourceString("%Attribute_name_case_for_HTML_UI_")); //$NON-NLS-1$ = "Attribute name case for HTML:"
		attrNameCase.setLayout(hLayout);
		fRadioButtonAttrNameCaseAsis = new Button(attrNameCase, SWT.RADIO);
		fRadioButtonAttrNameCaseAsis.setText(SSEUIPlugin.getResourceString("%Attribute_name_case_As-is_UI_")); //$NON-NLS-1$ = "A&s-is"
		fRadioButtonAttrNameCaseAsis.addSelectionListener(this);
		fRadioButtonAttrNameCaseLower = new Button(attrNameCase, SWT.RADIO);
		fRadioButtonAttrNameCaseLower.setText(SSEUIPlugin.getResourceString("%Attribute_name_case_Lower_UI_")); //$NON-NLS-1$ = "L&ower"
		fRadioButtonAttrNameCaseLower.addSelectionListener(this);
		fRadioButtonAttrNameCaseUpper = new Button(attrNameCase, SWT.RADIO);
		fRadioButtonAttrNameCaseUpper.setText(SSEUIPlugin.getResourceString("%Attribute_name_case_Upper_UI_")); //$NON-NLS-1$ = "U&pper"
		fRadioButtonAttrNameCaseUpper.addSelectionListener(this);
	}

	protected void createDialogAreaInComposite(Composite composite) {
		if (isHTMLType()) {
			createDialogAreaInCompositeForHTML(composite);
			WorkbenchHelp.setHelp(composite, IHelpContextIds.CLEANUP_HTML_HELPID); // use HTML specific help
		}
		else
			WorkbenchHelp.setHelp(composite, org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds.CLEANUP_XML_HELPID); // use XML specific help

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);

		// Insert missing required attrs
		fCheckBoxInsertRequiredAttrs = new Button(composite, SWT.CHECK);
		fCheckBoxInsertRequiredAttrs.setText(SSEUIPlugin.getResourceString("%Insert_required_attributes_UI_")); //$NON-NLS-1$
		fCheckBoxInsertRequiredAttrs.addSelectionListener(this);

		// Insert missing begin/end tags
		fCheckBoxInsertMissingTags = new Button(composite, SWT.CHECK);
		fCheckBoxInsertMissingTags.setText(SSEUIPlugin.getResourceString("%Insert_missing_tags_UI_")); //$NON-NLS-1$ = "Insert missing tags"
		fCheckBoxInsertMissingTags.addSelectionListener(this);

		// Quote attribute values
		fCheckBoxQuoteAttrValues = new Button(composite, SWT.CHECK);
		fCheckBoxQuoteAttrValues.setText(SSEUIPlugin.getResourceString("%Quote_attribute_values_UI_")); //$NON-NLS-1$ = "Quote attribute values"
		fCheckBoxQuoteAttrValues.addSelectionListener(this);

		// Format source
		fCheckBoxFormatSource = new Button(composite, SWT.CHECK);
		fCheckBoxFormatSource.setText(SSEUIPlugin.getResourceString("%Format_source_UI_")); //$NON-NLS-1$ = "Format source"
		fCheckBoxFormatSource.addSelectionListener(this);

		// Convert EOL code
		fCheckBoxConvertEOLCodes = new Button(composite, SWT.CHECK);
		fCheckBoxConvertEOLCodes.setText(SSEUIPlugin.getResourceString("%Convert_EOL_codes_UI_")); //$NON-NLS-1$ = "Convert end-of-line codes"
		fCheckBoxConvertEOLCodes.addSelectionListener(this);
		Composite EOLCodes = new Composite(composite, SWT.NULL);
		GridLayout hLayout = new GridLayout();
		hLayout.numColumns = 3;
		EOLCodes.setLayout(hLayout);
		fRadioButtonConvertEOLWindows = new Button(EOLCodes, SWT.RADIO);
		fRadioButtonConvertEOLWindows.setText(SSEUIPlugin.getResourceString("%EOL_Windows_UI")); //$NON-NLS-1$ = "Windows"
		fRadioButtonConvertEOLWindows.addSelectionListener(this);
		fRadioButtonConvertEOLUnix = new Button(EOLCodes, SWT.RADIO);
		fRadioButtonConvertEOLUnix.setText(SSEUIPlugin.getResourceString("%EOL_Unix_UI")); //$NON-NLS-1$ = "Unix"
		fRadioButtonConvertEOLUnix.addSelectionListener(this);
		fRadioButtonConvertEOLMac = new Button(EOLCodes, SWT.RADIO);
		fRadioButtonConvertEOLMac.setText(SSEUIPlugin.getResourceString("%EOL_Mac_UI")); //$NON-NLS-1$ = "Mac"
		fRadioButtonConvertEOLMac.addSelectionListener(this);
	}

	protected void okPressed() {
		storeOptions();

		super.okPressed();
	}

	protected void initializeOptionsForHTML() {
		int tagNameCase = getModelPreferences().getInt(CommonModelPreferenceNames.CLEANUP_TAG_NAME_CASE);
		if (tagNameCase == CommonModelPreferenceNames.UPPER)
			fRadioButtonTagNameCaseUpper.setSelection(true);
		else if (tagNameCase == CommonModelPreferenceNames.LOWER)
			fRadioButtonTagNameCaseLower.setSelection(true);
		else
			fRadioButtonTagNameCaseAsis.setSelection(true);

		int attrNameCase = getModelPreferences().getInt(CommonModelPreferenceNames.CLEANUP_ATTR_NAME_CASE);
		if (attrNameCase == CommonModelPreferenceNames.UPPER)
			fRadioButtonAttrNameCaseUpper.setSelection(true);
		else if (attrNameCase == CommonModelPreferenceNames.LOWER)
			fRadioButtonAttrNameCaseLower.setSelection(true);
		else
			fRadioButtonAttrNameCaseAsis.setSelection(true);
	}

	protected void initializeOptions() {
		if (isHTMLType())
			initializeOptionsForHTML();

		fCheckBoxInsertRequiredAttrs.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.INSERT_REQUIRED_ATTRS));
		fCheckBoxInsertMissingTags.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.INSERT_MISSING_TAGS));
		fCheckBoxQuoteAttrValues.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.QUOTE_ATTR_VALUES));
		fCheckBoxFormatSource.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.FORMAT_SOURCE));
		fCheckBoxConvertEOLCodes.setSelection(getModelPreferences().getBoolean(CommonModelPreferenceNames.CONVERT_EOL_CODES));
		if (fCheckBoxConvertEOLCodes.getSelection()) {
			String EOLCode = getModelPreferences().getString(CommonModelPreferenceNames.CLEANUP_EOL_CODE);
			if (EOLCode == CommonEncodingPreferenceNames.LF)
				fRadioButtonConvertEOLUnix.setSelection(true);
			else if (EOLCode == CommonEncodingPreferenceNames.CR)
				fRadioButtonConvertEOLMac.setSelection(true);
			else
				fRadioButtonConvertEOLWindows.setSelection(true);
		}
		enableEOLCodeRadios(fCheckBoxConvertEOLCodes.getSelection());
	}

	protected void storeOptionsForHTML() {
		if (fRadioButtonTagNameCaseUpper.getSelection())
			getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_TAG_NAME_CASE, CommonModelPreferenceNames.UPPER);
		else if (fRadioButtonTagNameCaseLower.getSelection())
			getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_TAG_NAME_CASE, CommonModelPreferenceNames.LOWER);
		else
			getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_TAG_NAME_CASE, CommonModelPreferenceNames.ASIS);

		if (fRadioButtonAttrNameCaseUpper.getSelection())
			getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_ATTR_NAME_CASE, CommonModelPreferenceNames.UPPER);
		else if (fRadioButtonAttrNameCaseLower.getSelection())
			getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_ATTR_NAME_CASE, CommonModelPreferenceNames.LOWER);
		else
			getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_ATTR_NAME_CASE, CommonModelPreferenceNames.ASIS);

		// explicitly save plugin preferences so values are stored
		HTMLCorePlugin.getDefault().savePluginPreferences();
	}

	protected void storeOptions() {
		if (isHTMLType()) {
			storeOptionsForHTML();
			XMLCleanupPreferencesImpl.getInstance().setTagNameCase(getModelPreferences().getInt(CommonModelPreferenceNames.CLEANUP_TAG_NAME_CASE));
			XMLCleanupPreferencesImpl.getInstance().setAttrNameCase(getModelPreferences().getInt(CommonModelPreferenceNames.CLEANUP_ATTR_NAME_CASE));
		}
		else {
			XMLCleanupPreferencesImpl.getInstance().setTagNameCase(CommonModelPreferenceNames.ASIS);
			XMLCleanupPreferencesImpl.getInstance().setAttrNameCase(CommonModelPreferenceNames.ASIS);
		}

		getModelPreferences().setValue(CommonModelPreferenceNames.INSERT_REQUIRED_ATTRS, fCheckBoxInsertRequiredAttrs.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.INSERT_MISSING_TAGS, fCheckBoxInsertMissingTags.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.QUOTE_ATTR_VALUES, fCheckBoxQuoteAttrValues.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.FORMAT_SOURCE, fCheckBoxFormatSource.getSelection());
		getModelPreferences().setValue(CommonModelPreferenceNames.CONVERT_EOL_CODES, fCheckBoxConvertEOLCodes.getSelection());
		XMLCleanupPreferencesImpl.getInstance().setInsertMissingTags(fCheckBoxInsertMissingTags.getSelection());
		XMLCleanupPreferencesImpl.getInstance().setQuoteAttrValues(fCheckBoxQuoteAttrValues.getSelection());
		XMLCleanupPreferencesImpl.getInstance().setFormatSource(fCheckBoxFormatSource.getSelection());
		XMLCleanupPreferencesImpl.getInstance().setConvertEOLCodes(fCheckBoxConvertEOLCodes.getSelection());
		if (fCheckBoxConvertEOLCodes.getSelection()) {
			if (fRadioButtonConvertEOLUnix.getSelection()) {
				getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_EOL_CODE, CommonEncodingPreferenceNames.LF);
				XMLCleanupPreferencesImpl.getInstance().setEOLCode(CommonEncodingPreferenceNames.LF);
			}
			else if (fRadioButtonConvertEOLMac.getSelection()) {
				getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_EOL_CODE, CommonEncodingPreferenceNames.CR);
				XMLCleanupPreferencesImpl.getInstance().setEOLCode(CommonEncodingPreferenceNames.CR);
			}
			else {
				getModelPreferences().setValue(CommonModelPreferenceNames.CLEANUP_EOL_CODE, CommonEncodingPreferenceNames.CRLF);
				XMLCleanupPreferencesImpl.getInstance().setEOLCode(CommonEncodingPreferenceNames.CRLF);
			}
		}
		
		// explicitly save plugin preferences so values are stored
		HTMLCorePlugin.getDefault().savePluginPreferences();
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		getButton(OK).setEnabled((fRadioButtonTagNameCaseLower != null && (fRadioButtonTagNameCaseLower.getSelection() || fRadioButtonTagNameCaseUpper.getSelection())) || (fRadioButtonAttrNameCaseLower != null && (fRadioButtonAttrNameCaseLower.getSelection() || fRadioButtonAttrNameCaseUpper.getSelection())) || fCheckBoxInsertMissingTags.getSelection() || fCheckBoxQuoteAttrValues.getSelection() || fCheckBoxFormatSource.getSelection() || fCheckBoxConvertEOLCodes.getSelection() || (fRadioButtonConvertEOLUnix != null && (fRadioButtonConvertEOLUnix.getSelection() || fRadioButtonConvertEOLMac.getSelection() || fRadioButtonConvertEOLWindows.getSelection())));
		if (e.widget == fCheckBoxConvertEOLCodes)
			enableEOLCodeRadios(fCheckBoxConvertEOLCodes.getSelection());
	}

	public void setModel(IStructuredModel model) {
		fModel = model;
	}

	protected Preferences getModelPreferences() {
		return HTMLCorePlugin.getDefault().getPluginPreferences();
	}

	protected boolean isXMLType() {
		boolean result = false;

		if (fModel != null) {
			IModelHandler modelHandler = fModel.getModelHandler();
			if (modelHandler.getAssociatedContentTypeId().equals(ContentTypeIdForXML.ContentTypeID_XML))
				result = true;
		}

		return result;
	}

	protected boolean isHTMLType() {
		boolean result = true;

		if (fModel != null) {
			IModelHandler modelHandler = fModel.getModelHandler();
			if (modelHandler.getAssociatedContentTypeId().equals(ContentTypeIdForHTML.ContentTypeID_HTML))
				result = true;
		}

		return result;
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
}