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
package org.eclipse.wst.sse.core.internal.undo;

import org.eclipse.jface.text.IDocument;


public interface IDocumentSelectionMediator {
	/**
	 * Returns the document selection mediator's input document.
	 * 
	 * @return the document selection mediator's input document
	 */
	IDocument getDocument();

	/**
	 * Sets a new selection in the document as a result of an undo operation.
	 * 
	 * UndoDocumentEvent contains the requester of the undo operation, and the
	 * offset and length of the new selection. Implementation of
	 * IDocumentSelectionMediator can check if it's the requester that caused
	 * the new selection, and decide if the new selection should be applied.
	 */
	void undoOperationSelectionChanged(UndoDocumentEvent event);
}
