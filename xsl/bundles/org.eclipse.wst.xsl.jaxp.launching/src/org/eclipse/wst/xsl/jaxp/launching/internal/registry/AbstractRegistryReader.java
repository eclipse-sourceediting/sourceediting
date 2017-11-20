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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xsl.jaxp.launching.internal.JAXPLaunchingPlugin;
import org.eclipse.wst.xsl.jaxp.launching.internal.Messages;

public abstract class AbstractRegistryReader
{
	public static final String ATT_CLASS = "class"; //$NON-NLS-1$
	public static final String TAG_DESCRIPTION = "description"; //$NON-NLS-1$

	protected static void logError(IConfigurationElement element, String text)
	{
		IExtension extension = element.getDeclaringExtension();
		StringBuffer buf = new StringBuffer();
		buf.append("Plugin " + extension.getNamespaceIdentifier() + Messages.AbstractRegistryReader_3 + extension.getExtensionPointUniqueIdentifier()); //$NON-NLS-1$ 
		buf.append("\n" + text); //$NON-NLS-1$
		JAXPLaunchingPlugin.log(new CoreException(new Status(IStatus.ERROR, JAXPLaunchingPlugin.PLUGIN_ID, IStatus.OK, buf.toString(), null)));
	}

	protected static void logMissingAttribute(IConfigurationElement element, String attributeName)
	{
		logError(element, Messages.AbstractRegistryReader_5 + attributeName + Messages.AbstractRegistryReader_6); 
	}

	protected static void logMissingElement(IConfigurationElement element, String elementName)
	{
		logError(element, Messages.AbstractRegistryReader_7 + elementName + Messages.AbstractRegistryReader_8);
	}

	protected static void logUnknownElement(IConfigurationElement element)
	{
		logError(element, Messages.AbstractRegistryReader_9 + element.getName()); 
	}

	public static IExtension[] orderExtensions(IExtension[] extensions)
	{
		IExtension[] sortedExtension = new IExtension[extensions.length];
		System.arraycopy(extensions, 0, sortedExtension, 0, extensions.length);
		Comparator<IExtension> comparer = new Comparator<IExtension>()
		{
			public int compare(IExtension arg0, IExtension arg1)
			{
				String s1 = arg0.getNamespaceIdentifier();
				String s2 = arg1.getNamespaceIdentifier();
				return s1.compareToIgnoreCase(s2);
			}
		};
		Collections.sort(Arrays.asList(sortedExtension), comparer);
		return sortedExtension;
	}

	protected abstract boolean readElement(IConfigurationElement element);

	protected void readElementChildren(IConfigurationElement element)
	{
		readElements(element.getChildren());
	}

	protected void readElements(IConfigurationElement[] elements)
	{
		for (int i = 0; i < elements.length; i++)
		{
			if (!readElement(elements[i]))
				logUnknownElement(elements[i]);
		}
	}

	protected void readExtension(IExtension extension)
	{
		readElements(extension.getConfigurationElements());
	}

	public void readRegistry(IExtensionRegistry registry, String pluginId, String extensionPoint)
	{
		IExtensionPoint point = registry.getExtensionPoint(pluginId, extensionPoint);
		if (point == null)
			return;
		IExtension[] extensions = point.getExtensions();
		extensions = orderExtensions(extensions);
		for (IExtension element : extensions)
			readExtension(element);
	}

	public static String getDescription(IConfigurationElement configElement)
	{
		IConfigurationElement[] children = configElement.getChildren(TAG_DESCRIPTION);
		if (children.length >= 1)
		{
			return children[0].getValue();
		}
		return ""; //$NON-NLS-1$
	}

	public static String getClassValue(IConfigurationElement configElement, String classAttributeName)
	{
		String className = configElement.getAttribute(classAttributeName);
		if (className != null)
			return className;
		IConfigurationElement[] candidateChildren = configElement.getChildren(classAttributeName);
		if (candidateChildren.length == 0)
			return null;

		return candidateChildren[0].getAttribute(ATT_CLASS);
	}
}