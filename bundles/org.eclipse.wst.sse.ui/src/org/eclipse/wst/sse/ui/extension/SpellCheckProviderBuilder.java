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
package org.eclipse.wst.sse.ui.extension;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckProvider;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.extension.RegistryReader;
import org.osgi.framework.Bundle;

/**
 * @deprecated - to be removed in M4
 */

public class SpellCheckProviderBuilder extends RegistryReader {

	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static SpellCheckProviderBuilder instance;
	private static final String PL_SPELLCHECK = "spellcheck"; //$NON-NLS-1$

	private static final String PLUGIN_ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$
	private static final String TAG_PROVIDER = "provider"; //$NON-NLS-1$

	private static final String TAG_SPELLCHECK_CONTRIBUTION = "spellcheckContribution"; //$NON-NLS-1$

	/*
	 * Creates an executable extension. @param element the config element
	 * defining the extension @param classAttribute the name of the attribute
	 * carrying the class @return the extension object @throws CoreException
	 */
	static Object createExecutableExtension(final IConfigurationElement element, final String classAttribute) throws CoreException {
		return element.createExecutableExtension(classAttribute);
	}

	/**
	 * Creates an extension. If the extension plugin has not been loaded a
	 * busy cursor will be activated during the duration of the load.
	 * 
	 * @param element
	 *            the config element defining the extension
	 * @param classAttribute
	 *            the name of the attribute carrying the class
	 * @return the extension object
	 * @throws CoreException
	 */
	public static Object createExtension(final IConfigurationElement element, final String classAttribute) {
		// If plugin has been loaded create extension.
		// Otherwise, show busy cursor then create extension.
		final Object[] result = new Object[1];
		
		String pluginId = element.getDeclaringExtension().getNamespace();
		Bundle bundle = Platform.getBundle(pluginId);
		if (bundle.getState() == Bundle.ACTIVE) {
			try {
				return createExecutableExtension(element, classAttribute);
			} catch (Exception e) {
				handleCreateExecutableException(result, e);
			}
		} else {
			BusyIndicator.showWhile(null, new Runnable() {
				public void run() {
					try {
						result[0] = createExecutableExtension(element, classAttribute);
					} catch (Exception e) {
						handleCreateExecutableException(result, e);
					}
				}
			});
		}
		return result[0];
	}

	/**
	 * returns singleton instance of SpellCheckProviderBuilder
	 * 
	 * @return SpellCheckProviderBuilder
	 */
	public synchronized static SpellCheckProviderBuilder getInstance() {
		if (instance == null) {
			instance = new SpellCheckProviderBuilder();
		}
		return instance;
	}

	/**
	 * @param result
	 * @param e
	 */
	private static void handleCreateExecutableException(Object[] result, Throwable e) {
		Logger.logException(e);
		result[0] = null;

	}

	protected List cache;
	protected SpellCheckProvider[] providers = null;

	protected String targetContributionTag;

	//private static final String ATT_ID = "id"; //$NON-NLS-1$

	/*
	 * Constructor
	 */
	private SpellCheckProviderBuilder() {
	}

	/*
	 * Creates a breakpoint provider object to given element @param element
	 * configuration element object @return SpellCheckProvider
	 */
	protected SpellCheckProvider createSpellCheckProvider(IConfigurationElement element) {
		Object obj = null;
		obj = createExtension(element, ATT_CLASS);
		if (obj == null)
			return null;
		return (obj instanceof SpellCheckProvider) ? (SpellCheckProvider) obj : null;
	}

	/*
	 * Creates an array of breakpoint providers @return SpellCheckProvider[]
	 */
	protected SpellCheckProvider[] createSpellCheckProviders() {
		if (cache == null)
			return new SpellCheckProvider[0];

		final int num = cache.size();
		if (num == 0)
			return new SpellCheckProvider[0];

		SpellCheckProvider[] bp = new SpellCheckProvider[num];
		int j = 0;
		for (int i = 0; i < num; i++) {
			Object obj = cache.get(i);
			if (!(obj instanceof IConfigurationElement))
				continue;

			IConfigurationElement element = (IConfigurationElement) obj;
			if (!TAG_PROVIDER.equals(element.getName())) {
				continue;
			}

			SpellCheckProvider b = createSpellCheckProvider(element);
			if (b != null) {
				//				b.setSourceEditingTextTools(new
				// SourceEditingTextToolsImpl());
				bp[j] = b;
				j++;
			}
		}

		SpellCheckProvider[] bp2 = new SpellCheckProvider[j];

		for (int i = 0; i < j; i++) {
			bp2[i] = bp[i];
		}

		return bp2;
	}

	/**
	 * Returns an array of spellcheck providers for a specified content type
	 * handler
	 * 
	 * @param handler
	 *            a content type handler
	 * @param ext
	 *            file extension
	 * @return SpellCheckProvider[]
	 */
	public SpellCheckProvider[] getSpellCheckProviders() {
		if (cache == null) {
			readContributions(TAG_SPELLCHECK_CONTRIBUTION, PL_SPELLCHECK);
		}

		if (providers == null) {
			providers = createSpellCheckProviders();
		}

		return providers;
	}

	/**
	 * Returns an array of breakpoint providers
	 * 
	 * @return boolean
	 */
	public boolean isAvailable() {
		return getSpellCheckProviders().length != 0 ? true : false;
	}

	/**
	 * Reads the contributions from the registry for the provided workbench
	 * part and the provided extension point ID.
	 * 
	 * @param tag
	 * @param extensionPoint
	 */
	protected void readContributions(String tag, String extensionPoint) {
		cache = null;
		targetContributionTag = tag;
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		readRegistry(registry, PLUGIN_ID, extensionPoint);
	}

	/*
	 * @see com.ibm.sed.edit.internal.extension.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	protected boolean readElement(IConfigurationElement element) {
		String tag = element.getName();
		if (tag.equals(targetContributionTag)) {
			readElementChildren(element);
			return true;
		} else if (tag.equals(TAG_PROVIDER)) {
			if (cache == null)
				cache = new ArrayList();
			cache.add(element);
			return true; // just cache the element - don't go into it
		}
		return false;
	}
}
