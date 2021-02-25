/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.osgi.framework.BundleContext;

public class DTDCorePlugin extends Plugin {
	private static DTDCorePlugin instance;

	public static Map<String, String[]> KNOWN_URIS;

	public synchronized static DTDCorePlugin getInstance() {
		return instance;
	}

	public static DTDCorePlugin getPlugin() {
		return instance;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		KNOWN_URIS = new HashMap<>();
		IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(XMLCorePlugin.getDefault().getBundle().getSymbolicName(), "catalogContributions");
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
			KNOWN_URIS.put(contributor, uris.toArray(new String[uris.size()]));
		});
	}

	public DTDCorePlugin() {
		super();
		instance = this;
	}

}
