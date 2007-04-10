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
 * This interface allows nodes to be adapted.
 * 
 * The main difference between this type of adapter (IAdaptable) and base
 * adapter is that these adapters are notified of changes.
 * 
 * @plannedfor 1.0
 */

public interface INodeAdapter {

	/**
	 * The infrastructure calls this method to determine if the adapter is
	 * appropriate for 'type'. Typically, adapters return true based on
	 * identity comparison to 'type', but this is not required, that is, the
	 * decision can be based on complex logic.
	 * 
	 */
	boolean isAdapterForType(Object type);

	/**
	 * Sent to adapter when notifier changes. Each notifier is responsible for
	 * defining specific eventTypes, feature changed, etc.
	 * 
	 * ISSUE: may be more evolvable if the argument was one big 'notifier
	 * event' instance.
	 */
	void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos);
}
