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


package org.eclipse.wst.dtd.ui.dnd;

import java.util.Collection;

import org.eclipse.wst.common.ui.dnd.DragAndDropCommand;
import org.eclipse.wst.common.ui.dnd.DragAndDropManager;
import org.eclipse.wst.dtd.core.Attribute;
import org.eclipse.wst.dtd.core.CMNode;
import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.dtd.core.TopLevelNode;

public class DTDDragAndDropManager implements DragAndDropManager {

	public DragAndDropCommand createCommand(Object target, float location, int operations, int operation, Collection source) {
		if (target instanceof DTDNode) {
			DTDNode node = (DTDNode) target;

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

}
