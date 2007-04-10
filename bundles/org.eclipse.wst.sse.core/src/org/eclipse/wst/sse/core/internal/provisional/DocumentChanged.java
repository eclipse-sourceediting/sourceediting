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
package org.eclipse.wst.sse.core.internal.provisional;

import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

public class DocumentChanged extends ModelLifecycleEvent {
	private IStructuredDocument fNewDocument;

	private IStructuredDocument fOldDocument;

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

	public IStructuredDocument getOldDocument() {

		return fOldDocument;
	}

	void setNewDocument(IStructuredDocument newDocument) {

		fNewDocument = newDocument;
	}

	void setOldDocument(IStructuredDocument oldDocument) {

		fOldDocument = oldDocument;
	}
}
