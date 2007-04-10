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

import org.eclipse.wst.css.core.internal.encoding.CSSDocumentCharsetDetector;
import org.eclipse.wst.css.core.internal.encoding.CSSDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;


public class ModelHandlerForCSS extends AbstractModelHandler implements IModelHandler {

	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten
	 * at run time with what's in registry! (so should never be 'final')
	 */
	static String AssociatedContentTypeID = "org.eclipse.wst.css.core.csssource"; //$NON-NLS-1$
	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten
	 * at run time with what's in registry! (so should never be 'final')
	 */
	private static String ModelHandlerID = "org.eclipse.wst.css.core.modelhandler"; //$NON-NLS-1$

	public ModelHandlerForCSS() {
		super();
		setId(ModelHandlerID);
		setAssociatedContentTypeId(AssociatedContentTypeID);
	}

	public IDocumentCharsetDetector getEncodingDetector() {
		return new CSSDocumentCharsetDetector();
	}

	public IDocumentLoader getDocumentLoader() {
		return new CSSDocumentLoader();
	}

	/*
	 * @see ContentTypeDescription#getModelLoader()
	 */
	public IModelLoader getModelLoader() {
		return new CSSModelLoader();
	}

}
