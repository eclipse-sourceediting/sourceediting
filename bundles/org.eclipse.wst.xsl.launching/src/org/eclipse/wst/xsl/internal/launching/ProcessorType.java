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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xsl.launching.IFeature;
import org.eclipse.wst.xsl.launching.IOutputProperty;
import org.eclipse.wst.xsl.launching.IProcessorType;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

public class ProcessorType implements IProcessorType
{
	private static final String DESC_SUFFIX = ".DESC";
	private static final String TYPE_SUFFIX = ".TYPE";

	private final String id;
	private final String name;
	private final String transFactoryName;
	private final URL featurePropertiesURL;
	private final URL outputPropertiesURL;

	private IFeature[] features;
	private IOutputProperty[] outputProperties;
	private Properties outputPropertyValues;
	private Map featureValues;

	public ProcessorType(String id, String name, URL featurePropertiesURL, URL outputPropertiesURL, Map featureValues, Properties outputPropertyValues, String transFactoryName)
	{
		this.id = id;
		this.name = name;
		this.featurePropertiesURL = featurePropertiesURL;
		this.outputPropertiesURL = outputPropertiesURL;
		this.featureValues = featureValues;
		this.transFactoryName = transFactoryName;
		this.outputPropertyValues = outputPropertyValues;
	}

	public String getId()
	{
		return id;
	}

	public String getLabel()
	{
		return name;
	}

	public Map getFeatureValues()
	{
		return featureValues;
	}

	public IFeature[] getFeatures()
	{
		if (features == null && featurePropertiesURL != null)
			features = loadFeatures();
		else
			features = new IFeature[0];
		return features;
	}

	public Properties getOutputPropertyValues()
	{
		return outputPropertyValues;
	}

	public boolean isJREDefault()
	{
		return XSLTRuntime.JRE_DEFAULT_PROCESSOR_TYPE_ID.equals(id);
	}

	public String getTransformerFactoryName()
	{
		return transFactoryName;
	}

	public IOutputProperty[] getOutputProperties()
	{
		if (outputProperties == null && outputPropertiesURL != null)
			outputProperties = loadOutputProperties();
		else
			outputProperties = new IOutputProperty[0];
		return outputProperties;
	}

	private IOutputProperty[] loadOutputProperties()
	{
		BufferedInputStream is = null;
		List outputs = new ArrayList();
		Properties props = new Properties();
		try
		{

			is = new BufferedInputStream(outputPropertiesURL.openStream());
			props.load(is);
			for (Object element : props.keySet())
			{
				String key = (String) element;
				if (!key.endsWith(DESC_SUFFIX))
				{
					String name = key;
					String uri = props.getProperty(key);
					String desc = props.getProperty(key + DESC_SUFFIX);
					if (uri != null && name != null && desc != null)
					{
						OutputProperty prop = new OutputProperty(name.trim(), uri.trim(), desc);
						outputs.add(prop);
					}
					else
					{
						LaunchingPlugin.log(new CoreException(new Status(IStatus.WARNING, LaunchingPlugin.PLUGIN_ID, "Output properties file " + outputPropertiesURL
								+ " not configured properly for key " + key)));
					}
				}
			}
		}
		catch (IOException e)
		{
			LaunchingPlugin.log(e);
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					LaunchingPlugin.log(e);
				}
			}
		}
		return (IOutputProperty[]) outputs.toArray(new IOutputProperty[0]);
	}

	private IFeature[] loadFeatures()
	{
		BufferedInputStream is = null;
		List featuresList = new ArrayList();
		try
		{
			is = new BufferedInputStream(featurePropertiesURL.openStream());
			Properties props = new Properties();
			props.load(is);

			for (Object element : props.keySet())
			{
				String key = (String) element;
				if (!key.endsWith(DESC_SUFFIX) && !key.endsWith(TYPE_SUFFIX))
				{
					String uri = props.getProperty(key);
					String type = props.getProperty(key + TYPE_SUFFIX);
					String desc = props.getProperty(key + DESC_SUFFIX);
					if (uri != null && type != null && desc != null)
					{
						Feature feature = new Feature(uri.trim(), type.trim(), desc);
						featuresList.add(feature);
					}
					else
					{
						LaunchingPlugin.log(new CoreException(new Status(IStatus.WARNING, LaunchingPlugin.PLUGIN_ID, "Feature properties file " + featurePropertiesURL
								+ " not configured properly for key " + key)));
					}
				}
			}

		}
		catch (IOException e)
		{
			LaunchingPlugin.log(e);
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					LaunchingPlugin.log(e);
				}
			}
		}
		IFeature[] features = (IFeature[]) featuresList.toArray(new IFeature[0]);
		Arrays.sort(features);
		return features;
	}
}