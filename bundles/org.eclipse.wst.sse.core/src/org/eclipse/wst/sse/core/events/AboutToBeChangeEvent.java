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
package org.eclipse.wst.sse.core.events;

import org.eclipse.wst.sse.core.text.IStructuredDocument;

/**
 * This event is send to "StructuredDocumentAboutToChange listners". Its perfectly analagous to the 
 * jface DocumentEvent and is provided simply to allow clients to take a pure model point of view,
 * instead of using the document event.
 */
public class AboutToBeChangeEvent extends StructuredDocumentEvent {


	public AboutToBeChangeEvent(IStructuredDocument source, Object originalSource, String changes, int offset, int lengthToReplace) {
		super(source, originalSource, changes, offset, lengthToReplace);
	}
}
