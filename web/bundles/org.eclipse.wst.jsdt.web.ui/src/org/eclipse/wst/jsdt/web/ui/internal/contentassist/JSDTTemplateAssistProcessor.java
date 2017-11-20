/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.jsdt.internal.ui.text.java.TemplateCompletionProposalComputer;
import org.eclipse.wst.jsdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
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
