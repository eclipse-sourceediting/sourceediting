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
package org.eclipse.wst.sse.core.util;

import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;



public interface URIResolver {

	String getFileBaseLocation();

	/**
	 * Resolve the (possibly relative) URI acording to RFC1808
	 * using the default file base location.  Resolves resource references
	 * into absolute resource locations without ensuring that the resource
	 * actually exists.
	 */
	String getLocationByURI(String uri);

	/**
	 * Perform the getLocationByURI action using the baseReference as the
	 * point of reference instead of the default for this resolver
	 */
	String getLocationByURI(String uri, String baseReference);

	/**
	 * Resolve the (possibly relative) URI acording to RFC1808
	 * using the default file base location.  Resolves resource references
	 * into absolute resource locations without ensuring that the resource
	 * actually exists.  
	 * 
	 * If resolveCrossProjectLinks is set to true, then this method will 
	 * properly resolve the URI if it is a valid URI to another (appropriate)
	 * project.
	 */
	String getLocationByURI(String uri, boolean resolveCrossProjectLinks);

	/**
	 * Perform the getLocationByURI action using the baseReference as the
	 * point of reference instead of the default for this resolver
	 * 
	 * If resolveCrossProjectLinks is set to true, then this method will 
	 * properly resolve the URI if it is a valid URI to another (appropriate)
	 * project.
	 */
	String getLocationByURI(String uri, String baseReference, boolean resolveCrossProjectLinks);

	/**
	 * Attempts to return a direct inputstream to the given URI which must be
	 * relative to the default point of reference.
	 */
	InputStream getURIStream(String uri);

	IProject getProject();

	IContainer getRootLocation();

	void setFileBaseLocation(String newLocation);

	void setProject(IProject newProject);

}
