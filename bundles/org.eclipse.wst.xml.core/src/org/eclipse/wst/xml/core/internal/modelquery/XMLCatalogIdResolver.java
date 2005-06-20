/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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

import java.io.IOException;
import java.net.MalformedURLException;

//import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;



public class XMLCatalogIdResolver implements org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver {
	protected String resourceLocation;

	protected URIResolver uriresolver;

	private XMLCatalogIdResolver() {
		super();
	}
	private XMLCatalogIdResolver(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public XMLCatalogIdResolver(String resourceLocation, URIResolver uriresolver) {
		this(resourceLocation);
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
		}
		// first see if we can map the publicId to an alternative systemId
		// note: should probably verify the mappedSystemId before ignoring the
		// systemId
		ICatalog xmlCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		try {
			String mappedSystemId = xmlCatalog.resolvePublic(publicId, systemId);
			if (mappedSystemId != null) {
				result = mappedSystemId;
			}
		}
		catch (MalformedURLException e) {
			Logger.logException("error resolving from catalog: ", e);
		}
		catch (IOException e) {
			Logger.logException("error resolving from catalog: ", e);

		}

		// normalize the systemId
//		https://bugs.eclipse.org/bugs/show_bug.cgi?id=100481
		//boolean normalized = false;
		
		// account for Web Projects and others where *any* string may legally
		// resolve somehow
		if (this.uriresolver != null && systemId != null) {
			// check the provided URIResolver
			String resolvedValue = this.uriresolver.getLocationByURI(systemId, base);
			if (resolvedValue != null && resolvedValue.length() > 0) {
				result = resolvedValue;
//				https://bugs.eclipse.org/bugs/show_bug.cgi?id=100481
				//normalized = true;
			}
		}
		
//		https://bugs.eclipse.org/bugs/show_bug.cgi?id=100481
//		if (!normalized) {
//			// no URIResolver available; ask the URIHelper directly
//			result = URIHelper.normalize(result, base, null);
//			normalized = true;
//		}
		
		return result;
	}


}
