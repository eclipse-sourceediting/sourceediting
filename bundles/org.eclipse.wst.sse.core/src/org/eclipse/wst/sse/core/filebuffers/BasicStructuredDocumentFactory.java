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
package org.eclipse.wst.sse.core.filebuffers;

import org.eclipse.core.filebuffers.IDocumentFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.document.NullStructuredDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.internal.text.JobSafeStructuredDocument;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;


public class BasicStructuredDocumentFactory implements IDocumentFactory, IExecutableExtension {

	// The content type ID used to declare this factory; it is used to find
	// the corresponding ModelHandler
	private String fContentTypeIdentifier = null;

	public BasicStructuredDocumentFactory() {
		super();
	}

	public IDocument createDocument() {
		IDocument document = null;
		IModelHandler handler = ModelHandlerRegistry.getInstance().getHandlerForContentTypeId(getContentTypeIdentifier());
		if (handler != null) {
			document = handler.getDocumentLoader().createNewStructuredDocument();
		}
		else {
			document = new JobSafeStructuredDocument();
		}

		if (document.getDocumentPartitioner() == null) {
			IDocumentPartitioner defaultPartitioner = new NullStructuredDocumentPartitioner();
			document.setDocumentPartitioner(defaultPartitioner);
			defaultPartitioner.connect(document);
		}
		return document;
	}

	protected String getContentTypeIdentifier() {
		return fContentTypeIdentifier;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		fContentTypeIdentifier = config.getAttribute("contentTypeId");
		if (data != null) {
			if (data instanceof String && data.toString().length() > 0) {
				fContentTypeIdentifier = (String) data;
			}
		}
	}
}
