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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.AbstractReconcileStep;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilableModel;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.ui.IReleasable;


/**
 * ReconcileStep that knows about the annotation that it adds to the AnnotationModel.
 * It knows how to create an annotation key (for smart removal later)
 * It knows the partition types on which it can operate.
 * It knows the scope on which it operates (for short circuiting)
 * It knows if the Reconciler is reconciling the entire document.
 * 
 * Clients must subclass this class.
 * 
 * @author pavery
 */
public abstract class StructuredReconcileStep extends AbstractReconcileStep implements IStructuredReconcileStep, IReleasable {

	protected final IReconcileResult[] EMPTY_RECONCILE_RESULT_SET = new IReconcileResult[0];
	private IStructuredReconcileStep fNextStructuredStep = null;
	private HashSet fPartitionTypes = null;
	private IModelManager fModelManager = null;

	// these limits are safetys for "runaway" validation cases
	// should be used to safeguard potentially dangerous loops or potentially long annotations 
	// (since the painter seems to affect performance when painting long annotations)
	public static final int ELEMENT_ERROR_LIMIT = 100;
	public static final int ANNOTATION_LENGTH_LIMIT = 100;

	/**
	 * Flag so that TOTAL scope steps are only called once during a batch reconcile.
	 * reset() should be called after the batch reconcile.
	 */
	private boolean fAlreadyRanGlobalReconcile = false;
	/** 
	 * It's possible for a partial step to get called on the same area twice (as w/ a full document reconcile)
	 * this list keeps track of area already covered.  Should be reset() after the "batch" of reconciling is finished.
	 * */
	private List fPartialRangesCovered = null;

	public abstract int getScope();

	public StructuredReconcileStep() {
		super();
		fPartitionTypes = new HashSet();
		fPartialRangesCovered = new ArrayList();
	}

	public StructuredReconcileStep(IReconcileStep step) {
		super(step);
		if (step instanceof IStructuredReconcileStep)
			fNextStructuredStep = (IStructuredReconcileStep) step;

		fPartitionTypes = new HashSet();
		fPartialRangesCovered = new ArrayList();
	}

	/**
	 * Like IReconcileStep.reconcile() except takes into consideration if the strategy may be called
	 * multiple times in this same "run" (ie. a processAll() call from the StructuredTextReconciler)
	 */
	public final IReconcileResult[] reconcile(DirtyRegion dirtyRegion, IRegion subRegion, boolean refreshAll) {
		IReconcileResult[] result = EMPTY_RECONCILE_RESULT_SET;

		if (!refreshAll) {
			result = reconcileModel(dirtyRegion, subRegion);
			fAlreadyRanGlobalReconcile = false;
		}
		else if (getScope() == IReconcileAnnotationKey.TOTAL && !fAlreadyRanGlobalReconcile) {
			result = reconcileModel(dirtyRegion, subRegion);
			fAlreadyRanGlobalReconcile = true;
		}
		else if (getScope() == IReconcileAnnotationKey.PARTIAL) {
			if (!isInPartiallyCheckedRanges(dirtyRegion)) {
				result = reconcileModel(dirtyRegion, subRegion);
			}
		}

		if (!isLastStep()) {
			((IReconcileStep) fNextStructuredStep).setInputModel(getModel());
			IReconcileResult[] nextResult = fNextStructuredStep.reconcile(dirtyRegion, subRegion, refreshAll);
			return merge(result, convertToInputModel(nextResult));
		}
		return result;
	}

	/**
	 * @param dirtyRegion
	 * @return
	 */
	private boolean isInPartiallyCheckedRanges(DirtyRegion dirtyRegion) {
		// pa_TODO reconciler performance, this can be bad
		Iterator it = fPartialRangesCovered.iterator();
		Position p = null;
		while (it.hasNext()) {
			p = (Position) it.next();
			if (p.overlapsWith(dirtyRegion.getOffset(), dirtyRegion.getLength()))
				return true;
		}

		// add new range that has been covered
		IStructuredModel sm = getModelManager().getExistingModelForRead(getDocument());
		IndexedRegion indexed = sm.getIndexedRegion(dirtyRegion.getOffset());
		sm.releaseFromRead();
		if (indexed != null)
			fPartialRangesCovered.add(new Position(indexed.getStartOffset(), indexed.getEndOffset() - indexed.getStartOffset()));
		return false;
	}

	/**
	 * Removes duplicates.
	 * @param results1
	 * @param results2
	 * @return
	 */
	protected IReconcileResult[] merge(IReconcileResult[] results1, IReconcileResult[] results2) {
		if (results1 == null)
			return results2;
		if (results2 == null)
			return results1;

		List results = new ArrayList();
		results.addAll(Arrays.asList(results1));
		for (int i = 0; i < results2.length; i++) {
			// pa_TODO: could be bad for performance
			if (!results.contains(results2[i]))
				results.add(results2[i]);
		}

		return (IReconcileResult[]) results.toArray(new IReconcileResult[results.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.AbstractReconcileStep#reconcileModel(org.eclipse.jface.text.reconciler.DirtyRegion, org.eclipse.jface.text.IRegion)
	 */
	protected IReconcileResult[] reconcileModel(DirtyRegion dirtyRegion, IRegion subRegion) {
		return EMPTY_RECONCILE_RESULT_SET;
	}

	/*
	 * @see org.eclipse.text.reconcilerpipe.AbstractReconcilePipeParticipant#getModel()
	 */
	public IReconcilableModel getModel() {
		return getInputModel();
	}

	protected IDocument getDocument() {
		IDocument doc = null;
		IReconcilableModel rModel = getModel();
		if (rModel instanceof DocumentAdapter) {
			doc = ((DocumentAdapter) rModel).getDocument();
		}
		return doc;
	}

	protected IStructuredDocument getStructuredDocument() {
		IStructuredDocument sDoc = null;
		IDocument doc = getDocument();
		if (doc instanceof IStructuredDocument)
			sDoc = (IStructuredDocument) getDocument();
		return sDoc;
	}

	/**
	 * Avoid excessive calls to Platform.getPlugin(ModelPlugin.ID)
	 * @return sse model manager
	 */
	protected IModelManager getModelManager() {
		if (fModelManager == null)
			fModelManager = ((IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID)).getModelManager();
		return fModelManager;
	}

	protected IDocumentPartitioner getPartitioner() {
		return getDocument().getDocumentPartitioner();
	}

	public String getPartitionType(int offset) {
		ITypedRegion tr = getPartitioner().getPartition(offset);
		return (tr != null) ? tr.getType() : StructuredTextPartitioner.ST_UNKNOWN_PARTITION;
	}

	public IReconcileAnnotationKey createKey(IStructuredDocumentRegion sdRegion, int scope) {
		ITypedRegion tr = sdRegion.getParentDocument().getDocumentPartitioner().getPartition(sdRegion.getStartOffset());
		String partitionType = (tr != null) ? tr.getType() : StructuredTextPartitioner.ST_UNKNOWN_PARTITION;
		return createKey(partitionType, scope);
	}

	/**
	 * Clients should use this method to create annotation keys as it registers the key for removal later.
	 * 
	 * @param partitionType
	 * @param scope
	 * @return
	 */
	public IReconcileAnnotationKey createKey(String partitionType, int scope) {
		fPartitionTypes.add(partitionType);
		return new ReconcileAnnotationKey(this, partitionType, scope);
	}

	public String[] getPartitionTypes() {
		// using hash set to automatically get rid of dupes
		HashSet tempResults = new HashSet();
		// add these partition types
		tempResults.addAll(fPartitionTypes);
		// add next step's partition types
		if (fNextStructuredStep != null) {
			String[] nextResults = fNextStructuredStep.getPartitionTypes();
			for (int i = 0; i < nextResults.length; i++)
				tempResults.add(nextResults[i]);
		}
		return (String[]) tempResults.toArray(new String[tempResults.size()]);
	}

	/**
	 * Release resources used by the step here as needed.
	 * Be sure to call super.release() when you override this method as to propagate the release through all steps.
	 */
	public void release() {
		if (fNextStructuredStep != null && fNextStructuredStep instanceof IReleasable)
			((IReleasable) fNextStructuredStep).release();
		fNextStructuredStep = null;
		fModelManager = null;
	}

	public void reset() {
		fAlreadyRanGlobalReconcile = false;
		fPartialRangesCovered.clear();

		if (!isLastStep())
			fNextStructuredStep.reset();
	}

	/**
	 * If step passed in is found somewhere in the chain of steps.
	 * 
	 * @return true if step passed in is found somewhere in the chain of steps, else false
	 */
	public boolean isSiblingStep(IReconcileStep step) {
		if (step == null)
			return false;
		else if (step.equals(this))
			return true;
		else if (isLastStep())
			return false;
		else
			return fNextStructuredStep.isSiblingStep(step);
	}
}
