/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;



import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jst.jsp.core.JSP11Namespace;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.contentassist.ContentAssistRequest;

public class JSPUseBeanContentAssistProcessor extends JSPDummyContentAssistProcessor {

	public JSPUseBeanContentAssistProcessor() {
		super();
	}

	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {

		XMLNode node = (XMLNode) contentAssistRequest.getNode();

		// Find the attribute name for which this position should have a value
		IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
		ITextRegionList openRegions = open.getRegions();
		int i = openRegions.indexOf(contentAssistRequest.getRegion());
		if (i < 0)
			return;
		ITextRegion nameRegion = null;
		while (i >= 0) {
			nameRegion = openRegions.get(i--);
			if (nameRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME)
				break;
		}

		String attributeName = null;
		if (nameRegion != null)
			attributeName = open.getText(nameRegion);

		String currentValue = null;
		if (contentAssistRequest.getRegion().getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE)
			currentValue = contentAssistRequest.getText();
		else
			currentValue = ""; //$NON-NLS-1$
		String matchString = null;
		// fixups
		if (currentValue.length() > StringUtils.strip(currentValue).length() && (currentValue.startsWith("\"") || currentValue.startsWith("'")) && contentAssistRequest.getMatchString().length() > 0) //$NON-NLS-1$ //$NON-NLS-2$
			matchString = currentValue.substring(1, contentAssistRequest.getMatchString().length());
		else
			matchString = currentValue.substring(0, contentAssistRequest.getMatchString().length());
		boolean existingComplicatedValue = contentAssistRequest.getRegion() != null && contentAssistRequest.getRegion() instanceof ITextRegionContainer;
		if (existingComplicatedValue) {
			contentAssistRequest.getProposals().clear();
			contentAssistRequest.getMacros().clear();
		}
		else {
			if (attributeName.equals(JSP11Namespace.ATTR_NAME_CLASS)) {
				ICompletionProposal[] classProposals = JavaTypeFinder.getClassProposals(fResource, contentAssistRequest.getReplacementBeginPosition(), contentAssistRequest.getReplacementLength());
				if (classProposals != null) {
					for (int j = 0; j < classProposals.length; j++) {
                        CustomCompletionProposal proposal = (CustomCompletionProposal) classProposals[j];
						//if (matchString.length() == 0 || proposal.getQualifiedName().toLowerCase().startsWith(matchString.toLowerCase()) || proposal.getShortName().toLowerCase().startsWith(matchString.toLowerCase()))
							contentAssistRequest.addProposal(proposal);
					}
				}
			}
			else if (attributeName.equals(JSP11Namespace.ATTR_NAME_TYPE)) {
				ICompletionProposal[] typeProposals = JavaTypeFinder.getTypeProposals(fResource, contentAssistRequest.getReplacementBeginPosition(), contentAssistRequest.getReplacementLength());
				if (typeProposals != null) {
					for (int j = 0; j < typeProposals.length; j++) {
                        CustomCompletionProposal proposal = (CustomCompletionProposal) typeProposals[j];
						//if (matchString.length() == 0 || proposal.getQualifiedName().toLowerCase().startsWith(matchString.toLowerCase()) || proposal.getShortName().toLowerCase().startsWith(matchString.toLowerCase()))
							contentAssistRequest.addProposal(proposal);
					}
				}
			}
			else if (attributeName.equals(JSP11Namespace.ATTR_NAME_BEAN_NAME)) {
				ICompletionProposal[] beanNameProposals = JavaTypeFinder.getBeanProposals(fResource, contentAssistRequest.getReplacementBeginPosition(), contentAssistRequest.getReplacementLength());
				if (beanNameProposals != null) {
					for (int j = 0; j < beanNameProposals.length; j++) {
						if (beanNameProposals[j] instanceof CustomCompletionProposal) {
							CustomCompletionProposal proposal = (CustomCompletionProposal) beanNameProposals[j];
							//if (matchString.length() == 0 || proposal.getDisplayString().toLowerCase().startsWith(matchString.toLowerCase()))
								contentAssistRequest.addProposal(proposal);
						}
						else if (beanNameProposals[j] instanceof JavaTypeCompletionProposal) {
                            CustomCompletionProposal proposal = (CustomCompletionProposal) beanNameProposals[j];
							//if (matchString.length() == 0 || proposal.getQualifiedName().toLowerCase().startsWith(matchString.toLowerCase()) || proposal.getShortName().toLowerCase().startsWith(matchString.toLowerCase()))
								contentAssistRequest.addProposal(proposal);
						}
					}
				}
			}
		}
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.IContentAssistProcessorExtension#release()
	 */
	public void release() {
		fResource = null;
	}

}