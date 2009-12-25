/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.xml.xpath.core.XPathCorePlugin;
import org.eclipse.wst.xml.xpath.core.XPathProcessorPreferences;

/**
 * Preferences initializer for XSL core preferences.
 * 
 * @author David Carver
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = new DefaultScope().getNode(XPathCorePlugin.PLUGIN_ID);
		node.putBoolean(XPathProcessorPreferences.XPATH_1_0_PROCESSOR, true);
		node.putBoolean(XPathProcessorPreferences.XPATH_2_0_PROCESSOR, false);
		node.putBoolean(XPathProcessorPreferences.XPATH_OTHER, false);
	}
}
