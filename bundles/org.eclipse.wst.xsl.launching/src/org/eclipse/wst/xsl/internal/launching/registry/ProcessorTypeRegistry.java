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
package org.eclipse.wst.xsl.internal.launching.registry;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.eclipse.wst.xsl.internal.launching.ProcessorType;
import org.eclipse.wst.xsl.launching.FeaturePreferences;
import org.eclipse.wst.xsl.launching.IProcessorType;
import org.eclipse.wst.xsl.launching.OutputPropertyPreferences;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

public class ProcessorTypeRegistry
{
	protected static final String DESC_SUFFIX = ".DESC";
	protected static final String TYPE_SUFFIX = ".TYPE";

	private final List<IConfigurationElement> elements = new ArrayList<IConfigurationElement>();
	private IProcessorType[] installTypes;

	public ProcessorTypeRegistry()
	{
		ProcessorTypeRegistryReader registryReader = new ProcessorTypeRegistryReader();
		registryReader.addConfigs(this);
	}

	public IProcessorType[] getProcessorTypes()
	{
		if (installTypes == null)
		{
			List<ProcessorType> types = new ArrayList<ProcessorType>();
			String featureXMLString = XSLTRuntime.getPreferences().getString(XSLTRuntime.PREF_FEATURE_XML);
			// If the preference was found, load VMs from it into memory
			FeaturePreferences prefs = null;
			if (featureXMLString.length() > 0)
			{
				try
				{
					ByteArrayInputStream inputStream = new ByteArrayInputStream(featureXMLString.getBytes());
					prefs = FeaturePreferences.fromXML(inputStream);
				}
				catch (CoreException e)
				{
					LaunchingPlugin.log(e);
				}
			}

			String propXMLString = XSLTRuntime.getPreferences().getString(XSLTRuntime.PREF_OUTPUT_PROPERTIES_XML);
			// If the preference was found, load VMs from it into memory
			OutputPropertyPreferences outputprefs = null;
			if (propXMLString.length() > 0)
			{
				try
				{
					ByteArrayInputStream inputStream = new ByteArrayInputStream(propXMLString.getBytes());
					outputprefs = OutputPropertyPreferences.fromXML(inputStream);
				}
				catch (CoreException e)
				{
					LaunchingPlugin.log(e);
				}
			}

			for (Iterator<IConfigurationElement> iter = elements.iterator(); iter.hasNext();)
			{
				IConfigurationElement element = (IConfigurationElement) iter.next();
				String id = element.getAttribute(ProcessorTypeRegistryReader.ATT_ID);
				String label = element.getAttribute(ProcessorTypeRegistryReader.ATT_LABEL);
				String transFactoryName = element.getAttribute(ProcessorTypeRegistryReader.ATT_TRANSFORMER_FACTORY_NAME);
				Map<String, String> featureValues = new HashMap<String, String>();
				Properties propertyValues = new Properties();
				if (prefs != null && prefs.getFeaturesValues(id) != null)
					featureValues.putAll(prefs.getFeaturesValues(id));
				if (outputprefs != null && outputprefs.getOutputPropertyValues(id) != null)
					propertyValues.putAll(outputprefs.getOutputPropertyValues(id));

				String outputProperties = element.getAttribute(ProcessorTypeRegistryReader.ATT_OUTPUT_PROPERTIES);
				URL outputPropertiesURL = FileLocator.find(Platform.getBundle(element.getContributor().getName()), new Path(outputProperties), null);
				String featureProperties = element.getAttribute(ProcessorTypeRegistryReader.ATT_FEATURE_PROPERTIES);
				URL featurePropertiesURL = FileLocator.find(Platform.getBundle(element.getContributor().getName()), new Path(featureProperties), null);
				
				types.add(new ProcessorType(id, label, featurePropertiesURL, outputPropertiesURL, featureValues, propertyValues, transFactoryName));
			}
			installTypes = (IProcessorType[]) types.toArray(new IProcessorType[0]);
		}
		return installTypes;
	}

	public IProcessorType[] getProcessorTypesExclJREDefault()
	{
		IProcessorType[] installTypes = getProcessorTypes();
		List<IProcessorType> exclTypes = new ArrayList<IProcessorType>(installTypes.length - 1);
		for (IProcessorType type : installTypes)
		{
			if (!type.isJREDefault())
				exclTypes.add(type);
		}
		return (IProcessorType[]) exclTypes.toArray(new IProcessorType[0]);
	}

	public void addType(IConfigurationElement element)
	{
		elements.add(element);
	}

	public IProcessorType getProcessorType(String id)
	{
		IProcessorType[] installTypes = getProcessorTypes();
		for (IProcessorType type : installTypes)
		{
			if (type.getId().equals(id))
				return type;
		}
		return null;
	}
}
