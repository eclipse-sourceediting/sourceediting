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

import org.eclipse.jface.viewers.StructuredViewer;
import org.w3c.dom.Node;


/**
 * Can handle multiple subsequent calls to processNode(..) by buffering them
 * w/ a RefreshOutlineJob. Only one refresh is performed (on the UI Thread) on the
 * minimal affected area of the tree at the end of the batch of updates (after
 * the last update is processed).
 * 
 * @author pavery
 */
public class BufferedOutlineUpdater {

	private RefreshOutlineJob fRefreshJob = null;
	private StructuredViewer fViewer = null;
	
	/**
	 * @param structuredViewer
	 *       	the viewer we are updating
	 * @param node
	 * 			the specific node that changed
	 */
	public void processNode(final StructuredViewer structuredViewer, Node node) {

		// refresh on structural and "unknown" changes
		// it would be nice to not refresh the viewer if it's not visible
		// but only refresh when it's brought back to the front
		if (structuredViewer.getControl() != null) {
			if(getViewer() == null)
				setViewer(structuredViewer);
			getRefreshJob().refresh(node);
		}
	}

	private RefreshOutlineJob getRefreshJob() {
		if(fRefreshJob == null)
			fRefreshJob = new RefreshOutlineJob(getViewer());
		return fRefreshJob;
	}
	
	private StructuredViewer getViewer() {
		return fViewer;
	}
	
	private void setViewer(StructuredViewer viewer) {
		fViewer = viewer;
	}
}
