/*******************************************************************************
 * Copyright (c) 2001, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.osgi.framework.BundleContext;

public class DTDCorePlugin extends Plugin {
	private static DTDCorePlugin instance;

	static final String ORG_ECLIPSE_WST_XML_CORE = "org.eclipse.wst.xml.core"; //$NON-NLS-1$
	static final String CATALOG_CONTRIBUTIONS = "catalogContributions"; //$NON-NLS-1$

	class RegistryChangeListener implements IRegistryChangeListener {
		@Override
		public void registryChanged(IRegistryChangeEvent event) {
			if (event.getExtensionDeltas(ORG_ECLIPSE_WST_XML_CORE, CATALOG_CONTRIBUTIONS).length > 0) {
				resetKnownURIs();
			}
		}
	}

	private Map<String, String[]> fKnownURIs;
	private IRegistryChangeListener registryChangeListener;

	public synchronized static DTDCorePlugin getInstance() {
		return instance;
	}

	public static DTDCorePlugin getPlugin() {
		return instance;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		registryChangeListener = new RegistryChangeListener();
		Platform.getExtensionRegistry().addRegistryChangeListener(registryChangeListener, ORG_ECLIPSE_WST_XML_CORE);
	}

	public DTDCorePlugin() {
		super();
		instance = this;
	}

	public void resetKnownURIs() {
		fKnownURIs = null;
	}

	public Map<String, String[]> getKnownURIs() {
		if (fKnownURIs == null) {
			fKnownURIs = new HashMap<>();
			IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(XMLCorePlugin.getDefault().getBundle().getSymbolicName(), CATALOG_CONTRIBUTIONS);
			IConfigurationElement[] configurationElements = extension.getConfigurationElements();
			Map<String, List<String>> known = new HashMap<>();
			for (int i = 0; i < configurationElements.length; i++) {
				String contributor = configurationElements[i].getNamespaceIdentifier();
				if (!known.containsKey(contributor)) {
					known.put(contributor, new ArrayList<>());
				}
				IConfigurationElement[] elements = configurationElements[i].getChildren();
				for (int j = 0; j < elements.length; j++) {
					String uri = elements[j].getAttribute("uri");
					if (uri != null && uri.length() > 0) {
						known.get(contributor).add(uri);
					}
				}
			}
			known.forEach((contributor, uris) -> {
				fKnownURIs.put(contributor, uris.toArray(new String[uris.size()]));
			});
		}
		return fKnownURIs;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Platform.getExtensionRegistry().removeRegistryChangeListener(registryChangeListener);
		super.stop(context);
	}
}
