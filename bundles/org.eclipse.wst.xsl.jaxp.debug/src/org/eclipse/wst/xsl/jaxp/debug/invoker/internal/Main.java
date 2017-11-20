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
package org.eclipse.wst.xsl.jaxp.debug.invoker.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.wst.xsl.jaxp.debug.invoker.IProcessorInvoker;
import org.eclipse.wst.xsl.jaxp.debug.invoker.PipelineDefinition;

/**
 * The class whose <code>main</code> method is called when launching the transformation process from
 * Eclipse.
 * 
 * @author Doug Satchwell
 */
public class Main
{
	private static final Log log = LogFactory.getLog(Main.class);

	/**
	 * The <code>main</code> method called when launching the transformation process.
	 * There are 4 required arguments:
	 * <ol>
	 * <li>The class name of the <code>IProcessorInvoker</code> to use
	 * <li>The launch file (serialized <code>PipelineDefinition</code>)
	 * <li>The URL of the source XML document
	 * <li>The file where output will be written
	 * </ol>
	 * 
	 * @param args the 4 required arguments
	 */
	public static void main(String[] args)
	{
		log.info("javax.xml.transform.TransformerFactory=" + System.getProperty("javax.xml.transform.TransformerFactory")); //$NON-NLS-1$ //$NON-NLS-2$
		log.info("java.endorsed.dirs=" + System.getProperty("java.endorsed.dirs")); //$NON-NLS-1$ //$NON-NLS-2$
		
		String invokerClassName = args[0];
		File launchFile = new File(args[1]);
		String src = args[2];
		String target = args[3];

		log.info(Messages.getString("Main.4") + launchFile); //$NON-NLS-1$

		// create the invoker
		IProcessorInvoker invoker = null;
		try
		{
			Class clazz = Class.forName(invokerClassName);
			invoker = (IProcessorInvoker) clazz.newInstance();
		}
		catch (Exception e)
		{
			handleFatalError(Messages.getString("Main.5") + invokerClassName, e); //$NON-NLS-1$
		}
		try
		{
			PipelineDefinition pipeline = new PipelineDefinition(launchFile);
			pipeline.configure(invoker);
			invoker.transform(new URL(src), new StreamResult(new FileOutputStream(new File(target))));
		}
		catch (Exception e)
		{
			handleFatalError(e.getMessage(), e);
		}
	}

	private static void handleFatalError(String msg, Throwable t)
	{
		log.fatal(msg, t);
		System.exit(1);
	}
}
