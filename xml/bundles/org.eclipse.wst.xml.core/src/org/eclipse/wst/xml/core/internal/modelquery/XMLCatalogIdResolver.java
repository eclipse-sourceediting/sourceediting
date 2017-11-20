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
package org.eclipse.wst.xml.core.internal.modelquery;


import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.xml.core.internal.Logger;


// TODO cs : remove this class and utilize the common URIResolver directly
// We need to update some of the ModelQuery related code to pass the
// 'baseLocation' thru
// and then there'll be node need for this class.
// 
public class XMLCatalogIdResolver implements org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver {
	protected String resourceLocation;

	protected URIResolver uriresolver;

	private XMLCatalogIdResolver() {
		super();
	}

	private XMLCatalogIdResolver(String resourceLocation) {
		this();
		this.resourceLocation = resourceLocation;
	}

	public XMLCatalogIdResolver(String resourceLocation, URIResolver uriresolver) {
		this(resourceLocation);
// this constructor should not be called with two null arguments.
// If so, an assert will occur later when resolve is called. 
// See 118371 XMLCatalogIdResolver#resolve throws AssertionFailedException
//
// but, I'm not enabling this check now due to lateness in cycle. 		
//		if (resourceLocation == null && uriresolver == null) {
//			throw new IllegalArgumentException("both location and resolver can not be null");
//		}
		this.uriresolver = uriresolver;
	}


	/**
	 * Gets the resourceLocation.
	 * 
	 * @return Returns a String
	 */
	private String getResourceLocation() {
		String location = resourceLocation;
		if (location == null) {
			if (uriresolver != null)
				location = uriresolver.getFileBaseLocation();
		}
		return location;
	}


	public String resolve(String base, String publicId, String systemId) {

		String result = systemId;
		if (base == null) {
			base = getResourceLocation();
			// bug 117320, ensure base URI is 'protocal' qualified before
			// passing it thru to URIResolver
			// bug 117424, we should be able to assume that the base location
			// is non-null

			/**
			 * We shouldn't assert a failure because the catalog does not
			 * require a base location to operate and it will be called from
			 * non-file-based scenarios.
			 * 
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=206176
			 */
			// Assert.isNotNull(base, "Base location is expected to be non null."); //$NON-NLS-1$
			if (base != null) {
				base = URIHelper.addImpliedFileProtocol(base);
			}
		}
		result = URIResolverPlugin.createResolver().resolve(base, publicId, systemId);
		return result;
	}

	public String resolvePhysicalLocation(String baseLocation, String publicId, String logicalLocation) {
		// This class should never be called to perform physical resolution!
		// If it does we should log it as an error
		Logger.log(Logger.ERROR_DEBUG, "XMLCatalogIDResolver.resolvePhysicalLocation() called unexpectedly"); //$NON-NLS-1$
		return logicalLocation;
	}
}
