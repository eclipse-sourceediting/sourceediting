/**
 * 
 */
package org.eclipse.wst.xsl.internal.ui;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;

/**
 * @author dcarver
 *
 */
@SuppressWarnings("restriction")
public class XSLContentAssistProcessor extends XMLContentAssistProcessor {

	private String xslNamespace = "http://www.w3.org/1999/XSL/Transform";
	/**
	 * 
	 */
	public XSLContentAssistProcessor() {
		super();
	}
	
	
	/**
	 * Adds Attribute proposals based on the element and the attribute
	 * where the content proposal was instatiated.
	 * 
	 * @param contentAssistRequest Content Assist Request that initiated the proposal request
	 * 
	 */
	@Override
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest) {
		super.addAttributeValueProposals(contentAssistRequest);
		IDOMNode node = (IDOMNode)contentAssistRequest.getNode();
        String namespace = DOMNamespaceHelper.getNamespaceURI(node);
		String nodeName = DOMNamespaceHelper.getUnprefixedName(node.getNodeName());
		String attributeName = getAttributeName(contentAssistRequest);
		
		if (attributeName != null) {
			// Current node belongs in the XSL Namespace
			if (namespace.equals(this.xslNamespace)) {
				if (attributeName.equals("select")) {
					if (nodeName.equals("param") ||
						nodeName.equals("variable")){
						addXPath(contentAssistRequest);
					}
				}
			}
			
//			if (node.getParentNode().getNodeName().equalsIgnoreCase("template")){
//			
//	}
//	
//	if (node.getParentNode().getNodeName().equalsIgnoreCase("call-template")){
//		
//	}
			
		}
	}
	
	private String getAttributeName(ContentAssistRequest contentAssistRequest) {
		// Find the attribute region and name for which this position should
		// have a value proposed
		String attributeName = null;
		IDOMNode node = (IDOMNode)contentAssistRequest.getNode();
		IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
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
			attributeName = open.getText(nameRegion);
		}
		return attributeName;
	}
	
	/**
	 * Adds a list of XPath 1.0 proposals.
	 * @param contentAssistRequest
	 */
    private void addXPath(ContentAssistRequest contentAssistRequest) {
    	XPathFunctions xpathFunctions = new XPathFunctions();
    	for (int proposalcnt = 0; proposalcnt < xpathFunctions.size(); proposalcnt++ ) {
    		String functionName = xpathFunctions.getFunctionName(proposalcnt);
    		if (functionName != null) {
            	ICompletionProposal proposal = new XSLSelectCompletionProposal(functionName + "()", contentAssistRequest.getStartOffset() + 1, contentAssistRequest.getReplacementLength() - 1, contentAssistRequest.getStartOffset() + 1);
    	    	contentAssistRequest.addProposal(proposal);
    		}
    	}
    }
}
