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
package org.eclipse.wst.xml.core.document;



import org.w3c.dom.Text;

/**
 * This interface provides extensions to corresponding DOM interface to enable
 * functions for source editing and incremental parsing.
 * 
 * @since 1.0
 * 
 */

public interface DOMText extends DOMNode, Text {

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
