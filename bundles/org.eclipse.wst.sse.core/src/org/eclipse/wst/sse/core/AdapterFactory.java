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



public interface AdapterFactory {

	/**
	 * Method that returns the adapter associated with the given object. It
	 * may be a singleton or not ... depending on the needs of the
	 * INodeAdapter ... but in general it is recommended for an adapter to be
	 * stateless, so the efficiencies of a singleton can be gained.
	 * 
	 * The implementation of this method should call addAdapter on the adapted
	 * object with the correct instance of the adapter.
	 */
	INodeAdapter adapt(INodeNotifier object);

	/**
	 * returns an instance of the adapter factory. Unlike clone, this method
	 * may return the same instance, such as in the case where the
	 * AdapterFactory is intended to be a singleton.
	 */
	public AdapterFactory copy();

	boolean isFactoryForType(Object type);

	/**
	 *  
	 */
	public void release();

}
