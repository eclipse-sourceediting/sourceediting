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
package org.eclipse.wst.sse.core.internal.model;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;


/**
 * This class simply maintains the list of factories and returns singleton
 * instances of them. Some "built in" types are automatically created form
 * FactoryConfig, if not found registerd, but normally clients can/should
 * register their own factories.
 * 
 * Not intended for clients to subclass or instantiate.
 * 
 */
public final class FactoryRegistry {

	private List factories;

	/**
	 * intentionally default access
	 */
	FactoryRegistry() {
		super();

	}

	private List _getFactories() {

		if (factories == null) {
			// may need to use java.util.Collections.synchronizedList() if
			// syncronization becomes
			// necessary (and if so, remember to synchronize on factories)
			factories = new ArrayList();
		}
		return factories;

	}

	public void addFactory(INodeAdapterFactory factory) {
		_getFactories().add(factory);
	}

	public void clearFactories() {
		factories.clear();
	}

	/*
	 * @see FactoryRegistry#contains(Object)
	 */
	public boolean contains(Object type) {
		boolean result = false;
		// note: we're not using cloned list, so strictly speaking
		// is not thread safe.
		List internalList = _getFactories();
		for (int i = 0; i < internalList.size(); i++) {
			INodeAdapterFactory factory = (INodeAdapterFactory) internalList.get(i);
			if (factory.isFactoryForType(type)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Returns a shallow copy of the list of factories in the registry. Note:
	 * this can not be used to add/remove factories. Its primarily provided
	 * for those few cases where a list of factories must be copied from one
	 * model and added to another.
	 */
	public List getFactories() {
		// note: for object integrity, we don't let anyone get
		// our main list (so they have to add through addFactory),
		// but we will return a shallow "cloned" list.
		List factoryList = new ArrayList(_getFactories());
		return factoryList;
	}

	/**
	 * This method is a not a pure resistry. Factories retrieved based on
	 * their response to "isFactoryForType(type)". Note that if there is more
	 * than one factory that can answer 'true' that the most recently added
	 * factory is used.
	 */
	public INodeAdapterFactory getFactoryFor(Object type) {

		INodeAdapterFactory result = null;
		if (factories == null)
			return null;
		int listSize = factories.size();
		for (int i = listSize - 1; i >= 0; i--) {
			// It is the adapter factories responsibility to answer
			// isFactoryForType so it gets choosen.
			// Notice we are going through the list backwards to get the
			// factory added last.
			INodeAdapterFactory a = (INodeAdapterFactory) factories.get(i);
			if (a.isFactoryForType(type)) {
				result = a;
				break;
			}
		}
		return result;

	}

	/**
	 * 
	 */
	public void release() {
		// modified to work on copy of list, for V5PTF1
		// send release to local copy of list
		// of factories, since some factories, during
		// their release function, may remove
		// themselves from the registry.
		List localList = getFactories();
		for (int i = 0; i < localList.size(); i++) {
			INodeAdapterFactory a = (INodeAdapterFactory) localList.get(i);
			// To help bullet proof code, we'll catch and log
			// any messages thrown by factories during release,
			// but we'll attempt to keep going.
			// In nearly all cases, though, such errors are
			// severe for product/client, and need to be fixed.
			try {
				a.release();
			}
			catch (Exception e) {
				Logger.logException("Program problem releasing factory" + a, e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Removes a factory if it can be retrieved by getFactoryFor(type). If
	 * there is more than one, all are removed. If there is none, the call
	 * simply returns (that is, it is not considered an error).
	 */
	public void removeFactoriesFor(java.lang.Object type) {
		if (factories != null) {
			int listSize = factories.size();
			// we'll go backwards through list, since we're removing, so
			// 'size' change won't matter.
			// Note: I'm assuming other items in the collection do not change
			// position
			// simply because another was removed.
			for (int i = listSize - 1; i >= 0; i--) {
				// It is the adapter factories responsibility to answer
				// isFactoryForType so it gets choosen.
				INodeAdapterFactory a = (INodeAdapterFactory) factories.get(i);
				if (a.isFactoryForType(type)) {
					factories.remove(a);
				}
			}
		}
	}

	public void removeFactory(INodeAdapterFactory factory) {
		_getFactories().remove(factory);

	}

}
