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

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImages;
import org.eclipse.swt.graphics.Image;

/**
 * Completion processor for JSP Templates. Most of the work is already done by
 * the JSP Content Assist processor, so by the time the
 * JSPTemplateCompletionProcessor is asked for content assist proposals, the
 * jsp content assist processor has already set the context type for
 * templates.
 * 
 * @since 1.0
 */
class JSPTemplateCompletionProcessor extends TemplateCompletionProcessor {
	private String fContextTypeId = null;

	protected ICompletionProposal createProposal(Template template, TemplateContext context, IRegion region, int relevance) {
		return new CustomTemplateProposal(template, context, region, getImage(template), relevance);
	}

	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		TemplateContextType type = null;

		ContextTypeRegistry registry = getTemplateContextRegistry();
		if (registry != null)
			type = registry.getContextType(fContextTypeId);

		return type;
	}

	protected Image getImage(Template template) {
		// just return the same image for now
		return JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP);
	}

	private ContextTypeRegistry getTemplateContextRegistry() {
		return JSPUIPlugin.getDefault().getTemplateContextRegistry();
	}

	protected Template[] getTemplates(String contextTypeId) {
		Template templates[] = null;

		TemplateStore store = getTemplateStore();
		if (store != null)
			templates = store.getTemplates(contextTypeId);

		return templates;
	}

	private TemplateStore getTemplateStore() {
		return JSPUIPlugin.getDefault().getTemplateStore();
	}

	void setContextType(String contextTypeId) {
		fContextTypeId = contextTypeId;
	}
}
