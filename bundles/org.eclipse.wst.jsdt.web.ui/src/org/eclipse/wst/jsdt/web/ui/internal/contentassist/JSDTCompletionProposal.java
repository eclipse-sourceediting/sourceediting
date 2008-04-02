package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.jsdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.wst.jsdt.internal.ui.text.java.LazyJavaCompletionProposal;
import org.eclipse.wst.jsdt.ui.text.java.IJavaCompletionProposal;

/**
 * Implements IJavaCompletionProposal for use with JSPProposalCollector.
 * 
 * @plannedfor 1.0
 */
// public class JSPCompletionProposal extends CustomCompletionProposal
// implements
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
	 * Sets cursor position after applying.
	 */
	
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		if (this.fJavaCompletionProposal instanceof LazyJavaCompletionProposal)
		{
			((LazyJavaCompletionProposal)this.fJavaCompletionProposal).apply(viewer.getDocument(), trigger, offset);
		}
		super.apply(viewer, trigger, stateMask, offset);
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
}
