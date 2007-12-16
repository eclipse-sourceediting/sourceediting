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

public class LaunchAttribute
{
	public static final String TYPE_SUFFIX = ".TYPE";
	public static final String VALUE_SUFFIX = ".VALUE";

	public static final String TYPE_STRING = "string";
	public static final String TYPE_BOOLEAN = "boolean";
	public static final String TYPE_INT = "int";
	public static final String TYPE_DOUBLE = "double";
	public static final String TYPE_FLOAT = "float";
	public static final String TYPE_CLASS = "class";
	public static final String TYPE_OBJECT = "object";

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