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
package org.eclipse.wst.dtd.ui.views.contentoutline;

import java.util.List;

import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.dtd.core.NodeList;
import org.eclipse.wst.sse.core.IndexedRegion;


/* package */
class IndexedNodeList implements IndexedRegion {
	public NodeList fTarget;

	public IndexedNodeList(NodeList target) {
		fTarget = target;
	}

	/**
	 * @see org.eclipse.wst.sse.core.IndexedRegion#contains(int)
	 */
	public boolean contains(int testPosition) {
		return getStartOffset() <= testPosition && testPosition <= getEndOffset();
	}

	public boolean contains(Object child) {
		return fTarget.getNodes().contains(child);
	}

	/**
	 * @see org.eclipse.wst.sse.core.IndexedRegion#getEndOffset()
	 */
	public int getEndOffset() {
		int end = 0;
		List nodes = fTarget.getNodes();
		for (int i = 0; i < nodes.size(); i++) {
			int thisEnd = ((DTDNode) nodes.get(i)).getEndOffset();
			if (end < thisEnd)
				end = thisEnd;
		}
		return end;
	}

	/**
	 * @see org.eclipse.wst.sse.core.IndexedRegion#getStartOffset()
	 */
	public int getStartOffset() {
		int start = -1;
		List nodes = fTarget.getNodes();
		for (int i = 0; i < nodes.size(); i++) {
			int thisStart = ((DTDNode) nodes.get(i)).getStartOffset();
			if (start > thisStart || start < 0)
				start = thisStart;
		}
		if (start < 0)
			start = 0;
		return start;
	}

	/**
	 * @return
	 */
	public NodeList getTarget() {
		return fTarget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass().getName() + ":" + fTarget.toString(); //$NON-NLS-1$
	}
}
