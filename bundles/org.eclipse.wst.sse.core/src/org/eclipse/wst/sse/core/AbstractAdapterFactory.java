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
package org.eclipse.wst.sse.core;

/**
 * An abstract implementation of AdapterFactory that uses itself
 * as the key.  Subclass to override behavior.
 */
abstract public class AbstractAdapterFactory implements AdapterFactory {

	protected Object adapterKey;
	protected boolean shouldRegisterAdapter = true;

	public AbstractAdapterFactory() {
		// default constructor sets the adapterKey to the adapter factory itself (i.e. "this")
		adapterKey = this;
	}

	public AbstractAdapterFactory(Object adapterKey, boolean registerAdapters) {
		this.adapterKey = adapterKey;
		this.shouldRegisterAdapter = registerAdapters;
	}

	public INodeAdapter adapt(INodeNotifier target) {
		// target was null when all text deleted?
		if (target == null)
			return null;
		INodeAdapter adapter = target.getExistingAdapter(adapterKey);
		return adapter != null ? adapter : adaptNew(target);
	}

	public INodeAdapter adaptNew(INodeNotifier target) {
		INodeAdapter adapter = createAdapter(target);
		if (adapter == null)
			return adapter;
		if (shouldRegisterAdapter)
			target.addAdapter(adapter);
		return adapter;
	}

	abstract protected INodeAdapter createAdapter(INodeNotifier target);

	public boolean isFactoryForType(Object type) {
		return type.equals(adapterKey);
	}

	public void release() {
		// default is to do nothing
	}

	public void setAdapterKey(Object key) {
		if (adapterKey != null)
			throw new IllegalAccessError("INodeAdapter Key cannot be set more than once."); //$NON-NLS-1$
		adapterKey = key;
	}

	public void setRegisterAdapters(boolean flag) {
		shouldRegisterAdapter = flag;
	}

	/**
	 * Subclasses should normally implement their own 'copy' method.
	 * By default, we'll return the same instance, for convenience
	 * of those using singleton factories.
	 */
	public AdapterFactory copy() {
		return this;
	}
}
