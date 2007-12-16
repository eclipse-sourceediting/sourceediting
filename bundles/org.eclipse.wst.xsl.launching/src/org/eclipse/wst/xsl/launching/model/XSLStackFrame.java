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

/**
 * XSL stack frame.
 */
public class XSLStackFrame extends XSLDebugElement implements IStackFrame
{

	private final int id;
	private final XSLThread xslThread;
	private String name;
	private int lineNumber;
	private String xslFileName;
	private IVariable[] variables;

	public XSLStackFrame(XSLThread thread, String data, int id)
	{
		super((XSLDebugTarget) thread.getDebugTarget());
		this.id = id;
		this.xslThread = thread;
		init(data);
	}

	private void init(String data)
	{

		String[] strings = data.split("\\|");
		String fileName = strings[0];
		try
		{
			URL url = new URL(fileName);
			Path p = new Path(url.getFile());
			xslFileName = (new Path(fileName)).lastSegment();

			String pc = strings[1];
			lineNumber = Integer.parseInt(pc);
			String safename = strings[2];

			int theIndex;
			while ((theIndex = safename.indexOf("%@_PIPE_@%")) != -1)
			{
				safename = safename.substring(0, theIndex) + "|" + safename.substring(theIndex + "%@_PIPE_@%".length(), safename.length());
			}

			name = p.lastSegment() + " " + safename + " line: " + lineNumber;

			int numVars = strings.length - 3;
			variables = new IVariable[numVars];
			for (int i = 0; i < numVars; i++)
			{
				String val = strings[i + 3];
				int index = val.lastIndexOf('&');
				int slotNumber = Integer.parseInt(val.substring(index + 1));
				val = val.substring(0, index);
				index = val.lastIndexOf('&');
				String name = val.substring(0, index);
				String scope = val.substring(index + 1);
				variables[i] = new XSLVariable(this, scope, name, slotNumber);
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
		return name;
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
			try
			{
				return sf.getSourceName().equals(getSourceName()) && sf.getLineNumber() == getLineNumber() && sf.id == id;
			}
			catch (DebugException e)
			{
			}
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
}
