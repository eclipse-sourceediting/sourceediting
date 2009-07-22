/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;

/**
 * Implements IJavaCompletionProposal for use with JSPProposalCollector.
 *
 * @plannedfor 1.0
 */
public class JSPCompletionProposal extends CustomCompletionProposal implements IJavaCompletionProposal, ICompletionProposalExtension3, ICompletionProposalExtension5 {

	private static final char[] TRIGGERS = new char[] { '.', '[' };
	/*
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=124483
	 * 
	 * This is a wrapped proposal so we don't need to 
	 * make "slow" calls to the java proposal up front, only when needed
	 * for example, getAdditionalInfo() reads external javadoc, and it makes
	 * no sense
	 */ 
	ICompletionProposal fJavaCompletionProposal = null;
	
	public JSPCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo, int relevance, boolean updateReplacementLengthOnValidate) {
		super(replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString, contextInformation, additionalProposalInfo, relevance, updateReplacementLengthOnValidate);
	}
	
	/**
	 * Sets cursor position after applying.
	 */
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		if (trigger != (char) 0 ) {
			setReplacementString(getReplacementString() + trigger);
			setCursorPosition(getCursorPosition() + 1);
		}
		super.apply(viewer, trigger, stateMask, offset);
		//move the caret to the end of the change
		int endOffsetOfChanges = getReplacementString().length() + getReplacementOffset();
		viewer.getTextWidget().setCaretOffset(endOffsetOfChanges);
	}

	final public ICompletionProposal getJavaCompletionProposal() {
		return fJavaCompletionProposal;
	}

	final public void setJavaCompletionProposal(ICompletionProposal javaCompletionProposal) {
		fJavaCompletionProposal = javaCompletionProposal;
	}
	
	public String getAdditionalProposalInfo() {
		String additionalInfo = super.getAdditionalProposalInfo();
		ICompletionProposal javaProposal = getJavaCompletionProposal();
		if(javaProposal != null)
			additionalInfo = javaProposal.getAdditionalProposalInfo();
		
		return additionalInfo;
	}

	/**
	 * use the java proposals image if there is one for this proposals image
	 */                                                                     
	public Image getImage() {                                               
		if(this.fJavaCompletionProposal != null) {                          
			return this.fJavaCompletionProposal.getImage();                 
		} else {                                                            
			return super.getImage();                                        
		}                                                                   
	}                                                                       

	/* 
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension5#getAdditionalProposalInfo(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		Object additionalInfo = super.getAdditionalProposalInfo();
		ICompletionProposal javaProposal = getJavaCompletionProposal();
		if (javaProposal != null) {
			if (javaProposal instanceof ICompletionProposalExtension5)
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=260951
				additionalInfo = ((ICompletionProposalExtension5) javaProposal).getAdditionalProposalInfo(monitor);
			else
				additionalInfo = javaProposal.getAdditionalProposalInfo();
		}

		return additionalInfo;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getInformationControlCreator()
	 */
	public IInformationControlCreator getInformationControlCreator() {
		// [277530] Use the java proposal's information control creator
		ICompletionProposal javaProposal = getJavaCompletionProposal();
		IInformationControlCreator informationControlCreator = null;

		if (javaProposal instanceof ICompletionProposalExtension3)
			informationControlCreator = ((ICompletionProposalExtension3) javaProposal).getInformationControlCreator();

		return informationControlCreator;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getPrefixCompletionStart(org.eclipse.jface.text.IDocument, int)
	 */
	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		ICompletionProposal javaProposal = getJavaCompletionProposal();
		if (javaProposal instanceof ICompletionProposalExtension3)
			return ((ICompletionProposalExtension3) javaProposal).getPrefixCompletionStart(document, completionOffset);

		return getReplacementOffset();
	}

	public char[] getTriggerCharacters() {
		return TRIGGERS;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getPrefixCompletionText(org.eclipse.jface.text.IDocument, int)
	 */
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		ICompletionProposal javaProposal = getJavaCompletionProposal();
		if (javaProposal instanceof ICompletionProposalExtension3)
			return ((ICompletionProposalExtension3) javaProposal).getPrefixCompletionText(document, completionOffset);

		return getReplacementString();
	}
}
