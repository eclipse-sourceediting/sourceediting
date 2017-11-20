/*******************************************************************************
 * Copyright (c) 2001, 2013 IBM Corporation and others.
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


package org.eclipse.wst.dtd.ui.internal.dnd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.common.ui.internal.dnd.DragAndDropCommand;
import org.eclipse.wst.common.ui.internal.dnd.DragAndDropManager;
import org.eclipse.wst.dtd.core.internal.Attribute;
import org.eclipse.wst.dtd.core.internal.CMNode;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.TopLevelNode;
import org.w3c.dom.Node;

public class DTDDragAndDropManager implements DragAndDropManager {

	public DragAndDropCommand createCommand(Object target, float location, int operations, int operation, Collection source) {
		if (target instanceof DTDNode) {
			DTDNode node = (DTDNode) target;

			source = mergeSource(source);
			if (node instanceof TopLevelNode) {
				return new DragTopLevelNodesCommand(target, location, operations, operation, source);
			}
			if (node instanceof Attribute) {
				return new DragAttributeCommand(target, location, operations, operation, source);
			}
			if (node instanceof CMNode) {
				return new DragContentModelCommand(target, location, operations, operation, source);
			}

		}
		return null;
	}

	/**
	 * The source is merged only with the ancestor node.
	 */
	private Collection mergeSource(Collection collection) {
		List result = new ArrayList();
		for(Iterator it = collection.iterator(); it.hasNext();) {
			Node node = (Node) it.next();
			if(result.contains(node)) {
				continue ;
			}

			boolean isAdd = true;
			for(int i = result.size() - 1; i >= 0; i--) {
				Node addedNode = (Node)result.get(i);
				if(isAncestor(node, addedNode)) {
					if(isAdd) {
						// Replace with the ancestor node
						result.set(i, node);
						isAdd = false;
					}
					else {
						// Delete the child node
						result.remove(i);
					}
				}
				else if(!isAdd && isAncestor(addedNode, node)) {
					// Do not add child nodes
					isAdd = false;
					break ; 
				}
			}
			if(isAdd) {
				result.add(node);
			}
		}
		return result;
	}

	/**
	 * Returns with true if node1 is an ancestor of node2.
	 */
	private boolean isAncestor(Node node1, Node node2) {
		boolean result = false;
		for (Node parent = node2; parent != null; parent = parent.getParentNode()) {
			if (parent == node1) {
				result = true;
				break;
			}
		}
		return result;
	}
}
