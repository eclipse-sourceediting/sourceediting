/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
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
	 */
	public boolean contains(int testPosition) {
		return getStartOffset() <= testPosition && testPosition <= getEndOffset();
	}

	public boolean contains(Object child) {
		return fTarget.getNodes().contains(child);
	}

	/**
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass().getName() + ":" + fTarget.toString(); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public NodeList getTarget() {
		return fTarget;
	}
}
