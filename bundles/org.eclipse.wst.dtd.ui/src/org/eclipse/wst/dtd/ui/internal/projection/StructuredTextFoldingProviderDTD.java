/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.TopLevelNode;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;
import org.eclipse.wst.dtd.core.internal.event.IDTDFileListener;
import org.eclipse.wst.dtd.core.internal.event.NodesEvent;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.projection.IStructuredTextFoldingProvider;
import org.w3c.dom.Node;

/**
 * Updates the projection model of a structured model for DTD.
 */
public class StructuredTextFoldingProviderDTD implements IStructuredTextFoldingProvider, IProjectionListener, IDTDFileListener {
	private final static boolean debugProjectionPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.dtd.ui/projectionperf")); //$NON-NLS-1$ //$NON-NLS-2$

	private class TagProjectionAnnotation extends ProjectionAnnotation {
		private Node fNode;

		public TagProjectionAnnotation(Node node, boolean isCollapsed) {
			super(isCollapsed);
			fNode = node;
		}

		public Node getNode() {
			return fNode;
		}

		public void setNode(Node node) {
			fNode = node;
		}
	}

	private IDocument fDocument;
	private ProjectionViewer fViewer;


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

		long end = System.currentTimeMillis();
		if (debugProjectionPerf)
			System.out.println("StructuredTextFoldingProviderDTD.addAllAnnotations: " + (end - start)); //$NON-NLS-1$
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
					try {
						int startLine = fDocument.getLineOfOffset(start);
						int endLine = fDocument.getLineOfOffset(end);
						// checks if projection start/end region is on the
						// same line
						if ((startLine < endLine) && (endLine + 1 < fDocument.getNumberOfLines())) {
							int offset = fDocument.getLineOffset(startLine);
							int endOffset = fDocument.getLineOffset(endLine + 1);
							pos = new Position(offset, endOffset - offset);
						}
					}
					catch (BadLocationException x) {
						// Logger.log(Logger.WARNING_DEBUG, null, x);
					}
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
	 * @param document
	 * @return
	 */
	private DTDFile getDTDFile() {
		DTDFile dtdFile = null;

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
		return dtdFile;
	}

	/**
	 * Initialize this provider with the correct document. Assumes projection
	 * is enabled. (otherwise, only install would have been called)
	 */
	public void initialize() {
		if (!isInstalled())
			return;

		// remove dtd file listener from old dtd file
		DTDFile file = getDTDFile();
		if (file != null) {
			file.removeDTDFileListener(this);
		}

		// clear out all annotations
		if (fViewer.getProjectionAnnotationModel() != null)
			fViewer.getProjectionAnnotationModel().removeAllAnnotations();

		fDocument = fViewer.getDocument();
		file = getDTDFile();
		if (file != null) {
			// add dtd file listener to new dtd file
			file.addDTDFileListener(this);

			addAllAnnotations(file);
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
		long start = System.currentTimeMillis();

		// recalculate projection annotations for node
		// check if this was even a projectable node to start with
		if (isNodeProjectable(node)) {
			// find the existing annotation
			TagProjectionAnnotation anno = findExistingAnnotation(node);
			// if able to project node see if projection annotation was
			// already created and create new if needed
			Position newPos = createProjectionPosition(node);
			if (newPos != null && anno == null) {
				TagProjectionAnnotation newAnnotation = new TagProjectionAnnotation(node, false);
				// add to map containing annotations to add
				fViewer.getProjectionAnnotationModel().addAnnotation(newAnnotation, newPos);
			}
			// if not able to project node see if projection annotation was
			// already created and remove it
			if (newPos == null && anno != null) {
				fViewer.getProjectionAnnotationModel().removeAnnotation(anno);
			}
		}

		long end = System.currentTimeMillis();
		if (debugProjectionPerf) {
			String nodeName = node != null ? node.getNodeName() : "null"; //$NON-NLS-1$
			System.out.println("StructuredTextFoldingProviderDTD.nodeChanged (" + nodeName + "):" + (end - start)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void nodesAdded(NodesEvent event) {
		long start = System.currentTimeMillis();

		// add projection annotations for all nodes in event.getNodes()
		List nodes = event.getNodes();
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			DTDNode node = (DTDNode) it.next();
			if (isNodeProjectable(node)) {
				// add
				Position newPos = createProjectionPosition(node);
				if (newPos != null) {
					TagProjectionAnnotation newAnnotation = new TagProjectionAnnotation(node, false);
					// add to map containing annotations to add
					fViewer.getProjectionAnnotationModel().addAnnotation(newAnnotation, newPos);
				}
			}
		}

		long end = System.currentTimeMillis();
		if (debugProjectionPerf)
			System.out.println("StructuredTextFoldingProviderDTD.nodesAdded: " + (end - start)); //$NON-NLS-1$
	}

	public void nodesRemoved(NodesEvent event) {
		long start = System.currentTimeMillis();

		// remove projection annotations for all nodes in event.getNodes()
		List nodes = event.getNodes();
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			DTDNode node = (DTDNode) it.next();
			// check if removed node was projectable in the first place
			if (isNodeProjectable(node)) {
				// remove
				TagProjectionAnnotation anno = findExistingAnnotation(node);
				if (anno != null)
					fViewer.getProjectionAnnotationModel().removeAnnotation(anno);
			}
		}

		long end = System.currentTimeMillis();
		if (debugProjectionPerf)
			System.out.println("StructuredTextFoldingProviderDTD.nodesRemoved: " + (end - start)); //$NON-NLS-1$
	}

	public void projectionDisabled() {
		DTDFile file = getDTDFile();
		if (file != null) {
			file.removeDTDFileListener(this);
		}

		fDocument = null;
	}

	public void projectionEnabled() {
		initialize();
	}

	/**
	 * Disconnect this IStructuredTextFoldingProvider from projection viewer
	 */
	public void uninstall() {
		if (isInstalled()) {
			projectionDisabled();

			fViewer.removeProjectionListener(this);
			fViewer = null;
		}
	}
}
