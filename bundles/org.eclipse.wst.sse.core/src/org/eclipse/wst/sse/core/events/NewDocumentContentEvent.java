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
 * The NewModelEvent is fired when an instance of a IStructuredDocument sets
 * or replaces all of its text. So, in the case of replacement, its a new
 * model, conceptually and practically, even though it is technically the same
 * instance of the IStructuredDocument.
 */
public class NewDocumentContentEvent extends NewDocumentEvent {
	public NewDocumentContentEvent(IStructuredDocument source, Object originalSource) {
		super(source, originalSource);
	}


}
