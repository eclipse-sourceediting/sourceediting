/*******************************************************************************
 * Copyright (c) 2002, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.catalog.XMLCatalogURIResolverExtension
 *                                           modified in order to process JSON Objects.                 
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.schema.catalog;

import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.internal.JSONCoreMessages;
import org.eclipse.wst.json.core.internal.Logger;
import org.eclipse.wst.json.core.schema.catalog.ICatalog;

/**
 * This class is used to inject the JSONCatalog resolution behaviour into the
 * Common Extensible URI Resolver. This class is referenced in the JSON Catalog
 * plugin's plugin.xml file.
 */
public class JSONCatalogURIResolverExtension implements URIResolverExtension {
	public String resolve(IFile file, String baseLocation, String publicId, String systemId) {
		if (publicId != null || systemId != null) {
			return null;
		}
		if (file == null) { // Cannot resolve schema with no file
			Logger.log(Logger.ERROR_DEBUG, JSONCoreMessages.Catalog_resolution_malformed_url);
			return null;
		}

		// if we have catalog in a project we may add it
		// to the catalog manager first
		ICatalog catalog = JSONCorePlugin.getDefault().getDefaultJSONCatalog();
		if (catalog == null) {
			Logger.log(Logger.ERROR_DEBUG, JSONCoreMessages.Catalog_resolution_null_catalog);
			return null;
		}

		try {
			return catalog.resolveSchema(file.getName());
		} catch (MalformedURLException me) {
			Logger.log(Logger.ERROR_DEBUG, JSONCoreMessages.Catalog_resolution_malformed_url);
		} catch (IOException ie) {
			Logger.log(Logger.ERROR_DEBUG, JSONCoreMessages.Catalog_resolution_io_exception);
		}
		return null;
	}
}
