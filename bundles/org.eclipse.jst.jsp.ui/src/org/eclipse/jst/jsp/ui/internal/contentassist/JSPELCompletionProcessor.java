package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;

public class JSPELCompletionProcessor extends JSPCompletionProcessor {
	protected JSPProposalCollector getProposalCollector(ICompilationUnit cu, JSPTranslation translation) {
		return new JSPELProposalCollector(cu, translation);
	}
}
