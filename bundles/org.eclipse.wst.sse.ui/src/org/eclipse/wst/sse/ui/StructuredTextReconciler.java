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
package org.eclipse.wst.sse.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.reconciler.Reconciler;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.wst.sse.core.IModelLifecycleListener;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.events.NewModelEvent;
import org.eclipse.wst.sse.core.events.NoChangeEvent;
import org.eclipse.wst.sse.core.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.ui.internal.reconcile.IStructuredReconcilingStrategy;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorStrategy;
import org.eclipse.wst.sse.ui.util.Assert;


/**
 * Reconciler that maps different partitions to different strategies.
 * Strategies contain one or more Steps
 * Steps contain code that validates dirty regions
 * 
 * Is aware of StructuredDocumentEvents which determine if a reconcile should be done or not.
 * On partition change events in the document, all strategies are called.
 * 
 * @author pavery
 */
public class StructuredTextReconciler extends Reconciler implements IStructuredDocumentListener, IModelLifecycleListener {

	/**
	 * Cancels any running reconcile operations via progress monitor.
	 * Ensures that strategies are released on close of the editor.
	 */
	private class SourceWidgetDisposeListener implements DisposeListener {

		public void widgetDisposed(DisposeEvent e) {
			getLocalProgressMonitor().setCanceled(true);
			//getProgressMonitor().setCanceled(true);
			// release all strategies
			if (fDefaultStrategy != null && fDefaultStrategy instanceof IReleasable) {
				((IReleasable) fDefaultStrategy).release();
				fDefaultStrategy = null;
			}
			if (!fStrategyTypes.isEmpty()) {
				Iterator it = fStrategyTypes.iterator();
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
	}

	/**
	 * Reconclies the entire document when the document in the viewer is changed.
	 * This happens when the document is initially opened, as well as after a save-as
	 */
	private class SourceTextInputListener implements ITextInputListener {

		public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
			// do nothing
		}

		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			
			setDocumentOnAllStrategies(newInput);
			setEntireDocumentDirty(newInput);
		}
	}

	public static final String TRACE_FILTER = "reconciler"; //$NON-NLS-1$

	/** set true after first install to prevent duplicate work done in the install method (since install gets called multiple times)*/
	private boolean fIsInstalled = false;

	/** flag set via structured document events */
	private boolean fNeedsValidation = true;

	/** the list of partition types for which there are strategies */
	List fStrategyTypes = null;

	/** document that this reconciler works on */
	private IDocument fLocalDocument = null;

	/** to cancel any long running reconciles if someone closes the editor */
	private SourceWidgetDisposeListener fDisposeListener = null;

	/** for initital reconcile when document is opened*/
	private SourceTextInputListener fTextInputListener = null;

	/** local queue of dirty regions (created here) to be reconciled */
	private List fLocalDirtyRegionQueue = null;

	// use our own local for now until we resolve abstract calling it on every document change
	// resulting in some of our strategies getting cut short and not adding/removing annotations correctly
	private IProgressMonitor fLocalProgressMonitor = null;

	/** strategy called for unmapped partitions */
	IReconcilingStrategy fDefaultStrategy;

	/** the strategy that runs validators contributed via reconcileValidator ext point */
	private ValidatorStrategy fValidatorStrategy;
	
	/** local copy of model manager */
	private IModelManager modelManager = null;
	
	/**
	 * Creates a new StructuredTextReconciler
	 */
	public StructuredTextReconciler() {
		super();
		configure();
	}

	private void configure() {
		fStrategyTypes = new ArrayList();

		// we are always incremental
		setIsIncrementalReconciler(true);
		setDelay(500);
		//setProgressMonitor(new NullProgressMonitor());
		setLocalProgressMonitor(new NullProgressMonitor());
		fDisposeListener = new SourceWidgetDisposeListener();
		fTextInputListener = new SourceTextInputListener();
		fLocalDirtyRegionQueue = new ArrayList();
	}

	/**
	 * Gets the default strategy for this reconciler.
	 * @return the default strategy
	 */
	protected IReconcilingStrategy getDefaultStrategy() {
		return fDefaultStrategy;
	}

	protected void initialProcess() {
		// only happens ONCE on first dirty region in queue (not on doucment open)
		// not useful to us at the moment
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconciler#install(ITextViewer)
	 */
	public void install(ITextViewer textViewer) {
		// we might be called multiple times with the same viewer,
		// maybe after being uninstalled as well, so track separately
		if (fIsInstalled)
			return;

		super.install(textViewer);
		textViewer.getTextWidget().addDisposeListener(fDisposeListener);
		textViewer.addTextInputListener(fTextInputListener);

		//		getProgressMonitor().setCanceled(false);
		getLocalProgressMonitor().setCanceled(false);

		fIsInstalled = true;
	}

	/**
	 * The viewer has been set on this Reconciler.
	 * @return true if the viewer has been set on this Reconciler, false otherwise.
	 */
	public boolean isInstalled() {
		return fIsInstalled;
	}

	/**
	 * Cleanup listeners.
	 * @see org.eclipse.jface.text.reconciler.IReconciler#uninstall()
	 */
	public void uninstall() {
		if (fIsInstalled) {
			fIsInstalled = false;
			//getProgressMonitor().setCanceled(true);
			getLocalProgressMonitor().setCanceled(true);

			getTextViewer().removeTextInputListener(fTextInputListener);

			super.uninstall();
		}
		if (fLocalDocument != null && fLocalDocument instanceof IStructuredDocument) {
			// remove structured document listener
			((IStructuredDocument) fLocalDocument).removeModelChangedListener(this);
			// remove lifecycle listener on the model
			unhookModelLifecycleListener(fLocalDocument);
		}
		fLocalDocument = null;
	}

	/**
	 * We keep a local copy of the dirty region queue for compacting.
	 * 
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#process(org.eclipse.jface.text.reconciler.DirtyRegion)
	 */
	protected void process(DirtyRegion dirtyRegion) {
		// called here so that it only kick off after .5 seconds
		// but fNeedsValidation flag is set in structuredDoucmentsEvents below
		runStrategies();
	}

	/**
	 * Runs the appropriate strategies on the dirty region queue.
	 * The reconciler currently handles these reconciling scenarios:
	 *
	 * <ul>
	 * 	<li>partition change</li>
	 *  <li>routine text edits</li>
	 *  <li>entire document change</li>
	 *  <li>the default strategy</li>
	 * </ul>
	 *
	 */
	private void runStrategies() {
		DirtyRegion dirtyRegion = null;
		while (fDefaultStrategy != null && isInstalled() && fNeedsValidation) {
			// this flag may be set to true if more dirty regions come in while this method is running
			fNeedsValidation = false;
			Logger.trace(TRACE_FILTER, "start RUNNING STRATEGIES IN RECONCILER"); //$NON-NLS-1$
			dirtyRegion = compactDirtyRegionQueue();
			// will be null if there is nothing in the queue
			if (dirtyRegion != null) {
				Logger.trace(TRACE_FILTER, "RUNNING with dirty region:" + dirtyRegion.getOffset() + ":" + dirtyRegion.getLength()); //$NON-NLS-1$ //$NON-NLS-2$
				try {
					if (isEntireDocumentChange(dirtyRegion))
						processAll();
					else
						processPartial(dirtyRegion);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param dirtyRegion
	 * @return
	 */
	private boolean isEntireDocumentChange(DirtyRegion dirtyRegion) {
		return getDocument().getLength() == dirtyRegion.getLength();
	}

	/**
	 * This method reduces the dirty region queue to the least common dirty region. (the region that overlaps all dirty regions)
	 * Needs to be synchronized or we tend to "lose" dirty regions.
	 * 
	 * @return a condensed DirtyRegion representing all that was in the queue at the time this was called, or <code>null</code> if the queue is empty
	 */
	private synchronized DirtyRegion compactDirtyRegionQueue() {
		StringBuffer traceInfo = new StringBuffer();
		if (Logger.isTracing(TRACE_FILTER))
			traceInfo.append("[reconciler] COMPACTING STARTING... localDirtyRegionQueue.size():" + fLocalDirtyRegionQueue.size()); //$NON-NLS-1$

		if (fLocalDirtyRegionQueue.size() == 1)
			return (DirtyRegion) fLocalDirtyRegionQueue.remove(0);

		int min = -1;
		int max = -1;
		if (!fLocalDirtyRegionQueue.isEmpty()) {
			DirtyRegion dr = null;
			for (int i = 0; i < fLocalDirtyRegionQueue.size(); i++) {
				dr = (DirtyRegion) fLocalDirtyRegionQueue.get(i);
				if (dr == null)
					continue;

				if (Logger.isTracing(TRACE_FILTER))
					traceInfo.append("\r\n\r\n -> compacting dirty region (" + i + ")" + " start:" + dr.getOffset() + " length:" + dr.getLength()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

				//possibly expand the dirty region start
				if (min == -1 || min > dr.getOffset())
					min = dr.getOffset();
				// possibly expand the dirty region end
				if (max == -1 || max < dr.getOffset() + dr.getLength())
					max = dr.getOffset() + dr.getLength();
			}
			fLocalDirtyRegionQueue.clear();
		}
		if (Logger.isTracing(TRACE_FILTER)) {
			traceInfo.append("\r\n\r\nCOMPACTING DONE... dirtyRangeStart:" + min + " dirtyRangeEnd:" + max + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			Logger.trace(TRACE_FILTER, traceInfo.toString());
		}
		return (min != -1) ? createDirtyRegion(min, max - min, DirtyRegion.INSERT) : null;
	}

	/**
	 * Process the entire StructuredDocument.
	 * Much more resource intensive than simply running a strategy on a dirty region.
	 */
	protected void processAll() {
		Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] >StructuredTextReconciler: PROCESSING ALL"); //$NON-NLS-1$
		IDocument doc = getDocument();
		DirtyRegion durty = null;
		ITypedRegion tr[] = doc.getDocumentPartitioner().computePartitioning(0, doc.getLength());
		IReconcilingStrategy s = null;
		for (int i = 0; i < tr.length; i++) {
			durty = createDirtyRegion(tr[i], DirtyRegion.INSERT);
			s = getReconcilingStrategy(tr[i].getType());
			if (s != null) {
				if (s instanceof IStructuredReconcilingStrategy)
					((IStructuredReconcilingStrategy) s).reconcile(durty, durty, true);
				else
					s.reconcile(durty, durty);
			}
			// run validator strategy every time, it figures out if it has a validator for this partition
			// pass in true for "refreshAll" flag = true indicating that the entire document is being reconciled, only do it once
			if (fValidatorStrategy != null)
				fValidatorStrategy.reconcile(tr[i], durty, true);
		}
		// we ran the whole doc already now we can reset the strategies
		resetStrategies();
	}

	/**
	 * Process a subsection of the document.
	 */
	protected void processPartial(DirtyRegion durty) {
		IDocument doc = getDocument();
		HashSet alreadyRan = new HashSet();
		ITypedRegion tr[] = doc.getDocumentPartitioner().computePartitioning(durty.getOffset(), durty.getLength());
		IReconcilingStrategy s = null;
		for (int i = 0; i < tr.length; i++) {
			durty = createDirtyRegion(tr[i], DirtyRegion.INSERT);
			// keeping track of already ran might not be the way to do it...
			if (!alreadyRan.contains(tr[i].getType())) {
				alreadyRan.add(tr[i].getType());
				s = getReconcilingStrategy(tr[i].getType());
				if (s != null)
					s.reconcile(durty, durty);
			}
			// run validator strategy every time, it figures out if it has a validator for this parition
			if (fValidatorStrategy != null)
				fValidatorStrategy.reconcile(tr[i], durty, false);
		}
		resetStrategies();
	}

	private DirtyRegion createDirtyRegion(ITypedRegion tr, String type) {
		return createDirtyRegion(tr.getOffset(), tr.getLength(), type);
	}

	private DirtyRegion createDirtyRegion(int offset, int length, String type) {
		DirtyRegion durty = null;
		IDocument doc = getDocument();
		// safety for BLE
		int docLen = doc.getLength();
		if (offset + length > docLen)
			length = docLen - offset;

		if (doc != null) {
			try {
				durty = new DirtyRegion(offset, length, type, doc.get(offset, length));
			}
			catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return durty;
	}

	/**
	 * Resets any flags that were set (eg. flags set during processAll())
	 */
	protected void resetStrategies() {
		Iterator it = fStrategyTypes.iterator();
		String type = null;
		while (it.hasNext()) {
			type = (String) it.next();
			if (getReconcilingStrategy(type) instanceof IStructuredReconcilingStrategy)
				((IStructuredReconcilingStrategy) getReconcilingStrategy(type)).reset();
		}
	}

	/**
	 * Gets a strategy that is made to handle the given dirtyRegion.
	 * @param dirtyRegion
	 * @return a strategy that is made to handle the given dirtyRegion, or the default strategy for this reconciler if there isn't one
	 */
	protected IReconcilingStrategy getAppropriateStrategy(DirtyRegion dirtyRegion) {
		String[] partitions = getPartitions(dirtyRegion);
		// for now just grab first partition type in dirty region
		IReconcilingStrategy rs = null;
		if (partitions.length > 0)
			rs = getReconcilingStrategy(partitions[0]);
		return rs != null ? rs : fDefaultStrategy;
	}

	/**
	 * Utility method to get partitions of a dirty region
	 * @param dirtyRegion
	 * @return
	 */
	protected String[] getPartitions(DirtyRegion dirtyRegion) {
		ITypedRegion[] regions = getPartitioner().computePartitioning(dirtyRegion.getOffset(), dirtyRegion.getLength());
		String[] partitions = new String[regions.length];
		for (int i = 0; i < regions.length; i++)
			partitions[i] = regions[i].getType();
		return partitions;
	}

	/**
	 * assumes isInstalled() == true
	 * @return the document partitioner for the document this reconciler is working on.
	 */
	protected IDocumentPartitioner getPartitioner() {
		return getDocument().getDocumentPartitioner();
	}

	/**
	 * Reinitializes listeners and sets new document onall strategies.
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#reconcilerDocumentChanged(IDocument)
	 */
	protected void reconcilerDocumentChanged(IDocument document) {
		
		// unhook old lifecycle listner
		unhookModelLifecycleListener(fLocalDocument);
		
		// add new lifecycle listener
		hookUpModelLifecycleListener(document);
		
		if (fLocalDocument != null && fLocalDocument instanceof IStructuredDocument)
			((IStructuredDocument) fLocalDocument).removeModelChangedListener(this);

		fLocalDocument = document;

		if (document != null && document instanceof IStructuredDocument)
			((IStructuredDocument) fLocalDocument).addModelChangedListener(this);
		
		setDocumentOnAllStrategies(document);
	}

	
	
	/**
	 * @param document
	 */
	private void hookUpModelLifecycleListener(IDocument document) {
		IStructuredModel sModel = getStructuredModelForRead(document);
		try {
			if(sModel != null) {
				sModel.addModelLifecycleListener(this);
			}
		}
		finally {
			if(sModel != null)
				sModel.releaseFromRead();
		}
	}
	
	/**
	 * 
	 * @param document
	 */
	private void unhookModelLifecycleListener(IDocument document) {
		IStructuredModel sModel = getStructuredModelForRead(document);
		try {
			if(sModel != null) {
				sModel.removeModelLifecycleListener(this);
			}
		}
		finally {
			if(sModel != null)
				sModel.releaseFromRead();
		}
	}

	/**
	 * Sets the progress monitor for this reconciler. Used to cancel potentially long running operations.
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#setProgressMonitor(IProgressMonitor)
	 */
	//	public void setProgressMonitor(IProgressMonitor monitor) {
	//		super.setProgressMonitor(monitor);
	//		if(fDefaultStrategy != null && fDefaultStrategy instanceof IReconcilingStrategyExtension)
	//			((IReconcilingStrategyExtension)fDefaultStrategy).setProgressMonitor(monitor);
	//	}
	/**
	 * Sets the default reconciling strategy.
	 * @param strategy
	 */
	public void setDefaultStrategy(IReconcilingStrategy strategy) {
		Assert.isNotNull(strategy, "Can't set default strategy to null"); //$NON-NLS-1$

		fDefaultStrategy = strategy;
		fDefaultStrategy.setDocument(getDocument());
		if (fDefaultStrategy instanceof IReconcilingStrategyExtension)
			((IReconcilingStrategyExtension) fDefaultStrategy).setProgressMonitor(getLocalProgressMonitor());
		//((IReconcilingStrategyExtension)fDefaultStrategy).setProgressMonitor(getProgressMonitor());
	}

	/**
	 * Sets the strategy for a given contentType (partitionType)
	 * @see org.eclipse.jface.text.reconciler.Reconciler#setReconcilingStrategy(org.eclipse.jface.text.reconciler.IReconcilingStrategy, java.lang.String)
	 */
	public void setReconcilingStrategy(IReconcilingStrategy strategy, String contentType) {
		super.setReconcilingStrategy(strategy, contentType);
		if (strategy != null) {
			strategy.setDocument(fLocalDocument);
			if (strategy instanceof IReconcilingStrategyExtension) {
				((IReconcilingStrategyExtension) strategy).setProgressMonitor(getLocalProgressMonitor());
			}
		}
		fStrategyTypes.add(contentType);
	}

	public void newModel(NewModelEvent structuredDocumentEvent) {
		// do nothing
	}

	public void noChange(NoChangeEvent structuredDocumentEvent) {
		// do nothing
	}

	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
		Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] >StructuredTextReconciler: *NODES REPLACED"); //$NON-NLS-1$

		// if partition changed, create a full document dirty region (causes processAll)
		DirtyRegion dr = partitionChanged(structuredDocumentEvent) ? createDirtyRegion(0, getDocument().getLength(), DirtyRegion.INSERT) : createDirtyRegion(structuredDocumentEvent.getOriginalStart(), structuredDocumentEvent.getLength(), DirtyRegion.INSERT);
		fLocalDirtyRegionQueue.add(dr);

		fNeedsValidation = true;
	}

	public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
		Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] >StructuredTextReconciler: *REGION CHANGED: \r\n\r\n created dirty region from flat model event >> :" + structuredDocumentEvent.getOriginalStart() + ":" + structuredDocumentEvent.getLength() + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		String dirtyRegionType = structuredDocumentEvent.getDeletedText().equals("") ? DirtyRegion.INSERT : DirtyRegion.REMOVE; //$NON-NLS-1$
		DirtyRegion dr = createDirtyRegion(structuredDocumentEvent.getOriginalStart(), structuredDocumentEvent.getLength(), dirtyRegionType);

		fLocalDirtyRegionQueue.add(dr);

		fNeedsValidation = true;
	}

	public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
		Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] >StructuredTextReconciler: *REGIONS REPLACED: \r\n\r\n created dirty region from flat model event >> :" + structuredDocumentEvent.getOriginalStart() + ":" + structuredDocumentEvent.getLength() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		DirtyRegion dr = createDirtyRegion(structuredDocumentEvent.getOriginalStart(), structuredDocumentEvent.getLength(), DirtyRegion.INSERT);
		fLocalDirtyRegionQueue.add(dr);

		fNeedsValidation = true;
	}

	/**
	 * Checks if the StructuredDocumentEvent involved a partition change.
	 * If there's a partition change, we know we should run all strategies just to be sure we cover the new regions and
	 * remove obsolete annotations.
	 * 
	 * A primitive check for now.
	 * @param structuredDocumentEvent
	 * @return
	 */
	private boolean partitionChanged(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
		boolean changed = false;

		IDocumentPartitioner partitioner = structuredDocumentEvent.getStructuredDocument().getDocumentPartitioner();

		IStructuredDocumentRegionList oldNodes = structuredDocumentEvent.getOldStructuredDocumentRegions();
		IStructuredDocumentRegionList newNodes = structuredDocumentEvent.getNewStructuredDocumentRegions();

		IStructuredDocumentRegion oldNode = (oldNodes.getLength() > 0) ? oldNode = oldNodes.item(0) : null;
		IStructuredDocumentRegion newNode = (newNodes.getLength() > 0) ? newNodes.item(0) : null;

		if (oldNode != null && newNode != null)
			changed = partitioner.getContentType(oldNode.getStartOffset()).equals(partitioner.getContentType(newNode.getStartOffset()));

		return changed;
	}

	/**
	 * We use our own local progress monitor to cancel long running strategies/steps.
	 * Currently used when widget is disposed (user is trying to close the editor), and on uninstall.
	 * 
	 * @return the local progress monitor
	 */
	IProgressMonitor getLocalProgressMonitor() {
		return fLocalProgressMonitor;
	}

	private void setLocalProgressMonitor(IProgressMonitor pm) {
		fLocalProgressMonitor = pm;
		// set on default strategy
		if (fDefaultStrategy != null && fDefaultStrategy instanceof IReconcilingStrategyExtension)
			((IReconcilingStrategyExtension) fDefaultStrategy).setProgressMonitor(pm);
		// set on all other strategies
		if (!fStrategyTypes.isEmpty()) {
			Iterator it = fStrategyTypes.iterator();
			String type = null;
			while (it.hasNext()) {
				type = (String) it.next();
				if (getReconcilingStrategy(type) instanceof IReconcilingStrategyExtension)
					((IReconcilingStrategyExtension) getReconcilingStrategy(type)).setProgressMonitor(pm);
			}
		}
	}

	/**
	 * Set the strategy that runs validators from the reconcileValidator extension point.
	 * @param the ValidatorStrategy
	 */
	public void setValidatorStrategy(ValidatorStrategy strategy) {
		fValidatorStrategy = strategy;
		if (fValidatorStrategy != null)
			fValidatorStrategy.setDocument(getDocument());
	}

	/**
	 * Get the strategy that runs validators from the reconcileValidator extension point.
	 * @param the ValidatorStrategy
	 */
	public ValidatorStrategy getValidatorStrategy() {
		return fValidatorStrategy;
	}

	/**
	 * Propagates a new document to all strategies and steps.
	 * @param document
	 */
	protected void setDocumentOnAllStrategies(IDocument document) {
		if(isInstalled()) {
			// default strategies
			if (fDefaultStrategy != null) 
				fDefaultStrategy.setDocument(document);
			
			// external validator strategy
			if(fValidatorStrategy != null) 
				fValidatorStrategy.setDocument(document);
			
			// set document on all regular strategies
			super.reconcilerDocumentChanged(document);
		}
	}
	
	protected void setEntireDocumentDirty(IDocument document) {
		
		// make the entire document dirty
		// this also happens on a "save as"
		if (document != null && isInstalled() && fLocalDirtyRegionQueue.size() == 0) {		
			
			// since we're marking the entire doc dirty
			fLocalDirtyRegionQueue.clear();
			
			DirtyRegion entireDocument = createDirtyRegion(0, document.getLength(), DirtyRegion.INSERT);
			fLocalDirtyRegionQueue.add(entireDocument);
			
			// set this so reconcile won't be "short circuited"
			fNeedsValidation = true;
		}	
	}
	
	/**
	 */
	public void processPreModelEvent(ModelLifecycleEvent event) {

		if(event.getType() == ModelLifecycleEvent.MODEL_DOCUMENT_CHANGED) {
			
			// clear the dirty region queue
			fLocalDirtyRegionQueue.clear();
			
			// note: old annotations are removed via the strategies on AbstractStructuredTextReconcilingStrategy#setDocument(...)
		}
	}

	/**
	 */
	public void processPostModelEvent(ModelLifecycleEvent event) {
		
		// if underlying StructuredDocument changed, need to reconnect it here...
		// ex. file is modified outside the workbench
		if(event.getType() == ModelLifecycleEvent.MODEL_DOCUMENT_CHANGED) {
			IStructuredDocument sDoc = event.getModel().getStructuredDocument();
			
			setDocumentOnAllStrategies(sDoc);

			// ensure that the document is re-reconciled
			setEntireDocumentDirty(sDoc);
		}
	}
	
	/**
	 * Remember to release model after use!!
	 * @return
	 */
	public IStructuredModel getStructuredModelForRead(IDocument doc) {
		
		IStructuredModel sModel = null;
		if(doc != null)
			sModel = getModelManager().getExistingModelForRead(doc);
		return sModel;
	}
	
	/**
	 * Avoid excessive calls to Platform.getPlugin(ModelPlugin.ID)
	 * @return sse model manager
	 */
	protected IModelManager getModelManager() {
		
		if (this.modelManager == null)
			this.modelManager = ((IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID)).getModelManager();
		return this.modelManager;
	}
}
