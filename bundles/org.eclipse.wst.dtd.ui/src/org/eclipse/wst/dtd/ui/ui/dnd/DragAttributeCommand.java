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

import org.eclipse.wst.dtd.core.Attribute;
import org.eclipse.wst.dtd.core.AttributeList;
import org.eclipse.wst.dtd.core.DTDFile;
import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.dtd.ui.DTDEditorPlugin;
import org.eclipse.wst.ui.dnd.DefaultDragAndDropCommand;




public class DragAttributeCommand extends DefaultDragAndDropCommand {

	public DragAttributeCommand(Object target, float location, int operations, int operation, Collection sources) {
		super(target, location, operations, operation, sources);
	}

	public boolean canExecute() {
		Iterator iter = sources.iterator();
		while (iter.hasNext()) {
			Object source = iter.next();
			if (!(source instanceof Attribute)) {
				return false;
			}
		}
		return true;
	}

	public void execute() {
		DTDNode referenceNode = (DTDNode) target;
		DTDFile dtdFile = referenceNode.getDTDFile();
		if (referenceNode instanceof Attribute) {
			dtdFile.getDTDModel().beginRecording(this, DTDEditorPlugin.getResourceString("_UI_MOVE_ATTRIBUTE")); //$NON-NLS-1$
			AttributeList attList = (AttributeList) referenceNode.getParentNode();
			Iterator iter = sources.iterator();
			while (iter.hasNext()) {
				DTDNode node = (DTDNode) iter.next();
				if (node instanceof Attribute) {
					attList.insertIntoModel(this, (Attribute) referenceNode, (Attribute) node, isAfter());
					dtdFile.deleteNode(this, node);
				}
			}
			dtdFile.getDTDModel().endRecording(this);
		}
	}
}
