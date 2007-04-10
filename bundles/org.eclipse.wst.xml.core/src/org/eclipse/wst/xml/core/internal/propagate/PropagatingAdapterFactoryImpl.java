/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.propagate;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.PropagatingAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;


/**
 * The PropagatingAdapterFactory is part of the "adapt on create" mechanism. A
 * PropagatingAdapter, once added to a node, will cause proagating adapters to
 * be created for all child nodes. A side effect of creating a
 * PropagatingAdapter for a node is that is is also creates adapters for and
 * adapts the Node for all other registered 'create on adapt' Adapters. This
 * other adapters are registered by registering their factories via plugin
 * extension point.
 */
public class PropagatingAdapterFactoryImpl extends AbstractAdapterFactory implements PropagatingAdapterFactory {

	private PropagatingAdapter fAdapterInstance;
	private List fContributedFactories = null;

	/**
	 * PropagatingAdapterFactory constructor comment.
	 */
	public PropagatingAdapterFactoryImpl() {
		this(PropagatingAdapter.class, true);
	}

	protected PropagatingAdapterFactoryImpl(Object adapterKey, boolean registerAdapters) { // ,
		super(adapterKey, registerAdapters);
	}

	public void addContributedFactories(INodeAdapterFactory factory) {
		if (fContributedFactories != null) {
			fContributedFactories.add(factory);
		}

	}

	/**
	 * createAdapter method comment.
	 */
	protected INodeAdapter createAdapter(INodeNotifier target) {
		// every notifier get's one of these
		// (and the same instance of it)
		return getAdapterInstance();
	}

	/**
	 * Gets the adapterInstance.
	 * 
	 * @return Returns a PropagatingAdapter
	 */
	private PropagatingAdapter getAdapterInstance() {
		if (fAdapterInstance == null) {
			fAdapterInstance = new PropagatingAdapterImpl();
			if (fContributedFactories != null) {
				for (int i = 0; i < fContributedFactories.size(); i++)
					fAdapterInstance.addAdaptOnCreateFactory((PropagatingAdapterFactory) fContributedFactories.get(i));
			}
		}
		return fAdapterInstance;
	}

	public void release() {
		// give the adapter instance a chance to release its factories
		getAdapterInstance().release();

	}

	public void setContributedFactories(ArrayList list) {
		fContributedFactories = list;

	}

	public INodeAdapterFactory copy() {
		PropagatingAdapterFactory clonedInstance = new PropagatingAdapterFactoryImpl(getAdapterKey(), isShouldRegisterAdapter());
		// clone this adapters specific list of adapter factories too
		if (fContributedFactories != null) {
			
			Iterator iterator = fContributedFactories.iterator();
			clonedInstance.setContributedFactories(new ArrayList());
			while (iterator.hasNext()) {
				INodeAdapterFactory existingFactory = (INodeAdapterFactory) iterator.next();
				clonedInstance.addContributedFactories(existingFactory.copy());
			}
		}
		return clonedInstance;
	}

	

}
