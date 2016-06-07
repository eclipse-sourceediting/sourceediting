/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.ui.internal;

import java.io.IOException;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.json.ui.internal.preferences.JSONUIPreferenceNames;
import org.eclipse.wst.json.ui.internal.templates.TemplateContextTypeIdsJSON;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JSONUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.wst.json.ui"; //$NON-NLS-1$

	// The shared instance
	private static JSONUIPlugin plugin;

	/**
	 * The template store for the json ui.
	 */
	private TemplateStore fTemplateStore;

	/**
	 * The template context type registry for json ui.
	 */
	private ContextTypeRegistry fContextTypeRegistry;

	/**
	 * The constructor
	 */
	public JSONUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JSONUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the template store for the JSON editor templates.
	 *
	 * @return the template store for the JSON editor templates
	 */
	public TemplateStore getTemplateStore() {
		if (fTemplateStore == null) {
			fTemplateStore = new ContributionTemplateStore(getTemplateContextRegistry(), getPreferenceStore(),
					JSONUIPreferenceNames.TEMPLATES_KEY);

			try {
				fTemplateStore.load();
			} catch (IOException e) {
				Logger.logException(e);
			}
		}
		return fTemplateStore;
	}

	/**
	 * Returns the template context type registry for the JSON plugin.
	 *
	 * @return the template context type registry for the JSON plugin
	 */
	public ContextTypeRegistry getTemplateContextRegistry() {
		if (fContextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
			registry.addContextType(TemplateContextTypeIdsJSON.ALL);
			registry.addContextType(TemplateContextTypeIdsJSON.NEW);
			registry.addContextType(TemplateContextTypeIdsJSON.PACKAGE);
			fContextTypeRegistry = registry;
		}

		return fContextTypeRegistry;
	}
}
