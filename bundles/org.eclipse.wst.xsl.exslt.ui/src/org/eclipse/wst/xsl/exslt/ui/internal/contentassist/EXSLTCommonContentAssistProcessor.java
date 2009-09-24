/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.exslt.ui.internal.contentassist;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.ui.internal.XSLUIConstants;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.internal.contentassist.XSLContentAssistRequestFactory;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXSLContentAssistProcessor;
import org.eclipse.wst.xsl.ui.provisional.contentassist.IContentAssistProposalRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EXSLTCommonContentAssistProcessor extends
		AbstractXSLContentAssistProcessor implements IContentAssistProcessor {

	private ArrayList<ICompletionProposal> exsltProposals;
	
	public EXSLTCommonContentAssistProcessor() {
	}

	public String getMaximumVersion() {
		return "1.0";
	}

	public String getMinimumVersion() {
		return "1.0";
	}

	public ArrayList<String> getNamespaces() {
		return null;
	}


	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}


	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}
	
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentPosition) {
		initializeProposalVariables(textViewer, documentPosition);
		
		// Only provide proposals for elements in either the XSLT Namespace or EXSLT namespace.
		if (!xmlNode.getNamespaceURI().contains("http://exslt.org/") &&
			!xmlNode.getNamespaceURI().equals(XSLCore.XSL_NAMESPACE_URI)) {
			return null;
		}

		EXSLTContentAssistRequestFactory requestFactory = new EXSLTContentAssistRequestFactory(
				textViewer, cursorPosition, xmlNode, sdRegion,
				completionRegion, matchString);
		
		IContentAssistProposalRequest contentAssistRequest = requestFactory
			.getContentAssistRequest();
		
		exsltProposals = contentAssistRequest.getCompletionProposals();
		ICompletionProposal[] proposals = new ICompletionProposal[exsltProposals.size()];
		exsltProposals.toArray(proposals);
		
		return proposals;
	}

}
