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
package org.eclipse.wst.xsl.jaxp.launching.internal.registry;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInstall;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorJar;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;
import org.eclipse.wst.xsl.jaxp.launching.ProcessorInstall;
import org.eclipse.wst.xsl.jaxp.launching.internal.JAXPLaunchingPlugin;
import org.eclipse.wst.xsl.jaxp.launching.internal.ProcessorPreferences;

public class ProcessorRegistry
{
	private final List<ProcessorInstall> contributedInstalls = new ArrayList<ProcessorInstall>();
	private List<IProcessorInstall> userInstalls = new ArrayList<IProcessorInstall>();
	private IProcessorInstall defaultProcessor;
	private IProcessorInstall jreDefaultProcessor;
	private IProcessorInstall[] installs;

	public ProcessorRegistry()
	{
		initializeFromStorage();
	}

	private void initializeFromStorage()
	{
		// read from the registry
		ProcessorRegistryReader registryReader = new ProcessorRegistryReader();
		registryReader.addConfigs(this);
		// find the jre default
		for (Iterator<ProcessorInstall> iter = contributedInstalls.iterator(); iter.hasNext();)
		{
			IProcessorInstall install = iter.next();
			if (install.getId().equals(JAXPRuntime.JRE_DEFAULT_PROCESSOR_ID))
			{
				jreDefaultProcessor = install;
				break;
			}
		}
		// read from the preferences
		addPersistedVMs();
	}

	private void addPersistedVMs()
	{
		// Try retrieving the VM preferences from the preference store
		String vmXMLString = JAXPLaunchingPlugin.getDefault().getPluginPreferences().getString(JAXPRuntime.PREF_PROCESSOR_XML);

		// If the preference was found, load VMs from it into memory
		if (vmXMLString.length() > 0)
		{
			try
			{
				ByteArrayInputStream inputStream = new ByteArrayInputStream(vmXMLString.getBytes());
				ProcessorPreferences prefs = ProcessorPreferences.fromXML(inputStream);
				String defaultProcessorId = prefs.getDefaultProcessorId();
				userInstalls = prefs.getProcessors();
				for (Iterator<IProcessorInstall> iter = userInstalls.iterator(); iter.hasNext();)
				{
					IProcessorInstall install = iter.next();
					if (install.getId().equals(defaultProcessorId))
					{
						defaultProcessor = install;
					}
				}
				if (defaultProcessor == null)
				{
					for (Iterator<ProcessorInstall> iter = contributedInstalls.iterator(); iter.hasNext();)
					{
						IProcessorInstall install = iter.next();
						if (defaultProcessor == null && install.getId().equals(defaultProcessorId))
						{
							defaultProcessor = install;
						}
					}
				}
			}
			catch (CoreException e)
			{
				JAXPLaunchingPlugin.log(e);
			}
		}
		// make the JRE the default default
		if (defaultProcessor == null)
		{
			defaultProcessor = jreDefaultProcessor;
		}
	}

	public void addProcessor(String bundleId, String id, String label, String processorTypeId, String classpath, String debuggerId, String supports)
	{
		IProcessorJar[] jars = ProcessorInstall.createJars(bundleId, classpath);
		contributedInstalls.add(new ProcessorInstall(id, label, processorTypeId, jars, debuggerId, supports, true));
	}

	public void addProcessor(IProcessorInstall install)
	{
		if (!install.isContributed())
			userInstalls.add(install);
		IProcessorInstall[] newinstalls = new IProcessorInstall[installs.length + 1];
		System.arraycopy(installs, 0, newinstalls, 0, installs.length);
		newinstalls[installs.length] = install;
		installs = newinstalls;
	}

	public void removeProcessor(int index)
	{
		IProcessorInstall removed = installs[index];
		if (!removed.isContributed())
			userInstalls.remove(removed);
		IProcessorInstall[] newinstalls = new IProcessorInstall[installs.length - 1];
		System.arraycopy(installs, 0, newinstalls, 0, index);
		System.arraycopy(installs, index + 1, newinstalls, index, newinstalls.length - index);
		installs = newinstalls;
	}

	public IProcessorInstall[] getProcessors()
	{
		if (installs == null)
		{
			installs = new IProcessorInstall[contributedInstalls.size() + userInstalls.size()];
			int startIndex = 0;
			for (int i = 0; i < contributedInstalls.size(); i++)
			{
				installs[i] = contributedInstalls.get(i);
				startIndex = i + 1;
			}
			for (int i = 0; i < userInstalls.size(); i++)
			{
				installs[startIndex + i] = userInstalls.get(i);
			}
		}
		return installs;
	}

	public IProcessorInstall[] getProcessors(String id)
	{
		IProcessorInstall[] installs = getProcessors();
		List<IProcessorInstall> result = new ArrayList<IProcessorInstall>();
		for (IProcessorInstall type : installs)
		{
			if (type.getProcessorType().getId().equals(id))
				result.add(type);
		}
		return result.toArray(new IProcessorInstall[0]);
	}

	public void setDefaultProcessor(IProcessorInstall defaultInstall)
	{
		defaultProcessor = defaultInstall;
	}

	public IProcessorInstall getDefaultProcessor()
	{
		return defaultProcessor;
	}

	public IProcessorInstall getJREDefaultProcessor()
	{
		return jreDefaultProcessor;
	}

	public IProcessorInstall[] getContributedProcessors()
	{
		return contributedInstalls.toArray(new IProcessorInstall[0]);
	}
}
