/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;

import com.ibm.icu.util.StringTokenizer;

/**
 * @deprecated The URIResolver interface is deprecated. Use the resolver from
 *             org.eclipse.wst.common.uriresolver.
 */
public class ProjectResolver implements URIResolver {
	private String fFileBaseLocation = null;
	private IProject fProject = null;

	/**
	 * It is strongly recommended that clients use
	 * project.getAdapter(URIResolver.class) to obtain a URIResolver aware of
	 * the Project's special requirements. Note that a URIResolver may not be
	 * returned at all so manually creating this object may still be required.
	 */
	public ProjectResolver(IProject project) {
		super();
		fProject = project;
	}

	public String getFileBaseLocation() {
		return fFileBaseLocation;
	}

	public String getLocationByURI(String uri) {
		return getLocationByURI(uri, getFileBaseLocation());
	}

	// defect 244817 end
	/**
	 * Resolve the (possibly relative) URI acording to RFC1808 using the
	 * default file base location. Resolves resource references into absolute
	 * resource locations without ensuring that the resource actually exists.
	 * 
	 * Note: currently resolveCrossProjectLinks is ignored in this
	 * implementation.
	 */
	public String getLocationByURI(String uri, boolean resolveCrossProjectLinks) {
		return getLocationByURI(uri, getFileBaseLocation(), resolveCrossProjectLinks);
	}

	public String getLocationByURI(String uri, String baseReference) {
		if (uri == null)
			return null;
		/*
		 * defect 244817 try { URL aURL = new URL(uri);
		 */
		/**
		 * An actual URL was given, but only the "file:///" protocol is
		 * supported. Resolve the URI by finding the file to which it points.
		 */
		/*
		 * defect 244817 if (!aURL.getProtocol().equals("platform")) {
		 * //$NON-NLS-1$ if (aURL.getProtocol().equals("file") &&
		 * (aURL.getHost().equals("localhost") || aURL.getHost().length() ==
		 * 0)) { //$NON-NLS-2$//$NON-NLS-1$ return aURL.getFile(); } return
		 * uri; } } catch (MalformedURLException mfuExc) { }
		 */
		// defect 244817 start
		if (isFileURL(uri)) {
			try {
				URL url = new URL(uri);
				return getPath(url);
			}
			catch (MalformedURLException e) {
			}
		}
		// defect 244817 end

		// which of the serveral are we suppose to use here?
		//

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=71223
		// Workaround for problem in URIHelper; uris starting with '/' are
		// returned as-is.
		String location = null;
		if (uri.startsWith("/")) { //$NON-NLS-1$
			IProject p = getProject();
			if (p != null && p.isAccessible()) {
				IFile file = p.getFile(uri);
				
				if (file.getLocation() != null) {
					location = file.getLocation().toString();
				}
				if (location == null && file.getLocationURI() != null) {
					location = file.getLocationURI().toString();
				}
				if (location == null) {
					location = file.getFullPath().toString();
				}
			}
		}
		if(location == null) {
			location = URIHelper.normalize(uri, baseReference, getRootLocationString());
		}
		return location;
	}

	/**
	 * Perform the getLocationByURI action using the baseReference as the
	 * point of reference instead of the default for this resolver
	 * 
	 * Note: currently resolveCrossProjectLinks is ignored in this
	 * implementation.
	 */
	public String getLocationByURI(String uri, String baseReference, boolean resolveCrossProjectLinks) {
		return getLocationByURI(uri, baseReference);
	}

	/**
	 * 
	 * @param path
	 * @param host
	 * @return String
	 */
	private String getPath(IPath path, String host) {
		IPath newPath = path;
		// They are potentially for only Windows operating system.
		// a.) if path has a device, and if it begins with IPath.SEPARATOR,
		// remove it
		final String device = path.getDevice();
		if ((device != null) && (device.length() > 0)) {
			if (device.charAt(0) == IPath.SEPARATOR) {
				final String newDevice = device.substring(1);
				newPath = path.setDevice(newDevice);
			}
		}
		// b.) if it has a hostname, it is UNC name... Any java or eclipse api
		// helps it ??
		if (path != null && host != null && host.length() != 0) {
			IPath uncPath = new Path(host);
			uncPath = uncPath.append(path);
			newPath = uncPath.makeUNC(true);
		}
		return newPath.toString();
	}

	/**
	 * 
	 * @param url
	 * @return String
	 */
	private String getPath(URL url) {
		String ref = url.getRef() == null ? "" : "#" + url.getRef(); //$NON-NLS-1$ //$NON-NLS-2$
		String strPath = url.getFile() + ref;
		IPath path;
		if (strPath.length() == 0) {
			path = Path.ROOT;
		}
		else {
			path = new Path(strPath);
			String query = null;
			StringTokenizer parser = new StringTokenizer(strPath, "?"); //$NON-NLS-1$
			int tokenCount = parser.countTokens();
			if (tokenCount == 2) {
				path = new Path((String) parser.nextElement());
				query = (String) parser.nextElement();
			}
			if (query == null) {
				parser = new StringTokenizer(path.toString(), "#"); //$NON-NLS-1$
				tokenCount = parser.countTokens();
				if (tokenCount == 2) {
					path = new Path((String) parser.nextElement());
				}
			}
		}
		return getPath(path, url.getHost());
	}

	public org.eclipse.core.resources.IProject getProject() {
		return fProject;
	}

	public org.eclipse.core.resources.IContainer getRootLocation() {
		return fProject;
	}

	protected String getRootLocationString() {
		String location = null;
		if (fProject == null)
			return null;
		if (fProject.getLocation() != null) {
			location = fProject.getLocation().toString();
		}
		if (location == null && fProject.getLocationURI() != null) {
			location = fProject.getLocationURI().toString();
		}
		if (location == null) {
			location = fProject.getFullPath().toString();
		}
		return location;
	}

	public InputStream getURIStream(String uri) {
		return null;
	}

	// defect 244817 start
	/**
	 * 
	 * @param passedSpec
	 * @return boolean
	 */
	private boolean isFileURL(String passedSpec) {
		if (passedSpec == null) {
			return false;
		}
		final String spec = passedSpec.trim();
		if (spec.length() == 0) {
			return false;
		}
		final int limit = spec.length();
		String newProtocol = null;
		for (int index = 0; index < limit; index++) {
			final char p = spec.charAt(index);
			if (p == '/') { //$NON-NLS-1$
				break;
			}
			if (p == ':') { //$NON-NLS-1$
				newProtocol = spec.substring(0, index);
				break;
			}
		}
		return (newProtocol != null && newProtocol.compareToIgnoreCase("file") == 0); //$NON-NLS-1$
	}

	public void setFileBaseLocation(String newFileBaseLocation) {
		fFileBaseLocation = newFileBaseLocation;
	}

	public void setProject(IProject newProject) {
		fProject = newProject;
	}
}
