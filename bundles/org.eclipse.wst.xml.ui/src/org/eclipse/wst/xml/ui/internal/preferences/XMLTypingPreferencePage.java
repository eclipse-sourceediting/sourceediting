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
package org.eclipse.wst.xml.ui.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;
 
public class XMLTypingPreferencePage extends AbstractPreferencePage {

	private Button fCloseComment;
	private Button fCloseEndTag;
	private Button fRemoveEndTag;
	
	protected Control createContents(Composite parent) {
		Composite composite = super.createComposite(parent, 1);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.XML_PREFWEBX_FILES_HELPID);
		
		createAutoComplete(composite);
		createAutoRemove(composite);
		
		setSize(composite);
		loadPreferences();
		
		return composite;
	}
	
	private void createAutoComplete(Composite parent) {
		Group group = createGroup(parent, 2);
		
		group.setText(XMLUIMessages.XMLTyping_Auto_Complete);
		
		fCloseComment = createCheckBox(group, XMLUIMessages.XMLTyping_Complete_Comments);
		((GridData) fCloseComment.getLayoutData()).horizontalSpan = 2;
		
		fCloseEndTag = createCheckBox(group, XMLUIMessages.XMLTyping_Complete_End_Tags);
		((GridData) fCloseEndTag.getLayoutData()).horizontalSpan = 2;
		
	}
	
	private void createAutoRemove(Composite parent) {
		Group group = createGroup(parent, 2);
		
		group.setText(XMLUIMessages.XMLTyping_Auto_Remove);
		
		fRemoveEndTag = createCheckBox(group, XMLUIMessages.XMLTyping_Remove_End_Tags);
		((GridData) fRemoveEndTag.getLayoutData()).horizontalSpan = 2;
	}
	
	public boolean performOk() {
		boolean result = super.performOk();
		
		XMLUIPlugin.getDefault().savePluginPreferences();
		
		return result;
	}
	
	protected void initializeValues() {
		initCheckbox(fCloseComment, XMLUIPreferenceNames.TYPING_COMPLETE_COMMENTS);
		initCheckbox(fCloseEndTag, XMLUIPreferenceNames.TYPING_COMPLETE_END_TAGS);
		initCheckbox(fRemoveEndTag, XMLUIPreferenceNames.TYPING_REMOVE_END_TAGS);
	}
	
	protected void performDefaults() {
		defaultCheckbox(fCloseComment, XMLUIPreferenceNames.TYPING_COMPLETE_COMMENTS);
		defaultCheckbox(fCloseEndTag, XMLUIPreferenceNames.TYPING_COMPLETE_END_TAGS);
		defaultCheckbox(fRemoveEndTag, XMLUIPreferenceNames.TYPING_REMOVE_END_TAGS);
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
		getPreferenceStore().setValue(XMLUIPreferenceNames.TYPING_COMPLETE_COMMENTS, (fCloseComment != null) ? fCloseComment.getSelection() : false);
		getPreferenceStore().setValue(XMLUIPreferenceNames.TYPING_COMPLETE_END_TAGS, (fCloseEndTag != null) ? fCloseEndTag.getSelection() : false);
		getPreferenceStore().setValue(XMLUIPreferenceNames.TYPING_REMOVE_END_TAGS, (fRemoveEndTag != null) ? fRemoveEndTag.getSelection() : false);
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return XMLUIPlugin.getDefault().getPreferenceStore();
	}
}
