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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.internal.provisional.IModelLifecycleListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

/**
 * An IStructuredModel aware Region Processor.
 * Adds ModelLifecycle listener.  ModelLifecycle listener notifies
 * us that some reinitialization needs to take place.
 * 
 * Model aware "process()"
 * Implements a DOM based "contains()" method using IStructuredModel.
 * 
 */
public class StructuredRegionProcessor extends DocumentRegionProcessor implements IModelLifecycleListener {

	protected void process(DirtyRegion dirtyRegion) {
		
		if(getDocument() == null)
			return;
		
		// use structured model to determine area to process
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
		try {
			if(sModel != null) {
				
				int start = dirtyRegion.getOffset();
				IndexedRegion ir = sModel.getIndexedRegion(start);
				
				if(ir != null) {
					
					int end = ir.getEndOffset();
					
					ITypedRegion[] unfiltered = computePartitioning(start, end);
					// remove duplicate typed regions (partitions)
					// that are handled by the same "total scope" strategy
					ITypedRegion[] filtered = filterTotalScopeRegions(unfiltered);
					
					// iterate dirty partitions
					for (int i = 0; i < filtered.length; i++) {
						process(filtered[i]);
					}
				}
			}
		}
		finally {
			if(sModel != null) 
				sModel.releaseFromRead();
		}
	}
	
	private void process(ITypedRegion partition) {
		
		IStructuredDocumentRegion[] sdRegions = ((IStructuredDocument)getDocument()).getStructuredDocumentRegions(partition.getOffset(), partition.getLength());
		Position coverage = getNodeCoverage(sdRegions);
		DirtyRegion nodeDirtyRegion = createDirtyRegion(coverage.offset, coverage.length, DirtyRegion.INSERT);
		// we only run extension point strategy now
		if (getValidatorStrategy() != null)
			getValidatorStrategy().reconcile(partition, nodeDirtyRegion);
	}

	/**
	 * Expands coverage based on DOM.
	 * 
	 * @return node coverage as a position
	 */
	protected Position getNodeCoverage(IStructuredDocumentRegion[] sdRegions) {
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
		return new Position(start, end-start);
	}

	/**
	 * Returns the corresponding node for the StructuredDocumentRegion.
	 * 
	 * @param sdRegion
	 * @return the corresponding node for sdRegion
	 */
	protected IndexedRegion getCorrespondingNode(IStructuredDocumentRegion sdRegion) {
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
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
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.DirtyRegionProcessor#contains(org.eclipse.jface.text.reconciler.DirtyRegion,
	 *      org.eclipse.jface.text.reconciler.DirtyRegion)
	 */
	protected boolean contains(DirtyRegion root, DirtyRegion possible) {

		// this method is a performance hit
		// look for alternatives

		boolean contains = false;
		IStructuredModel sModel = getStructuredModelForRead(getDocument());
		try {
			if(sModel != null) {
				IndexedRegion rootRegion = sModel.getIndexedRegion(root.getOffset());
				IndexedRegion possRegion = sModel.getIndexedRegion(possible.getOffset());
				if (rootRegion != null && possRegion != null) {
					int rootStart = rootRegion.getStartOffset();
					int rootEnd = rootRegion.getEndOffset();
					int possStart = possRegion.getStartOffset();
					int possEnd = possRegion.getEndOffset();
	
					if (rootStart <= possStart && rootEnd >= possEnd)
						contains = true;
	
					if (DEBUG)
						System.out.println("checking if [" + rootStart + ":" + rootEnd + "] contains [" + possStart + ":" + possEnd + "] ... " + contains); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				}
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
		return contains;
	}

	/**
	 * Remember to release model after use!!
	 * 
	 * @return
	 */
	public IStructuredModel getStructuredModelForRead(IDocument doc) {

		IStructuredModel sModel = null;
		if (doc != null)
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
		return sModel;
	}


	/**
	 * @param document
	 */
	private void hookUpModelLifecycleListener(IDocument document) {
		IStructuredModel sModel = getStructuredModelForRead(document);
		try {
			if (sModel != null) {
				sModel.addModelLifecycleListener(this);
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
	}

	/**
	 * @see org.eclipse.wst.sse.core.internal.provisional.IModelLifecycleListener#processPostModelEvent(org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent)
	 */
	public void processPostModelEvent(ModelLifecycleEvent event) {

		// if underlying StructuredDocument changed, need to reconnect it
		// here...
		// ex. file is modified outside the workbench
		if (event.getType() == ModelLifecycleEvent.MODEL_DOCUMENT_CHANGED) {

			// check that it's this model that changed
			IStructuredModel thisModel = getStructuredModelForRead(getDocument());
			try {
				if (thisModel != null && event.getModel().equals(thisModel)) {

					IStructuredDocument sDoc = event.getModel().getStructuredDocument();

					if (DEBUG) {
						System.out.println("======================================================"); //$NON-NLS-1$
						System.out.println("StructuredRegionProcessor: DOCUMENT MODEL CHANGED TO: "); //$NON-NLS-1$
						System.out.println(sDoc.get());
						System.out.println("======================================================"); //$NON-NLS-1$
					}
					setDocument(sDoc);
					// propagate document change
					// setDocumentOnAllStrategies(sDoc);
					// ensure that the document is re-reconciled
					setEntireDocumentDirty(sDoc);
				}
			}
			finally {
				if (thisModel != null)
					thisModel.releaseFromRead();
			}
		}
	}

	/**
	 * @see org.eclipse.wst.sse.core.internal.provisional.IModelLifecycleListener#processPreModelEvent(org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent)
	 */
	public void processPreModelEvent(ModelLifecycleEvent event) {

		if (event.getType() == ModelLifecycleEvent.MODEL_DOCUMENT_CHANGED) {

			getDirtyRegionQueue().clear();
			// note: old annotations are removed via the strategies on
			// AbstractStructuredTextReconcilingStrategy#setDocument(...)
		}
	}

	/**
	 * Reinitializes listeners and sets new document onall strategies.
	 * 
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#reconcilerDocumentChanged(IDocument)
	 */
	protected void reconcilerDocumentChanged(IDocument newDocument) {

		IDocument currentDoc = getDocument();

		// unhook old lifecycle listner
		unhookModelLifecycleListener(currentDoc);
		// add new lifecycle listener
		if (newDocument != null)
			hookUpModelLifecycleListener(newDocument);

		// hooks up new document listener
		// sets document on all strategies
		super.reconcilerDocumentChanged(newDocument);
	}

	/**
	 * @param document
	 */
	private void unhookModelLifecycleListener(IDocument document) {
		IStructuredModel sModel = getStructuredModelForRead(document);
		try {
			if (sModel != null)
				sModel.removeModelLifecycleListener(this);

		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
	}

	public void uninstall() {
		
		// removes model listeners
		unhookModelLifecycleListener(getDocument());
		super.uninstall();
	}
	
	/**
	 * We already know content type from when we created the model, so just use that.
	 */
	protected String getContentType(IDocument doc) {

		String contentTypeId = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
			if (sModel != null) {
				contentTypeId = sModel.getContentTypeIdentifier();
			}
		} finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
		return contentTypeId;
	}
}
