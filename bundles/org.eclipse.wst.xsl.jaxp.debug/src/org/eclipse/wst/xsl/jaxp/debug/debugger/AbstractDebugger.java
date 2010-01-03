/*******************************************************************************
 * Copyright (c) 2007, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (Intalio) - cleanup findbug errors.
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.debugger;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.wst.xsl.jaxp.debug.invoker.IProcessorInvoker;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TransformationException;

/**
 * An implementation of <code>IXSLDebugger</code>.
 * 
 * This class can be subclassed in order to provide debugging for a particular
 * XSLT processor.
 * 
 * @author Doug Satchwell
 */
public abstract class AbstractDebugger implements IXSLDebugger {
	private static final Log log = LogFactory.getLog(AbstractDebugger.class);

	private static final int ACTION_DO_NOTHING = 0;
	private static final int ACTION_STOP = 1;
	private static final int ACTION_QUIT = 2;
	private static final int ACTION_SUSPEND = 3;
	private static final int ACTION_RESUME = 4;
	private static final int ACTION_STEP_INTO = 5;
	private static final int ACTION_STEP_OVER = 6;
	private static final int ACTION_STEP_RETURN = 7;

	private static final String EVENT_STARTED = "started"; //$NON-NLS-1$
	private static final String EVENT_STOPPED = "stopped"; //$NON-NLS-1$
	private static final String EVENT_SUSPENDED = "suspended client"; //$NON-NLS-1$
	private static final String EVENT_SUSPENDED_STEP = "suspended step"; //$NON-NLS-1$
	private static final String EVENT_RESUMED = "resumed client"; //$NON-NLS-1$
	private static final String EVENT_RESUMED_STEP = "resumed step"; //$NON-NLS-1$

	private IProcessorInvoker invoker;
	private int action;
	private Writer eventWriter;
	private Writer generatedWriter;
	private final Set<BreakPoint> breakpoints = Collections
			.synchronizedSet(new HashSet<BreakPoint>());
	private final Stack<StyleFrame> stack = new Stack<StyleFrame>();
	private StyleFrame stepOverFrame;
	private BreakPoint breakpoint;
	private URL sourceURL;
	private Result result;

	private int stepOverStackSize;

	public synchronized void setInvoker(IProcessorInvoker invoker) {
		this.invoker = invoker;
	}

	public synchronized void setEventWriter(Writer writer) {
		eventWriter = writer;
	}

	public void setGeneratedWriter(Writer writer) {
		this.generatedWriter = writer;
	}

	public synchronized void setSource(URL sourceURL) {
		this.sourceURL = sourceURL;
	}

	public synchronized void setTarget(final Writer writer) {
		result = new StreamResult(new Writer() {
			public void write(char[] cbuf, int off, int len) throws IOException {
				writer.write(cbuf, off, len);
				generatedWriter.write(cbuf, off, len);
			}

			public void close() throws IOException {
				writer.close();
				generatedWriter.close();
			}

			public void flush() throws IOException {
				writer.flush();
				generatedWriter.flush();
			}
		});
	}

	public synchronized void run() {
		if (action != ACTION_QUIT) {
			debuggerStarted();
			try {
				invoker.transform(sourceURL, result);
			} catch (TransformationException e) {
				log.error("Transform failed", e); //$NON-NLS-1$
			}
			debuggerStopped();
		}
	}

	public synchronized void suspend() {
		action = ACTION_SUSPEND;
		notify();
	}

	public synchronized void resume() {
		action = ACTION_RESUME;
		notify();
	}

	public synchronized void stepInto() {
		action = ACTION_STEP_INTO;
		notify();
	}

	public synchronized void stepOver() {
		action = ACTION_STEP_OVER;
		stepOverFrame = peekStyleFrame();
		stepOverStackSize = stack.size();
		notify();
	}

	public synchronized void stepReturn() {
		action = ACTION_STEP_RETURN;
		stepOverStackSize = stack.size();
		notify();
	}

	public synchronized void quit() {
		action = ACTION_QUIT;
	}

	public String stack() {
		StringBuffer sb = new StringBuffer();
		synchronized (stack) {
			for (Iterator<StyleFrame> iter = stack.iterator(); iter.hasNext();) {
				StyleFrame frame = (StyleFrame) iter.next();
				sb.append(frame.toString());
				for (Iterator<?> iter2 = frame.getVariableStack().iterator(); iter2
						.hasNext();) {
					sb.append("|"); //$NON-NLS-1$
					Variable v = (Variable) iter2.next();
					sb.append(v.getId());
				}
				if (iter.hasNext())
					sb.append("$$$"); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	/**
	 * Check whether the debugger has been stopped and perform the appropriate
	 * action if so.
	 */
	public synchronized void checkStopped() {
		if (action == ACTION_QUIT)
			debuggerQuit();
		else if (action == ACTION_STOP)
			debuggerStopped();
	}

	/**
	 * Check whether the debugger is currently suspended or stepping at the
	 * given breakpoint and style frame, and perform the appropriate action if
	 * so.
	 * 
	 * @param styleFrame
	 *            the styleframe to check
	 * @param breakpoint
	 *            the current location
	 */
	public synchronized void checkSuspended(StyleFrame styleFrame,
			BreakPoint breakpoint) {
		// do not suspend unless the line actually changed
		if (breakpoint.equals(this.breakpoint))
			return;
		int stackSize;
		synchronized (stack) {
			stackSize = stack.size();
		}
		// do not suspend if there is nothing in the stack
		if (stackSize == 0)
			return;
		switch (action) {
		case ACTION_SUSPEND:
			debuggerSuspendedClient(breakpoint);
			break;
		case ACTION_STEP_OVER:
			// suspend if we are in the same template or we are moving up the
			// stack
			if (styleFrame.equals(stepOverFrame)
					|| stackSize < stepOverStackSize)
				debuggerSuspendedStep(breakpoint);
			break;
		case ACTION_STEP_INTO:
			debuggerSuspendedStep(breakpoint);
			break;
		case ACTION_STEP_RETURN:
			// suspend if we moved up the stack
			if (stackSize < stepOverStackSize)
				debuggerSuspendedStep(breakpoint);
			break;
		default:
			checkBreakpoint(breakpoint);
		}
	}

	private synchronized void checkBreakpoint(BreakPoint breakpoint) {
		if (isBreakpoint(breakpoint))
			debuggerSuspendedBreakpoint(breakpoint);
	}

	/**
	 * Called when the next transform in the pipeline has begun.
	 */
	public synchronized void debuggerTransformStarted() {
		stack.clear();
	}

	protected synchronized void debuggerStarted() {
		action = ACTION_DO_NOTHING;
		sendEvent(EVENT_STARTED);
	}

	protected synchronized void debuggerStopped() {
		action = ACTION_DO_NOTHING;
		sendEvent(EVENT_STOPPED);
	}

	private synchronized void debuggerQuit() {
		// just wait here indefinitely until the JVM exists, just to make sure
		// we don't send any further events
		try {
			wait();
		} catch (InterruptedException e) {
		}
	}

	private synchronized void debuggerSuspendedBreakpoint(BreakPoint breakpoint) {
		sendEvent("suspended breakpoint " + breakpoint); //$NON-NLS-1$
		debuggerSuspended(breakpoint);
	}

	private synchronized void debuggerSuspendedStep(BreakPoint breakpoint) {
		sendEvent(EVENT_SUSPENDED_STEP);
		debuggerSuspended(breakpoint);
	}

	private synchronized void debuggerSuspendedClient(BreakPoint breakpoint) {
		sendEvent(EVENT_SUSPENDED);
		debuggerSuspended(breakpoint);
	}

	public synchronized void debuggerSuspended(BreakPoint breakpoint) {
		this.breakpoint = breakpoint;
		do {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		} while (action != ACTION_RESUME && action != ACTION_STEP_INTO
				&& action != ACTION_STEP_OVER && action != ACTION_STEP_RETURN
				&& action != ACTION_STOP);
		debuggerResumed();
	}

	private synchronized void debuggerResumed() {
		if (action == ACTION_STEP_INTO || action == ACTION_STEP_OVER
				|| action == ACTION_STEP_RETURN)
			sendEvent(EVENT_RESUMED_STEP);
		else
			sendEvent(EVENT_RESUMED);
	}

	private synchronized void sendEvent(String event) {
		try {
			log.info("Sending event: " + event + " eventWriter=" + eventWriter); //$NON-NLS-1$//$NON-NLS-2$
			eventWriter.write(event + "\n"); //$NON-NLS-1$
			eventWriter.flush();
		} catch (IOException e) {
			log.error("Error sending event", e); //$NON-NLS-1$
		}
	}

	public void addBreakpoint(BreakPoint breakpoint) {
		log.info("Adding breakpoint: " + breakpoint); //$NON-NLS-1$
		breakpoints.add(breakpoint);
	}

	public void removeBreakpoint(BreakPoint breakpoint) {
		log.info("Removing breakpoint: " + breakpoint); //$NON-NLS-1$
		breakpoints.remove(breakpoint);
	}

	private boolean isBreakpoint(BreakPoint breakpoint) {
		// do not check for breakpoint unless the line or filename actually
		// changed
		if (breakpoint.equals(this.breakpoint))
			return false;
		this.breakpoint = null;
		return breakpoints.contains(breakpoint);
	}

	/**
	 * Pop a style frame from the stack.
	 * 
	 * @return the popped style frame
	 */
	public StyleFrame popStyleFrame() {
		synchronized (stack) {
			StyleFrame styleFrame = (StyleFrame) stack.pop();
			if (styleFrame.getParent() != null)
				styleFrame.getParent().removeChild(styleFrame);
			log
					.trace("Popped frame: " + styleFrame + " (size after pop=" + stack.size() + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
			return styleFrame;
		}
	}

	/**
	 * Push a style frame onto the stack.
	 * 
	 * @param styleFrame
	 */
	public void pushStyleFrame(StyleFrame styleFrame) {
		synchronized (stack) {
			stack.push(styleFrame);
			log
					.trace("Pushed frame: " + styleFrame + " (size after push=" + stack.size() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	/**
	 * Peek a style frame from the stack.
	 * 
	 * @return the peeked style frame
	 */
	public StyleFrame peekStyleFrame() {
		synchronized (stack) {
			if (stack.size() > 0)
				return (StyleFrame) stack.peek();
			return null;
		}
	}
}
