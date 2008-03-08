/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 213849 - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.internal.contentassist;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceCompletionProposal;

/**
 * Purpose of this class is to make the additional proposal info into content
 * fit for an HTML viewer (by escaping characters)
 */
@SuppressWarnings("restriction")
class CustomTemplateProposal extends TemplateProposal implements IRelevanceCompletionProposal {
	// copies of this class exist in:
	// org.eclipse.jst.jsp.ui.internal.contentassist
	// org.eclipse.wst.html.ui.internal.contentassist
	// org.eclipse.wst.xml.ui.internal.contentassist
	// org.eclipse.wst.xsl.ui.internal.contentassist

	private String fDisplayString = null;
	private final Template fTemplate;

	
	public CustomTemplateProposal(Template template, TemplateContext context, IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
		
			Assert.isNotNull(template);
			Assert.isNotNull(context);
			Assert.isNotNull(region);

			fTemplate= template;
			fDisplayString= null;
	}

	public String getAdditionalProposalInfo() {
		String additionalInfo = "Description:\r\n" + fTemplate.getDescription(); 
		return additionalInfo;
	}
	
	/*
	 * @see ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		if (fDisplayString == null) {
			fDisplayString = fTemplate.getName();
		}
		return fDisplayString;
	}
	
}
