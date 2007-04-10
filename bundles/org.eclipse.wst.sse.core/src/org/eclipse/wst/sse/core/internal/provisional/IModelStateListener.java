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
 * Interface for those wanting to listen to a model's state changing.
 */
public interface IModelStateListener {

	/**
	 * A model is about to be changed. This typically is initiated by one
	 * client of the model, to signal a large change and/or a change to the
	 * model's ID or base Location. A typical use might be if a client might
	 * want to suspend processing until all changes have been made.
	 */
	void modelAboutToBeChanged(IStructuredModel model);

	/**
	 * Signals that the changes foretold by modelAboutToBeChanged have been
	 * made. A typical use might be to refresh, or to resume processing that
	 * was suspended as a result of modelAboutToBeChanged.
	 */
	void modelChanged(IStructuredModel model);

	/**
	 * Notifies that a model's dirty state has changed, and passes that state
	 * in isDirty. A model becomes dirty when any change is made, and becomes
	 * not-dirty when the model is saved.
	 */
	void modelDirtyStateChanged(IStructuredModel model, boolean isDirty);

	/**
	 * A modelDeleted means the underlying resource has been deleted. The
	 * model itself is not removed from model management until all have
	 * released it. Note: baseLocation is not (necessarily) changed in this
	 * event, but may not be accurate.
	 */
	void modelResourceDeleted(IStructuredModel model);

	/**
	 * A model has been renamed or copied (as in saveAs..). In the renamed
	 * case, the two paramenters are the same instance, and only contain the
	 * new info for id and base location.
	 */
	void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel);

	void modelAboutToBeReinitialized(IStructuredModel structuredModel);

	void modelReinitialized(IStructuredModel structuredModel);


}
