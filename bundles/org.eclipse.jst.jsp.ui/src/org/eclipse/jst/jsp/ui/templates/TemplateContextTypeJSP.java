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
package org.eclipse.jst.jsp.ui.templates;

import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeIds;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeXML;

/**
 * Base class for JSP template context types.  Templates of this context
 * type apply to any place within JSP content type.
 */
public class TemplateContextTypeJSP extends TemplateContextTypeXML {
	public static final String JSP_PREFIX = "jsp_"; //$NON-NLS-1$

	public TemplateContextTypeJSP() {
		this(generateContextTypeId(TemplateContextTypeIds.ALL), JSPUIPlugin.getResourceString("%TemplateContextTypeJSP.0")); //$NON-NLS-1$
	}

	/**
	 * @param id
	 * @param name
	 */
	public TemplateContextTypeJSP(String id, String name) {
		super(id, name);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see com.ibm.sse.editor.xml.templates.TemplateContextTypeXML#generateContextTypeId(java.lang.String)
	 */
	public static String generateContextTypeId(String base_contextTypeId) {
		return JSP_PREFIX+base_contextTypeId;
	}
}
