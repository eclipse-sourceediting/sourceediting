/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.undo;

import org.eclipse.jface.text.IDocument;

public class UndoDocumentEvent {
	private IDocument fDocument;
	private int fLength;
	private int fOffset;
	private IDocumentSelectionMediator fRequester;

	public UndoDocumentEvent(IDocumentSelectionMediator requester, IDocument document, int offset, int length) {
		fRequester = requester;
		fDocument = document;
		fOffset = offset;
		fLength = length;
	}

	public IDocument getDocument() {
		return fDocument;
	}

	public int getLength() {
		return fLength;
	}

	public int getOffset() {
		return fOffset;
	}

	public IDocumentSelectionMediator getRequester() {
		return fRequester;
	}
}
