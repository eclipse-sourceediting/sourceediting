/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal;

import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;

public interface PropagatingAdapter extends INodeAdapter {

	void addAdaptOnCreateFactory(INodeAdapterFactory factory);

	List getAdaptOnCreateFactories();

	/**
	 * This method should be called immediately after adding a factory,
	 * typically on the document (top level) node, so all nodes can be
	 * adapted, if needed. This is needed for those occasions when a factory
	 * is addeded after some nodes may have already been created at the time
	 * the factory is added.
	 */
	void initializeForFactory(INodeAdapterFactory factory, INodeNotifier node);

	// dmw: should have getFactoryFor?
	void release();
}
