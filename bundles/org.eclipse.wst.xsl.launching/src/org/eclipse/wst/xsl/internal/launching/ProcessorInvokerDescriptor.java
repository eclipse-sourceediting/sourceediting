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
package org.eclipse.wst.xsl.internal.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xsl.launching.IProcessorInvoker;

public class ProcessorInvokerDescriptor implements IProcessorInvoker
{

	private final String invokerClass;
	private final String[] classpath;
	private final String id;
	private final String bundleId;

	public ProcessorInvokerDescriptor(String id, String bundleId, String invokerClass, String[] classpath)
	{
		this.id = id;
		this.bundleId = bundleId;
		this.invokerClass = invokerClass;
		this.classpath = classpath;
	}

	public String[] getClasspathEntries()
	{
		List<String> entries = new ArrayList<String>();
		try {
			// if in dev mode, use the bin dir
			if (Platform.inDevelopmentMode())
				entries.add(Utils.getFileLocation(bundleId, "/bin"));
			for (int i=0;i <classpath.length;i++) 
			{
				String string = classpath[i];
				String entry;
				if (string.startsWith("${eclipse_orbit:") && string.endsWith("}"))
				{
					string = string.substring("${eclipse_orbit:".length());
					string = string.substring(0,string.length()-1);
					entry = Utils.getFileLocation(string,"");
				}
				else
				{
					entry = Utils.getFileLocation(bundleId,string);
				}
				if (entry!=null)
					entries.add(entry);
			}
		} 
		catch (CoreException e) 
		{
			LaunchingPlugin.log(e);
		}
		return entries.toArray(new String[0]);
	}

	/**
	 * The name of the class that implements IProcessorInvoker
	 */
	public String getInvokerClassName()
	{
		return invokerClass;
	}

	public String getId()
	{
		return id;
	}
}
