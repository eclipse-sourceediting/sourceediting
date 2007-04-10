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
package org.eclipse.wst.dtd.ui.internal.dnd;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.wst.common.ui.internal.dnd.DefaultDragAndDropCommand;
import org.eclipse.wst.dtd.core.internal.Attribute;
import org.eclipse.wst.dtd.core.internal.AttributeList;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;



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
			dtdFile.getDTDModel().beginRecording(this, DTDUIMessages._UI_MOVE_ATTRIBUTE); //$NON-NLS-1$
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
