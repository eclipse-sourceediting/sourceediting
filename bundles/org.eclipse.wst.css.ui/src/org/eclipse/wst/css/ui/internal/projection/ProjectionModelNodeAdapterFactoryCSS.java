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

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;

public class ProjectionModelNodeAdapterFactoryCSS extends AbstractAdapterFactory {
	/**
	 * List of projection viewers currently associated with this projection
	 * model node adapter factory.
	 */
	private HashMap fProjectionViewers;

	public ProjectionModelNodeAdapterFactoryCSS() {
		this(ProjectionModelNodeAdapterCSS.class);
	}

	public ProjectionModelNodeAdapterFactoryCSS(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	public ProjectionModelNodeAdapterFactoryCSS(Object adapterKey) {
		super(adapterKey);
	}

	protected INodeAdapter createAdapter(INodeNotifier target) {
		ProjectionModelNodeAdapterCSS adapter = null;

		if ((isActive()) && (target instanceof ICSSNode)) {
			ICSSNode node = (ICSSNode) target;
			short type = node.getNodeType();
			// only add for top stylesheet node
			if (type == ICSSNode.STYLESHEET_NODE) {
				adapter = new ProjectionModelNodeAdapterCSS(this);
				adapter.updateAdapter(node);
			}
		}

		return adapter;
	}

	/**
	 * Return true if this factory is currently actively managing projection
	 * 
	 * @return
	 */
	boolean isActive() {
		return (fProjectionViewers != null && !fProjectionViewers.isEmpty());
	}

	/**
	 * Updates projection annotation model if document is not in flux.
	 * Otherwise, queues up the changes to be applied when document is ready.
	 * 
	 * @param node
	 * @param deletions
	 * @param additions
	 * @param modifications
	 */
	void queueAnnotationModelChanges(ICSSNode node, Annotation[] deletions, Map additions, Annotation[] modifications) {
		queueAnnotationModelChanges(node, deletions, additions, modifications, null);
	}

	/**
	 * Updates projection annotation model for a specific projection viewer if
	 * document is not in flux. Otherwise, queues up the changes to be applied
	 * when document is ready.
	 * 
	 * @param node
	 * @param deletions
	 * @param additions
	 * @param modifications
	 * @param viewer
	 */
	void queueAnnotationModelChanges(ICSSNode node, Annotation[] deletions, Map additions, Annotation[] modifications, ProjectionViewer viewer) {
		// create a change object for latest change and add to queue
		ProjectionAnnotationModelChanges newChange = new ProjectionAnnotationModelChanges(node, deletions, additions, modifications);
		if (fProjectionViewers != null) {
			if (viewer != null) {
				ProjectionViewerInformation info = (ProjectionViewerInformation) fProjectionViewers.get(viewer);
				if (info != null) {
					info.queueAnnotationModelChanges(newChange);
				}
			}
			else {
				Iterator infos = fProjectionViewers.values().iterator();
				while (infos.hasNext()) {
					ProjectionViewerInformation info = (ProjectionViewerInformation) infos.next();
					info.queueAnnotationModelChanges(newChange);
				}
			}
		}
	}

	public void release() {
		// go through every projectionviewer and call
		// removeProjectionViewer(viewer);
		if (fProjectionViewers != null) {
			Iterator infos = fProjectionViewers.values().iterator();
			while (infos.hasNext()) {
				ProjectionViewerInformation info = (ProjectionViewerInformation) infos.next();
				info.dispose();
				infos.remove();
			}
			fProjectionViewers = null;
		}
		super.release();
	}

	/**
	 * Adds viewer to list of projection viewers this factory is associated
	 * with
	 * 
	 * @param viewer -
	 *            assumes viewer's document and projection annotation model
	 *            are not null
	 */
	void addProjectionViewer(ProjectionViewer viewer) {
		// remove old entry if it exists
		removeProjectionViewer(viewer);

		if (fProjectionViewers == null) {
			fProjectionViewers = new HashMap();
		}

		// create new object containing projection viewer and its info
		ProjectionViewerInformation info = new ProjectionViewerInformation(viewer);
		fProjectionViewers.put(viewer, info);
		info.initialize();
	}

	/**
	 * Removes the given viewer from the list of projection viewers this
	 * factor is associated with
	 * 
	 * @param viewer
	 */
	void removeProjectionViewer(ProjectionViewer viewer) {
		if (fProjectionViewers != null) {
			// remove entry from list of viewers
			ProjectionViewerInformation info = (ProjectionViewerInformation) fProjectionViewers.remove(viewer);
			if (info != null) {
				info.dispose();
			}
			// if removing last projection viewer, clear out everything
			if (fProjectionViewers.isEmpty()) {
				fProjectionViewers = null;
			}
		}
	}
}
