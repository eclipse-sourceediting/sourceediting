/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.internal.modelhandler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.modelhandler.IDocumentTypeHandler;


public class PluginContributedFactoryReader {

	protected final String EXTENSION_POINT_ID = "contentTypeFactoryContribution"; //$NON-NLS-1$
	protected final String TAG_NAME = "factory"; //$NON-NLS-1$
	protected final String ATTR_CLASS = "class"; //$NON-NLS-1$
	protected final String ATTR_CONTENTTYPE = "contentTypeIdentiferId"; //$NON-NLS-1$
	protected final String ATTR_ADAPTERKEY = "adapterKeyClass"; //$NON-NLS-1$
	protected final String ATTR_REGISTERADAPTER = "registerAdapters"; //$NON-NLS-1$
	private static PluginContributedFactoryReader reader = null;

	protected PluginContributedFactoryReader() {
		super();
	}

	public List getFactories(String type) {
		return loadRegistry(type);
	}

	public List getFactories(IDocumentTypeHandler handler) {
		return loadRegistry(handler.getId());
	}

	public synchronized static PluginContributedFactoryReader getInstance() {
		if (reader == null) {
			reader = new PluginContributedFactoryReader();
		}
		return reader;
	}

	protected AdapterFactory loadFactoryFromConfigurationElement(IConfigurationElement element, Object requesterType) {
		AdapterFactory factory = null;
		if (element.getName().equals(TAG_NAME)) {
			String contentType = element.getAttribute(ATTR_CONTENTTYPE);
			if (!requesterType.equals(contentType))
				return null;
			String className = element.getAttribute(ATTR_CLASS);
			String adapterKeyClass = element.getAttribute(ATTR_ADAPTERKEY);
			String registerAdapters = element.getAttribute(ATTR_REGISTERADAPTER);

			// if className is null, then no one defined the extension point
			// for adapter factories
			if (className != null) {
				Plugin plugin = null;
				IPluginDescriptor descriptor = element.getDeclaringExtension().getDeclaringPluginDescriptor();
				try {
					plugin = descriptor.getPlugin();
				}
				catch (CoreException e) {
					// if an error occurs here, its probably that the plugin
					// could not be found/loaded
					org.eclipse.wst.sse.core.internal.Logger.logException("Could not find plugin: " + descriptor, e); //$NON-NLS-1$

				}
				if (plugin != null) {
					boolean useExtendedConstructor = false;
					boolean doRegisterAdapters = false;
					Object adapterKey = null;

					if (registerAdapters != null && registerAdapters.length() > 0 && Boolean.valueOf(registerAdapters).booleanValue()) {
						doRegisterAdapters = true;
					}
					if (adapterKeyClass != null) {
						try {
							ClassLoader classLoader = plugin.getClass().getClassLoader();
							Class aClass = classLoader != null ? classLoader.loadClass(adapterKeyClass) : Class.forName(adapterKeyClass);
							if (aClass != null) {
								useExtendedConstructor = true;
								adapterKey = aClass;
							}
							else {
								adapterKey = adapterKeyClass;
							}
						}
						catch (Throwable anyErrors) {
							adapterKey = adapterKeyClass;
						}
					}

					try {
						ClassLoader classLoader = plugin.getClass().getClassLoader();
						Class theClass = classLoader != null ? classLoader.loadClass(className) : Class.forName(className);
						if (useExtendedConstructor) {
							java.lang.reflect.Constructor[] ctors = theClass.getConstructors();
							for (int i = 0; i < ctors.length; i++) {
								Class[] paramTypes = ctors[i].getParameterTypes();
								if (ctors[i].isAccessible() && paramTypes.length == 2 && paramTypes[0].equals(Object.class) && paramTypes[1].equals(boolean.class)) {
									try {
										factory = (AdapterFactory) ctors[i].newInstance(new Object[]{adapterKey, new Boolean(doRegisterAdapters)});
									}
									catch (IllegalAccessException e) {
										throw new SourceEditingRuntimeException(e);
									}
									catch (IllegalArgumentException e) {
										throw new SourceEditingRuntimeException(e);
									}
									catch (InstantiationException e) {
										throw new SourceEditingRuntimeException(e);
									}
									catch (InvocationTargetException e) {
										throw new SourceEditingRuntimeException(e);
									}
									catch (ExceptionInInitializerError e) {
										throw new SourceEditingRuntimeException(e);
									}
								}
							}
						}
						if (factory == null) {
							factory = (AdapterFactory) element.createExecutableExtension(ATTR_CLASS);
						}
					}
					catch (ClassNotFoundException e) {
						throw new SourceEditingRuntimeException(e);
					}
					catch (CoreException e) {
						throw new SourceEditingRuntimeException(e);
					}
				}
			}
		}
		return factory;
	}

	protected List loadRegistry(Object contentType) {
		List factoryList = null; // new Vector();
		IPluginRegistry pluginRegistry = Platform.getPluginRegistry();
		IExtensionPoint point = pluginRegistry.getExtensionPoint(IModelManagerPlugin.ID, EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			if (elements.length > 0) {
				// this is called a lot, so don't create vector unless really
				// needed
				// TODO: could eventually cache in a hashtable, or something,
				// to avoid repeat processing
				factoryList = new Vector();
				for (int i = 0; i < elements.length; i++) {
					AdapterFactory factory = loadFactoryFromConfigurationElement(elements[i], contentType);
					if (factory != null)
						factoryList.add(factory);
				}
			}
		}
		return factoryList;
	}
}
