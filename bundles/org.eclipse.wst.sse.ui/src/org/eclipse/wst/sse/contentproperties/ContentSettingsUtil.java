/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.contentproperties;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 */
public class ContentSettingsUtil {


	/**
	 * Not meant to be instantiated. All static utility methods.
	 */
	protected ContentSettingsUtil() {
		super();
	}

	public static String getEncodingFromSettings(IFile file) {
		if (file == null)
			return null;
		IProject project = file.getProject();
		if (project == null)
			return null;
		IContentSettings settings = ContentSettings.getInstance();
		if (settings == null)
			return null;

		String value = settings.getProperty(file, IContentSettings.JSP_PAGE_ENCODING);
		if (value == null || value.length() == 0) {
			value = settings.getProperty(project, IContentSettings.JSP_PAGE_ENCODING);
		}
		// for empty String, return null
		if (value != null && value.length() == 0) {
			value = null;
		}

		return value;

	}



	public static String getContentTypeFromSettings(IFile file) {
		if (file == null)
			return null;
		IProject project = file.getProject();
		if (project == null)
			return null;
		IContentSettings settings = ContentSettings.getInstance();
		if (settings == null)
			return null;

		String value = settings.getProperty(file, IContentSettings.JSP_CONTENT_TYPE);
		if (value == null || value.length() == 0) {
			value = settings.getProperty(project, IContentSettings.JSP_CONTENT_TYPE);
		}
		// for empty String, return null
		if (value != null && value.length() == 0) {
			value = null;
		}
		return value;

	}


	public static String getLanguageFromSettings(IFile file) {
		if (file == null)
			return null;
		IProject project = file.getProject();
		if (project == null)
			return null;
		IContentSettings settings = ContentSettings.getInstance();
		if (settings == null)
			return null;

		String value = settings.getProperty(file, IContentSettings.JSP_LANGUAGE);
		if (value == null || value.length() == 0) {
			value = settings.getProperty(project, IContentSettings.JSP_LANGUAGE);
		}
		// for empty String, return null
		if (value != null && value.length() == 0) {
			value = null;
		}
		return value;
	}


}
