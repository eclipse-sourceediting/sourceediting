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
 * The NewDocumentContentEvent is fired when an instance of a
 * IStructuredDocument replaces all of its text.
 * 
 * ISSUE: not currently used, but there's still some efficiencies to be had so
 * plan to implement.
 * 
 * @plannedfor 1.0
 */
public class NewDocumentContentEvent extends NewDocumentEvent {
	/**
	 * Creates an instance of this event.
	 * 
	 * @param document
	 *            the document being changed
	 * @param originalRequester
	 *            the original requester of the change
	 */
	public NewDocumentContentEvent(IStructuredDocument document, Object originalRequester) {
		super(document, originalRequester);
	}


}
