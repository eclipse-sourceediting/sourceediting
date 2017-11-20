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
import org.eclipse.wst.xsl.jaxp.launching.internal.ProcessorInvokerDescriptor;

public class InvokerRegistryReader extends AbstractRegistryReader
{
	public static final String TAG_INVOKE = "invoker"; //$NON-NLS-1$
	public static final String ATT_ID = "id"; //$NON-NLS-1$
	public static final String ATT_CLASS = "class"; //$NON-NLS-1$
	public static final String ATT_CLASSPATH = "classpath"; //$NON-NLS-1$

	private InvokerRegistry registry;

	@Override
	protected boolean readElement(IConfigurationElement element)
	{
		if (!element.getName().equals(TAG_INVOKE))
			return false;

		String id = element.getAttribute(ATT_ID);
		if (id == null)
		{
			logMissingAttribute(element, ATT_ID);
			return true;
		}

		String classname = element.getAttribute(ATT_CLASS);
		if (classname == null)
		{
			logMissingAttribute(element, ATT_CLASS);
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

		registry.addInvokerDescriptor(new ProcessorInvokerDescriptor(id, element.getContributor().getName(), classname, entries));

		return true;
	}

	public void readElement(InvokerRegistry registry, IConfigurationElement element)
	{
		this.registry = registry;
		readElement(element);
	}

	public void addConfigs(InvokerRegistry registry)
	{
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		this.registry = registry;
		readRegistry(extensionRegistry, JAXPLaunchingPlugin.PLUGIN_ID, "invoke"); //$NON-NLS-1$
	}
}
