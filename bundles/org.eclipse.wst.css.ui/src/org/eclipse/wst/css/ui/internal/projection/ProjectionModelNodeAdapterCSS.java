/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.css.ui.internal.projection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;

/**
 * Updates projection annotation model with projection annotations for this
 * adapter node's children
 */
public class ProjectionModelNodeAdapterCSS implements INodeAdapter {
	private final static boolean debugProjectionPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.css.ui/projectionperf")); //$NON-NLS-1$ //$NON-NLS-2$\

	private class TagProjectionAnnotation extends ProjectionAnnotation {
		private boolean fIsVisible = false; /* workaround for BUG85874 */
		private ICSSNode fNode;

		public TagProjectionAnnotation(ICSSNode node, boolean isCollapsed) {
			super(isCollapsed);
			fNode = node;
		}

		public ICSSNode getNode() {
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

	ProjectionModelNodeAdapterFactoryCSS fAdapterFactory;
	private Map fTagAnnotations = new HashMap();

	public ProjectionModelNodeAdapterCSS(ProjectionModelNodeAdapterFactoryCSS factory) {
		fAdapterFactory = factory;
	}

	/**
	 * Create a projection position from the given node. Able to get
	 * projection position if node isNodeProjectable.
	 * 
	 * @param node
	 * @return null if no projection position possible, a Position otherwise
	 */
	private Position createProjectionPosition(ICSSNode node) {
		Position pos = null;
		if (isNodeProjectable(node) && node instanceof IndexedRegion) {
			// IDocument document =
			// fAdapterFactory.getProjectionViewer().getDocument();
			// if (document != null) {
			IndexedRegion inode = (IndexedRegion) node;
			int start = inode.getStartOffset();
			int end = inode.getEndOffset();
			if (start >= 0 && start < end) {
				// region-based
				// extra line when collapsed, but no region increase when
				// add newline
				pos = new Position(start, end - start);
				// try {
				// // line-based
				// // extra line when collapsed, but no region
				// // increase when add newline
				// IRegion startLineRegion =
				// document.getLineInformationOfOffset(start);
				// IRegion endLineRegion =
				// document.getLineInformationOfOffset(end);
				// int startOffset = startLineRegion.getOffset();
				// int endOffset = endLineRegion.getOffset() +
				// endLineRegion.getLength();
				// if (endOffset > startOffset) {
				// pos = new Position(startOffset, endOffset -
				// startOffset);
				// }
				//
				// // line-based
				// // no extra line when collapsed, but region increase
				// // when add newline
				// int startLine = document.getLineOfOffset(start);
				// int endLine = document.getLineOfOffset(end);
				// if (endLine + 1 < document.getNumberOfLines()) {
				// int offset = document.getLineOffset(startLine);
				// int endOffset = document.getLineOffset(endLine + 1);
				// pos = new Position(offset, endOffset - offset);
				// }
				// }
				// catch (BadLocationException x) {
				// Logger.log(Logger.WARNING_DEBUG, null, x);
				// }
			}
			// }
		}
		return pos;
	}

	/**
	 * Find TagProjectionAnnotation for node in the current list of projection
	 * annotations for this adapter
	 * 
	 * @param node
	 * @return TagProjectionAnnotation
	 */
	private TagProjectionAnnotation getExistingAnnotation(ICSSNode node) {
		TagProjectionAnnotation anno = null;

		if ((node != null) && (!fTagAnnotations.isEmpty())) {
			Iterator it = fTagAnnotations.keySet().iterator();
			while (it.hasNext() && anno == null) {
				TagProjectionAnnotation a = (TagProjectionAnnotation) it.next();
				ICSSNode n = a.getNode();
				if (node.equals(n)) {
					anno = a;
				}
			}
		}
		return anno;
	}

	public boolean isAdapterForType(Object type) {
		return type == ProjectionModelNodeAdapterCSS.class;
	}

	/**
	 * Returns true if node is a node type able to fold
	 * 
	 * @param node
	 * @return boolean true if node is projectable, false otherwise
	 */
	private boolean isNodeProjectable(ICSSNode node) {
		if (node != null) {
			short type = node.getNodeType();
			if (type == ICSSNode.STYLERULE_NODE || type == ICSSNode.PAGERULE_NODE || type == ICSSNode.MEDIARULE_NODE || type == ICSSNode.IMPORTRULE_NODE || type == ICSSNode.FONTFACERULE_NODE || type == ICSSNode.CHARSETRULE_NODE)
				return true;
		}
		return false;
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		// check if folding is even enabled, if not, just ignore notifyChanged
		// events
		if (!fAdapterFactory.isActive()) {
			return;
		}

		// workaround for Bug85342 - STRUCTRE_CHANGED is never fired so need
		// to listen for every ADD, REMOVE
		// if ((eventType == INodeNotifier.STRUCTURE_CHANGED) && (notifier
		// instanceof ICSSNode)) {
		if ((eventType == INodeNotifier.ADD || eventType == INodeNotifier.REMOVE) && (notifier instanceof ICSSNode)) {
			updateAdapter((ICSSNode) notifier);
		}
	}

	/**
	 * Update the projection annotation of all the nodes that are children of
	 * node
	 * 
	 * @param node
	 */
	void updateAdapter(ICSSNode node) {
		updateAdapter(node, null);
	}

	/**
	 * Update the projection annotation of all the nodes that are children of
	 * node and adds all projection annotations to viewer (for newly added
	 * viewers)
	 * 
	 * @param node
	 * @param viewer
	 */
	void updateAdapter(ICSSNode node, ProjectionViewer viewer) {
		long start = System.currentTimeMillis();

		Map additions = new HashMap();
		Map projectionAnnotations = new HashMap();

		// go through immediate child nodes and figure out projection
		// model annotations
		if (node != null) {
			ICSSNode childNode = node.getFirstChild();
			while (childNode != null) {
				Position newPos = createProjectionPosition(childNode);
				if (newPos != null) {
					TagProjectionAnnotation newAnnotation = new TagProjectionAnnotation(childNode, false);
					TagProjectionAnnotation existing = getExistingAnnotation(childNode);
					if (existing == null) {
						// add to map containing all annotations for this
						// adapter
						projectionAnnotations.put(newAnnotation, newPos);
						// add to map containing annotations to add
						additions.put(newAnnotation, newPos);
					}
					else {
						// add to map containing all annotations for this
						// adapter
						projectionAnnotations.put(existing, newPos);
						// remove from map containing annotations to delete
						fTagAnnotations.remove(existing);
					}
				}
				childNode = childNode.getNextSibling();
			}

			// in the end, want to delete anything leftover in old list, add
			// everything in additions, and update everything in
			// projectionAnnotations
			ProjectionAnnotation[] oldList = null;
			if (!fTagAnnotations.isEmpty()) {
				oldList = (ProjectionAnnotation[]) fTagAnnotations.keySet().toArray(new ProjectionAnnotation[0]);
			}
			ProjectionAnnotation[] modifyList = null;
			if (!projectionAnnotations.isEmpty()) {
				modifyList = (ProjectionAnnotation[]) projectionAnnotations.keySet().toArray(new ProjectionAnnotation[0]);
			}

			// specifically add all annotations to viewer
			if (viewer != null && !projectionAnnotations.isEmpty()) {
				fAdapterFactory.queueAnnotationModelChanges(node, null, projectionAnnotations, null, viewer);
			}

			// only update when there is something to update
			if ((oldList != null && oldList.length > 0) || (!additions.isEmpty()) || (modifyList != null && modifyList.length > 0))
				fAdapterFactory.queueAnnotationModelChanges(node, oldList, additions, modifyList);
		}

		// save new list of annotations
		fTagAnnotations = projectionAnnotations;

		if (debugProjectionPerf) {
			long end = System.currentTimeMillis();
			String nodeName = node != null ? node.toString() : "null"; //$NON-NLS-1$
			System.out.println("ProjectionModelNodeAdapterCSS.updateAdapter (" + nodeName + "):" + (end - start)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
