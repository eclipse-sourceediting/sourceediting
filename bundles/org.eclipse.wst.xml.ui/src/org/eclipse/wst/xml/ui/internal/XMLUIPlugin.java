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
package org.eclipse.wst.xml.ui.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.internal.provisional.registry.embedded.EmbeddedAdapterFactoryRegistryImpl;
import org.eclipse.wst.xml.ui.internal.catalog.XMLCatalogFileType;
import org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceNames;
import org.eclipse.wst.xml.ui.internal.templates.TemplateContextTypeIdsXML;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class XMLUIPlugin extends AbstractUIPlugin {
	public final static String ID = "org.eclipse.wst.xml.ui"; //$NON-NLS-1$

	protected static XMLUIPlugin instance = null;

	protected static HashMap catalogFileTypeMap = new HashMap();

	public static XMLUIPlugin getDefault() {
		return instance;
	}

	public synchronized static XMLUIPlugin getInstance() {
		return instance;
	}

	/**
	 * The template context type registry for the xml editor.
	 * 
	 * @since 3.0
	 */
	private ContextTypeRegistry fContextTypeRegistry;

	/**
	 * The template store for the xml editor.
	 * 
	 * @since 3.0
	 */
	private TemplateStore fTemplateStore;

	public XMLUIPlugin() {
		super();
		instance = this;

		JobStatusLineHelper.init();
	}

	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();

	}

	public AdapterFactoryRegistry getEmbeddedAdapterFactoryRegistry() {
		return EmbeddedAdapterFactoryRegistryImpl.getInstance();

	}

	/**
	 * Returns the template store for the xml editor templates.
	 * 
	 * @return the template store for the xml editor templates
	 */
	public TemplateStore getTemplateStore() {
		if (fTemplateStore == null) {
			fTemplateStore = new ContributionTemplateStore(
					getTemplateContextRegistry(), getPreferenceStore(),
					XMLUIPreferenceNames.TEMPLATES_KEY);

			try {
				fTemplateStore.load();
			} catch (IOException e) {
				Logger.logException(e);
			}
		}
		return fTemplateStore;
	}

	/**
	 * Returns the template context type registry for the xml plugin.
	 * 
	 * @return the template context type registry for the xml plugin
	 */
	public ContextTypeRegistry getTemplateContextRegistry() {
		if (fContextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
			registry.addContextType(TemplateContextTypeIdsXML.ALL);
			registry.addContextType(TemplateContextTypeIdsXML.NEW);
			registry.addContextType(TemplateContextTypeIdsXML.TAG);
			registry.addContextType(TemplateContextTypeIdsXML.ATTRIBUTE);
			registry.addContextType(TemplateContextTypeIdsXML.ATTRIBUTE_VALUE);

			fContextTypeRegistry = registry;
		}

		return fContextTypeRegistry;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		new CatalogFileTypeRegistryReader(catalogFileTypeMap).readRegistry();
	}

	public static Collection getXMLCatalogFileTypes() {
		return catalogFileTypeMap.values();
	}
	
	/**
	   * Get an image from the registry. 
	   * 
	   * *This method is used by the referencingfile dialog and should be 
	   *  removed when the dialog is moved to anothercomponent.
	   * 
	   * @param imageName The name of the image.
	   * @return The image registered for the given name.
	   */
	public Image getImage(String imageName){
	    return getWorkbench().getSharedImages().getImage(imageName);
	}

	class CatalogFileTypeRegistryReader extends BaseRegistryReader {
		protected static final String EXTENSION_POINT_ID = "catalogFileType";

		protected static final String TAG_NAME = "fileType";

		protected static final String ATT_ID = "id";

		protected static final String ATT_EXTENSIONS = "extensions";

		protected static final String ATT_DESCRIPTION = "description";

		protected static final String ATT_ICON = "icon";

		protected HashMap hashMap;

		public CatalogFileTypeRegistryReader(HashMap hashMap) {
			this.hashMap = hashMap;
		}

		public void readRegistry() {
			readRegistry(EXTENSION_POINT_ID);
		}

		protected void readElement(IConfigurationElement element) {
			if (element.getName().equals(TAG_NAME)) {
				String id = element.getAttribute(ATT_ID);
				if (id != null) {
					XMLCatalogFileType fileType = (XMLCatalogFileType) hashMap
							.get(id);
					if (fileType == null) {
						fileType = new XMLCatalogFileType();
						hashMap.put(id, fileType);
					}

					if (fileType.description == null) {
						String description = element
								.getAttribute(ATT_DESCRIPTION);
						fileType.description = description;
					}

					fileType
							.addExtensions(element.getAttribute(ATT_EXTENSIONS));
				}
			}
		}

	}

	class BaseRegistryReader {
		/**
		 * read from plugin registry and parse it.
		 */
		public void readRegistry(String extensionPointId) {
			IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
			IExtensionPoint point = pluginRegistry.getExtensionPoint(ID,
					extensionPointId);
			if (point != null) {
				IConfigurationElement[] elements = point
						.getConfigurationElements();
				for (int i = 0; i < elements.length; i++) {
					readElement(elements[i]);
				}
			}
		}

		protected void readElement(IConfigurationElement element) {
		}
	}
}
