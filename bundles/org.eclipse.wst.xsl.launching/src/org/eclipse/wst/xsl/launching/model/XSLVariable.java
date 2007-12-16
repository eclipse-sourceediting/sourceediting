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

public class XSLVariable extends XSLDebugElement implements IVariable
{
	public static final String LOCAL_SCOPE = "L";
	public static final String TUNNEL_SCOPE = "T";
	public static final String GLOBAL_SCOPE = "G";

	private final String fName;
	private final XSLStackFrame fFrame;
	private final int slotNumber;
	private final String scope;

	public XSLVariable(XSLStackFrame frame, String scope, String name, int slotNumber)
	{
		super((XSLDebugTarget) frame.getDebugTarget());
		fFrame = frame;
		fName = name;
		this.scope = scope;
		this.slotNumber = slotNumber;
	}

	public int getSlotNumber()
	{
		return slotNumber;
	}

	public IValue getValue() throws DebugException
	{
		return ((XSLDebugTarget) getDebugTarget()).getVariableValue(this);
	}

	public String getName() throws DebugException
	{
		return fName;
	}

	public String getReferenceTypeName() throws DebugException
	{
		// TODO getReferenceTypeName
		return "Thing";
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

	/**
	 * Returns the stack frame owning this variable.
	 * 
	 * @return the stack frame owning this variable
	 */
	protected XSLStackFrame getStackFrame()
	{
		return fFrame;
	}

	public String getScope()
	{
		return scope;
	}

}
