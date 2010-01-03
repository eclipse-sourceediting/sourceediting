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

public class XSLVariable extends XSLDebugElement implements IVariable
{
	public static final String LOCAL_SCOPE = "L"; //$NON-NLS-1$
	public static final String TUNNEL_SCOPE = "T"; //$NON-NLS-1$
	public static final String GLOBAL_SCOPE = "G"; //$NON-NLS-1$

	private final int id;
	private String fName;
	private String scope;

	public XSLVariable(IDebugTarget target, int id)
	{
		super(target);
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public IValue getValue() throws DebugException
	{
		return ((IXSLDebugTarget) getDebugTarget()).getVariableValue(this);
	}
	
	public void setName(String name)
	{
		this.fName = name;
	}

	public String getName() throws DebugException
	{
		return fName;
	}

	public String getReferenceTypeName() throws DebugException
	{
		if (GLOBAL_SCOPE.equals(scope))
			return "global"; //$NON-NLS-1$
		return "local"; //$NON-NLS-1$
	}

	public boolean hasValueChanged() throws DebugException
	{
		return false;
	}

	public void setValue(String expression) throws DebugException
	{
	}

	public void setValue(IValue value) throws DebugException
	{
	}

	public boolean supportsValueModification()
	{
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException
	{
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException
	{
		return false;
	}
	
	public void setScope(String scope)
	{
		this.scope = scope;
	}

	public String getScope()
	{
		return scope;
	}
	
}
