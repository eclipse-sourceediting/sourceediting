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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.IReleasable;
import org.eclipse.wst.sse.ui.ITemporaryAnnotation;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.StructuredTextReconciler;
import org.eclipse.wst.sse.ui.util.Assert;


/**
 * A base ReconcilingStrategy. Subclasses must implement createReconcileSteps().
 * 
 * @author pavery
 */
public abstract class AbstractStructuredTextReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension, IReleasable, IStructuredReconcilingStrategy {

	protected IReconcileStep fFirstStep = null;
	protected IProgressMonitor fProgressMonitor = null;
	protected ITextEditor fTextEditor = null;
	protected IDocument fDocument = null;

	protected boolean fAlreadyRemovedAllThisRun = false;

	/**
	 * Creates a new strategy. The editor parameter is for access to the annotation model.
	 * 
	 * @param editor
	 */
	public AbstractStructuredTextReconcilingStrategy(ITextEditor editor) {
		fTextEditor = editor;
		init();
	}

	/**
	 * This is where you should create the steps for this strategy
	 */
	abstract public void createReconcileSteps();

	public void init() {
		createReconcileSteps();
	}

	/**
	 * Gets partition types from all steps in this strategy.
	 * 
	 * @return parition types from all steps
	 */
	public String[] getPartitionTypes() {
		if (fFirstStep instanceof IStructuredReconcileStep)
			return ((IStructuredReconcileStep) fFirstStep).getPartitionTypes();
		return new String[0];
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion, org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {

		// external files may be null
		if (isCanceled() || fFirstStep == null)
			return;

		reconcile(dirtyRegion, subRegion, false);
	}

	/**
	 * Like IReconcileStep.reconcile(DirtyRegion dirtyRegion, IRegion subRegion) but also aware of the fact that the reconciler is running a processAll() 
	 * operation, and short circuits removal and reconcile calls accordingly.
	 * 
	 * @param dirtyRegion
	 * @param refreshAll
	 * @param subRegion
	 * @see IStructuredReconcilingStrategy#reconcile(DirtyRegion, IRegion, boolean)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion, boolean refreshAll) {

		// external files may be null
		if (isCanceled() || fFirstStep == null)
			return;

		IStructuredDocumentRegion sdRegion = getStructuredDocumentRegion(dirtyRegion.getOffset());
		if (sdRegion == null)
			return;

		TemporaryAnnotation[] annotationsToRemove = new TemporaryAnnotation[0];
		IReconcileResult[] annotationsToAdd = new IReconcileResult[0];
		IStructuredReconcileStep structuredStep = (IStructuredReconcileStep) fFirstStep;
		if (!refreshAll) {
			// regular reconcile
			annotationsToRemove = getAnnotationsToRemove(dirtyRegion);
			annotationsToAdd = structuredStep.reconcile(dirtyRegion, subRegion);
			fAlreadyRemovedAllThisRun = false;
		}
		else {
			// the entire document is being reconciled (strategies may be called multiple times)
			if (!fAlreadyRemovedAllThisRun) {
				annotationsToRemove = getAllAnnotationsToRemove();
				fAlreadyRemovedAllThisRun = true;
			}
			annotationsToAdd = structuredStep.reconcile(dirtyRegion, subRegion, true);
		}
		smartProcess(annotationsToRemove, annotationsToAdd);
	}

	/**
	 * @return
	 */
	protected boolean isCanceled() {
		if (Logger.isTracing(StructuredTextReconciler.TRACE_FILTER) && (fProgressMonitor != null && fProgressMonitor.isCanceled()))
			Logger.trace(StructuredTextReconciler.TRACE_FILTER, "** STRATEGY CANCELED **:" + this.getClass().getName()); //$NON-NLS-1$
		return fProgressMonitor != null && fProgressMonitor.isCanceled();
	}

	/**
	 * pa_TODO make adding/removing smarter... Check if the annotation is already there, if it is, no need to remove or add again.
	 * this will avoid a lot of flickering behavior...
	 * 
	 * @param annotationsToRemove
	 * @param annotationsToAdd
	 */

	protected void smartProcess(TemporaryAnnotation[] annotationsToRemove, IReconcileResult[] annotationsToAdd) {
		// we should be doing a check like this during annotation creation (if possible)
		// might be too much work

		//		TemporaryAnnotation tempRemoval = null;
		//		TemporaryAnnotation tempAddition = null;
		//		for(int i=0; i<annotationsToRemove.length; i++) {
		//			tempRemoval = annotationsToRemove[i];
		//			boolean isInAdditions = false;
		//			for(int j=0; j<annotationsToAdd.length; j++) {
		//				// do i need instance of check here?
		//				tempAddition = (TemporaryAnnotation)annotationsToAdd[j];
		//				if(tempRemoval.equals(tempAddition)) {
		//					isInAdditions = true;
		//					break;
		//				}
		//			}
		//			//isInAdditions >  means we should just ignore this annotation
		//
		//			// add the rest of additions (that weren't in removals)
		//		}

		removeAnnotations(annotationsToRemove);
		process(annotationsToAdd);
	}

	/**
	 * Process the results from the reconcile steps in this strategy.
	 * 
	 * @param results
	 */
	private void process(final IReconcileResult[] results) {
		if (Logger.isTracing(StructuredTextReconciler.TRACE_FILTER))
			Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] > STARTING PROCESS METHOD with (" + results.length + ") results"); //$NON-NLS-1$ //$NON-NLS-2$

		if (results == null)
			return;

		for (int i = 0; i < results.length; i++) {
			if (isCanceled()) {
				Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] >** PROCESS (adding) WAS CANCELLED **"); //$NON-NLS-1$
				return;
			}
			addResultToAnnotationModel(results[i]);
		}
		// tracing --------------------------------------------------------------------
		if (Logger.isTracing(StructuredTextReconciler.TRACE_FILTER)) {
			StringBuffer traceString = new StringBuffer();
			for (int j = 0; j < results.length; j++)
				traceString.append("\n (+) :" + results[j] + ":\n"); //$NON-NLS-1$ //$NON-NLS-2$
			Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] > PROCESSING (" + results.length + ") results in AbstractStructuredTextReconcilingStrategy " + traceString); //$NON-NLS-1$ //$NON-NLS-2$
		}
		//------------------------------------------------------------------------------
	}

	/**
	 * This is where we add results to the annotationModel, doing any special "extra" processing.
	 */
	protected void addResultToAnnotationModel(IReconcileResult result) {
		if (!(result instanceof TemporaryAnnotation))
			return;

		TemporaryAnnotation tempAnnotation = (TemporaryAnnotation) result;
		getAnnotationModel().addAnnotation(tempAnnotation, tempAnnotation.getPosition());
	}

	private void removeAnnotations(TemporaryAnnotation[] annotationsToRemove) {
		IAnnotationModel annotationModel = getAnnotationModel();
		for (int i = 0; i < annotationsToRemove.length; i++) {
			if (isCanceled()) {
				Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] >** REMOVAL WAS CANCELLED **"); //$NON-NLS-1$
				return;
			}
			annotationModel.removeAnnotation(annotationsToRemove[i]);
		}
		// tracing --------------------------------------------------------------------
		if (Logger.isTracing(StructuredTextReconciler.TRACE_FILTER)) {
			StringBuffer traceString = new StringBuffer();
			for (int i = 0; i < annotationsToRemove.length; i++)
				traceString.append("\n (-) :" + annotationsToRemove[i] + ":\n"); //$NON-NLS-1$ //$NON-NLS-2$
			Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] > REMOVED (" + annotationsToRemove.length + ") annotations in AbstractStructuredTextReconcilingStrategy :" + traceString); //$NON-NLS-1$ //$NON-NLS-2$
		}
		//------------------------------------------------------------------------------
	}

	protected TemporaryAnnotation[] getAnnotationsToRemove(DirtyRegion dr) {
		IStructuredDocumentRegion[] sdRegions = getStructuredDocumentRegions(dr);
		List remove = new ArrayList();
		IAnnotationModel annotationModel = getAnnotationModel();
		Iterator i = annotationModel.getAnnotationIterator();
		while (i.hasNext()) {
			Object obj = i.next();
			if (!(obj instanceof TemporaryAnnotation))
				continue;

			TemporaryAnnotation annotation = (TemporaryAnnotation) obj;
			IReconcileAnnotationKey key = (IReconcileAnnotationKey) annotation.getKey();

			// first check if this annotation is still relevant for the current partition
			if (sdRegions.length > 0) {
				if (!partitionsMatch(key, annotation.getPosition().offset, sdRegions[0])) {
					remove.add(annotation);
					continue;
				}
			}

			// then if this strategy knows how to add/remove this partition type
			if (canHandlePartition(key.getPartitionType()) && containsStep(key.getStep())) {
				if (key.getScope() == IReconcileAnnotationKey.PARTIAL && overlaps(annotation.getPosition(), sdRegions)) {
					remove.add(annotation);
				}
				else if (key.getScope() == IReconcileAnnotationKey.TOTAL) {
					remove.add(annotation);
				}
			}
		}
		return (TemporaryAnnotation[]) remove.toArray(new TemporaryAnnotation[remove.size()]);
	}

	/**
	 * Remove ALL temporary annotations that this strategy can handle.
	 */
	protected TemporaryAnnotation[] getAllAnnotationsToRemove() {
		List removals = new ArrayList();
		IAnnotationModel annotationModel = getAnnotationModel();
		Iterator i = annotationModel.getAnnotationIterator();
		while (i.hasNext()) {
			Object obj = i.next();
			if (!(obj instanceof ITemporaryAnnotation))
				continue;

			ITemporaryAnnotation annotation = (ITemporaryAnnotation) obj;
			IReconcileAnnotationKey key = (IReconcileAnnotationKey) annotation.getKey();
			// then if this strategy knows how to add/remove this partition type
			if (canHandlePartition(key.getPartitionType()) && containsStep(key.getStep()))
				removals.add(annotation);
		}
		return (TemporaryAnnotation[]) removals.toArray(new TemporaryAnnotation[removals.size()]);
	}

	/**
	 * Returns the appropriate (first) IStructuredDocumentRegion for the given dirtyRegion.
	 * 
	 * @param dirtyRegion
	 * @return the appropriate StructuredDocumentRegion for the given dirtyRegion.
	 */
	private IStructuredDocumentRegion getStructuredDocumentRegion(int offset) {
		IStructuredDocumentRegion sdRegion = null;
		if (fDocument instanceof IStructuredDocument) {
			sdRegion = ((IStructuredDocument) fDocument).getRegionAtCharacterOffset(offset);
		}
		return sdRegion;
	}

	private IStructuredDocumentRegion[] getStructuredDocumentRegions(DirtyRegion dr) {
		int offset = dr.getOffset();
		int end = offset + dr.getLength();
		List regions = new ArrayList();
		IStructuredDocumentRegion r = getStructuredDocumentRegion(offset);
		while (r != null && r.getStartOffset() <= end) {
			regions.add(r);
			r = r.getNext();
		}
		return (IStructuredDocumentRegion[]) regions.toArray(new IStructuredDocumentRegion[regions.size()]);
	}

	/**
	 * @param partition
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(IRegion partition) {
		// not used, we use - reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	}

	/**
	 * Calls release() on all the steps in this strategy.
	 */
	public void release() {
		// release steps (each step calls release on the next)
		if (fFirstStep != null && fFirstStep instanceof IReleasable)
			((IReleasable) fFirstStep).release();
		fFirstStep = null;
	}

	/**
	 * Set the document for this strategy.
	 * 
	 * @param document
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {
		
		// remove all old annotations since it's a new document
		removeAnnotations(getAllAnnotationsToRemove());
		
		if (document == null)
			release();
		fDocument = document;
		if (fFirstStep != null)
			fFirstStep.setInputModel(new DocumentAdapter(document));
	}

	/**
	 * @param step
	 * @return
	 */
	protected boolean containsStep(IReconcileStep step) {
		if (fFirstStep instanceof IStructuredReconcileStep)
			return ((IStructuredReconcileStep) fFirstStep).isSiblingStep(step);
		return false;
	}

	/**
	 * Checks to make sure that the annotation key (partition type for which it was originally added) matches the current document partition at that offset. This can occur when the character you just typed caused the previous (or subsequent) partition type to change.
	 * 
	 * @param key
	 * @param sdRegion
	 * @return the partition type for this annotation matches the current document partition type
	 */
	private boolean partitionsMatch(IReconcileAnnotationKey key, int annotationPos, IStructuredDocumentRegion sdRegion) {
		String keyPartitionType = key.getPartitionType();
		IDocumentPartitioner p = getPartitioner(sdRegion);
		String partitionType = p.getPartition(annotationPos).getType();
		return keyPartitionType.equals(partitionType);
	}

	/**
	 * pa_TODO - should be temporary until we figure out a way to send in partition with "reconcile()" call
	 * 
	 * @param sdRegion
	 */
	protected IDocumentPartitioner getPartitioner(IStructuredDocumentRegion sdRegion) {
		Assert.isNotNull(fDocument, "document was null when partitioning information was sought"); //$NON-NLS-1$
		IStructuredModel sModel = ((IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID)).getModelManager().getExistingModelForRead(fDocument);
		IDocumentPartitioner partitioner = sModel.getStructuredDocument().getDocumentPartitioner();
		sModel.releaseFromRead();
		return partitioner;
	}

	/**
	 * @param object
	 * @return if this strategy is responisble for adding this type of key
	 */
	protected boolean canHandlePartition(String partition) {
		String[] haystack = getPartitionTypes();
		for (int i = 0; i < haystack.length; i++) {
			if (haystack[i].equals(partition))
				return true;
		}
		return false;
	}

	/**
	 * Checks if this position overlaps any of the StructuredDocument regions' correstponding IndexedRegion.
	 * 
	 * @param pos
	 * @param sdRegions
	 * @return true if the position overlaps any of the regions, otherwise false.
	 */
	protected boolean overlaps(Position pos, IStructuredDocumentRegion[] sdRegions) {
		int start = -1;
		int end = -1;
		for (int i = 0; i < sdRegions.length; i++) {
			IndexedRegion corresponding = getCorrespondingNode(sdRegions[i]);
			if (start == -1 || start > corresponding.getStartOffset())
				start = corresponding.getStartOffset();
			if (end == -1 || end < corresponding.getEndOffset())
				end = corresponding.getEndOffset();
		}
		//System.out.println("checking overlap: [node:" + start + ":" + end + " pos:" + pos.getOffset() + ":" + pos.getLength() + "> " + pos.overlapsWith(start, end - start));
		return pos.overlapsWith(start, end - start);
	}

	/**
	 * Returns the corresponding node for the StructuredDocumentRegion.
	 * 
	 * @param sdRegion
	 * @return the corresponding node for sdRegion
	 */
	protected IndexedRegion getCorrespondingNode(IStructuredDocumentRegion sdRegion) {
		IStructuredModel sModel = ((IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID)).getModelManager().getExistingModelForRead(fDocument);
		IndexedRegion xmlNode = sModel.getIndexedRegion(sdRegion.getStart());
		sModel.releaseFromRead();
		return xmlNode;
	}

	protected IAnnotationModel getAnnotationModel() {
		return fTextEditor.getDocumentProvider().getAnnotationModel(fTextEditor.getEditorInput());
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
	 */
	public void initialReconcile() {
		// do nothing
	}

	/**
	 * @param monitor
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor monitor) {
		fProgressMonitor = monitor;
		if (fFirstStep != null)
			fFirstStep.setProgressMonitor(fProgressMonitor);
	}

	/**
	 * The IFile that this strategy is operating on (the file input for the TextEditor)
	 * 
	 * @return the IFile that this strategy is operating on
	 */
	protected IFile getFile() {
		if (fTextEditor == null)
			return null;
		IEditorInput input = fTextEditor.getEditorInput();
		if (!(input instanceof IFileEditorInput))
			return null;
		return ((IFileEditorInput) input).getFile();
	}

	/**
	 * Resets any specially set for an operation such as processAll() from the reconciler.
	 */
	public void reset() {
		fAlreadyRemovedAllThisRun = false;
		if (fFirstStep instanceof IStructuredReconcileStep)
			((IStructuredReconcileStep) fFirstStep).reset();
	}
}
