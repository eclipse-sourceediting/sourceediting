/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;


/**
 * @deprecated
 * 
 * Should use extensible URIResolver from org.eclipse.wst.common.uriresolver
 * instead.
 */

public interface URIResolver {

	String getFileBaseLocation();

	/**
	 * Resolve the (possibly relative) URI acording to RFC1808 using the
	 * default file base location. Resolves resource references into absolute
	 * resource locations without ensuring that the resource actually exists.
	 */
	String getLocationByURI(String uri);

	/**
	 * Resolve the (possibly relative) URI acording to RFC1808 using the
	 * default file base location. Resolves resource references into absolute
	 * resource locations without ensuring that the resource actually exists.
	 * 
	 * If resolveCrossProjectLinks is set to true, then this method will
	 * properly resolve the URI if it is a valid URI to another (appropriate)
	 * project.
	 */
	String getLocationByURI(String uri, boolean resolveCrossProjectLinks);

	/**
	 * Perform the getLocationByURI action using the baseReference as the
	 * point of reference instead of the default for this resolver
	 */
	String getLocationByURI(String uri, String baseReference);

	/**
	 * Perform the getLocationByURI action using the baseReference as the
	 * point of reference instead of the default for this resolver
	 * 
	 * If resolveCrossProjectLinks is set to true, then this method will
	 * properly resolve the URI if it is a valid URI to another (appropriate)
	 * project.
	 */
	String getLocationByURI(String uri, String baseReference, boolean resolveCrossProjectLinks);

	IProject getProject();

	IContainer getRootLocation();

	/**
	 * Attempts to return a direct inputstream to the given URI which must be
	 * relative to the default point of reference.
	 * 
	 */
	InputStream getURIStream(String uri);

	void setFileBaseLocation(String newLocation);

	void setProject(IProject newProject);

}
