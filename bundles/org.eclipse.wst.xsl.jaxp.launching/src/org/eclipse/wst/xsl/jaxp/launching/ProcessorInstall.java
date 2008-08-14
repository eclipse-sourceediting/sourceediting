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
package org.eclipse.wst.xsl.jaxp.launching;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.xsl.jaxp.launching.internal.JAXPLaunchingPlugin;
import org.eclipse.wst.xsl.jaxp.launching.internal.PluginProcessorJar;
import org.eclipse.wst.xsl.jaxp.launching.internal.Utils;

public class ProcessorInstall implements IProcessorInstall
{
	private final String id;
	private String name;
	private String type;
	private IProcessorJar[] jars;
	private final boolean contributed;
	private String debuggerId;
	private String supports;

	public ProcessorInstall(String id, String label, String typeId, IProcessorJar[] jars, String debuggerId, String supports, boolean contributed)
	{
		this.id = id;
		name = label;
		type = typeId;
		this.debuggerId = debuggerId;
		this.contributed = contributed;
		this.jars = jars;
		this.supports = supports;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public IProcessorJar[] getProcessorJars()
	{
		return jars;
	}

	public void setProcessorJars(IProcessorJar[] jars)
	{
		this.jars = jars;
	}

	public String getProcessorTypeId()
	{
		return type;
	}

	public IProcessorType getProcessorType()
	{
		return JAXPRuntime.getProcessorType(type);
	}

	public void setProcessorTypeId(String id)
	{
		type = id;
	}

	public boolean isContributed()
	{
		return contributed;
	}

	public static IProcessorJar[] createJars(String bundleId, String classpath)
	{
		IProcessorJar[] jars;
		if (classpath == null)
			return new IProcessorJar[0];
		String[] jarstring = classpath.split(";"); //$NON-NLS-1$
		jars = new IProcessorJar[jarstring.length];
		for (int i = 0; i < jarstring.length; i++)
		{
			String jar = jarstring[i];
			try
			{
				if (jar.startsWith("${eclipse_orbit:") && jar.endsWith("}")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					jar = jar.substring("${eclipse_orbit:".length()); //$NON-NLS-1$
					jar = jar.substring(0,jar.length()-1);
					//jar = Utils.getFileLocation(jar,"");
					jars[i] = new PluginProcessorJar(jar, null);
				}
				else
				{
					jar = Utils.getFileLocation(bundleId,jar);
					jars[i] = new PluginProcessorJar(bundleId, new Path(jar));
				}
			}
			catch (CoreException e)
			{
				JAXPLaunchingPlugin.log(e);
			}
		}
		return jars;
	}

	public boolean hasDebugger()
	{
		return debuggerId != null;
	}

	public boolean supports(String xsltVersion)
	{
		return supports.indexOf(xsltVersion) >= 0;
	}

	public String getSupports()
	{
		return supports;
	}

	public void setSupports(String supports)
	{
		this.supports = supports;
	}

	public IDebugger getDebugger()
	{
		return JAXPRuntime.getDebugger(debuggerId);
	}

	public void setDebuggerId(String debuggerId)
	{
		this.debuggerId = debuggerId;
	}
}
