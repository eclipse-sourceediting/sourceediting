package org.eclipse.wst.xsl.ui.internal.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.w3c.dom.Node;

public class NullContentAssistRequest extends AbstractXSLContentAssistRequest {

	public NullContentAssistRequest(Node node, Node parent,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, parent, documentRegion, completionRegion, begin, length,
				filter, textViewer);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ICompletionProposal[] getCompletionProposals() {
		return null;
	}

}
