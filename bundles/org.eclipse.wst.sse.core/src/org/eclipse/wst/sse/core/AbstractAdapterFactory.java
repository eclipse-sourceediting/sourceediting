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
package org.eclipse.wst.sse.core;

/**
 * An abstract implementation of IAdapterFactory. All implementers of
 * IAdapterFactory should subclass this class. The default constructor uses
 * itself (this) as the key. Subclasses need to provide a way to create the
 * adapter, and can override or call other methods.
 */
abstract public class AbstractAdapterFactory implements IAdapterFactory {

	// ISSUE: make private and use setters/getters
	protected Object adapterKey;
	// ISSUE: make private and use setters/getters
	// ISSUE: when would this ever be false?
	protected boolean shouldRegisterAdapter = true;

	/**
	 * Subclasses may override this behavior is desired.
	 */
	public AbstractAdapterFactory() {
		// default constructor sets the adapterKey to the adapter factory
		// itself (i.e. "this")
		adapterKey = this;
	}

	/**
	 * Suclasses may extended this constructor, if needed.
	 */
	public AbstractAdapterFactory(Object adapterKey, boolean registerAdapters) {
		this.adapterKey = adapterKey;
		this.shouldRegisterAdapter = registerAdapters;
	}

	/**
	 * Not to be overridden by subclasses.
	 */
	public INodeAdapter adapt(INodeNotifier target) {
		// target was null when all text deleted?
		if (target == null)
			return null;
		INodeAdapter adapter = target.getExistingAdapter(adapterKey);
		return adapter != null ? adapter : adaptNew(target);
	}

	/**
	 * Can be called by subclasses during 'adapt' process, but must not be
	 * overridden or reimplemented by subclasses.
	 * 
	 * @param target
	 * @return
	 */
	protected INodeAdapter adaptNew(INodeNotifier target) {
		INodeAdapter adapter = createAdapter(target);
		if (adapter == null)
			return adapter;
		if (shouldRegisterAdapter)
			target.addAdapter(adapter);
		return adapter;
	}

	/**
	 * Subclasses should normally implement their own 'copy' method. By
	 * default, we'll return the same instance, for convenience of those using
	 * singleton factories.
	 */
	public IAdapterFactory copy() {
		return this;
	}

	/**
	 * Subclasses must implement this method. It is called by infrastructure
	 * when an instance is needed. It is provided the node notifier, which may
	 * or may not be relevent when creating the adapter. Note: the adapter
	 * does not have to literally be a new intance and is actually recommended
	 * to typically be a singleton for performance reasons.
	 * 
	 * @param target
	 * @return
	 */
	abstract protected INodeAdapter createAdapter(INodeNotifier target);

	/**
	 * This method needs to return true of this factory is for adapters of
	 * type 'type'. It is required that it return true if 'equals' and this
	 * default behavior is provided by this super class. Clients may extend
	 * this behavior if more complex logic is required.
	 */
	public boolean isFactoryForType(Object type) {
		return type.equals(adapterKey);
	}

	/**
	 * Subclasses may need to "cleanup" their adapter factory, release
	 * adapters, resources, etc. Subclasses may extend this method if such
	 * clean up is required. Note: while current behavior is to do nothing,
	 * subclasses should not assume this would always be true, so should
	 * always call super.release at the end of their method.
	 */
	public void release() {
		// default for now is to do nothing
	}

	/**
	 * @deprecated is there a reason for this?
	 * @param key
	 */
	public void setAdapterKey(Object key) {
		if (adapterKey != null)
			throw new IllegalAccessError("INodeAdapter Key cannot be set more than once."); //$NON-NLS-1$
		adapterKey = key;
	}

	/**
	 * @deprecated is there a reason for this?
	 * @param key
	 */
	public void setRegisterAdapters(boolean flag) {
		shouldRegisterAdapter = flag;
	}
}
