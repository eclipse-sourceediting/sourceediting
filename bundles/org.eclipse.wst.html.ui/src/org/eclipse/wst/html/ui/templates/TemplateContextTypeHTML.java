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
package org.eclipse.wst.html.ui.templates;

import org.eclipse.wst.html.ui.internal.nls.ResourceHandler;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeIds;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeXML;


/**
 * Base class for HTML template context types.  Templates of this context
 * type apply to any place within HTML content type.
 */
public class TemplateContextTypeHTML extends TemplateContextTypeXML {
	public static final String HTML_PREFIX = "html_"; //$NON-NLS-1$
	
	public TemplateContextTypeHTML() {
		this(generateContextTypeId(TemplateContextTypeIds.ALL), ResourceHandler.getString("TemplateContextTypeHTML.0")); //$NON-NLS-1$
	}
	
	/**
	 * @param id
	 */
	public TemplateContextTypeHTML(String id) {
		this(id, null);
	}

	/**
	 * @param id
	 * @param name
	 */
	public TemplateContextTypeHTML(String id, String name) {
		super(id, name);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.templates.TemplateContextTypeXML#generateContextTypeId(java.lang.String)
	 */
	public static String generateContextTypeId(String base_contextTypeId) {
		return HTML_PREFIX+base_contextTypeId;
	}
}
