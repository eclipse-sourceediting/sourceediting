package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;

/**
 * Implements IJavaCompletionProposal for use with JSPProposalCollector.
 *
 * @plannedfor 1.0
 */
public class JSPCompletionProposal extends CustomCompletionProposal implements IJavaCompletionProposal  {

	public JSPCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo, int relevance, boolean updateReplacementLengthOnValidate) {
		super(replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString, contextInformation, additionalProposalInfo, relevance, updateReplacementLengthOnValidate);
	}
	
	/**
	 * Sets cursor position after applying.
	 */
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		super.apply(viewer, trigger, stateMask, offset);
		viewer.setSelectedRange(getCursorPosition(), 0);
	}
}
