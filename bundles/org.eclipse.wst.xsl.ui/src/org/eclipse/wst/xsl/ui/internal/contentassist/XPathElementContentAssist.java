/*******************************************************************************
 *Copyright (c) 2008, 2009 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 244978 - initial API and implementation
 *                          bug 259575 - replaces text if MatchString has a length with XML Content Model
 *                                       proposals.
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXMLElementContentAssistRequest;
import org.w3c.dom.Node;

/**
 * This class provides XML Element proposals within XPath related items like
 * select, test, and match attributes.  This will leverage an ContentModel that
 * has been loaded and try to provide all available XML elements that could be used.
 * It should be enhanced to know the elements to be used based on information from
 * the XPath.
 * 
 * @author David Carver
 * @since 1.0
 */
public class XPathElementContentAssist extends
		AbstractXMLElementContentAssistRequest {

	/**
	 * @param node
	 * @param parent
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	public XPathElementContentAssist(Node node,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length,
				filter, textViewer);
	}

	/**
	 * Provides a list of possible proposals for the XML Elements within the current
	 * scope.  This leverages the ContentModel that was loaded by the XML Catalog, 
	 * Custom Resolver, or the inferred grammar.
	 */
	@Override
	public ArrayList<ICompletionProposal> getCompletionProposals() {
		IDOMDocument domDocument = (IDOMDocument) node.getOwnerDocument();
		
		getXPathXMLElementProposals(domDocument);
		
		
		return getAllCompletionProposals();
	}

	private void getXPathXMLElementProposals(IDOMDocument domDocument) {
		try {
			Node ancestorNode = XSLTXPathHelper.selectSingleNode(getNode(),
					XPATH_FIRST_XSLANCESTOR_NODE);
			
			Iterator<CMNode> cmNodeIt = getAvailableContentNodes(domDocument,
					ancestorNode, ModelQuery.INCLUDE_CHILD_NODES);
			
			createXPathXMLProposals(ancestorNode, cmNodeIt);

		} catch (Exception ex) {
			XSLUIPlugin.log(ex);
		}
	}

	private void createXPathXMLProposals(Node ancestorNode,
			Iterator<CMNode> cmNodeIt) {
		while (cmNodeIt.hasNext()) {
			CMNode cmNode = cmNodeIt.next();
			String proposedText = getRequiredName(ancestorNode, cmNode);
			if (!(proposedText.contains("xsl:") || proposedText.contains("xslt:"))) { //$NON-NLS-1$ //$NON-NLS-2$
				int offset = getReplacementBeginPosition();
				Image image = getCMNodeImage(cmNode);
				int startLength = getCursorPosition() - offset;
				String additionalInfo = getInfoProvider().getInfo(cmNode);
				
				if (matchString.length() > 0) {
					if (proposedText.startsWith(matchString)) {
						CustomCompletionProposal proposal = createProposal(
								proposedText, additionalInfo, offset, image, startLength);
						addProposal(proposal);
					}
				} else {
					CustomCompletionProposal proposal = createProposal(
							proposedText, additionalInfo, offset, image, startLength);
					addProposal(proposal);
				}					
			}
		}
	}

}
