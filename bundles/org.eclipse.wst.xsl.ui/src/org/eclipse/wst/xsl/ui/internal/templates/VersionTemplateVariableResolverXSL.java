/*******************************************************************************
 * Copyright (c) 2008, 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - externalize strings.
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.templates;

import org.eclipse.jface.text.templates.SimpleTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.wst.xsl.ui.internal.Messages;

public class VersionTemplateVariableResolverXSL extends SimpleTemplateVariableResolver
{
	private static final String XSLT_VERSION_1_0 = "1.0"; //$NON-NLS-1$
	private static final String VERSION_TYPE = getVersionType();

	private static String getVersionType()
	{
		return "xsl_version"; //$NON-NLS-1$
	}

	public VersionTemplateVariableResolverXSL()
	{
		super(VERSION_TYPE, Messages.VersionTemplateVariableResolverPageDescription);
	}

	@Override
	protected String resolve(TemplateContext context)
	{
		return XSLT_VERSION_1_0;
	}
}
