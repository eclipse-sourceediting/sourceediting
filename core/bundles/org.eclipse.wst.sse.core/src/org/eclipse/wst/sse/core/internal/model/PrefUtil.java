/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.model;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.osgi.service.prefs.Preferences;

class PrefUtil {

	static long WAIT_INTERVAL_MS = 500;
	static int WAIT_DELAY = getInt("org.eclipse.wst.sse.core.modelmanager.maxWaitDuringConcurrentLoad");
	static boolean ALLOW_INTERRUPT_WAITING_THREAD = getBoolean("org.eclipse.wst.sse.core.modelmanager.allowInterruptsDuringConcurrentLoad");

	/** Base of millisecond timings, to avoid wrapping */
	private static final long MILLI_ORIGIN = System.currentTimeMillis();

	/**
	 * Returns millisecond time offset by origin
	 */
	static final long now() {
		return System.currentTimeMillis() - MILLI_ORIGIN;
	}
	
	private static IEclipsePreferences.IPreferenceChangeListener LISTENER;
	static {
		InstanceScope scope = new InstanceScope();
		IEclipsePreferences instancePrefs = scope.getNode(SSECorePlugin.ID);
		LISTENER = new IEclipsePreferences.IPreferenceChangeListener() {

			public void preferenceChange(PreferenceChangeEvent event) {

				if ("modelmanager.maxWaitDuringConcurrentLoad".equals(event.getKey())) {
					WAIT_DELAY = getInt("org.eclipse.wst.sse.core.modelmanager.maxWaitDuringConcurrentLoad");
				}
				else if ("modelmanager.allowInterruptsDuringConcurrentLoad".equals(event.getKey())) {
					ALLOW_INTERRUPT_WAITING_THREAD = getBoolean("org.eclipse.wst.sse.core.modelmanager.allowInterruptsDuringConcurrentLoad");
				}
			}
		};
		instancePrefs.addPreferenceChangeListener(LISTENER);
	}

	private static String getProperty(String property) {
		// Importance order is:
		// default-default < instanceScope < configurationScope < systemProperty
		// < envVar
		String value = null;

		if (value == null) {
			value = System.getenv(property);
		}
		if (value == null) {
			value = System.getProperty(property);
		}
		if (value == null) {
			IPreferencesService preferencesService = Platform.getPreferencesService();
			
			String key = property;
			if (property != null && property.startsWith(SSECorePlugin.ID)) {
				// +1, include the "."
				key = property.substring(SSECorePlugin.ID.length() + 1, property.length());
			}
			InstanceScope instance = new InstanceScope();
			ConfigurationScope config = new ConfigurationScope();
			
			Preferences instanceNode = instance.getNode(SSECorePlugin.ID);
			Preferences configNode = config.getNode(SSECorePlugin.ID);
			value = preferencesService.get(key, getDefault(property), new Preferences[]{configNode,instanceNode});
		}

		return value;
	}

	private static String getDefault(String property) {
		// this is the "default-default"
		if ("org.eclipse.wst.sse.core.modelmanager.maxWaitDuringConcurrentLoad".equals(property)) {
			return "0";
		}
		else if ("org.eclipse.wst.sse.core.modelmanager.allowInterruptsDuringConcurrentLoad".equals(property)) {
			return "false";
		}
		return null;
	}

	private static boolean getBoolean(String key) {
		String property = getProperty(key);
		// if (property != null) {
		//			System.out.println("Tweak: " + key + "=" + Boolean.parseBoolean(property)); //$NON-NLS-1$ //$NON-NLS-2$
		// }
		return (property != null ? Boolean.valueOf(property) : Boolean.valueOf(getDefault(key)))
				.booleanValue();
	}

	private static int getInt(String key) {
		String property = getProperty(key);
		int size = 0;
		if (property != null) {
			try {
				size = Integer.parseInt(property);
				//	System.out.println("Tweak: " + key + "=" + size); //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch (NumberFormatException e) {
				size = getDefaultInt(key, property, size);
			}
		}
		else {
			size = getDefaultInt(key, property, size);
		}
		return size;
	}

	private static int getDefaultInt(String key, String property, int size) {
		// ignored
		try {
			size = Integer.parseInt(getDefault(key));
		}
		catch (NumberFormatException e1) {
			handleIntParseException(key, property, e1);
			size = 0;
		}
		return size;
	}

	private static void handleIntParseException(String key, String property, NumberFormatException e1) {
		Exception n = new Exception(NLS.bind(
				"Exception during parse of default value for key ''{0}'' value was ''{1}''. Using 0 instead", //$NON-NLS-1$
				key, property), e1);
		n.printStackTrace();
	}
}
