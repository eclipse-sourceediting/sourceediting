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


package org.eclipse.wst.xml.ui.dnd;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.wst.common.ui.dnd.DragAndDropCommand;
import org.eclipse.wst.common.ui.dnd.DragAndDropManager;
import org.eclipse.wst.common.ui.dnd.ObjectTransfer;
import org.eclipse.wst.common.ui.dnd.ViewerDragAdapter;
import org.eclipse.wst.common.ui.dnd.ViewerDropAdapter;
import org.w3c.dom.Node;

public class XMLDragAndDropManager implements DragAndDropManager {
	public static void addDragAndDropSupport(TreeViewer viewer) {
		int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		Transfer[] transfers = new Transfer[]{ObjectTransfer.getInstance()};
		viewer.addDragSupport(dndOperations, transfers, new ViewerDragAdapter(viewer));
		viewer.addDropSupport(dndOperations, transfers, new ViewerDropAdapter(viewer, new XMLDragAndDropManager()));
	}

	public XMLDragAndDropManager() {
	}

	public DragAndDropCommand createCommand(Object target, float location, int operations, int operation, Collection source) {
		DragAndDropCommand result = null;
		if (target instanceof Node) {
			Node node = (Node) target;
			result = new DragNodeCommand(target, location, operations, operation, source);
		}
		return result;
	}
}
