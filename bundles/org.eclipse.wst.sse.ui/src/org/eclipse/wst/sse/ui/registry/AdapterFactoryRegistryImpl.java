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
package org.eclipse.wst.sse.ui.registry;

import java.util.HashSet;
import java.util.Iterator;

public class AdapterFactoryRegistryImpl implements AdapterFactoryRegistry {

	private static AdapterFactoryRegistry instance = null;
	private HashSet hashSet = null;

	private AdapterFactoryRegistryImpl() {
		super();
		hashSet = new HashSet();
		AdapterFactoryRegistryReader.readRegistry(hashSet);
	}

	static synchronized public AdapterFactoryRegistry getInstance() {
		if (instance == null) {
			instance = new AdapterFactoryRegistryImpl();
		}
		return instance;
	}

	public Iterator getAdapterFactories() {
		return hashSet.iterator();
	}

	void add(AdapterFactoryProvider adapterFactoryProvider) {
		hashSet.add(adapterFactoryProvider);
	}
}
