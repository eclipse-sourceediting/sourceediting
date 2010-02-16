/*******************************************************************************
 * Copyright (c) 2010 Intalio and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (Intalio) - bug 289498 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.wst.xsl.ui.internal.templates.TemplateContextTypeXSL;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXSLContentAssistProcessor;

public class XSLTemplateContentAssistProcessor extends
		AbstractXSLContentAssistProcessor implements IContentAssistProcessor {

	public XSLTemplateContentAssistProcessor() {
		
	}

	public ArrayList<String> getNamespaces() {
		return null;
	}

	public String getMinimumVersion() {
		return null;
	}

	public String getMaximumVersion() {
		return null;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		XSLTemplateCompletionProcessor templates = new XSLTemplateCompletionProcessor();
		
		if (viewer.getDocument().getLength() == 0) {
			templates.setContextType(TemplateContextTypeXSL.XSL_NEW);
			ICompletionProposal[] newFileTemplates = templates.computeCompletionProposals(viewer, offset);
			if (newFileTemplates != null && newFileTemplates.length > 0) {
				proposals.addAll(Arrays.asList(newFileTemplates));
			}
		}
		
		templates.setContextType(TemplateContextTypeXSL.XSL_TAG);
		ICompletionProposal[] xsltagproposals = templates.computeCompletionProposals(viewer, offset);
		if (xsltagproposals != null && xsltagproposals.length > 0) {
			proposals.addAll(Arrays.asList(xsltagproposals));
		}

		templates.setContextType(TemplateContextTypeXSL.XSL_ATTR);
		ICompletionProposal[] xslattrproposals = templates.computeCompletionProposals(viewer, offset);
		if (xslattrproposals != null && xslattrproposals.length > 0) {
			proposals.addAll(Arrays.asList(xslattrproposals));
		}
		
		ICompletionProposal[] allProposals = new ICompletionProposal[proposals.size()];
		if (proposals.size() > 0) {
			proposals.toArray(allProposals);
		} else {
			return null;
		}
		return allProposals;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

}
