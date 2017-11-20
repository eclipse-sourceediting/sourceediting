/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 230136 - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.ui.internal.Messages;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXSLContentAssistRequest;
import org.w3c.dom.Node;

/**
 * Provides content assistance for xsl elements that have the
 * exclude-result-prefixes attribute.  This will provide a list 
 * of all known result prefixes that aren't currently in the attribute
 * list.  This list is a space separated list.   The XSL prefix is
 * excluded.
 * 
 * @author dcarver
 * @since 1.0
 * 
 */
public class ExcludeResultPrefixesContentAssist extends AbstractXSLContentAssistRequest {
	private static final String EXCLUDE_RESULT_PREFIXES = "exclude-result-prefixes"; //$NON-NLS-1$
	private static final String DEFAULT = "#all"; //$NON-NLS-1$
	private static final String ADDITIONAL_INFO = Messages.ExcludeResultPrefixesContentAssist;
	protected String[] tokens = null;

	/**
	 * @param node
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	public ExcludeResultPrefixesContentAssist(Node node,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length, filter, textViewer);
	}
	
	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest#getCompletionProposals()
	 */
	@Override
	public ArrayList<ICompletionProposal> getCompletionProposals() {
		 proposals.clear();
		 
		 IDOMAttr attrNode = (IDOMAttr)((IDOMElement)getNode()).getAttributeNode(EXCLUDE_RESULT_PREFIXES);
		 String excludeResultPrefixes = attrNode.getValue();
		 int offset = getCursorPosition();
		 
		 if (excludeResultPrefixes == null || excludeResultPrefixes.equals(DEFAULT)) {
			 return getAllCompletionProposals();
		 }
		 
		 tokens = excludeResultPrefixes.split("\\s"); //$NON-NLS-1$
		 if (tokens[0].equals("")) { //$NON-NLS-1$
			 CustomCompletionProposal proposal = new CustomCompletionProposal(
						DEFAULT, offset, 0, DEFAULT.length(),
						XSLPluginImageHelper.getInstance().getImage(XSLPluginImages.IMG_PREFIX),
						DEFAULT, null, null, 0);
			 addProposal(proposal);
		 }
		 Collection<NamespaceInfo> namespaces = this.getNamespaces((IDOMElement)node);
		 for (NamespaceInfo namespace : namespaces) {
			 
			 if (includePrefix(namespace)) { 
				 CustomCompletionProposal proposal = new CustomCompletionProposal(
							namespace.prefix, offset, 0, namespace.prefix.length(),
							XSLPluginImageHelper.getInstance().getImage(XSLPluginImages.IMG_PREFIX),
							namespace.prefix, null, namespace.uri, 0);
				 addProposal(proposal);
			 }
		 }
		 
		return getAllCompletionProposals();
	}

	protected boolean includePrefix(NamespaceInfo namespace) {
		return !prefixExists(namespace.prefix) && !namespace.prefix.equals("") && !namespace.uri.equals(XSLCore.XSL_NAMESPACE_URI); //$NON-NLS-1$
	}
	
	
	protected boolean prefixExists(String prefix) {
		if (tokens == null || tokens.length == 0) {
			return false;
		}
		for (int cnt = 0; cnt < tokens.length; cnt++) {
			if (prefix.equals(tokens[cnt])) {
				return true;
			}
		}
		return false;
	}
	
}
