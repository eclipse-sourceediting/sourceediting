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
package org.eclipse.wst.sse.ui.internal;



import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.registry.embedded.EmbeddedAdapterFactoryRegistryImpl;


public class SSEUIPlugin extends AbstractUIPlugin {

	public final static String ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$

	static SSEUIPlugin instance = null;

	public static SSEUIPlugin getDefault() {
		return instance;
	}

	public synchronized static SSEUIPlugin getInstance() {
		return instance;
	}

	private TextHoverManager fTextHoverManager;

	public SSEUIPlugin() {
		super();
		instance = this;
	}

	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();

	}

	public AdapterFactoryRegistry getEmbeddedAdapterFactoryRegistry() {
		return EmbeddedAdapterFactoryRegistryImpl.getInstance();

	}

	/**
	 * Return text hover manager
	 * 
	 * @return TextHoverManager
	 */
	public TextHoverManager getTextHoverManager() {
		if (fTextHoverManager == null) {
			fTextHoverManager = new TextHoverManager();
		}
		return fTextHoverManager;
	}
}
