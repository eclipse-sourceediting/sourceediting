package org.eclipse.wst.xsl.ui.internal.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XSLContentAssistRequestFactory {
	private static final String ATTR_SELECT = "select"; //$NON-NLS-1$
	private static final String ATTR_TEST = "test"; //$NON-NLS-1$
	private static final String ATTR_MATCH = "match"; //$NON-NLS-1$
	private static final String ATTR_EXCLUDE_RESULT_PREFIXES = "exclude-result-prefixes"; //$NON-NLS-1$
	private static final String ATTR_MODE = "mode";
	private static final String ELEM_TEMPLATE = "template";	
	
	public XSLContentAssistRequestFactory() {
		// TODO Auto-generated constructor stub
	}
	
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
		
		if (element.getLocalName().equals(ELEM_TEMPLATE)) {
			if (hasAttributeAtTextRegion(ATTR_MODE, nodeMap, completionRegion)) {
				return new TemplateModeAttributeContentAssist(
					xmlNode, xmlNode.getParentNode(), sdRegion, completionRegion,
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
