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
package org.eclipse.wst.sse.core.events;

import org.eclipse.wst.sse.core.text.IStructuredDocument;

/**
 * The NewDocumentEvent is fired when an instance of a IStructuredDocument
 * sets or replaces all of its text.
 * 
 * ISSUE: need to change so this is used for 'set' only.
 * 
 * @since 1.0
 */

public class NewDocumentEvent extends StructuredDocumentEvent {


	/**
	 * @param document
	 *            being changed
	 * @param originalRequester
	 *            source of request
	 */
	public NewDocumentEvent(IStructuredDocument document, Object originalRequester) {
		super(document, originalRequester);
	}

	/**
	 * This doesn't mean quite the same thing as the IStructuredDocument
	 * Events in the super class.
	 */
	public int getLength() {
		return fLength;
	}

	/**
	 * This doesn't mean quite the same thing as the IStructuredDocument
	 * Events in the super class. It always will return zero.
	 */
	public int getOffset() {
		return 0;
	}

	public String getText() {
		String results = getStructuredDocument().getText();
		return results;
	}
}
