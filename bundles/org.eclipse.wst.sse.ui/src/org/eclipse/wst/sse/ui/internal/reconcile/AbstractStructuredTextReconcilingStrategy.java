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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
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
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.IReleasable;
import org.eclipse.wst.sse.ui.ITemporaryAnnotation;


/**
 * A base ReconcilingStrategy. Subclasses must implement
 * createReconcileSteps().
 * 
 * @author pavery
 */
public abstract class AbstractStructuredTextReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension, IReleasable {

    /** debug flag */
    protected static final boolean DEBUG;
    static {
        String value = Platform.getDebugOption("org.eclipse.wst.sse.ui/debug/reconcilerjob"); //$NON-NLS-1$
        DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
    }
    
	protected IDocument fDocument = null;
	protected IReconcileStep fFirstStep = null;
	protected IProgressMonitor fProgressMonitor = null;
	protected ITextEditor fTextEditor = null;
    private Comparator fComparator;

	/**
	 * Creates a new strategy. The editor parameter is for access to the
	 * annotation model.
	 * 
	 * @param editor
	 */
	public AbstractStructuredTextReconcilingStrategy(ITextEditor editor) {
		fTextEditor = editor;
		init();
	}

	/**
	 * This is where we add results to the annotationModel, doing any special
	 * "extra" processing.
	 */
	protected void addResultToAnnotationModel(IReconcileResult result) {
		if (!(result instanceof TemporaryAnnotation))
			return;
		// can be null when closing the editor
		if (getAnnotationModel() != null) {
			TemporaryAnnotation tempAnnotation = (TemporaryAnnotation) result;
			getAnnotationModel().addAnnotation(tempAnnotation, tempAnnotation.getPosition());
		}
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
	 * @param step
	 * @return
	 */
	protected boolean containsStep(IReconcileStep step) {
		if (fFirstStep instanceof StructuredReconcileStep)
			return ((StructuredReconcileStep) fFirstStep).isSiblingStep(step);
		return false;
	}

	/**
	 * This is where you should create the steps for this strategy
	 */
	abstract public void createReconcileSteps();

	/**
	 * Remove ALL temporary annotations that this strategy can handle.
	 */
	protected TemporaryAnnotation[] getAllAnnotationsToRemove() {
		List removals = new ArrayList();
		IAnnotationModel annotationModel = getAnnotationModel();
		if (annotationModel != null) {
			Iterator i = annotationModel.getAnnotationIterator();
			while (i.hasNext()) {
				Object obj = i.next();
				if (!(obj instanceof ITemporaryAnnotation))
					continue;

				ITemporaryAnnotation annotation = (ITemporaryAnnotation) obj;
				IReconcileAnnotationKey key = (IReconcileAnnotationKey) annotation.getKey();
				// then if this strategy knows how to add/remove this
				// partition type
				if (canHandlePartition(key.getPartitionType()) && containsStep(key.getStep()))
					removals.add(annotation);
			}
		}
		return (TemporaryAnnotation[]) removals.toArray(new TemporaryAnnotation[removals.size()]);
	}

	protected IAnnotationModel getAnnotationModel() {
		IAnnotationModel model = null;
		if (fTextEditor != null && fTextEditor.getEditorInput() != null) {
			model = fTextEditor.getDocumentProvider().getAnnotationModel(fTextEditor.getEditorInput());
		}
		return model;
	}

	protected TemporaryAnnotation[] getAnnotationsToRemove(DirtyRegion dr) {
		IStructuredDocumentRegion[] sdRegions = getStructuredDocumentRegions(dr);
		List remove = new ArrayList();
		IAnnotationModel annotationModel = getAnnotationModel();
		// can be null when closing the editor
		if (getAnnotationModel() != null) {
			Iterator i = annotationModel.getAnnotationIterator();
			while (i.hasNext()) {
				Object obj = i.next();
				if (!(obj instanceof TemporaryAnnotation))
					continue;

				TemporaryAnnotation annotation = (TemporaryAnnotation) obj;
				IReconcileAnnotationKey key = (IReconcileAnnotationKey) annotation.getKey();
				
				// then if this strategy knows how to add/remove this
				// partition type
				if (canHandlePartition(key.getPartitionType()) && containsStep(key.getStep())) {
					if (key.getScope() == IReconcileAnnotationKey.PARTIAL && overlaps(annotation.getPosition(), sdRegions)) {
						remove.add(annotation);
					} else if (key.getScope() == IReconcileAnnotationKey.TOTAL) {
						remove.add(annotation);
					}
				}
			}
		}
		return (TemporaryAnnotation[]) remove.toArray(new TemporaryAnnotation[remove.size()]);
	}

	/**
	 * Returns the corresponding node for the StructuredDocumentRegion.
	 * 
	 * @param sdRegion
	 * @return the corresponding node for sdRegion
	 */
	protected IndexedRegion getCorrespondingNode(IStructuredDocumentRegion sdRegion) {
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
        IndexedRegion indexedRegion = null;
        try {
            if (sModel != null) 
                indexedRegion = sModel.getIndexedRegion(sdRegion.getStart());    
        } finally {
            if (sModel != null)
                sModel.releaseFromRead();
        }
        return indexedRegion;
    }

	/**
	 * The IFile that this strategy is operating on (the file input for the
	 * TextEditor)
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
	 * Gets partition types from all steps in this strategy.
	 * 
	 * @return parition types from all steps
	 */
	public String[] getPartitionTypes() {
		if (fFirstStep instanceof StructuredReconcileStep)
			return ((StructuredReconcileStep) fFirstStep).getPartitionTypes();
		return new String[0];
	}

	/**
	 * Returns the appropriate (first) IStructuredDocumentRegion for the given
	 * dirtyRegion.
	 * 
	 * @param dirtyRegion
	 * @return the appropriate StructuredDocumentRegion for the given
	 *         dirtyRegion.
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
			if (!r.isDeleted())
				regions.add(r);
			r = r.getNext();
		}
		return (IStructuredDocumentRegion[]) regions.toArray(new IStructuredDocumentRegion[regions.size()]);
	}

	public void init() {
		createReconcileSteps();
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
	 */
	public void initialReconcile() {
		// do nothing
	}

	/**
	 * @return
	 */
	protected boolean isCanceled() {
		if (DEBUG && (fProgressMonitor != null && fProgressMonitor.isCanceled()))
			System.out.println("** STRATEGY CANCELED **:" + this.getClass().getName()); //$NON-NLS-1$
		return fProgressMonitor != null && fProgressMonitor.isCanceled();
	}

	/**
	 * Checks if this position overlaps any of the StructuredDocument regions'
	 * correstponding IndexedRegion.
	 * 
	 * @param pos
	 * @param sdRegions
	 * @return true if the position overlaps any of the regions, otherwise
	 *         false.
	 */
	protected boolean overlaps(Position pos, IStructuredDocumentRegion[] sdRegions) {
		int start = -1;
		int end = -1;
		for (int i = 0; i < sdRegions.length; i++) {
		    if(!sdRegions[i].isDeleted()) {
    			IndexedRegion corresponding = getCorrespondingNode(sdRegions[i]);
                if(corresponding != null) {
        			if (start == -1 || start > corresponding.getStartOffset())
        				start = corresponding.getStartOffset();
        			if (end == -1 || end < corresponding.getEndOffset())
        				end = corresponding.getEndOffset();
                }
            }
		}
		return pos.overlapsWith(start, end - start);
	}

	/**
	 * Process the results from the reconcile steps in this strategy.
	 * 
	 * @param results
	 */
	private void process(final IReconcileResult[] results) {
		if (DEBUG)
			System.out.println("[trace reconciler] > STARTING PROCESS METHOD with (" + results.length + ") results"); //$NON-NLS-1$ //$NON-NLS-2$

		if (results == null)
			return;

		for (int i = 0; i < results.length; i++) {
			if (isCanceled()) {
                if(DEBUG)
				    System.out.println("[trace reconciler] >** PROCESS (adding) WAS CANCELLED **"); //$NON-NLS-1$
				return;
			}
			addResultToAnnotationModel(results[i]);
		}
        
		if (DEBUG) {
			StringBuffer traceString = new StringBuffer();
			for (int j = 0; j < results.length; j++)
				traceString.append("\n (+) :" + results[j] + ":\n"); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println("[trace reconciler] > PROCESSING (" + results.length + ") results in AbstractStructuredTextReconcilingStrategy " + traceString); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,
	 *      org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {

		// external files may be null
		if (isCanceled() || fFirstStep == null)
			return;

        TemporaryAnnotation[] annotationsToRemove = new TemporaryAnnotation[0];
        IReconcileResult[] annotationsToAdd = new IReconcileResult[0];
        StructuredReconcileStep structuredStep = (StructuredReconcileStep) fFirstStep;
        
        annotationsToRemove = getAnnotationsToRemove(dirtyRegion);
        annotationsToAdd = structuredStep.reconcile(dirtyRegion, subRegion);
        
        smartProcess(annotationsToRemove, annotationsToAdd);
	}
    
	/**
	 * @param partition
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(IRegion partition) {
		// not used, we use:
        // reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	}

	/**
	 * Calls release() on all the steps in this strategy. Currently done in
	 * StructuredRegionProcessor.SourceWidgetDisposeListener#widgetDisposed(...)
	 */
	public void release() {
		// release steps (each step calls release on the next)
		if (fFirstStep != null && fFirstStep instanceof IReleasable)
			((IReleasable) fFirstStep).release();
		// we don't to null out the steps, in case
		// it's reconfigured later
	}

	private void removeAnnotations(TemporaryAnnotation[] annotationsToRemove) {
		IAnnotationModel annotationModel = getAnnotationModel();
		// can be null when closing the editor
		if (annotationModel != null) {
			for (int i = 0; i < annotationsToRemove.length; i++) {
				if (isCanceled()) {
                    if(DEBUG)
					    System.out.println("[trace reconciler] >** REMOVAL WAS CANCELLED **"); //$NON-NLS-1$
					return;
				}
				annotationModel.removeAnnotation(annotationsToRemove[i]);
			}
		}
        
		if (DEBUG) {
			StringBuffer traceString = new StringBuffer();
			for (int i = 0; i < annotationsToRemove.length; i++)
				traceString.append("\n (-) :" + annotationsToRemove[i] + ":\n"); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.println("[trace reconciler] > REMOVED (" + annotationsToRemove.length + ") annotations in AbstractStructuredTextReconcilingStrategy :" + traceString); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

    private void removeAllAnnotations() {
        removeAnnotations(getAllAnnotationsToRemove());
    }

	/**
	 * Set the document for this strategy.
	 * 
	 * @param document
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {

		// remove all old annotations since it's a new document
	    removeAllAnnotations();

		if (document == null)
			release();

		fDocument = document;
		if (fFirstStep != null)
			fFirstStep.setInputModel(new DocumentAdapter(document));
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
	 * Check if the annotation is already there, if it is, no need to 
     * remove or add again. this will avoid a lot of flickering behavior...
	 * 
	 * @param annotationsToRemove
	 * @param annotationsToAdd
	 */
	protected void smartProcess(TemporaryAnnotation[] annotationsToRemove, IReconcileResult[] annotationsToAdd) {
        
        // TODO: investigate a better algorithm, 
        // also see if this is a bad performance hit.
        
        Comparator comp = getAnnotationComparator();
        List sortedRemovals = Arrays.asList(annotationsToRemove);
	    Collections.sort(sortedRemovals, comp);
        
        List sortedAdditions = Arrays.asList(annotationsToAdd);
        Collections.sort(sortedAdditions, comp);
        
        List filteredRemovals = new ArrayList(sortedRemovals);
        List filteredAdditions = new ArrayList(sortedAdditions);
        
        boolean ignore = false;
        int lastFoundAdded = 0;
        for(int i=0; i<sortedRemovals.size(); i++) {
            TemporaryAnnotation removal = (TemporaryAnnotation)sortedRemovals.get(i);       
            for(int j=lastFoundAdded; j<sortedAdditions.size(); j++) {             
                TemporaryAnnotation addition = (TemporaryAnnotation)sortedAdditions.get(j);
                // quick position check here
                if(removal.getPosition().getOffset() == addition.getPosition().getOffset()) {
                    lastFoundAdded = j;
                    // remove performs TemporaryAnnotation.equals()
                    // which checks text as well
                    filteredAdditions.remove(addition);
                    ignore = true;
                    if(DEBUG)
                        System.out.println(" ~ smart process ignoring: " + removal.getPosition().getOffset());
                    break;
                }
            }
            if(ignore) {
                filteredRemovals.remove(removal);
            }
            ignore = false;
        }
        
        removeAnnotations((TemporaryAnnotation[])filteredRemovals.toArray(new TemporaryAnnotation[filteredRemovals.size()]));
        process((IReconcileResult[])filteredAdditions.toArray(new IReconcileResult[filteredAdditions.size()]));
	}
    
    private Comparator getAnnotationComparator() {
        if(fComparator == null) {
            fComparator = new Comparator( ) {
                public int compare(Object arg0, Object arg1) {
                    TemporaryAnnotation ta1 = (TemporaryAnnotation)arg0;
                    TemporaryAnnotation ta2 = (TemporaryAnnotation)arg1;
                    return ta1.getPosition().getOffset() - ta2.getPosition().getOffset();
                }
            };
        }
        return fComparator;
    }
}
