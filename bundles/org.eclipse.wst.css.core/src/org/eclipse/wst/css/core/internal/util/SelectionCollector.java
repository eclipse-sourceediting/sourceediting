/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.util;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;


/**
 */
public class SelectionCollector extends org.eclipse.wst.css.core.internal.util.AbstractCssTraverser {

	int start, end;
	List selectedNodes;

	/**
	 * SelectionCollector constructor comment.
	 */
	public SelectionCollector() {
		super();
	}

	/**
	 */
	protected void begin(ICSSNode node) {
		selectedNodes = new ArrayList();
	}

	/**
	 */
	public List getSelectedNodes() {
		if (selectedNodes == null)
			return new ArrayList();
		else
			return new ArrayList(selectedNodes);
	}

	/**
	 */
	protected short preNode(ICSSNode node) {
		IndexedRegion iNode = (IndexedRegion) node;
		if (iNode.getStartOffset() <= end && start < iNode.getEndOffset()) {
			if (node.getNodeType() != ICSSNode.STYLESHEET_NODE) {
				IndexedRegion iFirstChild = (IndexedRegion) node.getFirstChild();
				IndexedRegion iLastChild = (IndexedRegion) node.getLastChild();
				if (iFirstChild == null || start < iFirstChild.getStartOffset() || iLastChild.getEndOffset() <= end)
					selectedNodes.add(node);
			}
			return TRAV_CONT;
		}
		if (iNode.getStartOffset() > end)
			return TRAV_STOP;
		else
			return TRAV_PRUNE;
	}

	/**
	 */
	public void setRegion(int newStart, int newEnd) {
		start = newStart;
		end = newEnd;
	}
}
