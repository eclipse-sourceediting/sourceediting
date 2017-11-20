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
package org.eclipse.wst.xsl.jaxp.launching.internal;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.wst.xsl.jaxp.debug.invoker.PipelineDefinition;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TransformDefinition;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TypedValue;
import org.eclipse.wst.xsl.jaxp.launching.IAttribute;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInstall;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorType;
import org.eclipse.wst.xsl.jaxp.launching.ITransformerFactory;
import org.eclipse.wst.xsl.jaxp.launching.JAXPLaunchConfigurationConstants;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;
import org.eclipse.wst.xsl.jaxp.launching.LaunchAttributes;
import org.eclipse.wst.xsl.jaxp.launching.LaunchProperties;
import org.eclipse.wst.xsl.launching.config.BaseLaunchHelper;
import org.eclipse.wst.xsl.launching.config.LaunchAttribute;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;
import org.eclipse.wst.xsl.launching.config.PreferenceUtil;
import org.w3c.dom.Document;

public class LaunchHelper extends BaseLaunchHelper {
	private final LaunchAttributes attributes;
	private final LaunchProperties outputProperties;
	private final ITransformerFactory transformerFactory;

	public LaunchHelper(ILaunchConfiguration configuration)
			throws CoreException {
		super(configuration);
		attributes = hydrateAttributes(configuration);
		outputProperties = hydrateOutputProperties(configuration);
		transformerFactory = hydrateTransformerFactory(configuration);
	}

	public LaunchProperties getProperties() {
		return outputProperties;
	}

	public LaunchAttributes getAttributes() {
		return attributes;
	}

	public void save(File file) throws CoreException {
		BufferedWriter writer = null;
		try {
			// ensure it exists
			file.createNewFile();
			writer = new BufferedWriter(new FileWriter(file));
			PipelineDefinition pdef = new PipelineDefinition();
			for (Iterator<?> iter = attributes.getAttributes().iterator(); iter
					.hasNext();) {
				LaunchAttribute att = (LaunchAttribute) iter.next();
				pdef.addAttribute(new TypedValue(att.uri,
						TypedValue.TYPE_STRING, att.value));
			}
			for (Iterator<?> iter = pipeline.getTransformDefs().iterator(); iter
					.hasNext();) {
				LaunchTransform lt = (LaunchTransform) iter.next();
				TransformDefinition tdef = new TransformDefinition();
				URL url = pathToURL(lt.getLocation());
				tdef.setStylesheetURL(url.toExternalForm());
				tdef.setResolverClass(lt.getResolver());
				for (Iterator<?> iterator = lt.getParameters().iterator(); iterator
						.hasNext();) {
					LaunchAttribute att = (LaunchAttribute) iterator.next();
					tdef.addParameter(new TypedValue(att.uri,
							TypedValue.TYPE_STRING, att.getResolvedValue()));
				}
				// set the output props for the LAST transform only
				if (!iter.hasNext()) {
					for (Map.Entry<String, String> entry : outputProperties
							.getProperties().entrySet()) {
						String name = entry.getKey();
						String value = entry.getValue();
						if (name != null && value != null)
							tdef.setOutputProperty(name, value);
					}
				}
				pdef.addTransformDef(tdef);
			}

			Document doc = pdef.toXML();
			String s = PreferenceUtil.serializeDocument(doc);
			writer.write(s);
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					JAXPLaunchingPlugin.PLUGIN_ID, IStatus.ERROR,
					Messages.LaunchHelper_0, e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					JAXPLaunchingPlugin.PLUGIN_ID, IStatus.ERROR,
					Messages.LaunchHelper_1, e));
		} catch (ParserConfigurationException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					JAXPLaunchingPlugin.PLUGIN_ID, IStatus.ERROR,
					"ParserConfigurationException", e)); //$NON-NLS-1$
		} catch (TransformerException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					JAXPLaunchingPlugin.PLUGIN_ID, IStatus.ERROR,
					"TransformerException", e)); //$NON-NLS-1$
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					JAXPLaunchingPlugin.log(e);
				}
			}
		}
	}

	public static LaunchProperties hydrateOutputProperties(
			ILaunchConfiguration configuration) throws CoreException {
		LaunchProperties properties = new LaunchProperties();
		boolean usePreferenceProperties = false; // configuration.getAttribute(JAXPLaunchConfigurationConstants.ATTR_USE_PROPERTIES_FROM_PREFERENCES,
													// true);
		IProcessorType pt = getProcessorInstall(configuration)
				.getProcessorType();
		if (usePreferenceProperties) {
			for (Map.Entry<String, String> entry : pt.getOutputPropertyValues()
					.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue();
				properties.setProperty(name, value);
			}
		} else {
			String s = configuration.getAttribute(
					JAXPLaunchConfigurationConstants.ATTR_OUTPUT_PROPERTIES,
					(String) null);
			if (s != null && s.length() > 0) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(s
						.getBytes());
				properties = LaunchProperties.fromXML(inputStream);
			}
		}
		return properties;
	}

	private static LaunchAttributes hydrateAttributes(
			ILaunchConfiguration configuration) throws CoreException {
		LaunchAttributes attributes = new LaunchAttributes();
		boolean useDefaultAttributes = false; // configuration.getAttribute(JAXPLaunchConfigurationConstants.ATTR_USE_FEATURES_FROM_PREFERENCES,
												// true);
		if (useDefaultAttributes) {
			IProcessorType pt = getProcessorInstall(configuration)
					.getProcessorType();
			Map<String, String> fvals = pt.getAttributeValues();
			for (Map.Entry<String, String> entry : fvals.entrySet()) {
				String uri = entry.getKey();
				getAttribute(pt.getAttributes(), uri);
				attributes.addAttribute(new LaunchAttribute(uri,
						"string", entry.getValue())); //$NON-NLS-1$
			}
		} else {
			String s = configuration.getAttribute(
					JAXPLaunchConfigurationConstants.ATTR_ATTRIBUTES,
					(String) null);
			if (s != null && s.length() > 0) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(s
						.getBytes());
				attributes = LaunchAttributes.fromXML(inputStream);
			}
		}
		return attributes;
	}

	private static IAttribute getAttribute(IAttribute[] attributes, String uri) {
		for (IAttribute attribute : attributes) {
			if (attribute.getURI().equals(uri))
				return attribute;
		}
		return null;
	}

	public static IProcessorInstall getProcessorInstall(
			ILaunchConfiguration configuration) throws CoreException {
		boolean useDefaultProcessor = configuration.getAttribute(
				JAXPLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR,
				true);
		if (useDefaultProcessor)
			return JAXPRuntime.getDefaultProcessor();
		String processorId = configuration.getAttribute(
				JAXPLaunchConfigurationConstants.ATTR_PROCESSOR, ""); //$NON-NLS-1$
		IProcessorInstall processor = JAXPRuntime.getProcessor(processorId);
		return processor;
	}

	private static ITransformerFactory hydrateTransformerFactory(
			ILaunchConfiguration configuration) throws CoreException {
		IProcessorType type = getProcessorInstall(configuration)
				.getProcessorType();
		boolean useDefaultFactory = configuration.getAttribute(
				JAXPLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR,
				true);
		if (useDefaultFactory)
			return type.getDefaultTransformerFactory();

		String factoryId = configuration.getAttribute(
				JAXPLaunchConfigurationConstants.ATTR_TRANSFORMER_FACTORY,
				(String) null);
		if (factoryId == null)
			return null;

		for (ITransformerFactory factory : type.getTransformerFactories()) {
			if (factory.getFactoryClass().equals(factoryId))
				return factory;
		}
		return null;
	}

	public ITransformerFactory getTransformerFactory() {
		return transformerFactory;
	}
}
