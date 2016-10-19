/**
 *  Copyright (c) 2013, 2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.internal.schema;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.json.schema.IJSONSchemaDocument;
import org.eclipse.json.schema.IJSONSchemaProcessor;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.document.IJSONDocument;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.core.internal.Logger;
import org.eclipse.wst.json.core.util.JSONUtil;

public class SchemaProcessorRegistryReader {

	protected static final String EXTENSION_POINT_ID = "schemaProcessors"; //$NON-NLS-1$
	protected static final String TAG_CONTRIBUTION = "schemaProcessor"; //$NON-NLS-1$

	public static SchemaProcessorRegistryReader INSTANCE = null;

	private IJSONSchemaProcessor defaultProcessor;

	public static SchemaProcessorRegistryReader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SchemaProcessorRegistryReader();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
	}

	public IJSONSchemaProcessor getDefaultProcessor() {
		return defaultProcessor;
	}

	public IJSONSchemaDocument getSchemaDocument(IJSONNode node)
			throws IOException {
		return getSchemaDocument(node.getModel());
	}

	public IJSONSchemaDocument getSchemaDocument(IJSONModel model)
			throws IOException {
		IJSONSchemaProcessor processor = getDefaultProcessor();
		if (processor == null) {
			return null;
		}
		IJSONDocument document = model.getDocument();
		IJSONNode jsonObject = document.getFirstChild();
		if (jsonObject != null) {
			IJSONNode child = jsonObject.getFirstChild();
			while (child != null) {
				if (child instanceof IJSONPair) {
					IJSONPair pair = (IJSONPair) child;
					String name = pair.getName();
					IJSONValue valueNode = pair.getValue();
					if (valueNode != null && "$schema".equals(name)) { //$NON-NLS-1$
						String schema = JSONUtil.getString(valueNode);
						try {
							if (schema != null) {
								schema = URIHelper.addImpliedFileProtocol(schema);
								new URL(schema);
								return processor.getSchema(schema);
							}
						} catch (MalformedURLException e) {
						}
					}
				}
				child = child.getNextSibling();
			}
		}
		String base = model == null || model.getResolver() == null ?
				null : model.getResolver().getFileBaseLocation();
		/**
		 * We shouldn't assert a failure because the catalog does not require a
		 * base location to operate and it will be called from non-file-based
		 * scenarios.
		 * 
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=206176
		 */
		// Assert.isNotNull(base, "Base location is expected to be non null."); //$NON-NLS-1$
		if (base != null) {
			base = URIHelper.addImpliedFileProtocol(base);
		}
		String schemaURL = resolve(base, null, null);
		if (schemaURL != null) {
			return processor.getSchema(schemaURL);
		}
		return null;
	}

	private String getFileMatch(String location) {
		if (location == null) {
			return null;
		}
		int index = location.lastIndexOf('/');
		if (index == -1) {
			index = location.lastIndexOf('\\');
		}
		if (index != -1) {
			return location.substring(index, location.length());
		}
		return location;
	}

	private String resolve(String base, String publicId, String systemId) {
		String result = systemId;
		result = URIResolverPlugin.createResolver().resolve(base, publicId,
				systemId);
		return result;
	}

	/**
	 * read from plugin registry and parse it.
	 */
	protected void readRegistry() {
		IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = pluginRegistry
				.getExtensionPoint(JSONCorePlugin.getDefault().getBundle()
						.getSymbolicName(), EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				readElement(elements[i]);
			}
		}
	}

	protected void readElement(IConfigurationElement element) {
		if (TAG_CONTRIBUTION.equals(element.getName())) {
			String id = element.getAttribute("id");
			String name = element.getAttribute("name");
			try {
				IJSONSchemaProcessor schemaProcessor = (IJSONSchemaProcessor) element
						.createExecutableExtension("class");
				this.defaultProcessor = schemaProcessor;
			} catch (CoreException e) {
				Logger.logException(e);
			}
		}

	}

}
