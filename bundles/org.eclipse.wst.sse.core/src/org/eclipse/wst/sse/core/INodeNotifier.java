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



import java.util.Collection;

public interface INodeNotifier {

	// Possible values for eventType
	static final int CHANGE = 1; // update - non structural
	static final int ADD = 2;
	static final int REMOVE = 3;
	static final int STRUCTURE_CHANGED = 4; // sent in addition to adds and removed
	// when large changes are made to a sub-tree
	static final int CONTENT_CHANGED = 5; // sent to the parent notifier
	// these strings are for printing, such as during debuging
	static final String[] EVENT_TYPE_STRINGS = new String[]{"undefined", "CHANGE", "ADD", "REMOVE", "STRUCUTRED_CHANGED", "CONTENT_CHANGED"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	// when the child value changed

	/**
	 * Add an adapter of this notifier.
	 */
	void addAdapter(INodeAdapter o);

	/**
	 * Return an exisiting adapter of type "type" or if none found create
	 * a new adapter using a registered adapter factory
	 */
	INodeAdapter getAdapterFor(Object type);

	/**
	 * Return a read-only Collection of the Adapters to this notifier.
	 */
	Collection getAdapters();

	/**
	 * Return an exisiting adapter of type "type" or null if none found
	 */
	INodeAdapter getExistingAdapter(Object type);

	/**
	 */
	void notify(int eventType, Object changedFeature, Object oldValue, Object newValue, int pos);

	/**
	 * Remove an adapter of this notifier.
	 */
	void removeAdapter(INodeAdapter o);
}
