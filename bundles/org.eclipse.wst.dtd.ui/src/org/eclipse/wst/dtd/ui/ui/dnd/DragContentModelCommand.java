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
package org.eclipse.wst.dtd.ui.ui.dnd;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.swt.dnd.DND;
import org.eclipse.wst.dtd.core.CMBasicNode;
import org.eclipse.wst.dtd.core.CMGroupNode;
import org.eclipse.wst.dtd.core.CMNode;
import org.eclipse.wst.dtd.core.DTDFile;
import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.dtd.core.Element;
import org.eclipse.wst.dtd.ui.DTDEditorPlugin;
import org.eclipse.wst.ui.dnd.DefaultDragAndDropCommand;



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
		}
		return true;
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

	public void execute() {
		DTDNode referenceNode = (DTDNode) target;

		if (referenceNode instanceof CMNode) {
			DTDFile dtdFile = referenceNode.getDTDFile();

			DTDNode parent = (DTDNode) referenceNode.getParentNode();
			dtdFile.getDTDModel().beginRecording(this, DTDEditorPlugin.getResourceString("_UI_MOVE_CONTENT")); //$NON-NLS-1$
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
}
