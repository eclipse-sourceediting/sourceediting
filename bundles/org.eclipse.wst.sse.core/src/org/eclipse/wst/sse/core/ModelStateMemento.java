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



import org.eclipse.core.resources.IResource;

/**
 * Used my AbstractStructuredModel to keep track of what state it should be in, when
 * restoreState(memento) is called. See also getMemento.
 */
class ModelStateMemento implements IStateMemento {

	/** the resource used in 'getMemento'. Eventually (V2) will be part of the Model itself */
	private IResource underlyingResource;
	/** datesInSynch records whether or not the resource modification date, and synchronization date
	 are the same when getMemento is called. If so, they should be forced to be the same in restoreState */
	private boolean datesInSync;
	/** dirty State when 'getMemento' called. restoreState should set it back to this value. */
	private boolean dirtyState;

	/**
	 * ModelStateMemento constructor comment.
	 */
	ModelStateMemento() {
		super();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/7/2001 1:15:06 PM)
	 * @return org.eclipse.core.resources.IResource
	 */
	org.eclipse.core.resources.IResource getUnderlyingResource() {
		return underlyingResource;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/7/2001 2:18:03 PM)
	 * @return boolean
	 */
	boolean isDatesInSync() {
		return datesInSync;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/7/2001 1:15:06 PM)
	 * @return boolean
	 */
	boolean isDirtyState() {
		return dirtyState;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/7/2001 2:18:03 PM)
	 * @param newDatesInSync boolean
	 */
	void setDatesInSync(boolean newDatesInSync) {
		datesInSync = newDatesInSync;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/7/2001 1:15:06 PM)
	 * @param newDirtyState boolean
	 */
	void setDirtyState(boolean newDirtyState) {
		dirtyState = newDirtyState;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/7/2001 1:15:06 PM)
	 * @param newUnderlyingResource org.eclipse.core.resources.IResource
	 */
	void setUnderlyingResource(org.eclipse.core.resources.IResource newUnderlyingResource) {
		underlyingResource = newUnderlyingResource;
	}
}
