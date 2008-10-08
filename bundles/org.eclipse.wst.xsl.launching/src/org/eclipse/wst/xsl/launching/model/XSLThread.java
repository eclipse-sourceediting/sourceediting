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
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

public class XSLThread extends XSLDebugElement implements IThread
{

	/**
	 * Breakpoints this thread is suspended at or <code>null</code> if none.
	 */
	private IBreakpoint[] fBreakpoints;

	/**
	 * Whether this thread is stepping
	 */
	private boolean fStepping = false;

	/**
	 * Constructs a new thread for the given target
	 */
	public XSLThread(IDebugTarget target)
	{
		super(target);
	}

	public IStackFrame[] getStackFrames() throws DebugException
	{
		if (isSuspended())
		{
			return ((IXSLDebugTarget) getDebugTarget()).getStackFrames();
		}
		else
		{
			return new IStackFrame[0];
		}
	}

	public boolean hasStackFrames() throws DebugException
	{
		return isSuspended();
	}

	public int getPriority() throws DebugException
	{
		return 0;
	}

	public IStackFrame getTopStackFrame() throws DebugException
	{
		IStackFrame[] frames = getStackFrames();
		if (frames.length > 0)
		{
			return frames[0];
		}
		return null;
	}

	public String getName() throws DebugException
	{
		return "Thread[1]"; //$NON-NLS-1$
	}

	public IBreakpoint[] getBreakpoints()
	{
		if (fBreakpoints == null)
		{
			return new IBreakpoint[0];
		}
		return fBreakpoints;
	}

	/**
	 * Sets the breakpoints this thread is suspended at, or <code>null</code>
	 * if none.
	 * 
	 * @param breakpoints
	 *            the breakpoints this thread is suspended at, or
	 *            <code>null</code> if none
	 * @since 1.0
	 */
	public void setBreakpoints(IBreakpoint[] breakpoints)
	{
		fBreakpoints = breakpoints;
	}

	public boolean canResume()
	{
		return isSuspended();
	}

	public boolean canSuspend()
	{
		return !isSuspended();
	}

	public boolean isSuspended()
	{
		return getDebugTarget().isSuspended();
	}

	public void resume() throws DebugException
	{
		getDebugTarget().resume();
	}

	public void suspend() throws DebugException
	{
		getDebugTarget().suspend();
	}

	public boolean canStepInto()
	{
		return isSuspended();
	}

	public boolean canStepOver()
	{
		return isSuspended();
	}

	public boolean canStepReturn()
	{
		return isSuspended();
	}

	public boolean isStepping()
	{
		return fStepping;
	}

	public void stepInto() throws DebugException
	{
		((IXSLDebugTarget) getDebugTarget()).stepInto();
	}

	public void stepOver() throws DebugException
	{
		((IXSLDebugTarget) getDebugTarget()).stepOver();
	}

	public void stepReturn() throws DebugException
	{
		((IXSLDebugTarget) getDebugTarget()).stepReturn();
	}

	public boolean canTerminate()
	{
		return !isTerminated();
	}

	public boolean isTerminated()
	{
		return getDebugTarget().isTerminated();
	}

	public void terminate() throws DebugException
	{
		getDebugTarget().terminate();
	}

	/**
	 * @since 1.0
	 */
	public void setStepping(boolean stepping)
	{
		fStepping = stepping;
	}
}
