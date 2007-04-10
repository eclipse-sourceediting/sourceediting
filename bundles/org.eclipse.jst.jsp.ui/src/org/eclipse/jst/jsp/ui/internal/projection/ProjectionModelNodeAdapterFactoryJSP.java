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
package org.eclipse.jst.jsp.ui.internal.projection;

import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.w3c.dom.Node;

public class ProjectionModelNodeAdapterFactoryJSP extends ProjectionModelNodeAdapterFactoryHTML {
	public ProjectionModelNodeAdapterFactoryJSP() {
		this(ProjectionModelNodeAdapterJSP.class);
	}

	public ProjectionModelNodeAdapterFactoryJSP(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	public ProjectionModelNodeAdapterFactoryJSP(Object adapterKey) {
		super(adapterKey);
	}

	/**
	 * Actually creates an adapter for the parent of target if target is the
	 * "adapt-able" node
	 */
	protected INodeAdapter createAdapter(INodeNotifier target) {
		if ((isActive()) && (target instanceof Node) && ((Node) target).getNodeType() == Node.ELEMENT_NODE) {
			Node node = (Node) target;
			if (isNodeProjectable(node)) {

				// actually work with the parent node to listen for add,
				// delete events
				Node parent = node.getParentNode();
				if (parent instanceof INodeNotifier) {
					INodeNotifier parentNotifier = (INodeNotifier) parent;
					ProjectionModelNodeAdapterJSP parentAdapter = (ProjectionModelNodeAdapterJSP) parentNotifier.getExistingAdapter(ProjectionModelNodeAdapterJSP.class);
					if (parentAdapter == null) {
						// create a new adapter for parent
						parentAdapter = new ProjectionModelNodeAdapterJSP(this);
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

	/**
	 * Returns true if node is a node type able to fold
	 * 
	 * @param node
	 * @return boolean true if node is projectable, false otherwise
	 */
	boolean isNodeProjectable(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			String tagName = node.getNodeName();
			// node is only projectable if it is jsp scriptlet tag
			if (JSP11Namespace.ElementName.SCRIPTLET.equalsIgnoreCase(tagName))
				return true;
		}
		return false;
	}
}
