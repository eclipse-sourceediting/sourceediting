/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.NamedNodeMap;

/**
 * A Factory that determines which Content Assist Request class is
 * needed and returns the appropriate class.
 * 
 * @author David Carver
 * @since 1.0
 */
public class XSLContentAssistRequestFactory {
	private static final String ATTR_SELECT = "select"; //$NON-NLS-1$
	private static final String ATTR_TEST = "test"; //$NON-NLS-1$
	private static final String ATTR_MATCH = "match"; //$NON-NLS-1$
	private static final String ATTR_EXCLUDE_RESULT_PREFIXES = "exclude-result-prefixes"; //$NON-NLS-1$
	private static final String ATTR_MODE = "mode"; //$NON-NLS-1$
	private static final String ELEM_TEMPLATE = "template";	//$NON-NLS-1$
	private static final String ELEM_APPLYTEMPLATES = "apply-templates"; //$NON-NLS-1$
	private static final String ELEM_APPLY_IMPORTS = "apply-imports"; //$NON-NLS-1$
	private static final String ATTR_HREF = "href"; //$NON-NLS-1$
	private static final String ELEM_CALLTEMPLATE = "call-template"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	
	/**
	 * Get the appropriate content assist request class for the XSL request.
	 * @param textViewer
	 * @param documentPosition
	 * @param xmlNode
	 * @param sdRegion
	 * @param completionRegion
	 * @param proposals
	 * @param matchString
	 * @return
	 */
	public AbstractXSLContentAssistRequest getContentAssistRequest(ITextViewer textViewer,
			int documentPosition, IDOMNode xmlNode,
			IStructuredDocumentRegion sdRegion, ITextRegion completionRegion,
			ICompletionProposal[] proposals, String matchString) {
		NamedNodeMap nodeMap = xmlNode.getAttributes();
		IDOMElement element = (IDOMElement) xmlNode;


		if (this.hasAttributeAtTextRegion(ATTR_SELECT, nodeMap, completionRegion)) {
			return new SelectAttributeContentAssist(
					xmlNode, xmlNode.getParentNode(), sdRegion,
					completionRegion, documentPosition, 0, matchString,
					textViewer);
		}
		
		if (this.hasAttributeAtTextRegion(ATTR_MATCH, nodeMap, completionRegion)) {
			return new SelectAttributeContentAssist(
					xmlNode, xmlNode.getParentNode(), sdRegion,
					completionRegion, documentPosition, 0, matchString,
					textViewer);
		}
		
		
		if (this.hasAttributeAtTextRegion(ATTR_TEST, nodeMap, completionRegion)) {
			return new TestAttributeContentAssist(
					xmlNode, xmlNode.getParentNode(), sdRegion,
					completionRegion, documentPosition, 0, matchString,
					textViewer);
		}

		if (this.hasAttributeAtTextRegion(ATTR_EXCLUDE_RESULT_PREFIXES, nodeMap, completionRegion)) {
			return new ExcludeResultPrefixesContentAssist(
					xmlNode, xmlNode.getParentNode(), sdRegion,
					completionRegion, documentPosition, 0, matchString,
					textViewer);
		}
		
		if (hasAttributeAtTextRegion(ATTR_HREF, nodeMap, completionRegion)) {
			return new HrefContentAssistRequest(
				xmlNode, xmlNode.getParentNode(), sdRegion, completionRegion,
				documentPosition, 0, matchString, textViewer);
		} 
		
		
		if (element.getLocalName().equals(ELEM_TEMPLATE)) {
			if (hasAttributeAtTextRegion(ATTR_MODE, nodeMap, completionRegion)) {
				return new TemplateModeAttributeContentAssist(
					xmlNode, xmlNode.getParentNode(), sdRegion, completionRegion,
					documentPosition, 0, matchString, textViewer);
			}
		}
		
		if (element.getLocalName().equals(ELEM_APPLYTEMPLATES) || element.getLocalName().equals(ELEM_APPLY_IMPORTS)) {
			if (hasAttributeAtTextRegion(ATTR_MODE, nodeMap, completionRegion)) {
				return new TemplateModeAttributeContentAssist(
					xmlNode, xmlNode.getParentNode(), sdRegion, completionRegion,
					documentPosition, 0, matchString, textViewer);
			}
			
		}
		
		if (element.getLocalName().equals(ELEM_CALLTEMPLATE)) {
			if (hasAttributeAtTextRegion(ATTR_NAME, nodeMap, completionRegion)) {
				return new CallTemplateContentAssistRequest(xmlNode, xmlNode.getParentNode(), sdRegion, completionRegion,
						documentPosition, 0, matchString, textViewer);
			}
		}
		
		return new NullContentAssistRequest(xmlNode, xmlNode.getParentNode(), sdRegion, completionRegion,
					documentPosition, 0, matchString, textViewer);
	}

	protected boolean hasAttributeAtTextRegion(String attrName, NamedNodeMap nodeMap, ITextRegion aRegion) {
		IDOMAttr attrNode = (IDOMAttr) nodeMap.getNamedItem(attrName);
		return attrNode != null && attrNode.getValueRegion().getStart() == aRegion.getStart();
	}	
}
