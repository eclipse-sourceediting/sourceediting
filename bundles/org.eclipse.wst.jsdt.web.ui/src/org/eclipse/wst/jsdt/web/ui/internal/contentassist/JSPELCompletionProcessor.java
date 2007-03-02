package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;

public class JSPELCompletionProcessor extends JSPCompletionProcessor {
	@Override
	protected JSPProposalCollector getProposalCollector(ICompilationUnit cu,
			JSPTranslation translation) {
		return new JSPELProposalCollector(cu, translation);
	}
}
