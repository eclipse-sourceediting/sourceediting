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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.xsl.internal.launching.FileStylesheetEntry;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.eclipse.wst.xsl.internal.launching.ProcessorJar;
import org.eclipse.wst.xsl.internal.launching.StylesheetParameter;
import org.eclipse.wst.xsl.internal.launching.registry.DebuggerRegistry;
import org.eclipse.wst.xsl.internal.launching.registry.InvokerRegistry;
import org.eclipse.wst.xsl.internal.launching.registry.ProcessorTypeRegistry;

public class XSLTRuntime
{
	public static final String PREF_PROCESSOR_XML = "PREF_PROCESSOR_XML";
	public static final String PREF_FEATURE_XML = "PREF_FEATURE_XML";
	public static final String PREF_OUTPUT_PROPERTIES_XML = "PREF_OUTPUT_PROPERTIES_XML";
	public static final String JRE_DEFAULT_PROCESSOR_ID = "org.eclipse.wst.xsl.launching.jre.default";
	public static final String JRE_DEFAULT_PROCESSOR_TYPE_ID = "org.eclipse.wst.xsl.launching.processorType.jreDefault";

	private static byte[] NEXT_ID_LOCK = new byte[0];
	private static byte[] REGISTRY_LOCK = new byte[0];

	private static int lastStandinID;
	private static ProcessorTypeRegistry processorTypeRegistry;
	private static ProcessorRegistry processorRegistry;
	private static InvokerRegistry invokerRegistry;
	private static String jreXalanVersion;
	private static DebuggerRegistry debuggerRegistry;

	private static ProcessorTypeRegistry getProcessorTypeRegistry()
	{
		synchronized (REGISTRY_LOCK)
		{
			if (processorTypeRegistry == null)
				processorTypeRegistry = new ProcessorTypeRegistry();
		}
		return processorTypeRegistry;
	}

	public static ProcessorRegistry getProcessorRegistry()
	{
		synchronized (REGISTRY_LOCK)
		{
			if (processorRegistry == null)
				processorRegistry = new ProcessorRegistry();
		}
		return processorRegistry;
	}

	private static InvokerRegistry getInvokerRegistry()
	{
		synchronized (REGISTRY_LOCK)
		{
			if (invokerRegistry == null)
				invokerRegistry = new InvokerRegistry();
		}
		return invokerRegistry;
	}

	private static DebuggerRegistry getDebuggerRegistry()
	{
		synchronized (REGISTRY_LOCK)
		{
			if (debuggerRegistry == null)
				debuggerRegistry = new DebuggerRegistry();
		}
		return debuggerRegistry;
	}

	/**
	 * Find a unique processor install id. Check existing 'real' processors, as
	 * well as the last id used for a standin.
	 */
	public static String createUniqueProcessorId(IProcessorType type)
	{
		IProcessorInstall[] installs = XSLTRuntime.getProcessors(type.getId());
		String id = null;
		synchronized (NEXT_ID_LOCK)
		{
			do
			{
				id = String.valueOf(++lastStandinID);
			}
			while (isTaken(id, installs));
		}
		return id;
	}

	public static IProcessorJar createProcessorJar(IPath path)
	{
		return new ProcessorJar(path);
	}

	private static boolean isTaken(String id, IProcessorInstall[] installs)
	{
		for (IProcessorInstall install : installs)
		{
			if (install.getId().equals(id))
				return true;
		}
		return false;
	}

	public static IDebugger[] getDebuggers()
	{
		return getDebuggerRegistry().getDebuggers();
	}

	public static IDebugger getDebugger(String id)
	{
		return getDebuggerRegistry().getDebugger(id);
	}

	public static IProcessorInstall[] getProcessors()
	{
		return getProcessorRegistry().getProcessors();
	}

	public static IProcessorInstall[] getProcessors(String typeId)
	{
		return getProcessorRegistry().getProcessors(typeId);
	}

	public static IProcessorInstall getProcessor(String processorId)
	{
		IProcessorInstall[] processors = getProcessors();
		for (IProcessorInstall install : processors)
		{
			if (install.getId().equals(processorId))
				return install;
		}
		return null;
	}

	public static IProcessorInstall getDefaultProcessor()
	{
		return getProcessorRegistry().getDefaultProcessor();
	}

	public static IProcessorInstall getJREDefaultProcessor()
	{
		return getProcessorRegistry().getJREDefaultProcessor();
	}

	public static IProcessorType[] getProcessorTypes()
	{
		return getProcessorTypeRegistry().getProcessorTypes();
	}

	public static IProcessorType[] getProcessorTypesExclJREDefault()
	{
		return getProcessorTypeRegistry().getProcessorTypesExclJREDefault();
	}

	public static IProcessorType getProcessorType(String id)
	{
		return getProcessorTypeRegistry().getProcessorType(id);
	}

	public static IProcessorInvokerDescriptor getProcessorInvoker(String invokerId)
	{
		return getInvokerRegistry().getProcessorInvoker(invokerId);
	}

	public static IProcessorInvokerDescriptor[] getProcessorInvokers()
	{
		return getInvokerRegistry().getProcessorInvokers();
	}

	public static Preferences getPreferences()
	{
		return LaunchingPlugin.getDefault().getPluginPreferences();
	}

	public static void savePreferences()
	{
		LaunchingPlugin.getDefault().savePluginPreferences();
		synchronized (REGISTRY_LOCK)
		{
			// force the registry to be re-initialised next time it is required
			processorRegistry = null;
		}
		// getProcessorRegistry();
	}

	public static IStylesheetEntry newStylesheetEntry(IResource elem)
	{
		return new FileStylesheetEntry(elem.getFullPath());
	}

	public static IStylesheetEntry newStylesheetEntry(IPath path)
	{
		return new FileStylesheetEntry(path);
	}

	public static IProject getProject(ILaunchConfiguration configuration) throws CoreException
	{
		String projectName = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
		if ((projectName == null) || (projectName.trim().length() < 1))
		{
			return null;
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject javaProject = workspace.getRoot().getProject(projectName);
		return javaProject;
	}

	public static void saveToLaunchConfiguration(ILaunchConfigurationWorkingCopy configuration, IStylesheetEntry[] entries) throws CoreException
	{
		List list = new ArrayList();
		if (entries != null)
		{
			for (IStylesheetEntry entry : entries)
			{
				list.add(entry.getPath().toPortableString());
			}
		}
		configuration.setAttribute(XSLLaunchConfigurationConstants.ATTR_STYLESHEETS_LIST, list);
	}

	public static IStylesheetEntry[] loadFromLaunchConfiguration(ILaunchConfiguration configuration) throws CoreException
	{
		List list = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_STYLESHEETS_LIST, (List) null);
		if (list != null)
		{
			List entries = new ArrayList();
			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				String entry = (String) iter.next();
				IPath path = Path.fromPortableString(entry);
				XSLTRuntime.newStylesheetEntry(path);
				entries.add(XSLTRuntime.newStylesheetEntry(path));
			}
			return (IStylesheetEntry[]) entries.toArray(new IStylesheetEntry[0]);
		}
		return null;
	}

	public static synchronized String getEclipseJAXPImplInfo()
	{
		if (jreXalanVersion == null)
		{
			try
			{
				Class clazz = Class.forName("org.apache.xalan.Version");
				if (clazz != null)
				{
					BeanInfo info = Introspector.getBeanInfo(clazz);
					MethodDescriptor[] methods = info.getMethodDescriptors();
					for (MethodDescriptor descriptor : methods)
					{
						if (descriptor.getName().equals("getVersion"))
						{
							Object result = descriptor.getMethod().invoke(null, null);
							if (result != null)
								jreXalanVersion = result.toString();
							break;
						}
					}
				}
				// TODO do the same for Saxon
			}
			catch (ClassNotFoundException e)
			{
			}
			catch (IntrospectionException e)
			{
			}
			catch (IllegalArgumentException e)
			{
			}
			catch (IllegalAccessException e)
			{
			}
			catch (InvocationTargetException e)
			{
			}
		}
		return jreXalanVersion;
	}

	public static IStylesheetParameter createVariableParameter(String name, String value)
	{
		return new StylesheetParameter(name, value, IStylesheetParameter.VARIABLE_TYPE);
	}

	public static IStylesheetParameter createConstantParameter(String name, String value)
	{
		return new StylesheetParameter(name, value, IStylesheetParameter.CONSTANT_TYPE);
	}
}
