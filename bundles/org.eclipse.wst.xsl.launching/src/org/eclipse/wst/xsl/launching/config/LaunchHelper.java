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
package org.eclipse.wst.xsl.launching.config;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.eclipse.wst.xsl.internal.launching.PreferenceUtil;
import org.eclipse.wst.xsl.invoker.config.PipelineDefinition;
import org.eclipse.wst.xsl.invoker.config.TransformDefinition;
import org.eclipse.wst.xsl.invoker.config.TypedValue;
import org.eclipse.wst.xsl.launching.IFeature;
import org.eclipse.wst.xsl.launching.IOutputProperty;
import org.eclipse.wst.xsl.launching.IProcessorInstall;
import org.eclipse.wst.xsl.launching.IProcessorType;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.XSLTRuntime;
import org.w3c.dom.Document;

public class LaunchHelper
{
	private final URL source;
	private final File target;
	private final LaunchFeatures features;
	private final LaunchPipeline pipeline;
	private final LaunchProperties outputProperties;
	private int requestPort = -1;
	private int eventPort = -1;
	private final boolean openFileOnCompletion;

	public LaunchHelper(ILaunchConfiguration configuration) throws CoreException
	{
		source = hydrateSourceFileURL(configuration);
		target = hydrateOutputFile(configuration);
		features = hydrateFeatures(configuration);
		outputProperties = hydrateOutputProperties(configuration);
		pipeline = hydratePipeline(configuration);
		openFileOnCompletion = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_OPEN_FILE, true);
	}

	public int getRequestPort()
	{
		if (requestPort == -1)
			requestPort = findFreePort();
		return requestPort;
	}

	public int getEventPort()
	{
		if (eventPort == -1)
			eventPort = findFreePort();
		return eventPort;
	}

	public LaunchProperties getProperties()
	{
		return outputProperties;
	}

	public LaunchFeatures getFeatures()
	{
		return features;
	}

	public LaunchPipeline getPipeline()
	{
		return pipeline;
	}

	public URL getSource()
	{
		return source;
	}

	public File getTarget()
	{
		return target;
	}

	public void save(File file) throws CoreException
	{
		BufferedWriter writer = null;
		try
		{
			// ensure it exists
			file.createNewFile();
			writer = new BufferedWriter(new FileWriter(file));
			PipelineDefinition pdef = new PipelineDefinition();
			for (Iterator<?> iter = features.getFeatures().iterator(); iter.hasNext();)
			{
				LaunchAttribute att = (LaunchAttribute) iter.next();
				pdef.addFeature(new TypedValue(att.uri, att.type, att.value));
			}
			for (Iterator<?> iter = pipeline.getTransformDefs().iterator(); iter.hasNext();)
			{
				LaunchTransform lt = (LaunchTransform) iter.next();
				TransformDefinition tdef = new TransformDefinition();
				URL url = pathToURL(lt.getLocation());
				tdef.setStylesheetURL(url.toExternalForm());
				tdef.setResolverClass(lt.getResolver());
				for (Iterator<?> iterator = lt.getParameters().iterator(); iterator.hasNext();)
				{
					LaunchAttribute att = (LaunchAttribute) iterator.next();
					tdef.addParameter(new TypedValue(att.uri, att.type, att.getResolvedValue()));
				}
				// set the output props for the LAST transform only
				if (!iter.hasNext())
				{
					for (Iterator<?> iterator = outputProperties.getProperties().entrySet().iterator(); iterator.hasNext();)
					{
						Map.Entry entry = (Map.Entry) iterator.next();
						String name = (String) entry.getKey();
						String value = (String) entry.getValue();
						if (name != null && value != null)
							tdef.setOutputProperty(name, value);
					}
				}
				pdef.addTransformDef(tdef);
			}

			Document doc = pdef.toXML();
			String s = PreferenceUtil.serializeDocument(doc);
			writer.write(s);
		}
		catch (FileNotFoundException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, Messages.getString("LaunchHelper.0"), e)); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, Messages.getString("LaunchHelper.1"), e)); //$NON-NLS-1$
		}
		catch (ParserConfigurationException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, "ParserConfigurationException", e)); //$NON-NLS-1$
		}
		catch (TransformerException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, "TransformerException", e)); //$NON-NLS-1$
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
					LaunchingPlugin.log(e);
				}
			}
		}
	}

	public static LaunchProperties hydrateOutputProperties(ILaunchConfiguration configuration) throws CoreException
	{
		LaunchProperties properties = null;
		boolean usePreferenceProperties = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_USE_PROPERTIES_FROM_PREFERENCES, true);
		IProcessorType defaultType = XSLTRuntime.getJREDefaultProcessor().getProcessorType();
		IProcessorType pt = getProcessorInstall(configuration).getProcessorType();
		if (usePreferenceProperties)
		{
			properties = new LaunchProperties();
			// get the standard properties
			for (Object element : defaultType.getOutputPropertyValues().entrySet())
			{
				Map.Entry entry = (Map.Entry) element;
				String name = (String) entry.getKey();
				String value = (String) entry.getValue();
				properties.setProperty(name, value);
			}
			// get the processor-specific properties
			if (!pt.equals(defaultType))
			{
				for (Object element : pt.getOutputPropertyValues().entrySet())
				{
					Map.Entry entry = (Map.Entry) element;
					String name = (String) entry.getKey();
					String value = (String) entry.getValue();
					properties.setProperty(name, value);
				}
			}
		}
		else
		{
			String s = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_OUTPUT_PROPERTIES, (String) null);
			if (s != null && s.length() > 0)
			{
				ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
				properties = LaunchProperties.fromXML(inputStream);
				// ensure all properties pertain to the current processor type
				IOutputProperty[] defaultProps = defaultType.getOutputProperties();
				IOutputProperty[] specificProps = pt.getOutputProperties();
				IOutputProperty[] props = new IOutputProperty[specificProps.length + defaultProps.length];
				System.arraycopy(specificProps, 0, props, 0, specificProps.length);
				System.arraycopy(defaultProps, 0, props, specificProps.length, defaultProps.length);
				for (Iterator<?> iterator = properties.getProperties().keySet().iterator(); iterator.hasNext();)
				{
					String key = (String) iterator.next();
					boolean found = false;
					for (IOutputProperty outputProperty : props)
					{
						if (outputProperty.getURI().equals(key))
						{
							found = true;
						}
					}
					if (!found)
						properties.removeProperty(key);
				}
			}
		}
		return properties;
	}

	private LaunchFeatures hydrateFeatures(ILaunchConfiguration configuration) throws CoreException
	{
		LaunchFeatures features = null;
		boolean useDefaultFeatures = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_USE_FEATURES_FROM_PREFERENCES, true);
		if (useDefaultFeatures)
		{
			features = new LaunchFeatures();
			IProcessorType pt = getProcessorInstall(configuration).getProcessorType();
			Map<?, ?> fvals = pt.getFeatureValues();
			for (Iterator<?> iter = fvals.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();
				String uri = (String) entry.getKey();
				IFeature feature = getFeature(pt.getFeatures(), uri);
				features.addFeature(new LaunchAttribute(uri, feature.getType(), (String) entry.getValue()));
			}
		}
		else
		{
			String s = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_FEATURES, (String) null);
			if (s != null && s.length() > 0)
			{
				ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
				features = LaunchFeatures.fromXML(inputStream);
			}
		}
		return features;
	}

	private IFeature getFeature(IFeature[] features, String uri)
	{
		for (IFeature feature : features)
		{
			if (feature.getURI().equals(uri))
				return feature;
		}
		return null;
	}

	private static LaunchPipeline hydratePipeline(ILaunchConfiguration configuration) throws CoreException
	{
		LaunchPipeline pipeline = null;
		String s = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_PIPELINE, (String) null);
		if (s != null && s.length() > 0)
		{
			ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
			pipeline = LaunchPipeline.fromXML(inputStream);
		}
		return pipeline;
	}

	public static URL hydrateSourceFileURL(ILaunchConfiguration configuration) throws CoreException
	{
		IPath sourceFile = hydrateSourceFile(configuration);
		return pathToURL(sourceFile);
	}
	
	private static IPath hydrateSourceFile(ILaunchConfiguration configuration) throws CoreException
	{
		String sourceFileExpr = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_INPUT_FILE, (String) null);
		return getSubstitutedPath(sourceFileExpr);
	}

	private static URL pathToURL(IPath sourceFile) throws CoreException
	{
		URL url = null;
		try
		{
			url = sourceFile.toFile().toURL();
		}
		catch (MalformedURLException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, sourceFile.toString(), e));
		}
		return url;
	}

	public static File hydrateOutputFile(ILaunchConfiguration configuration) throws CoreException
	{
		IPath outputFile = null;
		boolean useDefaultOutputFile = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE, true);
		if (!useDefaultOutputFile)
		{
			String outputFileExpr = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_OUTPUT_FILE, (String) null);
			outputFile = getSubstitutedPath(outputFileExpr);
		}
		else
		{
			// TODO: where is the default output file? And must share this with
			// the value displayed in the UI.
			outputFile = (IPath) hydrateSourceFile(configuration);
			outputFile = outputFile.addFileExtension("out").addFileExtension("xml"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return outputFile.toFile();
	}

	private static IPath getSubstitutedPath(String path) throws CoreException
	{
		if (path != null)
		{
			path = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(path);
			return new Path(path);
		}
		return null;
	}

	public static IProcessorInstall getProcessorInstall(ILaunchConfiguration configuration) throws CoreException
	{
		boolean useDefaultProcessor = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, true);
		if (useDefaultProcessor)
			return XSLTRuntime.getDefaultProcessor();
		String processorId = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_PROCESSOR, ""); //$NON-NLS-1$
		IProcessorInstall processor = XSLTRuntime.getProcessor(processorId);
		return processor;
	}

	/**
	 * Returns a free port number on localhost, or -1 if unable to find a free
	 * port.
	 * 
	 * @return a free port number on localhost, or -1 if unable to find a free
	 *         port
	 */
	public static int findFreePort()
	{
		ServerSocket socket = null;
		try
		{
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		}
		catch (IOException e)
		{
			LaunchingPlugin.log(e);
		}
		finally
		{
			if (socket != null)
			{
				try
				{
					socket.close();
				}
				catch (IOException e)
				{
					LaunchingPlugin.log(e);
				}
			}
		}
		return -1;
	}

	public boolean getOpenFileOnCompletion()
	{
		return openFileOnCompletion;
	}
}
