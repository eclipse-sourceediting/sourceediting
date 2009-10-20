/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.preferences.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.preferences.JSPUIPreferenceNames;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;

public class JSPTypingPreferencePage extends AbstractPreferencePage {

	private Button fCloseBraces;
	private Button fCloseScriptlets;
	private Button fCloseComments;
	
	protected Control createContents(Composite parent) {
		Composite composite = super.createComposite(parent, 1);
		
		createAutoComplete(composite);
		
		setSize(composite);
		loadPreferences();
		
		return composite;
	}
	
	private void createAutoComplete(Composite parent) {
		Group group = createGroup(parent, 2);
		
		group.setText(JSPUIMessages.JSPTyping_Auto_Complete);
		
		fCloseBraces = createCheckBox(group, JSPUIMessages.JSPTyping_Complete_Braces);
		((GridData) fCloseBraces.getLayoutData()).horizontalSpan = 2;
		
		fCloseComments = createCheckBox(group, JSPUIMessages.JSPTyping_Complete_Comments);
		((GridData) fCloseComments.getLayoutData()).horizontalSpan = 2;
		
		fCloseScriptlets = createCheckBox(group, JSPUIMessages.JSPTyping_Complete_Scriptlets);
		((GridData) fCloseScriptlets.getLayoutData()).horizontalSpan = 2;
	}
	
	public boolean performOk() {
		boolean result = super.performOk();
		
		JSPUIPlugin.getDefault().savePluginPreferences();
		
		return result;
	}
	
	protected void initializeValues() {
		initCheckbox(fCloseBraces, JSPUIPreferenceNames.TYPING_COMPLETE_EL_BRACES);
		initCheckbox(fCloseScriptlets, JSPUIPreferenceNames.TYPING_COMPLETE_SCRIPTLETS);
		initCheckbox(fCloseComments, JSPUIPreferenceNames.TYPING_COMPLETE_COMMENTS);
	}
	
	protected void performDefaults() {
		defaultCheckbox(fCloseBraces, JSPUIPreferenceNames.TYPING_COMPLETE_EL_BRACES);
		defaultCheckbox(fCloseScriptlets, JSPUIPreferenceNames.TYPING_COMPLETE_SCRIPTLETS);
		defaultCheckbox(fCloseComments, JSPUIPreferenceNames.TYPING_COMPLETE_COMMENTS);
	}
	
	protected void storeValues() {
		getPreferenceStore().setValue(JSPUIPreferenceNames.TYPING_COMPLETE_EL_BRACES, (fCloseBraces != null) ? fCloseBraces.getSelection() : false);
		getPreferenceStore().setValue(JSPUIPreferenceNames.TYPING_COMPLETE_SCRIPTLETS, (fCloseScriptlets != null) ? fCloseScriptlets.getSelection() : false);
		getPreferenceStore().setValue(JSPUIPreferenceNames.TYPING_COMPLETE_COMMENTS, (fCloseComments != null) ? fCloseComments.getSelection() : false);
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}

}
