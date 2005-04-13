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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.wst.sse.core.IModelLifecycleListener;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.events.NoChangeEvent;
import org.eclipse.wst.sse.core.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorStrategy;

/**
 * Adds StructuredDocument and StructuredModel listeners. Adds Text viewer
 * (dispose, input changed) listeners.
 * 
 * Implements a smarter "contains" method.
 * 
 * Adds default and validator strategies. Adds DirtyRegion processing logic.
 */
public class StructuredRegionProcessor extends DirtyRegionProcessor implements IStructuredDocumentListener, IModelLifecycleListener {

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

	/**
	 * Cancels any running reconcile operations via progress monitor. Ensures
	 * that strategies are released on close of the editor.
	 */
	private class SourceWidgetDisposeListener implements DisposeListener {

		public void widgetDisposed(DisposeEvent e) {
			handleWidgetDisposed();
		}
	}

	/** to cancel any long running reconciles if someone closes the editor */
	private SourceWidgetDisposeListener fDisposeListener = null;
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
		return fValidatorStrategy;
	}

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
	 * Removed multiple "total-scope" regions (and leaves one)
	 * for a each partitionType.  This improves performance
	 * by preventing unnecessary full document validations.
	 * 
	 * @param unfiltered
	 * @return
	 */
	private ITypedRegion[] filterTotalScopeRegions(ITypedRegion[] unfiltered) {
		IReconcilingStrategy s = null;
		// ensure there is only one typed region in the list
		// for regions handled by "total scope" strategies
		HashMap totalScopeRegions = new HashMap();
		List allRegions = new ArrayList();
		for (int i = 0; i < unfiltered.length; i++) {
			String partitionType = unfiltered[i].getType();
			s = getReconcilingStrategy(partitionType);
			if(s instanceof AbstractStructuredTextReconcilingStrategy) {
				// only allow one dirty region for a strategy
				// that has "total scope"
				if(((AbstractStructuredTextReconcilingStrategy)s).isTotalScope())
					totalScopeRegions.put(partitionType, unfiltered[i]);
				else
					allRegions.add(unfiltered[i]);
			}
			else
				allRegions.add(unfiltered[i]);
		}
		allRegions.addAll(totalScopeRegions.values());
		ITypedRegion[] filtered = (ITypedRegion[])allRegions.toArray(new ITypedRegion[allRegions.size()]);
		
		if(DEBUG)
			System.out.println("filtered out this many 'total-scope' regions: " + (unfiltered.length - filtered.length));
		
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
	 * @param validatorStrategy
	 *            The fValidatorStrategy to set.
	 */
	public void setValidatorStrategy(ValidatorStrategy validatorStrategy) {
		fValidatorStrategy = validatorStrategy;
		if (fValidatorStrategy != null) {
			fValidatorStrategy.setDocument(getDocument());
			fValidatorStrategy.setProgressMonitor(getLocalProgressMonitor());
		}
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

	public void handleWidgetDisposed() {

		getLocalProgressMonitor().setCanceled(true);

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
		fDisposeListener = new SourceWidgetDisposeListener();
		fTextInputListener = new SourceTextInputListener();
		textViewer.getTextWidget().addDisposeListener(fDisposeListener);
		textViewer.addTextInputListener(fTextInputListener);
	}

	public void newModel(NewDocumentEvent structuredDocumentEvent) {
		// happens on a revert
		reconcilerDocumentChanged(structuredDocumentEvent.getDocument());
	}

	public void noChange(NoChangeEvent structuredDocumentEvent) {
		// do nothing
	}

	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
		if (DEBUG)
			System.out.println("[trace reconciler] >StructuredRegionProcessor: *NODES REPLACED"); //$NON-NLS-1$

		DirtyRegion dr = partitionChanged(structuredDocumentEvent) ? createDirtyRegion(0, getDocument().getLength(), DirtyRegion.INSERT) : createDirtyRegion(structuredDocumentEvent.getOffset(), structuredDocumentEvent.getLength(), DirtyRegion.INSERT);
		processDirtyRegion(dr);
	}

	/**
	 * Checks if the StructuredDocumentEvent involved a partition change. If
	 * there's a partition change, we know we should run all strategies just
	 * to be sure we cover the new regions and remove obsolete annotations.
	 * 
	 * A primitive check for now.
	 * 
	 * @param structuredDocumentEvent
	 * @return
	 */
	private boolean partitionChanged(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
		boolean changed = false;

		IDocumentPartitioner partitioner = structuredDocumentEvent.getStructuredDocument().getDocumentPartitioner();
		if (partitioner != null) {
			IStructuredDocumentRegionList oldNodes = structuredDocumentEvent.getOldStructuredDocumentRegions();
			IStructuredDocumentRegionList newNodes = structuredDocumentEvent.getNewStructuredDocumentRegions();

			IStructuredDocumentRegion oldNode = (oldNodes.getLength() > 0) ? oldNode = oldNodes.item(0) : null;
			IStructuredDocumentRegion newNode = (newNodes.getLength() > 0) ? newNodes.item(0) : null;

			if (oldNode != null && newNode != null)
				changed = partitioner.getContentType(oldNode.getStartOffset()).equals(partitioner.getContentType(newNode.getStartOffset()));
		}
		return changed;
	}

	/**
	 * @see org.eclipse.wst.sse.core.IModelLifecycleListener#processPostModelEvent(org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent)
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
	 * @see org.eclipse.wst.sse.core.IModelLifecycleListener#processPreModelEvent(org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent)
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
		hookUpModelLifecycleListener(newDocument);

		// unhook old document listener
		if (currentDoc != null && currentDoc instanceof IStructuredDocument)
			((IStructuredDocument) currentDoc).removeDocumentChangedListener(this);
		// hook up new document listener
		if (newDocument != null && newDocument instanceof IStructuredDocument)
			((IStructuredDocument) newDocument).addDocumentChangedListener(this);

		// sets document on all strategies
		super.reconcilerDocumentChanged(newDocument);
	}

	public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
		if (DEBUG)
			System.out.println("[trace reconciler] >StructuredRegionProcessor: *REGION CHANGED: \r\n\r\n created dirty region from flat model event >> :" + structuredDocumentEvent.getOffset() + ":" + structuredDocumentEvent.getLength() + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		String dirtyRegionType = structuredDocumentEvent.getDeletedText().equals("") ? DirtyRegion.INSERT : DirtyRegion.REMOVE; //$NON-NLS-1$
		DirtyRegion dr = createDirtyRegion(structuredDocumentEvent.getOffset(), structuredDocumentEvent.getLength(), dirtyRegionType);
		processDirtyRegion(dr);
	}

	public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
		if (DEBUG)
			System.out.println("[trace reconciler] >StructuredRegionProcessor: *REGIONS REPLACED: \r\n\r\n created dirty region from flat model event >> :" + structuredDocumentEvent.getOffset() + ":" + structuredDocumentEvent.getLength() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		DirtyRegion dr = createDirtyRegion(structuredDocumentEvent.getOffset(), structuredDocumentEvent.getLength(), DirtyRegion.INSERT);
		processDirtyRegion(dr);
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
			getTextViewer().removeTextInputListener(fTextInputListener);
			getTextViewer().getTextWidget().removeDisposeListener(fDisposeListener);
		}
		super.uninstall();
	}
}
