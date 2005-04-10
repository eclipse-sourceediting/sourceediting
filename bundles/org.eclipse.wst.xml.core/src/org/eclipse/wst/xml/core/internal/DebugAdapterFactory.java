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
package org.eclipse.wst.xml.core.internal;

import java.util.ArrayList;

import org.eclipse.wst.sse.core.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.internal.PropagatingAdapterFactory;


public class DebugAdapterFactory extends AbstractAdapterFactory implements PropagatingAdapterFactory {

	/**
	 * Constructor for PageDirectiveWatcherFactory.
	 */
	public DebugAdapterFactory() {
		this(IDebugAdapter.class, true);
	}

	/**
	 * Constructor for PageDirectiveWatcherFactory.
	 * 
	 * @param adapterKey
	 * @param registerAdapters
	 */
	public DebugAdapterFactory(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.PropagatingAdapterFactory#addContributedFactories(org.eclipse.wst.sse.core.IAdapterFactory)
	 */
	public void addContributedFactories(INodeAdapterFactory factory) {
		//none expected
	}

	public INodeAdapterFactory copy() {
		return new DebugAdapterFactory(this.adapterKey, this.shouldRegisterAdapter);
	}

	protected INodeAdapter createAdapter(INodeNotifier target) {
		EveryNodeDebugAdapter result = null;
		result = EveryNodeDebugAdapter.getInstance();
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.IAdapterFactory#isFactoryForType(java.lang.Object)
	 */
	public boolean isFactoryForType(Object type) {

		return IDebugAdapter.class == type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.PropagatingAdapterFactory#setContributedFactories(java.util.ArrayList)
	 */
	public void setContributedFactories(ArrayList list) {
		// none expected
	}
}
