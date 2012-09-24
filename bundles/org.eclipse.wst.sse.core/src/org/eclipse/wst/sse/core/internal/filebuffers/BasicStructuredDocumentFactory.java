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
package org.eclipse.wst.sse.core.internal.filebuffers;

import org.eclipse.core.filebuffers.IDocumentFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.internal.text.JobSafeStructuredDocument;


/**
 * Generic IDocumentFactory for IStructuredDocuments to be used by the
 * org.eclipse.core.filebuffers.documentCreation extension point. This class
 * is not meant to be subclassed.
 * 
 * @plannedfor 1.0
 */
public class BasicStructuredDocumentFactory implements IDocumentFactory, IExecutableExtension {

	/*
	 * The content type ID used to declare this factory; it is used to find
	 * the corresponding support for creating the document
	 */
	private String fContentTypeIdentifier = null;

	/**
	 * Constructor, only to be used by the
	 * org.eclipse.core.filebuffers.documentCreation extension point.
	 */
	public BasicStructuredDocumentFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.filebuffers.IDocumentFactory#createDocument()
	 */
	public IDocument createDocument() {
		IDocument document = null;
		IContentType contentType = Platform.getContentTypeManager().getContentType(getContentTypeIdentifier());
		IModelHandler handler = null;
		while (handler == null && !IContentTypeManager.CT_TEXT.equals(contentType.getId())) {
			handler = ModelHandlerRegistry.getInstance().getHandlerForContentTypeId(contentType.getId());
			contentType = contentType.getBaseType();
		}
		if (handler != null) {
			document = handler.getDocumentLoader().createNewStructuredDocument();
		}
		else {
			document = new JobSafeStructuredDocument();
		}
		return document;
	}

	private String getContentTypeIdentifier() {
		return fContentTypeIdentifier;
	}

	/*
	 * Loads the content type ID to be used when creating the Structured Document. 
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		fContentTypeIdentifier = config.getAttribute("contentTypeId"); //$NON-NLS-1$
		if (data != null) {
			if (data instanceof String && data.toString().length() > 0) {
				fContentTypeIdentifier = (String) data;
			}
		}
	}
}
