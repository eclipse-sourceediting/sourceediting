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
package org.eclipse.wst.xml.ui.templates;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.wst.xml.ui.nls.ResourceHandler;


/**
 * Base class for XML template context types. Templates of this context type
 * apply to any place within XML content type.
 */
public class TemplateContextTypeXML extends TemplateContextType {
	public static final String XML_PREFIX = "xml_"; //$NON-NLS-1$

	/**
	 * Generate a context type id that includes content type
	 * 
	 * @param base_contextTypeId
	 * @return String
	 */
	public static String generateContextTypeId(String base_contextTypeId) {
		return XML_PREFIX + base_contextTypeId;
	}

	public TemplateContextTypeXML() {
		this(generateContextTypeId(TemplateContextTypeIds.ALL), ResourceHandler.getString("TemplateContextTypeXML.0")); //$NON-NLS-1$
	}

	/**
	 * @param id
	 * @param name
	 */
	public TemplateContextTypeXML(String id, String name) {
		super(id, name);
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.Year());
	}
}
