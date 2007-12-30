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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import net.sf.saxon.Controller;
import net.sf.saxon.TransformerFactoryImpl;

import org.eclipse.wst.xsl.debugger.AbstractDebugger;

public class SaxonDebugger extends AbstractDebugger
{
	public void setTransformerFactory(TransformerFactory factory)
	{
		TransformerFactoryImpl t = (TransformerFactoryImpl)factory;
		t.getConfiguration().setCompileWithTracing(true);
		t.getConfiguration().setLineNumbering(true);
		t.getConfiguration().setTraceExternalFunctions(true);
		
		// TODO: use Eclipse's Classloader
		// t.getConfiguration().setClassLoader(loader);
//		t.getConfiguration().setDebugger(new Debugger(){
//
//			public SlotManager makeSlotManager()
//			{
//				return new SlotManager(){};
//			}});
		
		SaxonTraceListener traceListener = new SaxonTraceListener(this);
		traceListener.setOutputDestination(System.out);
		t.getConfiguration().setTraceListener(traceListener);
	}
	
	public void addTransformer(Transformer transformer)
	{
		Controller controller = (Controller) transformer;
		// controller.setLineNumbering(true);
	}
}
