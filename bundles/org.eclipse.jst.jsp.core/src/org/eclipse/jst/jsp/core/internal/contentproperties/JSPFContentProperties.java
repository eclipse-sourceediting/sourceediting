/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentproperties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Properties constants used by JSP Fragments. Clients should only read and
 * modify the JSP Fragment properties programmatically using this class.
 * 
 * @since 1.1
 */
public class JSPFContentProperties {
	static final String JSPCORE_ID = JSPCorePlugin.getDefault().getBundle().getSymbolicName();
	private static final String PROJECT_KEY = "<project>"; //$NON-NLS-1$

	/**
	 * A named key that controls the default language for JSP Fragments
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 * 
	 * @since 1.1
	 */
	public static final String JSPLANGUAGE = "jsp-language"; //$NON-NLS-1$
	/**
	 * A named key that controls the default content type for JSP Fragments
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 * 
	 * @since 1.1
	 */
	public static final String JSPCONTENTTYPE = "jsp-content-type"; //$NON-NLS-1$
	/**
	 * Indicates if JSP fragments should be compiled/validated. JSP fragments
	 * will be validated when true.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 * 
	 * @since 1.1
	 */
	public static final String VALIDATE_FRAGMENTS = "validateFragments";//$NON-NLS-1$

	/**
	 * Generates a preference key based on resourcePath
	 * 
	 * @param resourcePath
	 *            the path the key will be based off of
	 * @return preference key based on resourcePath (basically the
	 *         resourcePath without the filename); PROJECT_KEY if resourcePath
	 *         is null
	 */
	static String getKeyFor(IPath resourcePath) {
		String key = PROJECT_KEY;
		if (resourcePath != null && resourcePath.segmentCount() > 1) {
			key = resourcePath.removeFirstSegments(1).toString();
		}
		return key;
	}

	/**
	 * Get the preferences node associated with the given project scope and
	 * preference key (subNode) If create is true, the preference node will be
	 * created if one does not already exist
	 * 
	 * @param project
	 *            the project the preference node is under
	 * @param preferenceKey
	 *            the subnode/category the preference node is located in
	 * @param create
	 *            if true, a preference node will be created if one does not
	 *            already exist
	 * @return Preferences associated with the given project scope and
	 *         preference key. null if one could not be found and create is
	 *         false
	 */
	static Preferences getPreferences(IProject project, String preferenceKey, boolean create) {
		if (create)
			// create all nodes down to the one we are interested in
			return new ProjectScope(project).getNode(JSPCORE_ID).node(preferenceKey);
		// be careful looking up for our node so not to create any nodes as
		// side effect
		Preferences node = Platform.getPreferencesService().getRootNode().node(ProjectScope.SCOPE);
		try {
			// TODO once bug 90500 is fixed, should be as simple as this:
			// String path = project.getName() + IPath.SEPARATOR +
			// ResourcesPlugin.PI_RESOURCES + IPath.SEPARATOR +
			// ENCODING_PREF_NODE;
			// return node.nodeExists(path) ? node.node(path) : null;
			// for now, take the long way
			if (!node.nodeExists(project.getName()))
				return null;
			node = node.node(project.getName());
			if (!node.nodeExists(JSPCORE_ID))
				return null;
			node = node.node(JSPCORE_ID);
			if (!node.nodeExists(preferenceKey))
				return null;
			return node.node(preferenceKey);
		}
		catch (BackingStoreException e) {
			// nodeExists failed
			Logger.log(Logger.WARNING_DEBUG, "Could not retrieve preference node", e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Returns the value for the given key in the given context.
	 * 
	 * @param key
	 *            The property key
	 * @param resource
	 *            The current context or <code>null</code> if no context is
	 *            available and the workspace setting should be taken. Note
	 *            that passing <code>null</code> should be avoided.
	 * @param recurse
	 *            whether the parent should be queried till property is found
	 * @return Returns the current value for the key.
	 * @since 1.1
	 */
	public static String getProperty(String key, IResource resource, boolean recurse) {
		String val = null;
		if (resource != null) {
			IProject project = resource.getProject();
			if (project != null) {
				Preferences preferences = getPreferences(project, key, false);
				if (preferences != null) {
					val = internalGetProperty(resource, recurse, preferences);
				}
			}
		}
		// no preferences found - for performance reasons,
		// short-circuit
		// lookup by falling back to workspace's default
		// setting
		if (val == null && recurse)
			val = getWorkbenchPreference(key);
		return val;
	}

	private static String getWorkbenchPreference(String key) {
		return Platform.getPreferencesService().getString(JSPCORE_ID, key, null, null);
	}

	private static String internalGetProperty(IResource resource, boolean recurse, Preferences preferences) {
		String value = preferences.get(getKeyFor(resource.getFullPath()), null);
		if (value == null && resource != resource.getProject() && recurse) {
			value = preferences.get(getKeyFor(null), null);
		}

		return value;
	}

	/**
	 * Sets the value for the given key in the given context.
	 * 
	 * @param key
	 *            The property key
	 * @param resource
	 *            The current context. Note context cannot be
	 *            <code>null</code>.
	 * @param value
	 *            The value to set for the key. If value is <code>null</code>
	 *            the key is removed from properties.
	 * @since 1.1
	 */
	public static void setProperty(String key, IResource resource, String value) throws CoreException {
		if (resource != null) {
			IProject project = resource.getProject();
			if (project != null) {
				Preferences preferences = getPreferences(project, key, true);
				if (value == null || value.trim().length() == 0)
					preferences.remove(getKeyFor(resource.getFullPath()));
				else
					preferences.put(getKeyFor(resource.getFullPath()), value);
				try {
					// save changes
					preferences.flush();
				}
				catch (BackingStoreException e) {
					throw new CoreException(new Status(IStatus.ERROR, JSPCORE_ID, IStatus.ERROR, "Unable to set property", e)); //$NON-NLS-1$
				}
			}
		}
		// Workbench preference may not be needed so leave out for now
		// just set a preference in the instance scope
		// if (!preferenceFound) {
		// setWorkbenchPreference(key);
		// }
	}
}
