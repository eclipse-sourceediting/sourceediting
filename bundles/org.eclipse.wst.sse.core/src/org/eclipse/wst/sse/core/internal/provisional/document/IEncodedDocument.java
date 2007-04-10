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
package org.eclipse.wst.sse.core.internal.provisional.document;

import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;

/**
 * This interface is strictly to define important "document properties" not
 * found in IDocument, but not central to "StructuredDocument".
 * 
 * Its not to be be implmented by clients.
 * 
 * @plannedfor 1.0
 */

public interface IEncodedDocument extends IDocument {

	/**
	 * Returns the encoding memento for this document.
	 * 
	 * @return the encoding memento for this document.
	 */
	EncodingMemento getEncodingMemento();

	/**
	 * Returns the preferred line delimiter for this document.
	 */
	String getPreferredLineDelimiter();

	/**
	 * Sets the encoding memento for this document.
	 * 
	 * Is not to be called by clients, only document creation classes.
	 * 
	 * @param localEncodingMemento
	 */
	void setEncodingMemento(EncodingMemento localEncodingMemento);

	/**
	 * Sets the preferredLineDelimiter. Is not to be called by clients, only
	 * document creation classes.
	 * 
	 * @param probableLineDelimiter
	 */
	void setPreferredLineDelimiter(String probableLineDelimiter);

}
