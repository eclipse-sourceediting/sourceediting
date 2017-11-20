package org.eclipse.wst.xsl.ui.provisional.contentassist;

import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

/**
 * Adopters can use the class to help build a custom content completion proposal.  
 * @author dcarver
 * @since 1.1
 *
 */
public class CustomCompletionProposal extends
		org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal
		implements ICompletionProposalExtension4 {

	/**
	 * Constructor for the Custom Completion Proposal
	 * @param replacementString 
	 * @param replacementOffset 
	 * @param replacementLength 
	 * @param cursorPosition
	 * @param image
	 * @param displayString
	 * @param contextInformation
	 * @param additionalProposalInfo
	 * @param relevance
	 */
	public CustomCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation,
			String additionalProposalInfo, int relevance) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, relevance);
	}

	/**
	 * @param replacementString
	 * @param replacementOffset
	 * @param replacementLength
	 * @param cursorPosition
	 * @param image
	 * @param displayString
	 * @param contextInformation
	 * @param additionalProposalInfo
	 * @param relevance
	 * @param updateReplacementLengthOnValidate
	 */
	public CustomCompletionProposal(String replacementString,
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
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension4#isAutoInsertable()
	 */
	public boolean isAutoInsertable() {
		return true;
	}
}
