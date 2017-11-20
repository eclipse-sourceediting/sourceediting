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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.swt.dnd.DND;
import org.eclipse.wst.common.ui.internal.dnd.DefaultDragAndDropCommand;
import org.eclipse.wst.dtd.core.internal.CMBasicNode;
import org.eclipse.wst.dtd.core.internal.CMGroupNode;
import org.eclipse.wst.dtd.core.internal.CMNode;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;
import org.w3c.dom.Node;


public class DragContentModelCommand extends DefaultDragAndDropCommand {

	public DragContentModelCommand(Object target, float location, int operations, int operation, Collection sources) {
		super(target, location, operations, operation, sources);
	}

	public boolean canExecute() {
		if (!(target instanceof CMNode)) {
			return false;
		}

		Iterator iter = sources.iterator();
		while (iter.hasNext()) {
			Object source = iter.next();
			if (!(source instanceof CMNode)) {
				return false;
			}
			// Can not drag parent to its children.
			if(isAncestor((Node)source, (Node)target)) {
				return false;
			}
		}
		return true;
	}

	public void execute() {
		DTDNode referenceNode = (DTDNode) target;

		if (referenceNode instanceof CMNode) {
			DTDFile dtdFile = referenceNode.getDTDFile();

			DTDNode parent = (DTDNode) referenceNode.getParentNode();
			dtdFile.getDTDModel().beginRecording(this, DTDUIMessages._UI_MOVE_CONTENT); //$NON-NLS-1$
			boolean parentIsElement = false;
			Element element = null;
			CMGroupNode group = null;
			if (parent instanceof Element) {
				parentIsElement = true;
				element = (Element) parent;
			}
			else {
				group = (CMGroupNode) parent;
			}

			if (element == null && group == null) {
				// no parent to add to
				return;
			}

			Iterator iter = sources.iterator();
			while (iter.hasNext()) {
				DTDNode node = (DTDNode) iter.next();
				if (node instanceof CMNode) {
					if (parentIsElement) {
						if (element.getContentModel() == node) {
							continue;
						}
						element.replaceContentModel(this, (CMNode) node);
					}
					else {
						if (referenceNode == node || (isAfter() && referenceNode.getNextSibling() == node) || (!isAfter() && node.getNextSibling() == referenceNode)) {
							continue;
						}

						group.insertIntoModel(this, (CMNode) referenceNode, (CMNode) node, isAfter());

					}
					DTDNode nodeParent = (DTDNode) node.getParentNode();
					nodeParent.delete(this, node);
				}
			}
			dtdFile.getDTDModel().endRecording(this);
		}
	}

	public int getFeedback() {
		DTDNode referenceNode = (DTDNode) target;
		if (referenceNode instanceof CMNode) {
			CMNode cmNode = (CMNode) referenceNode;
			if (cmNode.isRootElementContent() && cmNode instanceof CMBasicNode) {
				return DND.FEEDBACK_SELECT;
			}
		}

		return super.getFeedback();
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
