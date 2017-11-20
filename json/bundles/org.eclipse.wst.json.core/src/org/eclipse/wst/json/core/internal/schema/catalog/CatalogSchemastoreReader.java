/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.eclipse.wst.json.core.internal.schema.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonArray;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.internal.Logger;
import org.eclipse.wst.json.core.internal.download.HttpClientProvider;
import org.eclipse.wst.json.core.schema.catalog.ICatalog;
import org.eclipse.wst.json.core.schema.catalog.ICatalogElement;
import org.eclipse.wst.json.core.schema.catalog.ICatalogEntry;
import org.osgi.framework.Bundle;

public class CatalogSchemastoreReader {

	private static final String SCHEMAS = "schemas"; //$NON-NLS-1$
	private static final String SCHEMASTORE_CATALOG = "https://raw.githubusercontent.com/SchemaStore/schemastore/master/src/api/json/catalog.json"; //$NON-NLS-1$
	protected ICatalog catalog;

	protected CatalogSchemastoreReader(ICatalog catalog) {
		this.catalog = catalog;
	}

	protected void readSchemastore() {
		File f = getUrl();
		if (f != null) {
			int type = ICatalogEntry.ENTRY_TYPE_SCHEMA;
			JsonValue schemas;
			try {
				InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
				JsonObject json = JsonObject.readFrom(reader);
				schemas = json.get(SCHEMAS);
			} catch (IOException e) {
				Logger.logException(e);
				return;
			}
			if (schemas != null && schemas instanceof JsonArray) {
				JsonArray elements = (JsonArray) schemas;
				Iterator<JsonValue> iter = elements.iterator();
				while (iter.hasNext()) {
					JsonValue value = iter.next();
					if (value instanceof JsonObject) {
						JsonObject jsonObject = (JsonObject) value;
						JsonValue urlJson = jsonObject.get("url"); //$NON-NLS-1$
						JsonValue fileMatchJson = jsonObject.get("fileMatch"); //$NON-NLS-1$
						if (urlJson != null && fileMatchJson != null && urlJson.isString() && fileMatchJson.isArray()) {
							String url = urlJson.asString();
							JsonArray fileMatchArray = fileMatchJson.asArray();
							Iterator<JsonValue> fileIter = fileMatchArray.iterator();
							while (fileIter.hasNext()) {
								JsonValue fileMatchValue = fileIter.next();
								if (fileMatchValue.isString()) {
									String fileMatch = fileMatchValue.asString();
									ICatalogElement catalogElement = catalog.createCatalogElement(type);
									if (catalogElement instanceof ICatalogEntry) {
										ICatalogEntry entry = (ICatalogEntry) catalogElement;
										entry.setKey(fileMatch);
										entry.setURI(url);
										entry.setId(fileMatch);
									}
									catalog.addCatalogElement(catalogElement);
								}
							}
						}
					}
				}
			}
		}
	}

	private static File getUrl() {
		try {
			File f = HttpClientProvider.getFile(new URL(SCHEMASTORE_CATALOG));
			if (f == null || !f.exists()) {
				URL url = getUrlFromBundle();
				File file;
				try {
				  file = new File(url.toURI());
				} catch(URISyntaxException e) {
				  file = new File(url.getPath());
				}
				return file;
			} else {
				return f;
			}
		} catch (Exception e) {
			Logger.logException(e);
		}
		return null;
	}

	private static URL getUrlFromBundle() {
		Bundle bundle = Platform.getBundle(JSONCorePlugin.PLUGIN_ID);
		if (bundle != null) {
			URL[] urls = FileLocator.findEntries(bundle, new Path("/schemastore/catalog.json")); //$NON-NLS-1$
			if (urls != null && urls.length > 0) {
				try {
					return FileLocator.resolve(urls[0]);
				} catch (IOException e) {
					Logger.logException(e);
				}
			}
		}
		return null;
	}

}
