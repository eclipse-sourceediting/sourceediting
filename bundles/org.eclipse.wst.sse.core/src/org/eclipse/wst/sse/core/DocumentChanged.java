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
package org.eclipse.wst.sse.core;

import org.eclipse.wst.sse.core.text.IStructuredDocument;

public class DocumentChanged extends ModelLifecycleEvent {

	private IStructuredDocument fOldDocument;
	private IStructuredDocument fNewDocument;

	protected DocumentChanged() {

		super(ModelLifecycleEvent.MODEL_DOCUMENT_CHANGED);

	}

	protected DocumentChanged(int additionalType, IStructuredModel model) {

		super(model, ModelLifecycleEvent.MODEL_DOCUMENT_CHANGED | additionalType);

	}

	public DocumentChanged(int additionalType, IStructuredModel model, IStructuredDocument oldDocument, IStructuredDocument newDocument) {

		this(additionalType, model);
		fOldDocument = oldDocument;
		fNewDocument = newDocument;
	}

	public IStructuredDocument getNewDocument() {

		return fNewDocument;
	}

	void setNewDocument(IStructuredDocument newDocument) {

		fNewDocument = newDocument;
	}

	public IStructuredDocument getOldDocument() {

		return fOldDocument;
	}

	void setOldDocument(IStructuredDocument oldDocument) {

		fOldDocument = oldDocument;
	}
}
