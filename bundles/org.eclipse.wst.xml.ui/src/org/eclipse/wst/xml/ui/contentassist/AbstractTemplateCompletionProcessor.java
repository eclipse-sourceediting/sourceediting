/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.contentassist;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;


/**
 * A completion processor that computes template proposals and works with
 * AbstractContentAssistProcessor
 */
abstract public class AbstractTemplateCompletionProcessor extends TemplateCompletionProcessor {
	private String fContextTypeId = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#createProposal(org.eclipse.jface.text.templates.Template,
	 *      org.eclipse.jface.text.templates.TemplateContext,
	 *      org.eclipse.jface.text.Region, int)
	 */
	protected ICompletionProposal createProposal(Template template, TemplateContext context, Region region, int relevance) {
		// CustomTemplateProposal turns the additional information to content
		// fit for HTML
		return new CustomTemplateProposal(template, context, region, getImage(template), relevance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getContextType(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 */
	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		// another completion processor should have already set the current
		// context type id, so just use that value instead of trying to
		// determine
		// the context type again
		if (getTemplateContextRegistry() != null) {
			return getTemplateContextRegistry().getContextType(getContextTypeId());
		}
		return null;
	}

	protected String getContextTypeId() {
		return fContextTypeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getImage(org.eclipse.jface.text.templates.Template)
	 */
	protected Image getImage(Template template) {
		// just return the same image for now
		return XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_TAG_MACRO);
	}

	/**
	 * Return the template context registry to use with this completion
	 * processor
	 * 
	 * @return ContextTypeRegistry
	 */
	abstract protected ContextTypeRegistry getTemplateContextRegistry();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getTemplates(java.lang.String)
	 */
	protected Template[] getTemplates(String contextTypeId) {
		if (getTemplateStore() != null) {
			return getTemplateStore().getTemplates(contextTypeId);
		}
		return null;
	}

	/**
	 * Return the template store to use with this template completion
	 * processor
	 * 
	 * @return TemplateStore
	 */
	abstract protected TemplateStore getTemplateStore();

	/**
	 * Set the current context type to use when determining completion
	 * proposals.
	 * 
	 * @param contextTypeId
	 */
	public void setContextType(String contextTypeId) {
		fContextTypeId = contextTypeId;
	}
}
