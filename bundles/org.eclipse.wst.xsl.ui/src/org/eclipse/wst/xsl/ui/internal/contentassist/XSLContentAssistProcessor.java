/*******************************************************************************
 * Copyright (c) 2008, 2010 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 213849 - initial API and implementation
 *     David Carver - STAR - bug 230958 - refactored to fix bug with getting
 *                                        the DOM Document for the current editor
 *     David Carver - STAR - bug 240170 - refactored code to help with narrowing of
 *                                        results and easier maintenance.
 *     David Carver (Intalio) - bug 289498 - Added additional context types.     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.ui.internal.Messages;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXSLContentAssistProcessor;
import org.eclipse.wst.xsl.ui.provisional.contentassist.IContentAssistProposalRequest;
import org.w3c.dom.NamedNodeMap;

/**
 * The XSL Content Assist Processor provides content assistance for various
 * attributes values within the XSL Editor. This includes support for xpaths on
 * select statements as well as on test and match attributes.
 * 
 * @author David Carver
 * @since 1.0
 */
public class XSLContentAssistProcessor extends AbstractXSLContentAssistProcessor implements IContentAssistProcessor {
	
	private static final String attributeXpath = "{"; //$NON-NLS-1$

	private ArrayList<ICompletionProposal> xslProposals;
	private ArrayList<ICompletionProposal> additionalProposals;
	private ArrayList<ICompletionProposal> attributeProposals;
	private ArrayList<String> namespaces;
	/**
	 * Provides an XSL Content Assist Processor class that is XSL aware and XML
	 * aware.
	 */
	public XSLContentAssistProcessor() {
		super();
		xslProposals = new ArrayList<ICompletionProposal>();
		additionalProposals = new ArrayList<ICompletionProposal>();
		namespaces = new ArrayList<String>();
		namespaces.add(XSLCore.XSL_NAMESPACE_URI);
	}

	/**
	 * CONTENT ASSIST STARTS HERE
	 * 
	 * Return a list of proposed code completions based on the specified
	 * location within the document that corresponds to the current cursor
	 * position within the text-editor control.
	 * 
	 * @param textViewer
	 * @param documentPosition
	 *            - the cursor location within the document
	 * 
	 * @return an array of ICompletionProposal
	 */
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentPosition) {
		initializeProposalVariables(textViewer, documentPosition);
		
		

		additionalProposals = getAdditionalXSLElementProposals();

		xslProposals = getXSLNamespaceProposals();
		
		attributeProposals = getAttributeProposals();

		ArrayList<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();
		proposalList.addAll(additionalProposals);
		proposalList.addAll(xslProposals);
		proposalList.addAll(attributeProposals);

		ICompletionProposal[] combinedProposals = combineProposals(proposalList);

		if (combinedProposals == null || combinedProposals.length == 0) {
			setErrorMessage(Messages.NoContentAssistance);
		}

		return combinedProposals;
	}

	private ArrayList<ICompletionProposal> getXSLNamespaceProposals() {
		if (XSLCore.isXSLNamespace(xmlNode)) {
			XSLContentAssistRequestFactory requestFactory = new XSLContentAssistRequestFactory(
					textViewer, cursorPosition, xmlNode, sdRegion,
					completionRegion, matchString);

			IContentAssistProposalRequest contentAssistRequest = requestFactory
					.getContentAssistRequest();
			xslProposals = contentAssistRequest.getCompletionProposals();
		}
		return xslProposals;
	}

	private ArrayList<ICompletionProposal> getAdditionalXSLElementProposals() {
		if (!XSLCore.isXSLNamespace(xmlNode)) {
			additionalProposals = new XSLElementContentAssistRequest(xmlNode,
					sdRegion, completionRegion, cursorPosition, 0, matchString,
					textViewer).getCompletionProposals();
		}
		return additionalProposals;
	}
	
	private ArrayList<ICompletionProposal> getAttributeProposals() {
		attributeProposals = new AttributeContentAssist(xmlNode,
				sdRegion, completionRegion, cursorPosition, 0, matchString, textViewer).getCompletionProposals();
		return attributeProposals;
	}

	private ICompletionProposal[] combineProposals(
			ArrayList<ICompletionProposal> proposalList) {
		ICompletionProposal[] combinedProposals = new ICompletionProposal[proposalList
				.size()];
		proposalList.toArray(combinedProposals);
		return combinedProposals;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public String getMaximumVersion() {
		return "2.0"; //$NON-NLS-1$
	}

	public String getMinimumVersion() {
		return "1.0"; //$NON-NLS-1$
	}

	public ArrayList<String> getNamespaces() {
		return null;
	}

	protected boolean assistanceOnAttribute(IDOMNode node, ITextRegion aRegion) {
		NamedNodeMap nodeMap = node.getAttributes();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			IDOMAttr attrNode = (IDOMAttr) nodeMap.item(i);
			if (attrNode.getValueRegion() != null &&
				attrNode.getValueRegion().getStart() == aRegion.getStart()) {
				if (attrNode.getValue().contains(attributeXpath)) {
					return true;
				}
			}
		}
		return false;
	}
}	

