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
package org.eclipse.wst.xml.ui.reconcile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.StructuredTextReconciler;
import org.eclipse.wst.sse.ui.internal.reconcile.IReconcileAnnotationKey;
import org.eclipse.wst.sse.ui.internal.reconcile.IReconcileStepAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.IStructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.ReconcileAnnotationKey;


/**
 * This reconcile step is an adapter on the IStructuredModel. Validation is
 * triggered from StructuredDocumentEvents
 * 
 * @author pavery
 */
public class AbstractReconcileStepAdapter implements IReconcileStepAdapter {

	protected static final IReconcileResult[] EMPTY_RECONCILE_RESULT_SET = new IReconcileResult[0];
	protected List fDirtyElements = new ArrayList();
	private IStructuredReconcileStep fParentStep = null;
	private HashSet fPartitionTypes = null;

	public AbstractReconcileStepAdapter() {
		super();
		fPartitionTypes = new HashSet();
	}

	public IReconcileAnnotationKey createKey(IStructuredDocumentRegion sdRegion, int scope) {
		ITypedRegion tr = null;
		if (!sdRegion.isDeleted()) {
			try {
				tr = sdRegion.getParentDocument().getPartition(sdRegion.getStartOffset());
			} catch (BadLocationException e) {
				// do nothing but leave tr as null
				// probably due to changes made on another thread
			}
		}
		String partitionType = (tr != null) ? tr.getType() : StructuredTextPartitioner.ST_UNKNOWN_PARTITION;
		return createKey(partitionType, scope);
	}

	public IReconcileAnnotationKey createKey(String partitionType, int scope) {
		fPartitionTypes.add(partitionType);
		return new ReconcileAnnotationKey(getParentStep(), partitionType, scope);
	}

	public IStructuredReconcileStep getParentStep() {
		return fParentStep;
	}

	public String[] getPartitionTypes() {
		String[] results = new String[fPartitionTypes.size()];
		System.arraycopy(fPartitionTypes.toArray(), 0, results, 0, results.length);
		return results;
	}

	/**
	 * @see com.ibm.sed.model.INodeAdapter#isAdapterForType(java.lang.Object)
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
		synchronized (fDirtyElements) {
			// pa_TODO possible bottleneck
			if (!fDirtyElements.contains(notifier)) {
				Logger.trace(StructuredTextReconciler.TRACE_FILTER, "marking :" + notifier + ": for reconciling"); //$NON-NLS-1$ //$NON-NLS-2$
				fDirtyElements.add(notifier);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.model.INodeAdapter#notifyChanged(com.ibm.sed.model.INodeNotifier,
	 *      int, java.lang.Object, java.lang.Object, java.lang.Object, int)
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		// Adapters are notified before the JFace Document events are sent to
		// the IReconciler
		markForReconciling(notifier);
	}

	public IReconcileResult[] reconcile(IProgressMonitor monitor, IndexedRegion xmlNode) {
		List workingElements = null;
		List results = new ArrayList();

		IReconcileResult[] temp = EMPTY_RECONCILE_RESULT_SET;
		synchronized (fDirtyElements) {
			if (fDirtyElements != null && fDirtyElements.size() > 0) {
				workingElements = new ArrayList();
				workingElements.addAll(0, fDirtyElements);
				fDirtyElements = new ArrayList();
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

	/**
	 * @see org.eclipse.wst.sse.ui.IReleasable#release()
	 */
	public void release() {
		// nothing to release
	}

	public void setParentStep(IStructuredReconcileStep parentStep) {
		fParentStep = parentStep;
	}
}
