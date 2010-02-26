package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;

/**
 * @deprecated This class is no longer used locally and will be removed in the future
 */
public class JSPELCompletionProcessor extends JSPCompletionProcessor {
	protected JSPProposalCollector getProposalCollector(ICompilationUnit cu, JSPTranslation translation) {
		return new JSPELProposalCollector(cu, translation);
	}
	
	/**
	 * The java position offset needs to be shifted 3 for the "get" in the java
	 * proposal mapped to a given JSP EL proposal
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int pos) {
		//3 for the "get" at the beginning of the java proposal
		return computeCompletionProposals(viewer, pos, 3);
	}
}
