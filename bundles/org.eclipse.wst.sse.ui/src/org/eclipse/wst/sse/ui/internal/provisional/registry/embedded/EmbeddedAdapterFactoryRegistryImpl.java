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
package org.eclipse.wst.sse.ui.internal.provisional.registry.embedded;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistry;


public class EmbeddedAdapterFactoryRegistryImpl implements AdapterFactoryRegistry {

	private static AdapterFactoryRegistry instance = null;

	static synchronized public AdapterFactoryRegistry getInstance() {
		if (instance == null) {
			instance = new EmbeddedAdapterFactoryRegistryImpl();
		}
		return instance;
	}

	private HashSet hashSet = null;

	private EmbeddedAdapterFactoryRegistryImpl() {
		super();
		hashSet = new HashSet();
		EmbeddedAdapterFactoryRegistryReader.readRegistry(hashSet);
	}

	void add(EmbeddedAdapterFactoryProvider adapterFactoryProvider) {
		hashSet.add(adapterFactoryProvider);
	}

	public Iterator getAdapterFactories() {
		return hashSet.iterator();
	}
}
