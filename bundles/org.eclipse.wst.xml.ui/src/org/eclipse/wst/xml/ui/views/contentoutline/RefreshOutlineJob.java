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
package org.eclipse.wst.xml.ui.views.contentoutline;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.w3c.dom.Node;



public class RefreshOutlineJob extends UIJob {


	private INodeNotifier fNodeNotifier;
	private StructuredViewer fStructuredViewer;

	/**
	 * @param jobDisplay
	 * @param name
	 */
	public RefreshOutlineJob(Display jobDisplay, String name, StructuredViewer structuredViewer, INodeNotifier nodeNotifier) {
		super(jobDisplay, name);
		setPriority(Job.SHORT);

		setStructuredViewer(structuredViewer);
		setNodeNotifier(nodeNotifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus runInUIThread(IProgressMonitor monitor) {
		IStatus result = Status.OK_STATUS;
		try {
			Control control = fStructuredViewer.getControl();
			// we should have check before even scheduling this, but even if
			// ok then, need to check again, right before executing.
			if (control != null && !control.isDisposed()) {

				if ((fNodeNotifier instanceof Node) && (((Node) fNodeNotifier).getParentNode() == null)) {
					// refresh whole document
					fStructuredViewer.refresh();
				} else {
					// refresh only the node that's changed
					fStructuredViewer.refresh(fNodeNotifier);
				}
			}
		} catch (Exception e) {
			result = errorStatus(e);
		} finally {
			monitor.done();
		}
		return result;
	}

	/**
	 * @param nodeNotifier
	 */
	private void setNodeNotifier(INodeNotifier nodeNotifier) {
		fNodeNotifier = nodeNotifier;

	}

	/**
	 * @param structuredViewer
	 */
	private void setStructuredViewer(StructuredViewer structuredViewer) {
		fStructuredViewer = structuredViewer;

	}
}
