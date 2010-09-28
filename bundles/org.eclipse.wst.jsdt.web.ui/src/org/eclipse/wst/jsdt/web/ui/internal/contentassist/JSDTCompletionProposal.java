/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.jsdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.wst.jsdt.internal.ui.text.java.LazyJavaCompletionProposal;
import org.eclipse.wst.jsdt.internal.ui.text.java.LazyJavaTypeCompletionProposal;
import org.eclipse.wst.jsdt.ui.text.java.IJavaCompletionProposal;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JSDTCompletionProposal extends JavaCompletionProposal implements IJavaCompletionProposal {
	/*
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=124483
	 * 
	 * This is a wrapped proposal so we don't need to make "slow" calls to the
	 * java proposal up front, only when needed for example, getAdditionalInfo()
	 * reads external javadoc, and it makes no sense
	 */
	ICompletionProposal fJavaCompletionProposal = null;
	
	public JSDTCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image,
			String displayString, IContextInformation contextInformation, String additionalProposalInfo, int relevance,
			boolean updateReplacementLengthOnValidate) {
		super(replacementString, replacementOffset, replacementLength, image, displayString, relevance);
		super.setCursorPosition(cursorPosition);
		super.setContextInformation(contextInformation);
// super(replacementString, replacementOffset, replacementLength,
// cursorPosition, image, displayString, contextInformation,
// additionalProposalInfo, relevance,
// updateReplacementLengthOnValidate);
	}
	
	/**
	 * <p>Uses the wrapped proposal if its lazy</p>
	 */
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		if (this.fJavaCompletionProposal instanceof LazyJavaCompletionProposal) {
			((LazyJavaCompletionProposal) this.fJavaCompletionProposal).apply(viewer, trigger, stateMask, offset);
		}
		else {
			super.apply(viewer, trigger, stateMask, offset);
		}
	}
	
	/**
	 * <p>Uses the wrapped proposal if its lazy</p>
	 *
	 * @return the selection after the proposal has been applied
	 */
	public Point getSelection(IDocument document) {
		Point selection;
		if (this.fJavaCompletionProposal instanceof LazyJavaCompletionProposal) {
			selection = this.fJavaCompletionProposal.getSelection(document);
		}
		else {
			selection = super.getSelection(document);
		}
		return selection;
	}
	
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		Object additionalInfo = super.getAdditionalProposalInfo(monitor);
		ICompletionProposal javaProposal = getJavaCompletionProposal();
		if ((javaProposal != null) && (javaProposal instanceof ICompletionProposalExtension5)) {
			additionalInfo = ((ICompletionProposalExtension5) javaProposal).getAdditionalProposalInfo(monitor);
		}
		return additionalInfo;
	}
	
	public String getAdditionalProposalInfo() {
		String additionalInfo = super.getAdditionalProposalInfo();
		ICompletionProposal javaProposal = getJavaCompletionProposal();
		if (javaProposal != null) {
			additionalInfo = javaProposal.getAdditionalProposalInfo();
		}
		return additionalInfo;
	}
	
	final public ICompletionProposal getJavaCompletionProposal() {
		return fJavaCompletionProposal;
	}
	
	final public void setJavaCompletionProposal(ICompletionProposal javaCompletionProposal) {
		fJavaCompletionProposal = javaCompletionProposal;
	}
	
	protected boolean isValidPrefix(String prefix) {
		if (this.fJavaCompletionProposal instanceof LazyJavaTypeCompletionProposal) {
			return ((LazyJavaTypeCompletionProposal) this.fJavaCompletionProposal).isValidTypePrefix(prefix);
		}
		return super.isValidPrefix(prefix);
	}
}
