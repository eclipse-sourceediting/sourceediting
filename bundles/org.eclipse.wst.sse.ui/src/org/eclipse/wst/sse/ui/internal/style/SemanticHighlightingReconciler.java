/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.style;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.style.SemanticHighlightingManager.HighlightedPosition;
import org.eclipse.wst.sse.ui.internal.style.SemanticHighlightingManager.HighlightingStyle;

/**
 * Semantic highlighting reconciler for Structured Source Editors. Based on 
 * org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightingReconciler
 *
 */
public class SemanticHighlightingReconciler implements IReconcilingStrategy, IReconcilingStrategyExtension {

	private IDocument fDocument;

	private StructuredTextEditor fEditor;
	private SemanticHighlightingPresenter fPresenter;

	private List fAddedPositions = new ArrayList();
	private List fRemovedPositions = new ArrayList();
	/** Number of removed positions */
	private int fNOfRemovedPositions;
	
	/** Background job */
	private Job fJob;
	/** Background job lock */
	private final Object fJobLock = new Object();
	
	/** Reconcile operation lock. */
	private final Object fReconcileLock = new Object();
	private boolean fIsReconciling = false;
	/** The semantic highlighting presenter - cache for background thread, only valid during {@link #reconcile(IRegion)} */
	private SemanticHighlightingPresenter fJobPresenter;

	public void install(StructuredTextEditor editor, ISourceViewer sourceViewer, SemanticHighlightingPresenter presenter) {
		fEditor = editor;
		fPresenter = presenter;
	}

	public void uninstall() {
		fEditor = null;
		fPresenter = null;
	}

	public void reconcile(IRegion partition) {
		// ensure at most one thread can be reconciling at any time
		synchronized (fReconcileLock) {
			if (fIsReconciling)
				return;
			else
				fIsReconciling= true;
		}
		fJobPresenter = fPresenter;
		
		try {
			if (fJobPresenter == null)
				return;

			fJobPresenter.setCanceled(false);
		
			startReconcilingPositions();
			IStructuredDocument document = (IStructuredDocument) fDocument;
			IStructuredDocumentRegion[] regions = document.getStructuredDocumentRegions(partition.getOffset(), partition.getLength());
			for (int i = 0; i < regions.length; i++) {
				if (document.containsReadOnly(regions[i].getStartOffset(), regions[i].getLength()))
					addPosition(new Position(regions[i].getStartOffset(), regions[i].getLength()), null, true);
			}
	
			List oldPositions = fRemovedPositions;
			List newPositions = new ArrayList(fNOfRemovedPositions);
			for (int i = 0, n = oldPositions.size(); i < n; i++) {
				Object current = oldPositions.get(i);
				if (current != null)
					newPositions.add(current);
			}
			fRemovedPositions = newPositions;
			
			TextPresentation presentation = null;
			if (!fJobPresenter.isCanceled())
				presentation = fJobPresenter.createPresentation(fAddedPositions, fRemovedPositions);
			if (!fJobPresenter.isCanceled())
				updatePresentation(presentation, fAddedPositions, fRemovedPositions);
	
			stopReconcilingPositions();
		}
		finally {
			fJobPresenter= null;
			synchronized (fReconcileLock) {
				fIsReconciling= false;
			}
		}
	}

	private void addPosition(Position position, HighlightingStyle highlighting, boolean isReadOnly) {
		boolean isExisting = false;
		// TODO: use binary search
		for (int i = 0, n = fRemovedPositions.size(); i < n; i++) {
			HighlightedPosition highlightedPosition = (HighlightedPosition) fRemovedPositions.get(i);
			if (highlightedPosition == null)
				continue;
			if (highlightedPosition.isEqual(position, highlighting)) {
				isExisting = true;
				fRemovedPositions.set(i, null);
				fNOfRemovedPositions--;
				break;
			}
		}
		if (!isExisting) {
			fAddedPositions.add(fPresenter.createHighlightedPosition(position, highlighting, isReadOnly));
		}
	}

	/**
	 * Update the presentation.
	 * 
	 * @param textPresentation
	 *            the text presentation
	 * @param addedPositions
	 *            the added positions
	 * @param removedPositions
	 *            the removed positions
	 */
	private void updatePresentation(TextPresentation textPresentation, List addedPositions, List removedPositions) {
		Runnable runnable = fPresenter.createUpdateRunnable(textPresentation, addedPositions, removedPositions);
		if (runnable == null)
			return;

		StructuredTextEditor editor = fEditor;
		if (editor == null)
			return;

		IWorkbenchPartSite site = editor.getSite();
		if (site == null)
			return;

		Shell shell = site.getShell();
		if (shell == null || shell.isDisposed())
			return;

		Display display = shell.getDisplay();
		if (display == null || display.isDisposed())
			return;

		display.asyncExec(runnable);
	}

	/**
	 * Start reconciling positions.
	 */
	private void startReconcilingPositions() {
		fPresenter.addAllPositions(fRemovedPositions);
		fNOfRemovedPositions = fRemovedPositions.size();
	}

	/**
	 * Stop reconciling positions.
	 */
	private void stopReconcilingPositions() {
		fRemovedPositions.clear();
		fNOfRemovedPositions = 0;
		fAddedPositions.clear();
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile(dirtyRegion);
	}

	public void setDocument(IDocument document) {
		fDocument = document;
		refresh();
	}

	public void initialReconcile() {
		// Do nothing
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
	}
	
	/**
	 * Schedule a background job for reconciling the Semantic Highlighting model.
	 */
	private void scheduleJob() {
		synchronized (fJobLock) {
			final Job oldJob= fJob;
			if (fJob != null) {
				fJob.cancel();
				fJob= null;
			}

			fJob= new Job("Semantic Highlighting Job") {
				protected IStatus run(IProgressMonitor monitor) {
					if (oldJob != null) {
						try {
							oldJob.join();
						} catch (InterruptedException e) {
							Logger.logException(e);
							return Status.CANCEL_STATUS;
						}
					}
					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;

					reconcile(new Region(0, fDocument.getLength()));
					synchronized (fJobLock) {
						// allow the job to be gc'ed
						if (fJob == this)
							fJob= null;
					}
					return Status.OK_STATUS;
				}
			};
			fJob.setSystem(true);
			fJob.setPriority(Job.DECORATE);
			fJob.schedule();
		}
	}

	public void refresh() {
		if (fDocument != null)
			scheduleJob();
	}
}