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
package org.eclipse.wst.sse.ui.extension;



import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * TextDropAction
 */
public class TextDropAction extends AbstractDropAction {


	/**
	 * @see AbstractDropAction#run(DropTargetEvent, IExtendedSimpleEditor)
	 */
	public boolean run(DropTargetEvent event, IExtendedSimpleEditor targetEditor) {
		return insert((String) event.data, targetEditor);
	}

}
