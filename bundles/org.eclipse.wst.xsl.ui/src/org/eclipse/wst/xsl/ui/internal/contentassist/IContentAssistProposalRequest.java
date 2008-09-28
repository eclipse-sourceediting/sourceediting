package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * Provides content assistance ICompletionProposals.
 * 
 * @author David Carver
 *
 */
public interface IContentAssistProposalRequest {

	/**
	 * Completion Proposals for a Content Assist Request.
	 * @return
	 */
	public ArrayList<ICompletionProposal> getCompletionProposals();
}