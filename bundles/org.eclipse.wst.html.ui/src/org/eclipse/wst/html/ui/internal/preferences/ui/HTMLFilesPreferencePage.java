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
package org.eclipse.wst.html.ui.internal.preferences.ui;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.html.core.HTMLFilesPreferenceNames;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.xml.ui.internal.preferences.EncodingSettings;
import org.eclipse.wst.xml.ui.internal.preferences.WorkbenchDefaultEncodingSettings;
import org.eclipse.wst.xml.ui.internal.preferences.XMLFilesPreferencePage;

public class HTMLFilesPreferencePage extends XMLFilesPreferencePage {
	private WorkbenchDefaultEncodingSettings fInputEncodingSettings = null;
	private Text fHtmlext_Field;
	private Button fDoctype_Button;
	private Button fGenerator_Button;

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.preferences.ui.AbstractPreferencePage#getModelPreferences()
	 */
	protected Preferences getModelPreferences() {
		return HTMLCorePlugin.getDefault().getPluginPreferences();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore() {
		return HTMLUIPlugin.getDefault().getPreferenceStore();
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.preferences.ui.XMLFilesPreferencePage#doSavePreferenceStore()
	 */
	protected void doSavePreferenceStore() {
		HTMLCorePlugin.getDefault().savePluginPreferences(); // model
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite scrolledComposite = createScrolledComposite(parent);
		createContentsForCreatingOrSavingGroup(scrolledComposite);
		createContentsForCreatingGroup(scrolledComposite);
		createContentsForLoadingGroup(scrolledComposite);
		
		WorkbenchHelp.setHelp(scrolledComposite, IHelpContextIds.HTML_PREFWEBX_FILES_HELPID);
		
		setSize(scrolledComposite);
		loadPreferences();
		
		return scrolledComposite;
	}

	protected void createContentsForLoadingGroup(Composite parent) {
		Group group = createGroup(parent, 1);
		group.setText(HTMLUIMessages.HTMLFilesPreferencePage_0);

		fInputEncodingSettings = new WorkbenchDefaultEncodingSettings(group);
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.preferences.ui.AbstractPreferencePage#initializeValues()
	 */
	protected void initializeValues() {
		super.initializeValues();
		initializeValuesForLoadingGroup();
	}
	
	protected void initializeValuesForLoadingGroup() {
		String encoding = getModelPreferences().getString(CommonEncodingPreferenceNames.INPUT_CODESET);
		
		fInputEncodingSettings.setIANATag(encoding);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		super.performDefaults();
		performDefaultsForLoadingGroup();
	}
	
	protected void performDefaultsForLoadingGroup() {
		String encoding = getModelPreferences().getDefaultString(CommonEncodingPreferenceNames.INPUT_CODESET);

		fInputEncodingSettings.setIANATag(encoding);
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.preferences.ui.AbstractPreferencePage#storeValues()
	 */
	protected void storeValues() {
		super.storeValues();
		storeValuesForLoadingGroup();
	}
	
	protected void storeValuesForLoadingGroup() {
		getModelPreferences().setValue(CommonEncodingPreferenceNames.INPUT_CODESET, fInputEncodingSettings.getIANATag());		
	}
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.preferences.ui.XMLFilesPreferencePage#createContentsForCreatingGroup(org.eclipse.swt.widgets.Composite)
	 */
	protected void createContentsForCreatingGroup(Composite parent) {
		Group creatingGroup = createGroup(parent, 2);
		creatingGroup.setText(HTMLUIMessages.Creating_files);

		// Add this suffix..
		createLabel(creatingGroup, HTMLUIMessages.HTMLFilesPreferencePage_1);
		fHtmlext_Field = createTextField(creatingGroup);
		
		// Encoding..
		Label label = createLabel(creatingGroup, HTMLUIMessages.Encoding_desc);
		((GridData)label.getLayoutData()).horizontalSpan = 2;
		fEncodingSettings = new EncodingSettings(creatingGroup);
		((GridData)fEncodingSettings.getLayoutData()).horizontalSpan = 2;
		
		// Insert DOCTYPE declaration
		fDoctype_Button = createCheckBox(creatingGroup, HTMLUIMessages.HTMLFilesPreferencePage_2);
		((GridData)fDoctype_Button.getLayoutData()).horizontalSpan = 2;
		
		// Insert GENERATOR with META tag
		fGenerator_Button = createCheckBox(creatingGroup, HTMLUIMessages.HTMLFilesPreferencePage_3);
		((GridData)fGenerator_Button.getLayoutData()).horizontalSpan = 2;
	}
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.preferences.ui.XMLFilesPreferencePage#initializeValuesForCreatingGroup()
	 */
	protected void initializeValuesForCreatingGroup() {
		super.initializeValuesForCreatingGroup();
		
		String defaultSuffix = getModelPreferences().getString(HTMLFilesPreferenceNames.DEFAULT_SUFFIX);
		if (defaultSuffix.length() > 0) {
			fHtmlext_Field.setText(defaultSuffix);
		}
	
		boolean bCheck;
		bCheck = getModelPreferences().getBoolean(HTMLFilesPreferenceNames.GENERATE_DOCUMENT_TYPE);
		fDoctype_Button.setSelection(bCheck);
		
		bCheck = getModelPreferences().getBoolean(HTMLFilesPreferenceNames.GENERATE_GENERATOR);
		fGenerator_Button.setSelection(bCheck);
	}
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.preferences.ui.XMLFilesPreferencePage#performDefaultsForCreatingGroup()
	 */
	protected void performDefaultsForCreatingGroup() {
		super.performDefaultsForCreatingGroup();

		fHtmlext_Field.setText(getModelPreferences().getDefaultString(HTMLFilesPreferenceNames.DEFAULT_SUFFIX));
		
		fDoctype_Button.setSelection(getModelPreferences().getDefaultBoolean(HTMLFilesPreferenceNames.GENERATE_DOCUMENT_TYPE));
		fGenerator_Button.setSelection(getModelPreferences().getDefaultBoolean(HTMLFilesPreferenceNames.GENERATE_GENERATOR));
	}
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.preferences.ui.XMLFilesPreferencePage#storeValuesForCreatingGroup()
	 */
	protected void storeValuesForCreatingGroup() {
		super.storeValuesForCreatingGroup();
		
		String str_ext = fHtmlext_Field.getText();	
		getModelPreferences().setValue(HTMLFilesPreferenceNames.DEFAULT_SUFFIX, str_ext); 

		getModelPreferences().setValue(HTMLFilesPreferenceNames.GENERATE_DOCUMENT_TYPE, fDoctype_Button.getSelection());
	
		getModelPreferences().setValue(HTMLFilesPreferenceNames.GENERATE_GENERATOR, fGenerator_Button.getSelection());
	}
}