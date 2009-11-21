/*******************************************************************************
 *Copyright (c) 2008, 2009 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 240170 - initial API and implementation
 *                          bug 259575 - fixed replacement issue and XPath tokenizer
 *                                       position adjuster for matchString.
 *                          bug 281420 - fixed variable replacement offsets.
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;
import org.eclipse.wst.xml.xpath.ui.internal.contentassist.XPathTemplateCompletionProcessor;
import org.eclipse.wst.xml.xpath.ui.internal.templates.TemplateContextTypeIdsXPath;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXMLElementContentAssistRequest;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXSLContentAssistRequest;
import org.eclipse.wst.xsl.ui.provisional.contentassist.CustomCompletionProposal;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides content assistance for the XSL select attribute.
 * 
 * @author dcarver
 * @since 1.1
 */
public class SelectAttributeContentAssist extends
		AbstractXSLContentAssistRequest {

	protected static final String SELECT_ATTRIBUTE = "select"; //$NON-NLS-1$

	private static final String XPATH_GLOBAL_VARIABLES = "/xsl:stylesheet/xsl:variable"; //$NON-NLS-1$

	/**
	 * Retrieve all global parameters in the stylesheet.
	 */
	private static final String XPATH_GLOBAL_PARAMS = "/xsl:stylesheet/xsl:param"; //$NON-NLS-1$

	/**
	 * Limit selection of variables to those that are in the local scope.
	 */
	private static final String XPATH_LOCAL_VARIABLES = "ancestor::xsl:template/descendant::xsl:variable"; //$NON-NLS-1$

	/**
	 * Limit selection of params to those that are in the local scope.
	 */
	private static final String XPATH_LOCAL_PARAMS = "ancestor::xsl:template/descendant::xsl:param"; //$NON-NLS-1$

	private XPathTemplateCompletionProcessor fTemplateProcessor = null;
	private List<String> fTemplateContexts = new ArrayList<String>();
	private static final byte[] XPATH_LOCK = new byte[0];

	/**
	 * Handles Content Assistance requests for Select Attributes. This is called
	 * an instantiated through the use of the computeProposals method from the
	 * XSLContentAssistProcessor. It will calculate the available proposals that
	 * are available for the XSL select attribute.
	 * 
	 * @param node
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	public SelectAttributeContentAssist(Node node,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length, filter,
				textViewer);
		// TODO Auto-generated constructor stub
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest#getCompletionProposals()
	 */
	@Override
	public ArrayList<ICompletionProposal> getCompletionProposals() {
		proposals.clear();

		adjustXPathStart(SELECT_ATTRIBUTE);

		int offset = getReplacementBeginPosition();
		IDOMAttr attrNode = getAttribute(SELECT_ATTRIBUTE);

		this.matchString = extractXPathMatchString(attrNode, getRegion(),
				getReplacementBeginPosition());

		addSelectProposals((Element) getNode().getParentNode(), offset);

		return getAllCompletionProposals();
	}

	protected IDOMAttr getAttribute(String attrName) {
		return (IDOMAttr) ((IDOMElement) getNode()).getAttributeNode(attrName);
	}

	/**
	 * This needs to setup the content assistance correctly. Here is what needs
	 * to happen: 1. Adjust the matchString (This should have been calculated
	 * earlier) 2. Get the current tokens offset position..this will be the
	 * starting offset. 3. Get the replacement length...this is the difference
	 * between the token offset and the next token or end of the string
	 * 
	 * @param attrName
	 *            The name of the attribute to use as the starting node.
	 */
	protected void adjustXPathStart(String attrName) {
		IDOMElement elem = (IDOMElement) getNode();
		IDOMAttr xpathNode = (IDOMAttr) elem.getAttributeNode(attrName);
		if (xpathNode == null) {
			return;
		}

		String xpathString = xpathNode.getValue();
		if (xpathString.length() == 0) {
			return;
		}

		int startOffset = xpathNode.getValueRegionStartOffset();

		replacementLength = getReplacementBeginPosition() - startOffset;
	}

	protected String extractXPathMatchString(IDOMAttr node,
			ITextRegion aRegion, int offset) {
		if (node == null || node.getValue().length() == 0)
			return ""; //$NON-NLS-1$

		if (matchString.length() < 1) {
			return matchString;
		}

		int column = offset - node.getValueRegionStartOffset() - 1;
		String nodeValue = node.getValue();

		int seperatorPos = getXPathSeperatorPos(column, nodeValue);

		if (seperatorPos >= column) {
			return ""; //$NON-NLS-1$
		}

		return node.getValue().substring(seperatorPos, column);
	}

	protected int getXPathSeperatorPos(int column, String nodeValue) {
		char[] keyTokens = { '{', '/', '[', ']', '(', ')', ',', ' ', };

		int seperatorPos = 0;

		String potentialMatchString = nodeValue.substring(0, column);

		for (int cnt = 0; cnt < keyTokens.length; cnt++) {
			int keyPos = potentialMatchString.lastIndexOf(keyTokens[cnt]);
			if (keyPos >= 0 && keyPos <= column - 1) {
				seperatorPos = keyPos + 1;
			}
		}

		int axisPos = nodeValue.indexOf("::"); //$NON-NLS-1$
		if (axisPos > seperatorPos && axisPos <= column - 1) {
			seperatorPos = axisPos + 1;
		}
		return seperatorPos;
	}

	protected void addSelectProposals(Element rootElement, int offset) {
		addContentModelProposals(offset);
		addGlobalProposals(rootElement, offset - getMatchString().length());
		addLocalProposals(getNode(), offset - getMatchString().length());
		addTemplates(TemplateContextTypeIdsXPath.AXIS, offset);
		addTemplates(TemplateContextTypeIdsXPath.XPATH, offset);
		addTemplates(TemplateContextTypeIdsXPath.CUSTOM, offset);
		addTemplates(TemplateContextTypeIdsXPath.OPERATOR, offset);
	}

	private void addContentModelProposals(int offset) {
		AbstractXMLElementContentAssistRequest xpathXMLproposals = new XPathElementContentAssist(
				node, documentRegion, getRegion(), offset
						- getMatchString().length(), getReplacementLength(),
				getMatchString(), textViewer);
		ArrayList<ICompletionProposal> xmlProposals = xpathXMLproposals
				.getCompletionProposals();
		proposals.addAll(xmlProposals);
	}

	/**
	 * Adds XPath related templates to the list of proposals
	 * 
	 * @param contentAssistRequest
	 * @param context
	 * @param startOffset
	 */
	protected void addTemplates(String context, int startOffset) {
		if (fTemplateContexts.contains(context)
				|| getTemplateCompletionProcessor() == null) {
			return;
		}
		XPathTemplateCompletionProcessor processor = getTemplateCompletionProcessor();

		fTemplateContexts.add(context);

		processor.setContextType(context);
		ICompletionProposal[] proposals = processor.computeCompletionProposals(
				textViewer, startOffset);
		for (int i = 0; i < proposals.length; ++i) {
			ICompletionProposal proposal = proposals[i];
			if (matchString.length() > 0) {
				if (proposal.getDisplayString().startsWith(matchString)) {
					addProposal(proposals[i]);
				}
			} else {
				addProposal(proposals[i]);
			}
		}
	}

	private void addLocalProposals(Node xpathnode, int offset) {
		addVariablesProposals(XPATH_LOCAL_VARIABLES, xpathnode, offset);
		addVariablesProposals(XPATH_LOCAL_PARAMS, xpathnode, offset );
	}

	private void addGlobalProposals(Node xpathnode, int offset) {
		addVariablesProposals(XPATH_GLOBAL_VARIABLES, xpathnode, offset );
		addVariablesProposals(XPATH_GLOBAL_PARAMS, xpathnode, offset);
	}

	/**
	 * Adds Parameter and Variables as proposals. This information is selected
	 * based on the XPath statement that is sent to it and the input Node
	 * passed. It uses a custom composer to XSL Variable proposal.
	 * 
	 * @param xpath
	 * @param xpathnode
	 * @param contentAssistRequest
	 * @param offset
	 */
	private void addVariablesProposals(String xpath, Node xpathnode, int offset) {
		synchronized (XPATH_LOCK) {
			try {
				NodeList nodes = XSLTXPathHelper.selectNodeList(xpathnode,
						xpath);
				if (!hasNodes(nodes)) {
					return;
				}
				
				int startLength = getCursorPosition() - offset;

				for (int nodecnt = 0; nodecnt < nodes.getLength(); nodecnt++) {
					Node node = nodes.item(nodecnt);

					String variableName = "$" + node.getAttributes().getNamedItem("name").getNodeValue(); //$NON-NLS-1$ //$NON-NLS-2$
					
					CustomCompletionProposal proposal = new CustomCompletionProposal(
							variableName, offset, 0, startLength
									+ variableName.length(),
							XSLPluginImageHelper.getInstance().getImage(
									XSLPluginImages.IMG_VARIABLES),
							variableName, null, null, 0);

					if (matchString.length() > 0) {
						if (proposal.getDisplayString().startsWith(
								matchString)) {
							addProposal(proposal);
						}
					} else {
						addProposal(proposal);
					}
				}

			} catch (TransformerException ex) {
				XSLUIPlugin.log(ex);
			}
		}
	}

	private XPathTemplateCompletionProcessor getTemplateCompletionProcessor() {
		if (fTemplateProcessor == null) {
			fTemplateProcessor = new XPathTemplateCompletionProcessor();
		}
		return fTemplateProcessor;
	}
}
