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
package org.eclipse.wst.sse.ui.internal.reconcile;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;


/**
 * Interface for structured reconciling strategies.
 * 
 * @author pavery
 */
public interface IStructuredReconcilingStrategy extends IReconcilingStrategy {
	/**
	 * Adds awareness that the reconciler is processing the entire document via refreshAll flag.
	 * 
	 * @param dirtyRegion
	 * @param subRegion
	 * @param refreshAll
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion, boolean refreshAll);

	/**
	 * Used to reset the state of the Strategy. For example: any flags that need to be reset after
	 * a long running operation like processAll().
	 */
	public void reset();
}
