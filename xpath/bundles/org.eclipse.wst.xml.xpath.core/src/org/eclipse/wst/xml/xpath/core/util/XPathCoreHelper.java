/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.core.util;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.xml.xpath.core.XPathCorePlugin;
import org.osgi.service.prefs.Preferences;

public class XPathCoreHelper {

	public static Preferences getPreferences() {
		IEclipsePreferences prefs = Platform.getPreferencesService().getRootNode();
		return prefs.node(DefaultScope.SCOPE).node(XPathCorePlugin.PLUGIN_ID);
	}	
}
