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
package org.eclipse.wst.xml.core.modelquery;

import org.eclipse.wst.sse.core.util.URIResolver;
import org.eclipse.wst.xml.uriresolver.XMLCatalog;
import org.eclipse.wst.xml.uriresolver.XMLCatalogPlugin;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;

public class XMLCatalogIdResolver implements IdResolver {

	protected URIResolver uriresolver;
	protected String resourceLocation;

	public XMLCatalogIdResolver(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public XMLCatalogIdResolver(String resourceLocation, URIResolver uriresolver) {
		this.resourceLocation = resourceLocation;
		this.uriresolver = uriresolver;
	}

	public String resolveId(String base, String publicId, String systemId) {
		// first see if we can map the publicId to an alternative systemId
		// note: should probably verify the mappedSystemId before ignoring the systemId
		XMLCatalog xmlCatalog = XMLCatalogPlugin.getInstance().getDefaultXMLCatalog();
		String mappedSystemId = xmlCatalog.getMappedSystemId(publicId, systemId);
		if (mappedSystemId != null) {
			systemId = mappedSystemId;
		}

		// normalize the systemId
		boolean normalized = false;
		// account for Web Projects and others where *any* string may legally resolve somehow
		if (this.uriresolver != null && systemId != null) {
			// check the provided URIResolver
			String resolvedValue = this.uriresolver.getLocationByURI(systemId, base);
			if (resolvedValue != null && resolvedValue.length() > 0) {
				systemId = resolvedValue;
				normalized = true;
			}
		}
		if (!normalized) {
			// no URIResolver available; ask the URIHelper directly
			systemId = URIHelper.normalize(systemId, base, null);
			normalized = true;
		}
		return systemId;
	}


	/**
	 * Gets the resourceLocation.
	 * @return Returns a String
	 */
	public String getResourceLocation() {
		String location = resourceLocation;
		if (location == null) {
			if (uriresolver != null)
				location = uriresolver.getFileBaseLocation();
		}
		return location;
	}

	/* (non-Javadoc)
	 * @deprecated - can delete this as soon as interface method is deleted.
	 */
	public String resolveId(String publicId, String systemId) {
		return resolveId(getResourceLocation(), publicId, systemId);
	}


}
