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



import java.util.List;

public interface IFactoryRegistry {

	void addFactory(AdapterFactory factory);

	/** 
	 * This method is a not a pure resistry. Factories retrieved based on their response
	 * to "isFactoryForType(type)". Note that if there is more than one factory that can
	 * answer 'true' that the most recently added factory is used.
	 */
	AdapterFactory getFactoryFor(Object type);

	/**
	 *
	 */
	void release();

	/**
	 * Removes a factory if it can be retrieved by getFactoryFor(type). If there
	 * is more than one, all are removed. If there is none, the call simply returns
	 * (that is, it is not considered an error).
	 */
	void removeFactoriesFor(Object type);

	void removeFactory(AdapterFactory factory);

	/**
	 * Returns a shallow cloned list of the factories 
	 * in the registry.
	 */
	List getFactories();

	/**
	 * returns true if already contains a factory for the 
	 * given type. This is purely a convenience method for 
	 * those few cases that want to avoid adding a factory 
	 * if it is already in the registry.
	 */
	boolean contains(Object type);

	void clearFactories();
}
