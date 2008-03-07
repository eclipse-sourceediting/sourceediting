package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.jsdt.internal.ui.text.java.TemplateCompletionProposalComputer;
import org.eclipse.wst.jsdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;

public class JSDTTemplateAssistProcessor {
	TemplateCompletionProposalComputer fJavaTemplateCompletion;
	JSDTProposalCollector fProposalCollector;
	JsTranslationAdapter fTranslationAdapter;
	IProgressMonitor monitor;
	
	public JSDTTemplateAssistProcessor() {
		monitor = new NullProgressMonitor();
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		JavaContentAssistInvocationContext context = getInvocationContext(viewer, offset);
		List props = getTemplateCompletionProposalComputer().computeCompletionProposals(context, monitor);
		return (ICompletionProposal[]) props.toArray(new ICompletionProposal[] {});
	}
	
	private JavaContentAssistInvocationContext getInvocationContext(ITextViewer viewer, int offset) {
		return JSDTContetAssistInvocationContext.getInstance(viewer, offset, getProposalCollector());
	}
	
	protected JSDTProposalCollector getProposalCollector() {
		return fProposalCollector;
		// return new JSPProposalCollector(translation);
	}
	
	private TemplateCompletionProposalComputer getTemplateCompletionProposalComputer() {
		if (fJavaTemplateCompletion == null) {
			fJavaTemplateCompletion = new TemplateCompletionProposalComputer();
		}
		return fJavaTemplateCompletion;
	}
	
	public void setProposalCollector(JSDTProposalCollector translation) {
		this.fProposalCollector = translation;
	}
}
