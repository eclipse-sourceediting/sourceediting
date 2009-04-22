/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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



import java.util.Collection;

/**
 * INodeNotifiers and INodeAdapters form a collaboration that allows clients
 * to use the typical adapter pattern but with notification added, that is,
 * client's adapters will be notified when the nodeNotifier changes.
 * 
 * @plannedfor 1.0
 */

public interface INodeNotifier {

	/**
	 * The change represents a non-structural change, sent to node notifier's
	 * parent.
	 */
	static final int CHANGE = 1;
	/**
	 * The change represents an add event.
	 */
	static final int ADD = 2;

	/**
	 * The change represents a remove event.
	 */
	static final int REMOVE = 3;

	/**
	 * The change represents a structural change, sent to least-common parent
	 * of node notifiers involved in the structural change
	 */
	static final int STRUCTURE_CHANGED = 4;

	/**
	 * The change represents a notification to parent notifier than its
	 * contents have changed.
	 */
	static final int CONTENT_CHANGED = 5;


	/**
	 * NOT API: these strings are for printing, such as during debugging
	 */
	static final String[] EVENT_TYPE_STRINGS = new String[]{"undefined", "CHANGE", "ADD", "REMOVE", "STRUCTURE_CHANGED", "CONTENT_CHANGED"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$


	/**
	 * Add an adapter of this notifier.
	 * 
	 * @param adapter
	 *            the adapter to be added
	 * 
	 */
	void addAdapter(INodeAdapter adapter);

	/**
	 * Return an exisiting adapter of type "type" or if none found create a
	 * new adapter using a registered adapter factory
	 */
	INodeAdapter getAdapterFor(Object type);

	/**
	 * Return a read-only Collection of the Adapters to this notifier.
	 * 
	 * @return collection of adapters.
	 */
	Collection getAdapters();

	/**
	 * Return an exisiting adapter of type "type" or null if none found
	 */
	INodeAdapter getExistingAdapter(Object type);

	/**
	 * sent to adapter when its nodeNotifier changes.
	 */
	void notify(int eventType, Object changedFeature, Object oldValue, Object newValue, int pos);

	/**
	 * Remove an adapter of this notifier. If the adapter does not exist for
	 * this node notifier, this request is ignored.
	 * 
	 * @param adapter
	 *            the adapter to remove
	 */
	void removeAdapter(INodeAdapter adapter);
}
