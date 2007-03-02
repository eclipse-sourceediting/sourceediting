package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import org.eclipse.wst.jsdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;

/**
 * Implements IJavaCompletionProposal for use with JSPProposalCollector.
 * 
 * @plannedfor 1.0
 */
public class JSPCompletionProposal extends CustomCompletionProposal implements
		IJavaCompletionProposal {

	/*
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=124483
	 * 
	 * This is a wrapped proposal so we don't need to make "slow" calls to the
	 * java proposal up front, only when needed for example, getAdditionalInfo()
	 * reads external javadoc, and it makes no sense
	 */
	ICompletionProposal fJavaCompletionProposal = null;

	public JSPCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation,
			String additionalProposalInfo, int relevance,
			boolean updateReplacementLengthOnValidate) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, relevance,
				updateReplacementLengthOnValidate);
	}

	/**
	 * Sets cursor position after applying.
	 */
	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask,
			int offset) {
		super.apply(viewer, trigger, stateMask, offset);
	}

	final public ICompletionProposal getJavaCompletionProposal() {
		return fJavaCompletionProposal;
	}

	final public void setJavaCompletionProposal(
			ICompletionProposal javaCompletionProposal) {
		fJavaCompletionProposal = javaCompletionProposal;
	}

	@Override
	public String getAdditionalProposalInfo() {

		String additionalInfo = super.getAdditionalProposalInfo();
		ICompletionProposal javaProposal = getJavaCompletionProposal();
		if (javaProposal != null) {
			additionalInfo = javaProposal.getAdditionalProposalInfo();
		}

		return additionalInfo;
	}
}
