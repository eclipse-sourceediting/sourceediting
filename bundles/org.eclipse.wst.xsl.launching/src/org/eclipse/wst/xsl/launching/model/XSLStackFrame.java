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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.eclipse.wst.xsl.internal.launching.Messages;

/**
 * XSL stack frame.
 */
public class XSLStackFrame extends XSLDebugElement implements IStackFrame
{
	private final XSLThread xslThread;
	private int id;
	private String name;
	private int lineNumber;
	private String xslFileName;
	private IVariable[] variables;

	public XSLStackFrame(XSLThread thread, String data, int index)
	{
		super(thread.getDebugTarget());
		this.xslThread = thread;
		init(data,(IXSLDebugTarget) thread.getDebugTarget());
	}

	private void init(String data,IXSLDebugTarget debugTarget)
	{

		String[] strings = data.split("\\|"); //$NON-NLS-1$
		String fileName = strings[0];
		try
		{
			URL url = new URL(fileName);
			Path p = new Path(url.getFile());
			xslFileName = (new Path(fileName)).lastSegment();

			String idString = strings[1];
			id = Integer.parseInt(idString);
			String pc = strings[2];
			lineNumber = Integer.parseInt(pc);
			String safename = strings[3];

			int theIndex;
			while ((theIndex = safename.indexOf("%@_PIPE_@%")) != -1) //$NON-NLS-1$
			{
				safename = safename.substring(0, theIndex) + "|" + safename.substring(theIndex + "%@_PIPE_@%".length(), safename.length()); //$NON-NLS-1$ //$NON-NLS-2$
			}

			name = p.lastSegment() + " " + safename; //$NON-NLS-1$
			
			variables = new XSLVariable[strings.length-4];
			for (int i=0;i<variables.length;i++)
			{
				int varId = Integer.parseInt(strings[i+4]);
				try
				{
					XSLVariable var = debugTarget.getVariable(varId);
					variables[i] = var;
				}
				catch (DebugException e)
				{
					LaunchingPlugin.log(e);
				}
			}
		}
		catch (MalformedURLException e)
		{
			LaunchingPlugin.log(e);
		}
	}

	public IThread getThread()
	{
		return xslThread;
	}

	public IVariable[] getVariables() throws DebugException
	{
		return variables;
	}

	public boolean hasVariables() throws DebugException
	{
		return variables.length > 0;
	}

	public int getLineNumber() throws DebugException
	{
		return lineNumber;
	}

	public int getCharStart() throws DebugException
	{
		return -1;
	}

	public int getCharEnd() throws DebugException
	{
		return -1;
	}

	public String getName() throws DebugException
	{
		return name + Messages.XSLStackFrame_5 + lineNumber; 
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException
	{
		return null;
	}

	public boolean hasRegisterGroups() throws DebugException
	{
		return false;
	}

	public boolean canStepInto()
	{
		return getThread().canStepInto();
	}

	public boolean canStepOver()
	{
		return getThread().canStepOver();
	}

	public boolean canStepReturn()
	{
		return getThread().canStepReturn();
	}

	public boolean isStepping()
	{
		return getThread().isStepping();
	}

	public void stepInto() throws DebugException
	{
		getThread().stepInto();
	}

	public void stepOver() throws DebugException
	{
		getThread().stepOver();
	}

	public void stepReturn() throws DebugException
	{
		getThread().stepReturn();
	}

	public boolean canResume()
	{
		return getThread().canResume();
	}

	public boolean canSuspend()
	{
		return getThread().canSuspend();
	}

	public boolean isSuspended()
	{
		return getThread().isSuspended();
	}

	public void resume() throws DebugException
	{
		getThread().resume();
	}

	public void suspend() throws DebugException
	{
		getThread().suspend();
	}

	public boolean canTerminate()
	{
		return getThread().canTerminate();
	}

	public boolean isTerminated()
	{
		return getThread().isTerminated();
	}

	public void terminate() throws DebugException
	{
		getThread().terminate();
	}

	/**
	 * Returns the name of the source file this stack frame is associated with.
	 */
	public String getSourceName()
	{
		return xslFileName;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof XSLStackFrame)
		{
			XSLStackFrame sf = (XSLStackFrame) obj;
			return sf.id == id;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getSourceName().hashCode() + id;
	}

	protected int getIdentifier()
	{
		return id;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	public void setVariables(IVariable[] variables)
	{
		this.variables = variables;
	}
}
