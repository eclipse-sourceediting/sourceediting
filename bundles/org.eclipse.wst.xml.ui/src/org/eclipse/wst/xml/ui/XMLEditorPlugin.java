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
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.sse.ui.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.registry.embedded.EmbeddedAdapterFactoryRegistryImpl;
import org.eclipse.wst.xml.ui.style.IStyleConstantsXML;
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

	public static XMLEditorPlugin getDefault() {
		return instance;
	}

	public synchronized static XMLEditorPlugin getInstance() {
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

	/**
	 * true if editor preference store has been initialized with default
	 * values false otherwise
	 */
	private boolean preferencesInitd = false;

	public XMLEditorPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		instance = this;

		// reference the preference store so
		// initializeDefaultPreferences(IPreferenceStore preferenceStore) is
		// called
		IPreferenceStore store = getPreferenceStore();
		// for some reason initializeDefaultPreferences is not always called,
		// so
		// just add an extra check to see if not initialized, then call init
		if (!preferencesInitd) {
			initializeDefaultPreferences(store);
		}
		JobStatusLineHelper.init();
	}

	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();

	}

	public AdapterFactoryRegistry getEmbeddedAdapterFactoryRegistry() {
		return EmbeddedAdapterFactoryRegistryImpl.getInstance();

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
			} catch (IOException e) {
				Logger.logException(e);
			}
		}
		return fTemplateStore;
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
		preferencesInitd = true;
	}

	protected void initializeDefaultXMLPreferences(IPreferenceStore store) {

		String ctId = IContentTypeIdentifier.ContentTypeID_SSEXML;

		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.CONTENT_ASSIST_SUPPORTED, ctId), true);
		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.AUTO_PROPOSE, ctId), true);
		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, ctId), CommonEditorPreferenceNames.LT);

		store.setDefault(CommonEditorPreferenceNames.EDITOR_VALIDATION_METHOD, CommonEditorPreferenceNames.EDITOR_VALIDATION_CONTENT_MODEL); //$NON-NLS-1$

		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.EDITOR_VALIDATION_METHOD, ctId), CommonEditorPreferenceNames.EDITOR_VALIDATION_CONTENT_MODEL); //$NON-NLS-1$		
		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR, ctId), true);

		// XML Style Preferences
		String NOBACKGROUNDBOLD = " | null | false"; //$NON-NLS-1$
		String styleValue = ColorHelper.getColorString(127, 0, 127) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_ATTRIBUTE_NAME, ctId), styleValue);

		styleValue = ColorHelper.getColorString(42, 0, 255) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE, ctId), styleValue);

		styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS, ctId), styleValue); // specified
		// value
		// is
		// black;
		// leaving
		// as
		// widget
		// default

		styleValue = ColorHelper.getColorString(63, 95, 191) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.COMMENT_BORDER, ctId), styleValue);
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.COMMENT_TEXT, ctId), styleValue);

		styleValue = ColorHelper.getColorString(0, 128, 128) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.DECL_BORDER, ctId), styleValue);

		styleValue = ColorHelper.getColorString(0, 0, 128) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.DOCTYPE_NAME, ctId), styleValue);
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF, ctId), styleValue);

		styleValue = ColorHelper.getColorString(128, 128, 128) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID, ctId), styleValue);

		styleValue = ColorHelper.getColorString(63, 127, 95) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF, ctId), styleValue);

		styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.XML_CONTENT, ctId), styleValue); // specified
		// value
		// is
		// black;
		// leaving
		// as
		// widget
		// default

		styleValue = ColorHelper.getColorString(0, 128, 128) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_BORDER, ctId), styleValue);

		styleValue = ColorHelper.getColorString(63, 127, 127) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_NAME, ctId), styleValue);

		styleValue = ColorHelper.getColorString(0, 128, 128) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.PI_BORDER, ctId), styleValue);

		styleValue = "null" + NOBACKGROUNDBOLD; //$NON-NLS-1$
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.PI_CONTENT, ctId), styleValue); // specified
		// value
		// is
		// black;
		// leaving
		// as
		// widget
		// default

		styleValue = ColorHelper.getColorString(0, 128, 128) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.CDATA_BORDER, ctId), styleValue);

		styleValue = ColorHelper.getColorString(0, 0, 0) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.CDATA_TEXT, ctId), styleValue);
	}
}
