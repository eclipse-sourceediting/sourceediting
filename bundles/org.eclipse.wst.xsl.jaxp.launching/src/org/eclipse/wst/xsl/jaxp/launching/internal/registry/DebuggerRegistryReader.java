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
import org.eclipse.wst.xsl.jaxp.launching.internal.DebuggerDescriptor;
import org.eclipse.wst.xsl.jaxp.launching.internal.JAXPLaunchingPlugin;

public class DebuggerRegistryReader extends AbstractRegistryReader
{
	public static final String TAG_DEBUGGER = "debugger"; //$NON-NLS-1$
	public static final String ATT_ID = "id"; //$NON-NLS-1$
	public static final String ATT_CLASSNAME = "className"; //$NON-NLS-1$
	public static final String ATT_CLASSPATH = "classpath"; //$NON-NLS-1$
	public static final String ATT_NAME = "name"; //$NON-NLS-1$
	public static final String ATT_PROCESSOR_TYPE_ID = "processorTypeId"; //$NON-NLS-1$
	public static final String ATT_TRANSFORMER_FACTORY = "transformerFactoryClass"; //$NON-NLS-1$

	private DebuggerRegistry registry;

	@Override
	protected boolean readElement(IConfigurationElement element)
	{
		if (!element.getName().equals(TAG_DEBUGGER))
			return false;

		String id = element.getAttribute(ATT_ID);
		if (id == null)
		{
			logMissingAttribute(element, ATT_ID);
			return true;
		}

		String className = element.getAttribute(ATT_CLASSNAME);
		if (className == null)
		{
			logMissingAttribute(element, ATT_CLASSNAME);
			return true;
		}

		String classpath = element.getAttribute(ATT_CLASSPATH);
		if (classpath == null)
		{
			logMissingAttribute(element, ATT_CLASSPATH);
			return true;
		}

		String[] entries = classpath.split(";"); //$NON-NLS-1$
		for (int i = 0; i < entries.length; i++)
		{
			String string = entries[i];
			entries[i] = string.trim();
		}

		String name = element.getAttribute(ATT_NAME);
		if (name == null)
		{
			logMissingAttribute(element, ATT_NAME);
			return true;
		}

		String processorTypeId = element.getAttribute(ATT_PROCESSOR_TYPE_ID);
		if (processorTypeId == null)
		{
			logMissingAttribute(element, ATT_PROCESSOR_TYPE_ID);
			return true;
		}
		String tFact = element.getAttribute(ATT_TRANSFORMER_FACTORY);

		registry.addDebugger(new DebuggerDescriptor(id, element.getContributor().getName(), className, entries, name, processorTypeId, tFact));

		return true;
	}

	public void readElement(DebuggerRegistry registry, IConfigurationElement element)
	{
		this.registry = registry;
		readElement(element);
	}

	public void addConfigs(DebuggerRegistry registry)
	{
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		this.registry = registry;
		readRegistry(extensionRegistry, JAXPLaunchingPlugin.PLUGIN_ID, "debugger"); //$NON-NLS-1$
	}
}
