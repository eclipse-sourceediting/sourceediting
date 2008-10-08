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

import java.util.TooManyListenersException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.eclipse.wst.xsl.jaxp.debug.debugger.AbstractDebugger;
import org.eclipse.wst.xsl.jaxp.debug.debugger.BreakPoint;
import org.eclipse.wst.xsl.jaxp.debug.debugger.Variable;
import org.xml.sax.SAXException;

public class XalanDebugger extends AbstractDebugger
{
	private static final Log log = LogFactory.getLog(XalanDebugger.class);
	private XalanTraceListener currentTraceListener;
	private TransformerImpl lastTransformerInChain;
	
	public void setTransformerFactory(TransformerFactory factory)
	{
		TransformerFactoryImpl tfi = (TransformerFactoryImpl) factory;
		tfi.setAttribute(TransformerFactoryImpl.FEATURE_SOURCE_LOCATION, Boolean.TRUE);
		tfi.setAttribute(TransformerFactoryImpl.FEATURE_OPTIMIZE, Boolean.FALSE);
	}
	
	public synchronized void debuggerSuspended(BreakPoint breakpoint)
	{
		// flush the serializer (which is buffered by Xalan itself)
		try
		{
			lastTransformerInChain.getSerializationHandler().flushPending();
		}
		catch (SAXException e)
		{
			log.error("Error flushing serializer", e);
		}
		super.debuggerSuspended(breakpoint);
	}

	public void addTransformer(Transformer transformer)
	{
		TransformerImpl transformerImpl = (TransformerImpl) transformer;
		
		lastTransformerInChain = transformerImpl;
		
		TraceManager trMgr = transformerImpl.getTraceManager();
		try
		{
//			XalanPrintTraceListener printer = new XalanPrintTraceListener(new PrintWriter(System.err));
//			printer.m_traceElements = true;
//			printer.m_traceSelection = true;
//			printer.m_traceTemplates = true;
//			trMgr.addTraceListener(printer);
			
			XalanTraceListener traceListener = new XalanTraceListener(this);
			trMgr.addTraceListener(traceListener);
		}
		catch (TooManyListenersException e)
		{
			// ignore
		}
	}

	/**
	 * Gets a variable by ID
	 * @since 1.0
	 */
	public Variable getVariable(int id)
	{
		return currentTraceListener.getVariable(id);
	}

	void setCurrentTraceListener(XalanTraceListener currentTraceListener)
	{
		log.debug("Setting new XalanTraceListener");
		this.currentTraceListener = currentTraceListener;
	}
}
