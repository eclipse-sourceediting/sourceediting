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
package org.eclipse.wst.xml.core.modelhandler;

import org.eclipse.wst.sse.core.ModelLoader;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.document.IDocumentLoader;
import org.eclipse.wst.sse.core.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;
import org.eclipse.wst.xml.core.document.DocumentLoaderForXML;
import org.eclipse.wst.xml.core.encoding.XMLDocumentCharsetDetector;
import org.eclipse.wst.xml.core.encoding.XMLDocumentLoader;


/**
 * Provides generic XML model handling. It is also marked as the default
 * content type handler. There should be only one implementation of the
 * default.
 */
public class ModelHandlerForXML extends AbstractModelHandler implements IModelHandler {
	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten
	 * at run time with what's in registry! (so should never be 'final')
	 */
	static String AssociatedContentTypeID = "org.eclipse.wst.xml.core.xmlsource"; //$NON-NLS-1$
	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten
	 * at run time with what's in registry! (so should never be 'final')
	 */
	private static String ModelHandlerID = "org.eclipse.wst.sse.core.handler.xml"; //$NON-NLS-1$
	
	public ModelHandlerForXML() {
		super();
		setId(ModelHandlerID);
		setAssociatedContentTypeId(AssociatedContentTypeID);
	}

	public IDocumentCharsetDetector getEncodingDetector() {
		return new XMLDocumentCharsetDetector();
	}

	public IDocumentLoader getDocumentLoader() {
		if (USE_FILE_BUFFERS)
			return new DocumentLoaderForXML();
		else
			return new XMLDocumentLoader();
	}

	public ModelLoader getModelLoader() {
		return new XMLModelLoader();
	}

}
