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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;

public abstract class XSLDebugElement extends PlatformObject implements IDebugElement
{
	protected XSLDebugTarget debugTarget;

	public XSLDebugElement(XSLDebugTarget target)
	{
		debugTarget = target;
	}

	public String getModelIdentifier()
	{
		return IXSLConstants.ID_XSL_DEBUG_MODEL;
	}

	public IDebugTarget getDebugTarget()
	{
		return debugTarget;
	}

	public ILaunch getLaunch()
	{
		return getDebugTarget().getLaunch();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (adapter == IDebugElement.class)
		{
			return this;
		}
		else if (adapter == ILaunch.class)
		{
			return getLaunch();
		}
		return super.getAdapter(adapter);
	}

	protected void abort(String message, Throwable e) throws DebugException
	{
		if (!getDebugTarget().isTerminated())
			getDebugTarget().getProcess().terminate();
		throw new DebugException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, message, e));
	}

	/**
	 * Fires a debug event
	 * 
	 * @param event
	 *            the event to be fired
	 */
	protected void fireEvent(DebugEvent event)
	{
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[]
		{ event });
	}

	/**
	 * Fires a <code>CREATE</code> event for this element.
	 */
	protected void fireCreationEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	/**
	 * Fires a <code>RESUME</code> event for this element with the given
	 * detail.
	 * 
	 * @param detail
	 *            event detail code
	 */
	public void fireResumeEvent(int detail)
	{
		fireEvent(new DebugEvent(this, DebugEvent.RESUME, detail));
	}

	/**
	 * Fires a <code>SUSPEND</code> event for this element with the given
	 * detail.
	 * 
	 * @param detail
	 *            event detail code
	 */
	public void fireSuspendEvent(int detail)
	{
		fireEvent(new DebugEvent(this, DebugEvent.SUSPEND, detail));
	}

	/**
	 * Fires a <code>TERMINATE</code> event for this element.
	 */
	protected void fireTerminateEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}
}
