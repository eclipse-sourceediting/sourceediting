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
package org.eclipse.wst.sse.ui.registry;

import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.ui.Logger;


/**
 * This class just converts what's in the plugins registry
 * into a form more easily useable by others, the ContentTypeRegistry.
 */
class AdapterFactoryRegistryReader {

	//
	protected final static String PLUGIN_ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$
	protected final static String EXTENSION_POINT_ID = "adapterFactoryDescription"; //$NON-NLS-1$
	protected final static String TAG_NAME = "adapterFactoryDescription"; //$NON-NLS-1$
	protected final static String ATT_CLASS = "class"; //$NON-NLS-1$

	//    protected final static String ADAPTER_CLASS = "adapterClass"; //$NON-NLS-1$
	//    protected final static String DOC_TYPE_ID = "docTypeId"; //$NON-NLS-1$
	//    protected final static String MIME_TYPE_LIST = "mimeTypeList"; //$NON-NLS-1$
	//
	/**
	 * ContentTypeRegistryReader constructor comment.
	 */
	AdapterFactoryRegistryReader() {
		super();
	}

	protected static AdapterFactoryProvider readElement(IConfigurationElement element) {
		AdapterFactoryProvider adapterFactoryProvider = null;
		if (element.getName().equals(TAG_NAME)) {
			try {
				adapterFactoryProvider = (AdapterFactoryProvider) element.createExecutableExtension(ATT_CLASS);
			}
			catch (Throwable e) {
				// if the provider throws any exception, just log and continue
				Logger.logException(e);
			}
		}
		return adapterFactoryProvider;
	}

	/**
	 * We simply require an 'add' method, of what ever it is we are to read into
	 */
	static void readRegistry(Set set) {
		IPluginRegistry pluginRegistry = Platform.getPluginRegistry();
		IExtensionPoint point = pluginRegistry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				AdapterFactoryProvider adapterFactoryProvider = readElement(elements[i]);
				if (adapterFactoryProvider != null) {
					set.add(adapterFactoryProvider);
					Logger.trace("Initialization", "adding to AdapterFactoryRegistry: " + adapterFactoryProvider.toString()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}
}
