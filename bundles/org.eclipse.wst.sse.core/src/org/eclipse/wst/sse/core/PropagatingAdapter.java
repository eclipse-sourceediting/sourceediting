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



public interface PropagatingAdapter extends org.eclipse.wst.sse.core.INodeAdapter {
	// dmw: should have getFactoryFor?
	void release();

	void addAdaptOnCreateFactory(AdapterFactory factory);

	List getAdaptOnCreateFactories();

	/**
	 * This method should be called immediately after adding a factory,
	 * typically on the document (top level) node, so all nodes can be 
	 * adapted, if needed. This is needed for those occasions when a
	 * factory is addeded after some nodes may have already been created
	 * at the time the factory is added.
	 */
	void initializeForFactory(AdapterFactory factory, INodeNotifier node);
}
