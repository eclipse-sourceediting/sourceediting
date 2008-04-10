/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.templates;

import org.eclipse.jface.text.templates.SimpleTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;

public class VersionTemplateVariableResolverXSL extends SimpleTemplateVariableResolver
{
	private static final String VERSION_TYPE = getVersionType();

	private static String getVersionType()
	{
		return "xsl_version"; //$NON-NLS-1$
	}

	public VersionTemplateVariableResolverXSL()
	{
		super(VERSION_TYPE, "XSLT Version preference");
	}

	protected String resolve(TemplateContext context)
	{
		// TODO
		return "1.0";
		// return
		// XMLCorePlugin.getDefault().getPluginPreferences().getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);
	}
}
