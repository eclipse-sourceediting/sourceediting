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
package org.eclipse.wst.sse.core.internal.provisional.model;


/**
 * Interface for those wanting to listen to a model's state changing.
 * 
 * @plannedfor 1.0
 */
public interface IModelStateListenerProposed {

	/**
	 * A model is about to be changed. The event object itself signifies which
	 * model, and any pertinent information.
	 */
	void modelAboutToBeChanged(IStructuredModelEvent event);

	/**
	 * Signals that the changes foretold by modelAboutToBeChanged have been
	 * made. A typical use might be to refresh, or to resume processing that
	 * was suspended as a result of modelAboutToBeChanged.
	 */
	void modelChanged(IStructuredModelEvent event);

}
