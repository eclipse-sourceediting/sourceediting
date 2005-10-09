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
package org.eclipse.wst.html.ui.internal.projection;

import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.w3c.dom.Node;

public class ProjectionModelNodeAdapterFactoryHTML extends AbstractAdapterFactory {
	// copies of this class located in:
	// org.eclipse.wst.html.ui.internal.projection
	// org.eclipse.jst.jsp.ui.internal.projection

	private ProjectionViewer fProjectionViewer;

	public ProjectionModelNodeAdapterFactoryHTML(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	public ProjectionModelNodeAdapterFactoryHTML(Object adapterKey) {
		super(adapterKey);
	}

	public ProjectionModelNodeAdapterFactoryHTML() {
		this(ProjectionModelNodeAdapterHTML.class);
	}

	/**
	 * Actually creates an adapter for the parent of target if target is the
	 * "adapt-able" node
	 */
	protected INodeAdapter createAdapter(INodeNotifier target) {
		if ((fProjectionViewer != null) && (target instanceof Node) && ((Node) target).getNodeType() == Node.ELEMENT_NODE) {
			Node node = (Node) target;
			if (isNodeProjectable(node)) {

				// actually work with the parent node to listen for add,
				// delete events
				Node parent = node.getParentNode();
				if (parent instanceof INodeNotifier) {
					INodeNotifier parentNotifier = (INodeNotifier) parent;
					ProjectionModelNodeAdapterHTML parentAdapter = (ProjectionModelNodeAdapterHTML) parentNotifier.getExistingAdapter(ProjectionModelNodeAdapterHTML.class);
					if (parentAdapter == null) {
						// create a new adapter for parent
						parentAdapter = new ProjectionModelNodeAdapterHTML(this);
						parentNotifier.addAdapter(parentAdapter);
					}
					// call update on parent because a new node has just been
					// added
					parentAdapter.updateAdapter(parent);
				}
			}
		}

		return null;
	}

	ProjectionViewer getProjectionViewer() {
		return fProjectionViewer;
	}

	/**
	 * Returns true if node is a node type able to fold
	 * 
	 * @param node
	 * @return boolean true if node is projectable, false otherwise
	 */
	boolean isNodeProjectable(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			String tagName = node.getNodeName();
			// node is only projectable if it is head, body, script, style,
			// table tags
			if (HTML40Namespace.ElementName.HEAD.equalsIgnoreCase(tagName) || HTML40Namespace.ElementName.BODY.equalsIgnoreCase(tagName) || HTML40Namespace.ElementName.SCRIPT.equalsIgnoreCase(tagName) || HTML40Namespace.ElementName.STYLE.equalsIgnoreCase(tagName) || HTML40Namespace.ElementName.TABLE.equalsIgnoreCase((tagName)))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.AbstractAdapterFactory#release()
	 */
	public void release() {
		fProjectionViewer = null;

		super.release();
	}

	void setProjectionViewer(ProjectionViewer viewer) {
		fProjectionViewer = viewer;
	}
}
