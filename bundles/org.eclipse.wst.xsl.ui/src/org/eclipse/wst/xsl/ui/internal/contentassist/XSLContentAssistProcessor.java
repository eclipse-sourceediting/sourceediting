/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 213849 - initial API and implementation
 *     David Carver - STAR - bug 230958 - refactored to fix bug with getting
 *                                        the DOM Document for the current editor
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.internal.templates.TemplateContextTypeIdsXPath;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The XSL Content Assist Processor provides content assistance for various
 * attributes values within the XSL Editor. This includes support for xpaths on
 * select statements as well as on test and match attributes.
 * 
 * @author David Carver
 * 
 *
 * 
 */
public class XSLContentAssistProcessor extends XMLContentAssistProcessor
		implements IPropertyChangeListener {

	private static final String ATTR_SELECT = "select"; //$NON-NLS-1$
	private static final String ATTR_TEST = "test"; //$NON-NLS-1$
	private static final String ATTR_MATCH = "match"; //$NON-NLS-1$
	/**
	 * Retrieve all global variables in the stylesheet.
	 */
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
	 * The XSL Content Assist Processor handles XSL specific functionality for
	 * content assistance. It leverages several XPath selection variables to
	 * help with the selection of elements and template names.
	 * 
	 */
	public XSLContentAssistProcessor() {
		super();
	}
	
	/**
	 * TODO: Add Javadoc
	 * 
	 * @param textViewer
	 * @param documentPosition
	 * @return
	 * 
	 * @see org.eclipse.wst.xml.ui.contentassist.AbstractContentAssistProcessor#
	 * 	computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentPosition) {
		fTemplateContexts.clear();
		return super.computeCompletionProposals(textViewer, documentPosition);
	}
	

	/**
	 * Adds Attribute proposals based on the element and the attribute where the
	 * content proposal was instantiated.
	 * 
	 * @param contentAssistRequest
	 * 		Content Assist Request that initiated the proposal request
	 * 
	 */
	@Override
	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {
		super.addAttributeValueProposals(contentAssistRequest);
		String namespace = DOMNamespaceHelper.getNamespaceURI(contentAssistRequest.getNode());

		String attributeName = getAttributeName(contentAssistRequest);
		Element rootElement = contentAssistRequest.getNode().getOwnerDocument().getDocumentElement();

		if (attributeName != null) {
			int offset = contentAssistRequest.getStartOffset() + 1;

			addAttributeValueOfProposals(contentAssistRequest, namespace, rootElement, offset);

			if (isXSLNamespace(namespace)) {
				addSelectAndTestProposals(contentAssistRequest, attributeName, rootElement, offset);
				addMatchProposals(contentAssistRequest, attributeName,	offset);
			}
		}
	}

	private void addMatchProposals(ContentAssistRequest contentAssistRequest, String attributeName, int offset) {
		if (attributeName.equals(ATTR_MATCH)) {
			addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.AXIS, offset);
			addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.XPATH, offset);
			addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.CUSTOM, offset);
		}
	}

	private void addSelectAndTestProposals(
			ContentAssistRequest contentAssistRequest, String attributeName, Element rootElement, int offset) {
		if (attributeName.equals(ATTR_SELECT) || attributeName.equals(ATTR_TEST)) {
			addGlobalProposals(rootElement, contentAssistRequest, offset);
			addLocalProposals(contentAssistRequest.getNode(), contentAssistRequest, offset);
			addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.AXIS, offset);
			addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.XPATH, offset);
			addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.CUSTOM, offset);
		}
	}

	private boolean isXSLNamespace(String namespace) {
		return namespace != null && namespace.equals(XSLCore.XSL_NAMESPACE_URI);
	}

	private void addAttributeValueOfProposals(
			ContentAssistRequest contentAssistRequest, String namespace, Element rootElement, int offset) {
		if (contentAssistRequest.getMatchString().contains("{")) {
			addGlobalProposals(rootElement, contentAssistRequest, contentAssistRequest.getReplacementBeginPosition());
			addLocalProposals(contentAssistRequest.getNode(), contentAssistRequest,
					          contentAssistRequest.getReplacementBeginPosition());
			addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.AXIS, offset);
			addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.XPATH, offset);
			addTemplates(contentAssistRequest, TemplateContextTypeIdsXPath.CUSTOM, offset);

		}
	}

	private void addLocalProposals(Node xpathnode,
			ContentAssistRequest contentAssistRequest, int offset) {
		addVariablesProposals(XPATH_LOCAL_VARIABLES, xpathnode,
				contentAssistRequest, offset);
		addVariablesProposals(XPATH_LOCAL_PARAMS, xpathnode,
				contentAssistRequest, offset);
	}

	private void addGlobalProposals(Node xpathnode,
			ContentAssistRequest contentAssistRequest, int offset) {
		addVariablesProposals(XPATH_GLOBAL_VARIABLES, xpathnode,
				contentAssistRequest, offset);
		addVariablesProposals(XPATH_GLOBAL_PARAMS, xpathnode,
				contentAssistRequest, offset);
	}

	/**
	 * Adds Parameter and Variables as proposals. This
	 * information is selected based on the XPath statement that is sent to it
	 * and the input Node passed. It uses a custom composer to XSL Variable
	 * proposal.
	 * 
	 * @param xpath
	 * @param xpathnode
	 * @param contentAssistRequest
	 * @param offset
	 */
	private void addVariablesProposals(String xpath, Node xpathnode,
			ContentAssistRequest contentAssistRequest, int offset) {
		synchronized (XPATH_LOCK) {
			try {
				NodeList nodes = XSLTXPathHelper.selectNodeList(xpathnode, xpath);
				int startLength = getCursorPosition() - offset;

				if (hasNodes(nodes)) {
					for (int nodecnt = 0; nodecnt < nodes.getLength(); nodecnt++) {
						Node node = nodes.item(nodecnt);
						String variableName = "$" + node.getAttributes().getNamedItem("name").getNodeValue(); //$NON-NLS-1$ //$NON-NLS-2$

						CustomCompletionProposal proposal = new CustomCompletionProposal(
								variableName, offset, 0, startLength + variableName.length(),
								XSLPluginImageHelper.getInstance().getImage(XSLPluginImages.IMG_VARIABLES),
								variableName, null, null, 0);
						contentAssistRequest.addProposal(proposal);
					}
				}

			} catch (TransformerException ex) {
				XSLUIPlugin.log(ex);
			}
		}
	}

	/**
	 * Checks to make sure that the NodeList has data
	 * @param nodes A NodeList object
	 * @return True if has data, false if empty
	 */
	private boolean hasNodes(NodeList nodes) {
		return nodes != null && nodes.getLength() > 0;
	}

	/**
	 * Get the cursor position within the Text Viewer
	 * @return An int value containing the cursor position
	 */
	private int getCursorPosition() {
		return fTextViewer.getTextWidget().getCaretOffset();
	}

	/**
	 * Adds XPath related templates to the list of proposals
	 * 
	 * @param contentAssistRequest
	 * @param context
	 * @param startOffset
	 */
	private void addTemplates(ContentAssistRequest contentAssistRequest,
			String context, int startOffset) {
		if (contentAssistRequest == null) {
			return;
		}

		// if already adding template proposals for a certain context type, do
		// not add again
		if (!fTemplateContexts.contains(context)) {
			fTemplateContexts.add(context);
			boolean useProposalList = !contentAssistRequest.shouldSeparate();

			if (getTemplateCompletionProcessor() != null) {
				getTemplateCompletionProcessor().setContextType(context);
				ICompletionProposal[] proposals = getTemplateCompletionProcessor()
						.computeCompletionProposals(fTextViewer, startOffset);
				for (int i = 0; i < proposals.length; ++i) {
					if (useProposalList) {
						contentAssistRequest.addProposal(proposals[i]);
					} else {
						contentAssistRequest.addMacro(proposals[i]);
					}
				}
			}
		}
	}

	private XPathTemplateCompletionProcessor getTemplateCompletionProcessor() {
		if (fTemplateProcessor == null) {
			fTemplateProcessor = new XPathTemplateCompletionProcessor();
		}
		return fTemplateProcessor;
	}

	/**
	 * Gets the attribute name that the content assist was triggered on.
	 * 
	 * @param contentAssistRequest
	 * @return
	 */
	private String getAttributeName(ContentAssistRequest contentAssistRequest) {
		IStructuredDocumentRegion open = ((IDOMNode)contentAssistRequest.getNode()).getFirstStructuredDocumentRegion();
		ITextRegionList openRegions = open.getRegions();
		int i = openRegions.indexOf(contentAssistRequest.getRegion());
		if (i >= 0) {

			ITextRegion nameRegion = null;
			while (i >= 0) {
				nameRegion = openRegions.get(i--);
				if (nameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
					break;
				}
			}

			// String attributeName = nameRegion.getText();
			return open.getText(nameRegion);
		}
		return null;
	}

}
