/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.IExternalSchemaLocationProvider;

/**
 * A registry for all external schema location providers.
 * 
 */
public class ExternalSchemaLocationProviderRegistry {

	private static ExternalSchemaLocationProviderRegistry fRegistry;
	private IExternalSchemaLocationProvider[] fProviders;

	private ExternalSchemaLocationProviderRegistry() {
		fProviders = new IExternalSchemaLocationProvider[0];
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.wst.xml.core", "externalSchemaLocations"); //$NON-NLS-1$ //$NON-NLS-2$
		List providers = new ArrayList(elements.length);
		try {
			for (int i = 0; i < elements.length; i++) {
				providers.add(elements[i].createExecutableExtension("class"));
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		fProviders = new IExternalSchemaLocationProvider[elements.length];
		providers.toArray(fProviders);
	}

	public IExternalSchemaLocationProvider[] getProviders() {
		return fProviders;
	}

	public static synchronized ExternalSchemaLocationProviderRegistry getInstance() {
		if (fRegistry == null)
			fRegistry = new ExternalSchemaLocationProviderRegistry();
		return fRegistry;
	}
}
