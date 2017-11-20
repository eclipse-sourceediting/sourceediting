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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xsl.jaxp.launching.internal.JAXPLaunchingPlugin;

public class ProcessorTypeRegistryReader extends AbstractRegistryReader
{
	public static final String TAG_processorType = "processorType"; //$NON-NLS-1$
	public static final String ATT_ID = "id"; //$NON-NLS-1$
	public static final String ATT_LABEL = "label"; //$NON-NLS-1$
	public static final String ATT_OUTPUT_PROPERTIES = "outputProperties"; //$NON-NLS-1$
	public static final String ATT_ATTRIBUTE_PROPERTIES = "attributeProperties"; //$NON-NLS-1$
	public static final String EL_TRANSFORMER_FACTORY = "transformerFactory"; //$NON-NLS-1$
	public static final String ATT_TRANSFORMER_FACTORY_NAME = "name"; //$NON-NLS-1$
	public static final String ATT_TRANSFORMER_FACTORY_CLASS = "factoryClass"; //$NON-NLS-1$

	private ProcessorTypeRegistry registry;

	@Override
	protected boolean readElement(IConfigurationElement element)
	{
		if (!element.getName().equals(TAG_processorType))
			return false;

		String id = element.getAttribute(ATT_ID);
		if (id == null)
		{
			logMissingAttribute(element, ATT_ID);
			return true;
		}

		String label = element.getAttribute(ATT_LABEL);
		if (label == null)
		{
			logMissingAttribute(element, ATT_LABEL);
			return true;
		}

		String outputProperties = element.getAttribute(ATT_OUTPUT_PROPERTIES);
		if (outputProperties == null)
		{
			logMissingAttribute(element, ATT_OUTPUT_PROPERTIES);
			return true;
		}

		String featureProperties = element.getAttribute(ATT_ATTRIBUTE_PROPERTIES);
		if (featureProperties == null)
		{
			logMissingAttribute(element, ATT_ATTRIBUTE_PROPERTIES);
			return true;
		}

		registry.addType(element);

		return true;
	}

	public void readElement(ProcessorTypeRegistry registry, IConfigurationElement element)
	{
		this.registry = registry;
		readElement(element);
	}

	protected void addConfigs(ProcessorTypeRegistry registry)
	{
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		this.registry = registry;
		readRegistry(extensionRegistry, JAXPLaunchingPlugin.PLUGIN_ID, "processorType"); //$NON-NLS-1$
	}
}
