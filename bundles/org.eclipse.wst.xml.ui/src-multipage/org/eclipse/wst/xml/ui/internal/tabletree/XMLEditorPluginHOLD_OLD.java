/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.tabletree;



import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistryImpl;

/**
 * @deprecated This plugin has combined with the org.eclipse.wst.xml.ui
 *             plugin. Use XMLUIPlugin instead.
 */
public class XMLEditorPluginHOLD_OLD extends AbstractUIPlugin {

	public final static String PLUGIN_ID = "org.eclipse.wst.xml.ui.internal.XMLEditorPluginHOLD_OLD"; //$NON-NLS-1$
	protected static XMLEditorPluginHOLD_OLD instance = null;

	/**
	 * XMLUIPlugin constructor comment.
	 */
	public XMLEditorPluginHOLD_OLD() {
		super();
		instance = this;

		// reference the preference store so
		// initializeDefaultPreferences(IPreferenceStore preferenceStore) is
		// called
		getPreferenceStore();
	}

	public static XMLEditorPluginHOLD_OLD getDefault() {
		return instance;
	}

	public synchronized static XMLEditorPluginHOLD_OLD getInstance() {
		return instance;
	}

	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();

	}
}
