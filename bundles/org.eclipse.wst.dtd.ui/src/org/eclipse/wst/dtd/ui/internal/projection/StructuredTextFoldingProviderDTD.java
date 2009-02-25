/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.projection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.TopLevelNode;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;
import org.eclipse.wst.dtd.core.internal.event.IDTDFileListener;
import org.eclipse.wst.dtd.core.internal.event.NodesEvent;
import org.eclipse.wst.dtd.ui.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.projection.IStructuredTextFoldingProvider;
import org.w3c.dom.Node;

/**
 * Updates the projection model of a structured model for DTD.
 */
public class StructuredTextFoldingProviderDTD implements IStructuredTextFoldingProvider, IProjectionListener, IDTDFileListener, ITextInputListener {
	private final static boolean debugProjectionPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.dtd.ui/projectionperf")); //$NON-NLS-1$ //$NON-NLS-2$

	private class TagProjectionAnnotation extends ProjectionAnnotation {
		private boolean fIsVisible = false; /* workaround for BUG85874 */
		private Node fNode;

		public TagProjectionAnnotation(Node node, boolean isCollapsed) {
			super(isCollapsed);
			fNode = node;
		}

		public Node getNode() {
			return fNode;
		}

		/**
		 * Does not paint hidden annotations. Annotations are hidden when they
		 * only span one line.
		 * 
		 * @see ProjectionAnnotation#paint(org.eclipse.swt.graphics.GC,
		 *      org.eclipse.swt.widgets.Canvas,
		 *      org.eclipse.swt.graphics.Rectangle)
		 */
		public void paint(GC gc, Canvas canvas, Rectangle rectangle) {
			/* workaround for BUG85874 */
			/*
			 * only need to check annotations that are expanded because hidden
			 * annotations should never have been given the chance to
			 * collapse.
			 */
			if (!isCollapsed()) {
				// working with rectangle, so line height
				FontMetrics metrics = gc.getFontMetrics();
				if (metrics != null) {
					// do not draw annotations that only span one line and
					// mark them as not visible
					if ((rectangle.height / metrics.getHeight()) <= 1) {
						fIsVisible = false;
						return;
					}
				}
			}
			fIsVisible = true;
			super.paint(gc, canvas, rectangle);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.text.source.projection.ProjectionAnnotation#markCollapsed()
		 */
		public void markCollapsed() {
			/* workaround for BUG85874 */
			// do not mark collapsed if annotation is not visible
			if (fIsVisible)
				super.markCollapsed();
		}
	}

	/**
	 * Listens to document to be aware of when to update the projection
	 * annotation model.
	 */
	class DocumentListener implements IDocumentListener {
		public void documentAboutToBeChanged(DocumentEvent event) {
			if (fDocument == event.getDocument())
				fIsDocumentChanging = true;
		}

		public void documentChanged(DocumentEvent event) {
			// register a post notification replace so that projection
			// annotation model will be updated after all documentChanged
			// listeners have been notified
			IDocument document = event.getDocument();
			if (document instanceof IDocumentExtension && fDocument == document) {
				if (fViewer != null && fQueuedNodeChanges != null && !fQueuedNodeChanges.isEmpty()) {
					((IDocumentExtension) document).registerPostNotificationReplace(this, new PostDocumentChangedListener());
				}
			}
		}
	}

	/**
	 * Essentially a post document changed listener because it is called after
	 * documentchanged has been fired.
	 */
	class PostDocumentChangedListener implements IDocumentExtension.IReplace {
		public void perform(IDocument document, IDocumentListener owner) {
			applyAnnotationModelChanges();
			fIsDocumentChanging = false;
		}
	}

	/**
	 * Contains node and an indication on how it changed
	 */
	class NodeChange {
		static final int ADD = 1;
		static final int REMOVE = 2;

		private Node fNode;
		private int fChangeType;

		public NodeChange(Node node, int changeType) {
			fNode = node;
			fChangeType = changeType;
		}

		public Node getNode() {
			return fNode;
		}

		public int getChangeType() {
			return fChangeType;
		}
	}

	IDocument fDocument;
	ProjectionViewer fViewer;
	private boolean fProjectionNeedsToBeEnabled = false;
	/**
	 * Listener to fProjectionViewer's document
	 */
	private IDocumentListener fDocumentListener;
	/**
	 * Indicates whether or not document is in the middle of changing
	 */
	boolean fIsDocumentChanging = false;
	/**
	 * List of changed nodes that need to be recalculated
	 */
	List fQueuedNodeChanges = null;

	/**
	 * Processes all the queued node changes and updates projection annotation
	 * model.
	 */
	void applyAnnotationModelChanges() {
		if (fViewer != null && fQueuedNodeChanges != null && !fQueuedNodeChanges.isEmpty()) {
			ProjectionAnnotationModel annotationModel = fViewer.getProjectionAnnotationModel();

			// go through all the pending annotation changes and apply them to
			// the projection annotation model
			while (!fQueuedNodeChanges.isEmpty()) {
				NodeChange changes = (NodeChange) fQueuedNodeChanges.remove(0);
				if (changes.getChangeType() == NodeChange.ADD) {
					// add
					Node node = changes.getNode();
					Position newPos = createProjectionPosition(node);
					if (newPos != null) {
						TagProjectionAnnotation newAnnotation = new TagProjectionAnnotation(node, false);
						// add to map containing annotations to add
						try {
							annotationModel.addAnnotation(newAnnotation, newPos);
						}
						catch (Exception e) {
							// if anything goes wrong, log it and continue
							Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
						}
					}
				}
				else if (changes.getChangeType() == NodeChange.REMOVE) {
					// remove
					Node node = changes.getNode();
					TagProjectionAnnotation anno = findExistingAnnotation(node);
					if (anno != null) {
						try {
							annotationModel.removeAnnotation(anno);
						}
						catch (Exception e) {
							// if anything goes wrong, log it and continue
							Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
						}
					}
				}
			}
			fQueuedNodeChanges = null;
		}
	}

	/**
	 * Goes through every node creates projection annotation if needed
	 * 
	 * @param DTDFile
	 *            assumes file is not null
	 */
	private void addAllAnnotations(DTDFile file) {
		long start = System.currentTimeMillis();

		List nodes = file.getNodes();
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			DTDNode node = (DTDNode) it.next();
			Position newPos = createProjectionPosition(node);
			if (newPos != null) {
				TagProjectionAnnotation newAnnotation = new TagProjectionAnnotation(node, false);
				// add to map containing annotations to add
				fViewer.getProjectionAnnotationModel().addAnnotation(newAnnotation, newPos);
			}
		}

		if (debugProjectionPerf) {
			long end = System.currentTimeMillis();
			System.out.println("StructuredTextFoldingProviderDTD.addAllAnnotations: " + (end - start)); //$NON-NLS-1$
		}
	}

	/**
	 * Create a projection position from the given node. Able to get
	 * projection position if node isNodeProjectable.
	 * 
	 * @param node
	 * @return null if no projection position possible, a Position otherwise
	 */
	private Position createProjectionPosition(Node node) {
		Position pos = null;
		if (isNodeProjectable(node) && node instanceof IndexedRegion) {
			IDocument document = fViewer.getDocument();
			if (document != null) {
				IndexedRegion inode = (IndexedRegion) node;
				int start = inode.getStartOffset();
				int end = inode.getEndOffset();
				if (start >= 0 && start < end) {
					pos = new Position(start, end - start);
				}
			}
		}
		return pos;
	}

	/**
	 * Searches through projection annotation model and retrieves
	 * TagProjectionAnnotation for node
	 * 
	 * @param node
	 * @return TagProjectionAnnotation for node or null if could not be found
	 */
	private TagProjectionAnnotation findExistingAnnotation(Node node) {
		TagProjectionAnnotation anno = null;

		if (node != null) {
			Iterator it = fViewer.getProjectionAnnotationModel().getAnnotationIterator();
			while (it.hasNext() && anno == null) {
				TagProjectionAnnotation a = (TagProjectionAnnotation) it.next();
				if (node.equals(a.getNode()))
					anno = a;
			}
		}
		return anno;
	}

	/**
	 * Get the dtd file for the fDocument
	 * 
	 * @return DTDFile if it exists, null otherwise
	 */
	private DTDFile getDTDFile() {
		DTDFile dtdFile = null;

		if (fDocument != null) {
			IStructuredModel sModel = null;
			try {
				sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
				if (sModel instanceof DTDModelImpl) {
					dtdFile = ((DTDModelImpl) sModel).getDTDFile();
				}
			}
			finally {
				if (sModel != null) {
					sModel.releaseFromRead();
				}
			}
		}
		return dtdFile;
	}

	/**
	 * Initialize this provider with the correct document. Assumes projection
	 * is enabled. (otherwise, only install would have been called)
	 */
	public void initialize() {
		if (!isInstalled())
			return;

		long start = System.currentTimeMillis();
		// remove old info
		projectionDisabled();

		fDocument = fViewer.getDocument();
		DTDFile file = getDTDFile();

		if (fDocument != null && file != null && fViewer.getProjectionAnnotationModel() != null) {
			if (fDocumentListener == null)
				fDocumentListener = new DocumentListener();

			fDocument.addDocumentListener(fDocumentListener);

			// add dtd file listener to new dtd file
			file.addDTDFileListener(this);

			try {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=198304
				// disable redraw while adding all adapters
				fViewer.setRedraw(false);
				addAllAnnotations(file);
			}
			finally {
				fViewer.setRedraw(true);
			}
		}
		fProjectionNeedsToBeEnabled = false;

		if (debugProjectionPerf) {
			long end = System.currentTimeMillis();
			System.out.println("StructuredTextFoldingProviderDTD.initialize: " + (end - start)); //$NON-NLS-1$
		}
	}

	/**
	 * Associate a ProjectionViewer with this IStructuredTextFoldingProvider
	 * 
	 * @param viewer
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

	/**
	 * Returns true if node is a node type able to fold
	 * 
	 * @param node
	 * @return boolean true if node is projectable, false otherwise
	 */
	private boolean isNodeProjectable(Node node) {
		if (node != null) {
			if (node instanceof TopLevelNode)
				return true;
		}
		return false;
	}

	public void nodeChanged(DTDNode node) {
		/*
		 * Don't believe this is necessary (used to need it to only add
		 * projection annotations to elements that span more than one line,
		 * but now just always add projection annotation)
		 */
		// long start = System.currentTimeMillis();
		// // recalculate projection annotations for node
		// // check if this was even a projectable node to start with
		// if (isNodeProjectable(node)) {
		// // find the existing annotation
		// TagProjectionAnnotation anno = findExistingAnnotation(node);
		// // if able to project node see if projection annotation was
		// // already created and create new if needed
		// Position newPos = createProjectionPosition(node);
		// if (newPos != null && anno == null) {
		// TagProjectionAnnotation newAnnotation = new
		// TagProjectionAnnotation(node, false);
		// // add to map containing annotations to add
		// fViewer.getProjectionAnnotationModel().addAnnotation(newAnnotation,
		// newPos);
		// }
		// // if not able to project node see if projection annotation was
		// // already created and remove it
		// if (newPos == null && anno != null) {
		// fViewer.getProjectionAnnotationModel().removeAnnotation(anno);
		// }
		// }
		//
		// long end = System.currentTimeMillis();
		// if (debugProjectionPerf) {
		// String nodeName = node != null ? node.getNodeName() : "null";
		// //$NON-NLS-1$
		// System.out.println("StructuredTextFoldingProviderDTD.nodeChanged ("
		// + nodeName + "):" + (end - start)); //$NON-NLS-1$ //$NON-NLS-2$
		// }
	}

	public void nodesAdded(NodesEvent event) {
		long start = System.currentTimeMillis();

		processNodesEvent(event, NodeChange.ADD);

		if (debugProjectionPerf) {
			long end = System.currentTimeMillis();
			System.out.println("StructuredTextFoldingProviderDTD.nodesAdded: " + (end - start)); //$NON-NLS-1$
		}
	}

	/**
	 * Goes through every changed node in event and add to queue of node
	 * changes that will be recalculated for projection annotation model.
	 * 
	 * @param event
	 * @param changeType
	 */
	private void processNodesEvent(NodesEvent event, int changeType) {
		List nodes = event.getNodes();
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			DTDNode node = (DTDNode) it.next();
			if (isNodeProjectable(node)) {
				if (fQueuedNodeChanges == null) {
					fQueuedNodeChanges = new ArrayList();
				}

				int existingIndex = fQueuedNodeChanges.indexOf(node);
				if (existingIndex > -1) {
					// node is already queued up
					NodeChange existingChange = (NodeChange) fQueuedNodeChanges.remove(existingIndex);
					// don't add if added then removed node or vice versa
					if (existingChange.getChangeType() == changeType) {
						NodeChange newChange = new NodeChange(node, changeType);
						fQueuedNodeChanges.add(newChange);
					}
				}
				else {
					// not queued up yet, so queue node
					NodeChange newChange = new NodeChange(node, changeType);
					fQueuedNodeChanges.add(newChange);
				}
			}
		}
		// if document isn't changing, go ahead and apply it
		if (!fIsDocumentChanging) {
			applyAnnotationModelChanges();
		}
	}

	public void nodesRemoved(NodesEvent event) {
		long start = System.currentTimeMillis();

		processNodesEvent(event, NodeChange.REMOVE);

		if (debugProjectionPerf) {
			long end = System.currentTimeMillis();
			System.out.println("StructuredTextFoldingProviderDTD.nodesRemoved: " + (end - start)); //$NON-NLS-1$
		}
	}

	public void projectionDisabled() {
		DTDFile file = getDTDFile();
		if (file != null) {
			file.removeDTDFileListener(this);
		}

		// remove document listener
		if (fDocumentListener != null && fDocument != null) {
			fDocument.removeDocumentListener(fDocumentListener);
			fDocument = null;

			// clear out list of queued changes since it may no longer be
			// accurate
			if (fQueuedNodeChanges != null) {
				fQueuedNodeChanges.clear();
				fQueuedNodeChanges = null;
			}
		}

		fDocument = null;
		fProjectionNeedsToBeEnabled = false;
	}

	public void projectionEnabled() {
		initialize();
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
			fViewer.addTextInputListener(this);
			fViewer = null;
		}
	}
}
