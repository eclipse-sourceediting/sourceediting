/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.launching.config;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;

public class LaunchAttribute
{
	public static final String TYPE_SUFFIX = ".TYPE"; //$NON-NLS-1$
	public static final String VALUE_SUFFIX = ".VALUE"; //$NON-NLS-1$

	public static final String TYPE_STRING = "string"; //$NON-NLS-1$
	public static final String TYPE_BOOLEAN = "boolean"; //$NON-NLS-1$
	public static final String TYPE_INT = "int"; //$NON-NLS-1$
	public static final String TYPE_DOUBLE = "double"; //$NON-NLS-1$
	public static final String TYPE_FLOAT = "float"; //$NON-NLS-1$
	public static final String TYPE_CLASS = "class"; //$NON-NLS-1$
	public static final String TYPE_OBJECT = "object"; //$NON-NLS-1$

	public final String uri;
	public String type;
	public String value;

	public LaunchAttribute(String uri, String type, String value)
	{
		this.uri = uri;
		this.type = type;
		this.value = value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
	
	public String getResolvedValue() throws CoreException
	{
		return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(value);
	}

	@Override
	public int hashCode()
	{
		return uri.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof LaunchAttribute)
		{
			LaunchAttribute att = (LaunchAttribute) obj;
			return att.uri.equals(uri);
		}
		return false;
	}
}