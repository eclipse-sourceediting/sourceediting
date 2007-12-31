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
package org.eclipse.wst.xsl.xalan.debugger;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.trace.EndSelectionEvent;
import org.apache.xalan.trace.ExtensionEvent;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.trace.TraceListenerEx2;
import org.apache.xalan.trace.TracerEvent;
import org.eclipse.wst.xsl.debugger.BreakPoint;

public class XalanTraceListener implements TraceListenerEx2 //TraceListenerEx3
{
	private static final Log log = LogFactory.getLog(XalanTraceListener.class);

	private final XalanDebugger debugger;
	private boolean started;
	
	public XalanTraceListener(XalanDebugger debugger)
	{
		this.debugger = debugger;
	}

	public void trace(TracerEvent ev)
	{
		XalanStyleFrame styleFrame = null;
		if (!started)
		{// this is the root of the stack
			started = true;
			debugger.debuggerTransformStarted();
			styleFrame = new XalanRootStyleFrame(ev);
			debugger.pushStyleFrame(styleFrame);
		}
		else if (ev.m_styleNode.getOwnerXSLTemplate() == ev.m_styleNode)
		{// this is an xsl:template, so add to template stack
			styleFrame = new XalanStyleFrame(debugger.peekStyleFrame(), ev);
			debugger.pushStyleFrame(styleFrame);
		}
		else if (ev.m_styleNode.getXSLToken() != Constants.ELEMNAME_TEXTLITERALRESULT)
		{// add to current template element stack
			styleFrame = (XalanStyleFrame) debugger.peekStyleFrame();
			styleFrame.pushElement(ev);
		}
		else
		{
			log.debug("Skipped push for element "+ev.m_styleNode.getLocalName());
		}
		check(styleFrame);
	}

	public void traceEnd(TracerEvent ev)
	{
		XalanStyleFrame styleFrame = (XalanStyleFrame) debugger.peekStyleFrame();
		if (ev.m_styleNode.getOwnerXSLTemplate() == ev.m_styleNode)
		{// remove from current template element stack
			styleFrame.popElement();
		}
		else if (ev.m_styleNode.getXSLToken() != Constants.ELEMNAME_TEXTLITERALRESULT)
		{// remove from current template element stack
			styleFrame.popElement();
		}
		else
		{
			log.debug("Skipped pop for element "+ev.m_styleNode.getLocalName());
		}
		check(styleFrame);
		if (ev.m_styleNode.getOwnerXSLTemplate() == ev.m_styleNode)
		{// end of template, so remove from stack
			debugger.popStyleFrame();
		}
		else
		{// because we don't get selectEnd events, we need to do this check
			TracerEvent tel = styleFrame.peekElement();
			// if the parent is a choose, move on to it
			switch (tel.m_styleNode.getXSLToken())
			{
				case Constants.ELEMNAME_CHOOSE:
					styleFrame.popElement();
					check(styleFrame);
			}
		}
	}
	
	private void check(XalanStyleFrame styleFrame)
	{
		debugger.checkStopped();
		if (styleFrame!=null)
		{
			BreakPoint breakpoint = new BreakPoint(styleFrame.getFilename(), styleFrame.getCurrentLine());
			debugger.checkSuspended(styleFrame, breakpoint);
		}
	}

	public void selected(SelectionEvent ev) throws TransformerException
	{}

	public void selectEnd(EndSelectionEvent ev) throws TransformerException
	{}

	public void generated(GenerateEvent ev)
	{}

	public void extension(ExtensionEvent ee)
	{}

	public void extensionEnd(ExtensionEvent ee)
	{}

}
