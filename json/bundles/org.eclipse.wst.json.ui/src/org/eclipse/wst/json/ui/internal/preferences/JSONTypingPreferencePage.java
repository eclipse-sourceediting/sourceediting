/*******************************************************************************
 * Copyright (c) 2008, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wst.json.ui.internal.JSONUIMessages;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;
 
public class JSONTypingPreferencePage extends AbstractPreferencePage {

	private Button fCloseBraces;
	private Button fCloseBrackets;
	private Button fCloseStrings;

	protected Control createContents(Composite parent) {
		Composite composite = super.createComposite(parent, 1);

		createAutoComplete(composite);
		
		setSize(composite);
		loadPreferences();
		
		return composite;
	}
	
	private void createAutoComplete(Composite parent) {
		Group group = createGroup(parent, 1);
		
		group.setText(JSONUIMessages.Automatically_close);

		fCloseBraces = createCheckBox(group, JSONUIMessages.Close_braces);
		fCloseBrackets = createCheckBox(group, JSONUIMessages.Close_brackets);
		fCloseStrings = createCheckBox(group, JSONUIMessages.Close_strings);
	}
	
	public boolean performOk() {
		boolean result = super.performOk();
		
		JSONUIPlugin.getDefault().savePluginPreferences();
		
		return result;
	}
	
	protected void initializeValues() {
		initCheckbox(fCloseBraces, JSONUIPreferenceNames.TYPING_CLOSE_BRACES);
		initCheckbox(fCloseBrackets, JSONUIPreferenceNames.TYPING_CLOSE_BRACKETS);
		initCheckbox(fCloseStrings, JSONUIPreferenceNames.TYPING_CLOSE_STRINGS);
	}
	
	protected void performDefaults() {
		defaultCheckbox(fCloseBraces, JSONUIPreferenceNames.TYPING_CLOSE_BRACES);
		defaultCheckbox(fCloseBrackets, JSONUIPreferenceNames.TYPING_CLOSE_BRACKETS);
		defaultCheckbox(fCloseStrings, JSONUIPreferenceNames.TYPING_CLOSE_STRINGS);
	}
	
	protected void storeValues() {
		getPreferenceStore().setValue(JSONUIPreferenceNames.TYPING_CLOSE_BRACES, (fCloseBraces != null) ? fCloseBraces.getSelection() : false);
		getPreferenceStore().setValue(JSONUIPreferenceNames.TYPING_CLOSE_BRACKETS, (fCloseBrackets != null) ? fCloseBrackets.getSelection() : false);
		getPreferenceStore().setValue(JSONUIPreferenceNames.TYPING_CLOSE_STRINGS, (fCloseStrings != null) ? fCloseStrings.getSelection() : false);
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return JSONUIPlugin.getDefault().getPreferenceStore();
	}
}
