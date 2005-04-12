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

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.registry.embedded.EmbeddedAdapterFactoryRegistryImpl;
import org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceNames;
import org.eclipse.wst.xml.ui.internal.templates.TemplateContextTypeIdsXML;

/**
 * The main plugin class to be used in the desktop.
 */
public class XMLUIPlugin extends AbstractUIPlugin {
	public final static String ID = "org.eclipse.wst.xml.ui"; //$NON-NLS-1$
	protected static XMLUIPlugin instance = null;

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
			fTemplateStore = new ContributionTemplateStore(getTemplateContextRegistry(), getPreferenceStore(), XMLUIPreferenceNames.TEMPLATES_KEY);

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
}
