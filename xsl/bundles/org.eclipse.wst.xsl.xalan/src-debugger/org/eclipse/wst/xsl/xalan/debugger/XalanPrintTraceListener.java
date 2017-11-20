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

import java.io.PrintWriter;

import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.trace.EndSelectionEvent;
import org.apache.xalan.trace.PrintTraceListener;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.trace.TracerEvent;

public class XalanPrintTraceListener extends PrintTraceListener
{
	private final PrintWriter writer;

	public XalanPrintTraceListener(PrintWriter pw)
	{
		super(pw);
		this.writer = pw;
	}

	public void _trace(TracerEvent ev)
	{
		switch (ev.m_styleNode.getXSLToken())
		{
			case Constants.ELEMNAME_TEXTLITERALRESULT:
				if (m_traceElements)
				{
					writer.print(ev.m_styleNode.getSystemId() + " Line #" + ev.m_styleNode.getLineNumber() + ", " + "Column #" + ev.m_styleNode.getColumnNumber() + " -- " + ev.m_styleNode.getNodeName()
							+ ": ");

					ElemTextLiteral etl = (ElemTextLiteral) ev.m_styleNode;
					String chars = new String(etl.getChars(), 0, etl.getChars().length);

					writer.println("    " + chars.trim());
				}
				break;
			case Constants.ELEMNAME_TEMPLATE:
				if (m_traceTemplates || m_traceElements)
				{
					ElemTemplate et = (ElemTemplate) ev.m_styleNode;

					writer.print(et.getSystemId() + " Line #" + et.getLineNumber() + ", " + "Column #" + et.getColumnNumber() + ": " + et.getNodeName() + " ");

					if (null != et.getMatch())
					{
						writer.print("match='" + et.getMatch().getPatternString() + "' ");
					}

					if (null != et.getName())
					{
						writer.print("name='" + et.getName() + "' ");
					}

					writer.println();
				}
				break;
			default:
				if (m_traceElements)
				{
					writer
							.println(ev.m_styleNode.getSystemId() + " Line #" + ev.m_styleNode.getLineNumber() + ", " + "Column #" + ev.m_styleNode.getColumnNumber() + ": "
									+ ev.m_styleNode.getNodeName());
				}
		}
	}

	public void selected(SelectionEvent ev) throws TransformerException
	{
		writer.print("selected: ");
		super.selected(ev);
	}
	
	public void selectEnd(EndSelectionEvent ev) throws TransformerException
	{
		writer.print("selectEnd: ");
		super.selectEnd(ev);
	}
	
	public void trace(TracerEvent ev)
	{
		writer.print("trace: ");
		super.trace(ev);
	}
	
	public void traceEnd(TracerEvent ev)
	{
		writer.print("traceEnd: ");
		_trace(ev);
	}
}
