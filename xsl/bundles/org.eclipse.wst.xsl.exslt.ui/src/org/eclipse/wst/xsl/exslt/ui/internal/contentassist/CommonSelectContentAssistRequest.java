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
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xsl.core.model.CallTemplate;
import org.eclipse.wst.xsl.exslt.core.internal.EXSLTCore;
import org.eclipse.wst.xsl.ui.internal.contentassist.SelectAttributeContentAssist;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXSLContentAssistRequest;
import org.eclipse.wst.xsl.ui.provisional.contentassist.CustomCompletionProposal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CommonSelectContentAssistRequest extends SelectAttributeContentAssist {

	private static final String EXSLT_COMMON_NAMESPACE = "http://exslt.org/common";
	private static final String NODE_SET = "node-set( )";
	private static final String OBJECT_TYPE = "object-type( )";
	
	String prefix = "";
	public CommonSelectContentAssistRequest(Node node,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length, filter,
				textViewer);
		
	}

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

	@Override
	protected void addSelectProposals(Element rootElement, int offset) {
		Document doc = rootElement.getOwnerDocument();
		NamespaceTable namespaceTable = new NamespaceTable(doc);
		namespaceTable.addElement(doc.getDocumentElement());
		prefix = namespaceTable.getPrefixForURI(EXSLTCore.EXSLT_COMMON_NAMESPACE);
		if (prefix != null) {
			addNodeSetProposal();
			addObjectTypeProposal();
		}
	}
	
	private void addNodeSetProposal() {
		String nodeset = "";
	
		if (prefix != null) {
			nodeset = prefix + ":" + NODE_SET;
		} else {
			nodeset = NODE_SET;
		}
		
		CustomCompletionProposal proposal = new CustomCompletionProposal(
				nodeset, getStartOffset() + 1, 0,
				nodeset.length(), XSLPluginImageHelper
						.getInstance().getImage(
								XSLPluginImages.IMG_XPATH_FUNCTION), nodeset, null, null, 0);
		proposals.add((ICompletionProposal)proposal);
	}
	
	private void addObjectTypeProposal() {
		String nodeset = "";
	
		if (prefix != null) {
			nodeset = prefix + ":" + OBJECT_TYPE;
		} else {
			nodeset = OBJECT_TYPE;
		}
		
		CustomCompletionProposal proposal = new CustomCompletionProposal(
				nodeset, getStartOffset() + 1, 0,
				nodeset.length(), XSLPluginImageHelper
						.getInstance().getImage(
								XSLPluginImages.IMG_XPATH_FUNCTION), nodeset, null, null, 0);
		proposals.add((ICompletionProposal)proposal);
	}	
	
}
