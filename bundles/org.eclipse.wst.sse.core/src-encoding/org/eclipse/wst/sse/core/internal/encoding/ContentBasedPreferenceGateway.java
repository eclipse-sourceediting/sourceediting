/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.encoding;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.Preferences;


public class ContentBasedPreferenceGateway {
	private static String DEFAULT_LOCATION = "org.eclipse.wst.sse.core"; //$NON-NLS-1$
	private static String RUNTIME_XML_ID = "org.eclipse.core.runtime.xml"; //$NON-NLS-1$
	private static String SSE_XML_ID = "org.eclipse.wst.xml.core.xmlsource"; //$NON-NLS-1$

	/**
	 * @param pluginId
	 * @return
	 */
	private static boolean bundleExists(String pluginId) {

		// this just verifies there's really a plugin with this ID in "stack"
		Bundle bundle = Platform.getBundle(pluginId);
		return (!(bundle == null));
	}

	/**
	 * @param contentType
	 * @return
	 */
	private static String getContributorPluginId(IContentType contentType) {
		// TODO: need to have registration info here, but for now, we'll use
		// simple heuristic to cover the cases we know about.
		String fullId = null;
		if (contentType == null) {
			fullId = DEFAULT_LOCATION;
		} else {
			fullId = contentType.getId();
		}
		// only one known case, so far, of hard coded re-direction
		// (not sure this is even needed, but just in case).
		// We don't want to store/change runtime.xml preferences
		if (RUNTIME_XML_ID.equals(fullId)) {
			fullId = SSE_XML_ID;
		}
		String pluginId = inferPluginId(fullId);
		return pluginId;
	}

	private static Preferences getDefaultPreferences(IContentType contentType) {
		IEclipsePreferences eclipsePreferences = Platform.getPreferencesService().getRootNode();
		// TODO: eventaully need extension mechanism to avoid these hard coded
		// mechanism.
		// The idea is to load/store based on plugin's preferences, where the
		// content type was contributed
		// Eventually, too, we could do more "dynamic lookup" to get parent
		// types for defaults, etc.

		// Get default plugin preferences
		String pluginPreferenceLocation = DefaultScope.SCOPE + IPath.SEPARATOR + getContributorPluginId(contentType);
		Preferences pluginPreferences = eclipsePreferences.node(pluginPreferenceLocation);

		// the below code does not work at this time because content type
		// preferences are stored in the place as plugin preferences

		//		Preferences contentPreferences = null;
		//		if (contentType != null) {
		//			contentPreferences = pluginPreferences.node(contentType.getId());
		//		}
		//		else {
		//			contentPreferences = pluginPreferences.node(DEFAULT_LOCATION );
		//		}
		//
		//		return contentPreferences;

		return pluginPreferences;

	}

	private static Preferences getDefaultPreferences(String contentTypeId) {
		IContentType contentType = Platform.getContentTypeManager().getContentType(contentTypeId);
		return getDefaultPreferences(contentType);
	}

	public static Preferences getPreferences(IContentType contentType) {
		IEclipsePreferences eclipsePreferences = Platform.getPreferencesService().getRootNode();
		// TODO: eventaully need extension mechanism to avoid these hard coded
		// mechanism.
		// The idea is to load/store based on plugin's preferences, where the
		// content type was contributed
		// Eventually, too, we could do more "dynamic lookup" to get parent
		// types for defaults, etc.

		// Get instance plugin preferences
		String pluginPreferenceLocation = Plugin.PLUGIN_PREFERENCE_SCOPE + IPath.SEPARATOR + getContributorPluginId(contentType);
		Preferences pluginPreferences = eclipsePreferences.node(pluginPreferenceLocation);

		// the below code does not work at this time because content type
		// preferences are stored in the place as plugin preferences

		//		Preferences contentPreferences = null;
		//		if (contentType != null) {
		//			contentPreferences = pluginPreferences.node(contentType.getId());
		//		}
		//		else {
		//			contentPreferences = pluginPreferences.node(DEFAULT_LOCATION );
		//		}
		//
		//		return contentPreferences;
		return pluginPreferences;

	}

	public static Preferences getPreferences(String contentTypeId) {
		IContentType contentType = Platform.getContentTypeManager().getContentType(contentTypeId);
		return getPreferences(contentType);
	}

	public static String getPreferencesString(IContentType contentType, String key) {
		Preferences preferences = getPreferences(contentType);
		String value = preferences.get(key, getDefaultPreferences(contentType).get(key, null));
		return value;
	}

	public static String getPreferencesString(String contentTypeId, String key) {
		Preferences preferences = getPreferences(contentTypeId);
		String value = preferences.get(key, getDefaultPreferences(contentTypeId).get(key, null));
		return value;
	}

	/**
	 * @param fullId
	 * @return
	 */
	private static String inferPluginId(String fullId) {
		// simply trim off last "segment" from full ID.
		int lastSegmentPos = fullId.lastIndexOf('.');
		String pluginId = null;
		if (lastSegmentPos != -1) {
			pluginId = fullId.substring(0, lastSegmentPos);
		} else {
			// weird case? We'll at least put/get them somewhere
			pluginId = DEFAULT_LOCATION;
		}
		if (!bundleExists(pluginId)) {
			// use default location
			pluginId = DEFAULT_LOCATION;
		}
		return pluginId;
	}
}
