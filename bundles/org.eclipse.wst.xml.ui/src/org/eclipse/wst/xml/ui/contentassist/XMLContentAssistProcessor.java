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
package org.eclipse.wst.xml.ui.contentassist;



import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;

public class XMLContentAssistProcessor extends AbstractContentAssistProcessor implements IPropertyChangeListener {

	protected IPreferenceStore fPreferenceStore = null;
	protected IResource fResource = null;
	protected AbstractTemplateCompletionProcessor fTemplateProcessor = null;

	public XMLContentAssistProcessor() {
		super();
	}

	/* (non-Javadoc)
	 */
	protected AbstractTemplateCompletionProcessor getTemplateCompletionProcessor() {
		if (fTemplateProcessor == null) {
			fTemplateProcessor = new XMLTemplateCompletionProcessor();
		}
		return fTemplateProcessor;
	}
	
	protected void init() {
		getPreferenceStore().addPropertyChangeListener(this);
		reinit();
	}

	protected void reinit() {
		String key = PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.AUTO_PROPOSE, IContentTypeIdentifier.ContentTypeID_SSEXML);
		boolean doAuto = getPreferenceStore().getBoolean(key);
		if (doAuto) {
			key = PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, IContentTypeIdentifier.ContentTypeID_SSEXML);
			completionProposalAutoActivationCharacters = getPreferenceStore().getString(key).toCharArray();
		}
		else {
			completionProposalAutoActivationCharacters = null;
		}
	}

	public void release() {
		super.release();
		getPreferenceStore().removePropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();

		if (property.compareTo(CommonEditorPreferenceNames.AUTO_PROPOSE) == 0 || property.compareTo(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE) == 0) {
			reinit();
		}
	}

	protected IPreferenceStore getPreferenceStore() {
		if (fPreferenceStore == null)
			fPreferenceStore = EditorPlugin.getDefault().getPreferenceStore();
		//fPreferenceStore = CommonPreferencesPlugin.getDefault().getPreferenceStore(ContentType.ContentTypeID_XML);

		return fPreferenceStore;
	}
}
