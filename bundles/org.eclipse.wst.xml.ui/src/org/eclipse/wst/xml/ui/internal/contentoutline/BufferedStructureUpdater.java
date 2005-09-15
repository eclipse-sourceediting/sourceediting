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
package org.eclipse.wst.xml.ui.internal.contentoutline;

import org.eclipse.jface.viewers.StructuredViewer;
import org.w3c.dom.Node;


/**
 * Can handle multiple subsequent calls to processNode(..) by buffering them
 * w/ a RefreshStructureJob. Only one refresh is performed (on the UI Thread)
 * on the minimal affected area of the tree at the end of the batch of updates
 * (after the last update is processed).
 * 
 * @author pavery
 */
class BufferedStructureUpdater {

	private RefreshStructureJob fRefreshJob = null;

	/**
	 * @param structuredViewer
	 *            the viewer we are updating
	 * @param node
	 *            the specific node that changed
	 */
	public synchronized void processNode(StructuredViewer structuredViewer, Node node) {
		// refresh on structural and "unknown" changes
		// it would be nice to not refresh the viewer if it's not visible
		// but only refresh when it's brought back to the front
		if (structuredViewer.getControl() != null) {
			getRefreshJob().refresh(structuredViewer, node);
		}
	}

	private synchronized RefreshStructureJob getRefreshJob() {
		if (fRefreshJob == null)
			fRefreshJob = new RefreshStructureJob();
		return fRefreshJob;
	}
}
