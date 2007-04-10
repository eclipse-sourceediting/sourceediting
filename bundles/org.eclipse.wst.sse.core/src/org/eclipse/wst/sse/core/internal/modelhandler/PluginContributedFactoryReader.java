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
package org.eclipse.wst.sse.core.internal.modelhandler;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;


/**
 * 
 * Clients can make use of IExecutableExtension to handle the optional adapter
 * class and key. Typically, many clients use a typical pattern of providing
 * an adapter class and key in their null argument constructor anyway, so
 * they'd only have to use IExecutableExtension if the factory was for more
 * than one.
 */
public class PluginContributedFactoryReader {
	//	protected final String ATTR_ADAPTERKEY = "adapterKeyClass";
	// //$NON-NLS-1$
	//	protected final String ATTR_REGISTERADAPTER = "registerAdapters";
	// //$NON-NLS-1$
	private static PluginContributedFactoryReader reader = null;

	public synchronized static PluginContributedFactoryReader getInstance() {
		if (reader == null) {
			reader = new PluginContributedFactoryReader();
		}
		return reader;
	}

	protected final String ATTR_CLASS = "class"; //$NON-NLS-1$
	protected final String ATTR_CONTENTTYPE = "contentTypeIdentiferId"; //$NON-NLS-1$

	protected final String EXTENSION_POINT_ID = "contentTypeFactoryContribution"; //$NON-NLS-1$
	protected final String TAG_NAME = "factory"; //$NON-NLS-1$

	protected PluginContributedFactoryReader() {
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
			if (!requesterType.equals(contentType))
				return null;
			String className = element.getAttribute(ATTR_CLASS);
			//			String adapterKeyClass = element.getAttribute(ATTR_ADAPTERKEY);
			//			String registerAdapters =
			// element.getAttribute(ATTR_REGISTERADAPTER);

			// if className is null, then no one defined the extension point
			// for adapter factories
			if (className != null) {
				try {
					factory = (INodeAdapterFactory) element.createExecutableExtension(ATTR_CLASS);
				} catch (CoreException e) {
					// if an error occurs here, its probably that the plugin
					// could not be found/loaded
					org.eclipse.wst.sse.core.internal.Logger.logException("Could not find class: " + className, e); //$NON-NLS-1$
				} catch (Exception e) {
					// if an error occurs here, its probably that the plugin
					// could not be found/loaded -- but in any case we just
					// want
					// to log the error and continue running and best we can.
					org.eclipse.wst.sse.core.internal.Logger.logException("Could not find class: " + className, e); //$NON-NLS-1$
				}
				//				if (plugin != null) {
				//					factory = oldAttributesCode(element, factory, className,
				// plugin);
				//
			}
		}

		return factory;
	}

	protected List loadRegistry(Object contentType) {
		List factoryList = null; // new Vector();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = extensionRegistry.getExtensionPoint(SSECorePlugin.ID, EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			if (elements.length > 0) {
				// this is called a lot, so don't create vector unless really
				// needed
				// TODO: could eventually cache in a hashtable, or something,
				// to avoid repeat processing
				factoryList = new Vector();
				for (int i = 0; i < elements.length; i++) {
					INodeAdapterFactory factory = loadFactoryFromConfigurationElement(elements[i], contentType);
					if (factory != null)
						factoryList.add(factory);
				}
			}
		}
		return factoryList;
	}
}
