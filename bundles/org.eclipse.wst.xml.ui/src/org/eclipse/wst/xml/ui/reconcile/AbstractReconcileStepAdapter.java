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
package org.eclipse.wst.xml.ui.reconcile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.ui.StructuredTextReconciler;
import org.eclipse.wst.sse.ui.internal.reconcile.IReconcileAnnotationKey;
import org.eclipse.wst.sse.ui.internal.reconcile.IReconcileStepAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.IStructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.ReconcileAnnotationKey;
import org.eclipse.wst.xml.ui.Logger;


/**
 * This reconcile step is an adapter on the IStructuredModel.
 * Validation is triggered from StructuredDocumentEvents
 * 
 * @author pavery
 */
public class AbstractReconcileStepAdapter implements IReconcileStepAdapter {

	protected static final IReconcileResult[] EMPTY_RECONCILE_RESULT_SET = new IReconcileResult[0];
	protected List dirtyElements = new ArrayList();
	private HashSet fPartitionTypes = null;
	private IStructuredReconcileStep fParentStep = null;

	public AbstractReconcileStepAdapter() {
		super();
		fPartitionTypes = new HashSet();
	}

	/**
	 */
	public boolean isAdapterForType(Object type) {
		return type == IReconcileStepAdapter.class;
	}

	protected boolean isCanceled(IProgressMonitor monitor) {
		return monitor != null && monitor.isCanceled();
	}

	/**
	 * @param notifier
	 */
	public void markForReconciling(Object notifier) {
		synchronized (dirtyElements) {
			// pa_TODO possible bottleneck
			if (!dirtyElements.contains(notifier)) {
				Logger.trace(StructuredTextReconciler.TRACE_FILTER, "marking :" + notifier + ": for reconciling"); //$NON-NLS-1$ //$NON-NLS-2$
				dirtyElements.add(notifier);
			}
		}
	}

	/* (non-Javadoc)
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		// Adapters are notified before the JFace Document events are sent to the IReconciler
		markForReconciling(notifier);
	}

	public IReconcileResult[] reconcile(IProgressMonitor monitor, IndexedRegion xmlNode) {
		List workingElements = null;
		List results = new ArrayList();

		IReconcileResult[] temp = EMPTY_RECONCILE_RESULT_SET;
		synchronized (dirtyElements) {
			if (dirtyElements != null && dirtyElements.size() > 0) {
				workingElements = new ArrayList();
				workingElements.addAll(0, dirtyElements);
				dirtyElements = new ArrayList();
			}
		}
		if (workingElements != null) {
			Iterator elements = workingElements.iterator();
			while (elements.hasNext() && !isCanceled(monitor)) {
				temp = reconcile(elements.next(), monitor);
				for (int i = 0; i < temp.length; i++)
					results.add(temp[i]);
			}
		}
		temp = new IReconcileResult[results.size()];
		System.arraycopy(results.toArray(), 0, temp, 0, results.size());
		return temp;
	}

	protected IReconcileResult[] reconcile(Object o, IProgressMonitor monitor) {
		return EMPTY_RECONCILE_RESULT_SET;
	}

	public IReconcileAnnotationKey createKey(IStructuredDocumentRegion sdRegion, int scope) {
		ITypedRegion tr = sdRegion.getParentDocument().getDocumentPartitioner().getPartition(sdRegion.getStartOffset());
		String partitionType = (tr != null) ? tr.getType() : StructuredTextPartitioner.ST_UNKNOWN_PARTITION;
		return createKey(partitionType, scope);
	}

	public IReconcileAnnotationKey createKey(String partitionType, int scope) {
		fPartitionTypes.add(partitionType);
		return new ReconcileAnnotationKey(getParentStep(), partitionType, scope);
	}

	public String[] getPartitionTypes() {
		String[] results = new String[fPartitionTypes.size()];
		System.arraycopy(fPartitionTypes.toArray(), 0, results, 0, results.length);
		return results;
	}

	/**
	 */
	public void release() {
		// nothing to release
	}

	public void setParentStep(IStructuredReconcileStep parentStep) {
		fParentStep = parentStep;
	}

	public IStructuredReconcileStep getParentStep() {
		return fParentStep;
	}
}
