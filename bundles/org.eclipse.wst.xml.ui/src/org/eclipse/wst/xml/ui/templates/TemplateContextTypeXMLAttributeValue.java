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

import org.eclipse.wst.xml.ui.nls.ResourceHandler;

/**
 * Templates of this context type apply to any attribute values within XML
 * content type.
 */
public class TemplateContextTypeXMLAttributeValue extends TemplateContextTypeXML {

	public TemplateContextTypeXMLAttributeValue() {
		super(generateContextTypeId(TemplateContextTypeIds.ATTRIBUTEVALUE), ResourceHandler.getString("TemplateContextTypeXMLAttributeValue.0")); //$NON-NLS-1$
	}
}
