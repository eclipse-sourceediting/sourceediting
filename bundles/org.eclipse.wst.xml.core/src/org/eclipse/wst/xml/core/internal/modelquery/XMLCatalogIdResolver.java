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

import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.xml.uriresolver.internal.XMLCatalog;
import org.eclipse.wst.xml.uriresolver.internal.XMLCatalogPlugin;
import org.eclipse.wst.xml.uriresolver.internal.util.IdResolver;
import org.eclipse.wst.xml.uriresolver.internal.util.URIHelper;


public class XMLCatalogIdResolver implements IdResolver {
	protected String resourceLocation;

	protected URIResolver uriresolver;

	public XMLCatalogIdResolver(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public XMLCatalogIdResolver(String resourceLocation, URIResolver uriresolver) {
		this.resourceLocation = resourceLocation;
		this.uriresolver = uriresolver;
	}


	/**
	 * Gets the resourceLocation.
	 * 
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

	/**
	 * @deprecated in superclass
	 */
	public String resolveId(String publicId, String systemId) {
		return resolveId(getResourceLocation(), publicId, systemId);
	}

	public String resolveId(String base, String publicId, String systemId) {
		// first see if we can map the publicId to an alternative systemId
		// note: should probably verify the mappedSystemId before ignoring the
		// systemId
		XMLCatalog xmlCatalog = XMLCatalogPlugin.getInstance().getDefaultXMLCatalog();
		String mappedSystemId = xmlCatalog.getMappedSystemId(publicId, systemId);
		if (mappedSystemId != null) {
			systemId = mappedSystemId;
		}

		// normalize the systemId
		boolean normalized = false;
		// account for Web Projects and others where *any* string may legally
		// resolve somehow
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


}
