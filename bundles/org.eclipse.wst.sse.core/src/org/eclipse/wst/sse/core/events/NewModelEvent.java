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
 * @deprecated - use NewDocumentEvent or NewDocumentContentEvent (this class
 *             will simply be deleted in next version, moving its subclass
 *             "up" to take its place.
 */
public class NewModelEvent extends StructuredDocumentEvent {
	public NewModelEvent(IStructuredDocument source, Object originalSource) {
		super(source, originalSource);
	}
}
