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
package org.eclipse.wst.dtd.core.modelhandler;

import org.eclipse.wst.dtd.core.document.DTDModelImpl;
import org.eclipse.wst.dtd.core.encoding.DTDDocumentLoader;
import org.eclipse.wst.sse.core.AbstractModelLoader;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.ModelLoader;
import org.eclipse.wst.sse.core.document.IDocumentLoader;



public final class DTDModelLoader extends AbstractModelLoader {
	public DTDModelLoader() {
		super();
	}

	public IStructuredModel newModel() {
		IStructuredModel model = new DTDModelImpl();
		// now done in create
		//model.setStructuredDocument(createNewStructuredDocument());
		//model.setFactoryRegistry(defaultFactoryRegistry());
		return model;
	}

	public ModelLoader newInstance() {
		return new DTDModelLoader();
	}

	public IDocumentLoader getDocumentLoader() {
		if (documentLoaderInstance == null) {
			documentLoaderInstance = new DTDDocumentLoader();
		}
		return documentLoaderInstance;
	}
}
