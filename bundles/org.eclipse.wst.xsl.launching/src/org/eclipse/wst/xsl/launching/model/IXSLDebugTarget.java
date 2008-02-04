package org.eclipse.wst.xsl.launching.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IValue;

public interface IXSLDebugTarget extends IDebugTarget
{
	XSLVariable getVariable(int varId) throws DebugException;

	IStackFrame[] getStackFrames() throws DebugException;

	void stepInto() throws DebugException;

	void stepOver() throws DebugException;

	void stepReturn() throws DebugException;

	IValue getVariableValue(XSLVariable variable) throws DebugException;	
}
