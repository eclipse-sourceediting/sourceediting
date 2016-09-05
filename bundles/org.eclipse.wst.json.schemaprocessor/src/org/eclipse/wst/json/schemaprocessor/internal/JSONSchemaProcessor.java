/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.schemaprocessor.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.json.impl.schema.JSONSchemaDocument;
import org.eclipse.json.schema.IJSONSchemaDocument;
import org.eclipse.json.schema.IJSONSchemaProcessor;
import org.eclipse.wst.json.core.internal.download.HttpClientProvider;

public class JSONSchemaProcessor implements IJSONSchemaProcessor {

	private static final int MAP_SIZE = 10;
	private static Map<String,IJSONSchemaDocument> schemaDocuments = new LinkedHashMap<String, IJSONSchemaDocument>();
	
	@Override
	public IJSONSchemaDocument getSchema(String uriString) throws IOException {
		IJSONSchemaDocument schemaDocument = schemaDocuments.get(uriString);
		if (schemaDocument != null) {
			return schemaDocument;
		}
		int size = schemaDocuments.size();
		if (size > MAP_SIZE) {
			String key = schemaDocuments.keySet().iterator().next();
			schemaDocuments.remove(key);
		}
		URL url = new URL(uriString);
		InputStream is = null;
		try {
			if ("jar".equals(url.getProtocol())) {
				is = url.openStream();
			} else {
				File f = HttpClientProvider.getFile(url);
				is = new FileInputStream(f);
			}
			schemaDocument = new JSONSchemaDocument(new InputStreamReader(is));
		} finally {
			if (is != null) {
				is.close();
			}
		}
		schemaDocuments.put(uriString, schemaDocument);
		return schemaDocument;
	}
	
	public static void clearCache() {
		schemaDocuments.clear();
	}

}
