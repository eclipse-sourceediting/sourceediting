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

import org.w3c.dom.Node;


public interface IExtendedMarkupEditorExtension extends IExtendedMarkupEditor {
	/**
	 * Return the line under the mouse Cursor
	 * @return
	 */
	int getCursorOffset();

	/**
	 * Return the DOM Node under the mouse Cursor
	 * @return
	 */
	Node getCursorNode();
}
