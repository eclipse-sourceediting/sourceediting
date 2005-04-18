/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.reconcile;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.IReleasable;


/**
 * This interface is for reconcile steps that need to "work" off of
 * StructuredDocument events, as well as the ReconcilerThread.
 * 
 * @deprecated don't need this now that content model strategy will go away
 */
public interface IReconcileStepAdapter extends INodeAdapter, IReleasable {

	/**
	 * Partition types for which this step can add annootations.
	 * 
	 * @return partition types for which this step can add annootations.
	 */
	String[] getPartitionTypes();

	/**
	 * Marks a node for reconciling.
	 * 
	 * @param o
	 */
	void markForReconciling(Object o);

	/**
	 * Reconcile call seeded with an indexedNode.
	 * 
	 * @param monitor
	 * @param xmlNode
	 * @return
	 */
	IReconcileResult[] reconcile(IProgressMonitor monitor, IndexedRegion indexedNode);
}
