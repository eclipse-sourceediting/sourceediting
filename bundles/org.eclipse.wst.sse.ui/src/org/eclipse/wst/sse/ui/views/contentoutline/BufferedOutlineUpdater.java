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
package org.eclipse.wst.sse.ui.views.contentoutline;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.w3c.dom.Node;


/**
 * Can handle multiple subsequent calls to processNode(..) by buffering them
 * w/ the JobManager. Only one refresh is performed (on the UI Thread) on the
 * minimal affected area of the tree at the end of the batch of updates (after
 * the last update is processed).
 * 
 * @author pavery
 */
public class BufferedOutlineUpdater {

	private Node fSmallestParent = null;

	private Node getSmallestParent() {
		return fSmallestParent;
	}

	/**
	 * @param structuredViewer
	 *            the viewer we are updating
	 * @param the
	 *            specific node that changed
	 */
	public void processNode(final StructuredViewer structuredViewer, Node node) {

		// refresh on structural and "unknown" changes
		// it would be nice to not refresh the viewer if it's not visible
		// but only refresh when it's brought back to the front
		if (structuredViewer.getControl() != null /*
												   * &&
												   * !structuredViewer.getControl().isDisposed() &&
												   * structuredViewer.getControl().isVisible()
												   */) {
			final RefreshOutlineJob refreshJob = new RefreshOutlineJob(ResourceHandler.getString("JFaceNodeAdapter.0"), structuredViewer, getSmallestParent(), node); //$NON-NLS-1$
			refreshJob.addJobChangeListener(new JobChangeAdapter() {
				public void done(IJobChangeEvent event) {
					super.done(event);
					// each job reports the newly calculated minimal affected
					// node
					setSmallestParent(refreshJob.getSmallestParent());
					refreshJob.removeJobChangeListener(this);
				}
			});
			refreshJob.schedule();
		}
	}

	/**
	 * @param node
	 */
	private void setSmallestParent(Node node) {
		fSmallestParent = node;
	}

}
