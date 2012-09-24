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
package org.eclipse.wst.sse.core.internal.provisional;

/**
 * An abstract implementation of IAdapterFactory. All implementers of
 * IAdapterFactory should subclass this class. The default constructor uses
 * itself (this) as the key. Subclasses need to provide a way to create the
 * adapter, and can override or call other methods.
 */
abstract public class AbstractAdapterFactory implements INodeAdapterFactory {



	private Object fAdapterKey;

	private boolean fShouldRegisterAdapter;

	/**
	 * If this default constructor used, setAdapterKey and setShouldRegister
	 * should be used to properly initialize.
	 */
	protected AbstractAdapterFactory() {
		super();

	}


	public AbstractAdapterFactory(Object adapterKey) {
		this(adapterKey, true);
	}

	/**
	 * Suclasses may extended this constructor, if needed.
	 */

	public AbstractAdapterFactory(Object adapterKey, boolean registerAdapters) {

		super();
		fAdapterKey = adapterKey;
		fShouldRegisterAdapter = registerAdapters;

	}

	/**
	 * ISSUE: should be final. See those that implement it 
	 * for complicating details and "unknowns". 
	 */
	public INodeAdapter adapt(INodeNotifier target) {
		INodeAdapter adapter = null;
		if (target != null) {
			adapter = target.getExistingAdapter(fAdapterKey);
			if (adapter == null) {
				adapter = adaptNew(target);
			}
		}
		return adapter;
	}

	/**
	 * Subclasses should normally implement their own 'copy' method. By
	 * default, we'll return the same instance, for convenience of those using
	 * singleton factories (which have no state, and so need to do anything on
	 * 'release').
	 * 
	 */
	public INodeAdapterFactory copy() {
		return this;
	}

	/**
	 * This method needs to return true of this factory is for adapters of
	 * type 'type'. It is required that it return true if 'equals' and this
	 * default behavior is provided by this super class. Clients may extend
	 * this behavior if more complex logic is required.
	 */
	public boolean isFactoryForType(Object type) {
		return type.equals(fAdapterKey);
	}

	/**
	 * Subclasses may need to "cleanup" their adapter factory, release
	 * adapters, resources, etc. Subclasses may extend this method if such
	 * clean up is required. Note: while current behavior is to do nothing,
	 * subclasses should not assume this would always be true, so should
	 * always call super.release at the end of their method.
	 */
	public void release() {
		// default for superclass is to do nothing
	}

	/**
	 * Only for special cases there the adapter key can be set in the
	 * constructor. It can be set here. If it is set more than, and
	 */
	final protected void setAdapterKey(Object key) {
		if (fAdapterKey != null && !(fAdapterKey.equals(key)))
			throw new IllegalStateException("INodeAdapter Key cannot be changed."); //$NON-NLS-1$
		fAdapterKey = key;
	}

	/**
	 * Can be called by subclasses during 'adapt' process, but must not be
	 * overridden or reimplemented by subclasses.
	 * 
	 * @param target
	 * @return
	 */
	protected final INodeAdapter adaptNew(INodeNotifier target) {
		INodeAdapter adapter = createAdapter(target);
		if (adapter != null) {
			if (fShouldRegisterAdapter) {
				target.addAdapter(adapter);
			}
		}
		return adapter;
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


	protected final boolean isShouldRegisterAdapter() {
		return fShouldRegisterAdapter;
	}


	protected final void setShouldRegisterAdapter(boolean shouldRegisterAdapter) {
		// ISSUE: technically we probably should not allow this value to
		// change, after initialization, but is not so easy to do,
		// and I'm not sure its "worth" the protection.
		fShouldRegisterAdapter = shouldRegisterAdapter;
	}


	protected final Object getAdapterKey() {
		return fAdapterKey;
	}
}
