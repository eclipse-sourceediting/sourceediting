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
 * This event is send to structured document listeners. It is perfectly
 * analagous to its corresponding jface DocumentEvent and is provided simply
 * to allow clients to distinguish the source of the event.
 */
public class AboutToBeChangeEvent extends StructuredDocumentEvent {


	/**
	 * @param document
	 *            document involved in the change
	 * @param originalRequester
	 *            source of original request
	 * @param changes
	 *            the text changes
	 * @param offset
	 *            offset of request
	 * @param lengthToReplace
	 *            amount, if any, of replaced text
	 */
	public AboutToBeChangeEvent(IStructuredDocument document, Object originalRequester, String changes, int offset, int lengthToReplace) {
		super(document, originalRequester, changes, offset, lengthToReplace);
	}
}
