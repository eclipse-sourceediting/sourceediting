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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xsl.internal.launching.FeaturePreferences;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.eclipse.wst.xsl.internal.launching.OutputPropertyPreferences;
import org.eclipse.wst.xsl.internal.launching.ProcessorJar;
import org.eclipse.wst.xsl.internal.launching.ProcessorPreferences;
import org.eclipse.wst.xsl.internal.launching.registry.DebuggerRegistry;
import org.eclipse.wst.xsl.internal.launching.registry.InvokerRegistry;
import org.eclipse.wst.xsl.internal.launching.registry.ProcessorRegistry;
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
	
	private static void savePreferences()
	{
		LaunchingPlugin.getDefault().savePluginPreferences();
		synchronized (REGISTRY_LOCK)
		{
			// force the registries to be re-initialised next time it is required
			processorRegistry = null;
			processorTypeRegistry = null;
		}
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

	private static Preferences getPreferences()
	{
		return LaunchingPlugin.getDefault().getPluginPreferences();
	}

	public static void saveFeaturePreferences(Map<IProcessorType, Map<String, String>> typeFeatures, IProgressMonitor monitor) throws CoreException
	{
		if (monitor.isCanceled())
			return;
		try
		{
			monitor.beginTask("Saving features...", 100);
			FeaturePreferences prefs = new FeaturePreferences();
			Map<String,Map<String,String>> typeIdFeatures = new HashMap<String,Map<String,String>>(typeFeatures.size());
			for (IProcessorType type : typeFeatures.keySet())
			{
				Map<String,String> values = typeFeatures.get(type);
				typeIdFeatures.put(type.getId(), values);
			}
			prefs.setTypeFeatures(typeIdFeatures);
			String xml = prefs.getAsXML();
			monitor.worked(40);
			if (monitor.isCanceled())
				return;
			XSLTRuntime.getPreferences().setValue(XSLTRuntime.PREF_FEATURE_XML, xml);
			monitor.worked(30);
			if (monitor.isCanceled())
				return;
			XSLTRuntime.savePreferences();
			monitor.worked(30);
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR,LaunchingPlugin.PLUGIN_ID,"Failed to save feature preferences",e));
		}
		finally
		{
			monitor.done();
		}
	}

	public static void saveOutputPropertyPreferences(Map<IProcessorType, Properties> typeProperties, IProgressMonitor monitor) throws CoreException
	{
		if (monitor.isCanceled())
			return;
		try
		{
			monitor.beginTask("Saving properties...", 100);
			OutputPropertyPreferences prefs = new OutputPropertyPreferences();
			for (IProcessorType type : typeProperties.keySet())
			{
				prefs.setOutputPropertyValues(type.getId(), typeProperties.get(type));
			}
			String xml = prefs.getAsXML();
			monitor.worked(40);
			if (monitor.isCanceled())
				return;
			XSLTRuntime.getPreferences().setValue(XSLTRuntime.PREF_OUTPUT_PROPERTIES_XML, xml);
			monitor.worked(30);
			if (monitor.isCanceled())
				return;
			XSLTRuntime.savePreferences();
			monitor.worked(30);
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR,LaunchingPlugin.PLUGIN_ID,"Failed to save output property preferences",e));
		}
		finally
		{
			monitor.done();
		}
	}

	public static void saveProcessorPreferences(IProcessorInstall[] installs, IProcessorInstall defaultInstall, IProgressMonitor monitor) throws CoreException
	{
		if (monitor.isCanceled())
			return;
		try
		{
			monitor.beginTask("Saving processors...", 100);
			ProcessorPreferences prefs = new ProcessorPreferences();
			if (defaultInstall != null)
				prefs.setDefaultProcessorId(defaultInstall.getId());
			prefs.setProcessors(new ArrayList<IProcessorInstall>(Arrays.asList(installs)));
			String xml = prefs.getAsXML();
			monitor.worked(40);
			if (monitor.isCanceled())
				return;
			XSLTRuntime.getPreferences().setValue(XSLTRuntime.PREF_PROCESSOR_XML, xml);
			monitor.worked(30);
			if (monitor.isCanceled())
				return;
			XSLTRuntime.savePreferences();
			monitor.worked(30);
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR,LaunchingPlugin.PLUGIN_ID,"Failed to save process preferences",e));
		}
		finally
		{
			monitor.done();
		}
	}

	public static Properties createDefaultOutputProperties(String typeId)
	{
		Properties props = new Properties();
		if (JRE_DEFAULT_PROCESSOR_TYPE_ID.equals(typeId))
			props.put("indent", "yes");
		else if (XSLLaunchConfigurationConstants.XALAN_TYPE_ID.equals(typeId))
			props.put("{http://xml.apache.org/xslt}indent-amount", "4");
		else if (XSLLaunchConfigurationConstants.SAXONB_TYPE_ID.equals(typeId))
			props.put("{http://saxon.sf.net/}indent-spaces", "4");
		return props;
	}
}
