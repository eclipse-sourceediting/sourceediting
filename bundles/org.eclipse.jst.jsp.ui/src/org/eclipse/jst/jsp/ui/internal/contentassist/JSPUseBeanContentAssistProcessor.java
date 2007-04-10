/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;

/**
 * @plannedfor 1.0
 */
public class JSPUseBeanContentAssistProcessor extends JSPDummyContentAssistProcessor {

	public JSPUseBeanContentAssistProcessor() {
		super();
	}

	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {

		IDOMNode node = (IDOMNode) contentAssistRequest.getNode();

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
				IResource resource = getResource(contentAssistRequest);
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
				IResource resource = getResource(contentAssistRequest);
				ICompletionProposal[] typeProposals = JavaTypeFinder.getTypeProposals(resource, start, length);
				if (typeProposals != null) {
					for (int j = 0; j < typeProposals.length; j++) {
						JavaTypeCompletionProposal proposal = (JavaTypeCompletionProposal) typeProposals[j];
						if (matchString.length() == 0 || proposal.getQualifiedName().toLowerCase().startsWith(matchString.toLowerCase()) || proposal.getShortName().toLowerCase().startsWith(matchString.toLowerCase()))
							contentAssistRequest.addProposal(proposal);
					}
				}
			} else if (attributeName.equals(JSP11Namespace.ATTR_NAME_BEAN_NAME)) {
				IResource resource = getResource(contentAssistRequest);
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

	/**
	 * Returns project request is in
	 * 
	 * @param request
	 * @return
	 */
	private IResource getResource(ContentAssistRequest request) {
		IResource resource = null;
		String baselocation = null;

		if (request != null) {
			IStructuredDocumentRegion region = request.getDocumentRegion();
			if (region != null) {
				IDocument document = region.getParentDocument();
				IStructuredModel model = null;
				try {
					model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
					if (model != null) {
						baselocation = model.getBaseLocation();
					}
				} finally {
					if (model != null)
						model.releaseFromRead();
				}
			}
		}

		if (baselocation != null) {
			// copied from JSPTranslationAdapter#getJavaProject
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baselocation);
			IFile file = null;

			if (filePath.segmentCount() > 1) {
				file = root.getFile(filePath);
			}
			if (file != null) {
				resource = file.getProject();
			}
		}
		return resource;
	}

}
