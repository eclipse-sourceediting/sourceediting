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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.wst.sse.core.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.events.NoChangeEvent;
import org.eclipse.wst.sse.ui.internal.Logger;

/**
 * This job holds a queue of updates from the editor (DirtyRegions) to
 * process. When a new request comes in, the current run is canceled, the new
 * request is added to the queue, then the job is re-scheduled.
 * 
 * @author pavery
 */
public class DirtyRegionProcessor extends Job implements IReconciler {
	/** debug flag */
	protected static final boolean DEBUG;

	private static final long UPDATE_DELAY = 750;
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.sse.ui/debug/reconcilerjob"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	private long fDelay;

	/** local queue of dirty regions (created here) to be reconciled */
	private List fDirtyRegionQueue = null;

	/** document that this reconciler works on */
	private IDocument fDocument = null;

	/**
	 * set true after first install to prevent duplicate work done in the
	 * install method (since install gets called multiple times)
	 */
	private boolean fIsInstalled = false;

	private IProgressMonitor fLocalProgressMonitor = null;

	/**
	 * The partitioning this reconciler uses.
	 */
	private String fPartitioning;

	/** The map of reconciling strategies. */
	private Map fStrategies;

	/** the list of partition types for which there are strategies */
	private List fStrategyTypes = null;

	/** the text viewer */
	private ITextViewer fViewer;

	/**
	 * Creates a new StructuredRegionProcessor
	 */
	public DirtyRegionProcessor() {
		// init job stuff
		super("Processing Dirty Regions");
		setPriority(Job.LONG);
		setSystem(true);
		// init some fields
		fStrategyTypes = new ArrayList();
		setLocalProgressMonitor(new NullProgressMonitor());
		fDirtyRegionQueue = Collections.synchronizedList(new ArrayList());
		// init reconciler stuff
		setDelay(UPDATE_DELAY);
	}

	/**
	 * Adds the given resource to the set of resources that need refreshing.
	 * Synchronized in order to protect the collection during add.
	 * 
	 * @param resource
	 */
	private synchronized void addRequest(DirtyRegion dr) {
        
        
        
		List drq = getDirtyRegionQueue();
		// if we already have a request which contains the new request,
		// discare the new request
		int size = drq.size();
		for (int i = 0; i < size; i++) {
			if (contains((DirtyRegion) drq.get(i), dr))
				return;
		}
		// if new request is contains any existing requests,
		// remove those
		for (Iterator it = drq.iterator(); it.hasNext();) {
			if (contains(dr, (DirtyRegion) it.next()))
				it.remove();
		}
		drq.add(dr);
	}

	/**
	 * @param dirtyRegion
	 * @return
	 */
	protected ITypedRegion[] computePartitioning(DirtyRegion dirtyRegion) {
		IDocument doc = getDocument();
		ITypedRegion tr[] = null;
        
        int drOffset = dirtyRegion.getOffset();
        int drLength = dirtyRegion.getLength();
        int docLength = doc.getLength();
        
        if(drOffset > docLength) {
            drOffset = docLength;
            drLength = 0;
        }
        else if(drOffset + drLength > docLength) {
            drLength = docLength - drOffset;
        }
            
		try {
			// dirty region may span multiple partitions
            tr = TextUtilities.computePartitioning(doc, getDocumentPartitioning(), drOffset, drLength, true);
		}
		catch (BadLocationException e) {
            String info = "dr: ["+ drOffset+":"+ drLength + "] doc: [" + docLength + "] ";
			Logger.logException(info, e);
			tr = new ITypedRegion[0];
		}
		return tr;
	}

	/**
	 * @return if the root is parent of possible, return true, otherwise
	 *         return false
	 */
	protected boolean contains(DirtyRegion root, DirtyRegion possible) {

		int rootStart = root.getOffset();
		int rootEnd = rootStart + root.getLength();
		int possStart = possible.getOffset();
		int possEnd = possStart + possible.getLength();
		if (rootStart <= possStart && rootEnd >= possEnd)
			return true;
		return false;
	}

	protected DirtyRegion createDirtyRegion(int offset, int length, String type) {
		DirtyRegion durty = null;
		IDocument doc = getDocument();

		if (doc != null) {
			// safety for BLE
			int docLen = doc.getLength();
            if(offset > docLen) {
                offset = docLen;
                length = 0;
            }
            else if (offset + length >= docLen)
				length = docLen - offset;
			try {
				durty = new DirtyRegion(offset, length, type, doc.get(offset, length));
			}
			catch (BadLocationException e) {
                String info = "dr: ["+ offset+":"+ length + "] doc: [" + docLen + "] ";
                Logger.logException(info, e);
			}
		}
		return durty;
	}

	protected DirtyRegion createDirtyRegion(ITypedRegion tr, String type) {
		return createDirtyRegion(tr.getOffset(), tr.getLength(), type);
	}

	/**
	 * Delay between processing of DirtyRegions.
	 * 
	 * @return
	 */
	protected long getDelay() {
		return fDelay;
	}

	protected List getDirtyRegionQueue() {
		return fDirtyRegionQueue;
	}

	/**
	 * The IDocument on which this reconciler operates
	 * 
	 * @return
	 */
	protected IDocument getDocument() {
		return fDocument;
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilerExtension#getDocumentPartitioning()
	 * @since 3.0
	 */
	public String getDocumentPartitioning() {
		if (fPartitioning == null)
			return IDocumentExtension3.DEFAULT_PARTITIONING;
		return fPartitioning;
	}

	/**
	 * We use our own local progress monitor to cancel long running
	 * strategies/steps. Currently used when widget is disposed (user is
	 * trying to close the editor), and on uninstall.
	 * 
	 * @return the local progress monitor
	 */
	IProgressMonitor getLocalProgressMonitor() {
		return fLocalProgressMonitor;
	}

	/**
	 * Utility method to get partitions of a dirty region
	 * 
	 * @param dirtyRegion
	 * @return
	 */
	protected String[] getPartitions(DirtyRegion dirtyRegion) {
		ITypedRegion regions[] = null;
        
        int drOffset = dirtyRegion.getOffset();
        int drLength = dirtyRegion.getLength();
        int docLength = getDocument().getLength();
        
        if(drOffset > docLength) {
            drOffset = docLength;
            drLength = 0;
        }
        else if(drOffset + drLength > docLength) {
            drLength = docLength - drOffset;
        }
            
		try {
			regions = TextUtilities.computePartitioning(getDocument(), getDocumentPartitioning(), dirtyRegion.getOffset(), dirtyRegion.getLength(), true);
		}
		catch (BadLocationException e) {
			Logger.logException(e);
			regions = new ITypedRegion[0];
		}
		String[] partitions = new String[regions.length];
		for (int i = 0; i < regions.length; i++)
			partitions[i] = regions[i].getType();
		return partitions;
	}

	/**
	 * @see IReconciler#getReconcilingStrategy(String)
	 */
	public IReconcilingStrategy getReconcilingStrategy(String contentType) {
		if (fStrategies == null)
			return null;
		return (IReconcilingStrategy) fStrategies.get(contentType);
	}

	/**
	 * This method also synchronized because it accesses the fRequests queue
	 * 
	 * @return an array of the currently requested Nodes to refresh
	 */
	private synchronized DirtyRegion[] getRequests() {
		DirtyRegion[] toRefresh = (DirtyRegion[]) fDirtyRegionQueue.toArray(new DirtyRegion[fDirtyRegionQueue.size()]);
		fDirtyRegionQueue.clear();
		return toRefresh;
	}

	/**
	 * Gets a strategy that is made to handle the given dirtyRegion.
	 * 
	 * @param dirtyRegion
	 * @return a strategy that is made to handle the given dirtyRegion, or the
	 *         default strategy for this reconciler if there isn't one
	 */
	protected IReconcilingStrategy getStrategy(DirtyRegion dirtyRegion) {
		String[] partitions = getPartitions(dirtyRegion);
		// for now just grab first partition type in dirty region
		IReconcilingStrategy rs = null;
		if (partitions.length > 0)
			rs = getReconcilingStrategy(partitions[0]);
		return rs;
	}

	/**
	 * A list of strategy types (keys) for this reconciler. Each strategy
	 * should have a unique key.
	 * 
	 * @return
	 */
	public List getStrategyTypes() {
		return fStrategyTypes;
	}

	/**
	 * Returns the text viewer this reconciler is installed on.
	 * 
	 * @return the text viewer this reconciler is installed on
	 */
	protected ITextViewer getTextViewer() {
		return fViewer;
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconciler#install(ITextViewer)
	 */
	public void install(ITextViewer textViewer) {
		// we might be called multiple times with the same viewer,
		// maybe after being uninstalled as well, so track separately
		if (!isInstalled()) {
			fViewer = textViewer;
			getLocalProgressMonitor().setCanceled(false);
			setInstalled(true);
		}
	}

	/**
	 * The viewer has been set on this Reconciler.
	 * 
	 * @return true if the viewer has been set on this Reconciler, false
	 *         otherwise.
	 */
	public boolean isInstalled() {
		return fIsInstalled;
	}

	public void newModel(NewDocumentEvent structuredDocumentEvent) {
		// do nothing
	}

	public void noChange(NoChangeEvent structuredDocumentEvent) {
		// do nothing
	}

	/**
	 * Subclasses should implement for specific handling of dirty regions.
	 * 
	 * @param dr
	 */
	protected void process(DirtyRegion dr) {
		// subclasses should implement
	}

	/**
	 * Invoke a refresh on the viewer on the given node.
	 * 
	 * @param node
	 */
	public final void processDirtyRegion(DirtyRegion dr) {
		if (dr == null)
			return;
        
        // if no strategies, don't bother doing
        // anything.
	    if(getStrategyTypes().size() == 0)
            return;
        
		cancel();
		addRequest(dr);
		schedule(getDelay());

		if (DEBUG) {
			System.out.println("added request for: [" + dr.getText() + "]");
			System.out.println("queue size is now: " + getDirtyRegionQueue().size());
		}
	}

	/**
	 * Reinitializes listeners and sets new document on all strategies.
	 * 
	 * @see org.eclipse.jface.text.reconciler.AbstractReconciler#reconcilerDocumentChanged(IDocument)
	 */
	protected void reconcilerDocumentChanged(IDocument document) {
		setDocument(document);
		setDocumentOnAllStrategies(document);
	}

	protected IStatus run(IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		try {
			DirtyRegion[] toRefresh = getRequests();
			for (int i = 0; i < toRefresh.length; i++) {
				if (monitor.isCanceled())
					throw new OperationCanceledException();
				process(toRefresh[i]);
			}
		}
		finally {
			monitor.done();
		}
		return status;
	}

	protected void setDelay(long delay) {
		fDelay = delay;
	}

	public void setDocument(IDocument doc) {
		fDocument = doc;
		setDocumentOnAllStrategies(doc);
	}

	/**
	 * Propagates a new document to all strategies and steps.
	 * 
	 * @param document
	 */
	protected void setDocumentOnAllStrategies(IDocument document) {
		if (isInstalled()) {
			// set document on all regular strategies
			if (fStrategies != null) {
				Iterator e = fStrategies.values().iterator();
				while (e.hasNext()) {
					IReconcilingStrategy strategy = (IReconcilingStrategy) e.next();
					strategy.setDocument(document);
				}
			}
		}
	}

	/**
	 * Sets the document partitioning for this reconciler.
	 * 
	 * @param partitioning
	 *            the document partitioning for this reconciler
	 * @since 3.0
	 */
	public void setDocumentPartitioning(String partitioning) {
		fPartitioning = partitioning;
	}

	/**
	 * @param isInstalled
	 *            The isInstalled to set.
	 */
	public void setInstalled(boolean isInstalled) {
		fIsInstalled = isInstalled;
	}

	private void setLocalProgressMonitor(IProgressMonitor pm) {
		fLocalProgressMonitor = pm;
		List strategyTypes = getStrategyTypes();
		// set on all other strategies
		if (!strategyTypes.isEmpty()) {
			Iterator it = strategyTypes.iterator();
			String type = null;
			while (it.hasNext()) {
				type = (String) it.next();
				if (getReconcilingStrategy(type) instanceof IReconcilingStrategyExtension)
					((IReconcilingStrategyExtension) getReconcilingStrategy(type)).setProgressMonitor(pm);
			}
		}
	}

	/**
	 * Sets the strategy for a given contentType (partitionType)
	 * 
	 * @see org.eclipse.jface.text.reconciler.Reconciler#setReconcilingStrategy(org.eclipse.jface.text.reconciler.IReconcilingStrategy,
	 *      java.lang.String)
	 */
	public void setReconcilingStrategy(IReconcilingStrategy strategy, String contentType) {

		if (fStrategies == null)
			fStrategies = new HashMap();

		if (strategy == null) {
			fStrategies.remove(contentType);
		}
		else {
			fStrategies.put(contentType, strategy);
			if (strategy instanceof IReconcilingStrategyExtension && getLocalProgressMonitor() != null) {
				((IReconcilingStrategyExtension)strategy).setProgressMonitor(getLocalProgressMonitor());
			}	
			strategy.setDocument(fDocument);
		}
		getStrategyTypes().add(contentType);
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconciler#uninstall()
	 */
	public void uninstall() {
		if (isInstalled()) {
			setInstalled(false);
			getLocalProgressMonitor().setCanceled(true);
		}
		setDocument(null);
	}
}