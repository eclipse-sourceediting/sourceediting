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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.jsdt.ui.PreferenceConstants;
import org.eclipse.wst.sse.ui.contentassist.AutoActivationDelegate;

/**
 * <p>Used to provide auto activation characters to the content assist framework for JS regions in web pages
 * based on the JS auto activation character preferences.</p>
 *
 */
public class JSDTAutoActivationDelegate extends AutoActivationDelegate implements IPropertyChangeListener {

	/** auto activation characters */
	private char[] fCompletionPropoaslAutoActivationCharacters;
	
	/**
	 * <p>Default constructor needed because this class is instantiated by extension point.</p>
	 */
	public JSDTAutoActivationDelegate() {
		getPreferenceStore().addPropertyChangeListener(this);
		
		//get the current user preference
		getAutoActivationCharacterPreferences();
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.contentassist.AutoActivationDelegate#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return this.fCompletionPropoaslAutoActivationCharacters;
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.contentassist.AutoActivationDelegate#dispose()
	 */
	public void dispose() {
		getPreferenceStore().removePropertyChangeListener(this);
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
	 * @return the associated preference store
	 */
	private IPreferenceStore getPreferenceStore() {
		return PreferenceConstants.getPreferenceStore();
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
