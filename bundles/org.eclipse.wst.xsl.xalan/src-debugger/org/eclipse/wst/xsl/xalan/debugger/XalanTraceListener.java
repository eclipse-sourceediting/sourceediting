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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.trace.EndSelectionEvent;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.trace.TraceListenerEx2;
import org.apache.xalan.trace.TracerEvent;
import org.apache.xpath.VariableStack;
import org.eclipse.wst.xsl.debugger.BreakPoint;
import org.eclipse.wst.xsl.debugger.StyleFrame;
import org.eclipse.wst.xsl.debugger.Variable;

public class XalanTraceListener implements TraceListenerEx2
{
	private static final Log log = LogFactory.getLog(XalanTraceListener.class);

	private final XalanDebugger debugger;
	private final VariableStack varStack;
	private boolean started;

	public XalanTraceListener(VariableStack stack, XalanDebugger debugger)
	{
		this.debugger = debugger;
		varStack = stack;
	}

	public void generated(GenerateEvent ev)
	{
		// TODO here we could build up the output tree into a string, and return to the UI if requested?
	}

	// These events are thrown when hitting xsl:when or xsl:if
	public void selected(SelectionEvent ev) throws TransformerException
	{
	}

	public void selectEnd(EndSelectionEvent ev) throws TransformerException
	{
	}

	public void trace(TracerEvent ev)
	{
		// log.debug("trace: "+ev);
		// There is one XalanTraceListener for each stylesheet in the pipeline.
		// So if this is the first trace event, we clear down the debugger's
		// state from the previous transformation.
		if (!started)
		{
			started = true;
			debugger.debuggerTransformStarted();
		}
		StyleFrame styleFrame = pushStyleFrame(ev);
		if (styleFrame == null)
			return;
		debugger.checkStopped();
		BreakPoint breakpoint = new BreakPoint(styleFrame.getFilename(), styleFrame.getLine());
		debugger.checkSuspended(styleFrame, breakpoint);
	}

	public void traceEnd(TracerEvent ev)
	{
		// log.debug("traceEnd: "+ev);
		XalanStyleFrame styleFrame = popStyleFrame(ev);
		debugger.checkStopped();
		BreakPoint breakpoint = new BreakPoint(styleFrame.getFilename(), styleFrame.getEndLine());
		debugger.checkSuspended(styleFrame, breakpoint);
		// consistency check
		log.debug(styleFrame.getFilename() + " " + styleFrame.getName() + " " + styleFrame.getLine() + " " + styleFrame.getEndLine());
	}

	private XalanStyleFrame pushStyleFrame(TracerEvent ev)
	{
		StyleFrame parent = debugger.peekStyleFrame();
		XalanStyleFrame styleFrame;
		if (parent == null)
		{
			List globals = getGlobals(ev.m_styleNode.getStylesheetRoot());
			styleFrame = new XalanRootStyleFrame(parent, ev.m_styleNode, varStack, globals);
		}
		else
			styleFrame = new XalanStyleFrame(parent, ev.m_styleNode, varStack);
		debugger.pushStyleFrame(styleFrame);
		return styleFrame;
	}

	private List getGlobals(StylesheetRoot root)
	{
		List vars = new ArrayList();
		Vector composedVars = root.getVariablesAndParamsComposed();
		int i = 0;
		for (Iterator iter = composedVars.iterator(); iter.hasNext();)
		{
			ElemVariable variable = (ElemVariable) iter.next();
			int token = variable.getXSLToken();
			if ((token == Constants.ELEMNAME_PARAMVARIABLE || token == Constants.ELEMNAME_VARIABLE) && variable.getIsTopLevel())
			{
				vars.add(new XalanVariable(varStack, Variable.GLOBAL_SCOPE, i, variable));
				++i;
			}
		}
		return vars;
	}

	private XalanStyleFrame popStyleFrame(TracerEvent ev)
	{
		ElemTemplateElement element = ev.m_styleNode;
		String filename = element.getSystemId();
		int line = element.getLineNumber();

		// why the while loop? Because Xalan doesn't report xsl:when or xsl:if
		// as TracerEvents
		// ...so it is a workaround to make sure we get to the right frame
		XalanStyleFrame styleFrame;
		while ((styleFrame = (XalanStyleFrame) debugger.popStyleFrame()) != null)
		{
			if (styleFrame.getFilename().equals(filename) && styleFrame.getLine() == line)
			{
				String name = element.getNodeName();
				if (name.equals("param") || name.equals("variable"))
				{
					XalanStyleFrame parent = (XalanStyleFrame) styleFrame.getParent();
					parent.addVariable((ElemVariable) element);
				}
				styleFrame.setEndLine(line);
				return styleFrame;
			}
		}
		return null;
	}
}
