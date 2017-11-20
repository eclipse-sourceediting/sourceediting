/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
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
package org.eclipse.wst.dtd.ui.internal.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.dtd.core.internal.DTDCorePlugin;
import org.eclipse.wst.dtd.core.internal.preferences.DTDCorePreferenceNames;
import org.eclipse.wst.dtd.core.internal.provisional.contenttype.ContentTypeIdForDTD;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.eclipse.wst.dtd.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;


public class DTDFilesPreferencePage extends AbstractPreferencePage {
	private Combo fDefaultSuffix = null;
	private List fValidExtensions = null;

	protected Control createContents(final Composite parent) {
		Composite composite = super.createComposite(parent, 1);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.DTD_PREFWEBX_FILES_HELPID);

		Group creatingGroup = createGroup(composite, 2);
		creatingGroup.setText(DTDUIMessages.Creating_files);

		// Default extension for New file Wizard
		createLabel(creatingGroup, DTDUIMessages.DTDFilesPreferencePage_ExtensionLabel);
		fDefaultSuffix = createDropDownBox(creatingGroup);
		String[] validExtensions = (String[]) getValidExtensions().toArray(new String[0]);
		Arrays.sort(validExtensions);
		fDefaultSuffix.setItems(validExtensions);
		fDefaultSuffix.addSelectionListener(this);

		setSize(composite);
		loadPreferences();

		return composite;
	}

	public void dispose() {
		fDefaultSuffix.removeModifyListener(this);
		super.dispose();
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return DTDUIPlugin.getDefault().getPreferenceStore();
	}

	protected void doSavePreferenceStore() {
		DTDCorePlugin.getInstance().savePluginPreferences(); // model
	}

	/**
	 * Get content type associated with this new file wizard
	 * 
	 * @return IContentType
	 */
	private IContentType getContentType() {
		return Platform.getContentTypeManager().getContentType(ContentTypeIdForDTD.ContentTypeID_DTD);
	}

	/**
	 * Get list of valid extensions
	 * 
	 * @return List
	 */
	private List getValidExtensions() {
		if (fValidExtensions == null) {
			IContentType type = getContentType();
			fValidExtensions = new ArrayList(Arrays.asList(type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC)));
		}
		return fValidExtensions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.AbstractPreferencePage#getModelPreferences()
	 */
	protected Preferences getModelPreferences() {
		return DTDCorePlugin.getInstance().getPluginPreferences();
	}

	protected void initializeValues() {
		String suffix = getModelPreferences().getString(DTDCorePreferenceNames.DEFAULT_EXTENSION);
		fDefaultSuffix.setText(suffix);
	}

	protected void performDefaults() {
		String suffix = getModelPreferences().getDefaultString(DTDCorePreferenceNames.DEFAULT_EXTENSION);
		fDefaultSuffix.setText(suffix);

		super.performDefaults();
	}

	public boolean performOk() {
		boolean result = super.performOk();

		doSavePreferenceStore();

		return result;
	}

	protected void storeValues() {
		String suffix = fDefaultSuffix.getText();
		getModelPreferences().setValue(DTDCorePreferenceNames.DEFAULT_EXTENSION, suffix);
	}

	protected void validateValues() {
		boolean isValid = false;
		Iterator i = getValidExtensions().iterator();
		while (i.hasNext() && !isValid) {
			String extension = (String) i.next();
			isValid = extension.equalsIgnoreCase(fDefaultSuffix.getText());
		}

		if (!isValid) {
			setErrorMessage(NLS.bind(DTDUIMessages.DTDFilesPreferencePage_ExtensionError, getValidExtensions().toString()));
			setValid(false);
		}
		else {
			setErrorMessage(null);
			setValid(true);
		}
	}
}
