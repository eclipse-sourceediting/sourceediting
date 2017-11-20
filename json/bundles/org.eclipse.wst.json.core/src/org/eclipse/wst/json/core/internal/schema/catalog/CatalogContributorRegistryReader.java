/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.catalog.CatalogContributorRegistryReader
 *                                           modified in order to process JSON Objects.          
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.schema.catalog;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.internal.JSONCoreMessages;
import org.eclipse.wst.json.core.internal.Logger;
import org.eclipse.wst.json.core.schema.catalog.ICatalog;
import org.eclipse.wst.json.core.schema.catalog.ICatalogElement;
import org.eclipse.wst.json.core.schema.catalog.ICatalogEntry;
import org.osgi.framework.Bundle;

/**
 * <pre>
 * <extension point="org.eclipse.wst.xml.core.schemaCatalogContributions"> 
 * <schemaCatalogContribution>
 *         	<schema name="bower.json"
 *          			description="Bower package description file"
 *          			fileMatch="bower.json,bower.json"
 *          			url="http://json.schemastore.org/bower"
 *          			uri="schemastore/bower" />
 * 			<schema name=".bowerrc"
 *          			description="Bower configuration file"
 *          			fileMatch=".bowerrc"
 *          			url="http://json.schemastore.org/bowerrc"
 *          			uri="schemastore/bowerrc" />
 * 			<schema name=".jshintrc"
 *          			description="JSHint configuration file"
 *          			fileMatch=".jshintrc"
 *          			url="http://json.schemastore.org/jshintrc"
 *          			uri="schemastore/jshintrc" />         			
 * 
 *  </schemaCatalogContribution> 
 *  </extension>
 * </pre>
 * 
 */
public class CatalogContributorRegistryReader {
	protected static final String EXTENSION_POINT_ID = "schemaCatalogContributions"; //$NON-NLS-1$
	protected static final String TAG_CONTRIBUTION = "schemaCatalogContribution"; //$NON-NLS-1$

	protected ICatalog catalog;

	protected String declaringExtensionId;

	protected CatalogContributorRegistryReader(ICatalog catalog) {
		this.catalog = catalog;
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

	public static String resolvePath(URL platformURL, String path) {
		String fileLocation = path;
		int jarPathStart = path.indexOf("jar:"); //$NON-NLS-1$
		jarPathStart = jarPathStart < 0 ? 0 : jarPathStart + "jar:".length(); //$NON-NLS-1$
		int jarPathEnd = path.indexOf("!"); //$NON-NLS-1$
		jarPathEnd = jarPathEnd < 0 ? path.length() : jarPathEnd;
		fileLocation = path.substring(jarPathStart, jarPathEnd);

		String result = path;
		String resolvedLocation = fileLocation;
		URL resolvedURL = null;
		if (fileLocation.startsWith("platform:/plugin")) //$NON-NLS-1$
		{
			// this is the speclial case, where the resource is located relative
			// to another plugin (not the one that declares the extension point)
			//
			try {
				resolvedURL = Platform.resolve(new URL(fileLocation));
			} catch (IOException e) {
				// do nothing
			}
		} else {
			// this is the typical case, where the resource is located relative
			// to the plugin that declares the extension point
			try {
				resolvedURL = new URL(Platform.resolve(platformURL),
						fileLocation);
			} catch (IOException e) {
				// do nothing
			}
		}

		if (resolvedURL != null) {
			resolvedLocation = resolvedURL.toExternalForm().replace('\\', '/');
			result = result.replaceFirst(fileLocation, resolvedLocation);
		}
		return result;
	}

	public static URL getPlatformURL(String pluginId) {
		Bundle bundle = Platform.getBundle(pluginId);
		if (bundle != null) {
			URL bundleEntry = bundle.getEntry("/"); //$NON-NLS-1$

			if (bundleEntry != null) {
				try {
					return Platform.resolve(bundleEntry);
				} catch (IOException e) {
					Logger.logException(e);
				}
			}
		}
		return null;
	}

	private String resolvePath(String path) {
		return resolvePath(getPlatformURL(declaringExtensionId), path);
	}

	protected void readElement(IConfigurationElement element) {
		try {
			declaringExtensionId = element.getDeclaringExtension()
					.getNamespace();
		} catch (InvalidRegistryObjectException e) {
			Logger.logException(e);
		}

		if (TAG_CONTRIBUTION.equals(element.getName())) {
			IConfigurationElement[] mappingInfoElementList = element
					.getChildren(SchemaStoreCatalogConstants.TAG_SCHEMA);
			processMappingInfoElements(mappingInfoElementList);
		}

	}

	private void processMappingInfoElements(
			IConfigurationElement[] childElementList) {
		if (catalog == null)
			return;
		for (int i = 0; i < childElementList.length; i++) {
			IConfigurationElement childElement = childElementList[i];
			String name = childElement.getName();
			int type = ICatalogEntry.ENTRY_TYPE_SCHEMA;
			String fileMatch = null;
			if (SchemaStoreCatalogConstants.TAG_SCHEMA.equals(name)) {
				fileMatch = childElement
						.getAttribute(SchemaStoreCatalogConstants.ATTR_SCHEMA_FILEMATCH);
//				fileMatch = childElement
//						.getAttribute(SchemaStoreCatalogConstants.ATTR_SCHEMA_FILEMATCH);
				
			}
/*if (SchemaStoreCatalogConstants.TAG_PUBLIC.equals(name)) {
				key = childElement
						.getAttribute(SchemaStoreCatalogConstants.ATTR_PUBLIC_ID);
			} else if (SchemaStoreCatalogConstants.TAG_SYSTEM.equals(name)) {
				key = childElement
						.getAttribute(SchemaStoreCatalogConstants.ATTR_SYSTEM_ID);
				type = ICatalogEntry.ENTRY_TYPE_SYSTEM;
			} else if (SchemaStoreCatalogConstants.TAG_URI.equals(name)) {
				key = childElement
						.getAttribute(SchemaStoreCatalogConstants.ATTR_NAME);
				type = ICatalogEntry.ENTRY_TYPE_URI;
			} else if (SchemaStoreCatalogConstants.TAG_NEXT_CATALOG
					.equals(name)) {
				processNextSchemaCatalogElements(new IConfigurationElement[] { childElement });
				continue;
			}
			if (key == null || key.equals("")) //$NON-NLS-1$
			{
				Logger.log(Logger.ERROR,
						JSONCoreMessages.SchemaCatalog_entry_key_not_set);
				continue;
			}*/
			String entryURI = childElement
					.getAttribute(SchemaStoreCatalogConstants.ATTR_SCHEMA_URI); // mandatory
			if (entryURI == null || entryURI.equals("")) //$NON-NLS-1$
			{
				Logger.log(Logger.ERROR,
						JSONCoreMessages.Catalog_entry_uri_not_set);
				continue;
			}
			ICatalogElement catalogElement = catalog
					.createCatalogElement(type);
			if (catalogElement instanceof ICatalogEntry) {
				ICatalogEntry entry = (ICatalogEntry) catalogElement;
				entry.setKey(fileMatch);
				String resolvedPath = resolvePath(entryURI);
				entry.setURI(resolvedPath);
				String id = childElement
						.getAttribute(SchemaStoreCatalogConstants.ATTR_SCHEMA_NAME); // optional
				if (id != null && !id.equals("")) //$NON-NLS-1$
				{
					entry.setId(id);
				}
			}
			// process any other attributes
			for (int j = 0; j < childElement.getAttributeNames().length; j++) {
				String attrName = childElement.getAttributeNames()[j];
//				if (!attrName.equals(SchemaStoreCatalogConstants.ATTR_URI)
//						&& !attrName
//								.equals(SchemaStoreCatalogConstants.ATTR_NAME)
//						&& !attrName
//								.equals(SchemaStoreCatalogConstants.ATTR_PUBLIC_ID)
//						&& !attrName
//								.equals(SchemaStoreCatalogConstants.ATTR_SYSTEM_ID)
//						&& !attrName
//								.equals(SchemaStoreCatalogConstants.ATTR_CATALOG)
//						&& !attrName
//								.equals(SchemaStoreCatalogConstants.ATTR_ID)
//						&& !attrName
//								.equals(SchemaStoreCatalogConstants.ATTR_BASE)) {
//					String attrValue = childElement.getAttribute(attrName);
//					if (attrValue != null && !attrValue.equals("")) //$NON-NLS-1$
//					{
//						schemaCatalogElement.setAttributeValue(attrName,
//								attrValue);
//					}
//				}
			}
			catalog.addCatalogElement(catalogElement);			
		}
	}

//	private void processNextSchemaCatalogElements(
//			IConfigurationElement[] childElementList) {
//		if (schemaCatalog == null)
//			return;
//		for (int i = 0; i < childElementList.length; i++) {
//			IConfigurationElement childElement = childElementList[i];
//			String location = childElement
//					.getAttribute(SchemaStoreCatalogConstants.ATTR_CATALOG); // mandatory
//			if (location == null || location.equals("")) //$NON-NLS-1$
//			{
//				Logger.log(
//						Logger.ERROR,
//						JSONCoreMessages.SchemaCatalog_next_schemaCatalog_location_uri_not_set);
//				continue;
//			}
//			INextCatalog nextSchemaCatalog = new NextCatalog();
//			String resolvedPath = resolvePath(location);
//			nextSchemaCatalog.setCatalogLocation(resolvedPath);
//			String id = childElement
//					.getAttribute(SchemaStoreCatalogConstants.ATTR_ID);
//			if (id != null && !id.equals("")) //$NON-NLS-1$
//			{
//				nextSchemaCatalog.setId(id);
//			}
//			schemaCatalog.addCatalogElement(nextSchemaCatalog);
//		}
//	}

}
