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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.wst.xsl.debugger.DebugConstants;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.eclipse.wst.xsl.internal.launching.XSLTLaunchConfigurationDelegate;
import org.eclipse.wst.xsl.launching.IProcessorInstall;
import org.eclipse.wst.xsl.launching.config.LaunchHelper;

public class XSLDebugTarget extends XSLDebugElement implements IDebugTarget
{
	private final byte[] STACK_FRAMES_LOCK = new byte[0];
	private final byte[] VALUE_MAP_LOCK = new byte[0];
	private final byte[] WRITE_LOCK = new byte[0];

	private final int CONNECT_ATTEMPTS = 10;
	private final int CONNECT_WAIT = 1000;

	private final IProcess process;
	private final ILaunch launch;
	private XSLThread thread;
	private IThread[] threads;
	private IStackFrame[] stackFramesCache;

	private EventDispatchJob eventDispatch;
	private final LaunchHelper launchHelper;

	private final Map<XSLVariable, XSLValue> valueMapCache = new HashMap<XSLVariable, XSLValue>();
	private String name;
	private boolean suspended;
	private boolean terminated;

	private Socket requestSocket;
	private Socket eventSocket;
	private BufferedReader requestReader;
	private BufferedReader eventReader;
	private PrintWriter requestWriter;

	public XSLDebugTarget(ILaunch launch, IProcess process, LaunchHelper launchHelper) throws CoreException
	{
		super(null);
		this.launch = launch;
		this.debugTarget = this;
		this.process = process;
		this.launchHelper = launchHelper;
		this.requestSocket = attemptConnect(launchHelper.getRequestPort());
		this.eventSocket = attemptConnect(launchHelper.getEventPort());

		if (requestSocket != null && eventSocket != null)
		{
			try
			{
				this.eventReader = new BufferedReader(new InputStreamReader(eventSocket.getInputStream()));
				this.requestWriter = new PrintWriter(requestSocket.getOutputStream());
				this.requestReader = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
			}
			catch (IOException e)
			{
				abort("Unable to connect to debugger", e);
			}
			this.thread = new XSLThread(this);
			this.threads = new IThread[]{ thread };
			this.eventDispatch = new EventDispatchJob();
			this.eventDispatch.schedule();

			DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
		}
	}
	
	private Socket attemptConnect(int port) throws CoreException
	{
		Socket socket = null;
		for(int i=0;i<CONNECT_ATTEMPTS;i++)
		{	
			// break out if process is terminated
			if (process.isTerminated())
				break;
			try
			{
				socket = new Socket("localhost",port);
			}
			catch (ConnectException e)
			{}
			catch (IOException e)
			{}
			if (socket != null)
				break;
			try
			{
				Thread.sleep(CONNECT_WAIT);
			}
			catch (InterruptedException e)
			{}
		}
		if (socket == null && !process.isTerminated())
			throw new CoreException(new Status(Status.ERROR, LaunchingPlugin.PLUGIN_ID, "Could not connect to socket "+port+" after "+CONNECT_ATTEMPTS+" attempts"));
		return socket;
	}

	public IProcess getProcess()
	{
		return process;
	}

	public IThread[] getThreads() throws DebugException
	{
		return threads;
	}

	public boolean hasThreads() throws DebugException
	{
		return threads.length > 0;
	}

	public String getName() throws DebugException
	{
		if (name == null)
		{
			try
			{
				IProcessorInstall install = XSLTLaunchConfigurationDelegate.getProcessorInstall(getLaunch().getLaunchConfiguration(), ILaunchManager.DEBUG_MODE);
				String type = install.getProcessorType().getLabel();
				name = type + " [" + install.getName() + "]";
			}
			catch (CoreException e)
			{
				throw new DebugException(e.getStatus());
			}
		}
		return name;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint)
	{
		if (breakpoint.getModelIdentifier().equals(IXSLConstants.ID_XSL_DEBUG_MODEL) && breakpoint instanceof ILineBreakpoint)
		{
//			try
//			{
//				ILineBreakpoint lb = (ILineBreakpoint) breakpoint;
//				IMarker marker = lb.getMarker();
//				for (Iterator<?> iter = launchHelper.getPipeline().getTransformDefs().iterator(); iter.hasNext();)
//				{
//					LaunchTransform lt = (LaunchTransform) iter.next();
//					if (marker.getResource().getLocation().equals(lt.getLocation()))
//						return true;
//				}
//			}
//			catch (CoreException e)
//			{
//				LaunchingPlugin.log(e);
//			}
			return true;
		}
		return false;
	}

	@Override
	public IDebugTarget getDebugTarget()
	{
		return this;
	}

	@Override
	public ILaunch getLaunch()
	{
		return launch;
	}

	public boolean canTerminate()
	{
		return getProcess().canTerminate();
	}

	public boolean isTerminated()
	{
		return terminated;
	}

	public void terminate() throws DebugException
	{
		synchronized (WRITE_LOCK)
		{
			getProcess().terminate();
		}
	}

	public boolean canResume()
	{
		return !isTerminated() && isSuspended();
	}

	public boolean canSuspend()
	{
		return !isTerminated() && !isSuspended();
	}

	public boolean isSuspended()
	{
		return suspended;
	}

	public void resume() throws DebugException
	{
		sendRequest(DebugConstants.REQUEST_RESUME);
	}

	private void resumed(int detail)
	{
		suspended = false;
		thread.fireResumeEvent(detail);
	}

	private void suspended(int detail)
	{
		suspended = true;
		thread.fireSuspendEvent(detail);
	}

	public void suspend() throws DebugException
	{
		sendRequest(DebugConstants.REQUEST_SUSPEND);
	}

	public void breakpointAdded(IBreakpoint breakpoint)
	{
		if (supportsBreakpoint(breakpoint))
		{
			try
			{
				ILineBreakpoint lb = (ILineBreakpoint) breakpoint;
				if (breakpoint.isEnabled())
				{
					try
					{
						IMarker marker = lb.getMarker();
						if (marker != null)
						{
							URL file = marker.getResource().getLocation().toFile().toURL();
							sendRequest(DebugConstants.REQUEST_ADD_BREAKPOINT + " " + file + " " + lb.getLineNumber());
						}
					}
					catch (CoreException e)
					{
						LaunchingPlugin.log(e);
					}
					catch (MalformedURLException e)
					{
						LaunchingPlugin.log(e);
					}
				}
			}
			catch (CoreException e)
			{
			}
		}
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		if (supportsBreakpoint(breakpoint))
		{
			try
			{
				ILineBreakpoint lb = (ILineBreakpoint) breakpoint;
				IMarker marker = lb.getMarker();
				if (marker != null)
				{
					URL file = marker.getResource().getLocation().toFile().toURL();
					sendRequest(DebugConstants.REQUEST_REMOVE_BREAKPOINT + " " + file + " " + lb.getLineNumber());
				}
			}
			catch (CoreException e)
			{
				LaunchingPlugin.log(e);
			}
			catch (MalformedURLException e)
			{
				LaunchingPlugin.log(e);
			}
		}
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		if (supportsBreakpoint(breakpoint))
		{
			try
			{
				if (breakpoint.isEnabled())
				{
					breakpointAdded(breakpoint);
				}
				else
				{
					breakpointRemoved(breakpoint, null);
				}
			}
			catch (CoreException e)
			{
			}
		}
	}

	public boolean canDisconnect()
	{
		return false;
	}

	public void disconnect() throws DebugException
	{
	}

	public boolean isDisconnected()
	{
		return false;
	}

	public boolean supportsStorageRetrieval()
	{
		return false;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException
	{
		return null;
	}

	private void ready()
	{
		fireCreationEvent();
		installDeferredBreakpoints();
		try
		{
			sendRequest(DebugConstants.REQUEST_START);
		}
		catch (DebugException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Install breakpoints that are already registered with the breakpoint
	 * manager.
	 */
	private void installDeferredBreakpoints()
	{
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(IXSLConstants.ID_XSL_DEBUG_MODEL);
		for (IBreakpoint element : breakpoints)
		{
			breakpointAdded(element);
		}
	}

	private void terminated()
	{
		terminated = true;
		suspended = true;
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);

		threads = new IThread[0];

		fireTerminateEvent();
	}

	/**
	 * Returns the current stack frames in the target.
	 */
	protected IStackFrame[] getStackFrames() throws DebugException
	{
		synchronized (STACK_FRAMES_LOCK)
		{
			if (stackFramesCache == null)
			{
				String framesData = sendRequest(DebugConstants.REQUEST_STACK);
				IStackFrame[] sf = null;
				if (framesData != null && framesData.length() > 0)
				{
					String[] frames = framesData.split("\\$\\$\\$");
					sf = new IStackFrame[frames.length];
					for (int i = 0; i < frames.length; i++)
					{
						String data = frames[i];
						sf[frames.length - i - 1] = new XSLStackFrame(thread, data, i);
					}
				}
				else
				{
					sf = new IStackFrame[0];
				}
				stackFramesCache = sf;
			}
			return stackFramesCache;
		}
	}

	private void ressetStackFramesCache()
	{
		synchronized (STACK_FRAMES_LOCK)
		{
			stackFramesCache = null;
		}
		synchronized (VALUE_MAP_LOCK)
		{
			valueMapCache.clear();
		}
	}

	/**
	 * Single step the interpreter.
	 */
	protected void stepOver() throws DebugException
	{
		sendRequest(DebugConstants.REQUEST_STEP_OVER);
	}

	protected void stepInto() throws DebugException
	{
		sendRequest(DebugConstants.REQUEST_STEP_INTO);
	}

	protected IValue getVariableValue(XSLVariable variable) throws DebugException
	{
		synchronized (VALUE_MAP_LOCK)
		{
			XSLValue value = (XSLValue) valueMapCache.get(variable);
			if (value == null)
			{
				String res = sendRequest(DebugConstants.REQUEST_VARIABLE + " " + variable.getStackFrame().getIdentifier() + "&" + variable.getScope() + "&" + variable.getSlotNumber());
				value = new XSLValue(this, res);
			}
			valueMapCache.put(variable, value);
			return value;
		}
	}

	private String sendRequest(String request) throws DebugException
	{
		String response = null;
		synchronized (WRITE_LOCK)
		{
			// System.out.println("REQUEST: " + request);
			requestWriter.println(request);
			requestWriter.flush();
			try
			{
				// wait for response
				response = requestReader.readLine();
				// System.out.println("RESPONSE: " + response);
			}
			catch (IOException e)
			{
				abort("Request failed: " + request, e);
			}
		}
		return response;
	}

	private void breakpointHit(String event)
	{
		// determine which breakpoint was hit, and set the thread's breakpoint
		int lastSpace = event.lastIndexOf(' ');
		if (lastSpace > 0)
		{
			String line = event.substring(lastSpace + 1);
			int lineNumber = Integer.parseInt(line);
			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(IXSLConstants.ID_XSL_DEBUG_MODEL);
			for (IBreakpoint breakpoint : breakpoints)
			{
				if (supportsBreakpoint(breakpoint))
				{
					if (breakpoint instanceof ILineBreakpoint)
					{
						ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoint;
						try
						{
							if (lineBreakpoint.getLineNumber() == lineNumber)
							{
								thread.setBreakpoints(new IBreakpoint[]{ breakpoint });
								break;
							}
						}
						catch (CoreException e)
						{
						}
					}
				}
			}
		}
		suspended(DebugEvent.BREAKPOINT);
	}

	private class EventDispatchJob extends Job
	{

		public EventDispatchJob()
		{
			super("Event Dispatch");
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			String event = "";
			while (!isTerminated() && event != null)
			{
				try
				{
					event = eventReader.readLine();
					if (event != null)
					{
						thread.setBreakpoints(null);
						thread.setStepping(false);
						if (event.equals("ready"))
						{
							ready();
						}
						else if (event.equals("stopped"))
						{
							try
							{
								terminate();
							}
							catch (DebugException e)
							{
							}
						}
						else if (event.equals("terminated"))
						{
							terminated();
						}
						else if (event.startsWith("resumed"))
						{
							if (event.endsWith("step"))
							{
								thread.setStepping(true);
								resumed(DebugEvent.STEP_OVER);
							}
							else if (event.endsWith("client"))
							{
								resumed(DebugEvent.CLIENT_REQUEST);
							}
							else
							{
								// System.out.println("Did not understand event:
								// " + event);
							}
						}
						else if (event.startsWith("suspended"))
						{
							// clear down the frames so that they are re-fetched
							ressetStackFramesCache();
							if (event.endsWith("client"))
							{
								suspended(DebugEvent.CLIENT_REQUEST);
							}
							else if (event.endsWith("step"))
							{
								suspended(DebugEvent.STEP_END);
							}
							else if (event.indexOf("breakpoint") >= 0)
							{
								breakpointHit(event);
							}
							else
							{
								// System.out.println("Did not understand event:
								// " + event);
							}
						}
						else
						{
							// System.out.println("Did not understand event: " +
							// event);
						}
					}
				}
				catch (IOException e)
				{
					terminated();
				}
			}
			return Status.OK_STATUS;
		}
	}
}
