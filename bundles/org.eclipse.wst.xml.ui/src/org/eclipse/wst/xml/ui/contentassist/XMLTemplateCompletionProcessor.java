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

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.xml.ui.XMLEditorPlugin;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeXML;


/**
 * Completion processor for XML Templates
 */
public class XMLTemplateCompletionProcessor extends AbstractTemplateCompletionProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.ui.contentassist.AbstractTemplateCompletionProcessor#getContextTypeId()
	 */
	protected String getContextTypeId() {
		// turn the context type id into content type specific
		return TemplateContextTypeXML.generateContextTypeId(super.getContextTypeId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.ui.contentassist.AbstractTemplateCompletionProcessor#getTemplateContextRegistry()
	 */
	protected ContextTypeRegistry getTemplateContextRegistry() {
		return getXMLEditorPlugin().getTemplateContextRegistry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.ui.contentassist.AbstractTemplateCompletionProcessor#getTemplateStore()
	 */
	protected TemplateStore getTemplateStore() {
		return getXMLEditorPlugin().getTemplateStore();
	}

	/**
	 * Returns the XMLEditorPlugin
	 * 
	 * @return XMLEditorPlugin
	 */
	private XMLEditorPlugin getXMLEditorPlugin() {
		return (XMLEditorPlugin) Platform.getPlugin(XMLEditorPlugin.ID);
	}
}
