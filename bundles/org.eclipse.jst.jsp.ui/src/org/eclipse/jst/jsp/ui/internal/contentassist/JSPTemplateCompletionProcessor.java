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

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.jsp.ui.JSPEditorPlugin;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImages;
import org.eclipse.jst.jsp.ui.templates.TemplateContextTypeJSP;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.ui.contentassist.AbstractTemplateCompletionProcessor;

/**
 * Completion processor for JSP Templates
 */
public class JSPTemplateCompletionProcessor extends AbstractTemplateCompletionProcessor {

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.contentassist.AbstractTemplateCompletionProcessor#getTemplateStore()
	 */
	protected TemplateStore getTemplateStore() {
		return getJSPEditorPlugin().getTemplateStore();
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.contentassist.AbstractTemplateCompletionProcessor#getTemplateContextRegistry()
	 */
	protected ContextTypeRegistry getTemplateContextRegistry() {
		return getJSPEditorPlugin().getTemplateContextRegistry();
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.contentassist.AbstractTemplateCompletionProcessor#getContextTypeId()
	 */
	protected String getContextTypeId() {
		// turn the context type id into content type specific
		return TemplateContextTypeJSP.generateContextTypeId(super.getContextTypeId());
	}
	
	/**
	 * Returns the JSPEditorPlugin
	 * @return JSPEditorPlugin
	 */
	private JSPEditorPlugin getJSPEditorPlugin() {
		return (JSPEditorPlugin) Platform.getPlugin(JSPEditorPlugin.ID);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getImage(org.eclipse.jface.text.templates.Template)
	 */
	protected Image getImage(Template template) {
		// just return the same image for now
		return JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP);
	}
}
