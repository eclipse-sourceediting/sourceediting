/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodel;



import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.css.core.internal.Logger;
import org.eclipse.wst.css.core.metamodel.CSSProfile;
import org.eclipse.wst.sse.core.IModelManagerPlugin;



public class RegistryReader {

	//
	private String PLUGIN_ID = IModelManagerPlugin.ID; //$NON-NLS-1$
	private String EXTENSION_POINT_ID = "cssprofile"; //$NON-NLS-1$
	private String TAG_NAME = "profile"; //$NON-NLS-1$
	private String ATT_ID = "id"; //$NON-NLS-1$
	private String ATT_NAME = "name"; //$NON-NLS-1$
	private String ATT_URI = "uri"; //$NON-NLS-1$
	private String ATT_DEFAULT = "default"; //$NON-NLS-1$
	private String ATT_LOGGING = "logging"; // Hidden Option //$NON-NLS-1$

	/**
	 * Constructor for CSSMetaModelRegistryReader.
	 */
	public RegistryReader() {
		super();
	}

	/**
	 * 
	 */
	protected CSSProfile readElement(IConfigurationElement element) {
		CSSProfileImpl info = null;
		if (element.getName().equals(TAG_NAME)) {
			String strID = element.getAttribute(ATT_ID);
			String strNAME = element.getAttribute(ATT_NAME);
			String strURI = element.getAttribute(ATT_URI);

			if (strID != null || strURI != null) {
				Plugin plugin = null;
				IPluginDescriptor descriptor = element.getDeclaringExtension().getDeclaringPluginDescriptor();
				try {
					plugin = descriptor.getPlugin();
				}
				catch (CoreException e) {
					// if an error occurs here, its probably that the plugin
					// could not be found/loaded
					// Logger.log("could not find plugin: " + descriptor, e);
					// //$NON-NLS-1$
				}
				if (plugin != null) {
					Path path = new Path(strURI);
					URL url = plugin.find(path);
					if (url != null) {
						try {
							url = Platform.asLocalURL(url);
							info = new CSSProfileImpl(strID, url);
							info.setProfileName(strNAME);
							info.setDefault((element.getAttribute(ATT_DEFAULT) != null));
							info.setLogging((element.getAttribute(ATT_LOGGING) != null));
							info.setOwnerPluginID(descriptor.getUniqueIdentifier());
						}
						catch (java.io.IOException e) {
							// through
						}
					}
				}
			}

			if (info == null) {
				Logger.log(Logger.ERROR, "Error reading CSS Profile: " + strID); //$NON-NLS-1$
			}
		}
		return info;
	}

	/**
	 * We simply require an 'add' method, of what ever it is we are to read
	 * into
	 */
	public Iterator enumProfiles() {
		Set set = new HashSet();
		IPluginRegistry pluginRegistry = Platform.getPluginRegistry();
		IExtensionPoint point = pluginRegistry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				CSSProfile info = readElement(elements[i]);
				// null can be returned if there's an error reading the
				// element
				if (info != null) {
					set.add(info);
				}
			}
		}
		return set.iterator();
	}

}