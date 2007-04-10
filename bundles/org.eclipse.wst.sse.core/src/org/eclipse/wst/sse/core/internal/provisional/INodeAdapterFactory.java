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
 * INodeNotifiers can be adapted by INodeAdapters. This factory interface
 * provides a way to provide factories which are invoked by the infrastructure
 * to manage this process, from creating, to adapting, to releasing, if
 * required.
 * 
 * @plannedfor 1.0
 * 
 */
public interface INodeAdapterFactory {

	/**
	 * The primary purpose of an adapter factory is to create an adapter and
	 * associate it with an INodeNotifier. This adapt method Method that
	 * returns the adapter associated with the given object. The
	 * implementation of this method should call addAdapter on the adapted
	 * object with the correct instance of the adapter, if appropriate.
	 * 
	 * Note: the instance of the adapter returned may be a singleton or not
	 * ... depending on the needs of the INodeAdapter ... but in general it is
	 * recommended for an adapter to be stateless, so the efficiencies of a
	 * singleton can be gained.
	 * 
	 * @param object
	 *            the node notifier to be adapted
	 */
	INodeAdapter adapt(INodeNotifier object);

	/**
	 * Unlike clone, this method may or may not return the same instance, such
	 * as in the case where the IAdapterFactory is intended to be a singleton.
	 * 
	 * @return an instance of this adapter factory.
	 */
	public INodeAdapterFactory copy();

	/**
	 * isFactoryForType is called by infrastructure to decide if this adapter
	 * factory is apporiate to use for an adapter request that specifies
	 * 'type'.
	 * 
	 * @param type -
	 *            same object used to identify/request adapters.
	 * @return true if factory is appropriate for type, false otherwise.
	 */
	boolean isFactoryForType(Object type);

	/**
	 * release is called by infrastructure when the factory registry is
	 * released (which is done when a structured model is released). This
	 * intened for the factory to be allowed to clean up any state information
	 * it may have.
	 * 
	 * Note: while not recommended, due to performance reasons, if individual
	 * adapters need some cleanup (or need to be released) it is (typically)
	 * the responsibility of the adapter factory to track them, and initiate
	 * what ever clean up is needed. In other works, cleanup at the adatper
	 * level is not provided by infrastructure.
	 */
	public void release();

}
