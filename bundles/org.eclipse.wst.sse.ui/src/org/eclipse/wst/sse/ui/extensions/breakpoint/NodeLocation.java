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
package org.eclipse.wst.sse.ui.extensions.breakpoint;


public interface NodeLocation {
	/**
	 * Returns the document end offset of the end tag, -1 of there is no end tag
	 * @return
	 */
	int getEndTagEndOffset();

	/**
	 * Returns the document start offset of the end tag, -1 of there is no end tag
	 * @return
	 */
	int getEndTagStartOffset();

	/**
	 * Returns the document end offset of the start tag, -1 of there is no start tag
	 * @return
	 */
	int getStartTagEndOffset();

	/**
	 * Returns the document start offset of the start tag, -1 of there is no start tag
	 * @return
	 */
	int getStartTagStartOffset();
}
