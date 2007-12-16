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
package org.eclipse.wst.xsl.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.wst.xsl.launching.IStylesheetParameter;

public class StylesheetParameter implements IStylesheetParameter
{
	private final String name;
	private final String value;
	private final int type;

	public StylesheetParameter(String name, String value, int type)
	{
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public int getType()
	{
		return type;
	}

	public String getValue()
	{
		return value;
	}

	public String getResolvedValue() throws CoreException
	{
		if (type == IStylesheetParameter.CONSTANT_TYPE)
			return getValue();
		return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(getValue());
	}
}
