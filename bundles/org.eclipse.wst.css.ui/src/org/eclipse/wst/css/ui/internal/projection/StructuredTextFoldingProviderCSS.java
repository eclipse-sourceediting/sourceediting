/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.internal.projection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.projection.IStructuredTextFoldingProvider;

/**
 * Updates the projection model of a structured model for CSS.
 */
public class StructuredTextFoldingProviderCSS implements IStructuredTextFoldingProvider, IProjectionListener, ITextInputListener {
	private final static boolean debugProjectionPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.css.ui/projectionperf")); //$NON-NLS-1$ //$NON-NLS-2$\

	private IDocument fDocument;
	private ProjectionViewer fViewer;
	private boolean fProjectionNeedsToBeEnabled = false;

	/**
	 * Just add adapter to top stylesheet node. This adapter will track
	 * children addition/deletion.
	 */
	private void addAllAdapters() {
		long start = System.currentTimeMillis();

		if (fDocument != null) {
			IStructuredModel sModel = null;
			try {
				sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
				if (sModel instanceof ICSSModel) {
					ICSSModel cssModel = (ICSSModel) sModel;
					ICSSDocument cssDoc = cssModel.getDocument();
					if (cssDoc instanceof INodeNotifier) {
						INodeNotifier notifier = (INodeNotifier) cssDoc;
						ProjectionModelNodeAdapterCSS adapter = (ProjectionModelNodeAdapterCSS) notifier.getExistingAdapter(ProjectionModelNodeAdapterCSS.class);
						if (adapter != null) {
							adapter.updateAdapter(cssDoc, fViewer);
						}
						else {
							// just call getadapter so the adapter is created
							// and
							// automatically initialized
							notifier.getAdapterFor(ProjectionModelNodeAdapterCSS.class);
						}
					}
				}
			}
			finally {
				if (sModel != null) {
					sModel.releaseFromRead();
				}
			}
		}

		if (debugProjectionPerf) {
			long end = System.currentTimeMillis();
			System.out.println("StructuredTextFoldingProviderCSS.addAllAdapters: " + (end - start)); //$NON-NLS-1$
		}
	}

	/**
	 * Get the ProjectionModelNodeAdapterFactoryCSS to use with this provider.
	 * 
	 * @return ProjectionModelNodeAdapterFactoryCSS
	 */
	private ProjectionModelNodeAdapterFactoryCSS getAdapterFactory(boolean createIfNeeded) {
		ProjectionModelNodeAdapterFactoryCSS factory = null;
		if (fDocument != null) {
			IStructuredModel sModel = null;
			try {
				sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
				if (sModel != null) {
					FactoryRegistry factoryRegistry = sModel.getFactoryRegistry();

					// getting the projectionmodelnodeadapter for the first
					// time
					// so do some initializing
					if (!factoryRegistry.contains(ProjectionModelNodeAdapterCSS.class) && createIfNeeded) {
						ProjectionModelNodeAdapterFactoryCSS newFactory = new ProjectionModelNodeAdapterFactoryCSS();

						// add factory to factory registry
						factoryRegistry.addFactory(newFactory);
					}

					// try and get the factory
					factory = (ProjectionModelNodeAdapterFactoryCSS) factoryRegistry.getFactoryFor(ProjectionModelNodeAdapterCSS.class);
				}
			}
			finally {
				if (sModel != null)
					sModel.releaseFromRead();
			}
		}
		return factory;
	}

	/**
	 * Initialize this provider with the correct document. Assumes projection
	 * is enabled. (otherwise, only install would have been called)
	 */
	public void initialize() {
		if (!isInstalled())
			return;

		long start = System.currentTimeMillis();
		// clear out old info
		projectionDisabled();

		fDocument = fViewer.getDocument();

		// set projection viewer on new document's adapter factory
		if (fViewer.getProjectionAnnotationModel() != null) {
			ProjectionModelNodeAdapterFactoryCSS factory = getAdapterFactory(true);
			if (factory != null) {
				factory.addProjectionViewer(fViewer);
			}

			try {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=198304
				// disable redraw while adding all adapters
				fViewer.setRedraw(false);
				addAllAdapters();
			}
			finally {
				fViewer.setRedraw(true);
			}
		}
		fProjectionNeedsToBeEnabled = false;

		if (debugProjectionPerf) {
			long end = System.currentTimeMillis();
			System.out.println("StructuredTextFoldingProviderCSS.initialize: " + (end - start)); //$NON-NLS-1$
		}
	}

	/**
	 * Associate a ProjectionViewer with this IStructuredTextFoldingProvider
	 * 
	 * @param viewer -
	 *            assumes not null
	 */
	public void install(ProjectionViewer viewer) {
		// uninstall before trying to install new viewer
		if (isInstalled()) {
			uninstall();
		}
		fViewer = viewer;
		fViewer.addProjectionListener(this);
		fViewer.addTextInputListener(this);
	}

	private boolean isInstalled() {
		return fViewer != null;
	}

	public void projectionDisabled() {
		ProjectionModelNodeAdapterFactoryCSS factory = getAdapterFactory(false);
		if (factory != null) {
			factory.removeProjectionViewer(fViewer);
		}

		// clear out all annotations
		if (fViewer.getProjectionAnnotationModel() != null)
			fViewer.getProjectionAnnotationModel().removeAllAnnotations();

		removeAllAdapters();

		fDocument = null;
		fProjectionNeedsToBeEnabled = false;
	}

	public void projectionEnabled() {
		initialize();
	}

	/**
	 * Removes adapter from top stylesheet node
	 */
	private void removeAllAdapters() {
		long start = System.currentTimeMillis();

		if (fDocument != null) {
			IStructuredModel sModel = null;
			try {
				sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
				if (sModel instanceof ICSSModel) {
					ICSSModel cssModel = (ICSSModel) sModel;
					ICSSDocument cssDoc = cssModel.getDocument();
					if (cssDoc instanceof INodeNotifier) {
						INodeNotifier notifier = (INodeNotifier) cssDoc;
						INodeAdapter adapter = notifier.getExistingAdapter(ProjectionModelNodeAdapterCSS.class);
						if (adapter != null) {
							notifier.removeAdapter(adapter);
						}
					}
				}
			}
			finally {
				if (sModel != null) {
					sModel.releaseFromRead();
				}
			}
		}

		if (debugProjectionPerf) {
			long end = System.currentTimeMillis();
			System.out.println("StructuredTextFoldingProviderCSS.addAllAdapters: " + (end - start)); //$NON-NLS-1$
		}
	}

	public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
		// if folding is enabled and new document is going to be a totally
		// different document, disable projection
		if (fDocument != null && fDocument != newInput) {
			// disable projection and disconnect everything
			projectionDisabled();
			fProjectionNeedsToBeEnabled = true;
		}
	}

	public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
		// if projection was previously enabled before input document changed
		// and new document is different than old document
		if (fProjectionNeedsToBeEnabled && fDocument == null && newInput != null) {
			projectionEnabled();
			fProjectionNeedsToBeEnabled = false;
		}
	}

	/**
	 * Disconnect this IStructuredTextFoldingProvider from projection viewer
	 */
	public void uninstall() {
		if (isInstalled()) {
			projectionDisabled();

			fViewer.removeProjectionListener(this);
			fViewer.removeTextInputListener(this);
			fViewer = null;
		}
	}
}
