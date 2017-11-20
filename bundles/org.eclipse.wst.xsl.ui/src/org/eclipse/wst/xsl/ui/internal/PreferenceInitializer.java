/*******************************************************************************
 * Copyright (c) 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	private static final String ORG_ECLIPSE_WST_XSLT_TEMPLATES_XSL_BASIC = "org.eclipse.wst.xslt.templates.xsl_basic"; //$NON-NLS-1$

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = new DefaultScope().getNode(XSLUIPlugin.getDefault().getBundle().getSymbolicName());
		node.put(XSLUIConstants.NEW_FILE_TEMPLATE_NAME, ORG_ECLIPSE_WST_XSLT_TEMPLATES_XSL_BASIC); // default to the basic stylesheet
	}
}
