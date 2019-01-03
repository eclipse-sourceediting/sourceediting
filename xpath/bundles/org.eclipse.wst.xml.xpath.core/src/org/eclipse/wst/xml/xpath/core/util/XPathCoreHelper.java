/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
