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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.ui.IReleasable;


/**
 * This interface is for reconcile steps that need to "work" off of 
 * StructuredDocument events, as well as the ReconcilerThread.
 */
public interface IReconcileStepAdapter extends INodeAdapter, IReleasable {

	/**
	 * Marks a node for reconciling.
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

	/**
	 * Partition types for which this step can add annootations.
	 * 
	 * @return partition types for which this step can add annootations.
	 */
	String[] getPartitionTypes();
}
