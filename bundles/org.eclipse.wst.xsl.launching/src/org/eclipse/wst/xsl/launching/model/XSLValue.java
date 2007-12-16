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
package org.eclipse.wst.xsl.launching.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class XSLValue extends XSLDebugElement implements IValue
{

	private final String fValue;

	public XSLValue(XSLDebugTarget target, String value)
	{
		super(target);
		fValue = value;
	}

	public String getReferenceTypeName() throws DebugException
	{
		try
		{
			Integer.parseInt(fValue);
		}
		catch (NumberFormatException e)
		{
			return "text";
		}
		return "integer";
	}

	public String getValueString() throws DebugException
	{
		return fValue;
	}

	public boolean isAllocated() throws DebugException
	{
		return true;
	}

	public IVariable[] getVariables() throws DebugException
	{
		return new IVariable[0];
	}

	public boolean hasVariables() throws DebugException
	{
		return false;
	}
}
