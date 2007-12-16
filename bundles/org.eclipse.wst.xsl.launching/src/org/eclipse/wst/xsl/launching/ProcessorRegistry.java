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
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.eclipse.wst.xsl.internal.launching.registry.ProcessorRegistryReader;

public class ProcessorRegistry
{
	private final ListenerList fgProcessorListeners = new ListenerList();
	private final List contributedInstalls = new ArrayList();
	private List userInstalls = new ArrayList();
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
		for (Iterator iter = contributedInstalls.iterator(); iter.hasNext();)
		{
			IProcessorInstall install = (IProcessorInstall) iter.next();
			if (install.getId().equals(XSLTRuntime.JRE_DEFAULT_PROCESSOR_ID))
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
		String vmXMLString = XSLTRuntime.getPreferences().getString(XSLTRuntime.PREF_PROCESSOR_XML);

		// If the preference was found, load VMs from it into memory
		if (vmXMLString.length() > 0)
		{
			try
			{
				ByteArrayInputStream inputStream = new ByteArrayInputStream(vmXMLString.getBytes());
				ProcessorPreferences prefs = ProcessorPreferences.fromXML(inputStream);
				String defaultProcessorId = prefs.getDefaultProcessorId();
				userInstalls = prefs.getProcessors();
				for (Iterator iter = userInstalls.iterator(); iter.hasNext();)
				{
					IProcessorInstall install = (IProcessorInstall) iter.next();
					if (install.getId().equals(defaultProcessorId))
					{
						defaultProcessor = install;
					}
				}
				if (defaultProcessor == null)
				{
					for (Iterator iter = contributedInstalls.iterator(); iter.hasNext();)
					{
						IProcessorInstall install = (IProcessorInstall) iter.next();
						if (defaultProcessor == null && install.getId().equals(defaultProcessorId))
						{
							defaultProcessor = install;
						}
					}
				}
			}
			catch (CoreException e)
			{
				LaunchingPlugin.log(e);
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
		fireProcessorAdded(install);
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
		fireProcessorRemoved(removed);
	}

	public IProcessorInstall[] getProcessors()
	{
		if (installs == null)
		{
			installs = new IProcessorInstall[contributedInstalls.size() + userInstalls.size()];
			int startIndex = 0;
			for (int i = 0; i < contributedInstalls.size(); i++)
			{
				installs[i] = (IProcessorInstall) contributedInstalls.get(i);
				startIndex = i + 1;
			}
			for (int i = 0; i < userInstalls.size(); i++)
			{
				installs[startIndex + i] = (IProcessorInstall) userInstalls.get(i);
			}
		}
		return installs;
	}

	public IProcessorInstall[] getProcessors(String id)
	{
		IProcessorInstall[] installs = getProcessors();
		List result = new ArrayList();
		for (IProcessorInstall type : installs)
		{
			if (type.getProcessorTypeId().equals(id))
				result.add(type);
		}
		return (IProcessorInstall[]) result.toArray(new IProcessorInstall[0]);
	}

	public void setDefaultProcessor(IProcessorInstall defaultInstall)
	{
		IProcessorInstall previous = getDefaultProcessor();
		defaultProcessor = defaultInstall;
		notifyDefaultProcessorChanged(previous, defaultInstall);
	}

	public IProcessorInstall getDefaultProcessor()
	{
		return defaultProcessor;
	}

	public IProcessorInstall getJREDefaultProcessor()
	{
		return jreDefaultProcessor;
	}

	/**
	 * Adds the given listener to the list of registered Processor install
	 * changed listeners. Has no effect if an identical listener is already
	 * registered.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addProcessorInstallChangedListener(IProcessorInstallChangedListener listener)
	{
		fgProcessorListeners.add(listener);
	}

	/**
	 * Removes the given listener from the list of registered Processor install
	 * changed listeners. Has no effect if an identical listener is not already
	 * registered.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeProcessorInstallChangedListener(IProcessorInstallChangedListener listener)
	{
		fgProcessorListeners.remove(listener);
	}

	private void notifyDefaultProcessorChanged(IProcessorInstall previous, IProcessorInstall current)
	{
		Object[] listeners = fgProcessorListeners.getListeners();
		for (Object element : listeners)
		{
			IProcessorInstallChangedListener listener = (IProcessorInstallChangedListener) element;
			listener.defaultProcessorInstallChanged(previous, current);
		}
	}

	/**
	 * Notifies all Processor install changed listeners of the given property
	 * change.
	 * 
	 * @param event
	 *            event describing the change.
	 */
	public void fireProcessorChanged(PropertyChangeEvent event)
	{
		Object[] listeners = fgProcessorListeners.getListeners();
		for (Object element : listeners)
		{
			IProcessorInstallChangedListener listener = (IProcessorInstallChangedListener) element;
			listener.processorChanged(event);
		}
	}

	/**
	 * Notifies all Processor install changed listeners of the Processor
	 * addition
	 * 
	 * @param Processor
	 *            the Processor that has been added
	 */
	private void fireProcessorAdded(IProcessorInstall Processor)
	{
		Object[] listeners = fgProcessorListeners.getListeners();
		for (Object element : listeners)
		{
			IProcessorInstallChangedListener listener = (IProcessorInstallChangedListener) element;
			listener.processorAdded(Processor);
		}
	}

	/**
	 * Notifies all Processor install changed listeners of the Processor removal
	 * 
	 * @param Processor
	 *            the Processor that has been removed
	 */
	private void fireProcessorRemoved(IProcessorInstall Processor)
	{
		Object[] listeners = fgProcessorListeners.getListeners();
		for (Object element : listeners)
		{
			IProcessorInstallChangedListener listener = (IProcessorInstallChangedListener) element;
			listener.processorRemoved(Processor);
		}
	}

	public IProcessorInstall[] getContributedProcessors()
	{
		return (IProcessorInstall[]) contributedInstalls.toArray(new IProcessorInstall[0]);
	}
}
