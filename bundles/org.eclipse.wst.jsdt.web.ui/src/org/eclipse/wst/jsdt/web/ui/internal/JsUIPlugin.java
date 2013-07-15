/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal;

import java.io.IOException;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.jsdt.internal.ui.JavaScriptPlugin;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistryImpl;
import org.osgi.framework.BundleContext;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JsUIPlugin extends AbstractUIPlugin {
	public final static String ID = "org.eclipse.wst.jsdt.web.ui"; //$NON-NLS-1$
	protected static JsUIPlugin instance = null;
	
	public static JsUIPlugin getDefault() {
		return JsUIPlugin.instance;
	}
	
	public synchronized static JsUIPlugin getInstance() {
		return JsUIPlugin.instance;
	}
	/**
	 * The template context type registry for the jsp editor.
	 */
	private ContextTypeRegistry fContextTypeRegistry;
	/**
	 * The template store for the jsp editor.
	 */
	private TemplateStore fTemplateStore;
	
	public JsUIPlugin() {
		super();
		JsUIPlugin.instance = this;
	}
	
	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();
	}
	
	/**
	 * Returns the template context type registry for the jsp plugin.
	 * 
	 * @return the template context type registry for the jsp plugin
	 */
	public ContextTypeRegistry getTemplateContextRegistry() {
		if (fContextTypeRegistry == null) {
// ContributionContextTypeRegistry registry = new
// ContributionContextTypeRegistry();
// registry.addContextType(TemplateContextTypeIdsJSP.ALL);
// registry.addContextType(TemplateContextTypeIdsJSP.NEW);
// registry.addContextType(TemplateContextTypeIdsJSP.TAG);
// registry.addContextType(TemplateContextTypeIdsJSP.ATTRIBUTE);
// registry.addContextType(TemplateContextTypeIdsJSP.ATTRIBUTE_VALUE);
			fContextTypeRegistry = JavaScriptPlugin.getDefault().getCodeTemplateContextRegistry();
		}
		return fContextTypeRegistry;
	}
	
	/**
	 * Returns the template store for the jsp editor templates.
	 * 
	 * @return the template store for the jsp editor templates
	 */
	public TemplateStore getTemplateStore() {
		if (fTemplateStore == null) {
// fTemplateStore = new ContributionTemplateStore(
// getTemplateContextRegistry(), getPreferenceStore(),
// JSPUIPreferenceNames.TEMPLATES_KEY);
			JavaScriptPlugin jp = JavaScriptPlugin.getDefault();
			fTemplateStore = jp.getTemplateStore();
			try {
				fTemplateStore.load();
			} catch (IOException e) {
				Logger.logException(e);
			}
		}
		return fTemplateStore;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		instance = this;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		instance = null;
	}
}
