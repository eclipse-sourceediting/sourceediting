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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.xml.core.XMLModelPlugin;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;


public class XMLFilesPreferencePage extends AbstractPreferencePage {

	protected Combo fEndOfLineCode = null;
	protected EncodingSettings fEncodingSettings = null;

	protected IPreferenceStore doGetPreferenceStore() {
		return EditorPlugin.getDefault().getPreferenceStore();
	}

	/* (non-Javadoc)
	 */
	protected Preferences getModelPreferences() {
		return XMLModelPlugin.getDefault().getPluginPreferences();
	}

	protected void doSavePreferenceStore() {
		XMLModelPlugin.getDefault().savePluginPreferences(); // model
	}

	protected Control createContents(Composite parent) {
		Composite composite = (Composite) super.createContents(parent);
		WorkbenchHelp.setHelp(composite, IHelpContextIds.XML_PREFWEBX_FILES_HELPID);
		createContentsForCreatingOrSavingGroup(composite);
		createContentsForCreatingGroup(composite);

		setSize(composite);
		loadPreferences();

		return composite;
	}

	protected void createContentsForCreatingOrSavingGroup(Composite parent) {
		Group creatingOrSavingGroup = createGroup(parent, 2);
		creatingOrSavingGroup.setText(ResourceHandler.getString("Creating_or_saving_files")); //$NON-NLS-1$

		Label label = createLabel(creatingOrSavingGroup, ResourceHandler.getString("End-of-line_code_desc")); //$NON-NLS-1$
		((GridData)label.getLayoutData()).horizontalSpan = 2;
		((GridData)label.getLayoutData()).grabExcessHorizontalSpace = true;

		createLabel(creatingOrSavingGroup, ResourceHandler.getString("End-of-line_code")); //$NON-NLS-1$
		fEndOfLineCode = createDropDownBox(creatingOrSavingGroup);
		fEndOfLineCode.add(CommonModelPreferenceNames.LF);
		fEndOfLineCode.add(CommonModelPreferenceNames.CR);
		fEndOfLineCode.add(CommonModelPreferenceNames.CRLF);
		fEndOfLineCode.add(CommonModelPreferenceNames.NO_TRANSLATION);
	}

	protected void createContentsForCreatingGroup(Composite parent) {
		Group creatingGroup = createGroup(parent, 1);
		creatingGroup.setText(ResourceHandler.getString("Creating_files")); //$NON-NLS-1$

		Label label = createLabel(creatingGroup, ResourceHandler.getString("Encoding_desc")); //$NON-NLS-1$

		fEncodingSettings = new EncodingSettings(creatingGroup, ResourceHandler.getString("Encoding")); //$NON-NLS-1$
	}

	protected void performDefaults() {
		performDefaultsForCreatingOrSavingGroup();
		performDefaultsForCreatingGroup();

		super.performDefaults();
	}

	protected void performDefaultsForCreatingOrSavingGroup() {
		String endOfLineCode = getModelPreferences().getDefaultString(CommonModelPreferenceNames.END_OF_LINE_CODE);

		if (endOfLineCode.length() > 0)
			fEndOfLineCode.setText(endOfLineCode);
		else
			fEndOfLineCode.setText(CommonModelPreferenceNames.NO_TRANSLATION);
	}

	protected void performDefaultsForCreatingGroup() {
		fEncodingSettings.resetToDefaultEncoding();
	}

	protected void initializeValues() {
		initializeValuesForCreatingOrSavingGroup();
		initializeValuesForCreatingGroup();
	}

	protected void initializeValuesForCreatingOrSavingGroup() {
		String endOfLineCode = getModelPreferences().getString(CommonModelPreferenceNames.END_OF_LINE_CODE);

		if (endOfLineCode.length() > 0)
			fEndOfLineCode.setText(endOfLineCode);
		else
			fEndOfLineCode.setText(CommonModelPreferenceNames.NO_TRANSLATION);
	}

	protected void initializeValuesForCreatingGroup() {
		String encoding = getModelPreferences().getString(CommonModelPreferenceNames.OUTPUT_CODESET);

		fEncodingSettings.setIANATag(encoding);
	}

	protected void storeValues() {
		storeValuesForCreatingOrSavingGroup();
		storeValuesForCreatingGroup();
	}

	protected void storeValuesForCreatingOrSavingGroup() {
		String endOfLineCode = fEndOfLineCode.getText();

		if (endOfLineCode.compareTo(CommonModelPreferenceNames.NO_TRANSLATION) == 0)
			getModelPreferences().setValue(CommonModelPreferenceNames.END_OF_LINE_CODE, ""); //$NON-NLS-1$
		else
			getModelPreferences().setValue(CommonModelPreferenceNames.END_OF_LINE_CODE, endOfLineCode);
	}

	protected void storeValuesForCreatingGroup() {
		getModelPreferences().setValue(CommonModelPreferenceNames.OUTPUT_CODESET, fEncodingSettings.getIANATag());
	}

	public boolean performOk() {
		boolean result = super.performOk();

		doSavePreferenceStore();

		return result;
	}
}
