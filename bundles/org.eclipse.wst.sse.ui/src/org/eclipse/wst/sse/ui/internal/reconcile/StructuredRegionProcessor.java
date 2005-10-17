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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.internal.provisional.IModelLifecycleListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorBuilder;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorMetaData;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorStrategy;

/**
 * Adds IDocument and (Structured) ModelLifecycle listeners. Adds Text viewer
 * (dispose, input changed) listeners.
 * 
 * Implements a smarter "contains" method.
 * 
 * Adds default and validator strategies. Adds DirtyRegion processing logic.
 */
public class StructuredRegionProcessor extends DirtyRegionProcessor implements IDocumentListener, IModelLifecycleListener {

	/**
	 * Reconclies the entire document when the document in the viewer is
	 * changed. This happens when the document is initially opened, as well as
	 * after a save-as.
	 * 
	 * Also see processPostModelEvent(...) for similar behavior when document
	 * for the model is changed.
	 */
	private class SourceTextInputListener implements ITextInputListener {

		public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
			// do nothing
		}

		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			handleInputDocumentChanged(oldInput, newInput);
		}
	}

	/** for initital reconcile when document is opened */
	private SourceTextInputListener fTextInputListener = null;


	/** strategy called for unmapped partitions */
	private IReconcilingStrategy fDefaultStrategy;

	/**
	 * The strategy that runs validators contributed via
	 * <code>org.eclipse.wst.sse.ui.extensions.sourcevalidation</code>
	 * extension point
	 */
	private ValidatorStrategy fValidatorStrategy;
	
	private final String SSE_EDITOR_ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$


	private String[] fLastPartitions;

	/**
	 * @return Returns the fDefaultStrategy.
	 */
	public IReconcilingStrategy getDefaultStrategy() {
		return fDefaultStrategy;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.DirtyRegionProcessor#getAppropriateStrategy(org.eclipse.jface.text.reconciler.DirtyRegion)
	 */
	protected IReconcilingStrategy getStrategy(DirtyRegion dirtyRegion) {
		IReconcilingStrategy strategy = super.getStrategy(dirtyRegion);
		if (strategy == null)
			strategy = getDefaultStrategy();
		return strategy;
	}

	/**
	 * @return Returns the fValidatorStrategy.
	 */
	public ValidatorStrategy getValidatorStrategy() {
		if (fValidatorStrategy == null) {
			ValidatorStrategy validatorStrategy = null;
			
			if (getTextViewer() instanceof ISourceViewer) {
				ISourceViewer viewer = (ISourceViewer)getTextViewer();
				String contentTypeId = null;
				
				IDocument doc = viewer.getDocument();
				IStructuredModel sModel = null;
				try {
					sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
					if (sModel != null) {
						contentTypeId = sModel.getContentTypeIdentifier();
					}
//					else {
//						// some editors may be model-less (like JS)
//						contentTypeId = getContentType(doc);
//					}
				} finally {
					if (sModel != null)
						sModel.releaseFromRead();
				}
				
				if (contentTypeId != null) {
					validatorStrategy = new ValidatorStrategy(viewer, contentTypeId);
				
					ValidatorBuilder vBuilder = new ValidatorBuilder();
					ValidatorMetaData[] vmds = vBuilder.getValidatorMetaData(SSE_EDITOR_ID);
					for (int i = 0; i < vmds.length; i++) {
						if (vmds[i].canHandleContentType(contentTypeId))
							validatorStrategy.addValidatorMetaData(vmds[i]);
					}
				}
			}
			fValidatorStrategy = validatorStrategy;
		}
		return fValidatorStrategy;
	}

//	private String getContentType(IDocument doc) {
//		
//		if(doc == null)
//			return null;
//		
//		String contentTypeId = null;
//		// no model, like w/ JS editor
//		try {
//			IContentDescription desc = Platform.getContentTypeManager().getDescriptionFor(new DocumentInputStream(doc), null, IContentDescription.ALL);
//			if(desc != null) {
//				IContentType ct  = desc.getContentType();
//				if(ct != null) {
//					contentTypeId = ct.getId();
//				}
//			}
//		}
//		catch (IOException e) {
//			// just bail
//		}
//		return contentTypeId;
//	}

	/**
	 * @param dirtyRegion
	 */
	protected void process(DirtyRegion dirtyRegion) {
		if (!isInstalled())
			return;

		ITypedRegion[] unfiltered = computePartitioning(dirtyRegion);

		// remove duplicate typed regions
		// that are handled by the same "total scope" strategy
		ITypedRegion[] filtered = filterTotalScopeRegions(unfiltered);

		IReconcilingStrategy s;
		DirtyRegion dirty = null;
		for (int i = 0; i < filtered.length; i++) {

			dirty = createDirtyRegion(filtered[i], DirtyRegion.INSERT);
			s = getReconcilingStrategy(filtered[i].getType());
			if (s != null && dirty != null) {
				s.reconcile(dirty, dirty);
			}

			// validator for this partition
			if (fValidatorStrategy != null)
				fValidatorStrategy.reconcile(filtered[i], dirty);
		}
	}

	/**
	 * Removes multiple "total-scope" regions (and leaves one) for a each
	 * partitionType. This improves performance by preventing unnecessary full
	 * document validations.
	 * 
	 * @param unfiltered
	 * @return
	 */
	private ITypedRegion[] filterTotalScopeRegions(ITypedRegion[] unfiltered) {
		IReconcilingStrategy s = null;
		// ensure there is only one typed region in the list
		// for regions handled by "total scope" strategies
		HashMap totalScopeRegions = new HashMap();
		HashMap partialScopeRegions = new HashMap();
		List allRegions = new ArrayList();
		for (int i = 0; i < unfiltered.length; i++) {

			String partitionType = unfiltered[i].getType();

			// short circuit loop
			if (totalScopeRegions.containsKey(partitionType) || partialScopeRegions.containsKey(partitionType))
				continue;

			s = getReconcilingStrategy(partitionType);

			// might be the validator strategy
			if (s == null) {
				ValidatorStrategy validatorStrategy = getValidatorStrategy();
				if (validatorStrategy != null) {
					if (validatorStrategy.canValidatePartition(partitionType))
						s = validatorStrategy;
				}
			}

			if (s instanceof AbstractStructuredTextReconcilingStrategy) {
				// only allow one dirty region for a strategy
				// that has "total scope"
				if (((AbstractStructuredTextReconcilingStrategy) s).isTotalScope())
					totalScopeRegions.put(partitionType, unfiltered[i]);
				else
					partialScopeRegions.put(partitionType, unfiltered[i]);
			}
			else
				partialScopeRegions.put(partitionType, unfiltered[i]);
		}
		allRegions.addAll(totalScopeRegions.values());
		allRegions.addAll(partialScopeRegions.values());
		ITypedRegion[] filtered = (ITypedRegion[]) allRegions.toArray(new ITypedRegion[allRegions.size()]);

		if (DEBUG)
			System.out.println("filtered out this many 'total-scope' regions: " + (unfiltered.length - filtered.length)); //$NON-NLS-1$

		return filtered;
	}

	/**
	 * @param defaultStrategy
	 *            The fDefaultStrategy to set.
	 */
	public void setDefaultStrategy(IReconcilingStrategy defaultStrategy) {
		fDefaultStrategy = defaultStrategy;
		if (fDefaultStrategy != null) {
			fDefaultStrategy.setDocument(getDocument());
			if (fDefaultStrategy instanceof IReconcilingStrategyExtension)
				((IReconcilingStrategyExtension) fDefaultStrategy).setProgressMonitor(getLocalProgressMonitor());
		}
	}

	/**
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.DirtyRegionProcessor#setDocumentOnAllStrategies(org.eclipse.jface.text.IDocument)
	 */
	protected void setDocumentOnAllStrategies(IDocument document) {

		super.setDocumentOnAllStrategies(document);

		IReconcilingStrategy defaultStrategy = getDefaultStrategy();
		IReconcilingStrategy validatorStrategy = getValidatorStrategy();

		// default strategies
		if (defaultStrategy != null)
			defaultStrategy.setDocument(document);

		// external validator strategy
		if (validatorStrategy != null)
			validatorStrategy.setDocument(document);
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
	 * 
	 * @param oldInput
	 * @param newInput
	 */
	public void handleInputDocumentChanged(IDocument oldInput, IDocument newInput) {
		// don't bother if reconciler not installed
		if (isInstalled()) {

			reconcilerDocumentChanged(newInput);

			setDocument(newInput);
			setDocumentOnAllStrategies(newInput);
			setEntireDocumentDirty(newInput);
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.DirtyRegionProcessor#install(org.eclipse.jface.text.ITextViewer)
	 */
	public void install(ITextViewer textViewer) {

		super.install(textViewer);
		fTextInputListener = new SourceTextInputListener();
		textViewer.addTextInputListener(fTextInputListener);
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
					setDocumentOnAllStrategies(sDoc);
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

		// unhook old document listener
		if (currentDoc != null)
			currentDoc.removeDocumentListener(this);
		// hook up new document listener
		if (newDocument != null)
			newDocument.addDocumentListener(this);

		// sets document on all strategies
		super.reconcilerDocumentChanged(newDocument);
	}

	protected void setEntireDocumentDirty(IDocument document) {

		// make the entire document dirty
		// this also happens on a "save as"
		if (document != null && isInstalled()) {

			// since we're marking the entire doc dirty
			getDirtyRegionQueue().clear();
			DirtyRegion entireDocument = createDirtyRegion(0, document.getLength(), DirtyRegion.INSERT);
			processDirtyRegion(entireDocument);
		}
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

	/**
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.DirtyRegionProcessor#uninstall()
	 */
	public void uninstall() {
		if (isInstalled()) {

			getLocalProgressMonitor().setCanceled(true);
			cancel();

			// removes model listeners
			unhookModelLifecycleListener(getDocument());

			// removes document listeners
			reconcilerDocumentChanged(null);

			// removes widget listener
			getTextViewer().removeTextInputListener(fTextInputListener);
			// getTextViewer().getTextWidget().removeDisposeListener(fDisposeListener);

			// release all strategies
			List strategyTypes = getStrategyTypes();
			if (!strategyTypes.isEmpty()) {
				Iterator it = strategyTypes.iterator();
				IReconcilingStrategy strategy = null;
				while (it.hasNext()) {
					strategy = getReconcilingStrategy((String) it.next());
					if (strategy instanceof IReleasable) {
						((IReleasable) strategy).release();
						strategy = null;
					}
				}
			}
			
			IReconcilingStrategy defaultStrategy = getDefaultStrategy();
			IReconcilingStrategy validatorStrategy = getValidatorStrategy();
			
			if(defaultStrategy != null) {
				if(defaultStrategy instanceof IReleasable)
					((IReleasable)defaultStrategy).release();
			}
			
			if(validatorStrategy != null) {
				if(validatorStrategy instanceof IReleasable)
					((IReleasable)validatorStrategy).release();
			}
		}
		super.uninstall();
	}
	
	public void documentAboutToBeChanged(DocumentEvent event) {
		// save partition type (to see if it changes)
		fLastPartitions = getPartitions(event.getOffset(), event.getLength());			
	}

	public void documentChanged(DocumentEvent event) {
		if(partitionsChanged(event)) {
			// pa_TODO
			// this is a simple way to ensure old
			// annotations are removed when partition changes
			
			// it might be a performance hit though
			setEntireDocumentDirty(getDocument());
		}
		else {
			DirtyRegion dr = null;
			if(event.getLength() == 0) {
				// it's an insert
				dr = createDirtyRegion(event.getOffset(), 0, DirtyRegion.INSERT);
			}
			else {
				if(event.getText().equals("")) {
					// it's a delete
					dr =createDirtyRegion(event.getOffset(), event.getLength(), DirtyRegion.REMOVE);
				}
				else {
					// it's a replace
					dr = createDirtyRegion(event.getOffset(), event.getLength(), DirtyRegion.INSERT);	
				}
			}
			processDirtyRegion(dr);
		}
	}
	
	/**
	 * Checks previous partitions from the span of the event
	 * w/ the new partitions from the span of the event.
	 * If partitions changed, return true, else return false
	 * 
	 * @param event
	 * @return
	 */
	private boolean partitionsChanged(DocumentEvent event) {
		boolean changed = false;
		String[] newPartitions = getPartitions(event.getOffset(), event.getLength());
		if(fLastPartitions != null) {
			if(fLastPartitions.length != newPartitions.length) {
				changed = true;
			}
			else {
				for(int i=0; i<fLastPartitions.length; i++) {
					if(!fLastPartitions[i].equals(newPartitions[i])) {
						changed = true;
						break;
					}
				}	
			}
		}
		return changed;
	}
}
