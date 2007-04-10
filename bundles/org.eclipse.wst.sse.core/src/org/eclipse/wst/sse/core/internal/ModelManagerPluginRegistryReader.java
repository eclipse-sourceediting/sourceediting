/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.osgi.framework.Bundle;


public class ModelManagerPluginRegistryReader {
	private static ModelManagerPluginRegistryReader reader = null;

	public synchronized static ModelManagerPluginRegistryReader getInstance() {
		if (reader == null) {
			reader = new ModelManagerPluginRegistryReader();
		}
		return reader;
	}

	protected final String ATTR_ADAPTERKEY = "adapterKeyClass"; //$NON-NLS-1$
	protected final String ATTR_CLASS = "class"; //$NON-NLS-1$
	protected final String ATTR_CONTENTTYPE = "type"; //$NON-NLS-1$
	protected final String ATTR_REGISTERADAPTER = "registerAdapters"; //$NON-NLS-1$

	protected final String EXTENSION_POINT_ID = "adaptOnCreateFactory"; //$NON-NLS-1$
	protected final String TAG_NAME = "AdaptOnCreateFactory"; //$NON-NLS-1$

	/**
	 * XMLEditorPluginRegistryReader constructor comment.
	 */
	protected ModelManagerPluginRegistryReader() {
		super();
	}

	public List getFactories(IDocumentTypeHandler handler) {
		return loadRegistry(handler.getId());
	}

	public List getFactories(String type) {
		return loadRegistry(type);
	}

	protected INodeAdapterFactory loadFactoryFromConfigurationElement(IConfigurationElement element, Object requesterType) {
		INodeAdapterFactory factory = null;
		if (element.getName().equals(TAG_NAME)) {
			String contentType = element.getAttribute(ATTR_CONTENTTYPE);
			if (!contentType.equals(requesterType))
				return null;
			String className = element.getAttribute(ATTR_CLASS);
			String adapterKeyClass = element.getAttribute(ATTR_ADAPTERKEY);
			String registerAdapters = element.getAttribute(ATTR_REGISTERADAPTER);

			// if className is null, then no one defined the extension point
			// for adapter factories
			if (className != null) {
				String name = element.getDeclaringExtension().getNamespace();
				Bundle bundle = null;
				try {
					bundle = Platform.getBundle(name);
				}
				catch (Exception e) {
					// if an error occurs here, its probably that the plugin
					// could not be found/loaded
					Logger.logException("Could not find bundle: " + name, e); //$NON-NLS-1$

				}
				if (bundle != null) {
					boolean useExtendedConstructor = false;
					boolean doRegisterAdapters = false;
					Object adapterKey = null;

					if (registerAdapters != null && registerAdapters.length() > 0 && Boolean.valueOf(registerAdapters).booleanValue()) {
						doRegisterAdapters = true;
					}
					if (adapterKeyClass != null) {
						try {
							Class aClass = null;
							// aClass = classLoader != null ?
							// classLoader.loadClass(adapterKeyClass) :
							// Class.forName(adapterKeyClass);
							if (bundle.getState() != Bundle.UNINSTALLED) {
								aClass = bundle.loadClass(adapterKeyClass);
							}
							else {
								aClass = Class.forName(adapterKeyClass);
							}
							if (aClass != null) {
								useExtendedConstructor = true;
								adapterKey = aClass;
							}
							else {
								adapterKey = adapterKeyClass;
							}
						}
						catch (Exception anyErrors) {
							adapterKey = adapterKeyClass;
						}
					}

					try {
						Class theClass = null;
						// Class theClass = classLoader != null ?
						// classLoader.loadClass(className) :
						// Class.forName(className);
						if (bundle.getState() != Bundle.UNINSTALLED) {
							theClass = bundle.loadClass(className);
						}
						else {
							theClass = Class.forName(className);
						}
						if (useExtendedConstructor) {
							java.lang.reflect.Constructor[] ctors = theClass.getConstructors();
							for (int i = 0; i < ctors.length; i++) {
								Class[] paramTypes = ctors[i].getParameterTypes();
								if (ctors[i].isAccessible() && paramTypes.length == 2 && paramTypes[0].equals(Object.class) && paramTypes[1].equals(boolean.class)) {
									try {
										factory = (INodeAdapterFactory) ctors[i].newInstance(new Object[]{adapterKey, new Boolean(doRegisterAdapters)});
									}
									catch (IllegalAccessException e) {
										// log for now, unless we find reason
										// not to
										Logger.log(Logger.INFO, e.getMessage());
									}
									catch (IllegalArgumentException e) {
										// log for now, unless we find reason
										// not to
										Logger.log(Logger.INFO, e.getMessage());
									}
									catch (InstantiationException e) {
										// log for now, unless we find reason
										// not to
										Logger.log(Logger.INFO, e.getMessage());
									}
									catch (InvocationTargetException e) {
										// log for now, unless we find reason
										// not to
										Logger.log(Logger.INFO, e.getMessage());
									}
									catch (ExceptionInInitializerError e) {
										// log or now, unless we find reason
										// not to
										Logger.log(Logger.INFO, e.getMessage());
									}
								}
							}
						}
						if (factory == null) {
							factory = (INodeAdapterFactory) element.createExecutableExtension(ATTR_CLASS);
						}
					}
					catch (ClassNotFoundException e) {
						// log or now, unless we find reason not to
						Logger.log(Logger.INFO, e.getMessage());
					}
					catch (CoreException e) {
						// log or now, unless we find reason not to
						Logger.log(Logger.INFO, e.getMessage());
					}
				}
			}
		}
		return factory;
	}

	protected List loadRegistry(Object contentType) {
		List factoryList = new Vector();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = extensionRegistry.getExtensionPoint(SSECorePlugin.ID, EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				INodeAdapterFactory factory = loadFactoryFromConfigurationElement(elements[i], contentType);
				if (factory != null)
					factoryList.add(factory);
			}
		}
		return factoryList;
	}
}
