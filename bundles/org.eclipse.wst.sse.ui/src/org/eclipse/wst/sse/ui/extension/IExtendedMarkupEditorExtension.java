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
package org.eclipse.wst.sse.ui.extension;

import org.w3c.dom.Node;

/**
 * @deprecated - will be removed in M4
 * 
 * @author nsd
 */

public interface IExtendedMarkupEditorExtension extends IExtendedMarkupEditor {

	/**
	 * Return the DOM Node under the mouse Cursor
	 * 
	 * @return
	 */
	Node getCursorNode();

	/**
	 * Return the line under the mouse Cursor
	 * 
	 * @return
	 */
	int getCursorOffset();
}
