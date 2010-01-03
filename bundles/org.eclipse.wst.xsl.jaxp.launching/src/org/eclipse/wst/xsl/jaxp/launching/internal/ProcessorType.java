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
import org.eclipse.wst.xsl.jaxp.launching.IAttribute;
import org.eclipse.wst.xsl.jaxp.launching.IOutputProperty;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorType;
import org.eclipse.wst.xsl.jaxp.launching.ITransformerFactory;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;

public class ProcessorType implements IProcessorType {
	private static final String DESC_SUFFIX = ".DESC"; //$NON-NLS-1$
	private static final String TYPE_SUFFIX = ".TYPE"; //$NON-NLS-1$

	private final String id;
	private final String name;
	private final ITransformerFactory[] transformerFactories;
	private final URL attributePropertiesURL;
	private final URL outputPropertiesURL;

	private IAttribute[] attributes;
	private IOutputProperty[] outputProperties;
	private Map<String, String> outputPropertyValues;
	private Map<String, String> attributeValues;

	public ProcessorType(String id, String name, URL attributePropertiesURL,
			URL outputPropertiesURL, Map<String, String> attributeValues,
			Map<String, String> outputPropertyValues,
			ITransformerFactory[] transformerFactories) {
		this.id = id;
		this.name = name;
		this.attributePropertiesURL = attributePropertiesURL;
		this.outputPropertiesURL = outputPropertiesURL;
		this.attributeValues = attributeValues;
		this.transformerFactories = transformerFactories;
		this.outputPropertyValues = outputPropertyValues;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return name;
	}

	public Map<String, String> getAttributeValues() {
		return attributeValues;
	}

	public IAttribute[] getAttributes() {
		if (attributes == null) {
			if (attributePropertiesURL != null)
				attributes = loadAttributes();
			else
				attributes = new IAttribute[0];
		}
		return attributes;
	}

	public Map<String, String> getOutputPropertyValues() {
		return outputPropertyValues;
	}

	public boolean isJREDefault() {
		return JAXPRuntime.JRE_DEFAULT_PROCESSOR_TYPE_ID.equals(id);
	}

	public ITransformerFactory[] getTransformerFactories() {
		return transformerFactories;
	}

	public ITransformerFactory getDefaultTransformerFactory() {
		if (transformerFactories.length > 0)
			return transformerFactories[0];
		return null;
	}

	public IOutputProperty[] getOutputProperties() {
		if (outputProperties == null) {
			if (outputPropertiesURL != null)
				outputProperties = loadOutputProperties();
			else
				outputProperties = new IOutputProperty[0];
		}
		return outputProperties;
	}

	private IOutputProperty[] loadOutputProperties() {
		BufferedInputStream is = null;
		List<OutputProperty> outputs = new ArrayList<OutputProperty>();
		Properties props = new Properties();
		try {

			is = new BufferedInputStream(outputPropertiesURL.openStream());
			props.load(is);
			for (Object element : props.keySet()) {
				String key = (String) element;
				if (!key.endsWith(DESC_SUFFIX)) {
					String name = key;
					String uri = props.getProperty(key);
					String desc = props.getProperty(key + DESC_SUFFIX);
					if (uri != null && name != null && desc != null) {
						OutputProperty prop = new OutputProperty(uri.trim(),
								desc);
						outputs.add(prop);
					} else {
						JAXPLaunchingPlugin.log(new CoreException(new Status(
								IStatus.WARNING, JAXPLaunchingPlugin.PLUGIN_ID,
								Messages.ProcessorType_2 + outputPropertiesURL
										+ Messages.ProcessorType_3 + key)));
					}
				}
			}
		} catch (IOException e) {
			JAXPLaunchingPlugin.log(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					JAXPLaunchingPlugin.log(e);
				}
			}
		}
		return outputs.toArray(new IOutputProperty[0]);
	}

	private IAttribute[] loadAttributes() {
		BufferedInputStream is = null;
		List<Attribute> attributesList = new ArrayList<Attribute>();
		try {
			is = new BufferedInputStream(attributePropertiesURL.openStream());
			Properties props = new Properties();
			props.load(is);

			for (Object element : props.keySet()) {
				String key = (String) element;
				if (!key.endsWith(DESC_SUFFIX) && !key.endsWith(TYPE_SUFFIX)) {
					String uri = props.getProperty(key);
					String type = props.getProperty(key + TYPE_SUFFIX);
					String desc = props.getProperty(key + DESC_SUFFIX);
					if (uri != null && type != null && desc != null) {
						Attribute attribute = new Attribute(uri.trim(), type
								.trim(), desc);
						attributesList.add(attribute);
					} else {
						JAXPLaunchingPlugin.log(new CoreException(new Status(
								IStatus.WARNING, JAXPLaunchingPlugin.PLUGIN_ID,
								Messages.ProcessorType_4
										+ attributePropertiesURL
										+ Messages.ProcessorType_5 + key)));
					}
				}
			}

		} catch (IOException e) {
			JAXPLaunchingPlugin.log(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					JAXPLaunchingPlugin.log(e);
				}
			}
		}
		IAttribute[] attributes = attributesList.toArray(new IAttribute[0]);
		Arrays.sort(attributes);
		return attributes;
	}
}