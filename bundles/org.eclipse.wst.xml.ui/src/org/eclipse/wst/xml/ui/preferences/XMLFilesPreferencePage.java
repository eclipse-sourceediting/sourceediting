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



import java.util.Vector;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.common.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.xml.core.XMLModelPlugin;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;


public class XMLFilesPreferencePage extends AbstractPreferencePage {
	protected EncodingSettings fEncodingSettings = null;

	protected Combo fEndOfLineCode = null;
	private Vector fEOLCodes = null;

	protected Control createContents(Composite parent) {
		Composite composite = (Composite) super.createContents(parent);
		WorkbenchHelp.setHelp(composite, IHelpContextIds.XML_PREFWEBX_FILES_HELPID);
		createContentsForCreatingOrSavingGroup(composite);
		createContentsForCreatingGroup(composite);

		setSize(composite);
		loadPreferences();

		return composite;
	}

	protected void createContentsForCreatingGroup(Composite parent) {
		Group creatingGroup = createGroup(parent, 1);
		creatingGroup.setText(ResourceHandler.getString("Creating_files")); //$NON-NLS-1$

		Label label = createLabel(creatingGroup, ResourceHandler.getString("Encoding_desc")); //$NON-NLS-1$

		fEncodingSettings = new EncodingSettings(creatingGroup, ResourceHandler.getString("Encoding")); //$NON-NLS-1$
	}

	protected void createContentsForCreatingOrSavingGroup(Composite parent) {
		Group creatingOrSavingGroup = createGroup(parent, 2);
		creatingOrSavingGroup.setText(ResourceHandler.getString("Creating_or_saving_files")); //$NON-NLS-1$

		Label label = createLabel(creatingOrSavingGroup, ResourceHandler.getString("End-of-line_code_desc")); //$NON-NLS-1$
		((GridData) label.getLayoutData()).horizontalSpan = 2;
		((GridData) label.getLayoutData()).grabExcessHorizontalSpace = true;

		createLabel(creatingOrSavingGroup, ResourceHandler.getString("End-of-line_code")); //$NON-NLS-1$
		fEndOfLineCode = createDropDownBox(creatingOrSavingGroup);
		populateLineDelimiters();
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return EditorPlugin.getDefault().getPreferenceStore();
	}

	protected void doSavePreferenceStore() {
		XMLModelPlugin.getDefault().savePluginPreferences(); // model
	}

	/**
	 * Return the currently selected line delimiter preference
	 * 
	 * @return a line delimiter constant from CommonEncodingPreferenceNames
	 */
	private String getCurrentEOLCode() {
		int i = fEndOfLineCode.getSelectionIndex();
		if (i >= 0) {
			return (String) (fEOLCodes.elementAt(i));
		}
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.AbstractPreferencePage#getModelPreferences()
	 */
	protected Preferences getModelPreferences() {
		return XMLModelPlugin.getDefault().getPluginPreferences();
	}

	protected void initializeValues() {
		initializeValuesForCreatingOrSavingGroup();
		initializeValuesForCreatingGroup();
	}

	protected void initializeValuesForCreatingGroup() {
		String encoding = getModelPreferences().getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);

		fEncodingSettings.setIANATag(encoding);
	}

	protected void initializeValuesForCreatingOrSavingGroup() {
		String endOfLineCode = getModelPreferences().getString(CommonEncodingPreferenceNames.END_OF_LINE_CODE);

		if (endOfLineCode.length() > 0)
			setCurrentEOLCode(endOfLineCode);
		else
			setCurrentEOLCode(CommonEncodingPreferenceNames.NO_TRANSLATION);
	}

	protected void performDefaults() {
		performDefaultsForCreatingOrSavingGroup();
		performDefaultsForCreatingGroup();

		super.performDefaults();
	}

	protected void performDefaultsForCreatingGroup() {
		String encoding = getModelPreferences().getDefaultString(CommonEncodingPreferenceNames.OUTPUT_CODESET);

		fEncodingSettings.setIANATag(encoding);
		//		fEncodingSettings.resetToDefaultEncoding();
	}

	protected void performDefaultsForCreatingOrSavingGroup() {
		String endOfLineCode = getModelPreferences().getDefaultString(CommonEncodingPreferenceNames.END_OF_LINE_CODE);

		if (endOfLineCode.length() > 0)
			setCurrentEOLCode(endOfLineCode);
		else
			setCurrentEOLCode(CommonEncodingPreferenceNames.NO_TRANSLATION);
	}

	public boolean performOk() {
		boolean result = super.performOk();

		doSavePreferenceStore();

		return result;
	}

	/**
	 * Populates the vector containing the line delimiter to display string
	 * mapping and the combobox displaying line delimiters
	 */
	private void populateLineDelimiters() {
		fEOLCodes = new Vector();
		fEndOfLineCode.add(ResourceHandler.getString("EOL_Unix")); //$NON-NLS-1$
		fEOLCodes.add(CommonEncodingPreferenceNames.LF);

		fEndOfLineCode.add(ResourceHandler.getString("EOL_Mac")); //$NON-NLS-1$
		fEOLCodes.add(CommonEncodingPreferenceNames.CR);

		fEndOfLineCode.add(ResourceHandler.getString("EOL_Windows")); //$NON-NLS-1$
		fEOLCodes.add(CommonEncodingPreferenceNames.CRLF);

		fEndOfLineCode.add(ResourceHandler.getString("EOL_NoTranslation")); //$NON-NLS-1$
		fEOLCodes.add(CommonEncodingPreferenceNames.NO_TRANSLATION);
	}

	/**
	 * Select the line delimiter in the eol combobox
	 * 
	 */
	private void setCurrentEOLCode(String eolCode) {
		// Clear the current selection.
		fEndOfLineCode.clearSelection();
		fEndOfLineCode.deselectAll();

		int i = fEOLCodes.indexOf(eolCode);
		if (i >= 0) {
			fEndOfLineCode.select(i);
		}
	}

	protected void storeValues() {
		storeValuesForCreatingOrSavingGroup();
		storeValuesForCreatingGroup();
	}

	protected void storeValuesForCreatingGroup() {
		getModelPreferences().setValue(CommonEncodingPreferenceNames.OUTPUT_CODESET, fEncodingSettings.getIANATag());
	}

	protected void storeValuesForCreatingOrSavingGroup() {
		String eolCode = getCurrentEOLCode();
		getModelPreferences().setValue(CommonEncodingPreferenceNames.END_OF_LINE_CODE, eolCode);
	}
}
