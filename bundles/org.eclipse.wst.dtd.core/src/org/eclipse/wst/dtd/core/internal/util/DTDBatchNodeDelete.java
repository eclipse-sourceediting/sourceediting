/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.dtd.core.internal.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;


public class DTDBatchNodeDelete {
	protected DTDFile dtdFile;

	protected List nodes = new ArrayList();

	public DTDBatchNodeDelete(DTDFile dtdFile) {
		this.dtdFile = dtdFile;
	}

	public void addNode(DTDNode node) {
		// first check if the node is contained by anyone
		for (int i = 0; i < nodes.size(); i++) {
			DTDNode currentNode = (DTDNode) nodes.get(i);

			if (currentNode.containsRange(node.getStartOffset(), node.getEndOffset())) {
				// then no need to add the node to the list to be deleted
				return;
			}

			if (node.getStartOffset() < currentNode.getStartOffset() && node.getEndOffset() <= currentNode.getStartOffset()) {
				nodes.add(i, node);
				return;
			}
		}
		// if we get here, then add it to the end
		nodes.add(node);
	}

	public void deleteNodes(Object requestor) {
		for (int i = nodes.size() - 1; i >= 0; i--) {
			DTDNode node = (DTDNode) nodes.get(i);
			dtdFile.deleteNode(requestor, node);
		}
		nodes.clear();
	}
}
