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



import java.util.List;

public interface IFactoryRegistry {

	void addFactory(AdapterFactory factory);

	void clearFactories();

	/**
	 * returns true if already contains a factory for the given type. This is
	 * purely a convenience method for those few cases that want to avoid
	 * adding a factory if it is already in the registry.
	 */
	boolean contains(Object type);

	/**
	 * Returns a shallow cloned list of the factories in the registry.
	 */
	List getFactories();

	/**
	 * This method is a not a pure resistry. Factories retrieved based on
	 * their response to "isFactoryForType(type)". Note that if there is more
	 * than one factory that can answer 'true' that the most recently added
	 * factory is used.
	 */
	AdapterFactory getFactoryFor(Object type);

	/**
	 *  
	 */
	void release();

	/**
	 * Removes a factory if it can be retrieved by getFactoryFor(type). If
	 * there is more than one, all are removed. If there is none, the call
	 * simply returns (that is, it is not considered an error).
	 */
	void removeFactoriesFor(Object type);

	void removeFactory(AdapterFactory factory);
}
