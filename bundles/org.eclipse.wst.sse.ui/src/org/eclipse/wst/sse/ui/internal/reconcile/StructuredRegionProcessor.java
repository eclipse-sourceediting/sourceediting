/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.internal.provisional.IModelLifecycleListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

/**
 * An IStructuredModel aware Region Processor. Adds ModelLifecycle listener.
 * ModelLifecycle listener notifies us that some reinitialization needs to
 * take place.
 * 
 * Model aware "process()" Implements a IndexedRegion-based "contains()"
 * method using IStructuredModel.
 * 
 */
public class StructuredRegionProcessor extends DocumentRegionProcessor {

	class ModelLifecycleListener implements IModelLifecycleListener {
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

				flushDirtyRegionQueue();
				// note: old annotations are removed via the strategies on
				// AbstractStructuredTextReconcilingStrategy#setDocument(...)
			}
		}
	}

	private IModelLifecycleListener fLifeCycleListener = new ModelLifecycleListener();

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
			if (sModel != null) {
				IndexedRegion rootRegion = sModel.getIndexedRegion(root.getOffset());
				IndexedRegion rootRegion2 = sModel.getIndexedRegion(root.getOffset() + root.getLength());
				IndexedRegion possRegion = sModel.getIndexedRegion(possible.getOffset());
				IndexedRegion possRegion2 = sModel.getIndexedRegion(possible.getOffset() + possible.getLength());
				if (rootRegion != null && possRegion != null) {
					int rootStart = rootRegion.getStartOffset();
					int rootEnd = rootRegion2 != null ? rootRegion2.getEndOffset() : getDocument().getLength();
					int possStart = possRegion.getStartOffset();
					int possEnd = possRegion2 != null ? possRegion2.getEndOffset() : getDocument().getLength();

					if (rootStart <= possStart && rootEnd >= possEnd) {
						contains = true;
					}

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
	 * We already know content type from when we created the model, so just
	 * use that.
	 */
	protected String getContentType(IDocument doc) {

		String contentTypeId = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
			if (sModel != null) {
				contentTypeId = sModel.getContentTypeIdentifier();
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
		return contentTypeId;
	}


	/**
	 * Remember to release model after use!!
	 * 
	 * @return
	 */
	private IStructuredModel getStructuredModelForRead(IDocument doc) {
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
				sModel.addModelLifecycleListener(fLifeCycleListener);
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
	}

	protected void process(DirtyRegion dirtyRegion) {
		if (getDocument() == null)
			return;

		// use structured model to determine area to process
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
		try {
			if (sModel != null) {
				int start = dirtyRegion.getOffset();
				int end = start + dirtyRegion.getLength();
				IndexedRegion irStart = sModel.getIndexedRegion(start);
				IndexedRegion irEnd = sModel.getIndexedRegion(end);

				if (irStart != null) {
					start = Math.min(start, irStart.getStartOffset());
				}
				if (irEnd != null) {
					end = Math.max(end, irEnd.getEndOffset());
				}
				super.process(createDirtyRegion(start, end - start, DirtyRegion.INSERT));
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
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

		super.reconcilerDocumentChanged(newDocument);
	}

	/**
	 * @param document
	 */
	private void unhookModelLifecycleListener(IDocument document) {
		IStructuredModel sModel = getStructuredModelForRead(document);
		try {
			if (sModel != null)
				sModel.removeModelLifecycleListener(fLifeCycleListener);

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
}
