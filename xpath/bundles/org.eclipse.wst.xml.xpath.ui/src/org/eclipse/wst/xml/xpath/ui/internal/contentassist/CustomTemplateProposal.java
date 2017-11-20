/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API based off wst.xml.ui.CustomTemplateProposal
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.contentassist;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceCompletionProposal;

/**
 * Purpose of this class is to make the additional proposal info into content
 * fit for an HTML viewer (by escaping characters)
 */
@SuppressWarnings("restriction")
public class CustomTemplateProposal extends TemplateProposal implements
		IRelevanceCompletionProposal {

	private final Template fTemplate;

	public CustomTemplateProposal(Template template, TemplateContext context,
			IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
		this.fTemplate = template;
	}

	@Override
	public String getDisplayString() {
		return fTemplate.getName();
	}

	@Override
	public String getAdditionalProposalInfo() {
		return StringUtils.convertToHTMLContent(fTemplate.getDescription());
	}
}
