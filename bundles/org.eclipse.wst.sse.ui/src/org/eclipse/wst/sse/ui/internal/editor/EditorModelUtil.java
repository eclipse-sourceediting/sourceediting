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
package org.eclipse.wst.sse.ui.internal.editor;

import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.wst.sse.core.FactoryRegistry;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryProvider;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistryExtension;
import org.eclipse.wst.sse.ui.util.Assert;


/**
 * INTERNAL USAGE ONLY
 * 
 * @author nsd
 */
public class EditorModelUtil {

	public static void addFactoriesTo(final IStructuredModel structuredModel) {
		AdapterFactoryRegistry adapterRegistry = SSEUIPlugin.getDefault().getAdapterFactoryRegistry();
		String contentTypeId = structuredModel.getContentTypeIdentifier();

		Iterator adapterFactoryProviders = null;
		if (adapterRegistry instanceof AdapterFactoryRegistryExtension) {
			adapterFactoryProviders = ((AdapterFactoryRegistryExtension) adapterRegistry).getAdapterFactories(contentTypeId);
		}
		else {
			adapterFactoryProviders = adapterRegistry.getAdapterFactories();
		}

		FactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		Assert.isNotNull(factoryRegistry, "model in invalid state");
		// Add all those appropriate for this particular type of content
		while (adapterFactoryProviders.hasNext()) {
			try {
				final AdapterFactoryProvider provider = (AdapterFactoryProvider) adapterFactoryProviders.next();
				/*
				 * ContentType might have already been checked above, this
				 * check is here for backwards compatability for those that
				 * don't specify a content type
				 */
				if (provider.isFor(structuredModel.getModelHandler())) {
					Platform.run(new SafeRunnable("There was a problem adding adapter factories") {
						public void run() {
							provider.addAdapterFactories(structuredModel);
						}
					});
				}
			}
			catch (Exception e) {
				Logger.logException(e);
			}
		}
	}

}
