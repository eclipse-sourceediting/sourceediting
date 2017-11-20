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

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.jsdt.ui.PreferenceConstants;
import org.eclipse.wst.sse.ui.contentassist.StructuredContentAssistProcessor;

/**
 * @deprecated - use proposal computers instead
 */
public class JSDTStructuredContentAssistProcessor extends StructuredContentAssistProcessor {
	/**
	 * @param assistant {@link ContentAssistant} to use
	 * @param partitionTypeID the Javascript partition type this processor is for
	 * @param viewer {@link ITextViewer} this processor is acting in
	 */
	public JSDTStructuredContentAssistProcessor(ContentAssistant assistant,
			String partitionTypeID, ITextViewer viewer) {
		super(assistant, partitionTypeID, viewer, PreferenceConstants.getPreferenceStore());
		
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.contentassist.StructuredContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return super.getCompletionProposalAutoActivationCharacters() != null ? super.getCompletionProposalAutoActivationCharacters() : new char[0];

	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.contentassist.StructuredContentAssistProcessor#propertyChange(
	 * 	org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {}
}
