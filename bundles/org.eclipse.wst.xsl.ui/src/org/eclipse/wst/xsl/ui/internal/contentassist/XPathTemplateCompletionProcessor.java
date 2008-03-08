/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Based on org.eclipse.wst.xml.ui.internal.contentassist.XMLTemplateCompletionProcessor
 * Contributors:
 *     David Carver - STAR - bug 213849 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;


//import java.util.Comparator;
import org.eclipse.jface.text.IRegion;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
//import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.internal.templates.TemplateContextTypeIdsXPath;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;


/**
 * Completion processor for XPath Templates. Most of the work is already done by
 * the XSL Content Assist processor, so by the time the
 * XPathTemplateCompletionProcessor is asked for content assist proposals, the
 * XSL content assist processor has already set the context type for
 * templates.
 * 
 * @see org.eclipse.wst.xml.ui.
 * @since 0.5M6
 */
class XPathTemplateCompletionProcessor extends TemplateCompletionProcessor {
//	private static final class ProposalComparator implements Comparator {
//		public int compare(Object o1, Object o2) {
//			return ((TemplateProposal) o2).getRelevance() - ((TemplateProposal) o1).getRelevance();
//		}
//	}

	private String fContextTypeId = null;

	protected ICompletionProposal createProposal(Template template, TemplateContext context, IRegion region, int relevance) {
		return new CustomTemplateProposal(template, context, region, getImage(template), relevance);
	}

	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		TemplateContextType type = null;

		ContextTypeRegistry registry = getTemplateContextRegistry();
		if (registry != null) {
			type = registry.getContextType(fContextTypeId);
		}

		return type;
	}

	protected Image getImage(Template template) {
		// just return the same image for now
		Image returnImage = null;
		if (TemplateContextTypeIdsXPath.AXIS.equals(template.getContextTypeId())) {
			returnImage = XSLPluginImageHelper.getInstance().getImage(XSLPluginImages.IMG_XPATH_AXIS); 
		} else if (TemplateContextTypeIdsXPath.XPATH.equals(template.getContextTypeId())) {
			returnImage = XSLPluginImageHelper.getInstance().getImage(XSLPluginImages.IMG_XPATH_FUNCTION);
		}
		return returnImage; 
	}

	private ContextTypeRegistry getTemplateContextRegistry() {
		return XSLUIPlugin.getDefault().getTemplateContextRegistry();
	}

	protected Template[] getTemplates(String contextTypeId) {
		Template templates[] = null;

		TemplateStore store = getTemplateStore();
		if (store != null) {
			templates = store.getTemplates(contextTypeId);
		}

		return templates;
	}

	private TemplateStore getTemplateStore() {
		return XSLUIPlugin.getDefault().getTemplateStore();
	}

	void setContextType(String contextTypeId) {
		fContextTypeId = contextTypeId;
	}
}
