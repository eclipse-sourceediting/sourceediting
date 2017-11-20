/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (Intalio) - bug 289498 - Added additional context types.
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.templates;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.wst.xml.ui.internal.templates.EncodingTemplateVariableResolverXML;

public class TemplateContextTypeXSL extends TemplateContextType
{
	public static final String XSL_TAG = "xsl_tag"; //$NON-NLS-1$
	public static final String XSL_ATTR = "xsl_attr"; //$NON-NLS-1$
	public static final String XSL_NEW = "xsl_new"; //$NON-NLS-1$
	
	
	public TemplateContextTypeXSL()
	{
		super();
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.Year());
		addResolver(new EncodingTemplateVariableResolverXML());
		addResolver(new VersionTemplateVariableResolverXSL());
	}
}
