/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.registry.embedded;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.util.Assert;
import org.eclipse.wst.sse.ui.Logger;


/**
 * This class just converts what's in the plugins registry into a form more
 * easily useable by others, the ContentTypeRegistry.
 */
class EmbeddedAdapterFactoryRegistryReader {
	protected final static String ATT_CLASS = "class"; //$NON-NLS-1$
	protected final static String EXTENSION_POINT_ID = "embeddedAdapterFactoryProvider"; //$NON-NLS-1$

	//
	protected final static String PLUGIN_ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$
	protected final static String TAG_NAME = "embeddedAdapterFactoryProvider"; //$NON-NLS-1$

	protected static EmbeddedAdapterFactoryProvider readElement(IConfigurationElement element) {
		EmbeddedAdapterFactoryProvider embeddedAdapterFactoryProvider = null;
		if (element.getName().equals(TAG_NAME)) {
			String className = element.getAttribute(ATT_CLASS);
			// if className is null, then no one defined the extension point
			// for design view
			if (className != null) {
				try {
					embeddedAdapterFactoryProvider = (EmbeddedAdapterFactoryProvider) element.createExecutableExtension(ATT_CLASS);
				} catch (CoreException e) {
					Logger.logException(e);
				}
			}
		}

		Assert.isNotNull(embeddedAdapterFactoryProvider, "Error reading embedded adapter factory registry"); //$NON-NLS-1$
		return embeddedAdapterFactoryProvider;
	}

	/**
	 * We simply require an 'add' method, of what ever it is we are to read
	 * into
	 */
	static void readRegistry(Set set) {
		IPluginRegistry pluginRegistry = Platform.getPluginRegistry();
		IExtensionPoint point = pluginRegistry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				EmbeddedAdapterFactoryProvider adapterFactoryProvider = readElement(elements[i]);
				set.add(adapterFactoryProvider);
				Logger.trace("Initialization", "adding to AdapterFactoryRegistry: " + adapterFactoryProvider.toString()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	//    protected final static String ADAPTER_CLASS = "adapterClass";
	// //$NON-NLS-1$
	//    protected final static String DOC_TYPE_ID = "docTypeId"; //$NON-NLS-1$
	//    protected final static String MIME_TYPE_LIST = "mimeTypeList";
	// //$NON-NLS-1$

	//
	/**
	 * ContentTypeRegistryReader constructor comment.
	 */
	EmbeddedAdapterFactoryRegistryReader() {
		super();
	}
}
