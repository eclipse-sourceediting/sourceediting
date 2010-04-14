/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.contentassist.ICompletionProposalComputer;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentAssistProcessor;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JSDTContentAssistant extends AbstractContentAssistProcessor implements ICompletionProposalComputer {
	private JSDTContentAssistantProcessor fContentAssistProcessor;
	private JSDTTemplateAssistProcessor fTemplateAssistProcessor;
	private JSDTHtmlCompletionProcessor fHhtmlcomp;
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentPosition) {
		Vector proposals = new Vector();
		ICompletionProposal[] completionProposals;
		ICompletionProposal endScript = getHtmlContentAssistProcessor().getEndScriptProposal(viewer, documentPosition);
		if(endScript!=null) {
			return new ICompletionProposal[] {endScript};
			//proposals.add(endScript);
		}
		JSDTProposalCollector theCollector = getProposalCollector(viewer, documentPosition);
		/* add end script tag if needed */

		/* --------- Content Assistant --------- */
		if(theCollector==null) return new ICompletionProposal[0];
		
		getContentAssistProcessor().setProposalCollector(theCollector);
		completionProposals = getContentAssistProcessor().computeCompletionProposals(viewer, documentPosition);
		proposals.addAll(Arrays.asList(completionProposals));
		/* HTML Proposals */
		completionProposals = getHtmlContentAssistProcessor().computeCompletionProposals(viewer, documentPosition);
		proposals.addAll(Arrays.asList(completionProposals));
		/* --------- template completions --------- */
		getTemplateCompletionProcessor().setProposalCollector(theCollector);
		completionProposals = getTemplateCompletionProcessor().computeCompletionProposals(viewer, documentPosition);
		proposals.addAll(Arrays.asList(completionProposals));
		return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0]);
	}
	
	private JSDTHtmlCompletionProcessor getHtmlContentAssistProcessor() {
		if (fHhtmlcomp == null) {
			fHhtmlcomp = new JSDTHtmlCompletionProcessor();
		}
		return fHhtmlcomp;
	}
	
	private JSDTContentAssistantProcessor getContentAssistProcessor() {
		if (fContentAssistProcessor == null) {
			fContentAssistProcessor = new JSDTContentAssistantProcessor();
		}
		return fContentAssistProcessor;
	}
	private IJsTranslation getJSPTranslation(ITextViewer viewer, int offset) {
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
			IDOMDocument xmlDoc = xmlModel.getDocument();

			JsTranslationAdapter translationAdapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
			
			if (translationAdapter != null) {
				return translationAdapter.getJsTranslation(true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		return null;
	}
	
	protected JSDTProposalCollector getProposalCollector(ITextViewer viewer, int offset) {
		IJsTranslation tran = getJSPTranslation(viewer, offset);
		if(tran==null) return null;
		return new JSDTProposalCollector(tran);
	}
	
	private JSDTTemplateAssistProcessor getTemplateCompletionProcessor() {
		if (fTemplateAssistProcessor == null) {
			fTemplateAssistProcessor = new JSDTTemplateAssistProcessor();
		}
		return fTemplateAssistProcessor;
	}

	public void sessionStarted() {
		// TODO Auto-generated method stub
		
	}

	public List computeCompletionProposals(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
		Vector proposals = new Vector();
		ICompletionProposal[] completionProposals;
		ICompletionProposal endScript = getHtmlContentAssistProcessor().getEndScriptProposal(context.getViewer(), context.getInvocationOffset());
		if(endScript!=null) {
			return new ArrayList(0);
			//proposals.add(endScript);
		}
		JSDTProposalCollector theCollector = getProposalCollector(context.getViewer(), context.getInvocationOffset());
		/* add end script tag if needed */

		/* --------- Content Assistant --------- */
		if(theCollector==null) return new ArrayList(0);
		
		getContentAssistProcessor().setProposalCollector(theCollector);
		completionProposals = getContentAssistProcessor().computeCompletionProposals(context.getViewer(), context.getInvocationOffset());
		proposals.addAll(Arrays.asList(completionProposals));
		/* HTML Proposals */
		completionProposals = getHtmlContentAssistProcessor().computeCompletionProposals(context.getViewer(), context.getInvocationOffset());
		proposals.addAll(Arrays.asList(completionProposals));
		/* --------- template completions --------- */
		getTemplateCompletionProcessor().setProposalCollector(theCollector);
		completionProposals = getTemplateCompletionProcessor().computeCompletionProposals(context.getViewer(), context.getInvocationOffset());
		proposals.addAll(Arrays.asList(completionProposals));
		return proposals;
	}

	public List computeContextInformation(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
		return Arrays.asList(computeContextInformation(context.getViewer(), context.getInvocationOffset()));
	}

	public void sessionEnded() {
		// TODO Auto-generated method stub
		
	}
}
