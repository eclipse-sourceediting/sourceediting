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
package org.eclipse.wst.xml.core.document;



import org.w3c.dom.Text;

/**
 */
public interface XMLText extends XMLNode, Text {

	/**
	 * Appends the content of the text node
	 */
	void appendText(Text text);

	/**
	 * Inserts the content of the text node at the specified position
	 */
	void insertText(Text text, int offset);

	/**
	 */
	boolean isInvalid();

	/**
	 */
	boolean isWhitespace();
}
