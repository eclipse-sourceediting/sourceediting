/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bjorn Freeman-Benson - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.launching.model;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;
import org.eclipse.wst.xsl.internal.launching.Messages;

public class XSLLineBreakpoint extends LineBreakpoint
{
	private int lineNumber;

	public XSLLineBreakpoint()
	{
	}

	public XSLLineBreakpoint(final IResource resource, final int lineNumber, final int charStart, final int charEnd) throws CoreException
	{
		this.lineNumber = lineNumber;
		IWorkspaceRunnable runnable = new IWorkspaceRunnable()
		{
			public void run(IProgressMonitor monitor) throws CoreException
			{
				IMarker marker = resource.createMarker(IXSLConstants.MARKER_ID);
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.MESSAGE, Messages.XSLLineBreakpoint_0 + resource.getName() + " [line: " + lineNumber + "]"); //$NON-NLS-1$ //$NON-NLS-2$ 

				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IMarker.CHAR_START, Integer.valueOf(charStart));
				marker.setAttribute(IMarker.CHAR_END, Integer.valueOf(charEnd));

				register(true);
			}
		};
		run(getMarkerRule(resource), runnable);
	}

	@Override
	public int getLineNumber() throws CoreException
	{
		int line = super.getLineNumber();
		return line == -1 ? lineNumber : line;
	}

	protected void register(boolean register) throws CoreException
	{
		DebugPlugin plugin = DebugPlugin.getDefault();
		if (plugin != null && register)
		{
			plugin.getBreakpointManager().addBreakpoint(this);
		}
		else
		{
			setRegistered(false);
		}
	}

	public String getModelIdentifier()
	{
		return IXSLConstants.ID_XSL_DEBUG_MODEL;
	}
}
