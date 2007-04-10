/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.util;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

/**
 * 
 */
public class CSSPathService {

	// Constants
	private static final String FILEURLSCHEME = "file";//$NON-NLS-1$
	private static final String URLCOLON = ":"; //$NON-NLS-1$
	private static final String FILEURLPREFIX = FILEURLSCHEME + URLCOLON + "//";//$NON-NLS-1$
	private static final String URLSEPARATOR = "/"; //$NON-NLS-1$

	/**
	 * @return java.lang.String
	 */
	public static String getAbsoluteURL(IStructuredModel baseModel, String ref) {
		String absLink = URLModelProviderCSS.resolveURI(baseModel, ref, true);
		String url = absLink;
		if (absLink != null) { // if it has not scheme, we must add "file"
								// scheme
			try {
				new java.net.URL(absLink);
			}
			catch (java.net.MalformedURLException e) {
				IPath path = new Path(absLink);
				if (path != null)
					url = toURL(path);
			}
		}
		return url;
	}

	/**
	 * 
	 * @return org.eclipse.core.resources.IFile
	 * @param location
	 *            java.lang.String
	 */
	public static IFile location2File(String location) {
		Path path = new Path(location);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
		if (file == null && path.segmentCount() > 1)
			file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		return file;
	}

	/**
	 * Gets a file URL for the path.
	 */
	private static String toURL(IPath path) {
		path = new Path(path.toFile().getAbsolutePath());
		if (path.isUNC() == true) {
			// it's UNC. return file://host/foo/bar/baz.html
			return FILEURLSCHEME + URLCOLON + path.toString();
		}
		return FILEURLPREFIX + URLSEPARATOR + path.toString();
	}
}
