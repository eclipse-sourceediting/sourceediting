/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.contentassist;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.jsdt.ui.PreferenceConstants;
import org.eclipse.wst.sse.ui.contentassist.StructuredContentAssistProcessor;

/**
 * <p>Content assist processor for Javascript regions so that autoactivation will work in those regions.</p>
 * 
 * <p><b>NOTE:</b> This class does not check that the given partition type is a javascript region, it just
 * assumes it is, so the instantiator of this class must be sure they want the given partition type to be
 * treated as if it was Javascript.</p>
 */
public class JSDTStructuredContentAssistProcessor extends StructuredContentAssistProcessor {
	/** auto activation characters */
	private char[] fCompletionPropoaslAutoActivationCharacters;

	/**
	 * @param assistant {@link ContentAssistant} to use
	 * @param partitionTypeID the Javascript partition type this processor is for
	 * @param viewer {@link ITextViewer} this processor is acting in
	 */
	public JSDTStructuredContentAssistProcessor(ContentAssistant assistant,
			String partitionTypeID, ITextViewer viewer) {
		super(assistant, partitionTypeID, viewer, PreferenceConstants.getPreferenceStore());
		
		//get the current user preference
		getAutoActivationCharacterPreferences();
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.contentassist.StructuredContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return this.fCompletionPropoaslAutoActivationCharacters;
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.contentassist.StructuredContentAssistProcessor#propertyChange(
	 * 	org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if(property.equals(PreferenceConstants.CODEASSIST_AUTOACTIVATION) ||
				property.equals(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_JAVA)) {
			
			getAutoActivationCharacterPreferences();
		}
	}
	
	/**
	 * <p>Gets the auto activation character user preferences for Javascript and stores them for later use</p>
	 */
	private void getAutoActivationCharacterPreferences() {
		IPreferenceStore store = getPreferenceStore();
		
		boolean doAuto = store.getBoolean(PreferenceConstants.CODEASSIST_AUTOACTIVATION);
		if (doAuto) {
			fCompletionPropoaslAutoActivationCharacters =
				store.getString(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_JAVA).toCharArray();
		} else {
			fCompletionPropoaslAutoActivationCharacters = null;
		}
	}
}