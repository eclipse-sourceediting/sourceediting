/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.preferences.HTMLUIPreferenceNames;
import org.eclipse.wst.sse.ui.contentassist.StructuredContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.contentassist.AttributeContextInformationPresenter;

/**
 * <p>Implementation of {@link StructuredContentAssistProcessor} for JSP documents</p>
 * 
 * <p>Currently this implementation still uses the HTML preferences for auto
 * activation characters, but in the future this should probably change</p>
 */
public class JSPStructuredContentAssistProcessor extends StructuredContentAssistProcessor {

	/** auto activation characters */
	private char[] fCompletionProposalAutoActivationCharacters;
	
	/** property key for determining if auto activation is enabled */
	private String fAutoActivationEnabledPropertyKey;
	
	/** property key for determining what the auto activation characters are */
	private String fAutoActivationCharactersPropertyKey;
	
	/** property key for determining what the auto activation delay is*/
	private String fAutoActivationDelayKey;
	
	/** the context information validator for this processor */
	private IContextInformationValidator fContextInformationValidator;
	
	/**
	 * <p>Constructor</p>
	 * 
	 * @param assistant {@link ContentAssistant} to use
	 * @param partitionTypeID the partition type this processor is for
	 * @param viewer {@link ITextViewer} this processor is acting in
	 */
	public JSPStructuredContentAssistProcessor(ContentAssistant assistant,
			String partitionTypeID, ITextViewer viewer) {
		
		super(assistant, partitionTypeID, viewer, isJavaPartitionType(partitionTypeID) ?
				PreferenceConstants.getPreferenceStore() : HTMLUIPlugin.getDefault().getPreferenceStore());

		//determine which property keys to used based on weather this processor is for Java or HTML syntax
		if(isJavaPartitionType(partitionTypeID)) {
			fAutoActivationEnabledPropertyKey = PreferenceConstants.CODEASSIST_AUTOACTIVATION;
			fAutoActivationCharactersPropertyKey = PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_JAVA;
		} else {
			fAutoActivationEnabledPropertyKey = HTMLUIPreferenceNames.AUTO_PROPOSE;
			fAutoActivationCharactersPropertyKey = HTMLUIPreferenceNames.AUTO_PROPOSE_CODE;
		}
		fAutoActivationDelayKey = HTMLUIPreferenceNames.AUTO_PROPOSE_DELAY;
		
		//get the current user preference
		getAutoActivationCharacterPreferences();
		updateAutoActivationDelay();
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.contentassist.StructuredContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		if (this.fContextInformationValidator == null) {
			this.fContextInformationValidator = new AttributeContextInformationPresenter();
		}
		return this.fContextInformationValidator;
	}
	
	/**
	 * @see org.eclipse.wst.html.ui.internal.contentassist.HTMLStructuredContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return super.getCompletionProposalAutoActivationCharacters() != null ? super.getCompletionProposalAutoActivationCharacters() : this.fCompletionProposalAutoActivationCharacters;
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.contentassist.StructuredContentAssistProcessor#propertyChange(
	 * 	org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if(property.equals(fAutoActivationEnabledPropertyKey) ||
				property.equals(fAutoActivationCharactersPropertyKey)) {			
			getAutoActivationCharacterPreferences();
		}else if (property.equals(fAutoActivationDelayKey)) {
			updateAutoActivationDelay();
		}
	}
	
	/**
	 * <p>Sets the auto activation delay in Content Assist</p>
	 */
	private void updateAutoActivationDelay() {
		IPreferenceStore store = getPreferenceStore();
		boolean doAuto = store.getBoolean(fAutoActivationEnabledPropertyKey);
		if (doAuto) {
			setAutoActivationDelay(store.getInt(fAutoActivationDelayKey));
		} 
		
	}

	/**
	 * <p>Gets the auto activation character user preferences for Java and stores them for later use</p>
	 */
	private void getAutoActivationCharacterPreferences() {
		IPreferenceStore store = getPreferenceStore();
		
		boolean doAuto = store.getBoolean(fAutoActivationEnabledPropertyKey);
		if (doAuto) {
			fCompletionProposalAutoActivationCharacters =
				store.getString(fAutoActivationCharactersPropertyKey).toCharArray();
		} else {
			fCompletionProposalAutoActivationCharacters = null;
		}
	}
	
	/**
	 * @param partitionTypeID check to see if this partition type ID is for a Java partition type
	 * @return <code>true</code> if the given partiton type is a Java partition type,
	 * <code>false</code> otherwise
	 */
	private static boolean isJavaPartitionType(String partitionTypeID) {
		return IJSPPartitions.JSP_CONTENT_JAVA.equals(partitionTypeID) || IJSPPartitions.JSP_DEFAULT_EL.equals(partitionTypeID) || IJSPPartitions.JSP_DEFAULT_EL2.equals(partitionTypeID);
	}
}
