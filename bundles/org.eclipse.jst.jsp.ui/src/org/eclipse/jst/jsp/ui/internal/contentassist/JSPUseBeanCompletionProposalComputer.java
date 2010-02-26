/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;

/**
 * <p>Compute JSP use bean completion proposals</p>
 */
public class JSPUseBeanCompletionProposalComputer extends
		DefaultXMLCompletionProposalComputer {

	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer#addAttributeValueProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		
		IDOMNode node = (IDOMNode) contentAssistRequest.getNode();

		//only add attribute value proposals for specific elements
		if(node.getNodeName().equals(JSP11Namespace.ElementName.USEBEAN)) {
			// Find the attribute name for which this position should have a value
			IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
			ITextRegionList openRegions = open.getRegions();
			int i = openRegions.indexOf(contentAssistRequest.getRegion());
			if (i < 0)
				return;
			ITextRegion nameRegion = null;
			while (i >= 0) {
				nameRegion = openRegions.get(i--);
				if (nameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
					break;
			}
	
			String attributeName = null;
			if (nameRegion != null)
				attributeName = open.getText(nameRegion);
	
			String currentValue = null;
			if (contentAssistRequest.getRegion().getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)
				currentValue = contentAssistRequest.getText();
			else
				currentValue = ""; //$NON-NLS-1$
			String matchString = null;
			// fixups
			int start = contentAssistRequest.getReplacementBeginPosition();
			int length = contentAssistRequest.getReplacementLength();
			if (currentValue.length() > StringUtils.strip(currentValue).length() && (currentValue.startsWith("\"") || currentValue.startsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
						&& contentAssistRequest.getMatchString().length() > 0) {
				matchString = currentValue.substring(1, contentAssistRequest.getMatchString().length());
				start++;
				length = matchString.length();
			} else
				matchString = currentValue.substring(0, contentAssistRequest.getMatchString().length());
			boolean existingComplicatedValue = contentAssistRequest.getRegion() != null && contentAssistRequest.getRegion() instanceof ITextRegionContainer;
			if (existingComplicatedValue) {
				contentAssistRequest.getProposals().clear();
				contentAssistRequest.getMacros().clear();
			} else {
				if (attributeName.equals(JSP11Namespace.ATTR_NAME_CLASS)) {
					// class is the concrete implementation class
					IResource resource = JSPContentAssistHelper.getResource(contentAssistRequest);
					ICompletionProposal[] classProposals = JavaTypeFinder.getClassProposals(resource, start, length);
					if (classProposals != null) {
						for (int j = 0; j < classProposals.length; j++) {
							JavaTypeCompletionProposal proposal = (JavaTypeCompletionProposal) classProposals[j];
							if (matchString.length() == 0 || proposal.getQualifiedName().toLowerCase().startsWith(matchString.toLowerCase()) || proposal.getShortName().toLowerCase().startsWith(matchString.toLowerCase()))
								contentAssistRequest.addProposal(proposal);
						}
					}
				} else if (attributeName.equals(JSP11Namespace.ATTR_NAME_TYPE)) {
					// type is the more general type for the bean
					// which means it may be an interface
					IResource resource = JSPContentAssistHelper.getResource(contentAssistRequest);
					ICompletionProposal[] typeProposals = JavaTypeFinder.getTypeProposals(resource, start, length);
					if (typeProposals != null) {
						for (int j = 0; j < typeProposals.length; j++) {
							JavaTypeCompletionProposal proposal = (JavaTypeCompletionProposal) typeProposals[j];
							if (matchString.length() == 0 || proposal.getQualifiedName().toLowerCase().startsWith(matchString.toLowerCase()) || proposal.getShortName().toLowerCase().startsWith(matchString.toLowerCase()))
								contentAssistRequest.addProposal(proposal);
						}
					}
				} else if (attributeName.equals(JSP11Namespace.ATTR_NAME_BEAN_NAME)) {
					IResource resource = JSPContentAssistHelper.getResource(contentAssistRequest);
					ICompletionProposal[] beanNameProposals = JavaTypeFinder.getBeanProposals(resource, start, length);
					if (beanNameProposals != null) {
						for (int j = 0; j < beanNameProposals.length; j++) {
							if (beanNameProposals[j] instanceof CustomCompletionProposal) {
								JavaTypeCompletionProposal proposal = (JavaTypeCompletionProposal) beanNameProposals[j];
								if (matchString.length() == 0 || proposal.getDisplayString().toLowerCase().startsWith(matchString.toLowerCase()))
									contentAssistRequest.addProposal(proposal);
							} else if (beanNameProposals[j] instanceof JavaTypeCompletionProposal) {
								JavaTypeCompletionProposal proposal = (JavaTypeCompletionProposal) beanNameProposals[j];
								if (matchString.length() == 0 || proposal.getQualifiedName().toLowerCase().startsWith(matchString.toLowerCase()) || proposal.getShortName().toLowerCase().startsWith(matchString.toLowerCase()))
									contentAssistRequest.addProposal(proposal);
							}
						}
					}
				}
			}
		}
	}
}
