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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.eclipse.wst.xsl.launching.ProcessorRegistry;

public class ProcessorRegistryReader extends AbstractRegistryReader
{
	public static final String TAG_processor = "processor";
	public static final String ATT_ID = "id";
	public static final String ATT_LABEL = "label";
	public static final String ATT_TYPE_ID = "processorTypeId";
	public static final String ATT_DEBUGGER_ID = "debuggerId";
	public static final String ATT_CLASSPATH = "classpath";
	public static final String ATT_SUPPORTS = "supports";

	private ProcessorRegistry registry;

	@Override
	protected boolean readElement(IConfigurationElement element)
	{
		if (!element.getName().equals(TAG_processor))
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

		String processorTypeId = element.getAttribute(ATT_TYPE_ID);
		if (processorTypeId == null)
		{
			logMissingAttribute(element, ATT_TYPE_ID);
			return true;
		}

		String classpath = element.getAttribute(ATT_CLASSPATH);
		if (classpath == null)
		{
			logMissingAttribute(element, ATT_CLASSPATH);
			return true;
		}

		String debuggerId = element.getAttribute(ATT_DEBUGGER_ID);

		String supports = element.getAttribute(ATT_SUPPORTS);
		if (classpath == null)
		{
			logMissingAttribute(element, ATT_SUPPORTS);
			return true;
		}

		registry.addProcessor(element.getContributor().getName(), id, label, processorTypeId, classpath, debuggerId, supports);

		return true;
	}

	public void readElement(ProcessorRegistry registry, IConfigurationElement element)
	{
		this.registry = registry;
		readElement(element);
	}

	public void addConfigs(ProcessorRegistry registry)
	{
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		this.registry = registry;
		readRegistry(extensionRegistry, LaunchingPlugin.PLUGIN_ID, "processor");
	}
}
