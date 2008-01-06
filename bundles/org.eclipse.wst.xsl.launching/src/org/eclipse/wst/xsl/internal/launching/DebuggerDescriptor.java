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
import org.eclipse.wst.xsl.launching.IDebugger;
import org.eclipse.wst.xsl.launching.IProcessorType;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

public class DebuggerDescriptor implements IDebugger
{
	private final String[] classpath;
	private final String id;
	private final String bundleId;
	private final String name;
	private final String processorTypeId;
	private final String className;

	public DebuggerDescriptor(String id, String bundleId, String className, String[] classpath, String name, String processorTypeId)
	{
		this.id = id;
		this.classpath = classpath;
		this.bundleId = bundleId;
		this.name = name;
		this.processorTypeId = processorTypeId;
		this.className = className;
	}

	public String getClassName()
	{
		return className;
	}

	public String[] getClassPath()
	{
		List<String> entries = new ArrayList<String>();
		try 
		{
			// in dev, add the bin dir
			if (Platform.inDevelopmentMode())
				entries.add(ProcessorInvokerDescriptor.getFileLocation(bundleId, "/bin"));
			for (String jar : classpath)
			{
				String entry = ProcessorInvokerDescriptor.getFileLocation(bundleId, "/" + jar);
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

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public IProcessorType getProcessorType()
	{
		return XSLTRuntime.getProcessorType(processorTypeId);
	}
}
