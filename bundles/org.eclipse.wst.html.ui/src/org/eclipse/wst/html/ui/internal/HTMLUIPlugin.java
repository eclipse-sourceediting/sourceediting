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
package org.eclipse.wst.html.ui.internal;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.html.ui.templates.TemplateContextTypeHTML;
import org.eclipse.wst.html.ui.templates.TemplateContextTypeHTMLAttribute;
import org.eclipse.wst.html.ui.templates.TemplateContextTypeHTMLAttributeValue;
import org.eclipse.wst.html.ui.templates.TemplateContextTypeHTMLTag;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.registry.embedded.EmbeddedAdapterFactoryRegistryImpl;

/**
 * The main plugin class to be used in the desktop.
 */
public class HTMLUIPlugin extends AbstractUIPlugin {
	public final static String ID = "org.eclipse.wst.html.ui"; //$NON-NLS-1$

	protected static HTMLUIPlugin instance = null;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private static final String KEY_PREFIX = "%"; //$NON-NLS-1$
	private static final String KEY_DOUBLE_PREFIX = "%%"; //$NON-NLS-1$	
	
	/**
	 * The template store for the html editor. 
	 */
	private TemplateStore fTemplateStore;
	
	/** 
	 * The template context type registry for the html editor. 
	 */
	private ContextTypeRegistry fContextTypeRegistry;

	public HTMLUIPlugin() {
		super();
		instance = this;

		// Force a call to initialize default preferences since
		// initializeDefaultPreferences is only called if *this* plugin's
		// preference store is accessed
		initializeDefaultHTMLPreferences(SSEUIPlugin.getDefault().getPreferenceStore());
	}

	public static HTMLUIPlugin getDefault() {
		return instance;
	}

	public synchronized static HTMLUIPlugin getInstance() {
		return instance;
	}

	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();

	}

	public AdapterFactoryRegistry getEmbeddedAdapterFactoryRegistry() {
		return EmbeddedAdapterFactoryRegistryImpl.getInstance();

	}

	/**
	 * @deprecated using HTMLUIPreferenceInitializer instead
	 */
	protected void initializeDefaultHTMLPreferences(IPreferenceStore store) {
//		String ctId = IContentTypeIdentifier.ContentTypeID_HTML;
//
//		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.CONTENT_ASSIST_SUPPORTED, ctId), true);
//		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.AUTO_PROPOSE, ctId), true);
//		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, ctId), CommonEditorPreferenceNames.LT);
//
//		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.EDITOR_VALIDATION_METHOD, ctId), CommonEditorPreferenceNames.EDITOR_VALIDATION_WORKBENCH_DEFAULT); //$NON-NLS-1$
//		store.setDefault(PreferenceKeyGenerator.generateKey(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR, ctId), true);
//		
//		// HTML Style Preferences
//		String NOBACKGROUNDBOLD = " | null | false";   //$NON-NLS-1$
//		String styleValue = ColorHelper.getColorString(127, 0, 127) + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_ATTRIBUTE_NAME, ctId), styleValue);
//		
//		styleValue = ColorHelper.getColorString(42, 0, 255)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE, ctId), styleValue);
//		
//		styleValue = "null" + NOBACKGROUNDBOLD;  //$NON-NLS-1$
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS, ctId), styleValue); // specified value is black; leaving as widget default
//		
//		styleValue = ColorHelper.getColorString(63, 95, 191)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.COMMENT_BORDER, ctId), styleValue);
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.COMMENT_TEXT, ctId), styleValue);
//		
//		styleValue = ColorHelper.getColorString(0, 128, 128)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.DECL_BORDER, ctId), styleValue);
//
//		styleValue = ColorHelper.getColorString(0, 0, 128)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.DOCTYPE_NAME, ctId), styleValue);
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF, ctId), styleValue);
//		
//		styleValue = ColorHelper.getColorString(128, 128, 128)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID, ctId), styleValue);
//
//		styleValue = ColorHelper.getColorString(63, 127, 95)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF, ctId), styleValue);
//
//		styleValue = "null" + NOBACKGROUNDBOLD;	//$NON-NLS-1$
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.XML_CONTENT, ctId), styleValue);	// specified value is black; leaving as widget default
//
//		styleValue = ColorHelper.getColorString(0, 128, 128)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_BORDER, ctId), styleValue);
//		
//		styleValue = ColorHelper.getColorString(63, 127, 127)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.TAG_NAME, ctId), styleValue);
//		
//		styleValue = ColorHelper.getColorString(0, 128, 128)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.PI_BORDER, ctId), styleValue);
// 
//		styleValue = "null" + NOBACKGROUNDBOLD;	//$NON-NLS-1$
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.PI_CONTENT, ctId), styleValue);	// specified value is black; leaving as widget default
//
//		styleValue = ColorHelper.getColorString(0, 128, 128)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.CDATA_BORDER, ctId), styleValue);
//		
//		styleValue = ColorHelper.getColorString(0, 0, 0)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsXML.CDATA_TEXT, ctId), styleValue);
//		
//		styleValue = ColorHelper.getColorString(191, 95, 63)  + NOBACKGROUNDBOLD;
//		store.setDefault(PreferenceKeyGenerator.generateKey(IStyleConstantsHTML.SCRIPT_AREA_BORDER, ctId), styleValue);
	}
	
	/**
	 * Returns the template store for the html editor templates.
	 * 
	 * @return the template store for the html editor templates
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
	 * Returns the template context type registry for the html plugin.
	 * 
	 * @return the template context type registry for the html plugin
	 */
	public ContextTypeRegistry getTemplateContextRegistry() {
		if (fContextTypeRegistry == null) {
			fContextTypeRegistry= new ContributionContextTypeRegistry();
			
			fContextTypeRegistry.addContextType(new TemplateContextTypeHTML());
			fContextTypeRegistry.addContextType(new TemplateContextTypeHTMLTag());
			fContextTypeRegistry.addContextType(new TemplateContextTypeHTMLAttribute());
			fContextTypeRegistry.addContextType(new TemplateContextTypeHTMLAttributeValue());
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
				resourceBundle = ResourceBundle.getBundle("org.eclipse.wst.html.ui.internal.HTMLUIPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
}
