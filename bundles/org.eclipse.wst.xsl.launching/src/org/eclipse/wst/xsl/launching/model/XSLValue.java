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
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class XSLValue extends XSLDebugElement implements IValue
{
	private String fValue;
	private final String type;
	private boolean hasVariables;
	private XSLVariable variable;

	public XSLValue(IDebugTarget target, String type, String value)
	{
		super(target);
		this.type = type;
		if (type.equals("nodeset")) {
			hasVariables = true;
		} else {
			hasVariables = false;
		}
		if (value.equals("<EMPTY NODESET>")) {
			hasVariables = false;
		}
		fValue = value;
	}
	


	public String getReferenceTypeName() throws DebugException
	{
		return type;
	}

	public String getValueString() throws DebugException
	{
		if ("string".equals(type)) //$NON-NLS-1$
			return "'"+fValue+"'"; //$NON-NLS-1$ //$NON-NLS-2$
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
		return hasVariables; 
	}
}
