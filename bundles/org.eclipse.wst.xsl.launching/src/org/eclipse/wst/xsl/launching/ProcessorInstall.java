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
package org.eclipse.wst.xsl.launching;

import java.beans.PropertyChangeEvent;
import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xsl.internal.launching.PluginProcessorJar;

public class ProcessorInstall implements IProcessorInstall
{
	private final String id;
	private String name;
	private String type;
	private IProcessorJar[] jars;
	private final boolean contributed;
	private final boolean notify;
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
		notify = true;
	}

	public String getId()
	{
		return id;
	}

	public String getLabel()
	{
		return name;
	}

	public void setLabel(String name)
	{
		String previous = this.name;
		this.name = name;
		if (notify)
		{
			PropertyChangeEvent event = new PropertyChangeEvent(this, IProcessorInstallChangedListener.PROPERTY_NAME, previous, name);
			XSLTRuntime.getProcessorRegistry().fireProcessorChanged(event);
		}
	}

	public IProcessorJar[] getProcessorJars()
	{
		return jars;
	}

	public void setProcessorJars(IProcessorJar[] jars)
	{
		IProcessorJar[] previous = jars;
		this.jars = jars;
		if (notify)
		{
			PropertyChangeEvent event = new PropertyChangeEvent(this, IProcessorInstallChangedListener.PROPERTY_JARS, previous, jars);
			XSLTRuntime.getProcessorRegistry().fireProcessorChanged(event);
		}
	}

	public String getProcessorTypeId()
	{
		return type;
	}

	public IProcessorType getProcessorType()
	{
		return XSLTRuntime.getProcessorType(type);
	}

	public void setProcessorTypeId(String id)
	{
		String oldId = id;
		type = id;
		if (notify)
		{
			PropertyChangeEvent event = new PropertyChangeEvent(this, IProcessorInstallChangedListener.PROPERTY_TYPE, oldId, id);
			XSLTRuntime.getProcessorRegistry().fireProcessorChanged(event);
		}
	}

	public IStatus validateInstallLocation(File installLocation)
	{
		if (contributed)
			return Status.OK_STATUS;
		// TODO validate install location for user-created installs
		return null;
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
		String[] jarstring = classpath.split(";");
		jars = new IProcessorJar[jarstring.length];
		for (int i = 0; i < jarstring.length; i++)
		{
			String jar = jarstring[i];
			jars[i] = new PluginProcessorJar(bundleId, new Path(jar));
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
		return XSLTRuntime.getDebugger(debuggerId);
	}

	public void setDebuggerId(String debuggerId)
	{
		this.debuggerId = debuggerId;
	}
}
