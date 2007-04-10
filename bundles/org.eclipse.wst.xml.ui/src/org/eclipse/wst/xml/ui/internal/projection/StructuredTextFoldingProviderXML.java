/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.projection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.projection.IStructuredTextFoldingProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Updates the projection model of a structured model for XML.
 */
public class StructuredTextFoldingProviderXML implements IStructuredTextFoldingProvider, IProjectionListener, ITextInputListener {
	private final static boolean debugProjectionPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.xml.ui/projectionperf")); //$NON-NLS-1$ //$NON-NLS-2$

	private IDocument fDocument;
	private ProjectionViewer fViewer;
	private boolean fProjectionNeedsToBeEnabled = false;
	/**
	 * Maximum number of child nodes to add adapters to (limit for performance
	 * sake)
	 */
	private final int MAX_CHILDREN = 10;
	/**
	 * Maximum number of sibling nodes to add adapters to (limit for
	 * performance sake)
	 */
	private final int MAX_SIBLINGS = 1000;

	/**
	 * Adds an adapter to node and its children
	 * 
	 * @param node
	 * @param childLevel
	 */
	private void addAdapterToNodeAndChildren(Node node, int childLevel) {
		// stop adding initial adapters MAX_CHILDREN levels deep for
		// performance sake
		if ((node instanceof INodeNotifier) && (childLevel < MAX_CHILDREN)) {
			INodeNotifier notifier = (INodeNotifier) node;

			// try and get the adapter for the current node and update the
			// adapter with projection information
			ProjectionModelNodeAdapterXML adapter = (ProjectionModelNodeAdapterXML) notifier.getExistingAdapter(ProjectionModelNodeAdapterXML.class);
			if (adapter != null) {
				adapter.updateAdapter(node, fViewer);
			}
			else {
				// just call getadapter so the adapter is created and
				// automatically initialized
				notifier.getAdapterFor(ProjectionModelNodeAdapterXML.class);
			}
			int siblingLevel = 0;
			Node nextChild = node.getFirstChild();
			while ((nextChild != null) && (siblingLevel < MAX_SIBLINGS)) {
				Node childNode = nextChild;
				nextChild = childNode.getNextSibling();

				addAdapterToNodeAndChildren(childNode, childLevel + 1);
			}
		}
	}

	/**
	 * Goes through every node and adds an adapter onto each for tracking
	 * purposes
	 */
	private void addAllAdapters() {
		long start = System.currentTimeMillis();

		if (fDocument != null) {
			IStructuredModel sModel = null;
			try {
				sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
				if (sModel != null) {
					int startOffset = 0;
					IndexedRegion startNode = sModel.getIndexedRegion(startOffset);
					if (startNode instanceof Node) {
						int siblingLevel = 0;
						Node nextSibling = (Node) startNode;
						while ((nextSibling != null) && (siblingLevel < MAX_SIBLINGS)) {
							Node currentNode = nextSibling;
							nextSibling = currentNode.getNextSibling();

							addAdapterToNodeAndChildren(currentNode, 0);
							++siblingLevel;
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
			System.out.println("StructuredTextFoldingProviderXML.addAllAdapters: " + (end - start)); //$NON-NLS-1$
		}
	}

	/**
	 * Get the ProjectionModelNodeAdapterFactoryXML to use with this provider.
	 * 
	 * @return ProjectionModelNodeAdapterFactoryXML
	 */
	private ProjectionModelNodeAdapterFactoryXML getAdapterFactory(boolean createIfNeeded) {
		long start = System.currentTimeMillis();

		ProjectionModelNodeAdapterFactoryXML factory = null;
		if (fDocument != null) {
			IStructuredModel sModel = null;
			try {
				sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
				if (sModel != null) {
					FactoryRegistry factoryRegistry = sModel.getFactoryRegistry();

					// getting the projectionmodelnodeadapter for the first
					// time
					// so do some initializing
					if (!factoryRegistry.contains(ProjectionModelNodeAdapterXML.class) && createIfNeeded) {
						ProjectionModelNodeAdapterFactoryXML newFactory = new ProjectionModelNodeAdapterFactoryXML();

						// add factory to factory registry
						factoryRegistry.addFactory(newFactory);

						// add factory to propogating adapter
						IDOMModel domModel = (IDOMModel) sModel;
						Document document = domModel.getDocument();
						PropagatingAdapter propagatingAdapter = (PropagatingAdapter) ((INodeNotifier) document).getAdapterFor(PropagatingAdapter.class);
						if (propagatingAdapter != null) {
							propagatingAdapter.addAdaptOnCreateFactory(newFactory);
						}
					}

					// try and get the factory
					factory = (ProjectionModelNodeAdapterFactoryXML) factoryRegistry.getFactoryFor(ProjectionModelNodeAdapterXML.class);
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
			System.out.println("StructuredTextFoldingProviderXML.getAdapterFactory: " + (end - start)); //$NON-NLS-1$
		}
		return factory;
	}

	/**
	 * Initialize this provider with the correct document. Assumes projection
	 * is enabled. (otherwise, only install would have been called)
	 */
	public void initialize() {
		if (!isInstalled()) {
			return;
		}

		// clear out old info
		projectionDisabled();

		fDocument = fViewer.getDocument();

		// set projection viewer on new document's adapter factory
		if (fViewer.getProjectionAnnotationModel() != null) {
			ProjectionModelNodeAdapterFactoryXML factory = getAdapterFactory(true);
			if (factory != null) {
				factory.addProjectionViewer(fViewer);
			}

			addAllAdapters();
		}
		fProjectionNeedsToBeEnabled = false;
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
		ProjectionModelNodeAdapterFactoryXML factory = getAdapterFactory(false);
		if (factory != null) {
			factory.removeProjectionViewer(fViewer);
		}

		// clear out all annotations
		if (fViewer.getProjectionAnnotationModel() != null) {
			fViewer.getProjectionAnnotationModel().removeAllAnnotations();
		}

		removeAllAdapters();

		fDocument = null;
		fProjectionNeedsToBeEnabled = false;
	}

	public void projectionEnabled() {
		initialize();
	}

	/**
	 * Removes an adapter from node and its children
	 * 
	 * @param node
	 * @param level
	 */
	private void removeAdapterFromNodeAndChildren(Node node, int level) {
		if (node instanceof INodeNotifier) {
			INodeNotifier notifier = (INodeNotifier) node;

			// try and get the adapter for the current node and remove it
			INodeAdapter adapter = notifier.getExistingAdapter(ProjectionModelNodeAdapterXML.class);
			if (adapter != null) {
				notifier.removeAdapter(adapter);
			}

			Node nextChild = node.getFirstChild();
			while (nextChild != null) {
				Node childNode = nextChild;
				nextChild = childNode.getNextSibling();

				removeAdapterFromNodeAndChildren(childNode, level + 1);
			}
		}
	}

	/**
	 * Goes through every node and removes adapter from each for cleanup
	 * purposes
	 */
	private void removeAllAdapters() {
		long start = System.currentTimeMillis();

		if (fDocument != null) {
			IStructuredModel sModel = null;
			try {
				sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
				if (sModel != null) {
					int startOffset = 0;
					IndexedRegion startNode = sModel.getIndexedRegion(startOffset);
					if (startNode instanceof Node) {
						Node nextSibling = (Node) startNode;
						while (nextSibling != null) {
							Node currentNode = nextSibling;
							nextSibling = currentNode.getNextSibling();

							removeAdapterFromNodeAndChildren(currentNode, 0);
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
			System.out.println("StructuredTextFoldingProviderXML.removeAllAdapters: " + (end - start)); //$NON-NLS-1$
		}
	}

	public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
		// if folding is enabled and new document is going to be a totally
		// different document, disable projection
		if ((fDocument != null) && (fDocument != newInput)) {
			// disable projection and disconnect everything
			projectionDisabled();
			fProjectionNeedsToBeEnabled = true;
		}
	}

	public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
		// if projection was previously enabled before input document changed
		// and new document is different than old document
		if (fProjectionNeedsToBeEnabled && (fDocument == null) && (newInput != null)) {
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
