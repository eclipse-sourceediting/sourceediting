/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui;

import java.io.IOException;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.registry.embedded.EmbeddedAdapterFactoryRegistryImpl;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeXML;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeXMLAttribute;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeXMLAttributeValue;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeXMLTag;


/**
 * The main plugin class to be used in the desktop.
 */
public class XMLEditorPlugin extends AbstractUIPlugin {
	public final static String ID = "org.eclipse.wst.xml.ui"; //$NON-NLS-1$
	protected static XMLEditorPlugin instance = null;

	/**
	 * The template store for the xml editor.
	 * 
	 * @since 3.0
	 */
	private TemplateStore fTemplateStore;

	/**
	 * The template context type registry for the xml editor.
	 * 
	 * @since 3.0
	 */
	private ContextTypeRegistry fContextTypeRegistry;

	public XMLEditorPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		instance = this;

		// reference the preference store so
		// initializeDefaultPreferences(IPreferenceStore preferenceStore) is
		// called
		getPreferenceStore();
		JobStatusLineHelper.init();
	}

	public static XMLEditorPlugin getDefault() {
		return instance;
	}

	public synchronized static XMLEditorPlugin getInstance() {
		return instance;
	}

	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();

	}

	public AdapterFactoryRegistry getEmbeddedAdapterFactoryRegistry() {
		return EmbeddedAdapterFactoryRegistryImpl.getInstance();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeDefaultPluginPreferences()
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {

		// ignore this preference store
		// use EditorPlugin preference store
		IPreferenceStore editorStore = ((AbstractUIPlugin) Platform.getPlugin(EditorPlugin.ID)).getPreferenceStore();
		EditorPlugin.initializeDefaultEditorPreferences(editorStore);
		initializeDefaultXMLPreferences(editorStore);
	}

	protected void initializeDefaultXMLPreferences(IPreferenceStore store) {

		String ctId = IContentTypeIdentifier.ContentTypeID_SSEXML;

		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.CONTENT_ASSIST_SUPPORTED, ctId), true);
		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.AUTO_PROPOSE, ctId), true);
		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, ctId), CommonEditorPreferenceNames.LT);

		store.setDefault(CommonEditorPreferenceNames.EDITOR_VALIDATION_METHOD, CommonEditorPreferenceNames.EDITOR_VALIDATION_CONTENT_MODEL); //$NON-NLS-1$

		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.EDITOR_VALIDATION_METHOD, ctId), CommonEditorPreferenceNames.EDITOR_VALIDATION_CONTENT_MODEL); //$NON-NLS-1$		
		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR, ctId), true);
	}

	/**
	 * Returns the template store for the xml editor templates.
	 * 
	 * @return the template store for the xml editor templates
	 */
	public TemplateStore getTemplateStore() {
		if (fTemplateStore == null) {
			fTemplateStore = new ContributionTemplateStore(getTemplateContextRegistry(), getPreferenceStore(), CommonEditorPreferenceNames.TEMPLATES_KEY);

			try {
				fTemplateStore.load();
			}
			catch (IOException e) {
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
			fContextTypeRegistry = new ContributionContextTypeRegistry();

			fContextTypeRegistry.addContextType(new TemplateContextTypeXML());
			fContextTypeRegistry.addContextType(new TemplateContextTypeXMLTag());
			fContextTypeRegistry.addContextType(new TemplateContextTypeXMLAttribute());
			fContextTypeRegistry.addContextType(new TemplateContextTypeXMLAttributeValue());
		}

		return fContextTypeRegistry;
	}
}
