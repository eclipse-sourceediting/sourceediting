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
package org.eclipse.wst.xml.ui.contentassist;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;

public class XMLContentAssistProcessor extends AbstractContentAssistProcessor implements IPropertyChangeListener {

	protected IPreferenceStore fPreferenceStore = null;
	protected IResource fResource = null;
	protected AbstractTemplateCompletionProcessor fTemplateProcessor = null;

	public XMLContentAssistProcessor() {
		super();
	}

	protected IPreferenceStore getPreferenceStore() {
		if (fPreferenceStore == null)
			fPreferenceStore = XMLUIPlugin.getDefault().getPreferenceStore();
		return fPreferenceStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.ui.contentassist.AbstractContentAssistProcessor#getTemplateCompletionProcessor()
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

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();

		if (property.compareTo(CommonEditorPreferenceNames.AUTO_PROPOSE) == 0 || property.compareTo(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE) == 0) {
			reinit();
		}
	}

	protected void reinit() {
		String key = CommonEditorPreferenceNames.AUTO_PROPOSE;
		boolean doAuto = getPreferenceStore().getBoolean(key);
		if (doAuto) {
			key = CommonEditorPreferenceNames.AUTO_PROPOSE_CODE;
			completionProposalAutoActivationCharacters = getPreferenceStore().getString(key).toCharArray();
		} else {
			completionProposalAutoActivationCharacters = null;
		}
	}

	public void release() {
		super.release();
		getPreferenceStore().removePropertyChangeListener(this);
	}
}
