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
package org.eclipse.wst.sse.core.filebuffers;

import org.eclipse.core.filebuffers.IDocumentFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;

/**
 * 
 * @deprecated - no need for abstract class here (they are each pretty simple). 
 * 
 */
public abstract class BasicStructuredDocumentFactory implements IDocumentFactory {
	/**
	 * 
	 */
	public BasicStructuredDocumentFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filebuffers.IDocumentFactory#createDocument()
	 */
	public IDocument createDocument() {
		IModelManagerPlugin mgr = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelHandler handler = mgr.getModelHandlerRegistry().getHandlerForContentTypeId(getContentTypeIdentifier());
		IEncodedDocument document = handler.getDocumentLoader().createNewStructuredDocument();
		document.setDocumentPartitioner(handler.getDocumentLoader().getDefaultDocumentPartitioner());
		return document;
	}

	/**
	 * @return
	 */
	protected abstract String getContentTypeIdentifier();
}
