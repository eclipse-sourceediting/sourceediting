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
package org.eclipse.wst.html.ui.internal.preferences.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.preferences.HTMLUIPreferenceNames;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;

public class HTMLTypingPreferencePage extends AbstractPreferencePage {

	private Button fCloseComment;
	private Button fCloseEndTag;
	private Button fRemoveEndTag;
	
	protected Control createContents(Composite parent) {
		Composite composite = super.createComposite(parent, 1);
		
		createAutoComplete(composite);
		createAutoRemove(composite);
		
		setSize(composite);
		loadPreferences();
		
		return composite;
	}
	
	private void createAutoComplete(Composite parent) {
		Group group = createGroup(parent, 2);
		
		group.setText(HTMLUIMessages.HTMLTyping_Auto_Complete);
		
		fCloseComment = createCheckBox(group, HTMLUIMessages.HTMLTyping_Complete_Comments);
		((GridData) fCloseComment.getLayoutData()).horizontalSpan = 2;
		
		fCloseEndTag = createCheckBox(group, HTMLUIMessages.HTMLTyping_Complete_End_Tags);
		((GridData) fCloseEndTag.getLayoutData()).horizontalSpan = 2;
		
	}
	
	private void createAutoRemove(Composite parent) {
		Group group = createGroup(parent, 2);
		
		group.setText(HTMLUIMessages.HTMLTyping_Auto_Remove);
		
		fRemoveEndTag = createCheckBox(group, HTMLUIMessages.HTMLTyping_Remove_End_Tags);
		((GridData) fRemoveEndTag.getLayoutData()).horizontalSpan = 2;
	}
	
	public boolean performOk() {
		boolean result = super.performOk();
		
		HTMLUIPlugin.getDefault().savePluginPreferences();
		
		return result;
	}
	
	protected void initializeValues() {
		initCheckbox(fCloseComment, HTMLUIPreferenceNames.TYPING_COMPLETE_COMMENTS);
		initCheckbox(fCloseEndTag, HTMLUIPreferenceNames.TYPING_COMPLETE_END_TAGS);
		initCheckbox(fRemoveEndTag, HTMLUIPreferenceNames.TYPING_REMOVE_END_TAGS);
	}
	
	protected void performDefaults() {
		defaultCheckbox(fCloseComment, HTMLUIPreferenceNames.TYPING_COMPLETE_COMMENTS);
		defaultCheckbox(fCloseEndTag, HTMLUIPreferenceNames.TYPING_COMPLETE_END_TAGS);
		defaultCheckbox(fRemoveEndTag, HTMLUIPreferenceNames.TYPING_REMOVE_END_TAGS);
	}
	
	private void initCheckbox(Button box, String key) {
		if(box != null && key != null)
			box.setSelection(getPreferenceStore().getBoolean(key));
	}
	
	private void defaultCheckbox(Button box, String key) {
		if(box != null && key != null)
			box.setSelection(getPreferenceStore().getDefaultBoolean(key));
	}
	
	protected void storeValues() {
		getPreferenceStore().setValue(HTMLUIPreferenceNames.TYPING_COMPLETE_COMMENTS, (fCloseComment != null) ? fCloseComment.getSelection() : false);
		getPreferenceStore().setValue(HTMLUIPreferenceNames.TYPING_COMPLETE_END_TAGS, (fCloseEndTag != null) ? fCloseEndTag.getSelection() : false);
		getPreferenceStore().setValue(HTMLUIPreferenceNames.TYPING_REMOVE_END_TAGS, (fRemoveEndTag != null) ? fRemoveEndTag.getSelection() : false);
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return HTMLUIPlugin.getDefault().getPreferenceStore();
	}

}
