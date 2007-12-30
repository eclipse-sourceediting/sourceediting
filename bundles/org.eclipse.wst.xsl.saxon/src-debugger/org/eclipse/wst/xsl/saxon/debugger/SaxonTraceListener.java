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
package org.eclipse.wst.xsl.saxon.debugger;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.trace.InstructionInfo;
import net.sf.saxon.trace.XSLTTraceListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.wst.xsl.debugger.BreakPoint;
import org.eclipse.wst.xsl.debugger.StyleFrame;

public class SaxonTraceListener extends XSLTTraceListener
{
	private static final Log log = LogFactory.getLog(SaxonTraceListener.class);

	private SaxonDebugger debugger;

	public SaxonTraceListener(SaxonDebugger debugger)
	{
		this.debugger = debugger;
	}

	public void open()
	{
		super.open();
		log.debug("open");
	}

	public void close()
	{
		super.close();
		log.debug("close");
	}

	private boolean isFrame(int construct)
	{
		if (construct < 1024)
			return true;
		// switch (construct)
		// {
		// case Location.LITERAL_RESULT_ELEMENT:
		// case Location.LITERAL_RESULT_ATTRIBUTE:
		// case Location.EXTENSION_INSTRUCTION:
		// case Location.TRACE_CALL:
		return true;
		// }
		// return false;
	}

	public void enter(InstructionInfo info, XPathContext context)
	{
		super.enter(info, context);
		log.debug("enter: " + info + " " + context);
		StyleFrame styleFrame = pushStyleFrame(info, context);
		if (styleFrame == null)
			return;
		debugger.checkStopped();
		BreakPoint breakpoint = new BreakPoint(styleFrame.getFilename(), styleFrame.getLine());
		debugger.checkSuspended(styleFrame, breakpoint);
	}

	public void leave(InstructionInfo info)
	{
		super.leave(info);
		log.debug("leave: " + info);
		SaxonStyleFrame styleFrame = peekStyleFrame(info);
		if (styleFrame == null)
			return;
		log.debug("start="+styleFrame.getLine() + " end=" + styleFrame.getEndLine());
		
		debugger.checkStopped();
		BreakPoint breakpoint = new BreakPoint(styleFrame.getFilename(), styleFrame.getEndLine());
		debugger.checkSuspended(styleFrame, breakpoint);
		debugger.popStyleFrame();
	}

	private SaxonStyleFrame pushStyleFrame(InstructionInfo info, XPathContext context)
	{
		int infotype = info.getConstructType();
		if (!isFrame(infotype))
			return null;
		String tag = tag(infotype);
		if (tag == null)
			return null;
		SaxonStyleFrame styleFrame = new SaxonStyleFrame(debugger.peekStyleFrame(), info, context, tag);
		debugger.pushStyleFrame(styleFrame);
		return styleFrame;
	}

	private SaxonStyleFrame peekStyleFrame(InstructionInfo info)
	{
		int infotype = info.getConstructType();
		if (!isFrame(infotype))
			return null;
		SaxonStyleFrame styleFrame = (SaxonStyleFrame) debugger.peekStyleFrame();
		return styleFrame;
	}
}
