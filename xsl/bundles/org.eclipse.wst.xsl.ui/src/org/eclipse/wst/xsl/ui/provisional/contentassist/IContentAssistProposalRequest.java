package org.eclipse.wst.xsl.ui.provisional.contentassist;

import java.util.ArrayList;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * Provides content assistance ICompletionProposals.
 * 
 * @author David Carver
 * @since 1.1
 */
public interface IContentAssistProposalRequest {

	/**
	 * Completion Proposals for a Content Assist Request.
	 * @return ArrayLlist<ICompletionProposal>
	 */
	public ArrayList<ICompletionProposal> getCompletionProposals();
}