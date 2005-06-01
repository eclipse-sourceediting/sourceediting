package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;

public class JSPELCompletionProcessor extends JSPCompletionProcessor {

	public JSPELCompletionProcessor(IResource resource) {
		super(resource);
	}

	protected JSPProposalCollector getProposalCollector(ICompilationUnit cu, JSPTranslation translation) {
		return new JSPELProposalCollector(cu, translation);
	}
}
