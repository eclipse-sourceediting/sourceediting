/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.modelhandler;


import org.eclipse.wst.css.core.internal.document.CSSModelImpl;
import org.eclipse.wst.css.core.internal.encoding.CSSDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.model.AbstractModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;


/**
 * 
 */
public class CSSModelLoader extends AbstractModelLoader {
	/**
	 * CSSLoader constructor comment.
	 */
	public CSSModelLoader() {
		super();
	}

	/*
	 * @see IModelLoader#newModel()
	 */
	public IStructuredModel newModel() {
		IStructuredModel model = new CSSModelImpl();
		// now done in create
		// model.setStructuredDocument(createNewStructuredDocument());
		// model.setFactoryRegistry(defaultFactoryRegistry());
		return model;
	}

	public IModelLoader newInstance() {
		return new CSSModelLoader();
	}

	public IDocumentLoader getDocumentLoader() {
		if (documentLoaderInstance == null) {
			documentLoaderInstance = new CSSDocumentLoader();
		}
		return documentLoaderInstance;
	}
}
