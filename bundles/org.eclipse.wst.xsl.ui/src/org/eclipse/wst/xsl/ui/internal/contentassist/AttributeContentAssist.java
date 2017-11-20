/*******************************************************************************
 *Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 294079 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This class provides content assistance for attributes that contain the "{" character attribute.
 *  
 * @author dcarver
 * @since 1.1
 */
public class AttributeContentAssist extends SelectAttributeContentAssist {

	/**
	 * Constructor for the XSL content assistance for the test attribute.
	 * 
	 * @param node
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	public AttributeContentAssist(Node node, 
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length, filter,
				textViewer);
	}
	
	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xsl.ui.internal.contentassist.SelectAttributeContentAssist#getCompletionProposals()
	 */
	@Override
	public ArrayList<ICompletionProposal> getCompletionProposals() {
				
		int offset = getReplacementBeginPosition();
		
		IDOMAttr attrNode = getAttributeAtOffset(offset);
	    
	    if (attrNode == null || !matchString.contains("{")) { //$NON-NLS-1$
	    	return getAllCompletionProposals();
	    }
	    
		adjustXPathStart(attrNode.getLocalName());
		offset = getReplacementBeginPosition();
		
		matchString = extractXPathMatchString(attrNode, getRegion(), getReplacementBeginPosition());
		
	    addSelectProposals((Element)getNode().getParentNode(), offset);

		return getAllCompletionProposals();
    }

	private IDOMAttr getAttributeAtOffset(int offset) {
		Node node = getNode();
		if (node == null) {
			return null;
		}
		NamedNodeMap nodeMap = node.getAttributes();
		IDOMAttr attrNode = null;
		
		for (int i = 0; i < nodeMap.getLength(); i++) {
			IDOMAttr tmpAttr = (IDOMAttr) nodeMap.item(i);
			int start = tmpAttr.getStartOffset();
			int end = tmpAttr.getEndOffset();
			if (start <= offset ||
				end  > offset) {
				attrNode = tmpAttr;
				break;
			}	
		}
		return attrNode;
	}
	
}
