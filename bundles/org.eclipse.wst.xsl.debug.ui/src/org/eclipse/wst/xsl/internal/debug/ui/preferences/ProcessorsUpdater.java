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
package org.eclipse.wst.xsl.internal.debug.ui.preferences;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.wst.xsl.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.processor.ProcessorMessages;
import org.eclipse.wst.xsl.launching.IProcessorInstall;
import org.eclipse.wst.xsl.launching.ProcessorPreferences;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

public class ProcessorsUpdater
{

	public boolean updateProcessorSettings(IProcessorInstall[] installs, IProcessorInstall defaultInstall)
	{
		ProcessorPreferences prefs = new ProcessorPreferences();
		if (defaultInstall != null)
			prefs.setDefaultProcessorId(defaultInstall.getId());
		prefs.setProcessors(new ArrayList(Arrays.asList(installs)));
		saveSettings(prefs);
		return true;
	}

	private void saveSettings(final ProcessorPreferences prefs)
	{
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				try
				{
					monitor.beginTask(ProcessorMessages.ProcessorsUpdater, 100);
					String xml = prefs.getAsXML();
					monitor.worked(40);
					XSLTRuntime.getPreferences().setValue(XSLTRuntime.PREF_PROCESSOR_XML, xml);
					monitor.worked(30);
					XSLTRuntime.savePreferences();
					monitor.worked(30);
				}
				catch (IOException ioe)
				{
					XSLDebugUIPlugin.log(ioe);
				}
				catch (ParserConfigurationException e)
				{
					XSLDebugUIPlugin.log(e);
				}
				catch (TransformerException e)
				{
					XSLDebugUIPlugin.log(e);
				}
				finally
				{
					monitor.done();
				}

			}
		};
		try
		{
			XSLDebugUIPlugin.getDefault().getWorkbench().getProgressService().busyCursorWhile(runnable);
		}
		catch (InvocationTargetException e)
		{
			XSLDebugUIPlugin.log(e);
		}
		catch (InterruptedException e)
		{
			XSLDebugUIPlugin.log(e);
		}
	}
}
