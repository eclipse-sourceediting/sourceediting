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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.w3c.dom.Node;


public class RefreshOutlineJob extends Job {

	/**
	 * Tree refresh must be done on UI Thread so UIJob is used
	 */
	public class RefreshJob extends UIJob {
		StructuredViewer viewer = null;

		public RefreshJob(StructuredViewer structuredViewer) {
			// might need a different label, like "Refreshing Outline"
			super(ResourceHandler.getString("JFaceNodeAdapter.0"));
			this.viewer = structuredViewer;
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {

			IStatus status = Status.OK_STATUS;
			//monitor.beginTask("", IProgressMonitor.UNKNOWN);
			try {
				Control control = this.viewer.getControl();
				// we should have check before even scheduling this, but even
				// if
				// ok then, need to check again, right before executing.
				if (control != null && !control.isDisposed()) {
					Node smallest = getSmallestParent();
					if (smallest == null || smallest.getParentNode() == null) {
						// refresh whole document
						this.viewer.refresh();
						if (DEBUG)
							System.out.println("refreshing viewer (entire tree)");
					} else {
						// refresh only the node that's changed
						this.viewer.refresh(smallest);
						if (DEBUG)
							System.out.println("refreshing viewer on (" + smallest + ")");
					}
				}
			} catch (Exception e) {
				status = errorStatus(e);
			} finally {
				monitor.done();
			}
			return status;
		}
	}

	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.sse.ui/debug/outline"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	private Node fPossibleParent = null;
	private Set fPruned = null;
	private Node fSmallestParent = null;

	// end RefreshJob class

	private StructuredViewer fStructuredViewer;

	/**
	 * name is the name of the Job (like which out line is being updated)
	 * possibleParent cannot be null.
	 * 
	 * @param name
	 * @param structuredViewer
	 * @param currentParent
	 * @param possibleParent
	 */
	public RefreshOutlineJob(String name, StructuredViewer structuredViewer, Node currentParent, Node possibleParent) {
		super(name);

		setPriority(Job.SHORT);
		setSystem(true);

		fPruned = new HashSet();
		setPossibleParent(possibleParent);
		if (currentParent == null)
			setSmallestParent(fPossibleParent);
		setStructuredViewer(structuredViewer);
	}

	// each pruned node represents a subtree
	private void addPruned(Node n) {
		if (DEBUG)
			System.out.println("(-) pruned node: " + n.getNodeName());
		fPruned.add(n);
	}


	/**
	 * Calculates minimal common parent between the previous parent (passed in
	 * constructor) and the notifier (also passed in the constructor), and
	 * calls setSmallsetParent(...) with the result.
	 */
	private void calculateSmallestCommonParent() {
		// quick checks
		if (getPossibleParent() == null)
			return;
		// document root
		if (getPossibleParent().getParentNode() == null) {
			setSmallestParent(getPossibleParent());
			if (DEBUG)
				System.out.println("*** found smallest parent for refresh: " + getPossibleParent());
			return;
		}
		// we already have document root
		if (getSmallestParent() != null && getSmallestParent().getParentNode() == null) {
			if (DEBUG)
				System.out.println("*** already have smallest parent for refresh: " + getSmallestParent());
			return;
		}

		// check one side
		if (!contains(getPossibleParent(), getSmallestParent())) {

			addPruned(getPossibleParent());
			// check other side
			if (!contains(getSmallestParent(), getPossibleParent())) {

				addPruned(getSmallestParent());
				// keep climbing up and searching now
				Node parent = getSmallestParent();
				while (parent != null) {
					// check subtree
					if (contains(parent, getPossibleParent())) {
						// we found it, it's parent
						setSmallestParent(parent);
						if (DEBUG)
							System.out.println("*** found smallest parent for refresh: " + parent);
						break;
					} else {
						// not in this subtree
						addPruned(parent);
					}
					parent = parent.getParentNode();
				}
			} else {
				// its smaller parent (we already have it)
			}
		} else {
			// it's the new possible one
			if (DEBUG)
				System.out.println("*** found smallest parent for refresh: " + getPossibleParent());
			setSmallestParent(getPossibleParent());
		}
	}

	/**
	 * @return if the root is parent of possible, return true, otherwise
	 *         return false
	 */
	protected boolean contains(Node root, Node possible) {

		if (DEBUG) {
			System.out.println("==============================================================================================================");
			System.out.println("recursive call w/ root: " + root.getNodeName() + " and possible " + possible);
			System.out.println("--------------------------------------------------------------------------------------------------------------");
		}

		// can't contain the parent if it's null
		if (root == null)
			return false;
		// it's the root node
		if (root.getParentNode() == null)
			return true;
		// no parent set yet
		if (getSmallestParent() == null)
			return true;


		// depth first
		Node current = root;
		// loop siblings
		while (current != null) {

			if (DEBUG)
				System.out.println("   -> iterating sibling (" + current.getNodeName() + ")");

			// found it
			if (possible.equals(current)) {
				if (DEBUG)
					System.out.println("   !!! found: " + possible.getNodeName() + " in subtree for: " + root.getNodeName());
				return true;
			}
			// drop one level deeper if necessary
			if (current.getFirstChild() != null && !isPruned(current.getFirstChild())) {
				return contains(current.getFirstChild(), possible);
			}
			// already checked subtree, eliminate
			addPruned(current);
			current = current.getNextSibling();
		}

		// never found it
		return false;
	}

	private Node getPossibleParent() {
		return fPossibleParent;
	}

	public Node getSmallestParent() {
		return fSmallestParent;
	}

	private StructuredViewer getStructuredViewer() {
		return fStructuredViewer;
	}

	/**
	 * @return true if this job is the last RefreshOutlineJob running
	 */
	private boolean iAmLast() {

		IJobManager jobManager = Platform.getJobManager();
		Job[] jobs = jobManager.find(null);
		Job job = null;
		for (int i = 0; i < jobs.length; i++) {
			job = jobs[i];
			if (job instanceof RefreshOutlineJob || job instanceof RefreshOutlineJob.RefreshJob) {

				int state = job.getState();
				if (state == Job.RUNNING || state == Job.WAITING) {
					if (jobs[i] != this) {
						if (DEBUG)
							System.out.println("... still waiting for another refresh job: " + jobs[i]);
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean isPruned(Node n) {
		Iterator it = fPruned.iterator();
		while (it.hasNext()) {
			if (it.next().equals(n))
				return true;
		}
		return false;
	}

	/**
	 * perform on UI Thread
	 */
	public void refreshViewer() {

		Job refresh = new RefreshJob(getStructuredViewer());
		refresh.setPriority(Job.SHORT);
		refresh.setSystem(true);
		refresh.schedule();
	}

	/**
	 * perform on worker thread
	 */
	public IStatus run(IProgressMonitor monitor) {

		IStatus result = Status.OK_STATUS;
		// monitor.beginTask("", IProgressMonitor.UNKNOWN);
		try {
			calculateSmallestCommonParent();

			// only the last one should update the outline
			// in case many requests came in consecutively
			if (iAmLast()) {
				refreshViewer();
			}
		} catch (Exception e) {
			result = new Status(IStatus.ERROR, EditorPlugin.ID, IStatus.ERROR, "Error updating outline", e);
		} finally {
			monitor.done();
		}
		return result;
	}

	private void setPossibleParent(Node possibleParent) {
		fPossibleParent = possibleParent;
	}

	private void setSmallestParent(Node newParent) {
		fSmallestParent = newParent;
	}

	/**
	 * @param structuredViewer
	 */
	private void setStructuredViewer(StructuredViewer structuredViewer) {
		fStructuredViewer = structuredViewer;

	}
}
