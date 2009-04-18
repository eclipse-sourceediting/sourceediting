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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xsl.jaxp.launching.internal.FeaturePreferences;
import org.eclipse.wst.xsl.jaxp.launching.internal.JAXPLaunchingPlugin;
import org.eclipse.wst.xsl.jaxp.launching.internal.Messages;
import org.eclipse.wst.xsl.jaxp.launching.internal.OutputPropertyPreferences;
import org.eclipse.wst.xsl.jaxp.launching.internal.ProcessorJar;
import org.eclipse.wst.xsl.jaxp.launching.internal.ProcessorPreferences;
import org.eclipse.wst.xsl.jaxp.launching.internal.registry.DebuggerRegistry;
import org.eclipse.wst.xsl.jaxp.launching.internal.registry.InvokerRegistry;
import org.eclipse.wst.xsl.jaxp.launching.internal.registry.ProcessorRegistry;
import org.eclipse.wst.xsl.jaxp.launching.internal.registry.ProcessorTypeRegistry;

public class JAXPRuntime
{
	public static final String PREF_PROCESSOR_XML = "PREF_PROCESSOR_XML"; //$NON-NLS-1$
	public static final String PREF_FEATURE_XML = "PREF_FEATURE_XML"; //$NON-NLS-1$
	public static final String PREF_OUTPUT_PROPERTIES_XML = "PREF_OUTPUT_PROPERTIES_XML"; //$NON-NLS-1$
	public static final String JRE_DEFAULT_PROCESSOR_ID = "org.eclipse.wst.xsl.launching.jre.default"; //$NON-NLS-1$
	public static final String JRE_DEFAULT_PROCESSOR_TYPE_ID = "org.eclipse.wst.xsl.launching.processorType.jreDefault"; //$NON-NLS-1$

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

	private static ProcessorRegistry getProcessorRegistry()
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
		JAXPLaunchingPlugin.getDefault().savePluginPreferences();
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
		IProcessorInstall[] installs = JAXPRuntime.getProcessors(type.getId());
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

	public static IProcessorInvoker getProcessorInvoker(String invokerId)
	{
		return getInvokerRegistry().getProcessorInvoker(invokerId);
	}

	public static IProcessorInvoker[] getProcessorInvokers()
	{
		return getInvokerRegistry().getProcessorInvokers();
	}

	private static Preferences getPreferences()
	{
		return JAXPLaunchingPlugin.getDefault().getPluginPreferences();
	}

	public static void saveFeaturePreferences(Map<IProcessorType, Map<String, String>> typeFeatures, IProgressMonitor monitor) throws CoreException
	{
		if (monitor.isCanceled())
			return;
		try
		{
			monitor.beginTask(Messages.XSLTRuntime_5, 100); 
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
			JAXPRuntime.getPreferences().setValue(JAXPRuntime.PREF_FEATURE_XML, xml);
			monitor.worked(30);
			if (monitor.isCanceled())
				return;
			JAXPRuntime.savePreferences();
			monitor.worked(30);
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR,JAXPLaunchingPlugin.PLUGIN_ID,Messages.XSLTRuntime_6,e)); 
		}
		finally
		{
			monitor.done();
		}
	}

	public static void saveOutputPropertyPreferences(Map<IProcessorType, Map<String,String>> typeProperties, IProgressMonitor monitor) throws CoreException
	{
		if (monitor.isCanceled())
			return;
		try
		{
			monitor.beginTask(Messages.XSLTRuntime_7, 100); 
			OutputPropertyPreferences prefs = new OutputPropertyPreferences();
			for (IProcessorType type : typeProperties.keySet())
			{
				prefs.setOutputPropertyValues(type.getId(), typeProperties.get(type));
			}
			String xml = prefs.getAsXML();
			monitor.worked(40);
			if (monitor.isCanceled())
				return;
			JAXPRuntime.getPreferences().setValue(JAXPRuntime.PREF_OUTPUT_PROPERTIES_XML, xml);
			monitor.worked(30);
			if (monitor.isCanceled())
				return;
			JAXPRuntime.savePreferences();
			monitor.worked(30);
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR,JAXPLaunchingPlugin.PLUGIN_ID,Messages.XSLTRuntime_8,e)); 
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
			monitor.beginTask(Messages.XSLTRuntime_9, 100); 
			ProcessorPreferences prefs = new ProcessorPreferences();
			if (defaultInstall != null)
				prefs.setDefaultProcessorId(defaultInstall.getId());
			prefs.setProcessors(new ArrayList<IProcessorInstall>(Arrays.asList(installs)));
			String xml = prefs.getAsXML();
			monitor.worked(40);
			if (monitor.isCanceled())
				return;
			JAXPRuntime.getPreferences().setValue(JAXPRuntime.PREF_PROCESSOR_XML, xml);
			monitor.worked(30);
			if (monitor.isCanceled())
				return;
			JAXPRuntime.savePreferences();
			monitor.worked(30);
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR,JAXPLaunchingPlugin.PLUGIN_ID,Messages.XSLTRuntime_10,e)); 
		}
		finally
		{
			monitor.done();
		}
	}

	public static Map<String,String> createDefaultOutputProperties(String typeId)
	{
		Map<String,String> props = new HashMap<String,String>();
		if (JRE_DEFAULT_PROCESSOR_TYPE_ID.equals(typeId))
			props.put("indent", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
		else if (JAXPLaunchConfigurationConstants.XALAN_TYPE_ID.equals(typeId))
		{
			props.put("indent", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			props.put("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if (JAXPLaunchConfigurationConstants.SAXON_TYPE_ID.equals(typeId))
		{
			props.put("indent", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			props.put("{http://saxon.sf.net/}indent-spaces", "4"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if (JAXPLaunchConfigurationConstants.SAXON_1_0_TYPE_ID.equals(typeId))
		{
			props.put("indent", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			props.put("{http://saxon.sf.net/}indent-spaces", "4"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return props;
	}
}
