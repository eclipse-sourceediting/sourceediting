/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
