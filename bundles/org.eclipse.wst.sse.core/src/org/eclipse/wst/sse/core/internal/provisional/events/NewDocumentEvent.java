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
package org.eclipse.wst.sse.core.internal.provisional.events;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

/**
 * The NewDocumentEvent is fired when an instance of a IStructuredDocument
 * sets or replaces all of its text.
 * 
 * ISSUE: need to change so this is used for 'set' only.
 * 
 * @plannedfor 1.0
 */
public class NewDocumentEvent extends StructuredDocumentEvent {


	/**
	 * Creates a new instance of the NewDocumentEvent.
	 * 
	 * @param document
	 *            being changed
	 * @param originalRequester
	 *            source of request
	 */
	public NewDocumentEvent(IStructuredDocument document, Object originalRequester) {
		super(document, originalRequester);
	}

	/**
	 * This returns the length of the new document.
	 * 
	 * @return int returns the length of the new document.
	 */
	public int getLength() {
		return getStructuredDocument().getLength();
	}

	/**
	 * This doesn't mean quite the same thing as the IStructuredDocument
	 * Events in the super class. It always will return zero.
	 * 
	 * @return int for a newDocument, the offset of is always 0
	 */
	public int getOffset() {
		return 0;
	}

	/**
	 * For a new document, the text involved is the complete text.
	 * 
	 * @return String the text that is the complete text of the documnet.
	 */
	public String getText() {
		String results = getStructuredDocument().getText();
		return results;
	}
}
