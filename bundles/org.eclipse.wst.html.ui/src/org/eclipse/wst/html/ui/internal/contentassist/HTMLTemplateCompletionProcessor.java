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
package org.eclipse.wst.html.ui.internal.contentassist;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.html.ui.HTMLEditorPlugin;
import org.eclipse.wst.html.ui.templates.TemplateContextTypeHTML;
import org.eclipse.wst.xml.ui.contentassist.AbstractTemplateCompletionProcessor;


/**
 * Completion processor for HTML Templates
 */
public class HTMLTemplateCompletionProcessor extends AbstractTemplateCompletionProcessor {
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.contentassist.AbstractTemplateCompletionProcessor#getTemplateStore()
	 */
	protected TemplateStore getTemplateStore() {
		return getHTMLEditorPlugin().getTemplateStore();
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.contentassist.AbstractTemplateCompletionProcessor#getTemplateContextRegistry()
	 */
	protected ContextTypeRegistry getTemplateContextRegistry() {
		return getHTMLEditorPlugin().getTemplateContextRegistry();
	}
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.contentassist.AbstractTemplateCompletionProcessor#getContextTypeId()
	 */
	protected String getContextTypeId() {
		// turn the context type id into content type specific
		return TemplateContextTypeHTML.generateContextTypeId(super.getContextTypeId());
	}
	
	/**
	 * Returns the HTMLEditorPlugin
	 * @return HTMLEditorPlugin
	 */
	private HTMLEditorPlugin getHTMLEditorPlugin() {
		return (HTMLEditorPlugin) Platform.getPlugin(HTMLEditorPlugin.ID);
	}
}
