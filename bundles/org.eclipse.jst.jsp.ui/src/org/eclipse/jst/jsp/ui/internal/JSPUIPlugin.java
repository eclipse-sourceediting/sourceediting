/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.jsp.ui.templates.TemplateContextTypeJSP;
import org.eclipse.jst.jsp.ui.templates.TemplateContextTypeJSPAttribute;
import org.eclipse.jst.jsp.ui.templates.TemplateContextTypeJSPAttributeValue;
import org.eclipse.jst.jsp.ui.templates.TemplateContextTypeJSPTag;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.html.ui.style.IStyleConstantsHTML;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.sse.ui.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.registry.embedded.EmbeddedAdapterFactoryRegistryImpl;
import org.eclipse.wst.xml.ui.style.IStyleConstantsXML;

/**
 * The main plugin class to be used in the desktop.
 */
public class JSPUIPlugin extends AbstractUIPlugin {
	public final static String ID = "org.eclipse.jst.jsp.ui"; //$NON-NLS-1$

	protected static JSPUIPlugin instance = null;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private static final String KEY_PREFIX = "%"; //$NON-NLS-1$
	private static final String KEY_DOUBLE_PREFIX = "%%"; //$NON-NLS-1$	
	
	/**
	 * The template store for the jsp editor. 
	 */
	private TemplateStore fTemplateStore;
	
	/** 
	 * The template context type registry for the jsp editor. 
	 */
	private ContextTypeRegistry fContextTypeRegistry;

	public JSPUIPlugin() {
		super();
		instance = this;
		
		// Force a call to initializeDefaultJSPPreferences since
		// initializeDefaultPreferences is only called if *this* plugin's
		// preference store is accessed
		initializeDefaultJSPPreferences(SSEUIPlugin.getDefault().getPreferenceStore());
	}

	public static JSPUIPlugin getDefault() {
		return instance;
	}

	public synchronized static JSPUIPlugin getInstance() {
		return instance;
	}

	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();

	}

	public AdapterFactoryRegistry getEmbeddedAdapterFactoryRegistry() {
		return EmbeddedAdapterFactoryRegistryImpl.getInstance();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeDefaultPluginPreferences()
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		
		// ignore this preference store
		// use EditorPlugin preference store
		IPreferenceStore editorStore = SSEUIPlugin.getDefault().getPreferenceStore();
		initializeDefaultJSPPreferences(editorStore);
	}

	protected void initializeDefaultJSPPreferences(IPreferenceStore store) {
		
		String ctId = IContentTypeIdentifier.ContentTypeID_JSP;
		// setting the same as HTML
		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.CONTENT_ASSIST_SUPPORTED, ctId), true);
		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.AUTO_PROPOSE, ctId), true);
		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, ctId), CommonEditorPreferenceNames.LT);

		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.EDITOR_VALIDATION_METHOD, ctId), CommonEditorPreferenceNames.EDITOR_VALIDATION_WORKBENCH_DEFAULT); //$NON-NLS-1$
		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR, ctId), true);
		
		// JSP Style Preferences
		String NOBACKGROUNDBOLD = " | null | false";   //$NON-NLS-1$
		String styleValue = ColorHelper.getColorString(127, 0, 127) + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_ATTRIBUTE_NAME, ctId), styleValue);
		
		styleValue = ColorHelper.getColorString(42, 0, 255)  + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE, ctId), styleValue);
		
		styleValue = "null" + NOBACKGROUNDBOLD;  //$NON-NLS-1$
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS, ctId), styleValue); // specified value is black; leaving as widget default
		
		styleValue = ColorHelper.getColorString(63, 95, 191)  + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.COMMENT_BORDER, ctId), styleValue);
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.COMMENT_TEXT, ctId), styleValue);
		
		styleValue = ColorHelper.getColorString(0, 128, 128)  + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_BORDER, ctId), styleValue);
		
		styleValue = ColorHelper.getColorString(63, 127, 127)  + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_NAME, ctId), styleValue);
		
		styleValue = ColorHelper.getColorString(191, 95, 63)  + NOBACKGROUNDBOLD;
		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsHTML.SCRIPT_AREA_BORDER, ctId), styleValue);
	}
	
	/**
	 * Returns the template store for the jsp editor templates.
	 * 
	 * @return the template store for the jsp editor templates
	 */
	public TemplateStore getTemplateStore() {
		if (fTemplateStore == null) {
			fTemplateStore= new ContributionTemplateStore(getTemplateContextRegistry(), getPreferenceStore(), CommonEditorPreferenceNames.TEMPLATES_KEY);

			try {
				fTemplateStore.load();
			} catch (IOException e) {
				Logger.logException(e);
			}
		}		
		return fTemplateStore;
	}
	
	/**
	 * Returns the template context type registry for the jsp plugin.
	 * 
	 * @return the template context type registry for the jsp plugin
	 */
	public ContextTypeRegistry getTemplateContextRegistry() {
		if (fContextTypeRegistry == null) {
			fContextTypeRegistry= new ContributionContextTypeRegistry();
			
			fContextTypeRegistry.addContextType(new TemplateContextTypeJSP());
			fContextTypeRegistry.addContextType(new TemplateContextTypeJSPTag());
			fContextTypeRegistry.addContextType(new TemplateContextTypeJSPAttribute());
			fContextTypeRegistry.addContextType(new TemplateContextTypeJSPAttributeValue());
		}

		return fContextTypeRegistry;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String value) {
		String s = value.trim();
		if (!s.startsWith(KEY_PREFIX, 0))
			return s;
		if (s.startsWith(KEY_DOUBLE_PREFIX, 0))
			return s.substring(1);

		int ix = s.indexOf(' ');
		String key = ix == -1 ? s : s.substring(0, ix);

		ResourceBundle bundle = getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key.substring(1)) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public static String getResourceString(String key, Object[] args) {

		try {
			return MessageFormat.format(getResourceString(key), args);
		} catch (IllegalArgumentException e) {
			return getResourceString(key);
		}

	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.eclipse.jst.jsp.ui.internal.JSPUIPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
}
